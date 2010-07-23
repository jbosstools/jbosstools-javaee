package org.jboss.tools.cdi.internal.core.impl;

import java.util.HashSet;
import java.util.Set;

import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.IObserverMethod;
import org.jboss.tools.cdi.core.IParameter;

public class ObserverMethod extends BeanMethod implements IObserverMethod {

	@Override
	protected Parameter newParameter() {
		return new InjectionPointParameter();
	}

	public Set<IParameter> getObservedParameters() {
		Set<IParameter> result = new HashSet<IParameter>();
		for (IParameter p: parameters) {
			if(p.isAnnotationPresent(CDIConstants.OBSERVERS_ANNOTATION_TYPE_NAME)) result.add(p);
		}
		return result;
	}

}
