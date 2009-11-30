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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class CDICoreBuilder extends IncrementalProjectBuilder {
	public static String BUILDER_ID = "org.jboss.tools.cdi.core.cdibuilder";

	static Set<ICDIBuilderDelegate> delegates = null;

	static Set<ICDIBuilderDelegate> getDelegates() {
		if(delegates == null) {
			delegates = new HashSet<ICDIBuilderDelegate>();
			//TODO populate			
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
				n.getClassPath().process();
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
			getProject().accept(getResourceVisitor());
		} catch (CoreException e) {
			CDICorePlugin.getDefault().logError(e);
		}
	}

	protected void incrementalBuild(IResourceDelta delta,
			IProgressMonitor monitor) throws CoreException {
		delta.accept(new SampleDeltaVisitor());
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
				if(p != null) p.pathRemoved(resource.getFullPath());
				break;
			case IResourceDelta.CHANGED:
				return getResourceVisitor().visit(resource);
			}
			//return true to continue visiting children.
			return true;
		}
	}

	class CDIResourceVisitor implements IResourceVisitor {

		public boolean visit(IResource resource) throws CoreException {
			// TODO 
			return false;
		}
		
	}

}

