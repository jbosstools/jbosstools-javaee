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
package org.jboss.tools.struts.ui.editor.edit;

import java.util.List;
import java.util.ArrayList;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.*;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.handles.ResizeHandle;

import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.tools.DragEditPartsTracker;

import org.jboss.tools.struts.StrutsConstants;
import org.jboss.tools.struts.ui.editor.figures.*;
import org.jboss.tools.struts.ui.editor.model.IProcessItem;
import org.jboss.tools.struts.ui.editor.model.IStrutsElement;
import org.jboss.tools.struts.ui.editor.model.commands.SetConstraintCommand;

public class StrutsXYLayoutEditPolicy extends
		org.eclipse.gef.editpolicies.XYLayoutEditPolicy {

	protected Command createAddCommand(EditPart childEditPart, Object constraint) {

		IStrutsElement part = (IStrutsElement) childEditPart.getModel();
		Rectangle rect = (Rectangle) constraint;

		SetConstraintCommand setConstraint = new SetConstraintCommand();

		setConstraint.setLocation(rect);
		setConstraint.setPart(part);
		setConstraint.setLabel("Reparenting JSFSubpart");
		setConstraint.setDebugLabel("LogicXYEP setConstraint");//$NON-NLS-1$*/
		return setConstraint;
	}

	protected Command createChangeConstraintCommand(EditPart child,
			Object constraint) {
		SetConstraintCommand locationCommand = new SetConstraintCommand();

		locationCommand.setShell(child.getViewer().getControl().getShell());
		locationCommand.setPart((IStrutsElement) child.getModel());
		locationCommand.setLocation((Rectangle) constraint);
		return locationCommand;
	}

	protected EditPolicy createChildEditPolicy(EditPart child) {
		if (child instanceof PageEditPart) {
			CustomPolicy policy = new CustomPolicy();
			// policy.setResizeDirections(0);
			return policy;
		} else if (child instanceof GlobalForwardEditPart
				|| child instanceof ActionEditPart
				|| child instanceof CommentEditPart) {
			StrutsResizableEditPolicy policy = new StrutsResizableEditPolicy();
			policy.setResizeDirections(PositionConstants.EAST
					| PositionConstants.WEST);
			return policy;
		}

		return new StrutsResizableEditPolicy();
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

	class CustomPolicy extends NonResizableEditPolicy {
		protected IFigure createDragSourceFeedbackFigure() {
			ProcessItemEditPart part = (ProcessItemEditPart) getHost();
			IFigure child = getCustomFeedbackFigure(part.getModel());
			addFeedback(child);
			Rectangle childBounds = part.getFigure().getBounds().getCopy();
			IFigure walker = part.getFigure().getParent();
			while (walker != ((GraphicalEditPart) part.getParent()).getFigure()) {
				walker.translateToParent(childBounds);
				walker = walker.getParent();
			}
			child.setBounds(childBounds);
			return child;
		}

		protected IFigure getFeedbackLayer() {
			return getLayer(LayerConstants.SCALED_FEEDBACK_LAYER);
		}

		protected IFigure getCustomFeedbackFigure(Object modelPart) {
			IFigure figure = null;

			if (modelPart instanceof IProcessItem) {
				if (((IProcessItem) modelPart).getType().equals(
						StrutsConstants.TYPE_ACTION)) {
					figure = new ActionFeedbackFigure();
				} else if (((IProcessItem) modelPart).getType().equals(
						StrutsConstants.TYPE_PAGE)) {
					figure = new PageFeedbackFigure();
				} else if (((IProcessItem) modelPart).getType().equals(
						StrutsConstants.TYPE_FORWARD)) {
					figure = new GlobalForwardFeedbackFigure();
				} else if (((IProcessItem) modelPart).getType().equals(
						StrutsConstants.TYPE_EXCEPTION)) {
					figure = new GlobalExceptionFeedbackFigure();
				} else if (((IProcessItem) modelPart).getType().equals(
						StrutsConstants.TYPE_COMMENT)) {
					figure = new CommentFeedbackFigure();
				}
			} else {
				figure = new RectangleFigure();
				((RectangleFigure) figure).setXOR(true);
				((RectangleFigure) figure).setFill(true);
				figure.setBackgroundColor(NodeFigure.ghostFillColor);
				figure.setForegroundColor(NodeFigure.whiteColor);
			}

			return figure;
		}

		protected List createSelectionHandles() {
			List list = new ArrayList();
			list.add(createHandle((GraphicalEditPart) getHost(),
					PositionConstants.SOUTH_EAST));
			list.add(createHandle((GraphicalEditPart) getHost(),
					PositionConstants.SOUTH_WEST));
			list.add(createHandle((GraphicalEditPart) getHost(),
					PositionConstants.NORTH_WEST));
			list.add(createHandle((GraphicalEditPart) getHost(),
					PositionConstants.NORTH_EAST));

			return list;
		}

		Handle createHandle(GraphicalEditPart owner, int direction) {
			ResizeHandle handle = new ResizeHandle(owner, direction);
			handle.setCursor(SharedCursors.SIZEALL);
			handle.setDragTracker(new DragEditPartsTracker(owner));
			return handle;
		}
	}
}