/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
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
public class ComponentsFromLibTest extends TestCase {
	static String BUNDLE = "org.jboss.tools.seam.core.test";
	IProject project1;
	TestProjectProvider provider1;

	protected void setUp() throws Exception {
		provider1 = new TestProjectProvider(BUNDLE,"/projects/SeamJava" , "SeamJava", true);
		project1 = provider1.getProject();

		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		JobUtils.waitForIdle();
		
		IFile source = project1.getFile(new Path("lib/jboss-seam.1"));
		IFile target = project1.getFile(new Path("lib/jboss-seam.jar"));

		target.create(source.getContents(), IResource.FORCE, new NullProgressMonitor());

		//To ensure that the project is built.
		project1.build(IncrementalProjectBuilder.CLEAN_BUILD, new NullProgressMonitor());
		JobUtils.waitForIdle();

		project1.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
		JobUtils.waitForIdle();

		ResourcesUtils.setBuildAutomatically(saveAutoBuild);
	}
	
	public void testComponentsFromLib() {
		ISeamProject sp1 = getSeamProject1();
		
		String component = "org.jboss.seam.core.conversation";
		assertNotNull("Bean " + component + " is not found in project CycleTest1", sp1.getComponent(component));

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
	
	@Override
	protected void tearDown() throws Exception {
		ISeamProject sp1 = getSeamProject1();
		SeamProject impl1 = (SeamProject)sp1;
		if(impl1 != null) impl1.clearStorage();

		JobUtils.waitForIdle();
		provider1.dispose();
	}

}
