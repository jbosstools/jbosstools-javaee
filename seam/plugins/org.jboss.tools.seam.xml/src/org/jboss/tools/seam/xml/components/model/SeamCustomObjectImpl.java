package org.jboss.tools.seam.xml.components.model;

import org.jboss.tools.common.model.impl.CustomizedObjectImpl;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;

public class SeamCustomObjectImpl extends CustomizedObjectImpl {

	public String getPathPart() {
		String s = get(XModelObjectLoaderUtil.ATTR_ID_NAME);
		if(s != null && s.length() > 0) s = ":" + s;
		return "" + super.name() + s;
	}

}
