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
import java.util.HashSet;
import java.util.Set;

import org.jboss.tools.common.text.INodeReference;

public class BeansXMLData {

	private Collection<INodeReference> interceptors = new ArrayList<INodeReference>();
	private Collection<INodeReference> decorators = new ArrayList<INodeReference>();
	private Collection<INodeReference> stereotypeAlternatives = new ArrayList<INodeReference>();
	private Collection<INodeReference> typeAlternatives = new ArrayList<INodeReference>();

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

	public Collection<INodeReference> getInterceptors() {
		return interceptors;
	}

	public Collection<INodeReference> getDecorators() {
		return decorators;
	}

	public Collection<INodeReference> getStereotypeAlternatives() {
		return stereotypeAlternatives;
	}

	public Collection<INodeReference> getTypeAlternatives() {
		return typeAlternatives;
	}

	public Collection<String> getInterceptorTypes() {
		return interceptorTypes;
	}

	public Collection<String> getDecoratorTypes() {
		return decoratorTypes;
	}

	public Collection<String> getStereotypeAlternativeTypes() {
		return stereotypeAlternativeTypes;
	}

	public Collection<String> getTypeAlternativeTypes() {
		return typeAlternativeTypes;
	}

	public synchronized void addInterceptor(INodeReference r) {
		interceptors.add(r);
		if (r.getValue() != null) {
			interceptorTypes.add(r.getValue());
		}
	}

	public synchronized void addDecorator(INodeReference r) {
		decorators.add(r);
		if (r.getValue() != null) {
			decoratorTypes.add(r.getValue());
		}
	}

	public synchronized void addStereotypeAlternative(INodeReference r) {
		stereotypeAlternatives.add(r);
		if (r.getValue() != null) {
			stereotypeAlternativeTypes.add(r.getValue());
		}
	}

	public synchronized void addTypeAlternative(INodeReference r) {
		typeAlternatives.add(r);
		if (r.getValue() != null) {
			typeAlternativeTypes.add(r.getValue());
		}
	}

}
