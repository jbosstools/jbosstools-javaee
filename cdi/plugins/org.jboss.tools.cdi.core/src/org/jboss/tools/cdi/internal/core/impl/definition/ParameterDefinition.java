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
package org.jboss.tools.cdi.internal.core.impl.definition;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.IRootDefinitionContext;
import org.jboss.tools.common.java.ParametedType;
import org.jboss.tools.common.java.TypeDeclaration;

public class ParameterDefinition extends BeanMemberDefinition {
	protected MethodDefinition methodDefinition;
	
	ILocalVariable variable;
	protected ParametedType type;
	protected TypeDeclaration overridenType;
	protected int index;

	public ParameterDefinition() {}

	public void setMethodDefinition(MethodDefinition methodDefinition) {
		this.methodDefinition = methodDefinition;
		typeDefinition = methodDefinition.getTypeDefinition();
	}

	public void setLocalVariable(ILocalVariable v, IRootDefinitionContext context, int flags) {
		variable = v;
		super.setAnnotatable(v, v.getDeclaringMember().getDeclaringType(), context, flags);
	}

	@Override
	protected void init(IType contextType, IRootDefinitionContext context, int flags) throws CoreException {
		super.init(contextType, context, flags);
		type = context.getProject().getTypeFactory().getParametedType(variable.getDeclaringMember(), variable.getTypeSignature());
	}

	public String getName() {
		return variable.getElementName();
	}

	public ParametedType getType() {
		return type;
	}

	public TypeDeclaration getOverridenType() {
		return overridenType;
	}

	public void setOverridenType(TypeDeclaration overridenType) {
		this.overridenType = overridenType;
	}

	public MethodDefinition getMethodDefinition() {
		return methodDefinition;
	}

	public ILocalVariable getVariable() {
		return variable;
	}

}