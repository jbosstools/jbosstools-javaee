/*******************************************************************************
 * Copyright (c) 2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.seam.config.core.definition;

import org.jboss.tools.common.java.IParametedType;

/**
 * Additional object for field definition, when an inline bean is injected to its value.
 * In case of collections and maps, there can be multiple value injections, for each one a 
 * separate injection point should be provided.
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class SeamFieldValueDefinition extends SeamFieldDefinition {
	SeamBeanDefinition inline;
	IParametedType requiredType;

	public void setInlineBean(SeamBeanDefinition inline) {
		this.inline = inline;
	}

	public SeamBeanDefinition getInlineBean() {
		return inline;
	}

	public void setRequiredType(IParametedType requiredType) {
		this.requiredType = requiredType;
	}

	public IParametedType getRequiredType() {
		return requiredType;
	}

}
