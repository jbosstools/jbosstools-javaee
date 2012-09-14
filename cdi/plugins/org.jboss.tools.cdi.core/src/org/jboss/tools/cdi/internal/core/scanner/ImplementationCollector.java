/******************************************************************************* 
 * Copyright (c) 2010 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.internal.core.scanner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;
import org.jboss.tools.common.java.IParametedType;

public class ImplementationCollector {
	List<TypeDefinition> typeDefinitions;
	Set<IType> decorators = new HashSet<IType>();
	Set<IType> interceptors = new HashSet<IType>();

	public ImplementationCollector(List<TypeDefinition> typeDefinitions) {
		this.typeDefinitions = typeDefinitions;
		try {
			process();
		} catch (JavaModelException e) {
			CDICorePlugin.getDefault().logError(e);
		}
	}

	void process() throws JavaModelException {
		for (TypeDefinition typeDef: typeDefinitions) {
			IType type = typeDef.getType();
			if(type == null || !type.exists() || type.isInterface()) continue;
			if(!mayBeRelevant(type)) continue;
			for (IParametedType t: typeDef.getInheritedTypes()) {
				IType q = t.getType();
				if(q == null) continue;
				String cn = q.getFullyQualifiedName();
				boolean isDecorator = CDIConstants.DECORATOR_TYPE_NAME.equals(cn);
				boolean isInterceptor = CDIConstants.INTERCEPTOR_TYPE_NAME.equals(cn);
				if(isDecorator || isInterceptor) {
					List<? extends IParametedType> ps = t.getParameters();
					if( ps != null) for (IParametedType p: ps) {
						IType pt = p.getType();
						if(pt != null) {
							if(isDecorator) {
								decorators.add(pt); 
							}
							if(isInterceptor) {
								interceptors.add(pt);
							}
						}
					}
				}
			}
		}
	}

	boolean mayBeRelevant(IType type) throws JavaModelException {
		String[] is = type.getSuperInterfaceNames();
		if(is != null) for (String s: is) {
			if(s.indexOf(CDIConstants.DECORATOR_SIMPLE_NAME) >= 0) return true;
			if(s.indexOf(CDIConstants.INTERCEPTOR_SIMPLE_NAME) >= 0) return true;
		}
		return false;
	}

	public boolean isDecorator(IType type) {
		return decorators.contains(type);
	}

	public boolean isInterceptor(IType type) {
		return interceptors.contains(type);
	}
}
