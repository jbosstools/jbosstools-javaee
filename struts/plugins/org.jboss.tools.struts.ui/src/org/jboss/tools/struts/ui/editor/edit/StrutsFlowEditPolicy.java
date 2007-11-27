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
package org.jboss.tools.struts.ui.editor.edit;


import java.util.Collections;
import java.util.List;

import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.geometry.Transposer;
import org.eclipse.gef.*;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.FeedbackHelper;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;

import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.DropRequest;

import org.jboss.tools.common.gef.edit.xpl.FeedBackUtils;
import org.jboss.tools.struts.ui.editor.model.IForward;
import org.jboss.tools.struts.ui.editor.model.IProcessItem;
import org.jboss.tools.struts.ui.editor.model.commands.ReorderPartCommand;

public class StrutsFlowEditPolicy
	extends org.eclipse.gef.editpolicies.FlowLayoutEditPolicy
{

protected Command createAddCommand(EditPart child, EditPart after) {
	return null;
}

protected Command createMoveChildCommand(EditPart child, EditPart after) {
	IForward childModel = (IForward)child.getModel();
	IProcessItem parentModel = (IProcessItem)getHost().getModel();
	int oldIndex = getHost().getChildren().indexOf(child);
	int newIndex = getHost().getChildren().indexOf(after);
	if (newIndex > oldIndex)
		newIndex--;
	
	ReorderPartCommand command = new ReorderPartCommand(childModel, parentModel, oldIndex, newIndex);
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

protected EditPolicy createChildEditPolicy(EditPart child){
	return new JSFNonResizableEditPolicy();
}

protected void showLayoutTargetFeedback(Request request) {
	FeedBackUtils.showLayoutTargetFeedBack(request, 
			this, getLineFeedback(), getFeedbackIndexFor(request), isHorizontal());
}

private Point getLocationFromRequest(Request request) {
	return ((DropRequest)request).getLocation();
}

private Rectangle getAbsoluteBounds(GraphicalEditPart ep) {
	Rectangle bounds = ep.getFigure().getBounds().getCopy();
	ep.getFigure().translateToAbsolute(bounds);
	return bounds;
}

class JSFNonResizableEditPolicy extends NonResizableEditPolicy{
	public JSFNonResizableEditPolicy(){
		super();
	}
	
	public List createSelectionHandles(){
		return Collections.EMPTY_LIST;
	}

}

}