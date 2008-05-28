package org.jboss.tools.seam.pages.xml.model.handlers;

import java.util.Properties;

import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateHandler;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.seam.pages.xml.model.SeamPagesConstants;

public class AddRuleHandler extends DefaultCreateHandler {

	public AddRuleHandler() {}

	protected XModelObject modifyCreatedObject(XModelObject o) {
		Properties p = extractProperties(data[0]);
		String kind = p.getProperty("kind");
		String viewId = p.getProperty(SeamPagesConstants.ATTR_VIEW_ID);
		String entity = getChildEntity(kind);
		XModelObject c = XModelObjectLoaderUtil.createValidObject(o.getModel(), entity);
		c.setAttributeValue(SeamPagesConstants.ATTR_VIEW_ID, viewId);
		o.addChild(c);
		return o;
	}

	private String getChildEntity(String kind) {
		String entityKey = ("render".equals(kind)) ? "renderEntity" : "redirectEntity";
		String entity = action.getProperty(entityKey);
		return entity;		
	}

}
