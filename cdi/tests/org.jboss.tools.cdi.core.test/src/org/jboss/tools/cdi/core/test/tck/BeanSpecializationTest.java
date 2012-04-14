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

import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.IBean;

/**
 * @author Alexey Kazakov
 */
public class BeanSpecializationTest extends TCKTest {

	/**
	 * Section 4.3.1 - Direct and indirect specialization
	 * j) A bean X that specializes bean Y will include all qualifiers of Y, together with all qualifiers declared explicitly by X.
	 * 
	 * @throws JavaModelException
	 */
	public void testSimpleSpecializingBeanHasQualifiersOfSpecializedAndSpecializingBean() throws JavaModelException {
		Set<IBean> beans = cdiProject.getBeans(true, "org.jboss.jsr299.tck.tests.inheritance.specialization.simple.LazyFarmer", "org.jboss.jsr299.tck.tests.inheritance.specialization.simple.Landowner");
		assertEquals("Wrong number of beans.", 1, beans.size());
		IBean bean = beans.iterator().next();
		assertContainsBeanTypes(false, bean, "org.jboss.jsr299.tck.tests.inheritance.specialization.simple.Farmer");
		assertContainsQualifierType(true, bean,
				"org.jboss.jsr299.tck.tests.inheritance.specialization.simple.Landowner",
				"org.jboss.jsr299.tck.tests.inheritance.specialization.simple.Lazy",
				"javax.enterprise.inject.Any",
				"javax.inject.Named");
	}

	/**
	 * Section 4.3.1 - Direct and indirect specialization
	 * j) A bean X that specializes bean Y will include all qualifiers of Y, together with all qualifiers declared explicitly by X.
	 * 
	 * @throws JavaModelException
	 */
	public void testEnterpriseSpecializingBeanHasQualifiersOfSpecializedAndSpecializingBean() throws JavaModelException {
		Set<IBean> beans = cdiProject.getBeans(true, "org.jboss.jsr299.tck.tests.inheritance.specialization.enterprise.LazyFarmerLocal", "org.jboss.jsr299.tck.tests.inheritance.specialization.enterprise.Landowner");
		assertEquals("Wrong number of beans.", 1, beans.size());
		IBean bean = beans.iterator().next();
		assertContainsBeanTypes(false, bean, "org.jboss.jsr299.tck.tests.inheritance.specialization.enterprise.FarmerLocal");
		assertContainsQualifierType(true, bean,
				"org.jboss.jsr299.tck.tests.inheritance.specialization.enterprise.Landowner",
				"org.jboss.jsr299.tck.tests.inheritance.specialization.enterprise.Lazy",
				"javax.enterprise.inject.Any",
				"javax.inject.Named");
	}

	/**
	 * Section 4.3.1 - Direct and indirect specialization
	 * k) A bean X that specializes bean Y will have the same name as Y if Y has a name.
	 * 
	 * @throws JavaModelException
	 */
	public void testSimpleSpecializingBeanHasNameOfSpecializedBean() throws JavaModelException {
		Set<IBean> beans = cdiProject.getBeans(true, "org.jboss.jsr299.tck.tests.inheritance.specialization.simple.LazyFarmer", "org.jboss.jsr299.tck.tests.inheritance.specialization.simple.Landowner");
		assertEquals("Wrong number of beans.", 1, beans.size());
		IBean bean = beans.iterator().next();
		assertEquals("Incorrect bean name", "farmer", bean.getName());
	}

	/**
	 * Section 4.3.1 - Direct and indirect specialization
	 * k) A bean X that specializes bean Y will have the same name as Y if Y has a name.
	 * 
	 * @throws JavaModelException
	 */
	public void testEnterpriseSpecializingBeanHasNameOfSpecializedBean() throws JavaModelException {
		Set<IBean> beans = cdiProject.getBeans(true, "org.jboss.jsr299.tck.tests.inheritance.specialization.enterprise.LazyFarmerLocal", "org.jboss.jsr299.tck.tests.inheritance.specialization.enterprise.Landowner");
		assertEquals("Wrong number of beans.", 1, beans.size());
		IBean bean = beans.iterator().next();
		assertEquals("Incorrect bean name", "farmer", bean.getName());
	}

	public void testSimpleSpecializingBeanDefinesNameWhenSpecializedBeanIsNotNamed() throws JavaModelException {
		Set<IBean> beans = cdiProject.getBeans(true, "org.jboss.jsr299.tck.tests.implementation.simple.lifecycle.TameLion", "org.jboss.jsr299.tck.tests.implementation.simple.lifecycle.Tame");
		assertEquals("Wrong number of beans.", 1, beans.size());
		IBean bean = beans.iterator().next();
		assertEquals("Incorrect bean name", "tameLion", bean.getName());
	}
}