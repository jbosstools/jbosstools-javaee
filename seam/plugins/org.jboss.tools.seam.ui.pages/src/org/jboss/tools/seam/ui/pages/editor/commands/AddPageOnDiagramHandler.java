package org.jboss.tools.seam.ui.pages.editor.commands;

import java.util.Properties;

import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.common.meta.action.impl.AbstractHandler;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
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

//		XActionInvoker.invoke("CreateActions.AddPageWizard", object, p);
	}

}
