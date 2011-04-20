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

import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IParameter;
import org.jboss.tools.cdi.core.IProducerMethod;

/**
 * @author Alexey Kazakov
 */
public class ProducerMethodDefinitionTest extends TCKTest {

	/**
	 * Section 3.3.2 - Declaring a producer method
	 *  i) All producer method parameters are injection points.
	 *
	 * @throws JavaModelException 
	 */
	public void testBindingTypesAppliedToProducerMethodParameters() throws JavaModelException {
		Set<IBean> beans = cdiProject.getBeans(true, "org.jboss.jsr299.tck.tests.implementation.producer.method.definition.Tarantula", "org.jboss.jsr299.tck.tests.implementation.producer.method.definition.Deadliest");
		IBean bean = beans.iterator().next();
		Set<IInjectionPoint> injections = bean.getInjectionPoints();
		assertEquals("Wrong number of injection points in the producer.", 2, injections.size());
		// TODO use real location for injection points.
		assertLocationEquals(injections, 1287, 29);
		assertLocationEquals(injections, 1328, 19);
	}

	// TODO continue implementing producer tests.

	public void testParameterDefinition() {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/definition/qualifier/SpiderProducer.java");
		Set<IBean> bs = cdiProject.getBeans(file.getFullPath());
		IProducerMethod producer = null;
		for (IBean bean: bs) {
			if(bean instanceof IProducerMethod) {
				IProducerMethod m = (IProducerMethod)bean;
				if(m.getMethod().getElementName().equals("produceSpiderFromInjection")) {
					producer = m;
				}
			}
		}
		assertNotNull(producer);
		List<IParameter> ps = producer.getParameters();
		assertEquals(1, ps.size());
		IParameter param = ps.get(0);
		assertTrue(param.isAnnotationPresent("org.jboss.jsr299.tck.tests.definition.qualifier.Tame"));
	}

	public void testParameterDefinitionOnBrokenMethod() {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/definition/qualifier/SpiderProducer_Broken.java");
		Set<IBean> bs = cdiProject.getBeans(file.getFullPath());
		IProducerMethod producer = null;
		for (IBean bean: bs) {
			if(bean instanceof IProducerMethod) {
				IProducerMethod m = (IProducerMethod)bean;
				if(m.getMethod().getElementName().equals("produceSpiderFromInjection")) {
					producer = m;
				}
			}
		}
		assertNotNull(producer);
		List<IParameter> ps = producer.getParameters();
		assertEquals(1, ps.size());
		IParameter param = ps.get(0);
		assertTrue(param.isAnnotationPresent("org.jboss.jsr299.tck.tests.definition.qualifier.Tame"));	
	}

}