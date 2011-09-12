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
package org.jboss.tools.cdi.core.test.tck;

import java.util.Set;

import org.jboss.tools.cdi.core.CDIUtil;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.common.java.IAnnotationDeclaration;

/**
 * @author Alexey Kazakov
 */
public class CDIUtilTest extends TCKTest {

	/**
	 * See https://issues.jboss.org/browse/JBIDE-9685 Seam JMS: CDI validator should be aware of JMS resource injections
	 */
	public void testMethodParameter() throws Exception {
		IClassBean bean = getClassBean("org.jboss.jsr299.tck.tests.jbt.core.TestInjection", "JavaSource/org/jboss/jsr299/tck/tests/jbt/core/TestInjection.java");
		Set<IInjectionPoint> injections = bean.getInjectionPoints();
		assertEquals(9, injections.size());
		for (IInjectionPoint injectionPoint : injections) {
			IAnnotationDeclaration declaration = CDIUtil.getAnnotationDeclaration(injectionPoint, "org.jboss.jsr299.tck.tests.jbt.test.core.TestQualifier");
			String elementName = injectionPoint.getSourceMember().getElementName();
			if(elementName.equals("i4")) {
				assertNull("Have found @TestQualifier for " + elementName, declaration);
			} else {
				assertNotNull("Have not found @TestQualifier for " + elementName, declaration);
			}
			declaration = CDIUtil.getAnnotationDeclaration(injectionPoint, "org.jboss.jsr299.tck.tests.jbt.test.core.TestQualifier3");
			if(elementName.equals("i1") || elementName.equals("i2") || elementName.equals("i3") || elementName.equals("i7")) {
				assertNull("Have found @TestQualifier3 for " + elementName, declaration);
			} else {
				assertNotNull("Have not found @TestQualifier3 for " + elementName, declaration);
			}
		}
	}
}