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
package org.jboss.tools.struts.model.helpers.page.link;

public class Link {
	String tag;
	String attribute;
	String referTo;
	
	public void setTag(String tag) {
		this.tag = tag;
	}
	
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	
	public void setReferTo(String referTo) {
		this.referTo = referTo;
	}
	
	public String getTag() {
		return tag;
	}
	
	public String getAttribute() {
		return attribute;
	}
	
	public String getReferTo() {
		return referTo;
	}
	
}
