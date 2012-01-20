/*******************************************************************************
 * Copyright (c) 2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.seam.config.ui.contentassist;

import org.eclipse.jdt.core.IMember;

public class TagData {
	String name;
	boolean hasClosingTag = true;
	boolean isUnique = false;
	int relevance;

	IMember member;
	
	public TagData(String name, int relevance) {
		this.name = name;
		this.relevance = relevance;
	}

	public TagData(String prefix, String localName, boolean hasClosingTag, boolean isUnique, int relevance) {
		this.name = prefix + ":" + localName;
		this.hasClosingTag = hasClosingTag;
		this.relevance = relevance;
		this.isUnique = isUnique;
	}

	public String getText() {
		return hasClosingTag ? "<" + name + "></" + name + ">" : "<" + name + "/>";
	}

	public String getName() {
		return name;
	}

	public int getRelevance() {
		return relevance;
	}

	public void setHasClosingTag(boolean b) {
		hasClosingTag = b;
	}

	public void setUnique(boolean b) {
		isUnique = b;
	}

	public boolean isUnique() {
		return isUnique;
	}

	public void setMember(IMember member) {
		this.member = member;
	}

	public IMember getMember() {
		return member;
	}

}
