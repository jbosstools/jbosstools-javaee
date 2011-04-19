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
package org.jboss.tools.cdi.seam.solder.core;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IJavaAnnotation;
import org.jboss.tools.cdi.core.IProducer;
import org.jboss.tools.cdi.core.IRootDefinitionContext;
import org.jboss.tools.cdi.core.extension.ICDIExtension;
import org.jboss.tools.cdi.core.extension.feature.IAmbiguousBeanResolverFeature;
import org.jboss.tools.cdi.core.extension.feature.IProcessAnnotatedTypeFeature;
import org.jboss.tools.cdi.internal.core.impl.AnnotationLiteral;
import org.jboss.tools.cdi.internal.core.impl.definition.AbstractMemberDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.FieldDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.MethodDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;

/**
 * Implements support for org.jboss.seam.solder.bean.defaultbean.DefaultBeanExtension.
 * 
 * In processing annotated type adds to each bean definition, which is a default bean, 
 * faked @Typed annotation with type set by @DefaultBean.
 * 
 * In resolving ambiguous beans removes default beans out of the result set if it 
 * contains at least one non-default bean;
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class CDISeamSolderDefaultBeanExtension implements ICDIExtension, IProcessAnnotatedTypeFeature, IAmbiguousBeanResolverFeature {

	public void processAnnotatedType(TypeDefinition typeDefinition, IRootDefinitionContext context) {
		boolean defaultBean = typeDefinition.isAnnotationPresent(CDISeamSolderConstants.DEFAULT_BEAN_ANNOTATION_TYPE_NAME);
		IJavaAnnotation beanTyped = null;
		if(defaultBean) {
			beanTyped = createFakeTypedAnnotation(typeDefinition, context);
			if(beanTyped != null) {
				typeDefinition.addAnnotation(beanTyped, context);
			}
		}
		List<MethodDefinition> ms = typeDefinition.getMethods();
		for (MethodDefinition m: ms) {
			if(m.isAnnotationPresent(CDIConstants.PRODUCES_ANNOTATION_TYPE_NAME)) {
				if(defaultBean || m.isAnnotationPresent(CDISeamSolderConstants.DEFAULT_BEAN_ANNOTATION_TYPE_NAME)) {
					IJavaAnnotation methodTyped = createFakeTypedAnnotation(m, context);
					if(methodTyped != null) {
						m.addAnnotation(methodTyped, context);
					}
				}
			}
		}
		List<FieldDefinition> fs = typeDefinition.getFields();
		for (FieldDefinition f: fs) {
			if(f.isAnnotationPresent(CDIConstants.PRODUCES_ANNOTATION_TYPE_NAME)) {
				if(defaultBean || f.isAnnotationPresent(CDISeamSolderConstants.DEFAULT_BEAN_ANNOTATION_TYPE_NAME)) {
					IJavaAnnotation fieldTyped = createFakeTypedAnnotation(f, context);
					if(fieldTyped != null) {
						f.addAnnotation(fieldTyped, context);
					}
				}
			}
		}
	}

	IJavaAnnotation createFakeTypedAnnotation(AbstractMemberDefinition def, IRootDefinitionContext context) {
		IAnnotationDeclaration a = def.getAnnotation(CDISeamSolderConstants.DEFAULT_BEAN_ANNOTATION_TYPE_NAME);
		if(a == null) return null;
		Object n = a.getMemberValue(null);
		String defaultType = null;
		if(n != null && n.toString().length() > 0) {
			defaultType = n.toString();
			IType typedAnnotation = context.getProject().getType(CDIConstants.TYPED_ANNOTATION_TYPE_NAME);
			return (typedAnnotation == null) ? null 
				: new AnnotationLiteral(def.getResource(), a.getStartPosition(), a.getLength(), defaultType, IMemberValuePair.K_CLASS, typedAnnotation);
		}
		return null;
	 
	}

	public Set<IBean> getResolvedBeans(Set<IBean> result) {
		Set<IBean> defaultBeans = new HashSet<IBean>();
		for (IBean b: result) {
			if(b.getAnnotation(CDISeamSolderConstants.DEFAULT_BEAN_ANNOTATION_TYPE_NAME) != null) {
				defaultBeans.add(b);
			} else if(b instanceof IProducer) {
				IProducer producer = (IProducer)b;
				IClassBean parent = producer.getClassBean();
				if(parent != null && parent.getAnnotation(CDISeamSolderConstants.DEFAULT_BEAN_ANNOTATION_TYPE_NAME) != null) {
					defaultBeans.add(b);
				}
			}
		}
		if(!defaultBeans.isEmpty() && defaultBeans.size() < result.size()) {
			result.removeAll(defaultBeans);
		}
		return result;
	}

}
