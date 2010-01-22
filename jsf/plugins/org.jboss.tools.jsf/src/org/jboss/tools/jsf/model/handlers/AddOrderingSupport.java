package org.jboss.tools.jsf.model.handlers;

import java.util.Properties;

import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateHandler;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateSupport;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;

public class AddOrderingSupport extends DefaultCreateSupport {
	static String ATTR_AFTER_NAME = "after name";
	static String ATTR_AFTER_OTHERS = "after others";
	static String ATTR_BEFORE_NAME = "before name";
	static String ATTR_BEFORE_OTHERS = "before others";

	public AddOrderingSupport() {}

	protected void finish() throws XModelException {
		String entity = getEntityName();
		Properties p = extractStepData(0);
		XModelObject c = XModelObjectLoaderUtil.createValidObject(getTarget().getModel(), entity, p);

		String afterName = p.getProperty(ATTR_AFTER_NAME);
		if(afterName == null || afterName.length() > 0) {
			XModelObject n = XModelObjectLoaderUtil.createValidObject(getTarget().getModel(), c.getChildByPath("After").getModelEntity().getChildren()[0].getName());
			n.setAttributeValue("name", afterName);
			c.getChildByPath("After").addChild(n);
		}
		String afterOthers = p.getProperty(ATTR_AFTER_OTHERS);
		if("true".equals(afterOthers)) {
			c.getChildByPath("After").setAttributeValue("others", "true");
		}

		String beforeName = p.getProperty(ATTR_BEFORE_NAME);
		if(beforeName == null || beforeName.length() > 0) {
			XModelObject n = XModelObjectLoaderUtil.createValidObject(getTarget().getModel(), c.getChildByPath("Before").getModelEntity().getChildren()[0].getName());
			n.setAttributeValue("name", beforeName);
			c.getChildByPath("Before").addChild(n);
		}
		String beforeOthers = p.getProperty(ATTR_BEFORE_OTHERS);
		if("true".equals(beforeOthers)) {
			c.getChildByPath("Before").setAttributeValue("others", "true");
		}

		DefaultCreateHandler.addCreatedObject(getTarget(), c, getProperties());
	}
	
}
