package org.jboss.tools.seam.pages.xml.model.impl;

import org.jboss.tools.common.model.impl.CustomizedObjectImpl;
import org.jboss.tools.seam.pages.xml.model.SeamPagesConstants;

public class SeamPageRedirectImpl extends CustomizedObjectImpl {
	private static final long serialVersionUID = 1L;

	public String getPathPart() {
		return super.getPathPart();
	}

	public String getPresentationString() {
		String v1 = getAttributeValue(SeamPagesConstants.ATTR_VIEW_ID);
		if(v1 != null && v1.length() > 0) return v1;
		return getModelEntity().getXMLSubPath();
	}

}
