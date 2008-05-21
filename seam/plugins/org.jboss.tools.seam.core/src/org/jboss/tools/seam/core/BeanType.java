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
	ENTITY(ENTITY_ANNOTATION_TYPE, ScopeType.CONVERSATION, "entity"), //$NON-NLS-1$
	STATEFUL(STATEFUL_ANNOTATION_TYPE, ScopeType.CONVERSATION, "stateful"), //$NON-NLS-1$
	STATELESS(STATELESS_ANNOTATION_TYPE, ScopeType.STATELESS, "stateless"), //$NON-NLS-1$
	MESSAGE_DRIVEN(MESSAGE_DRIVEN_ANNOTATION_TYPE, ScopeType.STATELESS, "message-driven"); //$NON-NLS-1$

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
