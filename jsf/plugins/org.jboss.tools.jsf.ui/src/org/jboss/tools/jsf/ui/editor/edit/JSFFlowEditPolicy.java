/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.jboss.tools.jsf.ui.editor.edit;


import java.util.Collections;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.jboss.tools.common.gef.edit.xpl.FeedBackUtils;
import org.jboss.tools.jsf.ui.editor.model.IGroup;
import org.jboss.tools.jsf.ui.editor.model.IPage;
import org.jboss.tools.jsf.ui.editor.model.commands.ReorderPartCommand;

public class JSFFlowEditPolicy extends org.eclipse.gef.editpolicies.FlowLayoutEditPolicy {

	protected Command createAddCommand(EditPart child, EditPart after) {
		return null;
	}

	protected Command createMoveChildCommand(EditPart child, EditPart after) {
		IPage childModel = (IPage) child.getModel();
		IGroup parentModel = (IGroup) getHost().getModel();
		int oldIndex = getHost().getChildren().indexOf(child);
		int newIndex = getHost().getChildren().indexOf(after);
		if (newIndex > oldIndex)
			newIndex--;

		ReorderPartCommand command = new ReorderPartCommand(childModel,
				parentModel, oldIndex, newIndex);
		return command;
	}

	protected Command getCreateCommand(CreateRequest request) {
		return null;
	}

	protected Command getDeleteDependantCommand(Request request) {
		return null;
	}

	protected Command getOrphanChildrenCommand(Request request) {
		return null;
	}

	protected boolean isHorizontal() {
		return false;
	}

	protected EditPolicy createChildEditPolicy(EditPart child) {
		return new JSFNonResizableEditPolicy();
	}

	protected void showLayoutTargetFeedback(Request request) {
		FeedBackUtils
				.showLayoutTargetFeedBack(request, this, getLineFeedback(),
						getFeedbackIndexFor(request), isHorizontal());
	}

	class JSFNonResizableEditPolicy extends NonResizableEditPolicy {
		public JSFNonResizableEditPolicy() {
			super();
		}

		public List createSelectionHandles() {
			return Collections.EMPTY_LIST;
		}

	}

}
