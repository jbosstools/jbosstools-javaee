package org.jboss.tools.cdi.internal.core.impl;

import java.util.HashSet;
import java.util.Set;

import org.jboss.tools.common.text.INodeReference;

public class BeansXMLData {

	private Set<INodeReference> interceptors = new HashSet<INodeReference>();
	private Set<INodeReference> decorators = new HashSet<INodeReference>();
	private Set<INodeReference> stereotypeAlternatives = new HashSet<INodeReference>();
	private Set<INodeReference> typeAlternatives = new HashSet<INodeReference>();

	private Set<String> interceptorTypes = new HashSet<String>();
	private Set<String> decoratorTypes = new HashSet<String>();
	private Set<String> stereotypeAlternativeTypes = new HashSet<String>();
	private Set<String> typeAlternativeTypes = new HashSet<String>();

	public BeansXMLData() {}

	public void clean() {
		synchronized(interceptors) {
			interceptors.clear();
		}
		synchronized (decorators) {
			decorators.clear();
		}
		synchronized (stereotypeAlternatives) {
			stereotypeAlternatives.clear();
		}
		synchronized (typeAlternatives) {
			typeAlternatives.clear();
		}
		synchronized (interceptorTypes) {
			interceptorTypes.clear();
		}
		synchronized (decoratorTypes) {
			decoratorTypes.clear();
		}
		synchronized (stereotypeAlternativeTypes) {
			stereotypeAlternativeTypes.clear();
		}
		synchronized (typeAlternativeTypes) {
			typeAlternativeTypes.clear();
		}
	}

	public Set<INodeReference> getInterceptors() {
		return interceptors;
	}

	public Set<INodeReference> getDecorators() {
		return decorators;
	}

	public Set<INodeReference> getStereotypeAlternatives() {
		return stereotypeAlternatives;
	}

	public Set<INodeReference> getTypeAlternatives() {
		return typeAlternatives;
	}

	public Set<String> getInterceptorTypes() {
		return interceptorTypes;
	}

	public Set<String> getDecoratorTypes() {
		return decoratorTypes;
	}

	public Set<String> getStereotypeAlternativeTypes() {
		return stereotypeAlternativeTypes;
	}

	public Set<String> getTypeAlternativeTypes() {
		return typeAlternativeTypes;
	}

	public void addInterceptor(INodeReference r) {
		synchronized (interceptors) {
			interceptors.add(r);
		}
		if (r.getValue() != null)
			synchronized (interceptorTypes) {
				interceptorTypes.add(r.getValue());
			}
	}

	public void addDecorator(INodeReference r) {
		synchronized (decorators) {
			decorators.add(r);
		}
		synchronized (decoratorTypes) {
			decoratorTypes.add(r.getValue());
		}
	}

	public void addStereotypeAlternative(INodeReference r) {
		synchronized (stereotypeAlternatives) {
			stereotypeAlternatives.add(r);
		}
		synchronized (stereotypeAlternativeTypes) {
			stereotypeAlternativeTypes.add(r.getValue());
		}
	}

	public void addTypeAlternative(INodeReference r) {
		synchronized (typeAlternatives) {
			typeAlternatives.add(r);
		}
		synchronized (typeAlternativeTypes) {
			typeAlternativeTypes.add(r.getValue());
		}
	}

}
