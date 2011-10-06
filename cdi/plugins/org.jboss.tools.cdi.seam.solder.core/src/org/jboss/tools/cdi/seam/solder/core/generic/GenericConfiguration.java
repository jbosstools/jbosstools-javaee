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
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.IQualifierDeclaration;
import org.jboss.tools.cdi.internal.core.impl.definition.AbstractMemberDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.AnnotationDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;
import org.jboss.tools.cdi.seam.solder.core.CDISeamSolderCorePlugin;
import org.jboss.tools.common.java.IAnnotationDeclaration;
import org.jboss.tools.common.java.IParametedType;
import org.jboss.tools.common.util.EclipseJavaUtil;

/**
 * This class collects objects bound by one generic configuration:
 * 1) Generic configuration annotation - annotation type annotatid with GenericType annotation;
 * 2) Generic configuration type - type as declared by value of GenericType annotation;
 * 3) Generic configuration points - beans annotated with generic configuration annotation;
 * 4) Generic beans - beans annotated with GenericConfiguration annotation; 
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class GenericConfiguration {
	/**
	 * Generic configuration annotation type name.
	 */
	String genericTypeName;

	/**
	 * Generic configuration annotation type definition.
	 */
	AnnotationDefinition genericType;

	/**
	 * Generic configuration type declared by generic configuration annotation.
	 */
	IParametedType configType;

	/**
	 * Generic Configuration point is a bean annotated with 'generic configuration annotation'.
	 * This field maps definitions of such beans to their qualifiers for quick reference. 
	 */
	Map<AbstractMemberDefinition, List<IQualifierDeclaration>> genericConfigurationPoints = new HashMap<AbstractMemberDefinition, List<IQualifierDeclaration>>();

	/**
	 * Generic Bean is bean class annotated with GenericConfiguration annotation.
	 */
	Set<TypeDefinition> genericBeans = new HashSet<TypeDefinition>();

	Set<IPath> involvedResources = new HashSet<IPath>();

	public GenericConfiguration(String genericTypeName) {
		this.genericTypeName = genericTypeName;
	}

	public void setGenericTypeDefinition(AnnotationDefinition genericType, GenericBeanDefinitionContext context) {
		this.genericType = genericType;
		IAnnotationDeclaration g = genericType.getAnnotation(context.getVersion().getGenericTypeAnnotationTypeName());
		Object o = g.getMemberValue(null);
		if(o != null) {
			String configTypeName = EclipseJavaUtil.resolveType(genericType.getType(), o.toString());
			try {
				configType = context.getRootContext().getProject().getTypeFactory().getParametedType(genericType.getType(), "Q" + o.toString() + ";");
			} catch (JavaModelException e) {
				CDISeamSolderCorePlugin.getDefault().logError(e);
			}
		}
	}

	public void clear(IPath path) {
		involvedResources.remove(path);
	}

	public void clear(String typeName) {
		Iterator<AbstractMemberDefinition> it = genericConfigurationPoints.keySet().iterator();
		while(it.hasNext()) {
			if(typeName.equals(it.next().getTypeDefinition().getQualifiedName())) {
				it.remove();
			}
		}
		Iterator<TypeDefinition> it2 = genericBeans.iterator();
		while(it2.hasNext()) {
			if(typeName.equals(it2.next().getTypeDefinition().getQualifiedName())) {
				it2.remove();
			}
		}
	}

	/**
	 * Returns map of definition to qualifiers for generic configuration points.
	 * 
	 * @return map of definition to qualifiers for generic configuration points
	 */
	public Map<AbstractMemberDefinition, List<IQualifierDeclaration>> getGenericConfigurationPoints() {
		return genericConfigurationPoints;
	}

	public String getGenericTypeName() {
		return genericTypeName;
	}

	public AnnotationDefinition getGenericTypeDefinition() {
		return genericType;
	}

	public IParametedType getConfigType() {
		return configType;
	}

	/**
	 * Returns set of generic beans.
	 * 
	 * @return set of generic beans
	 */
	public Set<TypeDefinition> getGenericBeans() {
		return genericBeans;
	}

	public Set<IPath> getInvolvedTypes() {
		return involvedResources;
	}

}
