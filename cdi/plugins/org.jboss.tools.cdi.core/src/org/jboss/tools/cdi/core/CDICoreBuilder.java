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
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.internal.core.scanner.CDIBuilderDelegate;
import org.jboss.tools.cdi.internal.core.scanner.FileSet;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.common.model.plugin.ModelPlugin;
import org.jboss.tools.common.model.project.ProjectHome;
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

			if(n.getClassPath().update()) {
				List<String> newJars = n.getClassPath().process();
				buildJars(newJars);
				
				n.getClassPath().validateProjectDependencies();
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
			getProject().accept(rv);
			FileSet fs = rv.fileSet;
			builderDelegate.build(fs, getCDICoreNature());
			
		} catch (CoreException e) {
			CDICorePlugin.getDefault().logError(e);
		}
	}

	protected void incrementalBuild(IResourceDelta delta,
			IProgressMonitor monitor) throws CoreException {
		CDIResourceVisitor rv = getResourceVisitor();
		delta.accept(new SampleDeltaVisitor());
		FileSet fs = rv.fileSet;
		builderDelegate.build(fs, getCDICoreNature());
	}

	protected void buildJars(List<String> newJars) throws CoreException {
		IJavaProject jp = EclipseResourceUtil.getJavaProject(getCDICoreNature().getProject());
		if(jp == null) return;
		FileSet fileSet = new FileSet();
		
		for (String jar: newJars) {
			Path path = new Path(jar);
			IPackageFragmentRoot root = jp.getPackageFragmentRoot(jar);
			if (root == null || !root.exists())
				return;
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
			//TODO add beans.xml object
		}
		builderDelegate.build(fileSet, getCDICoreNature());
	}

	protected void clean(IProgressMonitor monitor) throws CoreException {
		CDICoreNature n = getCDICoreNature();
		if(n != null) n.clean();
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
				if(p != null) p.getDefinitions().getWorkingCopy().clean(resource.getFullPath());
				break;
			case IResourceDelta.CHANGED:
				return getResourceVisitor().visit(resource);
			}
			//return true to continue visiting children.
			return true;
		}
	}

	class CDIResourceVisitor implements IResourceVisitor {
		FileSet fileSet = new FileSet();
		IPath[] outs = new IPath[0];
		IPath[] srcs = new IPath[0];
		IPath webinf = null;
		
		CDIResourceVisitor() {
			webinf = ProjectHome.getWebInfPath(getProject());
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
							IType[] ts = unit.getTypes();
							fileSet.add(f.getFullPath(), ts);
						}
						return false;
					}
				}
				if(webinf != null && webinf.isPrefixOf(path)) {
					if(f.getName().equals("beans.xml")) {
						fileSet.setBeanXML(f.getFullPath(), f); //file
					}
				}
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
				if(webinf != null) {
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

}

