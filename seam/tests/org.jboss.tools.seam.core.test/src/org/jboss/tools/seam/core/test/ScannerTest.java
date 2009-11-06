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

import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.jboss.tools.common.test.util.TestProjectProvider;
import org.jboss.tools.common.text.ITextSourceReference;
import org.jboss.tools.seam.core.BeanType;
import org.jboss.tools.seam.core.BijectedAttributeType;
import org.jboss.tools.seam.core.IBijectedAttribute;
import org.jboss.tools.seam.core.ISeamAnnotatedFactory;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamComponentDeclaration;
import org.jboss.tools.seam.core.ISeamComponentMethod;
import org.jboss.tools.seam.core.ISeamFactory;
import org.jboss.tools.seam.core.ISeamJavaComponentDeclaration;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.ISeamProperty;
import org.jboss.tools.seam.core.ISeamXmlComponentDeclaration;
import org.jboss.tools.seam.core.ISeamXmlFactory;
import org.jboss.tools.seam.core.ScopeType;
import org.jboss.tools.seam.core.SeamComponentMethodType;
import org.jboss.tools.seam.core.SeamCoreBuilder;
import org.jboss.tools.seam.core.event.ISeamValueList;
import org.jboss.tools.seam.core.event.ISeamValueMap;
import org.jboss.tools.seam.core.event.ISeamValueMapEntry;
import org.jboss.tools.seam.core.event.ISeamValueString;
import org.jboss.tools.seam.internal.core.SeamProject;
import org.jboss.tools.seam.internal.core.scanner.IFileScanner;
import org.jboss.tools.seam.internal.core.scanner.LoadedDeclarations;
import org.jboss.tools.seam.internal.core.scanner.lib.ClassPath;
import org.jboss.tools.seam.internal.core.scanner.lib.LibraryScanner;
import org.jboss.tools.test.util.JUnitUtils;

public class ScannerTest extends TestCase {
	IProject project = null;
	TestProjectProvider provider = null;
	boolean makeCopy = true;

	public ScannerTest() {
		super("Seam Scanner test");
	}
	
	protected void setUp() throws Exception {
		provider = new TestProjectProvider("org.jboss.tools.seam.core.test",
				null,"TestScanner" ,true);
		project = provider.getProject();
		project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		this.project.build(IncrementalProjectBuilder.FULL_BUILD, null);
		//EditorTestHelper.joinBackgroundActivities();
	}

	private ISeamProject getSeamProject() {
		ISeamProject seamProject = null;
		try {
			seamProject = (ISeamProject)project.getNature(SeamProject.NATURE_ID);
		} catch (Exception e) {
			JUnitUtils.fail("Cannot get seam nature.",e);
		}
		assertNotNull("Seam project is null", seamProject);
		return seamProject;
	}

	/**
	 * This test is to check different cases of declaring components in xml.
	 * It does not check interaction of xml declaration with other declarations.
	 */
	public void testXMLScanner() {
		ISeamProject seamProject = getSeamProject();

		IFile f = project.getFile("WebContent/WEB-INF/components.xml");
		assertTrue("Cannot find components.xml in test project", f != null && f.exists());
		
		IFileScanner scanner = SeamCoreBuilder.getXMLScanner();
		assertTrue("Scanner cannot recognise components.xml", scanner.isRelevant(f));
		assertTrue("Scanner cannot recognise components.xml content", scanner.isLikelyComponentSource(f));
		ISeamComponentDeclaration[] cs = null;
		ISeamFactory[] fs = null;
		
		try {
			LoadedDeclarations ds = scanner.parse(f, seamProject);
			cs = ds.getComponents().toArray(new ISeamComponentDeclaration[0]);
			fs = ds.getFactories().toArray(new ISeamFactory[0]);
		} catch (Exception e) {
			JUnitUtils.fail("Error in xml scanner",e);
		}
		assertTrue("Components are not found in components.xml", cs != null && cs.length > 0);

		//1. components.xml has entry
		// <component class="java.lang.Boolean" name="myComponent" scope="page">
		//  <property name="property1">value1</property>
		//  <property name="myList">
		//   <value>value1</value>
		//  </property>
		// <property name="myMap">
		//   <key>key1</key>
		//   <value>map value 1</value>
		//  </property>
		// </component>
		// check that
		// a) Declaration "myComponent" is loaded
		// b) property "property1" has value 'value1'
		// c) property "myList" has value 'value1'
		// d) property "myMap" has value 'map value 1' for key 'key1'
		ISeamComponentDeclaration myComponent = findDeclaration(cs, "myComponent");
		assertTrue("Declaration of " + "myComponent" + " is not found", myComponent instanceof ISeamXmlComponentDeclaration);

		//We have list property in this component
		ISeamProperty property = ((ISeamXmlComponentDeclaration)myComponent).getProperty("myList");
		assertTrue("Property myList is not found in declaration 'myComponent'", property != null);		
		Object o = property.getValue();
		assertTrue("Property myList in myComponent must be instanceof ISeamValueList", o instanceof ISeamValueList);
		ISeamValueList oList = (ISeamValueList)o;
		assertTrue("Property myList misses value 'value1.", "value1".equals(oList.getValues().get(0).getValue().getValue()));
		
		property = ((ISeamXmlComponentDeclaration)myComponent).getProperty("myMap");
		assertTrue("Property myMap is not found in declaration 'myComponent'", property != null);		
		o = property.getValue();
		assertTrue("Property myMap in myComponent must be instanceof ISeamValueMap", o instanceof ISeamValueMap);
		ISeamValueMap oMap = (ISeamValueMap)o;
		List<ISeamValueMapEntry> es = oMap.getEntries();
		assertTrue("Property myMap in myComponent is empty", es.size() > 0);

		assertTrue("First entry in myMap must have key='key1'", "key1".equals(es.get(0).getKey().getValue().getValue()));
		assertTrue("First entry in myMap must have value='map value 1'", "map value 1".equals(es.get(0).getValue().getValue().getValue()));
		
		//2. components.xml has entry
		// <core:resource-bundle>
		// 	<core:bundle-names>
		// 		<value>bundleA</value>
		// 		<value>bundleB</value>
		// 	</core:bundle-names>
		// </core:resource-bundle>
		// check that
		// a) declaration org.jboss.seam.core.resourceBundle exists,
		// b) it has property bundleNames as list with two specified values.
		ISeamComponentDeclaration resourceBundle = findDeclaration(cs, "org.jboss.seam.core.resourceBundle");
		assertTrue("Declaration of " + "org.jboss.seam.core.resourceBundle" + " is not found", resourceBundle instanceof ISeamXmlComponentDeclaration);
		property = ((ISeamXmlComponentDeclaration)resourceBundle).getProperty("bundleNames");
		assertTrue("Property 'bundleNames' is not found in declaration 'org.jboss.seam.core.resourceBundle'", property != null);		
		o = property.getValue();
		assertTrue("Property bundleNames in myComponent must be instanceof ISeamValueList", o instanceof ISeamValueList);
		oList = (ISeamValueList)o;
		assertTrue("Property bundleNames misses value 'bundleA'.", "bundleA".equals(oList.getValues().get(0).getValue().getValue()));
		assertTrue("Property bundleNames misses value 'bundleB'.", "bundleB".equals(oList.getValues().get(1).getValue().getValue()));
		
		//3. components.xml has entry
		//<factory name="factory1" scope="conversation"/>
		// check that
		// a) declaration 'factory1' exists,
		// b) it has scope 'conversation'.
		ISeamFactory factory = find(fs, "factory1");
		assertTrue("Declared factory 'factory1' is not found in components.xml", factory != null);
		ISeamXmlFactory af = (ISeamXmlFactory)factory;
		assertTrue("Scope of 'factory1' must be 'conversation'", af.getScope() == ScopeType.CONVERSATION);
		
		//4. components.xml has duplicated entry
		//<component class="java.lang.Boolean" name="duplicated"/>
		// check that
		// both declarations for "duplicated" are loaded
		int duplicatedCount = 0;
		for (int i = 0; i < cs.length; i++) {
			if("duplicated".equals(cs[i].getName())) duplicatedCount++;
		}
		assertEquals("There are 2 declarations of component \"duplicated\" in xml.", 2, duplicatedCount);
		
		//5. components.xml has components with different precedence.
		ISeamComponentDeclaration c10 = findDeclaration(cs, "compWithPrecedence10");
		assertNotNull(c10);
		assertEquals("10", ((ISeamXmlComponentDeclaration)c10).getPrecedence());
		ISeamComponentDeclaration cDefault = findDeclaration(cs, "compWithDefaultPrecedence");
		assertNotNull(cDefault);
		assertEquals("20", ((ISeamXmlComponentDeclaration)cDefault).getPrecedence());
		ISeamComponentDeclaration c20 = findDeclaration(cs, "compWithPrecedence20");
		assertNotNull(c20);
		assertEquals("20", ((ISeamXmlComponentDeclaration)c20).getPrecedence());
	}
	
	private ISeamComponentDeclaration findDeclaration(ISeamComponentDeclaration[] declarations, String name) {
		for (int i = 0; i < declarations.length; i++) {
			if(name.equals(declarations[i].getName())) return declarations[i];
		}
		return null;
	}
	
	public void testJavaScanner() {
		ISeamProject seamProject = getSeamProject();
		
		IFile f = project.getFile("JavaSource/demo/User.java");
		assertTrue("Cannot find User.java in test project", f != null && f.exists());
		
		IFileScanner scanner = SeamCoreBuilder.getJavaScanner();
		assertTrue("Scanner cannot recognise User.java", scanner.isRelevant(f));
		assertTrue("Scanner cannot recognise User.java content", scanner.isLikelyComponentSource(f));
		ISeamComponentDeclaration[] cs = null;
		ISeamFactory[] fs = null;
		
		try {
			LoadedDeclarations ds = scanner.parse(f, seamProject);
			cs = ds.getComponents().toArray(new ISeamComponentDeclaration[0]);
			fs = ds.getFactories().toArray(new ISeamFactory[0]);
		} catch (Exception e) {
			JUnitUtils.fail("Error in java scanner",e);
		}
		assertTrue("Components are not found in User.java", cs != null && cs.length > 0);
		
		ISeamJavaComponentDeclaration myUser = (ISeamJavaComponentDeclaration)findDeclaration(cs, "myUser");

		assertTrue("Component declaration myUser not found", myUser != null);

		 //After having tested details of java scanner now let us check
		 //that it succeeded in build.
		ISeamComponent c = seamProject.getComponent("myUser");
		assertTrue("Seam builder must put myUser to project.", c != null);

		//Now check annotations in User.java
		// a) @Scope(ScopeType.APPLICATION)
		ScopeType scope = myUser.getScope();
		assertTrue("Declared scope for myUser is Application rather than " + scope.getLabel(), scope == ScopeType.APPLICATION);
		
		// b) @Install(precedence=Install.FRAMEWORK)
		int precedence = myUser.getPrecedence();
		assertTrue("Declared precedence for myUser is 10 rather than " + precedence, precedence == 10);
		
		// c) @Entity
		boolean isEntity = myUser.isEntity();
		assertTrue("Java source for myUser is declared as entity", isEntity);

		// d) @In @Out
		Set<IBijectedAttribute> bijected = myUser.getBijectedAttributes();
		
		IBijectedAttribute a1 = findBijectedAttribute(bijected, "address");
		assertTrue("Attribute 'address' is not found in bijected attributes", a1 != null);
		assertTrue("Attribute 'address' is @Out annotated", a1.isOfType(BijectedAttributeType.OUT));
	
		IBijectedAttribute a2 = findBijectedAttribute(bijected, "payment");
		assertTrue("Attribute 'payment' is not found in bijected attributes", a2 != null);
		assertTrue("Attribute 'payment' is @In annotated", a2.isOfType(BijectedAttributeType.IN));

		// e) @Create @Destroy @Unwrap
		Set<ISeamComponentMethod> methods = myUser.getMethods();
		ISeamComponentMethod m = find(methods, "unwrapMethod");
		assertTrue("Declared method 'unwrapMethod' is not found in 'myUser'", m != null);
		assertTrue("Method 'unwrapMethod' in 'myUser' must be create method", m.isOfType(SeamComponentMethodType.UNWRAP));
		m = find(methods, "createAndDestroyMethod");
		assertTrue("Declared method 'createAndDestroyMethod' is not found in 'myUser'", m != null);
		assertTrue("Method 'createAndDestroyMethod' in 'myUser' must be create method", m.isOfType(SeamComponentMethodType.CREATE));
		assertTrue("Method 'createAndDestroyMethod' in 'myUser' must be destroy method", m.isOfType(SeamComponentMethodType.DESTROY));

		// f) @Factory
		ISeamFactory myFactory = find(fs, "myFactory");
		assertTrue("Declared factory 'myFactory' is not found in 'myUser'", myFactory != null);
		ISeamAnnotatedFactory af = (ISeamAnnotatedFactory)myFactory;
		assertTrue("Scope of 'myFactory' must be 'session'", af.getScope() == ScopeType.SESSION);

		// g) @Factory getMyFactory2 - no name is set in annotation
		ISeamFactory myFactory2 = find(fs, "myFactory");
		assertTrue("Declared factory 'myFactory2' is not found in 'myUser'", myFactory2 != null);
		ISeamAnnotatedFactory af2 = (ISeamAnnotatedFactory)myFactory2;
		assertTrue("Scope of 'myFactory' must be 'session'", af2.getScope() == ScopeType.SESSION);

	}
	
	private IBijectedAttribute findBijectedAttribute(Set<IBijectedAttribute> bijected, String name) {
		for (IBijectedAttribute a : bijected) {
			if(name.equals(a.getName())) return a;
		}
		return null;
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

		ISeamFactory[] factories = null;
		ISeamJavaComponentDeclaration[] componentDeclarations = null;
		
		try {
			LoadedDeclarations ds = scanner.parse(f, seamProject);
			factories = ds.getFactories().toArray(new ISeamFactory[0]);
			componentDeclarations = ds.getComponents().toArray(new ISeamJavaComponentDeclaration[0]);
		} catch (Exception e) {
			JUnitUtils.fail("Error in library scanner",e);
		}
		assertTrue("Factories are not found in jboss-seam.jar", factories != null && factories.length > 0);
		
		// Test factory 'actor'
		ISeamXmlFactory actor = (ISeamXmlFactory)find(factories, "actor");
		assertTrue("Factory " + "actor" + " is not found in jboss-seam.jar", actor != null);
		assertTrue("Factory " + "actor" + " loaded wrong value", "#{org.jboss.seam.core.actor}".equals(actor.getValue()));
		
		try {
			project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
			//EditorTestHelper.joinBackgroundActivities();
		} catch (Exception e) {
			JUnitUtils.fail("Cannot build",e);
		}

		//After having tested details of library scanner now let us check
		//that it succeeded in build.
		Set<ISeamFactory> components = seamProject.getFactoriesByName("actor");
	
		assertTrue("Seam builder must put actor to project.", components.size()==1);
		
		//Test components
		
		//1. Test component declaration org.jboss.seam.core.dispatcher
		
		ISeamJavaComponentDeclaration d = (ISeamJavaComponentDeclaration)findDeclaration(componentDeclarations, "org.jboss.seam.core.dispatcher");
		assertTrue("Java declaration 'org.jboss.seam.core.dispatcher' is not found", d != null);
		assertTrue("Java declaration 'org.jboss.seam.core.dispatcher' must be stateless", d.isOfType(BeanType.STATELESS));
		assertTrue("Java declaration 'org.jboss.seam.core.dispatcher' must have precedence 0", d.getPrecedence() == 0);
		
		//2. Test component declaration org.jboss.seam.core.ejb
		
		d = (ISeamJavaComponentDeclaration)findDeclaration(componentDeclarations, "org.jboss.seam.core.ejb");
		assertTrue("Java declaration 'org.jboss.seam.core.ejb' is not found", d != null);
		assertTrue("Java declaration 'org.jboss.seam.core.dispatcher' must have precedence 0", d.getPrecedence() == 0);
		Set<ISeamComponentMethod> methods = d.getMethods();
		ISeamComponentMethod m = find(methods, "startup");
		assertTrue("Declared method 'startup' is not found in 'org.jboss.seam.core.ejb'", m != null);
		assertTrue("Method 'startup' in 'org.jboss.seam.core.ejb' must be create method", m.isOfType(SeamComponentMethodType.CREATE));
		m = find(methods, "shutdown");
		assertTrue("Declared method 'shutdown' is not found in 'org.jboss.seam.core.ejb'", m != null);
		assertTrue("Method 'shutdown' in 'org.jboss.seam.core.ejb' must be destroy method", m.isOfType(SeamComponentMethodType.DESTROY));
		
		//3. Test component declaration org.jboss.seam.core.eventContext
		
		d = (ISeamJavaComponentDeclaration)findDeclaration(componentDeclarations, "org.jboss.seam.core.eventContext");
		assertTrue("Java declaration 'org.jboss.seam.core.eventContext' is not found", d != null);
		methods = d.getMethods();
		m = find(methods, "getContext");
		assertTrue("Declared method 'getContext' is not found in 'org.jboss.seam.core.eventContext'", m != null);
		assertTrue("Method 'getContext' in 'org.jboss.seam.core.eventContext' must be unwrap method", m.isOfType(SeamComponentMethodType.UNWRAP));
		
		
	}
	
	private ISeamComponentMethod find(Set<ISeamComponentMethod> methods, String name) {
		for (ISeamComponentMethod m : methods) {
			if(name.equals(m.getSourceMember().getElementName())) return m;
		}
		return null;
	}
	
	private ISeamFactory find(ISeamFactory[] factories, String name) {
		for (int i = 0; i < factories.length; i++) {
			if(name.equals(factories[i].getName())) return factories[i];
		}
		return null;
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
		
		//5. components.xml has <component class="demo.User"/> entry
		// check that
		// component myUser has both java and xml declaration, the latter without name
		
		ISeamComponent myUser = seamProject.getComponent("myUser");
		assertNotNull(myUser);
		Set<ISeamXmlComponentDeclaration> xml = myUser.getXmlDeclarations();
		assertEquals(1, xml.size());
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
	
	public void testInnerClass_JBIDE_1374() {
		ISeamProject seamProject = getSeamProject();
		ISeamComponent c = seamProject.getComponent("inner_JBIDE_1374");
		assertTrue("Component inner_JBIDE_1374 declared in inner static class is not found.", c != null);
	}
	
	public void testInnerClassInAnnotationType_JBIDE_4144() {
		ISeamProject seamProject = getSeamProject();
		ISeamComponent c = seamProject.getComponent("inner_JBIDE_4144");
		assertTrue("Component inner_JBIDE_4144 declared in inner static class inside an annotation type is not found.", c != null);
	}
	
	public void testInstallWithoutPrecedence_JBIDE_2052() {
		ISeamProject seamProject = getSeamProject();
		ISeamComponent c = seamProject.getComponent("installWithoutPrecedence_JBIDE_2052");
		// actually, exception may happen in building Seam project
		assertNotNull("Component installWithoutPrecedence_JBIDE_2052 declared in class annotated with @Install(false) is not found.", c);
		
	}
	
	public void testLocation_JBIDE_2080() {
		String EJB = "org.jboss.seam.core.ejb";
		ISeamProject seamProject = getSeamProject();
		ISeamComponent c = seamProject.getComponent(EJB);
		assertNotNull("Component " + EJB + " is not found.", c);
		Set<ISeamComponentDeclaration> ds = c.getAllDeclarations();
		ISeamXmlComponentDeclaration xml = null;
		for (ISeamComponentDeclaration d: ds) {
			if(d instanceof ISeamXmlComponentDeclaration) {
				xml = (ISeamXmlComponentDeclaration)d;
				break;
			}
		}
		
		String MY_COMPONENT = "myComponent";
		assertNotNull("XML declaration for component " + MY_COMPONENT + " is not found in components.xml.", xml);
		ITextSourceReference location = xml.getLocationFor(ISeamXmlComponentDeclaration.NAME);
		assertNotNull("Location of declaration of component " + MY_COMPONENT + " in components.xml is not found.", location);
		assertTrue("Location should not point to 0", location.getStartPosition() > 0 && location.getLength() > 0);
	}

	@Override
	protected void tearDown() throws Exception {
		provider.dispose();
	}
}
