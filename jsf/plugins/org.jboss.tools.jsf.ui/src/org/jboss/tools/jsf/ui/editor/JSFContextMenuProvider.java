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
package org.jboss.tools.jsf.ui.editor;

import java.util.*;
import org.eclipse.jface.action.*;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import org.eclipse.draw2d.geometry.Point;
import org.jboss.tools.common.model.ui.action.XModelObjectActionList;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.jsf.ui.editor.edit.JSFDiagramEditPart;
import org.jboss.tools.jsf.ui.editor.edit.JSFEditPart;
import org.jboss.tools.jsf.ui.editor.edit.LinkEditPart;
import org.jboss.tools.jsf.ui.editor.model.IJSFElement;

public class JSFContextMenuProvider	extends org.eclipse.gef.ContextMenuProvider {
	private ActionRegistry actionRegistry;
	private MouseEvent lastDownEvent = null;

	public JSFContextMenuProvider(EditPartViewer viewer, ActionRegistry registry) {
		super(viewer);
		viewer.getControl().addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent e) {
				lastDownEvent = e;
			}
		});
		setActionRegistry(registry);
	}

	public void buildContextMenu(IMenuManager manager) {
		GEFActionConstants.addStandardActionGroups(manager);
	}

	//never used
	ActionRegistry getActionRegistry() {
		return actionRegistry;
	}

	private void setActionRegistry(ActionRegistry registry) {
		actionRegistry = registry;
	}
	
	protected void update(boolean force, boolean recursive) {
		if(!isDirty() && !force) return;
		if(!menuExist()) return;
		MenuItem[] is = getMenu().getItems();
		for (int i = 0; i < is.length; i++) {
			if(!is[i].isDisposed()) is[i].dispose();
		} 
		ISelection s = getViewer().getSelection();
		if(s.isEmpty() || !(s instanceof StructuredSelection)) return;
		StructuredSelection ss = (StructuredSelection)s;
		XModelObject object = getTarget(ss.getFirstElement());
		if(object != null) {
			Properties p = new Properties();
			if(lastDownEvent != null) {
				Point point = new Point(lastDownEvent.x, lastDownEvent.y); 
				
				((JSFDiagramEditPart)getViewer().getRootEditPart().getChildren().get(0)).getFigure().translateToRelative(point);
				p.setProperty("process.mouse.x", "" + point.x); //$NON-NLS-1$ //$NON-NLS-2$
				p.setProperty("process.mouse.y", "" + point.y); //$NON-NLS-1$ //$NON-NLS-2$
				lastDownEvent = null;
			}
			XModelObjectActionList list = new XModelObjectActionList(object.getModelEntity().getActionList(), object, getTargets(ss), new Object[]{object, p});
			Menu menu = getMenu();
			list.createMenu(menu);
			list.removeLastSeparator(menu);
		}
	}
	
	private XModelObject[] getTargets(StructuredSelection ss) {
		if(ss.size() < 2) return null;
		Iterator it = ss.iterator();
		ArrayList<XModelObject> l = new ArrayList<XModelObject>();
		while(it.hasNext()) {
			XModelObject o = getTarget(it.next());
			if(o != null) l.add(o);		
		}
		return l.toArray(new XModelObject[0]);
	}
	
	private XModelObject getTarget(Object selected) {
		if(selected instanceof JSFEditPart) {
			JSFEditPart part = (JSFEditPart)selected;
			Object partModel = part.getModel();
			if(partModel instanceof IJSFElement) {
				return (XModelObject)((IJSFElement)partModel).getSource();
			}
		}
		if(selected instanceof LinkEditPart) {
			LinkEditPart part = (LinkEditPart)selected;
			Object partModel = part.getModel();
			if(partModel instanceof IJSFElement) {
				return (XModelObject)((IJSFElement)partModel).getSource();
			}
		}

		return null;
	}

}