/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.seam.config.core.definition;

import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IType;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class SeamParameterDefinition extends SeamMemberDefinition {
	IType type;
	String dimentions;

	ILocalVariable parameter;

	public SeamParameterDefinition() {}

	public void setType(IType type) {
		this.type = type;
	}

	public void setDimensions(String value) {
		dimentions = value == null ? null : value.trim();
	}

	public void setParameter(ILocalVariable parameter) {
		this.parameter = parameter;
	}

	public int getDimensions() {
		if(dimentions == null || dimentions.length() == 0) {
			return 0;
		}
		try {
			return Integer.parseInt(dimentions);
		} catch (NumberFormatException e) {
			//reporting is done by loader; do not report here.
			return 0;
		}
	}

	public IType getType() {
		return type;
	}

}
