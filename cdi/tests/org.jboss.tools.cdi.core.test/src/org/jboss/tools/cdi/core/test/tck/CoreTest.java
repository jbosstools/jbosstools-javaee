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

import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IBeanField;
import org.jboss.tools.cdi.core.IBeanMethod;
import org.jboss.tools.cdi.core.ICDIElement;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IParameter;
import org.jboss.tools.cdi.core.IProducer;
import org.jboss.tools.common.java.IParametedType;

/**
 * @author Alexey Kazakov
 */
public class CoreTest extends TCKTest {

	public void testElementNames() throws Exception {
		IBean[] beans = cdiProject.getBeans();
		assertTrue(beans.length>0);
		for (IBean bean : beans) {
			assertElementName(bean);
			Set<IParametedType> types = bean.getAllTypes();
			for (IParametedType type : types) {
				String name = type.getSimpleName();
				assertNotNull(name);
			}
			Set<IInjectionPoint> points = bean.getInjectionPoints();
			for (IInjectionPoint point : points) {
				assertElementName(point);
				if(point.getType()!=null) {
					if(point.getType().getSimpleName()==null) {
						System.out.println("!!!");
					}
					assertNotNull(point.getType().getSimpleName());
				}
			}
			if(bean instanceof IClassBean) {
				Set<IBeanMethod> methods = ((IClassBean)bean).getAllMethods();
				for (IBeanMethod method : methods) {
					assertElementName(method);
				}
			}
		}
	}

	public void testTypeName() throws Exception {
		IClassBean bean = getClassBean("JavaSource/org/jboss/jsr299/tck/tests/jbt/core/NamedElement.java");
		assertEquals("NamedElement", bean.getElementName());
	}

	public void testFieldName() throws Exception {
		IClassBean bean = getClassBean("JavaSource/org/jboss/jsr299/tck/tests/jbt/core/NamedElement.java");
		Set<IProducer> producers = bean.getProducers();
		assertEquals(2, producers.size());
		boolean found = false;
		for (IProducer producer : producers) {
			if(producer instanceof IBeanField) {
				assertEquals("NamedElement.i", producer.getElementName());
				found = true;
				break;
			}
		}
		assertTrue(found);
	}

	public void testMethodName() throws Exception {
		IClassBean bean = getClassBean("JavaSource/org/jboss/jsr299/tck/tests/jbt/core/NamedElement.java");
		Set<IProducer> producers = bean.getProducers();
		boolean found = false;
		for (IProducer producer : producers) {
			if(producer instanceof IBeanMethod) {
				assertEquals("NamedElement.getFoo()", producer.getElementName());
				found = true;
				break;
			}
		}
		assertTrue(found);
	}

	public void testParamName() throws Exception {
		IClassBean bean = getClassBean("JavaSource/org/jboss/jsr299/tck/tests/jbt/core/NamedElement.java");
		Set<IInjectionPoint> injections = bean.getInjectionPoints();
		assertEquals(1, injections.size());
		assertEquals("arg1", injections.iterator().next().getElementName());
	}

	public void assertElementName(ICDIElement element) throws Exception {
		assertNotNull("Name of " + element + " is null.", element.getElementName());
		if(element instanceof IBeanField) {
			IBeanField field = (IBeanField)element;
			assertEquals(field.getClassBean().getBeanClass().getElementName() + "." + field.getField().getElementName(), element.getElementName());
		} else if(element instanceof IBeanMethod) {
			IBeanMethod method = (IBeanMethod)element;
			assertEquals(method.getClassBean().getBeanClass().getElementName() + "." + method.getMethod().getElementName() + "()", element.getElementName());
		} else if(element instanceof IParameter) {
			IParameter param = (IParameter)element;
			assertEquals(param.getName(), element.getElementName());
		} else if(element instanceof IClassBean) {
			assertEquals(((IClassBean)element).getBeanClass().getElementName(), element.getElementName());
		}
	}
}