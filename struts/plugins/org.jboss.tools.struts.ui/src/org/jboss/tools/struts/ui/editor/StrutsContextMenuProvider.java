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
package org.jboss.tools.struts.ui.editor;

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
import org.jboss.tools.struts.ui.editor.edit.LinkEditPart;
import org.jboss.tools.struts.ui.editor.edit.ProcessItemEditPart;
import org.jboss.tools.struts.ui.editor.edit.StrutsDiagramEditPart;
import org.jboss.tools.struts.ui.editor.edit.StrutsEditPart;
import org.jboss.tools.struts.ui.editor.figures.BreakPointFigure;
import org.jboss.tools.struts.ui.editor.model.IStrutsElement;
import org.jboss.tools.struts.ui.editor.model.IStrutsModel;

public class StrutsContextMenuProvider	extends org.eclipse.gef.ContextMenuProvider {
//	private ActionRegistry actionRegistry;
	private MouseEvent lastDownEvent = null;
	private IStrutsModel model;

	public StrutsContextMenuProvider(IStrutsModel model, EditPartViewer viewer, ActionRegistry registry) {
		super(viewer);
		this.model = model;
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

//	private ActionRegistry getActionRegistry() {
//		return actionRegistry;
//	}

	private void setActionRegistry(ActionRegistry registry) {
//		actionRegistry = registry;
	}

	private boolean menuExist() {
		return getMenu() != null && !getMenu().isDisposed();
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
				
				((StrutsDiagramEditPart)getViewer().getRootEditPart().getChildren().get(0)).getFigure().translateToRelative(point);
				p.setProperty("process.mouse.x", "" + point.x);
				p.setProperty("process.mouse.y", "" + point.y);
				lastDownEvent = null;
			}
			XModelObjectActionList list;
			if(ss.getFirstElement() instanceof LinkEditPart){
				LinkEditPart lep = (LinkEditPart)ss.getFirstElement();
				if(BreakPointFigure.itemFlag){
					BreakPointFigure.itemFlag = false;
					XModelObject xmo = (XModelObject)lep.getLinkModel().getToProcessItem().getSource();
					list = new XModelObjectActionList(xmo.getModel().getMetaData().getEntity(xmo.getModelEntity().getName() + "_BreakpointActions").getActionList() ,xmo,null, new Object[]{xmo, p});	  
				}else if(BreakPointFigure.outputFlag){
					BreakPointFigure.outputFlag = false;
					list = new XModelObjectActionList(object.getModel().getMetaData().getEntity("StrutsProcessItemOutput_BreakpointActions").getActionList() ,object,null, new Object[]{object, p});
				}else{
					list = new XModelObjectActionList(model.getHelper().getLinkActionList(object), object, null, new Object[]{object, p});
				}
			}else if(ss.getFirstElement() instanceof ProcessItemEditPart){
				ProcessItemEditPart pep = (ProcessItemEditPart)ss.getFirstElement();
				if(BreakPointFigure.itemFlag){
					BreakPointFigure.itemFlag = false;
					XModelObject xmo = (XModelObject) pep.getProcessItemModel().getSource();
					list = new XModelObjectActionList(xmo.getModel().getMetaData().getEntity(xmo.getModelEntity().getName() + "_BreakpointActions").getActionList() ,xmo,null, new Object[]{xmo, p});	  
				}else{
					list = new XModelObjectActionList(object.getModelEntity().getActionList(), object, getTargets(ss), new Object[]{object, p});
				}
			}else list = new XModelObjectActionList(object.getModelEntity().getActionList(), object, getTargets(ss), new Object[]{object, p});
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
		if(selected instanceof StrutsEditPart) {
			StrutsEditPart part = (StrutsEditPart)selected;
			Object partModel = part.getModel();
			if(partModel instanceof IStrutsElement) {
				return (XModelObject)((IStrutsElement)partModel).getSource();
			}
		}
		if(selected instanceof LinkEditPart) {
			LinkEditPart part = (LinkEditPart)selected;
			Object partModel = part.getModel();
			if(partModel instanceof IStrutsElement) {
				return (XModelObject)((IStrutsElement)partModel).getSource();
			}
		}

		return null;
	}

}