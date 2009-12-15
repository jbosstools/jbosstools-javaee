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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class MethodDefinition extends BeanMemberDefinition {
	IMethod method;
	boolean isConstructor;

	public MethodDefinition() {}

	public void setMethod(IMethod method, DefinitionContext context) {
		this.method = method;
		setAnnotatable(method, method.getDeclaringType(), context);
	}

	public IMethod getMethod() {
		return method;
	}

	public boolean isConstructor() {
		return isConstructor();
	}

	protected void init(IType contextType, DefinitionContext context) throws CoreException {
		super.init(contextType, context);
		isConstructor = method.isConstructor();
		//TODO process parameters for disposers and observers
		
	}

	public boolean isCDIAnnotated() {
		//TODO return true if it is disposer or observer
		return super.isCDIAnnotated();
	}

}
