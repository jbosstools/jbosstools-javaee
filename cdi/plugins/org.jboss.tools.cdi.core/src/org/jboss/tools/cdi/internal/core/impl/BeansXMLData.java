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
		synchronized(this) {
			interceptors.clear();
			decorators.clear();
			stereotypeAlternatives.clear();
			typeAlternatives.clear();
			interceptorTypes.clear();
			decoratorTypes.clear();
			stereotypeAlternativeTypes.clear();
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
		synchronized (this) {
			interceptors.add(r);
		}
		if (r.getValue() != null) {
			synchronized (this) {
				interceptorTypes.add(r.getValue());
			}
		}
	}

	public synchronized void addDecorator(INodeReference r) {
		decorators.add(r);
		decoratorTypes.add(r.getValue());
	}

	public synchronized void addStereotypeAlternative(INodeReference r) {
		stereotypeAlternatives.add(r);
		stereotypeAlternativeTypes.add(r.getValue());
	}

	public synchronized void addTypeAlternative(INodeReference r) {
		typeAlternatives.add(r);
		typeAlternativeTypes.add(r.getValue());
	}

}
