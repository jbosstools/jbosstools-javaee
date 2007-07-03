 /*******************************************************************************
  * Copyright (c) 2007 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.seam.internal.core;

import org.eclipse.jdt.core.IMethod;
import org.jboss.tools.seam.core.ISeamAnnotatedFactory;
import org.jboss.tools.seam.core.ScopeType;

/**
 * @author Viacheslav Kabanovich
 */
public class SeamAnnotatedFactory implements ISeamAnnotatedFactory {
	IMethod method = null;
	String name = null;	
	ScopeType scopeType = ScopeType.UNSPECIFIED;

	public IMethod getSourceMethod() {
		return method;
	}
	
	public void setMethod(IMethod method) {
		this.method = method;
	}

	public String getName() {
		return name;
	}

	public ScopeType getScope() {
		return scopeType;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setScope(ScopeType type) {
		this.scopeType = type;
	}

}
