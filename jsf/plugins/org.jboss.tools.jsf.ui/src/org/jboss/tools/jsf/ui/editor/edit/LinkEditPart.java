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
package org.jboss.tools.jsf.ui.editor.edit;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.PointList;
import org.jboss.tools.common.model.ui.dnd.DnDUtil;
import org.eclipse.gef.AccessibleEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartListener;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.graphics.Image;

import org.jboss.tools.common.meta.action.XAction;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.gef.GEFGraphicalViewer;
import org.jboss.tools.common.gef.edit.GEFRootEditPart;
import org.jboss.tools.common.gef.figures.GEFLabel;
import org.jboss.tools.common.gef.figures.xpl.CustomLocator;
import org.jboss.tools.jsf.ui.JsfUiPlugin;
import org.jboss.tools.jsf.ui.JsfUIMessages;
import org.jboss.tools.jsf.ui.editor.JSFEditor;
import org.jboss.tools.jsf.ui.editor.figures.ConnectionFigure;
import org.jboss.tools.jsf.ui.editor.figures.FigureFactory;
import org.jboss.tools.jsf.ui.editor.model.ILink;
import org.jboss.tools.jsf.ui.editor.model.ILinkListener;

public class LinkEditPart extends AbstractConnectionEditPart implements
		PropertyChangeListener, ILinkListener, EditPartListener {
	public static final Image icon = ImageDescriptor.createFromFile(
			JSFEditor.class, "icons/shortcut.gif").createImage(); //$NON-NLS-1$

	AccessibleEditPart acc;

	private boolean shortcut;

	private CustomLocator shortcutLocator;

	private GEFLabel shortcutLabel;

	private CustomLocator pathLocator;

	private GEFLabel pathLabel;

	public void activate() {
		super.activate();
		getLink().addPropertyChangeListener(this);
		addEditPartListener(this);
	}

	public void activateFigure() {
		super.activateFigure();
		getFigure().addPropertyChangeListener(
				Connection.PROPERTY_CONNECTION_ROUTER, this);
	}

	public void doDoubleClick(boolean cf) {
		try {
			XModelObject s = (XModelObject) getLinkModel().getSource();
			XAction action = DnDUtil.getEnabledAction(s, null, "Properties.Properties"); //$NON-NLS-1$
			if (action != null)
				action.executeHandler(s, null);
		} catch (XModelException e) {
			JsfUiPlugin.getPluginLog().logError(e);
		}
	}

	public void doMouseDown(boolean cf) {

	}

	public void doMouseUp(boolean cf) {
		if(!(getTarget() instanceof GroupEditPart)) return;
		GroupEditPart g = (GroupEditPart)getTarget();
		if (cf && getLink().isShortcut()) {
			((GEFGraphicalViewer) getViewer()).getGEFEditor()
					.getModelSelectionProvider().setSelection(
							new StructuredSelection(g.getGroupModel().getSource()));
		}
	}

	/**
	 * Adds extra EditPolicies as required.
	 */
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE,
				new LinkEndpointEditPolicy());
		installEditPolicy(EditPolicy.CONNECTION_ROLE, new LinkEditPolicy());
	}

	protected IFigure createFigure() {
		if (getLink() == null)
			return null;
		ConnectionFigure conn = FigureFactory.createNewBendableWire(this,
				getLink());
		PointList list = getLink().getPointList();
		if (list.size() > 0) {
			conn.setManual(true);
			conn.setOldPoints(list.getFirstPoint(), list.getLastPoint());
			conn.setPoints(list);
		}

		pathLabel = new GEFLabel(getLink().getLinkName(),
				FigureFactory.normalColor);
		pathLabel.setFont(getLink().getJSFModel().getOptions()
				.getLinkPathFont());
		pathLabel.setIcon(null);
		pathLabel.setTextAlignment(Label.LEFT);
		pathLabel.setLabelAlignment(Label.LEFT);

		pathLocator = new CustomLocator(conn, false);
		pathLocator.setUDistance(5);
		pathLocator.setVDistance(-13);
		if (!getLink().isShortcut())
			conn.add(pathLabel, pathLocator);

		String text = ""; //$NON-NLS-1$
		if (getLink().getJSFModel().getOptions().showShortcutPath())
			text = getLink().getToGroup().getVisiblePath();
		shortcutLabel = new GEFLabel(text, FigureFactory.normalColor);
		if (getLink().getJSFModel().getOptions().showShortcutIcon())
			shortcutLabel.setIcon(icon);
		shortcutLabel.setFont(getLink().getJSFModel().getOptions()
				.getLinkPathFont());
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

	public ILink getLinkModel() {
		return (ILink) getModel();
	}

	public void save() {
		PointList list = ((ConnectionFigure) getFigure()).getPoints();
		getLink().savePointList(list);
	}

	public void clear() {
		getLink().clearPointList();
	}

	public void deactivate() {
		removeEditPartListener(this);
		getLink().removePropertyChangeListener(this);
		super.deactivate();
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
					e.result = JsfUIMessages.LinkEditPart_Link;
				}
			};
		return acc;
	}

	protected ILink getLink() {
		return (ILink) getModel();
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

	public void setModel(Object model) {
		super.setModel(model);
		((ILink) model).addLinkListener(this);
		shortcut = getLink().isShortcut();
	}

	public boolean isLinkListenerEnable() {
		return true;
	}

	public void linkChange(ILink source) {
		pathLabel.setText(getLink().getLinkName());
		if (getLinkModel().getJSFModel().getOptions().showShortcutPath())
			shortcutLabel.setText(getLink().getToGroup().getVisiblePath());
		else
			shortcutLabel.setText(""); //$NON-NLS-1$
		if (getLinkModel().getJSFModel().getOptions().showShortcutIcon())
			shortcutLabel.setIcon(icon);
		else
			shortcutLabel.setIcon(null);

		getLinkFigure().refreshFont();
		if (shortcut != getLink().isShortcut()) {
			shortcut = getLink().isShortcut();
			if (shortcut) {
				getLinkFigure().add(shortcutLabel, shortcutLocator);
				getLinkFigure().remove(pathLabel);
			} else {
				getLinkFigure().remove(shortcutLabel);
				getLinkFigure().add(pathLabel, pathLocator);
			}
			refresh();
		}

		if (getLinkFigure().isManual()
				&& getLink().getPathFromModel().equals("")) { //$NON-NLS-1$
			getLinkFigure().setManual(false);
			refresh();
		} else if (!getLinkFigure().isManual()
				&& !getLink().getPathFromModel().equals("")) { //$NON-NLS-1$
			getLinkFigure().setManual(true);
			refresh();
		}

	}

	public void linkRemove(ILink source) {
		getLink().removeLinkListener(this);
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
			((GEFRootEditPart) getParent()).setToFront(this);

		}
	}

}
