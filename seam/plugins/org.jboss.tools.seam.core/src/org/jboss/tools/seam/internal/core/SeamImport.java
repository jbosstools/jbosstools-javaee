package org.jboss.tools.seam.internal.core;

import org.jboss.tools.common.xml.XMLUtilities;
import org.w3c.dom.Element;

public class SeamImport {
	static String ATTR_PACKAGE = "package";

	protected String seamPackage;
	protected String javaPackage;

	public SeamImport() {}

	public String getSeamPackage() {
		return seamPackage;
	}

	public String getJavaPackage() {
		return javaPackage;
	}

	public void setSeamPackage(String seamPackage) {
		this.seamPackage = seamPackage;
	}

	public void setJavaPackage(String javaPackage) {
		this.javaPackage = javaPackage;
	}

	public Element toXML(Element parent) {
		Element element = XMLUtilities.createElement(parent, SeamXMLConstants.TAG_IMPORT);
		if(seamPackage != null) {
			element.setAttribute(SeamXMLConstants.ATTR_VALUE, seamPackage);
		}
		if(javaPackage != null) {
			element.setAttribute(ATTR_PACKAGE, javaPackage);
		}
		return element;
	}

	public void loadXML(Element element) {
		if(element.hasAttribute(ATTR_PACKAGE)) {
			javaPackage = element.getAttribute(ATTR_PACKAGE);
		}
		seamPackage = element.getAttribute(SeamXMLConstants.ATTR_VALUE);
	}

}
