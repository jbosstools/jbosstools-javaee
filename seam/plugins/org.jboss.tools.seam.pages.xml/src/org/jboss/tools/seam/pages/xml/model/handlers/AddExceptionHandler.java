package org.jboss.tools.seam.pages.xml.model.handlers;

import java.util.Properties;

import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateHandler;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;

public class AddExceptionHandler extends DefaultCreateHandler {

	public AddExceptionHandler() {}

	protected XModelObject modifyCreatedObject(XModelObject o) {
		Properties p = extractProperties(data[0]);
		String childEntity = action.getProperty("childEntity");
		XModelObject c = XModelObjectLoaderUtil.createValidObject(o.getModel(), childEntity, p);
		o.addChild(c);
		return o;
	}

}
