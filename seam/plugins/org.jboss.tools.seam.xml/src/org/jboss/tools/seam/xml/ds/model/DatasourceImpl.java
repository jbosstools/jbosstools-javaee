package org.jboss.tools.seam.xml.ds.model;

import org.jboss.tools.common.model.impl.CustomizedObjectImpl;

public class DatasourceImpl extends CustomizedObjectImpl {
	private static final long serialVersionUID = 1L;

	public DatasourceImpl() {}

	public boolean isAttributeEditable(String name) {
		boolean b = super.isAttributeEditable(name);
		if(b && name.equals(DSConstants.ATTR_SECURITY_DOMAIN)) {
			String type = getAttributeValue(DSConstants.ATTR_SECURITY_TYPE);
			if(type == null || !type.startsWith("security-domain")) {
				b = false;
			}
		}		
		return b;
	}
}
