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
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.core.internal.RuntimeWorkingCopy;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;

/**
 * @author V.Kabanovich
 *
 */
public class SeamEARTest extends TestCase {
	IProject projectEAR = null;
	IProject projectWAR = null;
	IProject projectEJB = null;

	boolean makeCopy = true;

	@Override
	protected void setUp() throws Exception {
		projectEAR = ResourcesPlugin.getWorkspace().getRoot().getProject("Test1-ear");
		assertTrue(projectEAR.exists());
		projectEJB = ResourcesPlugin.getWorkspace().getRoot().getProject("Test1-ejb");
		assertTrue(projectEJB.exists());
		projectWAR = ResourcesPlugin.getWorkspace().getRoot().getProject("Test1");
		assertTrue(projectWAR.exists());
		projectEAR.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
		projectEJB.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
		projectWAR.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
	}

	private ISeamProject getSeamProject(IProject project) throws CoreException {
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

		c = seamProject.getComponent("org.jboss.seam.core.interpolator");
		assertNotNull("War project must see component 'org.jboss.seam.core.interpolator' declared in ejb project", c);
	}

	/**
	 * See https://jira.jboss.org/browse/JBIDE-7066
	 * Seam model is not loaded if there are two dependent Seam projects and one of them has been cleaned.
	 * 
	 * @throws CoreException
	 */
	public void testCleanEarProject() throws CoreException {
		ISeamProject seamProject = getSeamProject(projectWAR);
		ISeamComponent c = seamProject.getComponent("authenticator");

		assertNotNull("War project must see component 'authenticator' declared in ejb project", c);

		projectWAR.build(IncrementalProjectBuilder.CLEAN_BUILD, new NullProgressMonitor());

		c = seamProject.getComponent("authenticator");
		assertNull("War project must see component 'authenticator' declared in ejb project", c);

		projectWAR.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());

		c = seamProject.getComponent("authenticator");
		assertNotNull("War project must see component 'authenticator' declared in ejb project", c);
	}

	static IRuntime createRuntime(String runtimeName) throws CoreException {
		IRuntimeWorkingCopy runtime = null;
		String type = null;
		String version = null;
		String runtimeId = null;
		IPath jbossAsLocationPath = new Path(System.getProperty("jbosstools.test.jboss.home.4.2",
				"C:/jbdevstudio/jboss-eap/jboss-as"));
		IRuntimeType[] runtimeTypes = ServerUtil.getRuntimeTypes(null, null, "org.jboss.ide.eclipse.as.runtime.42");
		if (runtimeTypes.length > 0) {
			runtime = runtimeTypes[0].createRuntime(null, new NullProgressMonitor());
			runtime.setLocation(jbossAsLocationPath);
			if(runtimeName!=null) {
				runtime.setName(runtimeName);				
			}
			IVMInstall defaultVM = JavaRuntime.getDefaultVMInstall();
			// IJBossServerRuntime.PROPERTY_VM_ID
			((RuntimeWorkingCopy) runtime).setAttribute("PROPERTY_VM_ID", defaultVM.getId());
			// IJBossServerRuntime.PROPERTY_VM_TYPE_ID
			((RuntimeWorkingCopy) runtime).setAttribute("PROPERTY_VM_TYPE_ID", defaultVM.getVMInstallType().getId());
			// IJBossServerRuntime.PROPERTY_CONFIGURATION_NAME
			((RuntimeWorkingCopy) runtime).setAttribute("org.jboss.ide.eclipse.as.core.runtime.configurationName", "default");

			return runtime.save(false, new NullProgressMonitor());
		}
		return runtime;
	}
}