package org.jboss.tools.cdi.core.test;

import java.io.IOException;
import java.util.Iterator;
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
import org.jboss.tools.cdi.core.IProducer;
import org.jboss.tools.cdi.core.IProducerMethod;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.core.IScope;
import org.jboss.tools.cdi.internal.core.impl.ProducerMethod;
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
		Set<CDICoreNature> dependsOn = cdi2.getCDIProjects();
		Set<CDICoreNature> usedBy = cdi2.getDependentProjects();
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
		assertNull(ns);
		assertNotNull(sd);
	}

	private IProducer getProducer(String file) {
		ICDIProject cdi2 = CDICorePlugin.getCDIProject(project2, true);
		Set<IBean> beans = cdi2.getBeans(new Path("/CDITest2/src/test/Test1.java"));
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
		Set<IBean> bs = cdi2.getBeans(true, f);
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
		Set<IBean> bs = cdi2.getBeans(true, f);
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
		Set<IBean> bs = cdi2.getBeans(true, f);
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
		Set<IBean> bs = cdi2.getBeans(true, f);
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
		Set<IBean> bs = cdi2.getBeans(true, f);
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
		Set<IBean> bs = cdi2.getBeans(true, f);
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
		Set<IBean> bs = cdi2.getBeans(true, f);
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
		Set<IBean> bs = cdi2.getBeans(true, f);
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
		Set<IBean> bs = cdi2.getBeans(true, f);
		assertEquals(1, bs.size());
		IBean b = bs.iterator().next();
		assertTrue(b instanceof IProducerMethod);
	}

	public void testIndirectDependency() throws CoreException, IOException {
		ICDIProject cdi3 = CDICorePlugin.getCDIProject(project3, true);
		Set<IBean> beans = cdi3.getBeans(new Path("/CDITest1/src/cdi/test/MyBean.java"));
		assertFalse(beans.isEmpty());
		IQualifier q = cdi3.getQualifier("cdi.test.MyQualifier");
		assertNotNull(q);		
	}

	public void testInjectionOfTypeRepeatedInJarCopies() throws CoreException {
		ICDIProject cdi2 = CDICorePlugin.getCDIProject(project2, true);
		Set<IBean> bs = cdi2.getBeans(new Path("/CDITest2/src/test/MyExampleInjection.java"));
		assertEquals(1, bs.size());
		IBean b = bs.iterator().next();
		Set<IInjectionPoint> ps = b.getInjectionPoints();
		assertEquals(1, ps.size());
		IInjectionPoint p = ps.iterator().next();
		Set<IBean> injected = cdi2.getBeans(false, p);
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

	public void testCleanDependentProject() throws CoreException, IOException {
		ICDIProject cdi2 = CDICorePlugin.getCDIProject(project2, true);
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);

		cdi2.getNature().getProject().build(IncrementalProjectBuilder.CLEAN_BUILD, new NullProgressMonitor());
		assertBeanIsPresent(cdi2, "cdi.test.MyBean", true);

		ResourcesUtils.setBuildAutomatically(true);
		assertBeanIsPresent(cdi2, "cdi.test.MyBean", true);

		ResourcesUtils.setBuildAutomatically(saveAutoBuild);
	}

	public static IInjectionPointField getInjectionPointField(ICDIProject cdi, String beanClassFilePath, String fieldName) {
		IFile file = cdi.getNature().getProject().getFile(beanClassFilePath);
		Set<IBean> beans = cdi.getBeans(file.getFullPath());
		Iterator<IBean> it = beans.iterator();
		while(it.hasNext()) {
			IBean b = it.next();
			if(b instanceof IProducer) it.remove();
		}
		assertEquals("Wrong number of the beans", 1, beans.size());
		Set<IInjectionPoint> injections = beans.iterator().next().getInjectionPoints();
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
}