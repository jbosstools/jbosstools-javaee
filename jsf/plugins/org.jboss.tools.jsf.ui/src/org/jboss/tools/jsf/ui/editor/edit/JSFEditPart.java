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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.*;

import org.jboss.tools.jsf.ui.editor.figures.NodeFigure;

/**
 * 
 * @author eskimo(dgolovin@exadel.com)
 *
 */
abstract public class JSFEditPart extends
		org.eclipse.gef.editparts.AbstractGraphicalEditPart implements
		NodeEditPart, PropertyChangeListener {

	/*
	 * 
	 */
	private AccessibleEditPart acc;

	/**
	 * 
	 */
	public void activate() {
		if (isActive())
			return;
		super.activate();
	}

	/**
	 * 
	 * @param cf
	 */
	public void doDoubleClick(boolean cf) { }

	/**
	 * 
	 * @param cf
	 */
	public void doMouseUp(boolean cf) {	}

	/**
	 * 
	 * @param cf
	 */
	public void doMouseDown(boolean cf) { }

	/**
	 * 
	 * @param cf
	 */
	public void doMouseHover(boolean cf) { }

	/**
	 * 
	 */
	public void doControlUp() { }

	/**
	 * 
	 */
	public void doControlDown() { }

	/**
	 * 
	 */
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new JSFElementEditPolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE,
				new JSFNodeEditPolicy());
	}

	/**
	 * 
	 * @return
	 */
	abstract protected AccessibleEditPart createAccessible();

	/**
	 * Makes the EditPart insensible to changes in the model by removing itself
	 * from the model's list of listeners.
	 */
	public void deactivate() {
		if (!isActive())
			return;
		super.deactivate();
	}

	/**
	 * 
	 */
	protected AccessibleEditPart getAccessibleEditPart() {
		if (acc == null)
			acc = createAccessible();
		return acc;
	}

	/**
	 * 
	 * @return
	 */
	protected NodeFigure getNodeFigure() {
		return (NodeFigure) getFigure();
	}

	/**
	 * 
	 */
	public ConnectionAnchor getSourceConnectionAnchor(
			ConnectionEditPart connEditPart) {
		return null;
	}

	/**
	 * 
	 */
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return null;
	}

	/**
	 * 
	 */
	public ConnectionAnchor getTargetConnectionAnchor(
			ConnectionEditPart connEditPart) {
		return null;
	}

	/**
	 * 
	 */
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return null;
	}

	/**
	 * 
	 * @param c
	 * @return
	 */
	final protected String mapConnectionAnchorToTerminal(ConnectionAnchor c) {
		return getNodeFigure().getConnectionAnchorName(c);
	}

	/**
	 * 
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		refreshVisuals();
	}

	/**
	 * 
	 */
	protected void refreshVisuals() {
		Point loc = new Point(100, 100);
		Dimension size = new Dimension(56, 100);
		Rectangle r = new Rectangle(loc, size);

		((GraphicalEditPart) getParent()).setLayoutConstraint(this,
				getFigure(), r);
	}

	/**
	 * 
	 */
	protected void refreshSourceConnections() {
		int i;
		ConnectionEditPart connectEditPart;
		Object modelEntry;

		Map<Object,ConnectionEditPart> mapModelToPart = new HashMap<Object,ConnectionEditPart>();
		List parts = getSourceConnections();

		for (i = 0; i < parts.size(); i++) {
			connectEditPart = (ConnectionEditPart) parts.get(i);
			mapModelToPart.put(connectEditPart.getModel(), connectEditPart);
		}

		List modelEntries = getModelSourceConnections();
		if (modelEntries == null)
			modelEntries = new ArrayList();

		for (i = 0; i < modelEntries.size(); i++) {
			modelEntry = modelEntries.get(i);

			if (i < parts.size()) {
				connectEditPart = (ConnectionEditPart) parts.get(i);
				if (connectEditPart.getModel() == modelEntry) {
					if (connectEditPart.getSource() != this)
						connectEditPart.setSource(this);
					continue;
				}
			}

			connectEditPart = (ConnectionEditPart) mapModelToPart.get(modelEntry);
			if (connectEditPart != null)
				reorderSourceConnection(connectEditPart, i);
			else {
				connectEditPart = createOrFindConnection(modelEntry);
				addSourceConnection(connectEditPart, i);
			}
		}

		List<Object> trash = new ArrayList<Object>();
		for (; i < parts.size(); i++)
			trash.add(parts.get(i));
		for (i = 0; i < trash.size(); i++)
			removeSourceConnection((ConnectionEditPart) trash.get(i));
	}

	/**
	 * 
	 */
	protected void refreshTargetConnections() {
		int i;
		ConnectionEditPart connectEditPart;
		Object modelEntry;

		Map<Object,ConnectionEditPart> mapModelToEditPart = new HashMap<Object,ConnectionEditPart>();
		List connections = getTargetConnections();

		for (i = 0; i < connections.size(); i++) {
			connectEditPart = (ConnectionEditPart) connections.get(i);
			mapModelToEditPart.put(connectEditPart.getModel(), connectEditPart);
		}

		List modelEntries = getModelTargetConnections();
		
		if (modelEntries == null)
			modelEntries = new ArrayList();

		for (i = 0; i < modelEntries.size(); i++) {
			modelEntry = modelEntries.get(i);

			if (i < connections.size()) {
				connectEditPart = (ConnectionEditPart) connections.get(i);
				if (connectEditPart.getModel() == modelEntry) {
					if (connectEditPart.getTarget() != this)
						connectEditPart.setTarget(this);
					continue;
				}
			}

			connectEditPart = (ConnectionEditPart) mapModelToEditPart.get(modelEntry);
			if (connectEditPart != null)
				reorderTargetConnection(connectEditPart, i);
			else {
				connectEditPart = createOrFindConnection(modelEntry);
				addTargetConnection(connectEditPart, i);
			}
		}

		List<Object> removed = new ArrayList<Object>();
		for (; i < connections.size(); i++)
			removed.add(connections.get(i));
		for (i = 0; i < removed.size(); i++)
			removeTargetConnection((ConnectionEditPart) removed.get(i));
	}

	/**
	 * 
	 */
	protected void removeSourceConnection(ConnectionEditPart connection) {
		if (connection.getSource() != this)
			return;
		fireRemovingSourceConnection(connection, getSourceConnections()
				.indexOf(connection));
		connection.deactivate();
		connection.setSource(null);
		primRemoveSourceConnection(connection);
	}

	/**
	 * 
	 */
	protected void removeTargetConnection(ConnectionEditPart connection) {
		if (connection.getTarget() != this)
			return;
		fireRemovingTargetConnection(connection, getTargetConnections()
				.indexOf(connection));
		connection.setTarget(null);
		primRemoveTargetConnection(connection);
	}
}
