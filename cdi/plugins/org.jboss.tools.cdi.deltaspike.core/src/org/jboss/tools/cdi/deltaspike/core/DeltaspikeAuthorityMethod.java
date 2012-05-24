/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.deltaspike.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.internal.core.impl.CDIProject;
import org.jboss.tools.cdi.internal.core.impl.definition.MethodDefinition;
import org.jboss.tools.common.java.IAnnotationDeclaration;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class DeltaspikeAuthorityMethod {
	MethodDefinition method;
	IPath path;
	String declaringTypeName;

	Map<SecurityBindingDeclaration, DeltaspikeSecurityBindingConfiguration> bindings = new HashMap<SecurityBindingDeclaration, DeltaspikeSecurityBindingConfiguration>();

	public DeltaspikeAuthorityMethod(MethodDefinition method) {
		this.method = method;
		IType type = method.getTypeDefinition().getType();
		if(type != null) {
			path = type.getPath();
			declaringTypeName = type.getFullyQualifiedName();
		}
	}

	public MethodDefinition getMethod() {
		return method;
	}

	public IPath getPath() {
		return path;
	}

	public String getDeclaringTypeName() {
		return declaringTypeName;
	}

	public void addBinding(SecurityBindingDeclaration d, DeltaspikeSecurityBindingConfiguration c) {
		bindings.put(d, c);
	}

	public Map<SecurityBindingDeclaration, DeltaspikeSecurityBindingConfiguration> getBindings() {
		return bindings;
	}

	public boolean isMatching(IAnnotationDeclaration d) throws CoreException {
		if(bindings.isEmpty()) {
			return false;
		}
		String key = CDIProject.getAnnotationDeclarationKey(d);
		for (SecurityBindingDeclaration d2: bindings.keySet()) {
			if(d2.getBinding().getTypeName().equals(d.getTypeName())) {
				if(key.equals(CDIProject.getAnnotationDeclarationKey(d2.getBinding()))) {
					return true;
				}
			}
		}
		return false;
	}
}
