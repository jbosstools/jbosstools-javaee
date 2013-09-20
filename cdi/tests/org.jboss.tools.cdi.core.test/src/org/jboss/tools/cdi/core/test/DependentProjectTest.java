/*******************************************************************************
 * Copyright (c) 2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.core.test;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.CDIUtil;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.core.IObserverMethod;
import org.jboss.tools.cdi.core.IProducer;
import org.jboss.tools.cdi.core.IProducerMethod;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.core.IScope;
import org.jboss.tools.cdi.internal.core.impl.ProducerMethod;
import org.jboss.tools.cdi.internal.core.impl.definition.DefinitionContext;
import org.jboss.tools.common.java.IAnnotationDeclaration;
import org.jboss.tools.jst.web.kb.IKbProject;
import org.jboss.tools.jst.web.kb.KbProjectFactory;
import org.jboss.tools.jst.web.kb.internal.KbProject;
import org.jboss.tools.test.util.ResourcesUtils;

/**
 *   
 * @author V.Kabanovich
 *
 */
public class DependentProjectTest extends TestCase {
	protected static String PLUGIN_ID = "org.jboss.tools.cdi.core.test";
	IProject project1 = null;
	IProject project2 = null;
	IProject project3 = null;

	@Override
	protected void setUp() throws Exception {
		project1 = ResourcesPlugin.getWorkspace().getRoot().getProject("CDITest1");
		project2 = ResourcesPlugin.getWorkspace().getRoot().getProject("CDITest2");
		project3 = ResourcesPlugin.getWorkspace().getRoot().getProject("CDITest3");
	}

	public void testProjectDependencyLoading() throws CoreException, IOException {
		IKbProject kb2 = KbProjectFactory.getKbProject(project2, true);
		((KbProject)kb2).store();
		CDICoreNature cdi2 = CDICorePlugin.getCDI(project2, true);
		Collection<CDICoreNature> dependsOn = cdi2.getCDIProjects();
		Collection<CDICoreNature> usedBy = cdi2.getDependentProjects();
		assertEquals(1, dependsOn.size());
		assertEquals(1, usedBy.size());
		cdi2.reloadProjectDependencies();
		dependsOn = cdi2.getCDIProjects();
		usedBy = cdi2.getDependentProjects();
		assertEquals(1, dependsOn.size());
		assertEquals(1, usedBy.size());
	}

	public void testDependentProject() throws CoreException, IOException {
		ICDIProject cdi2 = CDICorePlugin.getCDIProject(project2, true);
		assertBeanIsPresent(cdi2, "cdi.test.MyBean", true);
	}

	void assertBeanIsPresent(ICDIProject cdi2, String beanClass, boolean present) {
		IBean[] beans = cdi2.getBeans();
		IClassBean cb = null;
		for (IBean b: beans) {
			if(b instanceof IClassBean) {
				IClassBean cb1 = (IClassBean)b;
				if(beanClass.equals(cb1.getBeanClass().getFullyQualifiedName())) {
					cb = cb1;
				}
			}
		}
		if(present) {
			assertNotNull(cb);
		} else {
			assertNull(cb);
		}
	}

	public void testScopeFromParentProject() throws CoreException, IOException {
		IProducer producer = getProducer("/CDITest2/src/test/Test1.java");

		IScope scope = producer.getScope();
		IAnnotationDeclaration ns = scope.getAnnotationDeclaration(CDIConstants.NORMAL_SCOPE_ANNOTATION_TYPE_NAME);
		IAnnotationDeclaration sd = scope.getAnnotationDeclaration(CDIConstants.SCOPE_ANNOTATION_TYPE_NAME);
		assertNotNull(ns);
		assertNull(sd);

		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		IFile scope2File = project1.getFile(new Path("src/cdi/test/Scope2.java"));
		IFile scope21File = project1.getFile(new Path("src/cdi/test/Scope2.1"));
		scope2File.setContents(scope21File.getContents(), IFile.FORCE, new NullProgressMonitor());
		project1.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
		ResourcesUtils.setBuildAutomatically(saveAutoBuild);

		producer = getProducer("/CDITest2/src/test/Test1.java");
		scope = producer.getScope();
		ns = scope.getAnnotationDeclaration(CDIConstants.NORMAL_SCOPE_ANNOTATION_TYPE_NAME);
		sd = scope.getAnnotationDeclaration(CDIConstants.SCOPE_ANNOTATION_TYPE_NAME);
		
		ICDIProject cdi2 = CDICorePlugin.getCDIProject(project2, true);
		assertNull(ns);
		assertNotNull(sd);
	}

	private IProducer getProducer(String file) {
		ICDIProject cdi2 = CDICorePlugin.getCDIProject(project2, true);
		Collection<IBean> beans = cdi2.getBeans(new Path("/CDITest2/src/test/Test1.java"));
		IProducer producer = null;
		for (IBean b: beans) {
			if(b instanceof IProducer) {
				producer = (IProducer)b;
				break;
			}
		}
		assertNotNull(producer);
		return producer;
	}

	/*
	 * Case 1.
	 * Bean A is defined in CDITest1 project.
	 * Alternative bean B is defined in CDITest1 project. It is not selected.
	 * Alternative bean C is defined in CDITest2 project. It is not selected.
	 * 
	 * ASSERT: Injection resolved to bean A.
	 */
	public void testAlternativesInDependentProjects() throws CoreException {
		ICDIProject cdi2 = CDICorePlugin.getCDIProject(project2, true);
		IInjectionPointField f = getInjectionPointField(cdi2, "/src/cdi/test/alternative/case1/X.java", "a");
		Collection<IBean> bs = cdi2.getBeans(true, f);
		assertEquals(1, bs.size());
		assertEquals("A", bs.iterator().next().getBeanClass().getElementName());
	}

	/*
	 * Case 2.
	 * Bean A is defined in CDITest1 project.
	 * Alternative bean B is defined in CDITest1 project. It is not selected.
	 * Alternative bean C is defined in CDITest2 project. It is selected in CDITest2.
	 * 
	 * ASSERT: Injection resolved to bean C.
	 */
	public void testAlternativesInDependentProjects2() throws CoreException {
		ICDIProject cdi2 = CDICorePlugin.getCDIProject(project2, true);
		IInjectionPoint f = getInjectionPointField(cdi2, "/src/cdi/test/alternative/case2/X.java", "a");
		Collection<IBean> bs = cdi2.getBeans(true, f);
		assertEquals(1, bs.size());
		assertEquals("C", bs.iterator().next().getBeanClass().getElementName());
	}

	/*
	 * Case 3.
	 * Bean A is defined in CDITest1 project.
	 * Alternative bean B is defined in CDITest1 project. It is selected in CDITest2.
	 * Alternative bean C is defined in CDITest2 project. It is not selected.
	 * 
	 * ASSERT: Injection resolved to bean B.
	 */
	public void testAlternativesInDependentProjects3() throws CoreException {
		ICDIProject cdi2 = CDICorePlugin.getCDIProject(project2, true);
		IInjectionPoint f = getInjectionPointField(cdi2, "/src/cdi/test/alternative/case3/X.java", "a");
		Collection<IBean> bs = cdi2.getBeans(true, f);
		assertEquals(1, bs.size());
		assertEquals("B", bs.iterator().next().getBeanClass().getElementName());
	}

	/*
	 * Case 4-1.
	 * Bean A is defined in CDITest1 project.
	 * Alternative bean B is defined in CDITest1 project. It is selected in CDITest1.
	 * Alternative bean C is defined in CDITest2 project. It is not selected.
	 * 
	 * ASSERT: Injection resolved to bean A.
	 */
	public void testAlternativesInDependentProjects4_1() throws CoreException {
		ICDIProject cdi2 = CDICorePlugin.getCDIProject(project2, true);
		IInjectionPoint f = getInjectionPointField(cdi2, "/src/cdi/test/alternative/case4/X.java", "a");
		Collection<IBean> bs = cdi2.getBeans(true, f);
		assertEquals(1, bs.size());
		assertEquals("A", bs.iterator().next().getBeanClass().getElementName());
	}

	/*
	 * Case 4-2.
	 * Bean A is defined in CDITest1 project.
	 * Alternative bean B is defined in CDITest1 project. It is selected in CDITest1.
	 * Alternative bean C is defined in CDITest2 project. It is not selected.
	 * Bean Y is defined in CDITest1 but it is accessed through project CDITest2
	 * 
	 * ASSERT: Injection resolved to bean B.
	 */
	public void testAlternativesInDependentProjects4_2() throws CoreException {
		ICDIProject cdi1 = CDICorePlugin.getCDIProject(project1, true);
		ICDIProject cdi2 = CDICorePlugin.getCDIProject(project2, true);
		IInjectionPoint f = getInjectionPointField(cdi1, "/src/cdi/test/alternative/case4/Y.java", "b");
		Collection<IBean> bs = cdi2.getBeans(true, f);
		assertEquals(1, bs.size());
		assertEquals("B", bs.iterator().next().getBeanClass().getElementName());
	}

	/*
	 * Case 5.
	 * Bean A is defined in CDITest1 project.
	 * Alternative bean B is defined in CDITest1 project. It is selected in CDITest1.
	 * Alternative bean C is defined in CDITest2 project. It is selected in CDITest2.
	 * 
	 * ASSERT: Injection resolved to bean C.
	 */
	public void testAlternativesInDependentProjects5() throws CoreException {
		ICDIProject cdi2 = CDICorePlugin.getCDIProject(project2, true);
		IInjectionPoint f = getInjectionPointField(cdi2, "/src/cdi/test/alternative/case5/X.java", "a");
		Collection<IBean> bs = cdi2.getBeans(true, f);
		assertEquals(1, bs.size());
		assertEquals("C", bs.iterator().next().getBeanClass().getElementName());
	}

	/*
	 * Case 6.
	 * Bean A is defined in CDITest1 project.
	 * Alternative bean B is defined in CDITest1 project. It is selected in CDITest2.
	 * Alternative bean C is defined in CDITest2 project. It is selected in CDITest2.
	 * 
	 * ASSERT: Multiple beans: injection resolved to beans B and C.
	 */
	public void testAlternativesInDependentProjects6() throws CoreException {
		ICDIProject cdi2 = CDICorePlugin.getCDIProject(project2, true);
		IInjectionPoint f = getInjectionPointField(cdi2, "/src/cdi/test/alternative/case6/X.java", "a");
		Collection<IBean> bs = cdi2.getBeans(true, f);
		assertEquals(2, bs.size());
	}

	/*
	 * Case 7.
	 * Bean A is defined in CDITest1 project.
	 * Alternative bean B is defined in CDITest1 project. It is not selected.
	 * Producer bean P is declared in B.p().
	 * 
	 * ASSERT: No eligible bean.
	 */
	public void testAlternativesInDependentProjects7() throws CoreException {
		ICDIProject cdi2 = CDICorePlugin.getCDIProject(project2, true);
		IInjectionPoint f = getInjectionPointField(cdi2, "/src/cdi/test/alternative/case7/X.java", "p");
		Collection<IBean> bs = cdi2.getBeans(true, f);
		assertTrue(bs.isEmpty());
	}

	/*
	 * Case 8.
	 * Bean A is defined in CDITest1 project.
	 * Alternative bean B is defined in CDITest1 project. It is selected in CDITest1.
	 * Producer bean P is declared in B.p().
	 * 
	 * ASSERT: Injection resolved to bean B.p().
	 */
	public void testAlternativesInDependentProjects8() throws CoreException {
		ICDIProject cdi2 = CDICorePlugin.getCDIProject(project2, true);
		IInjectionPoint f = getInjectionPointField(cdi2, "/src/cdi/test/alternative/case8/X.java", "p");
		Collection<IBean> bs = cdi2.getBeans(true, f);
		assertEquals(1, bs.size());
		IBean b = bs.iterator().next();
		assertTrue(b instanceof IProducerMethod);
	}

	public void testIndirectDependency() throws CoreException, IOException {
		ICDIProject cdi3 = CDICorePlugin.getCDIProject(project3, true);
		Collection<IBean> beans = cdi3.getBeans(new Path("/CDITest1/src/cdi/test/MyBean.java"));
		assertFalse(beans.isEmpty());
		IQualifier q = cdi3.getQualifier("cdi.test.MyQualifier");
		assertNotNull(q);		
	}

	public void testInjectionOfTypeRepeatedInJarCopies() throws CoreException {
		ICDIProject cdi2 = CDICorePlugin.getCDIProject(project2, true);
		Collection<IBean> bs = cdi2.getBeans(new Path("/CDITest2/src/test/MyExampleInjection.java"));
		assertEquals(1, bs.size());
		IBean b = bs.iterator().next();
		Collection<IInjectionPoint> ps = b.getInjectionPoints();
		assertEquals(1, ps.size());
		IInjectionPoint p = ps.iterator().next();
		Collection<IBean> injected = cdi2.getBeans(false, p);
		assertEquals(1, injected.size());
		IBean i = injected.iterator().next();
		assertTrue(i instanceof IProducerMethod);
		IProducerMethod f = (IProducerMethod)i;
		IType producerType = ((ProducerMethod)f).getType().getType();
		IType injectionType = ((IInjectionPointField)p).getMemberType().getType();
		assertEquals("org.jboss.cdi.test.example.Example", producerType.getFullyQualifiedName());
		assertEquals("org.jboss.cdi.test.example.Example", injectionType.getFullyQualifiedName());
		assertFalse(producerType.equals(injectionType));
	}

	public void testSwitchingCDICapabilities() throws CoreException {
		CDICoreNature n1 = CDICorePlugin.getCDI(project1, true);
		CDICoreNature n2 = CDICorePlugin.getCDI(project2, true);
		CDICoreNature n3 = CDICorePlugin.getCDI(project3, true);
		assertTrue(n1.getDependentProjects().contains(n2));
		assertTrue(n3.getCDIProjects().contains(n2));

		CDIUtil.disableCDI(project2);
		project2.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
		assertTrue(n1.getDependentProjects().isEmpty());
		assertTrue(n3.getCDIProjects().isEmpty());

		CDIUtil.enableCDI(project2, false, new NullProgressMonitor());
		project2.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
		n2 = CDICorePlugin.getCDI(project2, true);
		assertTrue(n1.getDependentProjects().contains(n2));
		assertTrue(n3.getCDIProjects().contains(n2));
	}

	/**
	 * A dependent project creates its own bean objects for type definitions obtained from used projects.
	 * These separate instances of the same bean should be actual after changes to bean type.
	 * This test accesses an injection point in a bean through two different projects, 
	 * one project (project2) declares that bean type and the other project (project3) depends on it.
	 * Bean type is slightly modified - so that field type is resolved to Java type with the same 
	 * element name, but in another package. 
	 * 
	 * Test checks that there is no 'sticking' of out-of-date type in bean instance 
	 * of the dependent project.
	 *  
	 * @throws CoreException
	 */
	public void testResolvingInjections() throws CoreException {
		ICDIProject cdi2 = CDICorePlugin.getCDIProject(project2, true);
		ICDIProject cdi3 = CDICorePlugin.getCDIProject(project3, true);
	
		IInjectionPoint point2 = getInjectionPointField(cdi2, "/src/test/BeanI.java", "i");
		assertNotNull(point2);
		Collection<IBean> bs2 = cdi2.getBeans(false, point2);
		assertEquals(1, bs2.size());
		
		IInjectionPoint point3 = getInjectionPointField(cdi3, project2, "/src/test/BeanI.java", "i");
		assertNotNull(point3);
		Collection<IBean> bs3 = cdi3.getBeans(false, point3);
		assertEquals(1, bs3.size());
		
		RemoveJarFromClasspathTest.replaceFile(project2, "/src/test/BeanI.changed", "/src/test/BeanI.java");

		point2 = getInjectionPointField(cdi2, "/src/test/BeanI.java", "i");
		assertNotNull(point2);
		bs2 = cdi2.getBeans(false, point2);
		assertEquals(0, bs2.size());

		point3 = getInjectionPointField(cdi3, project2, "/src/test/BeanI.java", "i");
		assertNotNull(point3);
		bs3 = cdi3.getBeans(false, point3);
		assertEquals(0, bs3.size());

		RemoveJarFromClasspathTest.replaceFile(project2, "/src/test/BeanI.original", "/src/test/BeanI.java");

		point2 = getInjectionPointField(cdi2, "/src/test/BeanI.java", "i");
		assertNotNull(point2);
		bs2 = cdi2.getBeans(false, point2);
		assertEquals(1, bs2.size());

		point3 = getInjectionPointField(cdi3, project2, "/src/test/BeanI.java", "i");
		assertNotNull(point3);
		bs3 = cdi3.getBeans(false, point3);
		assertEquals(1, bs3.size());
	}

	public void testFindObservedEvents() throws CoreException {
		ICDIProject cdi1 = CDICorePlugin.getCDIProject(project1, true);
		Collection<IBean> bs = cdi1.getBeans(new Path("/CDITest1/src/cdi/test/observers/CDIBeanTest.java"));
		assertFalse(bs.isEmpty());
		IBean b = bs.iterator().next();
		assertTrue(b instanceof IClassBean);
		IClassBean cb = (IClassBean)b;
		Collection<IObserverMethod> ms = cb.getObserverMethods();
		assertEquals(1, ms.size());
		IObserverMethod m = ms.iterator().next();
		Collection<IInjectionPoint> ps = cdi1.findObservedEvents(m.getObservedParameters().iterator().next());
		assertEquals(1, ps.size());
		IInjectionPoint p = ps.iterator().next();
		assertTrue(p.getDeclaringProject() == CDICorePlugin.getCDIProject(project2, true));
	}

	/**
	 * This test checks that method resolveObserverMethods does not fail with exception (see JBIDE-9951)
	 */
	public void testNonrelevantInjectionPointAtResolvingObserverMethods() {
		ICDIProject cdi1 = CDICorePlugin.getCDIProject(project1, true);
		IInjectionPointField tamingEvent =  getInjectionPointField(cdi1, "/src/cdi/test/observers/CDIBeanTest.java", "point");
		Collection<IObserverMethod> observers = tamingEvent.getCDIProject().resolveObserverMethods(tamingEvent);
		assertTrue(observers.isEmpty());
	}

	public void testCleanDependentProject() throws CoreException, IOException {
		ICDIProject cdi2 = CDICorePlugin.getCDIProject(project2, true);
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);

		cdi2.getNature().getProject().build(IncrementalProjectBuilder.CLEAN_BUILD, new NullProgressMonitor());
		assertBeanIsPresent(cdi2, "cdi.test.MyBean", true);

		ResourcesUtils.setBuildAutomatically(true);
		assertBeanIsPresent(cdi2, "cdi.test.MyBean", true);

		ResourcesUtils.setBuildAutomatically(false);
		cdi2.getNature().getProject().build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());

		ResourcesUtils.setBuildAutomatically(saveAutoBuild);
	}
	public static IInjectionPointField getInjectionPointField(ICDIProject cdi, String beanClassFilePath, String fieldName) {
		return getInjectionPointField(cdi, cdi.getNature().getProject(), beanClassFilePath, fieldName);
	}

	public static IInjectionPointField getInjectionPointField(ICDIProject cdi, IProject project, String beanClassFilePath, String fieldName) {
		IFile file = project.getFile(beanClassFilePath);
		Collection<IBean> beans = cdi.getBeans(file.getFullPath());
		Iterator<IBean> it = beans.iterator();
		while(it.hasNext()) {
			IBean b = it.next();
			if(b instanceof IProducer) it.remove();
		}
		assertEquals("Wrong number of the beans", 1, beans.size());
		Collection<IInjectionPoint> injections = beans.iterator().next().getInjectionPoints();
		for (IInjectionPoint injectionPoint : injections) {
			if(injectionPoint instanceof IInjectionPointField) {
				IInjectionPointField field = (IInjectionPointField)injectionPoint;
				if(fieldName.equals(field.getField().getElementName())) {
					return field;
				}
			}
		}
		fail("Can't find \"" + fieldName + "\" injection point filed in " + beanClassFilePath);
		return null;
	}

	private CDICoreNature[] createArray(int length) {
		//create projects
		CDICoreNature[] natures = new CDICoreNature[length];
		for (int i = 0; i < natures.length; i++) {
			natures[i] = new CDICoreNature();
		}
		//shuffle to exclude influence of the order in which objects are created.
		shuffle(natures);
		return natures;
	}

	Random seed = new Random();

	private void shuffle(Object[] os) {
		for (int i = 0; i < os.length; i++) {
			int j = seed.nextInt(os.length - i) + i;
			Object n = os[i];
			os[i] = os[j];
			os[j] = n;
		}
	}

	public void testOrderedListOfDependencies() {
		int numberOfProjects = 5000;
		int beginOfLoop = 1000;
		int endOfLoop = 1010;

		//create projects
		CDICoreNature[] natures = createArray(numberOfProjects);

		//Add dependencies
		for (int k = 1; k < 15; k++) {
			for (int i = 0; i < natures.length - k; i++) {
				natures[i].addCDIProject(natures[i + k]);
			}
		}
		//Add a looping dependency
		natures[endOfLoop].addCDIProject(natures[beginOfLoop]);

		long t = System.currentTimeMillis();
		Set<CDICoreNature> set = natures[0].getCDIProjects(true);
		List<CDICoreNature> list = DefinitionContext.toListOrderedByDependencies(set);
		long dt = System.currentTimeMillis() - t;
		System.out.println("Ordered List Of Looped Dependencies of " + numberOfProjects + " projects in " + dt + "ms.");

		assertEquals(-1, list.indexOf(natures[0]));
		for (int i = 1; i < natures.length; i++) {
			int index = list.indexOf(natures[i]);
			if(i < beginOfLoop || i > endOfLoop) {
				assertEquals(natures.length - 1, index + i);
			}
		}
	}

	public void testOrderedListOfDependenciesWithFactorialTree() {
		int numberOfProjects = 3000;
		//create projects
		CDICoreNature[] natures = createArray(numberOfProjects);

		//Add dependencies
		for (int i = 0; i < natures.length - 1; i++) {
			for (int j = i + 1; j < natures.length; j++) {
				natures[i].addCDIProject(natures[j]);
			}
		}

		long t = System.currentTimeMillis();
		Set<CDICoreNature> set = natures[0].getCDIProjects(true);
		List<CDICoreNature> list = DefinitionContext.toListOrderedByDependencies(set);
		long dt = System.currentTimeMillis() - t;
		System.out.println("Ordered List Of Factorial Dependencies of " + numberOfProjects + " projects in " + dt + "ms.");

		assertEquals(-1, list.indexOf(natures[0]));
		checkOrder(natures, list);
	}

	void checkOrder(CDICoreNature[] natures, List<CDICoreNature> list) {
		for (int i = 1; i < natures.length; i++) {
			int index = list.indexOf(natures[i]);
			for (CDICoreNature n: natures[i].getCDIProjects()) {
				int index1 = list.indexOf(n);
				assertTrue(index1 < index);				
			}
		}
	}

	public void testOrderedListOfDependenciesWithModerateTree() {
		Random seed = new Random();
		int[] levels = new int[]{0,1,3,7,15,31,63,127,200,300,400,500,600,700,800,900,950,960,970,980,990,1000,1010,1020,1030};
		int numberOfProjects = levels[levels.length - 1];
		CDICoreNature[] natures = createArray(numberOfProjects);
		
		for (int i = 0; i < levels.length - 2; i++) {
			for (int p1 = levels[i]; p1 < levels[i + 1]; p1++) {
				int p2 = levels[i + 1] + seed.nextInt(levels[i + 2] - levels[i + 1]);
				natures[p1].addCDIProject(natures[p2]);
			}
			for (int p2 = levels[i + 1]; p2 < levels[i + 2]; p2++) {
				int p1 = levels[i] + seed.nextInt(levels[i + 1] - levels[i]);
				natures[p1].addCDIProject(natures[p2]);
			}
			
		}

		long t = System.currentTimeMillis();
		Set<CDICoreNature> set = natures[0].getCDIProjects(true);
		List<CDICoreNature> list = DefinitionContext.toListOrderedByDependencies(set);
		long dt = System.currentTimeMillis() - t;
		System.out.println("Ordered List Of Moderate Tree Dependencies of " + numberOfProjects + " projects in " + dt + "ms.");

		assertEquals(-1, list.indexOf(natures[0]));
		checkOrder(natures, list);
	}

}
