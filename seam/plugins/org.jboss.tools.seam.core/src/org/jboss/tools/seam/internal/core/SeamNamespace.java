package org.jboss.tools.seam.internal.core;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.jboss.tools.common.xml.XMLUtilities;
import org.jboss.tools.seam.core.ISeamNamespace;
import org.w3c.dom.Element;

public class SeamNamespace implements ISeamNamespace {
	static String ATTR_URI = "uri";
	static String ATTR_PACKAGE = "package";

	protected IPath source;
	protected String uri;
	protected String javaPackage;

	public SeamNamespace() {}

	public IPath getSourcePath() {
		return source;
	}

	public String getURI() {
		return uri;
	}

	public String getPackage() {
		return javaPackage;
	}

	public void setSourcePath(IPath source) {
		this.source = source;
		
	}

	public void setURI(String uri) {
		this.uri = uri;
	}

	public void setPackage(String javaPackage) {
		this.javaPackage = javaPackage;
	}

	public Element toXML(Element parent) {
		Element element = XMLUtilities.createElement(parent, "namespace");
		if(source != null) {
			element.setAttribute(SeamXMLConstants.ATTR_PATH, source.toString());
		}
		if(uri != null) {
			element.setAttribute(ATTR_URI, uri);
		}
		if(javaPackage != null) {
			element.setAttribute(ATTR_PACKAGE, javaPackage);
		}
		return element;
	}

	public void loadXML(Element element) {
		String s = element.getAttribute(SeamXMLConstants.ATTR_PATH);
		if(s != null && s.length() > 0) {
			source = new Path(s);
		}
		uri = element.getAttribute(ATTR_URI);
		javaPackage = element.getAttribute(ATTR_PACKAGE);
	}

}
