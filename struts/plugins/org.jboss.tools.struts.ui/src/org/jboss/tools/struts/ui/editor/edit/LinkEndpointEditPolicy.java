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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.handles.ConnectionHandle;
import org.eclipse.gef.tools.ConnectionEndpointTracker;

import org.jboss.tools.struts.ui.editor.dnd.DndHelper;
import org.jboss.tools.struts.ui.editor.figures.FigureFactory;
import org.jboss.tools.struts.ui.editor.model.ILink;

public class LinkEndpointEditPolicy
	extends org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy 
{
	private List Strutshandles=null;
		
private void addJSFHandles(){
	removeJSFHandles();
	Strutshandles = createJSFHandles();
	IFigure layer = getLayer(LayerConstants.HANDLE_LAYER);
	for (int i = 0; i < Strutshandles.size(); i++)
		layer.add((IFigure)Strutshandles.get(i));
	
}

private void removeJSFHandles(){
	if (Strutshandles == null)
		return;
	IFigure layer = getLayer(LayerConstants.HANDLE_LAYER);
	for (int i = 0; i < Strutshandles.size(); i++)
		layer.remove((IFigure)Strutshandles.get(i));
	Strutshandles = null;
}

protected void addSelectionHandles(){
	
	super.addSelectionHandles();
	addJSFHandles();
		
	getConnectionFigure().setForegroundColor(FigureFactory.selectedColor);
}

protected PolylineConnection getConnectionFigure(){
	return (PolylineConnection)((GraphicalEditPart)getHost()).getFigure();
}

protected void removeSelectionHandles(){
	super.removeSelectionHandles();
	removeJSFHandles();
	getConnectionFigure().setForegroundColor(FigureFactory.normalColor);
}

protected List createSelectionHandles() {
	List list = new ArrayList();
	list.add(new LinkEndHandle((ConnectionEditPart)getHost()));
	list.add(new LinkStartHandle((ConnectionEditPart)getHost()));
	return list;
}

protected List createJSFHandles() {
	List list = new ArrayList();
	PolylineConnection conn = getConnectionFigure();
	boolean flag = true;
	for(int i=0;i<conn.getPoints().size()-3;i++){
		if(flag)flag = false;
		else flag = true;
		list.add(new StrutsConnectionHandle((ConnectionEditPart)getHost(), flag, i+1));
	}

	return list;
}

class LinkEndHandle	extends ConnectionHandle{

public LinkEndHandle(ConnectionEditPart owner) {
	setOwner(owner);
	setLocator(new ConnectionLocator(getConnection(), ConnectionLocator.TARGET));
}

public LinkEndHandle(ConnectionEditPart owner, boolean fixed) {
	super(fixed);
	setOwner(owner);
	setLocator(new ConnectionLocator(getConnection(), ConnectionLocator.TARGET));
}

public LinkEndHandle(){}

protected DragTracker createDragTracker() {
	if (isFixed())
		return null;
	ConnectionEndpointTracker tracker;
	tracker = new LinkEndpointTracker((ConnectionEditPart)getOwner());
	tracker.setCommandName(RequestConstants.REQ_RECONNECT_TARGET);
	tracker.setDefaultCursor(getCursor());
	return tracker;
}
}

class LinkStartHandle extends ConnectionHandle {
	
public LinkStartHandle(ConnectionEditPart owner) {
	setOwner(owner);
	setLocator(new ConnectionLocator(getConnection(),ConnectionLocator.SOURCE));
}

public LinkStartHandle(ConnectionEditPart owner, boolean fixed) {
	super(fixed);
	setOwner(owner);
	setLocator(new ConnectionLocator(getConnection(),ConnectionLocator.SOURCE));
}

public LinkStartHandle(){}

protected DragTracker createDragTracker() {
	if (isFixed()) 
		return null;
	ConnectionEndpointTracker tracker;
	tracker = new LinkEndpointTracker((ConnectionEditPart)getOwner());
	tracker.setCommandName(RequestConstants.REQ_RECONNECT_SOURCE);
	tracker.setDefaultCursor(getCursor());
	return tracker;
}
}

class LinkEndpointTracker extends ConnectionEndpointTracker{
	public LinkEndpointTracker(ConnectionEditPart cpart){
		super(cpart);
	}
	
	public void mouseDown(MouseEvent me, EditPartViewer epv){
		super.mouseDown(me, epv);
		removeJSFHandles();
		DndHelper.drag(((ILink)getHost().getModel()).getSource());
	}

	public void mouseUp(MouseEvent me, EditPartViewer epv){
		super.mouseUp(me, epv);
		if(getHost().getSelected() != EditPart.SELECTED_NONE) 
			addJSFHandles();
	}

	protected boolean handleNativeDragFinished(DragSourceEvent event) {
		DndHelper.dragEnd();
		return false;
	}
}

}