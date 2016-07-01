/*************************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     JBoss by Red Hat - Initial implementation.
 ************************************************************************************/
package org.jboss.tools.batch.internal.core.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.jboss.tools.batch.core.BatchConstants;
import org.jboss.tools.batch.core.BatchCorePlugin;
import org.jboss.tools.batch.internal.core.impl.BatchUtil.DocumentScanner;
import org.jboss.tools.batch.internal.core.impl.definition.BatchJobDefinition;
import org.jboss.tools.batch.internal.core.impl.definition.BatchXMLDefinition;
import org.jboss.tools.batch.internal.core.impl.definition.TypeDefinition;
import org.jboss.tools.batch.internal.core.scanner.FileSet;
import org.jboss.tools.batch.internal.core.scanner.lib.JarSet;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.common.xml.XMLUtilities;
import org.jboss.tools.jst.web.kb.internal.IIncrementalProjectBuilderExtension;
import org.jboss.tools.jst.web.kb.internal.KbBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class BatchBuilder extends IncrementalProjectBuilder implements IIncrementalProjectBuilderExtension {
	BatchResourceVisitor resourceVisitor = null;
	/**
	 * Set only for instance created to initially load batch model.
	 */
	BatchProject batch = null;

	public BatchBuilder() {}

	public BatchBuilder(BatchProject batch) throws CoreException {
		this.batch = batch;
		build(IncrementalProjectBuilder.FULL_BUILD, null, new NullProgressMonitor());
	}

	protected BatchProject getBatchProject() {
		if(batch != null) {
			return batch;
		}
		IProject p = getProject();
		if(p == null) return null;
		return (BatchProject)BatchProjectFactory.getBatchProject(p, false);
	}

	BatchResourceVisitor getResourceVisitor() {
		if(resourceVisitor == null) {
			resourceVisitor = new BatchResourceVisitor();
		}
		return resourceVisitor;
	}

	@Override
	public IProject[] build(int kind, Map<String, String> args,
			IProgressMonitor monitor) throws CoreException {
		resourceVisitor = null;

		BatchProject n = getBatchProject();
		if(n == null) {
			return null;
		}
		if(n.getType(BatchConstants.ABSTRACT_BATCHLET_TYPE) == null) {
			n.clean();
			return null;
		} else {
			n.getClassPath().init();
		}

		if(n.hasNoStorage()) {
			kind = FULL_BUILD;
		}

		n.postponeFiring();

		//Set true when canceling build should be recovered by complete rebuild pf classpath at next build. 
		boolean classPathShouldBeReset = false;

		JavaModelManager manager = JavaModelManager.getJavaModelManager();
		try {
			n.resolveStorage(kind != FULL_BUILD);

			manager.cacheZipFiles(this);

			if(kind == FULL_BUILD) n.getClassPath().reset();

			//1. Check class path.
			boolean isClassPathUpdated = n.getClassPath().update();
			
			JarSet newJars = new JarSet();
			if(isClassPathUpdated || kind == FULL_BUILD) {
				classPathShouldBeReset = true;
				//2. Update class path. Removed paths will be cached to be applied to working copy of context. 
				n.getClassPath().setSrcs(getResourceVisitor().srcs);
				newJars = n.getClassPath().process();
			}

			//4. Create working copy of context.
			n.getDefinitions().newWorkingCopy(kind == FULL_BUILD);

			//5. Modify working copy of context.
			//5.1 Apply Removed paths.
			if(isClassPathUpdated) {
				n.getClassPath().applyRemovedPaths();
			}
		
			//5.2 Discover sources and build definitions.
			if(isClassPathUpdated || kind == FULL_BUILD) {
				buildJars(newJars, monitor);
				
				n.getClassPath().validateProjectDependencies();
				
				kind = FULL_BUILD;
			} else if(n.getClassPath().hasToUpdateProjectDependencies()) {
				n.getClassPath().validateProjectDependencies();
			}

			if (kind == FULL_BUILD) {
				fullBuild(monitor);
			} else {
				IResourceDelta delta = getDelta(getCurrentProject());
				if (delta == null) {
					fullBuild(monitor);
				} else {
					incrementalBuild(delta, monitor);
				}
			}

			// 6. Save created definitions to project context and build beans.
			getBatchProject().getDefinitions().applyWorkingCopy();
			
			try {
				n.store();
			} catch (IOException e) {
				BatchCorePlugin.pluginLog().logError(e); //$NON-NLS-1$
			}
		} catch (OperationCanceledException e) {
			//Recover partially built model.
			if(classPathShouldBeReset) {
				//we interrupt building jars in unknown state.
				//At the next build, jars should be completely rebuild.
				getBatchProject().getClassPath().clean();
			}
			getBatchProject().getDefinitions().dropWorkingCopy();
			throw e;
		} finally {
			manager.flushZipFiles(this);
			n.fireChanges();
		}
		return null;
	}

	protected void fullBuild(final IProgressMonitor monitor) throws CoreException {
		try {
			BatchResourceVisitor rv = getResourceVisitor();
			rv.setProgressMonitor(monitor);
			rv.incremental = false;
			getCurrentProject().accept(rv);
			FileSet fs = rv.fileSet;
			build(fs, getBatchProject(), monitor);		
		} catch (CoreException e) {
			BatchCorePlugin.pluginLog().logError(e);
		}
	}

	protected void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor) throws CoreException {
		BatchResourceVisitor rv = getResourceVisitor();
		rv.setProgressMonitor(monitor);
		rv.incremental = true;
		delta.accept(new SampleDeltaVisitor());
		FileSet fs = rv.fileSet;
		build(fs, getBatchProject(), monitor);
	}

	private void buildJars(JarSet newJars, IProgressMonitor monitor) throws CoreException {
		IJavaProject jp = EclipseUtil.getJavaProject(getBatchProject().getProject());
		if(jp == null) return;

			FileSet fileSet = new FileSet();
			for (String jar: newJars.getBatchModules()) {
				Path path = new Path(jar);
				IPackageFragmentRoot root = jp.getPackageFragmentRoot(jar);
				if(root == null) continue;
				if(!root.exists()) {
					IFile f = getFile(jar);
					if(f != null && f.exists()) {
						root = jp.getPackageFragmentRoot(f);
					} else {
						f = getFile(jar + "/META-INF/web-fragment.xml");
						if(f != null && f.exists()) {
							root = jp.getPackageFragmentRoot(f.getParent().getParent());
						}
					}
				}
				if (root == null || !root.exists())
					continue;
				IJavaElement[] es = root.getChildren();
				for (IJavaElement e : es) {
					if (e instanceof IPackageFragment) {
						IPackageFragment pf = (IPackageFragment) e;
						IClassFile[] cs = pf.getClassFiles();
						for (IClassFile c : cs) {
							KbBuilder.checkCanceled(monitor);
							fileSet.add(path, c.getType());
						}
					}
				}
			}
			build(fileSet, getBatchProject(), monitor);
	}

	void build(FileSet fs, BatchProject project, IProgressMonitor monitor) {
		DefinitionContext context = getBatchProject().getDefinitions().getWorkingCopy();
		Set<IFile> batchXMLs = fs.getBatchXMLs();
		for (IFile batchXML: batchXMLs) {
			final BatchXMLDefinition def = new BatchXMLDefinition();
			def.setFile(batchXML);
			BatchUtil.scanXMLFile(batchXML, new DocumentScanner() {
				@Override
				public void scanDocument(Document document) {
					if(document != null) {
						Element element = document.getDocumentElement();
						if(element != null) {
							for (Element ref: XMLUtilities.getChildren(element, BatchConstants.ATTR_REF)) {
								String id = ref.getAttribute(BatchConstants.ATTR_ID);
								String cls = ref.getAttribute(BatchConstants.ATTR_CLASS);
								if(id != null && id.length() > 0 && cls != null && cls.length() > 0) {
									def.add(cls, id);
								}
							}
						}
					}
				}
			});

			context.addBatchXML(def);
		}
		Map<IPath, Set<IType>> cs = fs.getClasses();
		for (IPath f: cs.keySet()) {
			Set<IType> ts = cs.get(f);
			for (IType type: ts) {
				KbBuilder.checkCanceled(monitor);
				TypeDefinition def = new TypeDefinition();
				def.setType(type, context, 0);
				if(def.getArtifactType() != null) {
					context.addType(f, type.getFullyQualifiedName(), def);
				}
			}
		}
		Set<IFile> batchJobs = fs.getBatchJobs();
		if(!batchJobs.isEmpty()) {
			for (IFile batchJob: batchJobs) {
				final BatchJobDefinition def = new BatchJobDefinition();
				def.setFile(batchJob);
				BatchUtil.scanXMLFile(batchJob, new DocumentScanner() {					
					@Override
					public void scanDocument(Document document) {
						if(document != null) {
							Element element = document.getDocumentElement();
							if(element != null) {
								String id = element.getAttribute(BatchConstants.ATTR_ID);
								def.setJobID(id);
							}
						}
						
					}
				});
				//
				context.addBatchConfig(def);
			}
		}
	}

	public static IFile getFile(String location) {
		IPath path = new Path(location).makeAbsolute();
		IFile result = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path);
		return result;
	}

	IProject getCurrentProject() {
		return batch != null ? batch.getProject() : getProject();
	}

	class SampleDeltaVisitor implements IResourceDeltaVisitor {
		/*
		 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
		 */
		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			switch (delta.getKind()) {
			case IResourceDelta.ADDED:
				return getResourceVisitor().visit(resource);
			case IResourceDelta.REMOVED:
				BatchProject p = getBatchProject();
				if(p != null) {
					p.getDefinitions().getWorkingCopy().clean(resource.getFullPath());
				}
				break;
			case IResourceDelta.CHANGED:
				return getResourceVisitor().visit(resource);
			}
			//return true to continue visiting children.
			return true;
		}
	}

	class BatchResourceVisitor implements IResourceVisitor {
		boolean incremental = false;
		FileSet fileSet = new FileSet();
		IPath[] outs = new IPath[0];
		IPath[] srcs = new IPath[0];
		IPath[] batch_jobs = new IPath[0];
		IPath[] batch_xmls = new IPath[0];
//		IPath[] webinfs = new IPath[0];
		Set<IPath> visited = new HashSet<IPath>();
		
		IProgressMonitor monitor = null;

		public BatchResourceVisitor() {
			getJavaSourceRoots(getCurrentProject());
//			webinfs = WebUtils.getWebInfPaths(getCurrentProject());
		}

		public void setProgressMonitor(IProgressMonitor monitor) {
			this.monitor = monitor;
		}

		void getJavaSourceRoots(IProject project) {
			IJavaProject javaProject = EclipseUtil.getJavaProject(project);
			if(javaProject == null) return;
			List<IPath> ps = new ArrayList<IPath>();
			List<IPath> os = new ArrayList<IPath>();
			try {
				IPath output = javaProject.getOutputLocation();
				if(output != null) os.add(output);
				IClasspathEntry[] es = javaProject.getResolvedClasspath(true);
				for (int i = 0; i < es.length; i++) {
					if(es[i].getEntryKind() == IClasspathEntry.CPE_SOURCE) {
						IResource findMember = ResourcesPlugin.getWorkspace().getRoot().findMember(es[i].getPath());
						if(findMember != null && findMember.exists()) {
							ps.add(findMember.getFullPath());
						}
						IPath out = es[i].getOutputLocation();
						if(out != null && !os.contains(out)) {
							os.add(out);
						}
					} 
				}
				srcs = ps.toArray(new IPath[0]);
				outs = os.toArray(new IPath[0]);

				batch_jobs = new IPath[srcs.length];
				batch_xmls = new IPath[srcs.length];
				for (int i = 0; i < srcs.length; i++) {
					IPath b = srcs[i].append(BatchConstants.BATCH_JOBS_PATH);
					IResource findMember = ResourcesPlugin.getWorkspace().getRoot().findMember(b);
					if(findMember != null && findMember.exists()) {
						batch_jobs[i] = findMember.getFullPath();
					}
					IPath x = srcs[i].append(BatchConstants.BATCH_XML_PATH);
					findMember = ResourcesPlugin.getWorkspace().getRoot().findMember(x);
					if(findMember != null && findMember.exists()) {
						batch_xmls[i] = findMember.getFullPath();
					}
				}
			} catch(CoreException ce) {
				BatchCorePlugin.pluginLog().logError("Error while locating java source roots for " + project, ce);
			}
		}

		@Override
		public boolean visit(IResource resource) throws CoreException {
			KbBuilder.checkCanceled(monitor);
			IPath path = resource.getFullPath();
			if(resource instanceof IFile) {
				if(visited.contains(path)) {
					return false;
				}
				visited.add(path);
				IFile f = (IFile)resource;
				for (int i = 0; i < outs.length; i++) {
					if(outs[i].isPrefixOf(path)) {
						return false;
					}
				}
				for (int i = 0; i < srcs.length; i++) {
					if(batch_jobs[i] != null && batch_jobs[i].isPrefixOf(path)) {
						if(f.getName().toLowerCase().endsWith(".xml")) {
							fileSet.addBatchJob(f);
						}
					} else if(batch_xmls[i] != null && batch_xmls[i].equals(path)) {
						fileSet.addBatchXML(f);
					} else if(srcs[i].isPrefixOf(path)) {
						if(f.getName().toLowerCase().endsWith(".java")) {
							ICompilationUnit unit = EclipseUtil.getCompilationUnit(f);
							if(unit != null) {
								fileSet.add(f.getFullPath(), unit.getTypes());
							}
						}
						Set<IFile> ds = getDependentFiles(path, visited);
						if(ds != null) for (IFile d: ds) visit(d);
						return false;
					}
				}
				Set<IFile> ds = getDependentFiles(path, visited);
				if(ds != null) for (IFile d: ds) visit(d);
			} else if(resource instanceof IFolder) {
				for (int i = 0; i < outs.length; i++) {
					if(outs[i].isPrefixOf(path)) {
						return false;
					}
				}
				for (int i = 0; i < srcs.length; i++) {
					if(srcs[i].isPrefixOf(path) || path.isPrefixOf(srcs[i])) {
						return true;
					}
				}
				if(resource == resource.getProject()) {
					return true;
				}
				return false;
			}
			//return true to continue visiting children.
			return true;
		}
		
	}

	//In case if we will need that
	Set<IFile> getDependentFiles(IPath path, Set<IPath> visited) {
		return null;
	}

	protected void clean(IProgressMonitor monitor) throws CoreException {
		BatchProject p = getBatchProject();
		if(p != null) p.clean();
	}
}
