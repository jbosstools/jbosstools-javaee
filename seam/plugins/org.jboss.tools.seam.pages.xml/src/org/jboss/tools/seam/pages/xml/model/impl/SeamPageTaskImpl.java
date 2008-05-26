package org.jboss.tools.seam.pages.xml.model.impl;

import org.jboss.tools.common.model.impl.CustomizedObjectImpl;

public class SeamPageTaskImpl extends CustomizedObjectImpl {
	private static final long serialVersionUID = 1L;

	public String getPresentationString() {
		return getModelEntity().getXMLSubPath();
	}

}
