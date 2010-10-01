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
import org.eclipse.core.runtime.NullProgressMonitor;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.internal.core.SeamProject;
import org.jboss.tools.test.util.JUnitUtils;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ResourcesUtils;
import org.jboss.tools.test.util.TestProjectProvider;

/**
 * Test that Seam builder works for projects that set cyclic dependency in build paths.

 * @author Viacheslav Kabanovich
 * 
 */
public class CyclicDependingProjectsTest extends TestCase {
	static String BUNDLE = "org.jboss.tools.seam.core.test";
	IProject project1;
	TestProjectProvider provider1;
	IProject project2;
	TestProjectProvider provider2;

	protected void setUp() throws Exception {
		provider1 = new TestProjectProvider(BUNDLE,"/projects/CycleTest1" , "CycleTest1", true);
		project1 = provider1.getProject();
		provider2 = new TestProjectProvider(BUNDLE,"/projects/CycleTest2" , "CycleTest2", true);
		project2 = provider2.getProject();

		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		JobUtils.waitForIdle();

		//To ensure that the project is built.
		project1.build(IncrementalProjectBuilder.CLEAN_BUILD, new NullProgressMonitor());
		project2.build(IncrementalProjectBuilder.CLEAN_BUILD, new NullProgressMonitor());

		project1.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
		project2.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());

		ResourcesUtils.setBuildAutomatically(saveAutoBuild);
	}
	
	public void testCyclicDependingProjects() {
		ISeamProject sp1 = getSeamProject1();
		
		assertNotNull("Bean test.bean1 is not found in project CycleTest1", sp1.getComponent("test.bean1"));
		assertNotNull("Bean test.bean2 is not found in project CycleTest1", sp1.getComponent("test.bean2"));

		ISeamProject sp2 = getSeamProject2();

		assertNotNull("Bean test.bean1 is not found in project CycleTest2", sp2.getComponent("test.bean1"));
		assertNotNull("Bean test.bean2 is not found in project CycleTest2", sp2.getComponent("test.bean2"));
	}


	private ISeamProject getSeamProject1() {
		ISeamProject seamProject = null;
		try {
			seamProject = (ISeamProject)project1.getNature(SeamProject.NATURE_ID);
		} catch (Exception e) {
			JUnitUtils.fail("Cannot get seam nature.",e);
		}
		assertNotNull("Seam project is null", seamProject);
		return seamProject;
	}
	
	private ISeamProject getSeamProject2() {
		ISeamProject seamProject = null;
		try {
			seamProject = (ISeamProject)project2.getNature(SeamProject.NATURE_ID);
		} catch (Exception e) {
			JUnitUtils.fail("Cannot get seam nature.",e);
		}
		assertNotNull("Seam project is null", seamProject);
		return seamProject;
	}
	
	@Override
	protected void tearDown() throws Exception {
		ISeamProject sp1 = getSeamProject1();
		SeamProject impl1 = (SeamProject)sp1;
		if(impl1 != null) impl1.clearStorage();

		ISeamProject sp2 = getSeamProject2();
		SeamProject impl2 = (SeamProject)sp2;
		if(impl2 != null) impl2.clearStorage();

		JobUtils.waitForIdle();
		provider1.dispose();
		provider2.dispose();
	}

}
