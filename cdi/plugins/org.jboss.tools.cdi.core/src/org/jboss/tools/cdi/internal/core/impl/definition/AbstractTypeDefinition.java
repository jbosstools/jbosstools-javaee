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
import org.jboss.tools.cdi.internal.core.impl.ParametedType;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class AbstractTypeDefinition extends AbstractMemberDefinition {
	protected String qualifiedName;
	protected IType type;
	protected ParametedType parametedType = null;

	Set<IParametedType> allInheritedTypes = null;
	
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
		parametedType = new ParametedType();
		parametedType.setType(this.type);
		parametedType.setSignature("Q" + qualifiedName + ";");
	}

	void buildAllInheritedTypes(Set<String> processed, ParametedType p) {
		IType t = p.getType();
		if(t == null) return;
		if(processed.contains(t.getFullyQualifiedName())) return;
		processed.add(t.getFullyQualifiedName());
		allInheritedTypes.add(p);
		Set<IParametedType> ts = p.getInheritedTypes();
		if(ts != null) for (IParametedType pp: ts) {
			buildAllInheritedTypes(processed, (ParametedType)pp);
		}
	}

	public Set<IParametedType> getInheritedTypes() {
		return parametedType == null ? new HashSet<IParametedType>() : parametedType.getInheritedTypes();
	}

	public Set<IParametedType> getAllInheritedTypes() {
		if(allInheritedTypes == null) {
			Set<String> processed = new HashSet<String>();
			buildAllInheritedTypes(processed, parametedType);
		}
		return allInheritedTypes;
	}

}
