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

package org.jboss.tools.seam.internal.core;

import org.jboss.tools.seam.core.IRole;
import org.jboss.tools.seam.core.ISeamContextVariable;
import org.jboss.tools.seam.core.ISeamElement;
import org.jboss.tools.seam.core.ISeamJavaComponentDeclaration;
import org.jboss.tools.seam.core.ScopeType;

public class Role extends SeamJavaContextVariable implements IRole {

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.seam.internal.core.SeamJavaContextVariable#clone()
	 */
	@Override
	public Role clone() throws CloneNotSupportedException {
		return (Role)super.clone();
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.seam.internal.core.AbstractContextVariable#getScope()
	 */
	@Override
	public ScopeType getScope() {
		ScopeType returnScopeType = scopeType;
		if(returnScopeType == null) {
			ISeamElement parent = getParent();
			if(parent instanceof ISeamContextVariable) {
				returnScopeType = ((ISeamContextVariable)parent).getScope();
			} else if(parent instanceof ISeamJavaComponentDeclaration) {
				returnScopeType = ((ISeamJavaComponentDeclaration)parent).getScope();
			}
			if(returnScopeType == null) {
				returnScopeType = ScopeType.UNSPECIFIED;
			}
		}
		return returnScopeType;
	}

	public String getXMLName() {
		return SeamXMLConstants.TAG_ROLE;
	}
	
}
