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

import org.eclipse.draw2d.ArrowLocator;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.FreeformViewport;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.gef.AccessibleEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartListener;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.common.gef.edit.GEFRootEditPart;
import org.jboss.tools.common.gef.figures.GEFLabel;
import org.jboss.tools.common.gef.figures.xpl.CustomLocator;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.event.XModelTreeEvent;
import org.jboss.tools.common.model.event.XModelTreeListener;
import org.jboss.tools.common.model.options.PreferenceModelUtilities;
import org.jboss.tools.common.model.util.XModelUtil;
import org.jboss.tools.seam.pages.xml.model.SeamPagesPreference;
import org.jboss.tools.seam.ui.pages.editor.PagesEditor;
import org.jboss.tools.seam.ui.pages.editor.PagesEditor.ModelSelectionProvider;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.Link;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.Page;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesElement;
import org.jboss.tools.seam.ui.pages.editor.figures.ConnectionFigure;
import org.jboss.tools.seam.ui.pages.editor.figures.FigureFactory;

public class LinkEditPart extends AbstractConnectionEditPart implements
		PropertyChangeListener, EditPartListener, XModelTreeListener {
	private static final Image icon = ImageDescriptor.createFromFile(
			PagesEditor.class, "icons/shortcut.gif").createImage();
	
	private static final Font pathFont = SeamPagesPreference.getFont(SeamPagesPreference.LINK_PATH_FONT.getValue(), null);
	

	AccessibleEditPart acc;
	
	PagesEditor editor;

	private boolean shortcut;

	private CustomLocator shortcutLocator;

	private GEFLabel shortcutLabel;

	private CustomLocator pathLocator;

	private GEFLabel pathLabel;
	
	private PageEditPart pagePart = null;
	
	public void setEditor(PagesEditor editor){
		this.editor = editor;
	}
	
	private boolean showShortcutPath(){
		return SeamPagesPreference.SHOW_SHORTCUT_PATH.getValue().equals("yes");
	}

	private boolean showShortcutIcon(){
		return SeamPagesPreference.SHOW_SHORTCUT_ICON.getValue().equals("yes");
	}

	public void activate() {
		if (!isActive()) {
			((Notifier) getModel()).eAdapters().add(adapter);
		}
		super.activate();
		addEditPartListener(this);
		PreferenceModelUtilities.getPreferenceModel().addModelTreeListener(this);
		Page page=null;
		if(getLinkModel().getFromElement() instanceof Page)
			page = (Page)getLinkModel().getFromElement();
		if(page != null){
			//pagePart = (PageEditPart)getViewer().getEditPartRegistry().get(page);
			if(pagePart != null){
				getLinkFigure().setVisible(false);
				pagePart.addEditPartListener(this);
			}
		}
	}

	public void activateFigure() {
		super.activateFigure();
		getFigure().addPropertyChangeListener(
				Connection.PROPERTY_CONNECTION_ROUTER, this);
	}

	/**
	 * Adds extra EditPolicies as required.
	 */
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE,
				new LinkEndpointEditPolicy());
		installEditPolicy(EditPolicy.CONNECTION_ROLE, new LinkEditPolicy());
	}
	
	PolygonDecoration arrow;

	protected IFigure createFigure() {
		if (getLink() == null)
			return null;
		ConnectionFigure conn = FigureFactory.createNewBendableWire(this,
				getLink());
		arrow = (PolygonDecoration)conn.getChildren().get(0);
		
		if (getLink().isShortcut())
			conn.remove(arrow);
		
		PointList list = getLink().getPointList();
		if (list.size() > 0) {
			conn.setManual(true);
			conn.setOldPoints(list.getFirstPoint(), list.getLastPoint());
			conn.setPoints(list);
			//conn.repaint();
		}

		pathLabel = new GEFLabel(getLink().getName(), FigureFactory.normalColor);
		pathLabel.setFont(pathFont);
		pathLabel.setIcon(null);
		pathLabel.setTextAlignment(Label.LEFT);
		pathLabel.setLabelAlignment(Label.LEFT);

		pathLocator = new CustomLocator(conn, false);
		pathLocator.setUDistance(5);
		pathLocator.setVDistance(-13);
		if (!getLink().isShortcut())
			conn.add(pathLabel, pathLocator);

		String text = "";
		if (showShortcutPath())
		if(getLink().getToElement() != null)
			text = getLink().getToElement().getName();
		
		shortcutLabel = new GEFLabel(text, FigureFactory.normalColor);
		if (showShortcutIcon())
			shortcutLabel.setIcon(icon);
		shortcutLabel.setFont(pathFont);
		shortcutLabel.setTextAlignment(Label.LEFT);
		shortcutLabel.setLabelAlignment(Label.LEFT);
		shortcutLabel.setIconAlignment(Label.LEFT);

		shortcutLocator = new CustomLocator(conn, false);
		shortcutLocator.setUDistance(18);
		shortcutLocator.setVDistance(-6);
		if (getLink().isShortcut())
			conn.add(shortcutLabel, shortcutLocator);

		return conn;
	}

	public Link getLinkModel() {
		return (Link) getModel();
	}

	public void save() {
		PointList list = ((ConnectionFigure) getFigure()).getPoints();
		getLink().savePointList(list);
	}

	public void clear() {
		getLink().clearPointList();
	}

	public void deactivate() {
		if(pagePart != null)
			pagePart.removeEditPartListener(this);
		removeEditPartListener(this);
		PreferenceModelUtilities.getPreferenceModel().removeModelTreeListener(this);
		if (isActive()) {
			((Notifier) getModel()).eAdapters().remove(this);
		}
		super.deactivate();
	}
	
	public void nodeChanged(XModelTreeEvent event){
		String path = event.getModelObject().getPath();
		if(path.equals(SeamPagesPreference.SEAM_PAGES_EDITOR_PATH)){
			linkChange(getLink());
		}
	}
	
    public void structureChanged(XModelTreeEvent event){
    	
    }

	public void deactivateFigure() {
		getFigure().removePropertyChangeListener(
				Connection.PROPERTY_CONNECTION_ROUTER, this);
		super.deactivateFigure();
	}

	public AccessibleEditPart getAccessibleEditPart() {
		if (acc == null)
			acc = new AccessibleGraphicalEditPart() {
				public void getName(AccessibleEvent e) {
					e.result = "Link";
				}
			};
		return acc;
	}

	protected Link getLink() {
		return (Link) getModel();
	}

	protected ConnectionFigure getLinkFigure() {
		return (ConnectionFigure) getFigure();
	}

	public void propertyChange(PropertyChangeEvent event) {
		String property = event.getPropertyName();
		if ("value".equals(property)) //$NON-NLS-1$
			refreshVisuals();
	}

	/**
	 * Refreshes the visual aspects of this, based upon the model (Wire). It
	 * changes the wire color depending on the state of Wire.
	 * 
	 */
	protected void refreshVisuals() {
	}
	
	public void performRequest(Request req) {
		if (RequestConstants.REQ_OPEN.equals(req.getType())) {
			if(getLink().isShortcut()){
				PagesElement element = getLink().getToElement();
				if(element == null) return;
				
				PagesEditPart part = (PagesEditPart)editor.getScrollingGraphicalViewer().getEditPartRegistry().get(element);
				if(part == null) return;
				
				Object[] sel = new Object[1];
				sel[0] = part;
				StructuredSelection newSelection = new StructuredSelection(sel);
				
				editor.getScrollingGraphicalViewer().setSelection(newSelection);
				
				PagesDiagramEditPart diagram = (PagesDiagramEditPart) editor.getScrollingGraphicalViewer()
				.getRootEditPart().getChildren().get(0);
				FreeformViewport vp = diagram.getFreeformViewport();
				
				((ModelSelectionProvider)editor.getModelSelectionProvider()).scroll(vp, part.getNodeFigure());
			}else{
				XModelUtil.openProperyDialog((XModelObject)getLink().getData());
			}
		}
	}

	public void setModel(Object model) {
		super.setModel(model);
		//((ILink) model).addLinkListener(this);
		shortcut = getLink().isShortcut();
	}

	public boolean isLinkListenerEnable() {
		return true;
	}

	public void linkChange(Link source) {
		pathLabel.setFont(SeamPagesPreference.getFont(SeamPagesPreference.LINK_PATH_FONT.getValue(), pathFont));
		pathLabel.setText(getLink().getName());
		
		if (showShortcutPath()){
			shortcutLabel.setFont(SeamPagesPreference.getFont(SeamPagesPreference.LINK_PATH_FONT.getValue(), pathFont));
			if(getLink().getToElement() != null)
				shortcutLabel.setText(getLink().getToElement().getName());
			else
				shortcutLabel.setText("");
		}else
			shortcutLabel.setText("");
		
		if (showShortcutIcon())
			shortcutLabel.setIcon(icon);
		else
			shortcutLabel.setIcon(null);

		getLinkFigure().refreshFont();
		if (shortcut != getLink().isShortcut()) {
			shortcut = getLink().isShortcut();
			if (shortcut) {
				getLinkFigure().add(shortcutLabel, shortcutLocator);
				getLinkFigure().remove(pathLabel);
				getLinkFigure().remove(arrow);
			} else {
				getLinkFigure().remove(shortcutLabel);
				getLinkFigure().add(pathLabel, pathLocator);
				getLinkFigure().add(arrow, new ArrowLocator(getLinkFigure(), ConnectionLocator.TARGET));
			}
			refresh();
		}
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
		if(editpart == pagePart){
			if(pagePart.getSelected() == EditPart.SELECTED_PRIMARY){
				getFigure().setVisible(true);
			}else
				getFigure().setVisible(false);
		}
		if (editpart == this && this.getSelected() == EditPart.SELECTED_PRIMARY) {
			((GEFRootEditPart) getParent()).setToFront(this);
		}
	}

	Adapter adapter = new AdapterImpl();

	class AdapterImpl implements Adapter {

		/**
		 * @see org.eclipse.emf.common.notify.Adapter#notifyChanged(org.eclipse.emf.common.notify.Notification)
		 */
		public void notifyChanged(Notification notification) {
			linkChange(getLink());
			pathLabel.setText(getLinkModel().getName());
			if (getLinkFigure().isManual()
					&& getLink().getPathFromModel().equals("")) {
				getLinkFigure().setManual(false);
				clear();
			} else if (!getLinkFigure().isManual()
					&& !getLink().getPathFromModel().equals("")) {
				getLinkFigure().setManual(true);
			}
			
			PointList list = getLink().getPointList();
			if(list.size() == 0)((ConnectionFigure)figure).setManual(false);
			refresh();
			refreshVisuals();
			((ConnectionFigure)figure).layout();
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

}
