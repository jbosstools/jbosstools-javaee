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
package org.jboss.tools.cdi.internal.core.impl.definition;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.IParametedType;
import org.jboss.tools.common.model.util.EclipseJavaUtil;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class AbstractTypeDefinition extends AbstractMemberDefinition {
	protected String qualifiedName;
	protected IType type;
	protected Set<IParametedType> inheritedTypes = new HashSet<IParametedType>();

	public AbstractTypeDefinition() {}

	public String getQualifiedName() {
		return qualifiedName;
	}

	public IType getType() {
		return type;
	}

	public void setType(IType type, DefinitionContext context) {
		super.setAnnotatable(type, type, context);
	}

	@Override
	protected void init(IType contextType, DefinitionContext context) throws CoreException {
		this.type = contextType;
		super.init(contextType, context);
		qualifiedName = getType().getFullyQualifiedName();
		String sc = type.getSuperclassTypeSignature();
		if(sc != null) {
			//TODO process parameter types correctly!!!
			sc = EclipseJavaUtil.resolveType(contextType, sc);
			if(sc != null && sc.length() > 0) {
				IType t = EclipseJavaUtil.findType(contextType.getJavaProject(), sc);
//TODO
//				if(t != null) inheritedTypes.add(t);
			}
		}
		String[] is = type.getSuperInterfaceTypeSignatures();
		if(is != null) for (int i = 0; i < is.length; i++) {
			String c = EclipseJavaUtil.resolveType(contextType, is[i]);
			if(c != null && c.length() > 0) {
				IType t = EclipseJavaUtil.findType(contextType.getJavaProject(), c);
//TODO
//				if(t != null) inheritedTypes.add(t);
			}
		}
	}

	public Set<IParametedType> getInheritedTypes() {
		return inheritedTypes;
	}

}
