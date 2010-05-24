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

import org.jboss.tools.cdi.core.CDIUtil;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IInjectionPointParameter;

/**
 * @author Alexey Kazakov
 */
public class InjectionPointTest extends TCKTest {

	/**
	 * Section 3.7.1 - Declaring a bean constructor
	 *  - All parameters of a bean constructor are injection points.
	 */
	public void testQualifierTypeAnnotatedConstructor() {
		IClassBean bean = getClassBean("JavaSource/org/jboss/jsr299/tck/tests/implementation/simple/lifecycle/Duck.java");
		Set<IInjectionPointParameter> points = CDIUtil.getInjectionPointParameters(bean);
		assertEquals("There should be two injection point parameters in the bean.", 2, points.size());
	}

	/**
	 * Section 3.9.1 - Declaring an initializer method
	 *  - All initializer method parameters are injection points.
	 */
	public void testBindingTypeOnInitializerParameter() {
		IClassBean bean = getClassBean("JavaSource/org/jboss/jsr299/tck/tests/implementation/enterprise/lifecycle/Mainz.java");
		Set<IInjectionPointParameter> points = CDIUtil.getInjectionPointParameters(bean);
		assertEquals("There should be two injection point parameters in the bean.", 1, points.size());
	}
}