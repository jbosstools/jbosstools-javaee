/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.seam.core.test;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.jboss.tools.common.model.XJob;
import org.jboss.tools.common.test.util.TestProjectProvider;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.SeamProject;
import org.jboss.tools.test.util.JUnitUtils;

import junit.framework.TestCase;

/**
 * @author V.Kabanovich
 *
 */
public class SeamEARTest extends TestCase {
	IProject projectEAR = null;
	IProject projectWAR = null;
	IProject projectEJB = null;
	
	boolean makeCopy = false;

	public SeamEARTest() {}

	protected void setUp() throws Exception {
		TestProjectProvider providerEAR = new TestProjectProvider("org.jboss.tools.seam.core.test", null, "Test1-ear", makeCopy);
		projectEAR = providerEAR.getProject();
		
		TestProjectProvider providerWAR = new TestProjectProvider("org.jboss.tools.seam.core.test", null, "Test1", makeCopy);
		projectWAR = providerWAR.getProject();
		
		TestProjectProvider providerEJB = new TestProjectProvider("org.jboss.tools.seam.core.test", null, "Test1-ejb", makeCopy);
		projectEJB = providerEJB.getProject();

		try {
			projectEAR.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
			projectWAR.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
			projectEJB.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		} catch (Exception e) {
			JUnitUtils.fail("Error in refreshing",e);
		}

		try {
			XJob.waitForJob();
		} catch (InterruptedException e) {
			JUnitUtils.fail("Interrupted",e);
		}
	}

	private ISeamProject getSeamProject(IProject project) {
		try {
			XJob.waitForJob();
		} catch (Exception e) {
			JUnitUtils.fail("Interrupted",e);
		}
		try {
			project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
			try {
				XJob.waitForJob();
			} catch (InterruptedException e) {
				JUnitUtils.fail("Interrupted",e);
			}
		} catch (Exception e) {
			JUnitUtils.fail("Cannot build", e);
		}
		ISeamProject seamProject = null;
		
		/*
		 * SeamCorePlugin.getSeamProject(IProject project, boolean resolve);
		 * is used to load Seam Project properly. 
		 * 
		 * It's not enough to use the following code:
		 * (ISeamProject)project.getNature(SeamProject.NATURE_ID);
		 */
		seamProject = SeamCorePlugin.getSeamProject(project, true);
//		try {
//			seamProject = (ISeamProject)project.getNature(SeamProject.NATURE_ID);
//		} catch (Exception e) {
//			JUnitUtils.fail("Cannot get seam nature.",e);
//		}
		assertNotNull("Seam project is null", seamProject);
		return seamProject;
	}
	
	/**
	 * This empty test is meaningful as it gives Eclipse opportunity 
	 * to pass for the first time setUp() and show the license dialog 
	 * that may cause InterruptedException for XJob.waitForJob()
	 */
	public void testCreatingProject() {
	}
	
	public void testProject() {
		ISeamProject seamProject = getSeamProject(projectWAR);
		ISeamComponent c = seamProject.getComponent("authenticator");
		
		assertNotNull("War project must see component 'authenticator' declared in ejb project", c);
		
	}

}
