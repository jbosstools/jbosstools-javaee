package org.jboss.tools.seam.pages.xml.model.handlers;

import java.util.Properties;

import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateHandler;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.seam.pages.xml.model.helpers.SeamPagesProcessStructureHelper;

public class AddExceptionHandler extends DefaultCreateHandler {

	public AddExceptionHandler() {}

    public void executeHandler(XModelObject object, Properties prop) throws XModelException {
    	super.executeHandler(object, prop);
    	if(prop == null) return;
    	XModelObject created = (XModelObject)prop.get("created");
    	if(created == null) return;
    	String path = created.getPathPart();
    	XModelObject item = SeamPagesProcessStructureHelper.getInstance().getProcess(object).getChildByPath(path);
		String shape = getShape(prop);
		if(item != null && shape != null) {
			item.setAttributeValue("shape", shape);
		}
    	
    }

	protected XModelObject modifyCreatedObject(XModelObject o) {
		Properties p = extractProperties(data[0]);
		String childEntity = action.getProperty("childEntity");
		XModelObject c = XModelObjectLoaderUtil.createValidObject(o.getModel(), childEntity, p);
		o.addChild(c);
		return o;
	}

	public static String getShape(Properties p) {
		String x = p.getProperty("process.mouse.x");
		String y = p.getProperty("process.mouse.y");
		return (x == null || y == null) ? null : x + "," + y + ",0,0";		
	}
	
}
