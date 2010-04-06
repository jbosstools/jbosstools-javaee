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
public class EnterpriseScopeDefinitionTest extends TCKTest {

	/**
	 * section 4.1 be)
	 * 
	 * @throws JavaModelException
	 */
	public void testScopeTypeDeclaredInheritedIsInherited()
			throws JavaModelException {
		Set<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.scope.enterprise.BorderCollieLocal");
		IBean bean = beans.iterator().next();
		assertEquals("Wrong scope type",
				"javax.enterprise.context.RequestScoped", bean.getScope()
						.getSourceType().getFullyQualifiedName());
	}

	/**
	 * section 4.1 bea)
	 * 
	 * @throws JavaModelException
	 */
	public void testScopeTypeNotDeclaredInheritedIsNotInherited()
			throws JavaModelException {
		Set<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.scope.enterprise.SiameseLocal");
		IBean bean = beans.iterator().next();
		assertFalse("Wrong scope type",
				"org.jboss.jsr299.tck.tests.definition.scope.enterprise.FooScoped"
						.equals(bean.getScope().getSourceType()
								.getFullyQualifiedName()));
	}

	/**
	 * section 4.1 bh)
	 * 
	 * @throws JavaModelException
	 */
	public void testScopeTypeDeclaredInheritedIsIndirectlyInherited()
			throws JavaModelException {
		Set<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.scope.enterprise.EnglishBorderCollieLocal");
		IBean bean = beans.iterator().next();
		assertEquals("Wrong scope type",
				"javax.enterprise.context.RequestScoped", bean.getScope()
						.getSourceType().getFullyQualifiedName());
	}

	/**
	 * section 4.1 bha)
	 * 
	 * @throws JavaModelException
	 */
	public void testScopeTypeNotDeclaredInheritedIsNotIndirectlyInherited()
			throws JavaModelException {
		Set<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.scope.enterprise.BengalTigerLocal");
		IBean bean = beans.iterator().next();
		assertFalse("Wrong scope type",
				"org.jboss.jsr299.tck.tests.definition.scope.enterprise.FooScoped"
						.equals(bean.getScope().getSourceType()
								.getFullyQualifiedName()));
	}
}