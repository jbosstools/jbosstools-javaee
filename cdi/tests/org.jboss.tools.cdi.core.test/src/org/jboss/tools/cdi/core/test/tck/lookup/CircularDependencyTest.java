/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.core.test.tck.lookup;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IInjectionPointParameter;
import org.jboss.tools.cdi.core.IProducerMethod;
import org.jboss.tools.cdi.core.test.tck.TCKTest;

/**
 * @author Alexey Kazakov
 */
public class CircularDependencyTest extends TCKTest {

	/**
	 * section 5 b) 
	 * @throws CoreException
	 */
	public void testCircularInjectionOnTwoNormalBeans() throws CoreException {
		assertOnlyInjectionOfClassBeanReslovedToAnotherClassBean("org.jboss.jsr299.tck.tests.lookup.circular.Pig", "org.jboss.jsr299.tck.tests.lookup.circular.Food");
		assertOnlyInjectionOfClassBeanReslovedToAnotherClassBean("org.jboss.jsr299.tck.tests.lookup.circular.Food", "org.jboss.jsr299.tck.tests.lookup.circular.Pig");
	}

	/**
	 * section 5 b) 
	 * @throws CoreException
	 */
	public void testCircularInjectionOnOneNormalAndOneDependentBean() throws CoreException {
		assertOnlyInjectionOfClassBeanReslovedToAnotherClassBean("org.jboss.jsr299.tck.tests.lookup.circular.Car", "org.jboss.jsr299.tck.tests.lookup.circular.Petrol");
		assertOnlyInjectionOfClassBeanReslovedToAnotherClassBean("org.jboss.jsr299.tck.tests.lookup.circular.Petrol", "org.jboss.jsr299.tck.tests.lookup.circular.Car");
	}

	/**
	 * section 5 b) 
	 * @throws CoreException
	 */
	public void testNormalProducerMethodDeclaredOnNormalBeanWhichInjectsProducedBean() throws CoreException {
		assertOnlyInjectionOfClassBeanReslovedToProducerMethodOfSameClass("org.jboss.jsr299.tck.tests.lookup.circular.NormalSelfConsumingNormalProducer");
	}

	/**
	 * section 5 b) 
	 * @throws CoreException
	 */
	public void testNormalProducerMethodDeclaredOnDependentBeanWhichInjectsProducedBean() throws CoreException {
		assertOnlyInjectionOfClassBeanReslovedToProducerMethodOfSameClass("org.jboss.jsr299.tck.tests.lookup.circular.DependentSelfConsumingNormalProducer");
	}

	/**
	 * section 5 b) 
	 * @throws CoreException
	 */
	public void testDependentProducerMethodDeclaredOnNormalBeanWhichInjectsProducedBean() throws CoreException {
		assertOnlyInjectionOfClassBeanReslovedToProducerMethodOfSameClass("org.jboss.jsr299.tck.tests.lookup.circular.NormalSelfConsumingDependentProducer");
	}

	/**
	 * section 5 b) 
	 * @throws CoreException
	 */
	public void testNormalSelfConsumingProducer() throws CoreException {
		assertEquals(1, getBeans("org.jboss.jsr299.tck.tests.lookup.circular.Violation").size());
	}

	/**
	 * section 5 b) 
	 * @throws CoreException
	 */
	public void testNormalCircularConstructors() throws CoreException {
		assertOnlyInjectionConstructorOfClassBeanReslovedToAnotherClassBean("org.jboss.jsr299.tck.tests.lookup.circular.Bird", "org.jboss.jsr299.tck.tests.lookup.circular.Air");
	}

	/**
	 * section 5 b) 
	 * @throws CoreException
	 */
	public void testNormalAndDependentCircularConstructors() throws CoreException {
		assertOnlyInjectionConstructorOfClassBeanReslovedToAnotherClassBean("org.jboss.jsr299.tck.tests.lookup.circular.Planet", "org.jboss.jsr299.tck.tests.lookup.circular.Space");
	}

	/**
	 * section 5 b) 
	 * @throws CoreException
	 */
	public void testSelfConsumingConstructorsOnNormalBean() throws CoreException {
		assertOnlyInjectionConstructorOfClassBeanReslovedToAnotherClassBean("org.jboss.jsr299.tck.tests.lookup.circular.House", "org.jboss.jsr299.tck.tests.lookup.circular.House");
	}

	public IClassBean getOnlyClassBean(String type) throws CoreException {
		Set<IBean> beans = getBeans(type);
		assertEquals("Wrong number of the beans", 1, beans.size());
		IBean bean = beans.iterator().next();
		assertTrue(bean instanceof IClassBean);
		assertEquals(type, ((IClassBean)bean).getBeanClass().getFullyQualifiedName());
		return (IClassBean)bean;
	}

	public void assertOnlyInjectionOfClassBeanReslovedToAnotherClassBean(String classBeanType, String injectionClassBeanType) throws CoreException {
		IBean bean = getOnlyClassBean(classBeanType);
		Set<IInjectionPoint> points = bean.getInjectionPoints();
		assertEquals("Wrong number of the injections", 1, points.size());
		IInjectionPoint point = points.iterator().next();
		Set<IBean> beans = cdiProject.getBeans(true, point);
		assertEquals("Wrong number of the beans", 1, beans.size());
		bean = beans.iterator().next();
		assertTrue(bean instanceof IClassBean);
		assertEquals(injectionClassBeanType, ((IClassBean)bean).getBeanClass().getFullyQualifiedName());
	}

	public void assertOnlyInjectionOfClassBeanReslovedToProducerMethodOfSameClass(String classBeanType) throws CoreException {
		IBean bean = getOnlyClassBean(classBeanType);
		Set<IInjectionPoint> points = bean.getInjectionPoints();
		assertEquals("Wrong number of the injections", 1, points.size());
		IInjectionPoint point = points.iterator().next();
		Set<IBean> beans = cdiProject.getBeans(true, point);
		assertEquals("Wrong number of the beans", 1, beans.size());
		bean = beans.iterator().next();
		assertTrue(bean instanceof IProducerMethod);
		assertEquals(classBeanType, ((IProducerMethod)bean).getBeanClass().getFullyQualifiedName());
	}

	public void assertOnlyInjectionConstructorOfClassBeanReslovedToAnotherClassBean(String classBeanType, String injectionClassBeanType) throws CoreException {
		IBean bean = getOnlyClassBean(classBeanType);
		Set<IInjectionPoint> allPoints = bean.getInjectionPoints();
		Set<IInjectionPointParameter> points = new HashSet<IInjectionPointParameter>();
		for (IInjectionPoint injectionPoint : allPoints) {
			if(injectionPoint instanceof IInjectionPointParameter) {
				points.add((IInjectionPointParameter)injectionPoint);
			}
		}
		assertEquals("Wrong number of the injections", 1, points.size());
		IInjectionPoint point = points.iterator().next();
		Set<IBean> beans = cdiProject.getBeans(true, point);
		assertEquals("Wrong number of the beans", 1, beans.size());
		bean = beans.iterator().next();
		assertTrue(bean instanceof IClassBean);
		assertEquals(injectionClassBeanType, ((IClassBean)bean).getBeanClass().getFullyQualifiedName());
	}
}