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


import java.beans.Introspector;

import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDIUtil;
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IProducerField;
import org.jboss.tools.cdi.core.IProducerMethod;
import org.jboss.tools.cdi.core.extension.feature.IBeanNameFeature;
import org.jboss.tools.cdi.internal.core.impl.AbstractBeanElement;
import org.jboss.tools.cdi.internal.core.impl.AnnotationDeclaration;
import org.jboss.tools.cdi.internal.core.impl.definition.AbstractMemberDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.AbstractTypeDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.PackageDefinition;
import org.jboss.tools.common.util.BeanUtil;
import org.jboss.tools.common.util.EclipseJavaUtil;

/**
 * 
 * @author Viacheslav Kabanovich
 * 
 */
public class BeanNameFeature implements IBeanNameFeature {
	/**
	 * The singleton instance that processes requests without building inner
	 * state.
	 */
	public static final IBeanNameFeature instance = new BeanNameFeature();

	public String computeBeanName(IBean bean) {
		AbstractBeanElement abe = (AbstractBeanElement)bean;
		AbstractMemberDefinition d = abe.getDefinition();
		if(d == null) return null;
		IAnnotationDeclaration named = CDIUtil.getNamedDeclaration(bean);

		AbstractTypeDefinition t = d.getTypeDefinition();
		PackageDefinition p = d.getPackageDefinition();
		AnnotationDeclaration namedOnPackage = null;
		AnnotationDeclaration fullyQualifiedOnPackage = null;
		if(p != null) {
			namedOnPackage = p.getAnnotation(CDIConstants.NAMED_QUALIFIER_TYPE_NAME);
			fullyQualifiedOnPackage = p.getAnnotation(CDISolderConstants.FULLY_QUALIFIED_ANNOTATION_TYPE_NAME);
		}

		AnnotationDeclaration fullyQualified = d.getAnnotation(CDISolderConstants.FULLY_QUALIFIED_ANNOTATION_TYPE_NAME);
		
		//@FullyQualified
		if((fullyQualified != null || fullyQualifiedOnPackage != null) && (named != null || namedOnPackage != null)) {
			if(named == null) named = namedOnPackage;
			String pkg = resolvePackageName(fullyQualified, fullyQualifiedOnPackage, t, p);
			String simpleName = getSimpleBeanName(bean, named);
			return (simpleName == null) ? null : pkg.length() > 0 ? pkg + "." + simpleName : simpleName;			
		}

		// @Named on package only
		if(named == null && namedOnPackage != null) {
			return getSimpleBeanName(bean, namedOnPackage);
		}

		return null;
	}

	private String getStringValue(IAnnotationDeclaration a) {
		if(a == null) return null;
		Object o = a.getMemberValue(null);
		return o == null ? null : o.toString();
	}

	private String resolvePackageName(AnnotationDeclaration fullyQualified, AnnotationDeclaration fullyQualifiedOnPackage, AbstractTypeDefinition t, PackageDefinition p) {
		String contextClass = null;
		AnnotationDeclaration a = fullyQualified != null ? fullyQualified : fullyQualifiedOnPackage;
		contextClass = getStringValue(a);
		if(contextClass == null) {
			contextClass = t == null ? "" : t.getQualifiedName();
		} else if(fullyQualified != null && t != null) {
			String resolved = EclipseJavaUtil.resolveType(t.getType(), contextClass);
			if(resolved != null) contextClass = resolved;				
		} else if(fullyQualifiedOnPackage != null) {
			contextClass = p.resolveType(contextClass);
		}
		int dot = contextClass.lastIndexOf('.');
		return dot < 0 ? "" : contextClass.substring(0, dot);
	}

	private String getSimpleBeanName(IBean bean, IAnnotationDeclaration named) {
		String simpleName = null;
		if(named != null) {
			simpleName = getStringValue(named);
		}
		if(simpleName != null && simpleName.length() > 0) {
			//do nothing
		} else if(bean instanceof IClassBean) {
			simpleName = Introspector.decapitalize(((IClassBean)bean).getBeanClass().getElementName());
		} else if(bean instanceof IProducerField) {
			simpleName = ((IProducerField)bean).getField().getElementName();
		} else if(bean instanceof IProducerMethod) {
			IProducerMethod m = (IProducerMethod)bean;
			String mn = m.getMethod().getElementName();
			if(BeanUtil.isGetter(m.getMethod())) {
				simpleName = BeanUtil.getPropertyName(mn);
			} else {
				simpleName = mn;
			}
		}
		
		return simpleName;
	}
}
