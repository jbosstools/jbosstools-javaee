/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.bot.test.uiutils.wizards;

public enum CDIWizardType {
	INTERCEPTOR_BINDING, QUALIFIER, SCOPE, STEREOTYPE, DECORATOR, INTERCEPTOR;

	String getName() {
		switch (this) {
		case INTERCEPTOR_BINDING:
			return "Interceptor Binding";
		case QUALIFIER:
			return "Qualifier";
		case SCOPE:
			return "Scope";
		case STEREOTYPE:
			return "Stereotype";
		case DECORATOR:
			return "Decorator";
		case INTERCEPTOR:
			return "Interceptor";
		default:
			throw new AssertionError("Unknown type");
		}
	}

	public String getAnnotationType() {
		return isAnnotation() ? getName() + " Annotation Type" : getName() + " Type";
	}
	
	private boolean isAnnotation() {
		return this.ordinal() < DECORATOR.ordinal();
	}
}