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
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.FreeformViewport;
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
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.gef.requests.DropRequest;
import org.eclipse.gef.requests.SelectionRequest;
import org.eclipse.swt.accessibility.AccessibleControlEvent;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.jboss.tools.common.gef.edit.GEFRootEditPart;
import org.jboss.tools.common.meta.action.XAction;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.event.XModelTreeEvent;
import org.jboss.tools.common.model.event.XModelTreeListener;
import org.jboss.tools.common.model.options.PreferenceModelUtilities;
import org.jboss.tools.common.model.ui.dnd.DnDUtil;
import org.jboss.tools.seam.pages.xml.model.SeamPagesPreference;
import org.jboss.tools.seam.ui.pages.SeamUiPagesPlugin;
import org.jboss.tools.seam.ui.pages.editor.PagesEditor;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.Link;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.Page;
import org.jboss.tools.seam.ui.pages.editor.figures.NodeFigure;
import org.jboss.tools.seam.ui.pages.editor.figures.PageFigure;

public class PageEditPart extends PagesEditPart implements
		PropertyChangeListener, EditPartListener, Adapter, XModelTreeListener {
	private PageFigure fig = null;
	
	PagesEditor editor;
	
	public void setEditor(PagesEditor editor){
		this.editor = editor;
	}

	public void doControlUp() {
	}

	public void doControlDown() {
	}

	public void doMouseHover(boolean cf) {
	}

	public void childAdded(EditPart child, int index) {
	}
	
	public void doMouseDown(Point mp) {
		FreeformViewport diagram = (FreeformViewport)((GEFRootEditPart)editor.getScrollingGraphicalViewer().getRootEditPart()).getFigure();
	    Point mouseLocation = mp.scale(1/((GEFRootEditPart)editor.getScrollingGraphicalViewer().getRootEditPart()).getZoomManager().getZoom())
	    .translate(-getPageFigure().getLocation().x, -getPageFigure().getLocation().y)
	    .translate(diagram.getClientArea().getLocation());
	    if(mouseLocation.x < 15 && mouseLocation.y > getPageFigure().getSize().height-15){
	    	getPageModel().setParamsVisible(!getPageModel().isParamsVisible());
			refresh();
			fig.repaint();
	    }
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
	
	public void nodeChanged(XModelTreeEvent event){
		String path = event.getModelObject().getPath();
		if(path.equals(SeamPagesPreference.SEAM_PAGES_EDITOR_PATH)){
			NodeFigure.nodeLabelFont = SeamPagesPreference.getFont(SeamPagesPreference.VIEW_PATH_FONT.getValue(), NodeFigure.nodeLabelFont);
			refreshVisuals();
		}
	}
	
    public void structureChanged(XModelTreeEvent event){
    	
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
				.translate(-getPageFigure().getLocation().x,
							-getPageFigure().getLocation().y);
			if (mouseLocation.x < 16 && mouseLocation.y > getPageFigure().getSize().height-16) {
				getPageModel().setParamsVisible(!getPageModel().isParamsVisible());
				refresh();
				fig.repaint();
			}else{
				try {
					XAction action = DnDUtil.getEnabledAction(
							(XModelObject) getPageModel().getData(), null,
							"OpenPage");
					if (action != null)
						action.executeHandler((XModelObject) getPageModel()
								.getData(), null);
				} catch (XModelException e) {
					SeamUiPagesPlugin.log(e);
				}
			}
		}else if (req.getType() == GraphicalPartFactory.REQ_INIT_EDIT) {
		    new ViewIDEditManager(this, new ViewIDEditorLocator(
				    (PageFigure) getFigure())).show();
		}else if (req.getType() == RequestConstants.REQ_DIRECT_EDIT) {
		    new ViewIDEditManager(this, new ViewIDEditorLocator(
				    (PageFigure) getFigure()), true).show();
		}
	}

	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.NODE_ROLE, null);
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, null);
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE,
				new PageEditPolicy());
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE,
				new ViewIDDirectEditPolicy());
	}

	/**
	 * Returns a newly created Figure to represent this.
	 * 
	 * @return Figure of this.
	 */

	protected IFigure createFigure() {
		fig = new PageFigure(getPageModel());
		((PageFigure) fig).setPageEditPart(this);
		return fig;
	}

	public PageFigure getPageFigure() {
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
		int height = getVisualHeight() + getPageModel().getOutputLinks().size()
				* NodeFigure.LINK_HEIGHT;

		if (getPageModel().getOutputLinks().size() == 0)
			height = getVisualHeight() + NodeFigure.LINK_HEIGHT;
		
		String name = getPageModel().getName();
		
		if(name == null)
			name = "";

		int width = getIconWidth()+FigureUtilities.getTextExtents(name, NodeFigure.nodeLabelFont).width;
		
		if(width < getMinimumWidth()) width = getMinimumWidth();
		
		size = new Dimension(width, height);
		adjustForGrid(loc);

		Rectangle r = new Rectangle(loc, size);

		((GraphicalEditPart) getParent()).setLayoutConstraint(this,
				getFigure(), r);
	}
	
	private int getVisualHeight() {
		return 21;
	}

	private int getIconWidth() {
		return 30;
	}
	
	private int getMinimumWidth() {
		return 130;
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
		PreferenceModelUtilities.getPreferenceModel().addModelTreeListener(this);
		super.activate();
		if("<initialize>".equals(getPageModel().getName())){
			getPageModel().setName("");
			DirectEditRequest req = new DirectEditRequest();
			req.setType(GraphicalPartFactory.REQ_INIT_EDIT);
			performRequest(req);
		}
	}

	public void deactivate() {
		if (!isActive())
			return;
		((Notifier) getModel()).eAdapters().remove(this);
		PreferenceModelUtilities.getPreferenceModel().removeModelTreeListener(this);
		super.deactivate();
	}

	/**
	 * @see org.eclipse.emf.common.notify.Adapter#notifyChanged(org.eclipse.emf.common.notify.Notification)
	 */
	public void notifyChanged(Notification notification) {
		refresh();
		refreshVisuals();
		getPageFigure().repaint();
		PagesDiagramEditPart diagram = (PagesDiagramEditPart)getParent().getRoot().getViewer().getEditPartRegistry().get(getPageModel().getParent());
		if(diagram != null) diagram.refresh();
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
