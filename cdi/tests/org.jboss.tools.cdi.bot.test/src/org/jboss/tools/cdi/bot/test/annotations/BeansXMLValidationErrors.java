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
package org.jboss.tools.cdi.bot.test.annotations;

public enum BeansXMLValidationErrors {

	ALTERNATIVE("must specify the name of an alternative bean class"), 
	DECORATOR("must specify the name of a decorator bean class"), 
	INTERCEPTOR("must specify the name of an interceptor class"), 
	NO_SUCH_CLASS("There is no class with the specified name");
	
	private String validationError;
	
	private BeansXMLValidationErrors(String validationError) {
		this.validationError = validationError;
	}
	
	public String message() {
		return validationError;
	}
	
}
