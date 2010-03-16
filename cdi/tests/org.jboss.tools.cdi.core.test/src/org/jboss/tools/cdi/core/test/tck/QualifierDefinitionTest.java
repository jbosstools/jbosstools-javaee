/******************************************************************************* 
 * Copyright (c) 2010 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.core.test.tck;

import java.util.Set;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IProducerMethod;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.core.IQualifierDeclaration;

/**
 * @author Alexey Kazakov
 */
public class QualifierDefinitionTest extends TCKTest {

	/**
	 * section 2.3.1 a0)
	 * section 2.3.1 aa)
	 *
	 * @throws JavaModelException 
	 */
	public void testDefaultQualifierDeclaredInJava() throws JavaModelException {
		Set<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.qualifier.Order");
		assertEquals("Wrong number of beans with org.jboss.jsr299.tck.tests.definition.qualifier.Order type.", 1, beans.size());
		IBean bean = beans.iterator().next();
		Set<IQualifier> qualifiers = bean.getQualifiers();
		assertEquals("Wrong number of qualifiers for org.jboss.jsr299.tck.tests.definition.qualifier.Order type.", 2, qualifiers.size());
		assertContainsQualifierType(bean, "javax.enterprise.inject.Default");
		assertContainsQualifierType(bean, "javax.enterprise.inject.Any");
	}

	/**
	 * section 2.3.1, b)
	 * section 11.1 c)
	 * @throws JavaModelException 
	 */
	public void testDefaultQualifierForInjectionPoint() throws JavaModelException {
		Set<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.qualifier.Order");
		assertEquals("Wrong number of beans with org.jboss.jsr299.tck.tests.definition.qualifier.Order type.", 1, beans.size());
		IBean bean = beans.iterator().next();
		Set<IInjectionPoint> points = bean.getInjectionPoints();
		IInjectionPoint point = points.iterator().next();
		Set<IQualifier> qualifiers = bean.getQualifiers();
		// TODO do we need to care about default qualifiers for InjectionPoint in CDI Tools?
//		assertContainsQualifierType(point, "javax.enterprise.inject.Default");
	}

	/**
	 * section 2.3.1 a0)
	 */
	public void testNewQualifierAndAnyBindingMutualExclusive() {
		// TODO
	}

	/**
	 * section 2.3.2 ba)
	 */
	public void testQualifierDeclaresBindingAnnotation() {
		Set<IBean> beans = cdiProject.getBeans(true, "org.jboss.jsr299.tck.tests.definition.qualifier.Tarantula", "org.jboss.jsr299.tck.tests.definition.qualifier.Tame");
		assertFalse("Wrong number of beans with org.jboss.jsr299.tck.tests.definition.qualifier.Tarantula type and org.jboss.jsr299.tck.tests.definition.qualifier.Tame qualifier.", beans.isEmpty());
	}

	/**
	 * section 2.3.3 a)
	 * section 3.1.3 be)
	 */
	public void testQualifiersDeclaredInJava() {
		Set<IBean> beans = cdiProject.getBeans(true, "org.jboss.jsr299.tck.tests.definition.qualifier.Cat", "org.jboss.jsr299.tck.tests.definition.qualifier.SynchronousQualifier");
		assertEquals("Wrong number of beans with org.jboss.jsr299.tck.tests.definition.qualifier.Cat type and org.jboss.jsr299.tck.tests.definition.qualifier.SynchronousQualifier qualifier.", 1, beans.size());
		IBean bean = beans.iterator().next();
		Set<IQualifier> qualifiers = bean.getQualifiers();
		assertEquals("Wrong number of qualifiers.", 2, qualifiers.size());
		assertContainsQualifierType(bean, "org.jboss.jsr299.tck.tests.definition.qualifier.SynchronousQualifier");
		Set<IQualifierDeclaration> declarations = bean.getQualifierDeclarations();
		assertEquals("Wrong number of qualifier declarations.", 1, declarations.size());
		// TODO use correct start position instead of 0. 
		assertLocationEquals(declarations, 0, 12);
	}

	/**
	 * section 2.3.3 d)
	 * @throws JavaModelException 
	 */
	public void testMultipleQualifiers() throws JavaModelException {
		IAnnotationDeclaration chunky = getAnnotationDeclarationFromBean("JavaSource/org/jboss/jsr299/tck/tests/definition/qualifier/Cod.java", "org.jboss.jsr299.tck.tests.definition.qualifier.Chunky");
		IAnnotationDeclaration whitefish = getAnnotationDeclarationFromBean("JavaSource/org/jboss/jsr299/tck/tests/definition/qualifier/Cod.java", "org.jboss.jsr299.tck.tests.definition.qualifier.Whitefish");
		IType type = getType("org.jboss.jsr299.tck.tests.definition.qualifier.Cod");
		Set<IBean> beans = cdiProject.getBeans(true, type, chunky, whitefish);
		assertEquals("Wrong number of beans.", 1, beans.size());
		IBean bean = beans.iterator().next();
		Set<IQualifier> qualifiers = bean.getQualifiers();
		assertEquals("Wrong number of qualifiers.", 4, qualifiers.size());
		Set<IQualifierDeclaration> declarations = bean.getQualifierDeclarations();
		assertEquals("Wrong number of qualifier declarations.", 2, declarations.size());
		// TODO use correct start position instead of 0.
		assertLocationEquals(declarations, 0, 6);
		assertLocationEquals(declarations, 0, 9);
	}

	/**
	 * section 2.3.5 a)
	 * @throws JavaModelException 
	 */
	public void testFieldInjectedFromProducerMethod() throws JavaModelException {
		Set<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.qualifier.Barn");
		assertEquals("Wrong number of beans with org.jboss.jsr299.tck.tests.definition.qualifier.Barn type.", 1, beans.size());
		IBean bean = beans.iterator().next();
		Set<IInjectionPoint> points = bean.getInjectionPoints();
		IInjectionPoint point = points.iterator().next();
		Set<IQualifierDeclaration> declarations = point.getQualifierDeclarations();
		assertEquals("Wrong number of qualifier declarations.", 1, declarations.size());
		// TODO use correct start position instead of 0.
		assertLocationEquals(declarations, 0, 5);

		Set<IBean> injectedBeans = cdiProject.getBeans(point);
		assertEquals("Wrong number of beans.", 1, injectedBeans.size());
		IBean injectedBean = injectedBeans.iterator().next();
		IType beanClass = injectedBean.getBeanClass();
		assertEquals("Wrong bean class.", "org.jboss.jsr299.tck.tests.definition.qualifier.SpiderProducer", beanClass.getFullyQualifiedName());
		assertTrue("The bean should be a producer method.", injectedBean instanceof IProducerMethod);
		IProducerMethod producer = (IProducerMethod)injectedBean;
		declarations = producer.getQualifierDeclarations();
		assertEquals("Wrong number of qualifier declarations.", 1, declarations.size());
		// TODO use correct start position instead of 0.
		assertLocationEquals(declarations, 0, 5);
	}

	/**
	 * section 4.1 aa)
	 * @throws JavaModelException 
	 */
	public void testQualifierDeclaredInheritedIsInherited() throws JavaModelException {
		IAnnotationDeclaration hairy = getAnnotationDeclarationFromBean("JavaSource/org/jboss/jsr299/tck/tests/definition/qualifier/LongHairedDog.java", "org.jboss.jsr299.tck.tests.definition.qualifier.Hairy");
		IType type = getType("org.jboss.jsr299.tck.tests.definition.qualifier.BorderCollie");
		Set<IBean> beans = cdiProject.getBeans(true, type, hairy);
		assertEquals("Wrong number of beans.", 1, beans.size());
		IBean bean = beans.iterator().next();
		Set<IQualifier> qualifiers = bean.getQualifiers();
		assertEquals("Wrong number of qualifiers for org.jboss.jsr299.tck.tests.definition.qualifier.BorderCollie type.", 2, qualifiers.size());
		assertContainsQualifierType(bean, "org.jboss.jsr299.tck.tests.definition.qualifier.Hairy");
		assertContainsQualifierType(bean, "javax.enterprise.inject.Any");
	}

	/**
	 * section 4.1 aaa)
	 * @throws JavaModelException
	 */
	public void testQualifierNotDeclaredInheritedIsNotInherited() throws JavaModelException {
		Set<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.qualifier.ShetlandPony");
		assertEquals("Wrong number of beans.", 1, beans.size());
		IBean bean = beans.iterator().next();
		Set<IQualifier> qualifiers = bean.getQualifiers();
		assertEquals(
				"Wrong number of qualifiers for org.jboss.jsr299.tck.tests.definition.qualifier.BorderCollie type.",
				2, qualifiers.size());
		assertContainsQualifierType(bean, "javax.enterprise.inject.Default");
		assertContainsQualifierType(bean, "javax.enterprise.inject.Any");
	}
}