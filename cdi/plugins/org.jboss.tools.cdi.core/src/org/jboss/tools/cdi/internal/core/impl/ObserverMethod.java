/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.internal.core.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.IObserverMethod;
import org.jboss.tools.cdi.core.IParameter;
import org.jboss.tools.cdi.internal.core.impl.definition.ParameterDefinition;

public class ObserverMethod extends BeanMethod implements IObserverMethod {

	@Override
	protected Parameter newParameter(ParameterDefinition p) {
		if(p.isAnnotationPresent(CDIConstants.OBSERVERS_ANNOTATION_TYPE_NAME)) {
			return new Parameter();
		}
		return new InjectionPointParameter();
	}

	public Collection<IParameter> getObservedParameters() {
		Collection<IParameter> result = new ArrayList<IParameter>(parameters.size());
		for (IParameter p: parameters) {
			if(p.isAnnotationPresent(CDIConstants.OBSERVERS_ANNOTATION_TYPE_NAME)) result.add(p);
		}
		return result;
	}

}
