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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.*;

import org.jboss.tools.struts.ui.editor.figures.NodeFigure;
import org.jboss.tools.struts.ui.editor.model.ILink;

abstract public class StrutsEditPart extends
		org.eclipse.gef.editparts.AbstractGraphicalEditPart implements
		NodeEditPart, PropertyChangeListener {

	private AccessibleEditPart acc;

	public void activate() {
		if (isActive())
			return;
		super.activate();
	}

	abstract public void doDoubleClick(boolean cf);

	abstract public void doMouseDown(boolean cf);

	abstract public void doMouseUp(boolean cf);

	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new StrutsElementEditPolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE,
				new StrutsNodeEditPolicy());
	}

	abstract protected AccessibleEditPart createAccessible();

	/**
	 * Makes the EditPart insensible to changes in the model by removing itself
	 * from the model's list of listeners.
	 */
	public void deactivate() {
		if (!isActive())
			return;
		super.deactivate();
		// getJSFSubpart().removePropertyChangeListener(this);
	}

	protected AccessibleEditPart getAccessibleEditPart() {
		if (acc == null)
			acc = createAccessible();
		return acc;
	}

	protected NodeFigure getNodeFigure() {
		return (NodeFigure) getFigure();
	}

	public ConnectionAnchor getSourceConnectionAnchor(
			ConnectionEditPart connEditPart) {
		return null;
	}

	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return null;
	}

	public ConnectionAnchor getTargetConnectionAnchor(
			ConnectionEditPart connEditPart) {
		return null;
	}

	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return null;
	}

	final protected String mapConnectionAnchorToTerminal(ConnectionAnchor c) {
		return getNodeFigure().getConnectionAnchorName(c);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		refreshVisuals();
	}

	protected void refreshVisuals() {
		Point loc = new Point(100, 100);
		Dimension size = new Dimension(56, 100);
		Rectangle r = new Rectangle(loc, size);

		((GraphicalEditPart) getParent()).setLayoutConstraint(this,
				getFigure(), r);
	}

	protected void refreshSourceConnections() {
		int i;
		ConnectionEditPart part;

		Map fromModelToPart = new HashMap();
		List parts = getSourceConnections();

		for (i = 0; i < parts.size(); i++) {
			part = (ConnectionEditPart) parts.get(i);
			fromModelToPart.put(part.getModel(), part);
		}

		List modelEnties = getModelSourceConnections();
		if (modelEnties == null)
			modelEnties = new ArrayList();

		for (i = 0; i < modelEnties.size(); i++) {
			Object model = modelEnties.get(i);

			if (i < parts.size()) {
				part = (ConnectionEditPart) parts.get(i);
				if (part.getModel() == model) {
					if (part.getSource() != this)
						part.setSource(this);
					continue;
				}
			}

			part = (ConnectionEditPart) fromModelToPart.get(model);
			if (part != null)
				reorderSourceConnection(part, i);
			else {
				part = createOrFindConnection(model);
				if (part != null)
					addSourceConnection(part, i);
			}
		}

		List removed = new ArrayList();
		for (; i < parts.size(); i++)
			removed.add(parts.get(i));
		for (i = 0; i < removed.size(); i++)
			removeSourceConnection((ConnectionEditPart) removed.get(i));
	}

	protected void refreshTargetConnections() {
		int i;
		ConnectionEditPart conn;

		Map fromModelToPart = new HashMap();
		List connList = getTargetConnections();

		for (i = 0; i < connList.size(); i++) {
			conn = (ConnectionEditPart) connList.get(i);
			fromModelToPart.put(conn.getModel(), conn);
		}

		List modelEntries = getModelTargetConnections();
		if (modelEntries == null)
			modelEntries = new ArrayList();

		for (i = 0; i < modelEntries.size(); i++) {
			Object model = modelEntries.get(i);

			if (i < connList.size()) {
				conn = (ConnectionEditPart) connList.get(i);
				if (conn.getModel() == model) {
					if (conn.getTarget() != this)
						conn.setTarget(this);
					continue;
				}
			}

			conn = (ConnectionEditPart) fromModelToPart.get(model);
			if (conn != null)
				reorderTargetConnection(conn, i);
			else {
				conn = createOrFindConnection(model);
				addTargetConnection(conn, i);
			}
		}

		List removed = new ArrayList();
		for (; i < connList.size(); i++)
			removed.add(connList.get(i));
		for (i = 0; i < removed.size(); i++)
			removeTargetConnection((ConnectionEditPart) removed.get(i));
	}

	protected void removeSourceConnection(ConnectionEditPart connection) {
		if (connection.getSource() != this)
			return;
		fireRemovingSourceConnection(connection, getSourceConnections()
				.indexOf(connection));
		connection.deactivate();
		connection.setSource(null);
		primRemoveSourceConnection(connection);
	}

	protected void removeTargetConnection(ConnectionEditPart connection) {
		if (connection.getTarget() != this)
			return;
		fireRemovingTargetConnection(connection, getTargetConnections()
				.indexOf(connection));
		connection.setTarget(null);
		primRemoveTargetConnection(connection);
	}

	protected void refreshTargetLink(ILink link) {
		if (link == null) {
			return;
		}
		if (link.getToProcessItem() == null) {
			link.setTarget();
			if (link.getToProcessItem() == null) {
				return;
			}
		}
		ProcessItemEditPart gep = (ProcessItemEditPart) getViewer()
				.getEditPartRegistry().get(link.getToProcessItem());
		if (gep == null) {
			return;
		}
		gep.refreshTargetConnections();
	}

}
