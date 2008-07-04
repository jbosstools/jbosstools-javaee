package org.jboss.tools.seam.ui.pages.editor.commands;

import java.util.Properties;
import java.util.Random;

import org.jboss.tools.common.meta.action.impl.AbstractHandler;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.FindObjectHelper;
import org.jboss.tools.seam.pages.xml.model.SeamPagesConstants;
import org.jboss.tools.seam.pages.xml.model.handlers.AddExceptionHandler;
import org.jboss.tools.seam.pages.xml.model.handlers.AddViewSupport;
import org.jboss.tools.seam.pages.xml.model.helpers.SeamPagesDiagramHelper;
import org.jboss.tools.seam.ui.pages.editor.edit.PagesDiagramEditPart;

public class AddPageOnDiagramHandler extends AbstractHandler {

	public AddPageOnDiagramHandler() {}

	public boolean isEnabled(XModelObject object) {
		return object != null && object.isObjectEditable();
	}

	public void executeHandler(XModelObject object, Properties p) throws XModelException {
		if(!isEnabled(object)) return;
		PagesDiagramEditPart part = (PagesDiagramEditPart)p.get("diagramEditPart");
		int x = Integer.parseInt(p.getProperty("mouse.x"));
		int y = Integer.parseInt(p.getProperty("mouse.y"));
		System.out.println("x=" + x + " y=" + y + " part=" + part);

//
		String viewId = "/test" + (new Random()).nextInt(10000);
		AddPageOnDiagramHandler.createPage(object, viewId, p);

//		XActionInvoker.invoke("CreateActions.AddPageWizard", object, p);
	}

	/**
	 * 
	 * @param diagram Root object for diagram model
	 * @param viewId User input
	 * @param p Properties passed to handler that include mouse coordinates
	 */
	public static void createPage(XModelObject diagram, String viewId, Properties p) {
		XModelObject m = diagram.getParent().getChildByPath(SeamPagesConstants.FOLDER_PAGES);
		String path = AddViewSupport.revalidatePath(viewId);
		String pp = SeamPagesDiagramHelper.toNavigationRulePathPart(path);
		XModelObject rule = AddViewSupport.addPage(m, path);
		m.setModified(true);
		XModelObject created = diagram.getChildByPath(pp);
		String shape = AddExceptionHandler.getShape(p);
		if(created != null && shape != null) created.setAttributeValue("shape", shape);
		if(created != null) {
			FindObjectHelper.findModelObject(created, FindObjectHelper.IN_EDITOR_ONLY);
		}		
	}

}
