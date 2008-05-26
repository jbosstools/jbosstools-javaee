package org.jboss.tools.seam.pages.xml.model.impl;

import org.jboss.tools.common.model.impl.CustomizedObjectImpl;

public class SeamPageRuleImpl extends CustomizedObjectImpl {
	private static final long serialVersionUID = 1L;

	public String getPathPart() {
    	String v1 = getAttributeValue("if outcome");
    	String v2 = getAttributeValue("if");
    	return "" + v1 + ":" + v2;
    }

	public String getPresentationString() {
		String v1 = getAttributeValue("if outcome");
		if(v1 != null && v1.length() > 0) return v1;
		return "rule";
	}

}
