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
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.seam.ui.pages.editor.commands.AddExceptionOnDiagramHandler;
import org.jboss.tools.seam.ui.pages.editor.commands.AddPageOnDiagramHandler;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.Page;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PageException;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesElement;

public class ViewIDDirectEditPolicy extends DirectEditPolicy {

	/**
	 * @see DirectEditPolicy#getDirectEditCommand(DirectEditRequest)
	 */
	protected Command getDirectEditCommand(DirectEditRequest edit) {
		String labelText = (String) edit.getCellEditor().getValue();
		PagesEditPart node = (PagesEditPart) getHost();
		PagesElement element = node.getElementModel();
		if (element != null) {
			return new FlowNameCommand(element, labelText);
		}
		return null;
	}

	/**
	 * @see DirectEditPolicy#showCurrentEditValue(DirectEditRequest)
	 */
	protected void showCurrentEditValue(DirectEditRequest request) {
	}

	public class FlowNameCommand extends Command {

		PagesElement node;
		String value;
		XModelObject object;

		public FlowNameCommand(PagesElement node, String value) {
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

}
