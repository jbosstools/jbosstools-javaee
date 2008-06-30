package org.jboss.tools.seam.pages.xml.model.handlers;

import java.util.Properties;

import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateHandler;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.seam.pages.xml.model.SeamPagesConstants;
import org.jboss.tools.seam.pages.xml.model.helpers.SeamPagesDiagramStructureHelper;

public class AddExceptionHandler extends DefaultCreateHandler {

	public AddExceptionHandler() {}

    public void executeHandler(XModelObject object, Properties prop) throws XModelException {
    	super.executeHandler(object, prop);
    	if(prop == null) return;
    	XModelObject created = (XModelObject)prop.get("created");
    	if(created == null) return;
    	String path = created.getPathPart();
    	XModelObject item = SeamPagesDiagramStructureHelper.getInstance().getDiagram(object).getChildByPath(path);
		String shape = getShape(prop);
		if(item != null && shape != null) {
			item.setAttributeValue("shape", shape);
		}
    	
    }

	protected XModelObject modifyCreatedObject(XModelObject o) {
		Properties p = extractProperties(data[0]);
		String viewId = p.getProperty(SeamPagesConstants.ATTR_VIEW_ID);
		viewId = AddViewSupport.revalidatePath(viewId);
		p.setProperty(SeamPagesConstants.ATTR_VIEW_ID, viewId);
		String childEntity = action.getProperty("childEntity");
		XModelObject c = XModelObjectLoaderUtil.createValidObject(o.getModel(), childEntity, p);
		o.addChild(c);
		return o;
	}

	public static String getShape(Properties p) {
		String x = p.getProperty("mouse.x");
		String y = p.getProperty("mouse.y");
		return (x == null || y == null) ? null : x + "," + y + ",0,0";		
	}
	
}
