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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IMethod;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class SeamMethodDefinition extends SeamMemberDefinition {
	IMethod method;
	List<SeamParameterDefinition> parameters = new ArrayList<SeamParameterDefinition>();

	public SeamMethodDefinition() {}

	public void setMethod(IMethod method) {
		this.method = method;
		//TODO set parameter objects to parameters
	}

	public IMethod getMethod() {
		return method;
	}

	public void addParameter(SeamParameterDefinition p) {
		parameters.add(p);
	}

	public List<SeamParameterDefinition> getParameters() {
		return parameters;
	}

}
