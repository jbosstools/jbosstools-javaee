package org.jboss.tools.cdi.seam.config.core.test.v30;

import java.util.Set;

import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IProducerField;
import org.jboss.tools.cdi.core.IProducerMethod;
import org.jboss.tools.cdi.core.test.DependentProjectTest;

public class ConfigBeansInjectionTest extends SeamConfigTest {
	static String INJECTIONS_CLASS_PATH = "src/org/jboss/beans/injection/Injections.java";
	static String INJECTIONS2_CLASS_PATH = "src/org/jboss/beans/injection/Injections2.java";

	public void testClassBeanInjection() {
		IInjectionPoint p = DependentProjectTest.getInjectionPointField(cdiProject, INJECTIONS_CLASS_PATH, "b5");
		Set<IBean> bs = cdiProject.getBeans(false, p);
		assertEquals(1, bs.size());
		IBean b = bs.iterator().next();
		assertTrue(b instanceof IClassBean);

		//The same in dependent project
		p = DependentProjectTest.getInjectionPointField(cdiDependentProject, INJECTIONS2_CLASS_PATH, "b5");
		bs = cdiDependentProject.getBeans(false, p);
		assertEquals(1, bs.size());
	}

	public void testVirtualFieldProducer() {
		IInjectionPoint p = DependentProjectTest.getInjectionPointField(cdiProject, INJECTIONS_CLASS_PATH, "s");
		Set<IBean> bs = cdiProject.getBeans(false, p);
		assertEquals(1, bs.size());
		IBean b = bs.iterator().next();
		assertTrue(b instanceof IClassBean);

		//The same in dependent project
		p = DependentProjectTest.getInjectionPointField(cdiDependentProject, INJECTIONS2_CLASS_PATH, "s");
		bs = cdiDependentProject.getBeans(false, p);
		assertEquals(1, bs.size());
	}

	public void testMethodProducer() {
		IInjectionPoint p = DependentProjectTest.getInjectionPointField(cdiProject, INJECTIONS_CLASS_PATH, "t1");
		Set<IBean> bs = cdiProject.getBeans(false, p);
		assertEquals(1, bs.size());
		IBean b = bs.iterator().next();
		assertTrue(b instanceof IProducerMethod);

		//The same in dependent project
		p = DependentProjectTest.getInjectionPointField(cdiDependentProject, INJECTIONS2_CLASS_PATH, "t1");
		bs = cdiDependentProject.getBeans(false, p);
		assertEquals(1, bs.size());
	}

	public void testFieldProducer() {
		IInjectionPoint p = DependentProjectTest.getInjectionPointField(cdiProject, INJECTIONS_CLASS_PATH, "t3");
		Set<IBean> bs = cdiProject.getBeans(false, p);
		assertEquals(1, bs.size());
		IBean b = bs.iterator().next();
		assertTrue(b instanceof IProducerField);

		//The same in dependent project
		p = DependentProjectTest.getInjectionPointField(cdiDependentProject, INJECTIONS2_CLASS_PATH, "t3");
		bs = cdiDependentProject.getBeans(false, p);
		assertEquals(1, bs.size());
	}
}
