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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IDecorator;
import org.jboss.tools.cdi.core.IParametedType;

/**
 * @author Alexey Kazakov
 */
public class DecoratorDefinitionTest extends TCKTest {

	/**
	 * section 8.1 b)
	 * section 8.1 c)
	 * section 11.1.1 b)
	 * section 11.3.11 a)
	 * section 11.3.11 b)
	 * 
	 * @throws JavaModelException
	 */
	public void testDecoratedTypes() throws JavaModelException, CoreException {
		IClassBean bean = getClassBean("JavaSource/org/jboss/jsr299/tck/tests/decorators/definition/FooDecorator.java");
		assertNotNull("Can't find the bean.", bean);
		if(!(bean instanceof IDecorator)) {
			fail("The bean is not a decorator.");
		}
		IDecorator decorator = (IDecorator)bean;
		Set<IParametedType> types = decorator.getDecoratedTypes();
		assertContainsTypes(types,
				"org.jboss.jsr299.tck.tests.decorators.definition.Foo",
				"org.jboss.jsr299.tck.tests.decorators.definition.Bar",
				"org.jboss.jsr299.tck.tests.decorators.definition.Baz");
	}
}