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
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.jboss.tools.common.model.XJob;
import org.jboss.tools.common.test.util.TestProjectProvider;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamComponentDeclaration;
import org.jboss.tools.seam.core.ISeamFactory;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.ISeamProperty;
import org.jboss.tools.seam.core.ScopeType;
import org.jboss.tools.seam.core.SeamCoreBuilder;
import org.jboss.tools.seam.core.event.ISeamValueList;
import org.jboss.tools.seam.core.event.ISeamValueString;
import org.jboss.tools.seam.internal.core.SeamProject;
import org.jboss.tools.seam.internal.core.scanner.IFileScanner;
import org.jboss.tools.seam.internal.core.scanner.lib.ClassPath;
import org.jboss.tools.seam.internal.core.scanner.lib.LibraryScanner;

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
		try {
			XJob.waitForJob();
		} catch (InterruptedException e) {
			//ignore
		}
	}
	
	private ISeamProject getSeamProject() {
		try {
			XJob.waitForJob();
		} catch (Exception e) {
			fail("Interrupted");
		}
		try {
			project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
			XJob.waitForJob();
		} catch (Exception e) {
			fail("Cannot build");
		}
		ISeamProject seamProject = null;
		try {
			seamProject = (ISeamProject)project.getNature(SeamProject.NATURE_ID);
		} catch (Exception e) {
			fail("Cannot get seam nature.");
		}
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

	public void testXMLScanner() {
		ISeamProject seamProject = getSeamProject();

		IFile f = project.getFile("WebContent/WEB-INF/components.xml");
		assertTrue("Cannot find components.xml in test project", f != null && f.exists());
		
		IFileScanner scanner = SeamCoreBuilder.getXMLScanner();
		assertTrue("Scanner cannot recognise components.xml", scanner.isRelevant(f));
		assertTrue("Scanner cannot recognise components.xml content", scanner.isLikelyComponentSource(f));
		ISeamComponentDeclaration[] cs = null;
		
		try {
			cs = scanner.parse(f).getComponents().toArray(new ISeamComponentDeclaration[0]);
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
		List<ISeamProperty> prs = c.getProperties("myList");
		assertTrue("Property myList is not found in components.xml", prs.size() == 1);		
		ISeamProperty property = prs.get(0);
		Object o = property.getValue();
		assertTrue("Property myList in myComponent must be instanceof ISeamValueList", o instanceof ISeamValueList);
		ISeamValueList oList = (ISeamValueList)o;
		assertTrue("Property myList misses value 'value1.", "value1".equals(oList.getValues().get(0).getValue().getValue()));
	}
	
	public void testJavaScanner() {
		ISeamProject seamProject = getSeamProject();
		
		IFile f = project.getFile("JavaSource/demo/User.java");
		assertTrue("Cannot find User.java in test project", f != null && f.exists());
		
		IFileScanner scanner = SeamCoreBuilder.getJavaScanner();
		assertTrue("Scanner cannot recognise User.java", scanner.isRelevant(f));
		assertTrue("Scanner cannot recognise User.java content", scanner.isLikelyComponentSource(f));
		ISeamComponentDeclaration[] cs = null;
		
		try {
			cs = scanner.parse(f).getComponents().toArray(new ISeamComponentDeclaration[0]);
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
		ISeamProject seamProject = getSeamProject();
		
		IFile f = project.getFile("WebContent/WEB-INF/lib/jboss-seam.jar");
		assertTrue("Cannot find User.java in test project", f != null && f.exists());
		
		LibraryScanner scanner =(LibraryScanner)SeamCoreBuilder.getLibraryScanner();
		ClassPath cp = ((SeamProject)seamProject).getClassPath();
		scanner.setClassPath(cp);
		cp.update();

		assertTrue("Scanner cannot recognise jboss-seam.jar", scanner.isRelevant(f));
		assertTrue("Scanner cannot recognise jboss-seam.jar content", scanner.isLikelyComponentSource(f));

		ISeamFactory[] cs = null;
		
		try {
			cs = scanner.parse(f).getFactories().toArray(new ISeamFactory[0]);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error in library scanner:" + e.getMessage());
		}
		assertTrue("Factories are not found in jboss-seam.jar", cs != null && cs.length > 0);
		
		boolean hasActor = false;
		for (int i = 0; i < cs.length && !hasActor; i++) {
			if("actor".equals(cs[i].getName())) hasActor = true;
		}

		assertTrue("Factory " + "actor" + " is not found in jboss-seam.jar", hasActor);
		
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
		Set<ISeamFactory> components = seamProject.getFactoriesByName("actor");
	
		assertTrue("Seam builder must put actor to project.", components.size()==1);		
	}
	
	/**
	 * This method is to cover most cases of configuring components 
	 */
	public void testSeamProjectObjects() {
		ISeamProject seamProject = getSeamProject();
	
		//1. components.xml has entry
		// <core:managed-persistence-context name="myPersistenceContext1"/>
		// check that myPersistenceContext1 exists and has scope CONVERSATION
		
		ISeamComponent c = seamProject.getComponent("myPersistenceContext1");
		assertNotNull("Component myPersistenceContext1 not found.", c);
		ScopeType scope = c.getScope();
		assertTrue("Component myPersistenceContext1 has scope=" + (scope == null ? null : scope.getLabel()) + ", but has to have " + ScopeType.CONVERSATION.getLabel(), ScopeType.CONVERSATION == scope);		
		
		//2. components.xml has entry
		//<core:resource-bundle>
		// 	<core:bundle-names>
		// 		<value>bundleA</value>
		// 		<value>bundleB</value>
		// 	</core:bundle-names>
		//</core:resource-bundle>
		// check that
		// a) component org.jboss.seam.core.resourceBundle exists,
		// b) getClassName returns org.jboss.seam.core.ResourceBundle
		// c) it has property bundleNames as list with two specified values.
		
		c = seamProject.getComponent("org.jboss.seam.core.resourceBundle");
		assertNotNull("Component org.jboss.seam.core.resourceBundle not found.", c);
		String className = c.getClassName();
		assertTrue("Class name of org.jboss.seam.core.resourceBundle must be "
			+ "org.jboss.seam.core.ResourceBundle " 
			+ " rather than " + className, "org.jboss.seam.core.ResourceBundle".equals(className));
		List<ISeamProperty> bundleNamesPropertyList = c.getProperties("bundleNames");
		assertTrue("Property bundleNames is not found", bundleNamesPropertyList != null && bundleNamesPropertyList.size() == 1);
		ISeamProperty bundleNamesProperty = bundleNamesPropertyList.get(0);
		assertTrue("Value of bundleNames must be instanceof ISeamValueList", (bundleNamesProperty.getValue() instanceof ISeamValueList));
		ISeamValueList bundleNames = (ISeamValueList)bundleNamesProperty.getValue();
		List<ISeamValueString> valueList = bundleNames.getValues();
		assertTrue("There must be 2 bundle names rather than " + valueList.size(), valueList.size() == 2);
		assertTrue("First bundle name is " + valueList.get(0).getValue().getValue() + " rather than bundleA"
				, "bundleA".equals(valueList.get(0).getValue().getValue()));
		assertTrue("Second bundle name is " + valueList.get(1).getValue().getValue() + " rather than bundleB"
				, "bundleB".equals(valueList.get(1).getValue().getValue()));
		
		//3. components.xml has entry
		// <core:manager
		// 	conversation-is-long-running-parameter="a"
		// 	parent-conversation-id-parameter="b"
		// 	conversation-id-parameter="c"
		// 	concurrent-request-timeout="2"
		// 	conversation-timeout="3"
		// />
		// check that
		// a) component org.jboss.seam.core.manager exists,
		// b) specified properties are loaded correctly
		
		String[][] managerTestProperties = new String[][]{
			{"conversationIsLongRunningParameter", "a"},
			{"parentConversationIdParameter", "b"},
			{"conversationIdParameter", "c"},
			{"concurrentRequestTimeout", "2"},
			{"conversationTimeout", "3"}
		};
		scanSimpleProperties(seamProject, "org.jboss.seam.core.manager", managerTestProperties);
		
		//4. seam.properties has entry
		//org.jboss.seam.core.microcontainer.persistenceUnitName=MyPersistenceUnit
		// check that
		// a) component org.jboss.seam.core.microcontainer exists,
		// b) specified property is loaded correctly

		String[][] microcontainerTestProperties = new String[][]{
			{"persistenceUnitName", "MyPersistenceUnit"},
		};
		scanSimpleProperties(seamProject, "org.jboss.seam.core.microcontainer", microcontainerTestProperties);
	}
	
	/**
	 * Tests if component componentName exists.
	 * Then tests if it has properties, names of which are listed by testProperties[i][0],
	 * and compares their values to those listed in testProperties[i][1].
	 * @param seamProject
	 * @param componentName
	 * @param testProperties = String[][]{{name1, value1}, {name2, value2}, ... }
	 */
	private void scanSimpleProperties(ISeamProject seamProject, String componentName, String[][] testProperties) {
		ISeamComponent c = seamProject.getComponent(componentName);
		assertNotNull("Component " + componentName + " not found.", c);
		
		for (int p = 0; p < testProperties.length; p++) {
			String propertyName = testProperties[p][0]; 
			String expectedValue = testProperties[p][1]; 
			List<ISeamProperty> ps = c.getProperties(propertyName);
			assertTrue("Property " + propertyName + " is not found", ps != null && ps.size() == 1);
			ISeamProperty property = ps.get(0);
			ISeamValueString valueObject = (ISeamValueString)property.getValue();
			String actualValue = valueObject.getValue().getValue();
			assertTrue("Property " + propertyName + " has value " + actualValue + " rather than " + expectedValue, expectedValue.equals(actualValue));
		}
		
	}

}
