package org.jboss.tools.cdi.internal.core.impl;

import java.util.HashSet;
import java.util.Set;

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

	public Set<IParameter> getObservedParameters() {
		Set<IParameter> result = new HashSet<IParameter>();
		for (IParameter p: parameters) {
			if(p.isAnnotationPresent(CDIConstants.OBSERVERS_ANNOTATION_TYPE_NAME)) result.add(p);
		}
		return result;
	}

}
