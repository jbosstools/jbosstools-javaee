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

import org.eclipse.jdt.core.IMethod;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.core.IInjectionPointParameter;
import org.jboss.tools.cdi.core.IObserverMethod;
import org.jboss.tools.cdi.core.IParameter;
import org.jboss.tools.cdi.core.IQualifier;

/**
 * @author Viacheslav Kabanovich
 */
public class ObserverMethodResolutionTest extends TCKTest {

	public void testObserverMethodResolution() {
		IInjectionPointField tamingEvent =  getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/event/fires/DogWhisperer.java", "tamingEvent");
		assertNotNull(tamingEvent);

		Set<IObserverMethod> observers = tamingEvent.getCDIProject().resolveObserverMethods(tamingEvent);
		assertFalse(observers.isEmpty());

		IObserverMethod tamedObserver = null;
		for (IObserverMethod m: observers) {
			IMethod jm = m.getMethod();
			if("tamed".equals(jm.getElementName())) {
				tamedObserver = m;
			}
		}
		assertNotNull(tamedObserver);

		Set<IParameter> p = tamedObserver.getObservedParameters();
		assertTrue(p.size() == 1);

		IParameter observerParameter = p.iterator().next();
		assertFalse(observerParameter instanceof IInjectionPointParameter);

		Set<IInjectionPoint> points = tamedObserver.getClassBean().getCDIProject().findObservedEvents(observerParameter);
		assertTrue(points.size() == 1);
		assertTrue(points.contains(tamingEvent));
	}

	public void testEventBean() {
		IInjectionPointField tamingEvent =  getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/event/fires/DogWhisperer.java", "tamingEvent");
		assertNotNull(tamingEvent);
		
		Set<IBean> beans = tamingEvent.getCDIProject().getBeans(false, tamingEvent);
		assertFalse(beans.isEmpty());

		IBean b = beans.iterator().next();
		Set<IQualifier> qs = b.getQualifiers();
		assertEquals(3, qs.size());
		
	}

}