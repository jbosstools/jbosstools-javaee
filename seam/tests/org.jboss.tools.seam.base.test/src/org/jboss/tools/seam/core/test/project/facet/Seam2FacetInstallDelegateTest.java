/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.seam.core.test.project.facet;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.jboss.tools.seam.core.SeamProjectsSet;
import org.jboss.tools.seam.internal.core.project.facet.SeamProjectCreator;

/**
 * @author Alexey Kazakov
 */
public class Seam2FacetInstallDelegateTest extends AbstractSeam2FacetInstallDelegateTest {

	public Seam2FacetInstallDelegateTest(String name) {
		super(name);
	}

	public void testMvelWarJars() {
		final IContainer warLibs = (IContainer) warProject.getProject()
				.findMember("WebContent/WEB-INF/lib").getAdapter(
						IContainer.class);
		try {
			for (IResource resource : warLibs.members()) {
				if (resource.getName().matches("mvel.*\\.jar")) {
					return;
				}
			}
			fail("mvel*.jar weren't found in seam 2.0. WAR project");
		} catch (CoreException e) {
			fail("Error occured during search mvel libraries in lib folder");
		}
	}

	public void testMvelEarJars() {
		IProject war = earProject.getProject();

		SeamProjectsSet seamProjectsSet = new SeamProjectsSet(earProject
				.getProject());

		IProject ear = seamProjectsSet.getEarProject();
		final IContainer earLibs = (IContainer) ear.findMember(
				new Path("EarContent").append("lib")).getAdapter(
				IContainer.class);
		try {
			for (IResource resource : earLibs.members()) {
				if (resource.getName().matches("mvel.*\\.jar")) {
					return;
				}
			}
			fail("mvel*.jar weren't found in seam 2.0. EAR project");
		} catch (CoreException e) {
			fail("Error occured during search mvel libraries in lib folder");
		}
	}

	public void testBootstrapDirPresent() throws CoreException, IOException {
		SeamProjectsSet warPs = new SeamProjectsSet(warProject.getProject());

		IProject testProject = warPs.getTestProject();
		assertTrue(testProject.exists());

		assertNotNull(testProject.findMember("bootstrap"));
		// assertNotNull(testProject.findMember("bootstrap/data"));

		assertNull("embedded-ejb should not be installed for seam2",
				testProject.findMember("embedded-ejb"));
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.seam.core.test.project.facet.AbstractSeam2FacetInstallDelegateTest#getTestLibs()
	 */
	@Override
	protected Set<String> getTestLibs() {
		Set<String> libs = new HashSet<String>();

		libs.add("testng.jar");
		libs.add("hibernate-all.jar");
		// libs.add("jboss-deployers.jar"); // JBIDE-2431: There is no such jar
		// created by Seam 2.0 seamgen
		libs.add("jboss-embedded-all.jar");
		libs.add("thirdparty-all.jar");
		libs.add("jboss-embedded-api.jar");
		libs.add("core.jar");

		return libs;
	}

	public void testTestLibs() throws CoreException, IOException {
		SeamProjectsSet warPs = new SeamProjectsSet(warProject.getProject());

		IProject testProject = warPs.getTestProject();
		assertTrue(testProject.exists());

		Set<String> libs = getTestLibs();

		assertOnlyContainsTheseFiles(libs, testProject.findMember("lib"));

		// JBIDE-2431: The following block is commented because it duplicates
		// the call to assertOnlyContainsTheseFiles()
		/*
		 * assertNotNull(testProject.findMember("lib/testng.jar"));
		 * assertNotNull(testProject.findMember("lib/hibernate-all.jar")); //
		 * assertNotNull(testProject.findMember("lib/jboss-deployers.jar")); //
		 * JBIDE-2431: There is no such jar created by Seam 2.0 seamgen
		 * assertNotNull(testProject.findMember("lib/jboss-embedded-all.jar"));
		 * assertNotNull(testProject.findMember("lib/thirdparty-all.jar"));
		 * assertNotNull(testProject.findMember("lib/core.jar")); // JBIDE-2431:
		 * lib/core.jar file is always created by Seam 2.0 seamgen
		 */
	}

	/**
	 * See https://issues.jboss.org/browse/JBIDE-8076
	 * 
	 * @throws CoreException
	 * @throws IOException
	 */
	public void testTestProjectClassPath() throws CoreException, IOException {
		SeamProjectsSet warPs = new SeamProjectsSet(warProject.getProject());

		IProject testProject = warPs.getTestProject();
		assertTrue(testProject.exists());

		IClasspathEntry warCpe = SeamProjectCreator.getJreContainer(warProject.getProject());
		assertNotNull(warCpe);

		IClasspathEntry testCpe = SeamProjectCreator.getJreContainer(testProject);
		assertNotNull(testCpe);

		assertEquals(warCpe.getPath(), testCpe.getPath());
	}

	public void testJbpmPresent() throws CoreException, IOException {
		SeamProjectsSet earPs = new SeamProjectsSet(earProject.getProject());

		IProject project = earPs.getEarProject();
		assertTrue(project.exists());

		assertNotNull(project.findMember("EarContent/lib/jbpm-jpdl.jar"));
	}

	public void testDroolsPresent() throws CoreException, IOException {
		IProject earRoot = earProject.getProject();
		SeamProjectsSet seamProjectsSet = new SeamProjectsSet(earRoot);

		// JBIDE-2431: security.drl is always created in <EJBProject>/ejbModule
		// directory by Seam 2.0 seamgen
		IProject ejbProject = seamProjectsSet.getEjbProject();
		assertNotNull(ejbProject.findMember("ejbModule/security.drl"));
	}
}