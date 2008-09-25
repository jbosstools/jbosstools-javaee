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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.handles.AbstractHandle;
import org.eclipse.gef.handles.ConnectionHandle;
import org.eclipse.gef.tools.ConnectionEndpointTracker;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.events.MouseEvent;
import org.jboss.tools.jst.web.model.ReferenceObject;
import org.jboss.tools.seam.ui.pages.editor.dnd.DndHelper;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.Link;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.Page;
import org.jboss.tools.seam.ui.pages.editor.figures.ConnectionFigure;
import org.jboss.tools.seam.ui.pages.editor.figures.FigureFactory;


public class LinkEndpointEditPolicy
	extends org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy {
	private List handles = null;

	private void addPagesHandles() {
		removePagesHandles();
		handles = createHandles();
		IFigure layer = getLayer(LayerConstants.HANDLE_LAYER);
		for (int i = 0; i < handles.size(); i++)
			layer.add((IFigure) handles.get(i));

	}

	private void removePagesHandles() {
		if (handles == null)
			return;
		IFigure layer = getLayer(LayerConstants.HANDLE_LAYER);
		for (int i = 0; i < handles.size(); i++)
			layer.remove((IFigure) handles.get(i));
		handles = null;
	}

	protected void addSelectionHandles() {
		Page page=null;
		Link link = ((LinkEditPart)getHost()).getLinkModel();
		if(link != null && link.getFromElement() instanceof Page)
			page = (Page)link.getFromElement();
		
		if(page != null && page.getData() != null && page.getData() instanceof ReferenceObject && ((ReferenceObject)page.getData()).getReference() == null){
			
		}else
			super.addSelectionHandles();
		addPagesHandles();

		getConnectionFigure().setForegroundColor(FigureFactory.selectedColor);
	}

	protected ConnectionFigure getConnectionFigure() {
		return (ConnectionFigure) ((GraphicalEditPart) getHost()).getFigure();
	}

	protected void removeSelectionHandles() {
		super.removeSelectionHandles();
		removePagesHandles();
		getConnectionFigure().setForegroundColor(FigureFactory.normalColor);
	}

	protected List createSelectionHandles() {
		List<ConnectionHandle> list = new ArrayList<ConnectionHandle>();
		list.add(new LinkEndHandle((ConnectionEditPart) getHost()));
		list.add(new LinkStartHandle((ConnectionEditPart) getHost()));
		return list;
	}

	protected List createHandles() {
		List<AbstractHandle> list = new ArrayList<AbstractHandle>();
		PolylineConnection conn = getConnectionFigure();
		boolean flag = true;
		for (int i = 0; i < conn.getPoints().size() - 3; i++) {
			if (flag)
				flag = false;
			else
				flag = true;
			list.add(new PagesConnectionHandle((ConnectionEditPart) getHost(),
					flag, i + 1));
		}

		return list;
	}

	class LinkEndHandle extends ConnectionHandle {

		public LinkEndHandle(ConnectionEditPart owner) {
			setOwner(owner);
			setLocator(new ConnectionLocator(getConnection(),
					ConnectionLocator.TARGET));
		}

		public LinkEndHandle(ConnectionEditPart owner, boolean fixed) {
			super(fixed);
			setOwner(owner);
			setLocator(new ConnectionLocator(getConnection(),
					ConnectionLocator.TARGET));
		}

		public LinkEndHandle() {
		}

		protected DragTracker createDragTracker() {
			if (isFixed())
				return null;
			ConnectionEndpointTracker tracker;
			tracker = new LinkEndpointTracker((ConnectionEditPart) getOwner());
			tracker.setCommandName(RequestConstants.REQ_RECONNECT_TARGET);
			tracker.setDefaultCursor(getCursor());
			return tracker;
		}
	}

	class LinkStartHandle extends ConnectionHandle {

		public LinkStartHandle(ConnectionEditPart owner) {
			setOwner(owner);
			setLocator(new ConnectionLocator(getConnection(),
					ConnectionLocator.SOURCE));
		}

		public LinkStartHandle(ConnectionEditPart owner, boolean fixed) {
			super(fixed);
			setOwner(owner);
			setLocator(new ConnectionLocator(getConnection(),
					ConnectionLocator.SOURCE));
		}

		public LinkStartHandle() {
		}

		protected DragTracker createDragTracker() {
			if (isFixed())
				return null;
			ConnectionEndpointTracker tracker;
			tracker = new LinkEndpointTracker((ConnectionEditPart) getOwner());
			tracker.setCommandName(RequestConstants.REQ_RECONNECT_SOURCE);
			tracker.setDefaultCursor(getCursor());
			return tracker;
		}
	}

	class LinkEndpointTracker extends ConnectionEndpointTracker {
		public LinkEndpointTracker(ConnectionEditPart cpart) {
			super(cpart);
		}

		public void mouseDown(MouseEvent me, EditPartViewer epv) {
			super.mouseDown(me, epv);
			removePagesHandles();
			DndHelper.drag(((Link) getHost().getModel()).getData());
		}

		public void mouseUp(MouseEvent me, EditPartViewer epv) {
			super.mouseUp(me, epv);
			if (getHost().getSelected() != EditPart.SELECTED_NONE)
				addPagesHandles();
		}

		protected boolean handleNativeDragFinished(DragSourceEvent event) {
			DndHelper.dragEnd();
			return false;
		}
	}

}