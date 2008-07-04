package org.jboss.tools.seam.ui.pages.editor.commands;

import java.util.Properties;
import java.util.Random;

import org.jboss.tools.common.meta.XChild;
import org.jboss.tools.common.meta.XModelEntity;
import org.jboss.tools.common.meta.action.impl.AbstractHandler;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.FindObjectHelper;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.seam.pages.xml.model.SeamPagesConstants;
import org.jboss.tools.seam.pages.xml.model.handlers.AddExceptionHandler;
import org.jboss.tools.seam.ui.pages.editor.edit.PagesDiagramEditPart;

public class AddExceptionOnDiagramHandler extends AbstractHandler {

	public AddExceptionOnDiagramHandler() {}

	public boolean isEnabled(XModelObject object) {
		return object != null && object.isObjectEditable();
	}

	public void executeHandler(XModelObject object, Properties p) throws XModelException {
		if(!isEnabled(object)) return;
		PagesDiagramEditPart part = (PagesDiagramEditPart)p.get("diagramEditPart");
		int x = Integer.parseInt(p.getProperty("mouse.x"));
		int y = Integer.parseInt(p.getProperty("mouse.y"));
		System.out.println("x=" + x + " y=" + y + " part=" + part);

		String className = "/test" + (new Random()).nextInt(10000);
		AddExceptionOnDiagramHandler.createException(object, className, p);

//		XActionInvoker.invoke("CreateActions.AddExceptionWizard", object, p);
	}

	/**
	 * 
	 * @param diagram Root object for diagram model
	 * @param viewId User input
	 * @param p Properties passed to handler that include mouse coordinates
	 */
	public static void createException(XModelObject diagram, String className, Properties p) {
		XModelObject m = diagram.getParent().getChildByPath(SeamPagesConstants.FOLDER_EXCEPTIONS);
		String childEntity = m.getModelEntity().getChildren()[0].getName();
		XModelObject e = m.getModel().createModelObject(childEntity, null);
		e.setAttributeValue("class", className);
		XModelObject c = XModelObjectLoaderUtil.createValidObject(diagram.getModel(), getRedirectChildEntity(e.getModelEntity()), p);
		c.setAttributeValue(SeamPagesConstants.ATTR_VIEW_ID, "");
		e.addChild(c);
		m.addChild(e);
		m.setModified(true);
		String pp = e.getPathPart();
		XModelObject created = diagram.getChildByPath(pp);
		String shape = AddExceptionHandler.getShape(p);
		if(created != null && shape != null) created.setAttributeValue("shape", shape);
		if(created != null) {
			FindObjectHelper.findModelObject(created, FindObjectHelper.IN_EDITOR_ONLY);
		}		
	}

	static String getRedirectChildEntity(XModelEntity exc) {
		XChild[] cs = exc.getChildren();
		for (int i = 0; i < cs.length; i++) {
			if(cs[i].getName().startsWith("SeamPageRedirect")) {
				return cs[i].getName();
			}
		}
		return null;
	}

}
