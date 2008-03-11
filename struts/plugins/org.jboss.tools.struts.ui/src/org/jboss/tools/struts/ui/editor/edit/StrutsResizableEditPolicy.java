/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.jboss.tools.struts.ui.editor.edit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.Rectangle;

import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Handle;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.SharedCursors;
import org.eclipse.gef.editpolicies.ResizableEditPolicy;
import org.eclipse.gef.handles.ResizeHandle;
import org.eclipse.gef.tools.DragEditPartsTracker;

import org.jboss.tools.struts.StrutsConstants;
import org.jboss.tools.struts.ui.editor.figures.ActionFeedbackFigure;
import org.jboss.tools.struts.ui.editor.figures.CommentFeedbackFigure;
import org.jboss.tools.struts.ui.editor.figures.GlobalExceptionFeedbackFigure;
import org.jboss.tools.struts.ui.editor.figures.GlobalForwardFeedbackFigure;
import org.jboss.tools.struts.ui.editor.figures.NodeFigure;
import org.jboss.tools.struts.ui.editor.figures.PageFeedbackFigure;
import org.jboss.tools.struts.ui.editor.model.IProcessItem;

/**
 * 
 * @author eskimo(dgolovin@exadel.com)
 *
 */
public class StrutsResizableEditPolicy extends ResizableEditPolicy {

	/**
	 * 
	 * @return 
	 */
	protected IFigure createDragSourceFeedbackFigure() {
		IFigure figure = createFigure((GraphicalEditPart) getHost(), null);

		figure.setBounds(getInitialFeedbackBounds());
		addFeedback(figure);
		return figure;
	}

	protected IFigure createFigure(GraphicalEditPart part, IFigure parent) {
		IFigure ch = getCustomFeedbackFigure(part.getModel());

		if (parent != null) parent.add(ch);

		Rectangle bounds = part.getFigure().getBounds().getCopy();
		IFigure i1 = part.getFigure().getParent();

		while (i1 != ((GraphicalEditPart) part.getParent()).getFigure()) {
			i1.translateToParent(bounds);
			i1 = i1.getParent();
		}

		ch.setBounds(bounds);
		Iterator i2 = part.getChildren().iterator();

		while (i2.hasNext()) createFigure((GraphicalEditPart) i2.next(), ch);

		return ch;
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

	/**
	 * Returns the layer used for displaying feedback.
	 * 
	 * @return the feedback layer
	 */
	protected IFigure getFeedbackLayer() {
		return getLayer(LayerConstants.SCALED_FEEDBACK_LAYER);
	}

	/**
	 * @see org.eclipse.gef.editpolicies.NonResizableEditPolicy#initialFeedbackRectangle()
	 */
	protected Rectangle getInitialFeedbackBounds() {
		return getHostFigure().getBounds();
	}

	Handle createHandle(GraphicalEditPart owner, int direction) {
		ResizeHandle handle = new ResizeHandle(owner, direction);
		handle.setCursor(SharedCursors.SIZEALL);
		handle.setDragTracker(new DragEditPartsTracker(owner));
		return handle;
	}

	Handle createResizeHandle(GraphicalEditPart owner, int direction) {
		ResizeHandle handle = new ResizeHandle(owner, direction);
		return handle;
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
		list.add(createResizeHandle((GraphicalEditPart) getHost(),
				PositionConstants.EAST));
		list.add(createResizeHandle((GraphicalEditPart) getHost(),
				PositionConstants.WEST));

		return list;
	}

}