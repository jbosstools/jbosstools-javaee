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
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.accessibility.AccessibleEvent;

import org.jboss.tools.common.meta.action.XAction;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.gef.GEFGraphicalViewer;
import org.jboss.tools.common.gef.edit.GEFRootEditPart;
import org.jboss.tools.common.gef.figures.GEFLabel;
import org.jboss.tools.struts.ui.StrutsUIPlugin;
import org.jboss.tools.struts.ui.editor.figures.ConnectionFigure;
import org.jboss.tools.struts.ui.editor.figures.FigureFactory;
import org.jboss.tools.struts.ui.editor.figures.xpl.CustomLocator;
import org.jboss.tools.struts.ui.editor.model.ILink;
import org.jboss.tools.struts.ui.editor.model.ILinkListener;

public class LinkEditPart extends AbstractConnectionEditPart implements
		PropertyChangeListener, ILinkListener, EditPartListener {
	public static final int ARROW_DECOR_INDEX = 0;

	public static final int HEAD_BREAKPOINT_INDEX = 1;

	public static final int TAIL_BREAKPOINT_INDEX = 2;

	public static final int SHORTCUT_INDEX = 3;

	AccessibleEditPart acc;

	private boolean shortcut;

	private CustomLocator shortcutLocator;

	private GEFLabel shortcutLabel;

	public void activate() {
		super.activate();
		getLink().addPropertyChangeListener(this);
		addEditPartListener(this);
	}

	public void linkRelink(ILink source) {
	}

	public void activateFigure() {
		super.activateFigure();
		getFigure().addPropertyChangeListener(
				Connection.PROPERTY_CONNECTION_ROUTER, this);
	}

	public void doMouseDown(boolean cf) {
	}

	public void doMouseUp(boolean cf) {
		if (cf && getLink().isShortcut()) {
			try {
				((GEFGraphicalViewer) getViewer()).getGEFEditor()
						.getModelSelectionProvider().setSelection(
								new StructuredSelection(
										((ProcessItemEditPart) getTarget())
												.getProcessItemModel()
												.getSource()));
			} catch (Exception ex) {
				StrutsUIPlugin.getPluginLog().logError(ex);
			}
			return;
		}
	}

	public void doDoubleClick(boolean cf) {
		try {
			XAction action = DnDUtil.getEnabledAction(
					(XModelObject) getLinkModel().getSource(), null,
					"Properties.Properties");
			if (action != null)
				action.executeHandler(
						(XModelObject) getLinkModel().getSource(), null);
		} catch (Exception e) {
			StrutsUIPlugin.getPluginLog().logError(e);
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

		String text = "";

		if (getLinkModel().getStrutsModel().getOptions().showShortcutPath()) {
			if (getLinkModel().getToProcessItem().isAction())
				text = getLinkModel().getToProcessItem().getVisiblePath();
			else
				text = getLinkModel().getToProcessItem().getName();
		}

		shortcutLabel = new GEFLabel(text, FigureFactory.normalColor);

		if (getLinkModel().getStrutsModel().getOptions().showShortcutIcon())
			shortcutLabel.setIcon(getLink().getToProcessItem().getImage());
		else
			shortcutLabel.setIcon(null);

		shortcutLabel.setFont(getLinkModel().getStrutsModel().getOptions()
				.getPathFont());
		shortcutLabel.setTextAlignment(Label.LEFT);
		shortcutLabel.setLabelAlignment(Label.LEFT);
		shortcutLabel.setIconAlignment(Label.LEFT);
		// shortcutLabel.setVisible(getLink().isShortcut());

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
					e.result = "Link";
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

	public void setModel(Object model) {
		super.setModel(model);
		((ILink) model).addLinkListener(this);
		shortcut = getLink().isShortcut();
	}

	public boolean isLinkListenerEnable() {
		return true;
	}

	public void linkChange(ILink source) {
		if (getLinkModel().getStrutsModel().getOptions().showShortcutPath()) {
			if (getLinkModel().getToProcessItem().isAction())
				shortcutLabel.setText(getLinkModel().getToProcessItem()
						.getVisiblePath());
			else
				shortcutLabel.setText(getLinkModel().getToProcessItem()
						.getName());
		} else
			shortcutLabel.setText("");

		shortcutLabel.setFont(getLinkModel().getStrutsModel().getOptions()
				.getPathFont());
		shortcutLabel.setSize(shortcutLabel.getPreferredSize());

		if (shortcut != getLink().isShortcut()) {
			shortcut = getLink().isShortcut();
			if (shortcut) {
				getLinkFigure().add(shortcutLabel, shortcutLocator);
			} else {
				getLinkFigure().remove(shortcutLabel);
			}
		}

		if (getLinkModel().getStrutsModel().getOptions().showShortcutIcon())
			shortcutLabel.setIcon(getLink().getToProcessItem().getImage());
		else
			shortcutLabel.setIcon(null);
		
		if (getLinkFigure().isManual()
				&& getLink().getPathFromModel().equals("")) {
			getLinkFigure().setManual(false);
		} else if (!getLinkFigure().isManual()
				&& !getLink().getPathFromModel().equals("")) {

			getLinkFigure().setManual(true);
		}
		refresh();

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
