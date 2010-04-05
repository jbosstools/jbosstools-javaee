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
import org.jboss.tools.cdi.core.IScopeDeclaration;

/**
 * @author Alexey Kazakov
 */
public class ScopeDefinitionTest extends TCKTest {

	/**
	 * section 2.4 c)
	 * @throws JavaModelException 
	 */
	public void testScopeTypesAreExtensible() throws JavaModelException {
		Set<IBean> beans = getBeans("org.jboss.jsr299.tck.tests.definition.scope.Mullet");
		assertEquals("Wrong number of beans.", 1, beans.size());
		IBean bean = beans.iterator().next();
		assertEquals("Wrong scope type", "org.jboss.jsr299.tck.tests.definition.scope.AnotherScopeType", bean.getScope().getSourceType().getFullyQualifiedName());
		Set<IScopeDeclaration> declarations = bean.getScopeDeclarations();
		assertEquals("Wrong number of scope declarations", 1, declarations.size());
		assertLocationEquals(declarations, 848, 17);
	}
}