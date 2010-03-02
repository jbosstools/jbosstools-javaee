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
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IBeanMember;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IParametedType;
import org.jboss.tools.cdi.internal.core.impl.definition.BeanMemberDefinition;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public abstract class BeanMember extends AbstractBeanElement implements IBeanMember {
	protected IClassBean classBean;
	protected TypeDeclaration typeDeclaration;

	public BeanMember() {}

	public BeanMemberDefinition getDefinition() {
		return (BeanMemberDefinition)definition;
	}

	protected void setMember(IMember member) {
		try {
			String returnType = member instanceof IField ? ((IField)member).getTypeSignature()
					: member instanceof IMethod ? ((IMethod)member).getReturnType() : null;
			if(returnType != null) {
				ParametedType p = getCDIProject().getNature().getTypeFactory().getParametedType(member.getDeclaringType(), returnType);
				if(p != null) {

					int offset = -1;
					int length = 0;
					String content = getDefinition().getTypeDefinition().getContent();
					if(content != null) {
						ISourceRange sr = member.getSourceRange();
						ISourceRange nr = member.getNameRange();
						if(sr != null && nr != null && sr.getOffset() < nr.getOffset() && nr.getOffset() < content.length()) {
							String start = content.substring(sr.getOffset(), nr.getOffset());
							int off = -1;
							int off0 = -1;
							for (int i = start.length() - 1; i >= 0; i--) {
								char ch = start.charAt(i);
								if(Character.isWhitespace(ch)) {
									if(off >= 0) break;
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

					typeDeclaration = new TypeDeclaration(p, offset, length);
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

	public IParametedType getType() {
		return typeDeclaration;
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
