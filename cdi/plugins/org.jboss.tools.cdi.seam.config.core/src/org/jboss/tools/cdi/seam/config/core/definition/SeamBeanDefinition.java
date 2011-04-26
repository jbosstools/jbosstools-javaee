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

import org.eclipse.jdt.core.IType;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class SeamBeanDefinition extends SeamMemberDefinition {
	IType type = null;
	List<SeamFieldDefinition> fields = new ArrayList<SeamFieldDefinition>();
	List<SeamMethodDefinition> methods = new ArrayList<SeamMethodDefinition>();

	public SeamBeanDefinition() {}

	public void setType(IType type) {
		this.type = type;
	}

	public void addField(SeamFieldDefinition field) {
		fields.add(field);
	}

	public void addMethod(SeamMethodDefinition method) {
		methods.add(method);
	}

}
