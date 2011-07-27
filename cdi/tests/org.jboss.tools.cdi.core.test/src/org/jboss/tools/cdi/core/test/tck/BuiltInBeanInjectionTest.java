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
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.internal.core.impl.BuiltInBean;

/**
 * @author Viacheslav Kabanovich
 */
public class BuiltInBeanInjectionTest extends TCKTest {

	/**
	 * Test built-in bean with type javax.transaction.UserTransaction
	 */
	public void testBuiltInUserTransactionBean() {
		IInjectionPointField field =  getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/implementation/builtin/UserTransactionInjectedBean.java", "userTransaction");
		assertNotNull(field);
		
		Set<IBean> beans = field.getCDIProject().getBeans(false, field);
		assertFalse(beans.isEmpty());

		IBean b = beans.iterator().next();
		assertTrue(b instanceof BuiltInBean);
		IType t = b.getBeanClass();
		assertEquals("javax.transaction.UserTransaction", t.getFullyQualifiedName());
	}

	/**
	 * Test built-in bean with type javax.validation.ValidatorFactory
	 */
	public void testBuiltInValidatorFactoryBean() {
		IInjectionPointField field =  getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/implementation/builtin/DefaultValidatorFactoryInjectedBean.java", "defaultValidatorFactory");
		assertNotNull(field);
		
		Set<IBean> beans = field.getCDIProject().getBeans(false, field);
		assertFalse(beans.isEmpty());

		IBean b = beans.iterator().next();
		assertTrue(b instanceof BuiltInBean);
		IType t = b.getBeanClass();
		assertEquals("javax.validation.ValidatorFactory", t.getFullyQualifiedName());
	}

	/**
	 * Test built-in bean with type javax.validation.Validator
	 */
	public void testBuiltInValidatorBean() {
		IInjectionPointField field =  getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/implementation/builtin/DefaultValidatorInjectedBean.java", "defaultValidator");
		assertNotNull(field);
		
		Set<IBean> beans = field.getCDIProject().getBeans(false, field);
		assertFalse(beans.isEmpty());

		IBean b = beans.iterator().next();
		assertTrue(b instanceof BuiltInBean);
		IType t = b.getBeanClass();
		assertEquals("javax.validation.Validator", t.getFullyQualifiedName());
	}

	/**
	 * Test built-in bean with type java.security.Principal
	 */
	public void testBuiltInPrincipalBean() {
		IInjectionPointField field =  getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/implementation/builtin/PrincipalInjectedBean.java", "principal");
		assertNotNull(field);
		
		Set<IBean> beans = field.getCDIProject().getBeans(false, field);
		assertFalse(beans.isEmpty());

		IBean b = beans.iterator().next();
		assertTrue(b instanceof BuiltInBean);
		IType t = b.getBeanClass();
		assertEquals("java.security.Principal", t.getFullyQualifiedName());
	}

	/**
	 * Test built-in bean with type javax.enterprise.inject.spi.BeanManager
	 */
	public void testBuiltInBeanManagerBean() {
		IInjectionPointField field =  getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/context/conversation/BuiltInConversation.java", "manager");
		assertNotNull(field);
		
		Set<IBean> beans = field.getCDIProject().getBeans(false, field);
		assertFalse(beans.isEmpty());

		IBean b = beans.iterator().next();
		assertTrue(b instanceof BuiltInBean);
		IType t = b.getBeanClass();
		assertEquals(CDIConstants.BEAN_MANAGER_TYPE_NAME, t.getFullyQualifiedName());
	}

	/**
	 * Test built-in bean with type javax.enterprise.context.Conversation
	 */
	public void testBuiltInConversationBean() {
		IInjectionPointField field =  getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/context/conversation/BuiltInConversation.java", "conversation");
		assertNotNull(field);
		
		Set<IBean> beans = field.getCDIProject().getBeans(false, field);
		assertFalse(beans.isEmpty());

		IBean b = beans.iterator().next();
		assertTrue(b instanceof IClassBean);
		IType t = b.getBeanClass();
		assertEquals(CDIConstants.CONVERSATION_TYPE_NAME, t.getFullyQualifiedName());
		assertEquals(CDIConstants.CONVERSATION_BEAN_NAME, b.getName());
		beans = cdiProject.getBeans(CDIConstants.CONVERSATION_BEAN_NAME, false);
		assertTrue(beans.contains(b));
	}

}