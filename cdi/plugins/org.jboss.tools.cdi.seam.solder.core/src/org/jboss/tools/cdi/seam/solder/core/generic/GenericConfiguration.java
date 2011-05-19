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
package org.jboss.tools.cdi.seam.solder.core.generic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.internal.core.impl.definition.AbstractMemberDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.AnnotationDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class GenericConfiguration {
	String genericTypeName;
	AnnotationDefinition genericType;

	Map<AbstractMemberDefinition, List<IAnnotationDeclaration>> genericProducerBeans = new HashMap<AbstractMemberDefinition, List<IAnnotationDeclaration>>();
	Set<TypeDefinition> genericConfigurationBeans = new HashSet<TypeDefinition>();

	Set<IPath> involvedResources = new HashSet<IPath>();

	public GenericConfiguration(String genericTypeName) {
		this.genericTypeName = genericTypeName;
	}

	public void setGenericTypeDefinition(AnnotationDefinition genericType) {
		this.genericType = genericType;
	}

	public void clear(String typeName) {
		Iterator<AbstractMemberDefinition> it = genericProducerBeans.keySet().iterator();
		while(it.hasNext()) {
			if(typeName.equals(it.next().getTypeDefinition().getQualifiedName())) {
				it.remove();
			}
		}
		Iterator<TypeDefinition> it2 = genericConfigurationBeans.iterator();
		while(it2.hasNext()) {
			if(typeName.equals(it2.next().getTypeDefinition().getQualifiedName())) {
				it2.remove();
			}
		}
	}

	public Map<AbstractMemberDefinition, List<IAnnotationDeclaration>> getGenericProducerBeans() {
		return genericProducerBeans;
	}

	public String getGenericTypeName() {
		return genericTypeName;
	}

	public AnnotationDefinition getGenericTypeDefinition() {
		return genericType;
	}

	public Set<TypeDefinition> getGenericConfigurationBeans() {
		return genericConfigurationBeans;
	}

	public Set<IPath> getInvolvedTypes() {
		return involvedResources;
	}

}
