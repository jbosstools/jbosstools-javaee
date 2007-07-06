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

/**
 * @author Viacheslav Kabanovich
 */
public class BijectedAttribute extends SeamJavaContextVariable implements IBijectedAttribute {
	BijectedAttributeType[] types = null;
	
	public BijectedAttribute() {		
	}

	public void setMember(IMember javaSource) {
		this.javaSource = javaSource;
	}

	public BijectedAttributeType[] getTypes() {
		return types;
	}
	
	public boolean isOfType(BijectedAttributeType type) {
		if(types == null) return false;
		for (int i = 0; i < types.length; i++) {
			if(types[i] == type) return true;
		}
		return false;
	}

	public void setTypes(BijectedAttributeType[] types) {
		this.types = types;
	}

}
