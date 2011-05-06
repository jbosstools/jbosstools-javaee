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
