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
package org.jboss.tools.cdi.internal.core.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.IBeanMethod;
import org.jboss.tools.cdi.core.IObserverMethod;
import org.jboss.tools.cdi.core.IProducer;
import org.jboss.tools.cdi.core.IQualifier;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class NewBean extends ClassBean {
	
	protected void computeScope() {
		scope = getCDIProject().getScope(CDIConstants.DEPENDENT_ANNOTATION_TYPE_NAME);
	}

	public boolean isEnabled() {
		return true;
	}

	public Collection<IQualifier> getQualifiers() {
		Collection<IQualifier> result = new ArrayList<IQualifier>(1);
		IQualifier q = getCDIProject().getQualifier(CDIConstants.NEW_QUALIFIER_TYPE_NAME);
		if(q != null) {
			result.add(q);
		}
		return result;
	}

	public boolean isAlternative() {
		return false;
	}

	public boolean isSelectedAlternative() {
		return false;
	}

	public Collection<IObserverMethod> getObserverMethods() {
		return new ArrayList<IObserverMethod>();
	}

	public Collection<IProducer> getProducers() {
		return new ArrayList<IProducer>();
	}

	public Collection<IBeanMethod> getDisposers() {
		return new ArrayList<IBeanMethod>();
	}
}
