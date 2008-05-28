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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.jboss.tools.common.model.ui.dnd.DnDUtil;
import org.eclipse.gef.*;
import org.eclipse.gef.requests.DropRequest;
import org.eclipse.swt.accessibility.AccessibleControlEvent;
import org.eclipse.swt.accessibility.AccessibleEvent;

import org.jboss.tools.common.meta.action.XAction;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.Link;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.Page;
import org.jboss.tools.seam.ui.pages.editor.figures.PageFigure;

public class PageEditPart extends PagesEditPart implements PropertyChangeListener, EditPartListener {
	private PageFigure fig = null;

	private boolean single = true;

	public boolean isSingle() {
		return single;
	}

	public void doControlUp() {
	}

	public void doControlDown() {
	}

	public void doMouseHover(boolean cf) {
	}

	public void childAdded(EditPart child, int index) {
	}

	public void partActivated(EditPart editpart) {
	}

	public void partDeactivated(EditPart editpart) {
	}

	public void removingChild(EditPart child, int index) {
	}

	public void selectedStateChanged(EditPart editpart) {
		if (this.getSelected() == EditPart.SELECTED_PRIMARY) {
			((PagesDiagramEditPart) PageEditPart.this.getParent())
					.setToFront(this);

		}
	}

	public boolean isGroupListenerEnable() {
		return true;
	}



	private void refreshTargetLink(Link link) {
		if (link == null)
			return;
		PageEditPart gep = (PageEditPart) getViewer().getEditPartRegistry()
				.get(link.getToElement());
		if (gep == null)
			return;
		gep.refreshTargetConnections();
	}


	protected AccessibleEditPart createAccessible() {
		return new AccessibleGraphicalEditPart() {

			public void getName(AccessibleEvent e) {
				e.result = "EditPart";
			}

			public void getValue(AccessibleControlEvent e) {
			}

		};
	}

	protected List getModelTargetConnections() {
		return getPageModel().getInputLinks();
	}

	protected List getModelSourceConnections() {
		return getPageModel().getOutputLinks();
	}

	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.NODE_ROLE, null);
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, null);
		//installEditPolicy(EditPolicy.COMPONENT_ROLE, new PageEditPolicy());
		//installEditPolicy(EditPolicy.LAYOUT_ROLE, new JSFFlowEditPolicy());
		//installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE,
		//		new PageEditPolicy());
	}

	/**
	 * Returns a newly created Figure to represent this.
	 * 
	 * @return Figure of this.
	 */

	protected IFigure createFigure() {
		fig = new PageFigure(getPageModel());
		((PageFigure) fig).setGroupEditPart(this);
		return fig;
	}

	public PageFigure getGroupFigure() {
		return (PageFigure) getFigure();
	}

	/**
	 * Returns the model of this as a LED.
	 * 
	 * @return Model of this as an LED.
	 */
	public Page getPageModel() {
		return (Page) getModel();
	}

	Dimension size;

	

	protected void refreshVisuals() {
		Point loc = getPageModel().getLocation();
		size = new Dimension(49, 40);
		loc.x -= loc.x % 8;
		loc.y -= loc.y % 8;

		Rectangle r = new Rectangle(loc, size);

		((GraphicalEditPart) getParent()).setLayoutConstraint(this,
				getFigure(), r);
	}

	public ConnectionAnchor getTargetConnectionAnchor(
			ConnectionEditPart connEditPart) {
		ConnectionAnchor anc = getNodeFigure().getConnectionAnchor("1_IN");
		return anc;
	}

	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		Point pt = new Point(((DropRequest) request).getLocation());
		return getNodeFigure().getTargetConnectionAnchorAt(pt);
	}

	public ConnectionAnchor getSourceConnectionAnchor(
			ConnectionEditPart connEditPart) {
		if (single) {
			Link link = (Link) connEditPart.getModel();
			int index = getPageModel().getOutputLinks().indexOf(link);
			return getNodeFigure().getConnectionAnchor((index + 1) + "_OUT");
		} else
			return super.getSourceConnectionAnchor(connEditPart);
	}

	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		if (single) {
			Point pt = new Point(((DropRequest) request).getLocation());
			return getNodeFigure().getSourceConnectionAnchorAt(pt);
		} else
			return super.getSourceConnectionAnchor(request);
	}

	protected List getModelChildren() {
		return getPageModel().getChildren();
	}

	protected void refreshChildren() {
		super.refreshChildren();
		for (int i = 0; i < getChildren().size(); i++) {
			((PageEditPart) getChildren().get(i)).refresh();

		}
	}

}
