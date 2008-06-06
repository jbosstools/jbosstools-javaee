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

import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.gef.AccessibleEditPart;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartListener;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.requests.DropRequest;
import org.eclipse.gef.requests.SelectionRequest;
import org.eclipse.swt.accessibility.AccessibleControlEvent;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.Link;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.Page;
import org.jboss.tools.seam.ui.pages.editor.figures.NodeFigure;
import org.jboss.tools.seam.ui.pages.editor.figures.PageFigure;

public class PageEditPart extends PagesEditPart implements
		PropertyChangeListener, EditPartListener, Adapter {
	private PageFigure fig = null;

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

	public void performRequest(Request req) {
		if (RequestConstants.REQ_OPEN.equals(req.getType())) {
			SelectionRequest request = (SelectionRequest) req;
			Point mouseLocation = request.getLocation()
				.translate(-getGroupFigure().getLocation().x,
							-getGroupFigure().getLocation().y);
			if (mouseLocation.x < 16 && mouseLocation.y > getGroupFigure().getSize().height-16) {
				getPageModel().setParamsVisible(!getPageModel().isParamsVisible());
				refresh();
				fig.repaint();
			}
		}
	}

	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.NODE_ROLE, null);
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, null);
		// installEditPolicy(EditPolicy.COMPONENT_ROLE, new PageEditPolicy());
		// installEditPolicy(EditPolicy.LAYOUT_ROLE, new JSFFlowEditPolicy());
		// installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE,
		// new PageEditPolicy());
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
		int height = 23 + getPageModel().getOutputLinks().size()
				* NodeFigure.LINK_HEIGHT;

		if (getPageModel().getOutputLinks().size() == 0)
			height = 23 + NodeFigure.LINK_HEIGHT;

		size = new Dimension(50, height);
		adjustForGrid(loc);

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
		Link link = (Link) connEditPart.getModel();
		int index = getPageModel().getOutputLinks().indexOf(link);
		return getNodeFigure().getConnectionAnchor((index + 1) + "_OUT");
	}

	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		Point pt = new Point(((DropRequest) request).getLocation());
		return getNodeFigure().getSourceConnectionAnchorAt(pt);
	}

	// protected List getModelChildren() {
	// return getPageModel().getChildren();
	// }

	protected void refreshChildren() {
		super.refreshChildren();
		for (int i = 0; i < getChildren().size(); i++) {
			((PageEditPart) getChildren().get(i)).refresh();

		}
	}

	/**
	 * @see org.eclipse.gef.EditPart#activate()
	 */
	public void activate() {
		if (isActive())
			return;
		((Notifier) getModel()).eAdapters().add(this);
		super.activate();
	}

	public void deactivate() {
		if (!isActive())
			return;
		((Notifier) getModel()).eAdapters().remove(this);
		super.deactivate();
	}

	/**
	 * @see org.eclipse.emf.common.notify.Adapter#notifyChanged(org.eclipse.emf.common.notify.Notification)
	 */
	public void notifyChanged(Notification notification) {
		refresh();
		refreshVisuals();
	}

	/**
	 * )
	 * 
	 * @see org.eclipse.emf.common.notify.Adapter#getTarget()
	 */
	public Notifier getTarget() {

		return null;
	}

	/**
	 * @see org.eclipse.emf.common.notify.Adapter#isAdapterForType(java.lang.Object)
	 */
	public boolean isAdapterForType(Object type) {
		return false;
	}

	/**
	 * @see org.eclipse.emf.common.notify.Adapter#setTarget(org.eclipse.emf.common.notify.Notifier)
	 */
	public void setTarget(Notifier newTarget) {
	}
}
