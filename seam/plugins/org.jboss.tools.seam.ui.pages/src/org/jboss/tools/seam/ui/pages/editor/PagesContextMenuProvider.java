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
package org.jboss.tools.seam.ui.pages.editor;

import java.util.*;
import org.eclipse.jface.action.*;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import org.eclipse.draw2d.geometry.Point;
import org.jboss.tools.common.meta.XModelEntity;
import org.jboss.tools.common.model.ui.action.XModelObjectActionList;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.jst.web.model.ReferenceObject;
import org.jboss.tools.seam.pages.xml.model.SeamPagesConstants;
import org.jboss.tools.seam.ui.pages.editor.edit.PagesDiagramEditPart;
import org.jboss.tools.seam.ui.pages.editor.edit.SelectionUtil;

public class PagesContextMenuProvider	extends org.eclipse.gef.ContextMenuProvider {
	private ActionRegistry actionRegistry;
	private MouseEvent lastDownEvent = null;
	private Point lastPoint = null;

	public PagesContextMenuProvider(EditPartViewer viewer, ActionRegistry registry) {
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
		if(s.isEmpty() || !(s instanceof IStructuredSelection)) return;
		IStructuredSelection ss = (IStructuredSelection)s;
		XModelObject object = SelectionUtil.getTarget(ss.getFirstElement());
		if(object != null) {
			String entityName = object.getModelEntity().getName();
			if(entityName.equals(SeamPagesConstants.ENT_DIAGRAM_ITEM_OUTPUT)
				&& object instanceof ReferenceObject && ((ReferenceObject)object).getReference() == null) {
				entityName = "SeamPagesDiagramItemOutputVirtual_ActionList";
			}
			else if(entityName.equals(SeamPagesConstants.ENT_DIAGRAM_ITEM) 
				&& object instanceof ReferenceObject && ((ReferenceObject)object).getReference() == null) {
				entityName = "SeamPagesDiagramItemVirtual_ActionList";
			}
			PagesDiagramEditPart part = (PagesDiagramEditPart)getViewer().getRootEditPart().getChildren().get(0);
			Properties p = new Properties();
			if(lastDownEvent != null) {
				lastPoint = new Point(lastDownEvent.x, lastDownEvent.y);				
				part.getFigure().translateToRelative(lastPoint);
				lastDownEvent = null;
			}
			if(lastPoint != null) {
				p.setProperty("mouse.x", "" + lastPoint.x); //$NON-NLS-1$ //$NON-NLS-2$
				p.setProperty("mouse.y", "" + lastPoint.y); //$NON-NLS-1$ //$NON-NLS-2$
			}
			p.put("diagramEditPart", part);

			XModelEntity entity = object.getModel().getMetaData().getEntity(entityName);
			if(entity == null) return;
			XModelObjectActionList list = new XModelObjectActionList(entity.getActionList(), object, SelectionUtil.getTargets(ss), new Object[]{object, p});
			Menu menu = getMenu();
			list.createMenu(menu);
			list.removeLastSeparator(menu);
		}
	}
	
}
