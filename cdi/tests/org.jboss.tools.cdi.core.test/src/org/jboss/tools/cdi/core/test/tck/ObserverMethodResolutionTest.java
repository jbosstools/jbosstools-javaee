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
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.core.IInjectionPointParameter;
import org.jboss.tools.cdi.core.IObserverMethod;
import org.jboss.tools.cdi.core.IParameter;

/**
 * @author Viacheslav Kabanovich
 */
public class ObserverMethodResolutionTest extends TCKTest {

	public void testObserverMethodResolution() {
		IInjectionPointField tamingEvent =  getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/event/fires/DogWhisperer.java", "tamingEvent");
		assertNotNull(toString());

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
		assertTrue(observerParameter instanceof IInjectionPointParameter);

		Set<IInjectionPoint> points = tamedObserver.getClassBean().getCDIProject().findObservedEvents((IInjectionPointParameter)observerParameter);
		assertTrue(points.size() == 1);
		assertTrue(points.contains(tamingEvent));
	}

}