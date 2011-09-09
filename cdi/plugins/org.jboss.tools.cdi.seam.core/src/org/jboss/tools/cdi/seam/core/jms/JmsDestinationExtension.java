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
package org.jboss.tools.cdi.seam.core.jms;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.CDIUtil;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.extension.ICDIExtension;
import org.jboss.tools.cdi.core.extension.feature.IInjectionPointValidatorFeature;
import org.jboss.tools.common.java.IAnnotationDeclaration;

/**
 * @author Alexey Kazakov
 */
public class JmsDestinationExtension implements ICDIExtension, IInjectionPointValidatorFeature {

	private static final Set<String> INJECTION_TYPES = new HashSet<String>();
	static{
		INJECTION_TYPES.add("javax.jms.Connection");
		INJECTION_TYPES.add("javax.jms.Session");
		INJECTION_TYPES.add("javax.jms.Topic");
		INJECTION_TYPES.add("javax.jms.Queue");
		INJECTION_TYPES.add("javax.jms.TopicPublisher");
		INJECTION_TYPES.add("javax.jms.QueueSender");
		INJECTION_TYPES.add("javax.jms.TopicSubscriber");
		INJECTION_TYPES.add("javax.jms.QueueReceiver");
	}

	private static final String JMS_DESTINATION_QUALIFIER = "org.jboss.seam.jms.annotations.JmsDestination";

	/**
	 * The following JMS resources are available for injection in Seam JMS:
	 * 
	 * javax.jms.Connection
	 * javax.jms.Session
	 * 
	 * Destination-based resources:
	 * 
	 * javax.jms.Topic
	 * javax.jms.Queue
	 * javax.jms.TopicPublisher
	 * javax.jms.QueueSender
	 * javax.jms.TopicSubscriber
	 * javax.jms.QueueReceiver
	 * 
	 * If an injection has any type from the list above and also it has a qualifier JmsDestination then it should be ignored by CDI validator.
	 * 
	 * See https://issues.jboss.org/browse/JBIDE-9685
	 */
	@Override
	public boolean shouldIgnoreInjection(IType typeOfInjectionPoint, IInjectionPoint injection) {
		if(typeOfInjectionPoint!=null && INJECTION_TYPES.contains(typeOfInjectionPoint.getFullyQualifiedName())) {
			IAnnotationDeclaration declaration = CDIUtil.getAnnotationDeclaration(injection, JMS_DESTINATION_QUALIFIER);
			return declaration!=null;
		}
		return false;
	}
}