/******************************************************************************* 
 * Copyright (c) 2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.deltaspike.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.jboss.tools.cdi.internal.core.impl.definition.AbstractTypeDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.AnnotationDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
@SuppressWarnings("restriction")
public class DeltaspikePartialbeanBindingConfiguration {
	String partialbeanBindingTypeName;
	AnnotationDefinition partialbeanBindingType;
	Map<String, TypeDefinition> partialBeans = new HashMap<String, TypeDefinition>();
	Map<String, TypeDefinition> invocationHandlers = new HashMap<String, TypeDefinition>();
	Map<String, TypeDefinition> invalidPartialBeans = new HashMap<String, TypeDefinition>();

	Set<IPath> involvedResources = new HashSet<IPath>();

	public DeltaspikePartialbeanBindingConfiguration(String partialbeanBindingTypeName) {
		this.partialbeanBindingTypeName = partialbeanBindingTypeName;
	}

	public void setPartialbeanBindingTypeDefinition(AnnotationDefinition partialbeanBindingType, DeltaspikePartialbeanDefinitionContext context) {
		this.partialbeanBindingType = partialbeanBindingType;
	}

	public void clear(IPath path) {
		involvedResources.remove(path);
	}

	public void clear(String typeName) {
		TypeDefinition t = partialBeans.remove(typeName);
		if(t != null) t.setBeanConstructor(false);
		invocationHandlers.remove(typeName);
		invalidPartialBeans.remove(typeName);
	}

	public String getPartialbeanBindingTypeName() {
		return partialbeanBindingTypeName;
	}

	public AnnotationDefinition getPartialbeanBindingTypeDefinition() {
		return partialbeanBindingType;
	}

	public Set<IPath> getInvolvedTypes() {
		return involvedResources;
	}

	public void addPartialBean(TypeDefinition partialBean) {
		partialBeans.put(partialBean.getQualifiedName(), partialBean);
	}

	public void addInvocationHandler(TypeDefinition handler) {
		invocationHandlers.put(handler.getQualifiedName(), handler);
	}

	public void addInvalidPartialBean(TypeDefinition partialBean) {
		invalidPartialBeans.put(partialBean.getQualifiedName(), partialBean);
	}

	public Map<String, TypeDefinition> getPartialBeans() {
		return partialBeans;
	}

	public Map<String, TypeDefinition> getInvocationHandlers() {
		return invocationHandlers;
	}

	public Map<String, TypeDefinition> getInvalidPartialBeans() {
		return invalidPartialBeans;
	}

}
