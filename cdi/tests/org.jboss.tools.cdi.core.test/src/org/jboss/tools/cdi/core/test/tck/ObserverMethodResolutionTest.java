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

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IMethod;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IInitializerMethod;
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

	public void testNonrelevantInjectionPoint() {
		IInjectionPointField tamingEvent =  getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/event/fires/DogWhisperer.java", "tamingCommand");
		Collection<IObserverMethod> observers = tamingEvent.getCDIProject().resolveObserverMethods(tamingEvent);
		assertTrue(observers.isEmpty());
	}

	/**
	 * Event injection point has no qualifiers.
	 */
	public void testObserverMethodResolution0() {
		IInjectionPointField generalEvent =  getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/event/fires/DogWhisperer.java", "generalEvent");
		assertNotNull(generalEvent);

		Collection<IObserverMethod> observers = generalEvent.getCDIProject().resolveObserverMethods(generalEvent);
		IObserverMethod tamedObserver = null;
		IObserverMethod recognizedFriendObserver = null;
		for (IObserverMethod m: observers) {
			IMethod jm = m.getMethod();
			if("tamed".equals(jm.getElementName())) {
				tamedObserver = m;
			} else if("recognizedFriend".equals(jm.getElementName())) {
				recognizedFriendObserver = m;
			}
		}
		assertNull(tamedObserver);
		assertNull(recognizedFriendObserver);
	}

	public void testObserverMethodResolution() {
		IInjectionPointField tamingEvent =  getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/event/fires/DogWhisperer.java", "tamingEvent");
		assertNotNull(tamingEvent);

		Collection<IObserverMethod> observers = tamingEvent.getCDIProject().resolveObserverMethods(tamingEvent);
		assertFalse(observers.isEmpty());

		IObserverMethod tamedObserver = null;
		IObserverMethod recognizedFriendObserver = null;
		for (IObserverMethod m: observers) {
			IMethod jm = m.getMethod();
			if("tamed".equals(jm.getElementName())) {
				tamedObserver = m;
			} else if("recognizedFriend".equals(jm.getElementName())) {
				recognizedFriendObserver = m;
			}
		}
		assertNotNull(tamedObserver);
		assertNull(recognizedFriendObserver);

		Collection<IParameter> p = tamedObserver.getObservedParameters();
		assertTrue(p.size() == 1);

		IParameter observerParameter = p.iterator().next();
		assertFalse(observerParameter instanceof IInjectionPointParameter);

		Collection<IInjectionPoint> points = tamedObserver.getClassBean().getCDIProject().findObservedEvents(observerParameter);
		assertTrue(points.size() == 1);
		assertTrue(points.contains(tamingEvent));
	}

	public void testObserverMethodResolution2() {
		IInjectionPointField solicitingEvent =  getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/event/fires/DogWhisperer.java", "solicitingEvent");
		assertNotNull(solicitingEvent);

		Collection<IObserverMethod> observers = solicitingEvent.getCDIProject().resolveObserverMethods(solicitingEvent);

		IObserverMethod tamedObserver = null;
		IObserverMethod recognizedFriendObserver = null;
		for (IObserverMethod m: observers) {
			IMethod jm = m.getMethod();
			if("tamed".equals(jm.getElementName())) {
				tamedObserver = m;
			} else if("recognizedFriend".equals(jm.getElementName())) {
				recognizedFriendObserver = m;
			} else {
				System.out.println(jm);
			}
		}
		assertNull(tamedObserver);
		assertNull(recognizedFriendObserver);


	}

	public void testObserverMethodResolution3() {
		IInjectionPointField friendlyEvent =  getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/event/fires/DogWhisperer.java", "friendlyEvent");
		assertNotNull(friendlyEvent);

		Collection<IObserverMethod> observers = friendlyEvent.getCDIProject().resolveObserverMethods(friendlyEvent);
		assertFalse(observers.isEmpty());

		IObserverMethod tamedObserver = null;
		IObserverMethod recognizedFriendObserver = null;
		for (IObserverMethod m: observers) {
			IMethod jm = m.getMethod();
			if("tamed".equals(jm.getElementName())) {
				tamedObserver = m;
			} else if("recognizedFriend".equals(jm.getElementName())) {
				recognizedFriendObserver = m;
			}
		}
		assertNull(tamedObserver);
		assertNotNull(recognizedFriendObserver);

		Collection<IParameter> p = recognizedFriendObserver.getObservedParameters();
		assertTrue(p.size() == 1);

		IParameter observerParameter = p.iterator().next();
		assertFalse(observerParameter instanceof IInjectionPointParameter);

		Collection<IInjectionPoint> points = recognizedFriendObserver.getClassBean().getCDIProject().findObservedEvents(observerParameter);
		assertTrue(points.size() == 1);
		assertTrue(points.contains(friendlyEvent));
	}

	public void testResolveObserverMethod() {
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/event/fires/DogWhisperer.java");
		Collection<IBean> beans = cdiProject.getBeans(file.getFullPath());
		IClassBean cb = null;
		for (IBean b: beans) {
			if(b instanceof IClassBean) {
				cb = (IClassBean)b;
			}
		}
		assertNotNull(cb);
		Collection<IInitializerMethod> ps = cb.getInitializers();
		IInitializerMethod mp = ps.isEmpty() ? null : ps.iterator().next();
		assertNotNull(mp);
		assertEquals("foo", mp.getMethod().getElementName());
		//no exception should happen on invoking resolveObserverMethods
//		cdiProject.resolveObserverMethods(mp); not applicable to initializer method		
	}


	public void testEventBean() {
		IInjectionPointField tamingEvent =  getInjectionPointField("JavaSource/org/jboss/jsr299/tck/tests/event/fires/DogWhisperer.java", "tamingEvent");
		assertNotNull(tamingEvent);
		
		Collection<IBean> beans = tamingEvent.getCDIProject().getBeans(false, tamingEvent);
		assertFalse(beans.isEmpty());

		IBean b = beans.iterator().next();
		Collection<IQualifier> qs = b.getQualifiers();
		assertEquals(3, qs.size());
		
	}

}