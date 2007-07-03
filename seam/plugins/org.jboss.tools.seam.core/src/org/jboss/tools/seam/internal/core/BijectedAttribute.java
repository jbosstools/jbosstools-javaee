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

import org.eclipse.jdt.core.IMember;
import org.jboss.tools.seam.core.BijectedAttributeType;
import org.jboss.tools.seam.core.IBijectedAttribute;
import org.jboss.tools.seam.core.ScopeType;

/**
 * @author Viacheslav Kabanovich
 */
public class BijectedAttribute implements IBijectedAttribute {
	IMember javaSource = null;
	BijectedAttributeType type = null;
	String name = null;
	ScopeType scopeType = ScopeType.UNSPECIFIED;

	public IMember getJavaSource() {
		return javaSource;
	}
	
	public void setMember(IMember javaSource) {
		this.javaSource = javaSource;
	}

	public BijectedAttributeType getType() {
		return type;
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
