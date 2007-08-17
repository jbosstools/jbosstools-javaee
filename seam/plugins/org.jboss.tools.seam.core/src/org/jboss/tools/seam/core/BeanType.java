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

package org.jboss.tools.seam.core;

import org.jboss.tools.seam.internal.core.scanner.java.SeamAnnotations;

public enum BeanType implements SeamAnnotations {
	ENTITY(ENTITY_ANNOTATION_TYPE, ScopeType.CONVERSATION, "entity"),
	STATEFUL(STATEFUL_ANNOTATION_TYPE, ScopeType.CONVERSATION, "stateful"),
	STATELESS(STATELESS_ANNOTATION_TYPE, ScopeType.STATELESS, "stateless"),
	MESSAGE_DRIVEN(MESSAGE_DRIVEN_ANNOTATION_TYPE, ScopeType.STATELESS, "message-driven");

	String annotationType;
	ScopeType defaultScope;
	String path;
	BeanType(String annotationType,ScopeType defaultScope, String path) {
		this.annotationType = annotationType;
		this.defaultScope = defaultScope;
		this.path = path;
	}

	public String getAnnotationType() {
		return annotationType;
	}
	
	ScopeType getDefaultScope() {
		return defaultScope;
	}
	
	public String getPath() {
		return path;
	}

}
