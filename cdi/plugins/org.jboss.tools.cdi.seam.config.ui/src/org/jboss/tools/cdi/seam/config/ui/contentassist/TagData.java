package org.jboss.tools.cdi.seam.config.ui.contentassist;

public class TagData {
	String name;
	boolean hasClosingTag = true;
	boolean isUnique = false;
	int relevance;
	
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

}
