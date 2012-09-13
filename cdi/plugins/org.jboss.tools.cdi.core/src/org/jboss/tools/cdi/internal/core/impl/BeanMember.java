/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.internal.core.impl;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IBeanMember;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.internal.core.impl.definition.AbstractMemberDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.BeanMemberDefinition;
import org.jboss.tools.common.java.IParametedType;
import org.jboss.tools.common.java.ParametedType;
import org.jboss.tools.common.java.ParametedTypeFactory;
import org.jboss.tools.common.java.TypeDeclaration;
import org.jboss.tools.common.java.TypeDeclaration.Lazy;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public abstract class BeanMember extends AbstractBeanElement implements IBeanMember {
	protected IClassBean classBean;
	protected TypeDeclaration typeDeclaration;

	public BeanMember() {}

	@Override
	public BeanMemberDefinition getDefinition() {
		return (BeanMemberDefinition)definition;
	}

	protected void setMember(IJavaElement member) {
		typeDeclaration = getTypeDeclaration(getDefinition(), getCDIProject().getNature().getTypeFactory());
	}

	@Override
	public IJavaElement getSourceElement() {
		return (IJavaElement)getDefinition().getMember();
	}

	public static TypeDeclaration getTypeDeclaration(final AbstractMemberDefinition definition, ParametedTypeFactory typeFactory) {
		final IJavaElement member = (IJavaElement)definition.getMember();
		try {
			String returnType = null;
			IMember currentMember = null;
			if (member instanceof IField) {
				returnType = ((IField)member).getTypeSignature();
				currentMember = (IMember)member;
			} else if (member instanceof IMethod) {
				returnType = ((IMethod)member).getReturnType();
				currentMember = (IMember)member;
			} else if (member instanceof ILocalVariable) {
				returnType = ((ILocalVariable)member).getTypeSignature();
				currentMember = ((ILocalVariable)member).getDeclaringMember();
			}
			if(returnType != null) {
				ParametedType p = typeFactory.getParametedType(currentMember, returnType);
				if(p != null) {
					Lazy lazy = new Lazy() {						
						@Override
						public void init(TypeDeclaration d) {
							int offset = -1;
							int length = 0;
							String content = definition.getTypeDefinition().getContent();
							if(content != null) {
								ISourceRange sr = null;
								ISourceRange nr = null;
								try {
									sr = ((ISourceReference)member).getSourceRange();
									nr = ((ISourceReference)member).getNameRange();
								} catch (JavaModelException e) {
									CDICorePlugin.getDefault().logError(e);
								}
								if(sr != null && nr != null && sr.getOffset() < nr.getOffset() && nr.getOffset() < content.length()) {
									String start = content.substring(sr.getOffset(), nr.getOffset());
									int off = -1;
									int off0 = -1;
									int bc = 0;
									for (int i = start.length() - 1; i >= 0; i--) {
										char ch = start.charAt(i);
										if(ch == '>') bc++; else if(ch == '<') bc--;
										if(Character.isWhitespace(ch)) {
											if(off >= 0 && bc <= 0) break;
										} else if(Character.isJavaIdentifierPart(ch) || ch == '.' || ch == '$' || ch == '<' || ch == '>') {
											off = i;
											if(off0 < 0) off0 = i + 1;
										}
									}
									if(off >= 0) {
										offset = sr.getOffset() + off;
										length = off0 - off;
									}
								}
							}
							d.init(offset, length);
						}
					};


					return new TypeDeclaration(p, member.getResource(), lazy);
				}
			}
		} catch (JavaModelException e) {
			CDICorePlugin.getDefault().logError(e);
		}
		return null;
	}

	/**
	 * These method is used to construct fake members that get all 
	 * annotations from this member, but should have another type;
	 * e.g. in implementation of Seam 3 persistence extension.
	 * @param d
	 */
	public void setTypeDeclaration(TypeDeclaration typeDeclaration) {
		this.typeDeclaration = typeDeclaration;
	}

	public TypeDeclaration getTypeDeclaration() {
		return typeDeclaration;
	}

	public IClassBean getClassBean() {
		return classBean;
	}

	public void setClassBean(ClassBean classBean) {
		this.classBean = classBean;
		setParent(classBean);
	}

	public IParametedType getType() {
		return typeDeclaration;
	}

	public IParametedType getMemberType() {
		return typeDeclaration;
	}

	public IResource getResource() {
		if(definition.getOriginalDefinition() != null) {
			return definition.getOriginalDefinition().getResource();
		}
		return super.getResource();
	}

	protected ISourceReference getSourceReference() {
		return getSourceMember();
	}

	protected ISourceRange getSourceRange() {
		ISourceRange result = null;
		try {
			result = getSourceReference().getSourceRange();
		} catch (JavaModelException e) {
			CDICorePlugin.getDefault().logError(e);
		}
		return result;
	}

	public int getLength() {
		if(definition.getOriginalDefinition() != null) {
			return definition.getOriginalDefinition().getLength();
		}
		ISourceRange r = getSourceRange();
		return r == null ? 0 : r.getLength();
	}

	public int getStartPosition() {
		if(definition.getOriginalDefinition() != null) {
			return definition.getOriginalDefinition().getStartPosition();
		}
		ISourceRange r = getSourceRange();
		return r == null ? 0 : r.getOffset();
	}

	public boolean isNullable() {
		return typeDeclaration == null ? false : !typeDeclaration.isPrimitive();
	}
}