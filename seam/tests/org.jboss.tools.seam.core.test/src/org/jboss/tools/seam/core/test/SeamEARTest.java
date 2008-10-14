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

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.jboss.tools.common.test.util.TestProjectProvider;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.test.util.JobUtils;

/**
 * @author V.Kabanovich
 *
 */
public class SeamEARTest extends TestCase {
	IProject projectEAR = null;
	IProject projectWAR = null;
	IProject projectEJB = null;
	
	boolean makeCopy = true;

	public SeamEARTest() {}

	protected void setUp() throws Exception {
		TestProjectProvider providerEAR = new TestProjectProvider("org.jboss.tools.seam.core.test", null, "Test1-ear", makeCopy);
		projectEAR = providerEAR.getProject();

		TestProjectProvider providerWAR = new TestProjectProvider("org.jboss.tools.seam.core.test", null, "Test1", makeCopy);
		projectWAR = providerWAR.getProject();

		TestProjectProvider providerEJB = new TestProjectProvider("org.jboss.tools.seam.core.test", null, "Test1-ejb", makeCopy);
		projectEJB = providerEJB.getProject();

		projectEAR.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		projectWAR.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		projectEJB.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());

		JobUtils.waitForIdle();
	}

	private ISeamProject getSeamProject(IProject project) throws CoreException {

		project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
		JobUtils.waitForIdle();
		ISeamProject seamProject = null;
		
		/*
		 * SeamCorePlugin.getSeamProject(IProject project, boolean resolve);
		 * is used to load Seam Project properly. 
		 * 
		 * It's not enough to use the following code:
		 * (ISeamProject)project.getNature(SeamProject.NATURE_ID);
		 */
		seamProject = SeamCorePlugin.getSeamProject(project, true);

		assertNotNull("Seam project is null", seamProject);
		return seamProject;
	}
	
	public void testEarProject() throws CoreException {
		ISeamProject seamProject = getSeamProject(projectWAR);
		ISeamComponent c = seamProject.getComponent("authenticator");

		assertNotNull("War project must see component 'authenticator' declared in ejb project", c);
	}
}