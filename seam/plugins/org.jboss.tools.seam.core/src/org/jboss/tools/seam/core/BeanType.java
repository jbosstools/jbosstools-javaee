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
