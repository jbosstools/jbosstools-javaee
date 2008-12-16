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
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.gef.requests.DropRequest;
import org.eclipse.swt.accessibility.AccessibleControlEvent;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.jboss.tools.common.model.event.XModelTreeEvent;
import org.jboss.tools.common.model.event.XModelTreeListener;
import org.jboss.tools.common.model.options.PreferenceModelUtilities;
import org.jboss.tools.seam.pages.xml.model.SeamPagesPreference;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.Link;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PageException;
import org.jboss.tools.seam.ui.pages.editor.figures.ExceptionFigure;
import org.jboss.tools.seam.ui.pages.editor.figures.NodeFigure;
import org.jboss.tools.seam.ui.pages.editor.figures.xpl.CompressNameUtil;

public class ExceptionEditPart extends PagesEditPart implements PropertyChangeListener, EditPartListener,
Adapter, XModelTreeListener {
	private NodeFigure fig = null;

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
			((PagesDiagramEditPart) ExceptionEditPart.this.getParent())
					.setToFront(this);

		}
	}

	public boolean isGroupListenerEnable() {
		return true;
	}



	private void refreshTargetLink(Link link) {
		if (link == null)
			return;
		ExceptionEditPart gep = (ExceptionEditPart) getViewer().getEditPartRegistry()
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
		return getExceptionModel().getInputLinks();
	}

	protected List getModelSourceConnections() {
		return getExceptionModel().getOutputLinks();
	}
	
	public void performRequest(Request req) {
		if (req.getType() == GraphicalPartFactory.REQ_INIT_EDIT) {
		    new ViewIDEditManager(this, new ViewIDEditorLocator(
				    (ExceptionFigure) getFigure())).show();
		}
	}

	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.NODE_ROLE, null);
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, null);
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE,
				new ViewIDDirectEditPolicy());
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

	/**
	 * Returns a newly created Figure to represent this.
	 * 
	 * @return Figure of this.
	 */

	protected IFigure createFigure() {
		fig = new ExceptionFigure(getExceptionModel());
		((ExceptionFigure) fig).setEditPart(this);
		return fig;
	}

	/**
	 * Returns the model of this as a LED.
	 * 
	 * @return Model of this as an LED.
	 */
	public PageException getExceptionModel() {
		return (PageException) getModel();
	}

	Dimension size;

	

	/** This returns the label to use when rendering the Exception in a readonly view.
	 *  Converts org.model.Exception to o.m.Exception to save visual space 
	 **/
	String getExceptionReadOnlyLabel() {
		if(getElementModel()==null || getElementModel().getName() == null) {
			return "Unknown Exception";
		} else {
			return CompressNameUtil.getCompressedName(getElementModel().getName());
		}
	}
	protected void refreshVisuals() {
		Point loc = getExceptionModel().getLocation();
		String text = getExceptionReadOnlyLabel();
		int width = getIconWidth()+FigureUtilities.getTextExtents(text, NodeFigure.nodeLabelFont).width;
		
		if(width < getMinimumWidth()) width = getMinimumWidth();
		
		size = new Dimension(width, getVisualHeight());
		adjustForGrid(loc);

		Rectangle r = new Rectangle(loc, size);

		((GraphicalEditPart) getParent()).setLayoutConstraint(this,
				getFigure(), r);
	}
	
	private int getMinimumWidth() {
		return 130;
	}

	private int getVisualHeight() {
		return 21;
	}

	private int getIconWidth() {
		return 30;
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
			int index = getExceptionModel().getOutputLinks().indexOf(link);
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
		return getExceptionModel().getChildren();
	}

	protected void refreshChildren() {
		super.refreshChildren();
		for (int i = 0; i < getChildren().size(); i++) {
			((ExceptionEditPart) getChildren().get(i)).refresh();

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
		if("<initialize>".equals(getExceptionModel().getName())){
			getExceptionModel().setName("");
			DirectEditRequest req = new DirectEditRequest();
			req.setType(GraphicalPartFactory.REQ_INIT_EDIT);
			performRequest(req);
		}
	}
	
	public void deactivate(){
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
		getFigure().repaint();
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
