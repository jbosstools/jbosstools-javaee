package org.jboss.tools.seam.pages.xml.model.handlers;

import org.jboss.tools.common.meta.action.impl.handlers.DefaultRemoveHandler;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.seam.pages.xml.model.SeamPagesConstants;

public class DeleteVirtualOutputHandler extends DefaultRemoveHandler {

	public boolean isEnabled(XModelObject object) {
		if(!super.isEnabled(object)) {
			return false;
		}
		String subtype = object.getAttributeValue(SeamPagesConstants.ATTR_SUBTYPE);
		return SeamPagesConstants.SUBTYPE_CUSTOM.equals(subtype);
	}

}
