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
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.internal.core.impl.BuiltInBean;

/**
 * @author Viacheslav Kabanovich
 */
public class BuiltInBeanInjectionTest extends TCKTest {

	public void testBuiltInBeans() {
		// javax.transaction.UserTransaction
		IInjectionPointField field =  getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/implementation/builtin/UserTransactionInjectedBean.java", "userTransaction");
		assertNotNull(field);
		
		Set<IBean> beans = field.getCDIProject().getBeans(false, field);
		assertFalse(beans.isEmpty());

		IBean b = beans.iterator().next();
		assertTrue(b instanceof BuiltInBean);
		IType t = b.getBeanClass();
		assertEquals("javax.transaction.UserTransaction", t.getFullyQualifiedName());

		
		// javax.validation.ValidatorFactory
		field =  getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/implementation/builtin/DefaultValidatorFactoryInjectedBean.java", "defaultValidatorFactory");
		assertNotNull(field);
		
		beans = field.getCDIProject().getBeans(false, field);
		assertFalse(beans.isEmpty());

		b = beans.iterator().next();
		assertTrue(b instanceof BuiltInBean);
		t = b.getBeanClass();
		assertEquals("javax.validation.ValidatorFactory", t.getFullyQualifiedName());

		// javax.validation.Validator
		field =  getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/implementation/builtin/DefaultValidatorInjectedBean.java", "defaultValidator");
		assertNotNull(field);
		
		beans = field.getCDIProject().getBeans(false, field);
		assertFalse(beans.isEmpty());

		b = beans.iterator().next();
		assertTrue(b instanceof BuiltInBean);
		t = b.getBeanClass();
		assertEquals("javax.validation.Validator", t.getFullyQualifiedName());

		// java.security.Principal
		field =  getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/implementation/builtin/PrincipalInjectedBean.java", "principal");
		assertNotNull(field);
		
		beans = field.getCDIProject().getBeans(false, field);
		assertFalse(beans.isEmpty());

		b = beans.iterator().next();
		assertTrue(b instanceof BuiltInBean);
		t = b.getBeanClass();
		assertEquals("java.security.Principal", t.getFullyQualifiedName());

	
	}

}