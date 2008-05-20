/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.seam.core;

/**
 * Types of Seam Scopes.
 * @author Alexey Kazakov
 */
public enum ScopeType implements SeamScope {
	STATELESS("Stateless"), //$NON-NLS-1$
	METHOD("Method"), //$NON-NLS-1$
	EVENT("Event", EVENT_PRIORITY_ORDER), //$NON-NLS-1$
	PAGE("Page", PAGE_PRIORITY_ORDER), //$NON-NLS-1$
	CONVERSATION("Conversation", CONVERSATION_PRIORITY_ORDER), //$NON-NLS-1$
	SESSION("Session", SESSION_PRIORITY_ORDER), //$NON-NLS-1$
	APPLICATION("Application", APPLICATION_PRIORITY_ORDER), //$NON-NLS-1$
	BUSINESS_PROCESS("Business Process", BUSINESS_PROCESS_PRIORITY_ORDER), //$NON-NLS-1$
	UNSPECIFIED("Unspecified", UNSPECIFIED_PRIORITY_ORDER);  //$NON-NLS-1$

	int priority;
	private String label;

	ScopeType(String label, int priority) {
		this.priority = priority;
		this.label = label;
	}

	ScopeType(String label) {
		this.priority = UNDEFINED_PRIORITY_ORDER;
		this.label = label;
	}

	public int getPriority() {
		return priority;
	}

	public String getLabel() {
		return label;
	}
}