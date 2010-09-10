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

import org.eclipse.core.runtime.Path;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IProducer;

/**
 * @author Viacheslav Kabanovich
 */
public class SelectedAlternativeTest extends TCKTest {

	/**
	  * 5.1.1. Declaring selected alternatives for a bean archive
	  * By default, a bean archive has no selected alternatives. An alternative must be explicitly declared using the
	  * <alternatives> element of the beans.xml file of the bean archive. The <alternatives> element contains a list of bean
	  * classes and stereotypes. An alternative is selected for the bean archive if either:
	  * 
	  * • the alternative is a managed bean or session bean and the bean class of the bean is listed,
	 */
	public void testSelectedAlternativeManagedBean() {
		IClassBean bean = getClassBean("JavaSource/org/jboss/jsr299/tck/tests/policy/broken/same/type/twice/Cat.java");
		assertTrue(bean.isSelectedAlternative());
	}

	/**
	 * • any @Alternative stereotype of the alternative is listed.
	 */
	public void testSelectedAlternativeStereotype() {
		IClassBean bean = getClassBean("JavaSource/org/jboss/jsr299/tck/tests/policy/Bird.java");
		assertTrue(bean.isSelectedAlternative());
	}

	/**
	 * • the alternative is a producer method, field or resource, and the bean class that declares the method or field is listed
	 */
	public void testSelectedAlternativeProducer() {
		Set<IBean> beans = cdiProject.getBeans(new Path("/tck/JavaSource/org/jboss/jsr299/tck/tests/policy/EnabledSheepProducer.java"));
		int producerCount = 0;
		for (IBean bean: beans) {
			if(bean instanceof IProducer) {
				producerCount++;
				assertTrue(bean.isSelectedAlternative());
			}
		}
		assertEquals(2, producerCount);
	}

}