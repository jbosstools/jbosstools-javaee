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
public class StereotypeInheritenceTest extends TCKTest {

	/**
	 * section 2.7.1.5 a)
	 * section 2.7.1.5 b)
	 * 
	 * @throws JavaModelException
	 */
	public void testInheritence() throws JavaModelException {
		Set<IBean> beans = getBeans(false, "org.jboss.jsr299.tck.tests.definition.stereotype.inheritance.Horse");
		assertEquals("Wrong number of beans.", 1, beans.size());
		IBean bean = beans.iterator().next();
		assertEquals("Wrong scope type",
				"javax.enterprise.context.RequestScoped", bean.getScope()
						.getSourceType().getFullyQualifiedName());
		assertTrue("The bean should be an alternative.", bean.isAlternative());
		assertEquals("Wrong EL name.", "horse", bean.getName());
	}
}