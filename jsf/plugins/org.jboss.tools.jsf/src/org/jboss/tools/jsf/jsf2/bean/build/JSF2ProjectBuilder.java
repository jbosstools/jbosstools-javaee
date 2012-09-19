/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.jsf.jsf2.bean.build;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
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
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.plugin.ModelPlugin;
import org.jboss.tools.common.model.project.ProjectHome;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.web.WebUtils;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.jsf2.bean.model.JSF2ProjectFactory;
import org.jboss.tools.jsf.jsf2.bean.model.impl.DefinitionContext;
import org.jboss.tools.jsf.jsf2.bean.model.impl.FacesConfigDefinition;
import org.jboss.tools.jsf.jsf2.bean.model.impl.JSF2Project;
import org.jboss.tools.jsf.jsf2.bean.model.impl.TypeDefinition;
import org.jboss.tools.jsf.jsf2.bean.scanner.FileSet;
import org.jboss.tools.jst.web.kb.internal.IIncrementalProjectBuilderExtension;

public class JSF2ProjectBuilder extends IncrementalProjectBuilder implements IIncrementalProjectBuilderExtension {

	JSF2ResourceVisitor resourceVisitor = null;
	
	/**
	 * Set only for instance created to initially load jsf2 model.
	 */
	JSF2Project jsf;

	public JSF2ProjectBuilder() {}

	public JSF2ProjectBuilder(JSF2Project jsf) throws CoreException {
		this.jsf = jsf;
		build(IncrementalProjectBuilder.FULL_BUILD, null, new NullProgressMonitor());
	}

	protected JSF2Project getJSF2Project() {
		if(jsf != null) {
			return jsf;
		}
		IProject p = getProject();
		if(p == null) return null;
		return (JSF2Project)JSF2ProjectFactory.getJSF2Project(p, false);
	}

	IProject getCurrentProject() {
		return jsf != null ? jsf.getProject() : getProject();
	}

	JSF2ResourceVisitor getResourceVisitor() {
		if(resourceVisitor == null) {
			resourceVisitor = new JSF2ResourceVisitor();
		}
		return resourceVisitor;
	}

	@Override
	public IProject[] build(int kind, Map<String, String> args,
			IProgressMonitor monitor) throws CoreException {
		resourceVisitor = null;
	
		JSF2Project n = getJSF2Project();
		if(n == null) {
			return null;
		}
		
		if(n.hasNoStorage()) {
			kind = FULL_BUILD;
		}
		
		n.postponeFiring();

		try {
			n.resolveStorage(kind != FULL_BUILD);
			
			if(kind == FULL_BUILD) n.getClassPath().reset();

			//1. Check class path.
			boolean isClassPathUpdated = n.getClassPath().update();

			Map<String, XModelObject> newJars = new HashMap<String, XModelObject>();
			if(isClassPathUpdated || kind == FULL_BUILD) {
				//2. Update class path. Removed paths will be cached to be applied to working copy of context. 
				n.getClassPath().setSrcs(getResourceVisitor().srcs);
				newJars = n.getClassPath().process();

			}int i = 0;

			//4. Create working copy of context.
			n.getDefinitions().newWorkingCopy(kind == FULL_BUILD);

			//5. Modify working copy of context.
			//5.1 Apply Removed paths.
			if(isClassPathUpdated) {
				n.getClassPath().applyRemovedPaths();
			}
		
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
				IResourceDelta delta = getDelta(getCurrentProject());
				if (delta == null) {
					fullBuild(monitor);
				} else {
					incrementalBuild(delta, monitor);
				}
			}

			// 6. Save created definitions to project context and build beans.
			getJSF2Project().getDefinitions().applyWorkingCopy();
			
			try {
				n.store();
			} catch (IOException e) {
				JSFModelPlugin.getDefault().logError(e); //$NON-NLS-1$
			}
		} finally {
			n.fireChanges();
		}

		return null;
	}

	protected void fullBuild(final IProgressMonitor monitor)
			throws CoreException {
			try {
				JSF2ResourceVisitor rv = getResourceVisitor();
				rv.incremental = false;
				getCurrentProject().accept(rv);
				FileSet fs = rv.fileSet;
				build(fs, getJSF2Project());
				
			} catch (CoreException e) {
				JSFModelPlugin.getDefault().logError(e);
			}
		}

		protected void incrementalBuild(IResourceDelta delta,
				IProgressMonitor monitor) throws CoreException {
			JSF2ResourceVisitor rv = getResourceVisitor();
			rv.incremental = true;
			delta.accept(new SampleDeltaVisitor());
			FileSet fs = rv.fileSet;
			build(fs, getJSF2Project());
		}

		protected void buildJars(Map<String, XModelObject> newJars) throws CoreException {
			IJavaProject jp = EclipseResourceUtil.getJavaProject(getJSF2Project().getProject());
			if(jp == null) return;
			FileSet fileSet = new FileSet();
			
			for (String jar: newJars.keySet()) {
				Path path = new Path(jar);
				IPackageFragmentRoot root = jp.getPackageFragmentRoot(jar);
				if(root == null) continue;
				if(!root.exists()) {
					IFile f = EclipseResourceUtil.getFile(jar);
					if(f != null && f.exists()) {
						root = jp.getPackageFragmentRoot(f);
					} else {
						f = EclipseResourceUtil.getFile(jar + "/META-INF/web-fragment.xml");
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
			}
			build(fileSet, getJSF2Project());
		}

	void build(FileSet fs, JSF2Project project) {
		DefinitionContext context = getJSF2Project().getDefinitions().getWorkingCopy();
		Map<IPath, Set<IType>> cs = fs.getClasses();
		for (IPath f: cs.keySet()) {
			Set<IType> ts = cs.get(f);
			for (IType type: ts) {
				TypeDefinition def = new TypeDefinition();
				def.setType(type, context, TypeDefinition.FLAG_ALL_MEMBERS);
				context.addType(f, type.getFullyQualifiedName(), def);
			}
		}
		IFile facesConfig = fs.getFacesConfig();
		if(facesConfig != null) {
			FacesConfigDefinition def = new FacesConfigDefinition();
			def.setPath(facesConfig.getFullPath());
			XModelObject o = EclipseResourceUtil.createObjectForResource(facesConfig);
			if(o != null) {
				def.setObject(o);
				context.setFacesConfig(def);
			}
		}
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
				JSF2Project p = getJSF2Project();
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

	class JSF2ResourceVisitor implements IResourceVisitor {
		boolean incremental = false;
		FileSet fileSet = new FileSet();
		IPath[] outs = new IPath[0];
		IPath[] srcs = new IPath[0];
		IPath[] webinfs = new IPath[0];
		Set<IPath> visited = new HashSet<IPath>();

		public JSF2ResourceVisitor() {
			getJavaSourceRoots(getCurrentProject());
			webinfs = WebUtils.getWebInfPaths(getCurrentProject());
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
				JSFModelPlugin.getDefault().logError("Error while locating java source roots for " + project, ce);
			}
		}

		@Override
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
						Set<IFile> ds = getDependentFiles(path, visited);
						if(ds != null) for (IFile d: ds) visit(d);
						return false;
					}
				}
				for (int i = 0; i < webinfs.length; i++) {
					if(webinfs[i].isPrefixOf(path) && f.getName().equals("faces-config.xml")
						&& path.removeLastSegments(1).equals(webinfs[i])) {
						fileSet.setFacesConfig(f);
					}
				}
				Set<IFile> ds = getDependentFiles(path, visited);
				if(ds != null) for (IFile d: ds) visit(d);
			}
			
			if(resource instanceof IFolder) {
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
				for (IPath webinf: webinfs) {
					if(webinf.isPrefixOf(path) || path.isPrefixOf(webinf)) {
						return true;
					}
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

}
