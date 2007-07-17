/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.seam.core;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.jboss.tools.seam.internal.core.SeamProject;
import org.jboss.tools.seam.internal.core.SeamResourceVisitor;
import org.jboss.tools.seam.internal.core.scanner.IFileScanner;
import org.jboss.tools.seam.internal.core.scanner.java.JavaScanner;
import org.jboss.tools.seam.internal.core.scanner.lib.LibraryScanner;
import org.jboss.tools.seam.internal.core.scanner.xml.XMLScanner;

public class SeamCoreBuilder extends IncrementalProjectBuilder {
	public static String BUILDER_ID = "org.jboss.tools.seam.core.seambuilder";
	
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
				getResourceVisitor().getVisitor().visit(resource);
				break;
			case IResourceDelta.REMOVED:
				SeamProject p = getSeamProject();
				if(p != null) p.pathRemoved(resource.getFullPath());
				break;
			case IResourceDelta.CHANGED:
				getResourceVisitor().getVisitor().visit(resource);
				break;
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
		sp.resolveStorage(kind != FULL_BUILD);
		
		if(sp.getClassPath().update()) {
			sp.getClassPath().process();
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
		try {
			sp.store();
		} catch (Exception e) {
			SeamCorePlugin.getPluginLog().logError("Error storing build results");
		}
		return null;
	}

	protected void fullBuild(final IProgressMonitor monitor)
			throws CoreException {
		try {
			getProject().accept(getResourceVisitor().getVisitor());
		} catch (CoreException e) {
			e.printStackTrace();
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

}
