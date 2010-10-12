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
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.jboss.tools.common.el.core.resolver.TypeInfoCollector;
import org.jboss.tools.seam.internal.core.SeamProject;
import org.jboss.tools.seam.internal.core.SeamResourceVisitor;
import org.jboss.tools.seam.internal.core.scanner.IFileScanner;
import org.jboss.tools.seam.internal.core.scanner.java.JavaScanner;
import org.jboss.tools.seam.internal.core.scanner.lib.LibraryScanner;
import org.jboss.tools.seam.internal.core.scanner.xml.XMLScanner;

public class SeamCoreBuilder extends IncrementalProjectBuilder {

	public static String BUILDER_ID = "org.jboss.tools.seam.core.seambuilder"; //$NON-NLS-1$

	/**
	 * @see org.eclipse.core.resource.InternalProjectBuilder#build(int,
	 *      java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		SeamProject seamProject = (SeamProject)SeamCorePlugin.getSeamProject(getProject(), false);
		SeamResourceVisitor resourceVisitor = new SeamResourceVisitor(seamProject);
		
		long begin = System.currentTimeMillis();
		
		seamProject.postponeFiring();

		try {

			seamProject.build();
			TypeInfoCollector.cleanCache();

			IResourceDelta delta = getDelta(getProject());

			if (seamProject.hasNoStorage() || delta == null ) {
				//Resource visitor filters project members to be processed
				getProject().accept(resourceVisitor);
			} else {
				delta.accept(resourceVisitor);
			}

			long end = System.currentTimeMillis();
			seamProject.fullBuildTime += end - begin;
//			try {
//				//It is important to save results of build right after the build is done.
//				//Otherwise, at Eclipse restart, the results can be lost.
//				seamProject.store();
//			} catch (IOException e) {
//				SeamCorePlugin.getDefault().logError(e);
//			}
			seamProject.postBuild();

		} finally {
			seamProject.fireChanges();
		}
		// Check if we need to return something here instead of null
		return null;
	}

	
	/**
	 * Access to xml scanner for test.
	 * @return
	 */
	public static IFileScanner createXMLScanner() {
		return new XMLScanner();
	}

	/**
	 * Access to java scanner for test.
	 * @return
	 */
	public static IFileScanner createJavaScanner() {
		return new JavaScanner();
	}

	/**
	 * Access to library scanner for test.
	 * @return
	 */
	public static IFileScanner createLibraryScanner() {
		return new LibraryScanner();
	}

	protected void clean(IProgressMonitor monitor) throws CoreException {
		((SeamProject)SeamCorePlugin.getSeamProject(getProject(), false)).clean();
	}

}
