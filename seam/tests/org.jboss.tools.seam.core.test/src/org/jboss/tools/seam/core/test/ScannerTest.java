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
package org.jboss.tools.seam.core.test;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.jboss.tools.common.model.XJob;
import org.jboss.tools.common.test.util.TestProjectProvider;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.ISeamProperty;
import org.jboss.tools.seam.core.SeamCoreBuilder;
import org.jboss.tools.seam.internal.core.SeamProject;
import org.jboss.tools.seam.internal.core.scanner.IFileScanner;

import junit.framework.TestCase;

public class ScannerTest extends TestCase {
	TestProjectProvider provider = null;
	IProject project = null;
	boolean makeCopy = true;

	public ScannerTest() {}
	
	protected void setUp() throws Exception {
		provider = new TestProjectProvider("org.jboss.tools.seam.core.test", null, "TestScanner", true); 
		project = provider.getProject();
		try {
			project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		} catch (Exception e) {
			e.printStackTrace();
		}
		XJob.waitForJob();
	}

	public void testXMLScanner() {
		try {
			XJob.waitForJob();
		} catch (Exception e) {
			fail("Interrupted");
		}
		ISeamProject seamProject = null;
		try {
			seamProject = (ISeamProject)project.getNature(SeamProject.NATURE_ID);
		} catch (Exception e) {
			fail("Cannot get seam nature.");
		}
		assertNotNull("Seam project is null", seamProject);
		
		
		IFile f = project.getFile("WebContent/WEB-INF/components.xml");
		assertTrue("Cannot find components.xml in test project", f != null && f.exists());
		
		IFileScanner scanner = SeamCoreBuilder.getXMLScanner();
		assertTrue("Scanner cannot recognise components.xml", scanner.isRelevant(f));
		assertTrue("Scanner cannot recognise components.xml content", scanner.isLikelyComponentSource(f));
		ISeamComponent[] cs = null;
		
		try {
			cs = scanner.parse(f);
		} catch (Exception e) {
			fail("Error in xml scanner:" + e.getMessage());
		}
		assertTrue("Components are not found in components.xml", cs != null && cs.length > 0);

		assertTrue("First component name must be " + "myComponent", "myComponent".equals(cs[0].getName()));

		//After having tested details of xml scanner now let us check
		// that it succeeded in build.
		ISeamComponent c = seamProject.getComponent("myComponent");
		
		assertTrue("Seam builder must put myComponent to project.", c != null);
		
		//We have list property in this component
		ISeamProperty<?> property = c.getProperty("myList");
		Object o = property.getValue();
		assertTrue("Property myList in myComponent must be instanceof java.util.List.", o instanceof List);
		List<?> oList = (List<?>)o;
		assertTrue("Property myList misses value 'value1.", "value1".equals(oList.get(0)));
		
		
	}
	
	public void testJavaScanner() {
		try {
			XJob.waitForJob();
		} catch (Exception e) {
			fail("Interrupted");
		}
		ISeamProject seamProject = null;
		try {
			seamProject = (ISeamProject)project.getNature(SeamProject.NATURE_ID);
		} catch (Exception e) {
			fail("Cannot get seam nature.");
		}
		assertNotNull("Seam project is null", seamProject);
		
		
		IFile f = project.getFile("JavaSource/demo/User.java");
		assertTrue("Cannot find User.java in test project", f != null && f.exists());
		
		IFileScanner scanner = SeamCoreBuilder.getJavaScanner();
		assertTrue("Scanner cannot recognise User.java", scanner.isRelevant(f));
		assertTrue("Scanner cannot recognise User.java content", scanner.isLikelyComponentSource(f));
		ISeamComponent[] cs = null;
		
		try {
			cs = scanner.parse(f);
		} catch (Exception e) {
			fail("Error in java scanner:" + e.getMessage());
		}
		assertTrue("Components are not found in User.java", cs != null && cs.length > 0);

		assertTrue("First component name must be " + "myUser", "myUser".equals(cs[0].getName()));

		 //After having tested details of java scanner now let us check
		 //that it succeeded in build.
		ISeamComponent c = seamProject.getComponent("myUser");
		
		assertTrue("Seam builder must put myUser to project.", c != null);		
	
	}

	public void testLibraryScanner() {
		
		try {
			XJob.waitForJob();
		} catch (Exception e) {
			fail("Interrupted");
		}
		ISeamProject seamProject = null;
		try {
			seamProject = (ISeamProject)project.getNature(SeamProject.NATURE_ID);
		} catch (Exception e) {
			fail("Cannot get seam nature.");
		}
		assertNotNull("Seam project is null", seamProject);
		
		
		IFile f = project.getFile("WebContent/WEB-INF/lib/jboss-seam.jar");
		assertTrue("Cannot find User.java in test project", f != null && f.exists());
		
		IFileScanner scanner = SeamCoreBuilder.getLibraryScanner();
		assertTrue("Scanner cannot recognise jboss-seam.jar", scanner.isRelevant(f));
		assertTrue("Scanner cannot recognise jboss-seam.jar content", scanner.isLikelyComponentSource(f));
		ISeamComponent[] cs = null;
		
		try {
			cs = scanner.parse(f);
		} catch (Exception e) {
			fail("Error in library scanner:" + e.getMessage());
		}
		assertTrue("Components are not found in jboss-seam.jar", cs != null && cs.length > 0);
		
		boolean hasActor = false;
		for (int i = 0; i < cs.length && !hasActor; i++) {
			if("actor".equals(cs[0].getName())) hasActor = true;
		}

		assertTrue("Component " + "actor" + " is not found in jboss-seam.jar", hasActor);
		
		try {
			project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
			XJob.waitForJob();
		} catch (Exception e) {
			fail("Cannot build");
		}

		/*
		 * After having tested details of library scanner now let us check
		 * that it succeeded in build.
		 */
		ISeamComponent c = seamProject.getComponent("actor");
		
		assertTrue("Seam builder must put actor to project.", c != null);		
	
	}
	
}
