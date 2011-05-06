package org.jboss.tools.cdi.seam.config.ui.contentassist;

public class TagData {
	String name;
	boolean hasClosingTag = true;
	int relevance;
	
	public TagData(String name, int relevance) {
		this.name = name;
		this.relevance = relevance;
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

}
