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
package org.jboss.tools.seam.core;

import java.io.IOException;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osgi.util.NLS;
import org.jboss.tools.common.el.core.resolver.TypeInfoCollector;
import org.jboss.tools.seam.internal.core.SeamProject;
import org.jboss.tools.seam.internal.core.SeamResourceVisitor;
import org.jboss.tools.seam.internal.core.scanner.IFileScanner;
import org.jboss.tools.seam.internal.core.scanner.java.JavaScanner;
import org.jboss.tools.seam.internal.core.scanner.lib.LibraryScanner;
import org.jboss.tools.seam.internal.core.scanner.xml.XMLScanner;

public class SeamCoreBuilder extends IncrementalProjectBuilder {
	public static String BUILDER_ID = "org.jboss.tools.seam.core.seambuilder"; //$NON-NLS-1$
	
	SeamResourceVisitor resourceVisitor = null;
	
	SeamProject getSeamProject() {
		IProject p = getProject();
		if(p == null) return null;
		return (SeamProject)SeamCorePlugin.getSeamProject(p, false);
	}
	
	SeamResourceVisitor getResourceVisitor() {
		if(resourceVisitor == null) {
			SeamProject p = getSeamProject();
			resourceVisitor = new SeamResourceVisitor(p);
		}
		return resourceVisitor;
	}

	class SampleDeltaVisitor implements IResourceDeltaVisitor {
		/*
		 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
		 */
		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			switch (delta.getKind()) {
			case IResourceDelta.ADDED:
				return getResourceVisitor().getVisitor().visit(resource);
			case IResourceDelta.REMOVED:
				SeamProject p = getSeamProject();
				if(p != null) p.pathRemoved(resource.getFullPath());
				break;
			case IResourceDelta.CHANGED:
				return getResourceVisitor().getVisitor().visit(resource);
			}
			//return true to continue visiting children.
			return true;
		}
	}

	/**
	 * @see org.eclipse.core.resource.InternalProjectBuilder#build(int,
	 *      java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		SeamProject sp = getSeamProject();
		if(sp == null) {
			return null; 
		}
	
		if(sp.hasNoStorage()) {
			kind = FULL_BUILD;
		}
		
		long begin = System.currentTimeMillis();
		
		sp.postponeFiring();
		
		try {
		
			sp.resolveStorage(kind != FULL_BUILD);
			
			if(sp.getClassPath().update()) {
				sp.getClassPath().process();
			} else if(sp.getClassPath().hasToUpdateProjectDependencies()) {
				sp.getClassPath().validateProjectDependencies();
			}

			TypeInfoCollector.cleanCache();

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
			sp.fullBuildTime += end - begin;
			try {
				sp.store();
			} catch (IOException e) {
				SeamCorePlugin.getPluginLog().logError(NLS.bind(SeamCoreMessages.SeamCoreBuilder_1,sp.getProject().getName()), e); //$NON-NLS-1$
			}
			
			sp.postBuild();
		
		} finally {
			sp.fireChanges();
		}
		
		return null;
	}

	protected void fullBuild(final IProgressMonitor monitor)
			throws CoreException {
		try {
			getProject().accept(getResourceVisitor().getVisitor());
		} catch (CoreException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		}
	}

	protected void incrementalBuild(IResourceDelta delta,
			IProgressMonitor monitor) throws CoreException {
		// the visitor does the work.
		delta.accept(new SampleDeltaVisitor());
	}
	
	/**
	 * Access to xml scanner for test.
	 * @return
	 */
	public static IFileScanner getXMLScanner() {
		return new XMLScanner();
	}

	/**
	 * Access to java scanner for test.
	 * @return
	 */
	public static IFileScanner getJavaScanner() {
		return new JavaScanner();
	}

	/**
	 * Access to library scanner for test.
	 * @return
	 */
	public static IFileScanner getLibraryScanner() {
		return new LibraryScanner();
	}

	protected void clean(IProgressMonitor monitor) throws CoreException {
		SeamProject sp = getSeamProject();
		if(sp != null) sp.clean();
	}

}
