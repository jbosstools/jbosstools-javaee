/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.jsf.model.impl;

public class ListEntryObjectImpl extends EntryObjectImpl {
	private static final long serialVersionUID = 683965910278232033L;
	
	public String getPresentationString() {
		return (isNullValue()) ? "<null-value>" : getAttributeValue("value");
	}
	
	public String getPathPart() {
		return "" + System.identityHashCode(this);
	}
	
}
