/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.internal.core.impl;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IBeanMember;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.ITypeDeclaration;
import org.jboss.tools.cdi.internal.core.impl.definition.BeanMemberDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.ParametedTypeFactory;
import org.jboss.tools.common.model.util.EclipseJavaUtil;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public abstract class BeanMember extends AbstractBeanElement implements IBeanMember {
	protected IClassBean classBean;
	protected ITypeDeclaration typeDeclaration;

	public BeanMember() {}

	public BeanMemberDefinition getDefinition() {
		return (BeanMemberDefinition)definition;
	}

	protected void setMember(IMember member) {
		try {
			String returnType = member instanceof IField ? ((IField)member).getTypeSignature()
					: member instanceof IMethod ? ((IMethod)member).getReturnType() : null;
			if(returnType != null) {
				ParametedType p = ParametedTypeFactory.getParametedType(member.getDeclaringType(), returnType);
				if(p != null) {
					typeDeclaration = new TypeDeclaration(p, -1, 0);
				}
			}
		} catch (JavaModelException e) {
			CDICorePlugin.getDefault().logError(e);
		}
	}

	public IClassBean getClassBean() {
		return classBean;
	}

	public void setClassBean(ClassBean classBean) {
		this.classBean = classBean;
		setParent(classBean);
	}

	public IType getType() {
		return typeDeclaration == null ? null : typeDeclaration.getType();
	}

	public int getLength() {
		ISourceRange r = null;
		try {
			getSourceMember().getSourceRange();
		} catch (JavaModelException e) {
			CDICorePlugin.getDefault().logError(e);
		}
		return r == null ? 0 : r.getLength();
	}

	public int getStartPosition() {
		ISourceRange r = null;
		try {
			getSourceMember().getSourceRange();
		} catch (JavaModelException e) {
			CDICorePlugin.getDefault().logError(e);
		}
		return r == null ? 0 : r.getOffset();
	}

}
