/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.core;

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
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.extension.IDefinitionContextExtension;
import org.jboss.tools.cdi.core.extension.feature.IBuildParticipant2Feature;
import org.jboss.tools.cdi.core.extension.feature.IBuildParticipantFeature;
import org.jboss.tools.cdi.internal.core.impl.definition.AnnotationHelper;
import org.jboss.tools.cdi.internal.core.impl.definition.Dependencies;
import org.jboss.tools.cdi.internal.core.scanner.CDIBuilderDelegate;
import org.jboss.tools.cdi.internal.core.scanner.FileSet;
import org.jboss.tools.cdi.internal.core.scanner.lib.JarSet;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.plugin.ModelPlugin;
import org.jboss.tools.common.model.project.ProjectHome;
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.common.model.util.EclipseResourceUtil;

public class CDICoreBuilder extends IncrementalProjectBuilder {
	public static String BUILDER_ID = "org.jboss.tools.cdi.core.cdibuilder";

	static Set<ICDIBuilderDelegate> delegates = null;

	static Set<ICDIBuilderDelegate> getDelegates() {
		if(delegates == null) {
			delegates = new HashSet<ICDIBuilderDelegate>();
			//TODO populate; extension point will be used
			delegates.add(new CDIBuilderDelegate()); //default
		}
		return delegates;
	}

	ICDIBuilderDelegate builderDelegate;

	CDIResourceVisitor resourceVisitor = null;

	Set<IBuildParticipantFeature> buildParticipants = null;
	Set<IBuildParticipant2Feature> buildParticipants2 = null;

	public CDICoreBuilder() {}

	CDICoreNature getCDICoreNature() {
		IProject p = getProject();
		if(p == null) return null;
		return CDICorePlugin.getCDI(p, false);
	}

	CDIResourceVisitor getResourceVisitor() {
		if(resourceVisitor == null) {
			resourceVisitor = new CDIResourceVisitor();
		}
		return resourceVisitor;
	}

	private void findDelegate() {
		Set<ICDIBuilderDelegate> ds = getDelegates();
		int relevance = 0;
		for (ICDIBuilderDelegate d: ds) {
			int r = d.computeRelevance(getProject());
			if(r > relevance) {
				builderDelegate = d;
				relevance = r;
			}
		}
	}

	public ICDIBuilderDelegate getDelegate() {
		return builderDelegate;
	}

	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		
		resourceVisitor = null;
		findDelegate();
		if(getDelegate() == null) {
			return null;
		}
		CDICoreNature n = getCDICoreNature();
		if(n == null) {
			return null;
		}
		
		if(n.hasNoStorage()) {
			kind = FULL_BUILD;
		}
		
		n.postponeFiring();

		long begin = System.currentTimeMillis();

		try {
			n.resolveStorage(kind != FULL_BUILD);

			if(n.getDelegate() == null || n.getDelegate().getClass() != getDelegate().getProjectImplementationClass()) {
				if(n.getDelegate() != null) {
					n.clean();
					n.postponeFiring();
				}
				kind = FULL_BUILD;
				try {
					ICDIProject delegate = (ICDIProject)getDelegate().getProjectImplementationClass().newInstance();
					n.setCDIProject(delegate);
				} catch (IllegalAccessException e1) {
					CDICorePlugin.getDefault().logError(e1);
				} catch (InstantiationException e2) {
					CDICorePlugin.getDefault().logError(e2);
				}
			}

			n.getTypeFactory().clean();
		
			//1. Check class path.
			boolean isClassPathUpdated = n.getClassPath().update();
	
			JarSet newJars = new JarSet();
			if(isClassPathUpdated) {
				//2. Update class path. Removed paths will be cached to be applied to working copy of context. 
				n.getClassPath().setSrcs(getResourceVisitor().srcs);
				newJars = n.getClassPath().process();
			}
			if(isClassPathUpdated || buildParticipants == null) {
				//3. Install extensions. That should be done before constructing working copy of context.
				buildParticipants = n.getExtensionManager().getBuildParticipantFeatures();
				buildParticipants2 = new HashSet<IBuildParticipant2Feature>();
				Set<IDefinitionContextExtension> es = new HashSet<IDefinitionContextExtension>();
				for (IBuildParticipantFeature p: buildParticipants) {
					IDefinitionContextExtension e = p.getContext();
					if(e != null) es.add(e);
					if(p instanceof IBuildParticipant2Feature) {
						buildParticipants2.add((IBuildParticipant2Feature)p);
					}
				}
				n.getDefinitions().setExtensions(es);
			}

			//4. Create working copy of context.
			n.getDefinitions().newWorkingCopy(kind == FULL_BUILD);

			//5. Modify working copy of context.
			//5.1 Apply Removed paths.
			if(isClassPathUpdated) {
				n.getClassPath().applyRemovedPaths();
			}
		
			for (IBuildParticipantFeature p: buildParticipants) p.beginVisiting();

			//5.2 Discover sources and build definitions.
			if(isClassPathUpdated) {
				buildJars(newJars);
				
				n.getClassPath().validateProjectDependencies();
				
				kind = FULL_BUILD;
			} else if(n.getClassPath().hasToUpdateProjectDependencies()) {
				n.getClassPath().validateProjectDependencies();
			}

			if (kind == FULL_BUILD) {
				fullBuild(monitor);
			} else {
				IResourceDelta delta = getDelta(getProject());
				if (delta == null) {
					fullBuild(monitor);
				} else {
					incrementalBuild(delta, monitor);
				}
			}
			for (IBuildParticipantFeature p: buildParticipants) p.buildDefinitions();

			// 6. Save created definitions to project context and build beans.
			getCDICoreNature().getDefinitions().applyWorkingCopy();
			
			long end = System.currentTimeMillis();
			n.fullBuildTime += end - begin;
			try {
				n.store();
			} catch (IOException e) {
				CDICorePlugin.getDefault().logError(e); //$NON-NLS-1$
			}
			
//			n.postBuild();
		
		} finally {
			n.fireChanges();
		}
		
		return null;
	}

	protected void fullBuild(final IProgressMonitor monitor)
		throws CoreException {
		try {
			CDIResourceVisitor rv = getResourceVisitor();
			rv.incremental = false;
			getProject().accept(rv);
			FileSet fs = rv.fileSet;
			invokeBuilderDelegates(fs, getCDICoreNature());
			
		} catch (CoreException e) {
			CDICorePlugin.getDefault().logError(e);
		}
	}

	protected void incrementalBuild(IResourceDelta delta,
			IProgressMonitor monitor) throws CoreException {
		CDIResourceVisitor rv = getResourceVisitor();
		rv.incremental = true;
		delta.accept(new SampleDeltaVisitor());
		FileSet fs = rv.fileSet;
		invokeBuilderDelegates(fs, getCDICoreNature());
	}

	protected void buildJars(JarSet newJars) throws CoreException {
		IJavaProject jp = EclipseResourceUtil.getJavaProject(getCDICoreNature().getProject());
		if(jp == null) return;
		FileSet fileSet = new FileSet();
		
		for (String jar: newJars.getBeanModules().keySet()) {
			Path path = new Path(jar);
			IPackageFragmentRoot root = jp.getPackageFragmentRoot(jar);
			if(root == null) continue;
			if(!root.exists()) {
				IFile f = EclipseResourceUtil.getFile(jar);
				if(f != null && f.exists()) {
					root = jp.getPackageFragmentRoot(f);
				} else {
					f = EclipseResourceUtil.getFile(jar + "/META-INF/beans.xml");
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
						fileSet.add(path, c.getType());
					}
				}
			}
			XModelObject beansXML = newJars.getBeanModules().get(jar);
			fileSet.setBeanXML(path, beansXML);
			
			for (IBuildParticipantFeature p: buildParticipants) p.visitJar(path, root, beansXML);
		}
		if(!buildParticipants2.isEmpty()) {
			for (String jar: newJars.getFileSystems().keySet()) {
				Path path = new Path(jar);
				XModelObject fs = newJars.getFileSystems().get(jar);
				for (IBuildParticipant2Feature p: buildParticipants2) p.visitJar(path, fs);
			}
		}
		addBasicTypes(fileSet);
		invokeBuilderDelegates(fileSet, getCDICoreNature());
	}

	void invokeBuilderDelegates(FileSet fileSet, CDICoreNature n) {
		builderDelegate.build(fileSet, n);
		for (IBuildParticipantFeature p: buildParticipants) p.buildDefinitions(fileSet);
	}

	void addBasicTypes(FileSet fs) throws CoreException {
		IJavaProject jp = EclipseResourceUtil.getJavaProject(getProject());
		if(jp == null) return;
		for (String s: AnnotationHelper.SCOPE_ANNOTATION_TYPES) {
			IType type = EclipseJavaUtil.findType(jp, s);
			if(type != null) fs.add(type.getPath(), type);
		}
		for (String s: AnnotationHelper.QUALIFIER_ANNOTATION_TYPES) {
			IType type = EclipseJavaUtil.findType(jp, s);
			if(type != null) fs.add(type.getPath(), type);
		}
		for (String s: AnnotationHelper.STEREOTYPE_ANNOTATION_TYPES) {
			IType type = EclipseJavaUtil.findType(jp, s);
			if(type != null) fs.add(type.getPath(), type);
		}
	}

	protected void clean(IProgressMonitor monitor) throws CoreException {
		CDICoreNature n = getCDICoreNature();
		if(n != null) n.clean();
	}

	/**
	 * Returns files directly dependent on path which are not included into visited set. 
	 * 
	 * @param path
	 * @param visited
	 * @return
	 */
	Set<IFile> getDependentFiles(IPath path, Set<IPath> visited) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		Dependencies d = getCDICoreNature().getDefinitions().getWorkingCopy().getDependencies();
		Set<IFile> result = new HashSet<IFile>();

		// we do not need to recurse: that will be done by visitor.
		Set<IPath> ps = d.getDirectDependencies(path);
		if(ps != null) for (IPath p: ps) {
			if(visited.contains(p)) continue;
			IFile f = root.getFile(p);
			if(f.exists()) {
				result.add(f);
			}
		}
		
		return result;		
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
				CDICoreNature p = getCDICoreNature();
				CDIResourceVisitor v = getResourceVisitor();
				Set<IFile> fs = getDependentFiles(resource.getFullPath(), v.visited);
				for (IFile f: fs) {
					v.visit(f);
				}
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

	class CDIResourceVisitor implements IResourceVisitor {
		boolean incremental = false;
		FileSet fileSet = new FileSet();
		IPath[] outs = new IPath[0];
		IPath[] srcs = new IPath[0];
		IPath[] webinfs = new IPath[0];
		Set<IPath> visited = new HashSet<IPath>();
		
		CDIResourceVisitor() {
			webinfs = ProjectHome.getWebInfPaths(getProject());
			getJavaSourceRoots(getProject());
		}

		void getJavaSourceRoots(IProject project) {
			IJavaProject javaProject = EclipseResourceUtil.getJavaProject(project);
			if(javaProject == null) return;
			List<IPath> ps = new ArrayList<IPath>();
			List<IPath> os = new ArrayList<IPath>();
			try {
				IPath output = javaProject.getOutputLocation();
				if(output != null) os.add(output);
				IClasspathEntry[] es = javaProject.getResolvedClasspath(true);
				for (int i = 0; i < es.length; i++) {
					if(es[i].getEntryKind() == IClasspathEntry.CPE_SOURCE) {
						IResource findMember = ModelPlugin.getWorkspace().getRoot().findMember(es[i].getPath());
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
			} catch(CoreException ce) {
				CDICorePlugin.getDefault().logError("Error while locating java source roots for " + project, ce);
			}
		}

		public boolean visit(IResource resource) throws CoreException {
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
					if(srcs[i].isPrefixOf(path)) {
						if(f.getName().endsWith(".java")) {
							ICompilationUnit unit = EclipseUtil.getCompilationUnit(f);
							if(unit!=null) {
								if(f.getName().equals("package-info.java")) {
									IPackageDeclaration[] pkg = unit.getPackageDeclarations();
									if(pkg != null && pkg.length > 0) {
										fileSet.add(f.getFullPath(), pkg[0]);
										if(incremental) {
											IResource[] ms = resource.getParent().members();
											for (IResource m: ms) {
												if(m instanceof IFile && !m.getName().equals("package-info.java")) {
													visit(m);
												}
											}
										}
									}
								} else {
									IType[] ts = unit.getTypes();
									fileSet.add(f.getFullPath(), ts);
								}
							}
						}
						else if(path.segmentCount() == srcs[i].segmentCount() + 2
							&& "META-INF".equals(path.segments()[path.segmentCount() - 2])) {
							addBeansXML(f, fileSet);
						}
						for (IBuildParticipantFeature p: buildParticipants) p.visit(f, srcs[i], null);
						Set<IFile> ds = getDependentFiles(path, visited);
						if(ds != null) for (IFile d: ds) visit(d);
						return false;
					}
				}
				for (IPath webinf: webinfs) {
					if(webinf.isPrefixOf(path)) {
						if(webinf.segmentCount() == path.segmentCount() - 1) {
							addBeansXML(f, fileSet);
						}
						for (IBuildParticipantFeature p: buildParticipants) p.visit(f, null, webinf);
					}
				}
				
				Set<IFile> ds = getDependentFiles(path, visited);
				if(ds != null) for (IFile d: ds) visit(d);
			}
			
			if(resource instanceof IFolder) {
				for (IPath out: outs) {
					if(out.isPrefixOf(path)) {
						return false;
					}
				}
				for (IPath src: srcs) {
					if(src.isPrefixOf(path) || path.isPrefixOf(src)) {
						return true;
					}
				}
				for (IPath webinf: webinfs) {
					if(webinf.isPrefixOf(path) || path.isPrefixOf(webinf)) {
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
	
	private void addBeansXML(IFile f, FileSet fileSet) {
		if(f.getName().equals("beans.xml")) {
			XModelObject beansXML = EclipseResourceUtil.getObjectByResource(f);
			if(beansXML == null) {
				beansXML = EclipseResourceUtil.createObjectForResource(f);
			}
			if(beansXML != null) {
				fileSet.setBeanXML(f.getFullPath(), beansXML);
			}
		}
	}

}

