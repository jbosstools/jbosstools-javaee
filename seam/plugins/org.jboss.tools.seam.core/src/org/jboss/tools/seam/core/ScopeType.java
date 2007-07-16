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
	STATELESS(),
	METHOD(),
	EVENT(EVENT_PRIORITY_ORDER),
	PAGE(PAGE_PRIORITY_ORDER),
	CONVERSATION(CONVERSATION_PRIORITY_ORDER),
	SESSION(SESSION_PRIORITY_ORDER),
	APPLICATION(APPLICATION_PRIORITY_ORDER),
	BUSINESS_PROCESS(BUSINESS_PROCESS_PRIORITY_ORDER),
	UNSPECIFIED(UNDEFINED_PRIORITY_ORDER);

	int priority;

	ScopeType(int priority) {
		this.priority = priority;
	}

	ScopeType() {
		this.priority = UNDEFINED_PRIORITY_ORDER;
	}

	public int getPriority() {
		return priority;
	}
}