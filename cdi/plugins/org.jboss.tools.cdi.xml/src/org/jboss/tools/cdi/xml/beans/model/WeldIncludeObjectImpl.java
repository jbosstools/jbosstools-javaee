package org.jboss.tools.cdi.xml.beans.model;

import org.jboss.tools.common.model.impl.CustomizedObjectImpl;

public class WeldIncludeObjectImpl extends CustomizedObjectImpl {
	private static final long serialVersionUID = 1L;

	public String name() {
		String name = getAttributeValue("name");
		if(name == null || name.length() == 0) {
			name = getAttributeValue("pattern");
		}
		return name;
	}

	public String getAttributeValue(String name) {
		if("name/pattern".equals(name)) {
			return name();
		}
		return super.getAttributeValue(name);
	}
}
