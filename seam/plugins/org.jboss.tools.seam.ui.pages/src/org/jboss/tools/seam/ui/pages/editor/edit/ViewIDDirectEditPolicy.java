/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.seam.ui.pages.editor.edit;

import java.util.Properties;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.jst.web.model.ReferenceObject;
import org.jboss.tools.seam.pages.xml.model.SeamPagesConstants;
import org.jboss.tools.seam.pages.xml.model.handlers.RenameViewSupport;
import org.jboss.tools.seam.pages.xml.model.helpers.SeamPagesDiagramHelper;
import org.jboss.tools.seam.pages.xml.model.helpers.SeamPagesDiagramStructureHelper;
import org.jboss.tools.seam.ui.pages.SeamUiPagesPlugin;
import org.jboss.tools.seam.ui.pages.editor.commands.AddExceptionOnDiagramHandler;
import org.jboss.tools.seam.ui.pages.editor.commands.AddPageOnDiagramHandler;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.Page;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PageException;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesElement;

public class ViewIDDirectEditPolicy extends DirectEditPolicy {

	/**
	 * @see DirectEditPolicy#getDirectEditCommand(DirectEditRequest)
	 */
	protected Command getDirectEditCommand(DirectEditRequest request) {
		String labelText = (String) request.getCellEditor().getValue();
		PagesEditPart node = (PagesEditPart) getHost();
		PagesElement element = node.getElementModel();
		if (element != null) {
			Object rename = request.getExtendedData().get("rename");
			if(rename != null && rename instanceof Boolean && ((Boolean)rename).booleanValue())
				return new RenameViewCommand(element, labelText);
			else
				return new InitViewCommand(element, labelText);
		}
		return null;
	}

	/**
	 * @see DirectEditPolicy#showCurrentEditValue(DirectEditRequest)
	 */
	protected void showCurrentEditValue(DirectEditRequest request) {
	}

	public class InitViewCommand extends Command {

		PagesElement node;
		String value;
		XModelObject object;

		public InitViewCommand(PagesElement node, String value) {
			this.node = node;
			this.value = value;
			object = (XModelObject)node.getPagesModel().getData();
		}

		public boolean canExecute() {
			ViewIDValidator val = new ViewIDValidator(object);
			String message = val.isValid(value);
			
			if (message == null || "".equals(message))
				return true;
			return false;
		}

		public boolean canUndo() {
			return false;
		}

		public void execute() {
			Properties props = new Properties();
			props.setProperty("mouse.x", ""+node.getLocation().x);
			props.setProperty("mouse.y", ""+node.getLocation().y);
			
			
			if(node instanceof Page)
				AddPageOnDiagramHandler.createPage(object, value, props);
			else if(node instanceof PageException)
				AddExceptionOnDiagramHandler.createException(object, value, props);
		}
	}
	
	public class RenameViewCommand extends Command {

		PagesElement node;
		String value;
		XModelObject object;
		String oldValue;

		public RenameViewCommand(PagesElement node, String value) {
			this.node = node;
			this.value = value;
			object = (XModelObject)node.getData();
			oldValue = object.getAttributeValue(SeamPagesConstants.ATTR_PATH);
		}

		public boolean canExecute() {
			ViewIDValidator val = new ViewIDValidator(object);
			String message = val.isValid(value);
			
			if (message == null || "".equals(message))
				return true;
			return false;
		}

		public boolean canUndo() {
			return false;
		}

		public void execute() {
			if(node instanceof Page && object instanceof ReferenceObject){
				SeamPagesDiagramHelper h = SeamPagesDiagramHelper.getHelper(SeamPagesDiagramStructureHelper.instance.getDiagram(object));
				h.addUpdateLock(this);
				try{
					RenameViewSupport.replace((ReferenceObject)object, oldValue, value);
				}catch(XModelException ex){
					SeamUiPagesPlugin.log(ex);
				} finally {
					h.removeUpdateLock(this);
					h.updateDiagram();
				}
			}
			
		}
	}
}
