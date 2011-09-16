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

import java.util.HashSet;
import java.util.Set;

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

	public Set<IQualifier> getQualifiers() {
		Set<IQualifier> result = new HashSet<IQualifier>();
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

	public Set<IObserverMethod> getObserverMethods() {
		return new HashSet<IObserverMethod>();
	}

	public Set<IProducer> getProducers() {
		return new HashSet<IProducer>();
	}

	public Set<IBeanMethod> getDisposers() {
		return new HashSet<IBeanMethod>();
	}
}
