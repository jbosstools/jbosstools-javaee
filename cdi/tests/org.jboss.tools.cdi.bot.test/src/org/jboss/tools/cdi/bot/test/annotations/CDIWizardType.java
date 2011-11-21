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
package org.jboss.tools.cdi.bot.test.annotations;

public enum CDIWizardType {

	INTERCEPTOR_BINDING, QUALIFIER, SCOPE, STEREOTYPE,
	DECORATOR, INTERCEPTOR, BEAN, ANNOTATION_LITERAL,
	BEANS_XML;

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
		case BEAN:
			return "Bean";
		case ANNOTATION_LITERAL:
			return "Annotation Literal";
		case BEANS_XML:
			return "File beans.xml";
		default:
			throw new AssertionError("Unknown type");
		}
	}

	public String getAnnotationType() {
		return isAnnotation() ? getName() + " Annotation" : getName();
	}
	
	private boolean isAnnotation() {
		return this.ordinal() < DECORATOR.ordinal();
	}
}