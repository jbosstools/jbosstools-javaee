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

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.Cursors;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.handles.SquareHandle;
import org.eclipse.gef.tools.ConnectionEndpointTracker;

/**
 * A handle used on a {@link Connection}.
 */
public class StrutsConnectionHandle
	extends SquareHandle
	implements PropertyChangeListener
{

private boolean vertical = false;
private int index;


public StrutsConnectionHandle(ConnectionEditPart owner, boolean vertical, int index) {
	this.vertical = vertical;
	this.index = index;
	if(vertical)
		setCursor(Cursors.SIZEN);
	else
		setCursor(Cursors.SIZEW);
		
	setOwner(owner);
	setLocator(new JSFMidpointLocator(getConnection(), index));
}

protected DragTracker createDragTracker() {
	ConnectionEndpointTracker tracker;
	tracker = new StrutsConnectionDragTracker((ConnectionEditPart)getOwner(), vertical, index);
	//tracker.setCommandName(RequestConstants.REQ_RECONNECT_SOURCE);
	//tracker.setDefaultCursor(getCursor());
	return tracker;
}

/**
 * Adds this as a {@link org.eclipse.draw2d.FigureListener} to the 
 * owner's {@link org.eclipse.draw2d.Figure}.
 */
public void addNotify() {
	super.addNotify();
	getConnection().addPropertyChangeListener(Connection.PROPERTY_POINTS, this);
}

/**
 * Returns the Connection this handle is on.
 */
public Connection getConnection() {
	return (Connection)getOwnerFigure();
}

protected boolean isVertical() {
	return vertical;
}

public void propertyChange(PropertyChangeEvent evt) {
	if (evt.getPropertyName().equals(Connection.PROPERTY_POINTS))
		revalidate();
}

public void removeNotify(){
	getConnection().removePropertyChangeListener(Connection.PROPERTY_POINTS, this);
	super.removeNotify();
}
class JSFMidpointLocator extends ConnectionLocator{
	private int index;

	public JSFMidpointLocator(Connection c, int i) {
		super(c);
		index = i;
	}

	protected int getIndex() {
		return index;
	}

	protected Point getReferencePoint() {
		Connection conn = super.getConnection();
		if(getIndex()+1 > conn.getPoints().size()-2){
			StrutsConnectionHandle.this.setVisible(false);
			return new Point(0,0);
		}
		Point p = Point.SINGLETON;
		Point p1 = conn.getPoints().getPoint(getIndex());
		Point p2 = conn.getPoints().getPoint(getIndex()+1);
		conn.translateToAbsolute(p1);
		conn.translateToAbsolute(p2);
		p.x = (p2.x-p1.x)/2 + p1.x;
		p.y = (p2.y-p1.y)/2 + p1.y;
		return p;
	}	
}
}