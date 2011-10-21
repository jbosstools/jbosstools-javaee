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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.jboss.tools.cdi.core.CDIUtil;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IInjectionPointParameter;
import org.jboss.tools.cdi.internal.core.impl.Parameter;
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
			IAnnotationDeclaration declaration = CDIUtil.getAnnotationDeclaration(injectionPoint, "org.jboss.jsr299.tck.tests.jbt.core.TestQualifier");
			String elementName = injectionPoint.getSourceMember().getElementName();
			if(elementName.equals("i4")) {
				assertNull("Have found @TestQualifier for " + elementName, declaration);
			} else {
				assertNotNull("Have not found @TestQualifier for " + elementName, declaration);
			}
			declaration = CDIUtil.getAnnotationDeclaration(injectionPoint, "org.jboss.jsr299.tck.tests.jbt.core.TestQualifier3");
			if(elementName.equals("i1") || elementName.equals("i2") || elementName.equals("i3") || elementName.equals("i7")) {
				assertNull("Have found @TestQualifier3 for " + elementName, declaration);
			} else {
				assertNotNull("Have not found @TestQualifier3 for " + elementName, declaration);
			}
		}
	}

	public void testFindInjectionPoint() throws Exception {
		String path = "JavaSource/org/jboss/jsr299/tck/tests/jbt/core/TestInjection2.java";
		IClassBean bean = getClassBean("org.jboss.jsr299.tck.tests.jbt.core.TestInjection2", path);
		Set<IBean> bs = cdiProject.getBeans(new Path("/tck/" + path));
		Set<IInjectionPointParameter> ps = CDIUtil.getInjectionPointParameters(bean); 
		assertEquals("Unexpected number of injection points.", 10, ps.size());
		int testcount = 0;
		for (IInjectionPointParameter p: ps) {
			for (int pos = p.getStartPosition(); pos <= p.getStartPosition() + p.getLength(); pos++) {
				IJavaElement element = bean.getBeanClass().getCompilationUnit().getElementAt(pos);
				IInjectionPoint p1 = CDIUtil.findInjectionPoint(bs, element, pos);
				assertTrue("Injection point is wrong at position " + pos + " for element " + element, p == p1);
				testcount++;
				if(element instanceof IMethod) {
					IMethod m = (IMethod)element;
					ILocalVariable[] vs = m.getParameters();
					for (ILocalVariable v: vs) {
						if(v.getSourceRange().getOffset() <= pos && pos <= v.getSourceRange().getOffset() + v.getSourceRange().getLength()) {
							IInjectionPoint p2 = CDIUtil.findInjectionPoint(bs, element, pos);
							assertTrue("Injection point is wrong at position " + pos + " for element " + element, p == p2);
							testcount++;
						}
					}
				}
			}
		}
		
		//Double length of all injected parameter ranges.  
		assertEquals("Unexpected double length of all injected parameter ranges.", 358, testcount);
	}

	public void testFindInjectionPoint2() throws Exception {
		String path = "JavaSource/org/jboss/jsr299/tck/tests/jbt/core/TestInjection2.java";
		Set<IBean> bs = cdiProject.getBeans(new Path("/tck/" + path));
		Set<IInjectionPoint> ps = new HashSet<IInjectionPoint>();
		for (IBean b: bs) {
			ps.addAll(b.getInjectionPoints());
		}
		IInjectionPoint[] array = ps.toArray(new IInjectionPoint[ps.size()]);
		for (int i = 0; i < array.length; i++) {
			IJavaElement element = array[i] instanceof Parameter 
					? ((Parameter)array[i]).getDefinition().getVariable()
					: array[i].getSourceMember();
			for (int j = 0; j < array.length; j++) {
				assertEquals(i == j, array[j].isDeclaredFor(element));
			}
		}		
	}

}