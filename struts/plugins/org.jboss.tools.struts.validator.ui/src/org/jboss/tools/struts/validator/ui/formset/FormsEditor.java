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
package org.jboss.tools.struts.validator.ui.formset;

import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.model.*;
import org.eclipse.jface.viewers.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.util.EclipseResourceUtil;

import org.jboss.tools.common.editor.AbstractSelectionProvider;
import org.jboss.tools.struts.validator.ui.XStudioValidatorPlugin;
import org.jboss.tools.struts.validator.ui.formset.model.*;
import org.jboss.tools.common.model.ui.dnd.ControlDragDrop;

public class FormsEditor {
	protected TreeViewer treeViewer;
	protected FormsetsModel formsetsModel = null;
	protected FSelectionListener[] selectionListeners = new FSelectionListener[2];
	WorkbenchContentProvider treeContent = new FormsetsTreeContent();
	LabelProvider treeLabel = new FormsetsTreeLabel();
	protected MenuInvoker menu = null; 
	FormsEditorDrop dndProvider = new FormsEditorDrop();
	ControlDragDrop dnd = new ControlDragDrop();	

	public FormsEditor() {
		dnd.setProvider(dndProvider);
	}
	
	public void registerMenu(MenuInvoker menu) {
		this.menu = menu;
	}
	
	public Control createControl(Composite parent) {
		treeViewer = new TreeViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		treeViewer.setContentProvider(treeContent);
		treeViewer.setLabelProvider(treeLabel);
		treeViewer.getTree().addSelectionListener(new TS());
		if(formsetsModel != null) treeViewer.setInput(/*formsetsModel*/this);
		menu.setViewer(treeViewer);
		dndProvider.setTreeViewer(treeViewer);
		dnd.enableDrop();
		dnd.enableDrag();
		return treeViewer.getTree();	
	}
	
	public Control getControl() {
		return treeViewer.getTree();
	}
	
	public void addListener(FSelectionListener selectionListener) {
		int i = (selectionListeners[0] == null) ? 0 : 1;
		selectionListeners[i] = selectionListener;
	}

	public void setFormsetsModel(FormsetsModel formsetsModel) {
		this.formsetsModel = formsetsModel;
		if(treeViewer != null && !treeViewer.getTree().isDisposed()) treeViewer.setInput(formsetsModel);
	}

	public void setObject(XModelObject object) {
		reloadTree();
	}
	
	protected void reloadTree() {
		if(treeViewer.getTree() == null || treeViewer.getTree().isDisposed()) return;
		treeViewer.refresh();
	}

	void updateTree(FModel source) {
		FModel s = getSelectedFModel();
		treeViewer.refresh(source);
		if(s != null) treeViewer.setSelection(new StructuredSelection(s), true);
	}
	
	class FormsetsTreeContent extends WorkbenchContentProvider {
		public Object[] getChildren(Object element) {
			FModel f = toFModel(element);
			if(f == null) return new Object[0];
			Object[] os = new Object[f.getChildCount()];
			for (int i = 0; i < os.length; i++) os[i] = f.getChildAt(i);
			return os;
		}

		public Object[] getElements(Object element) {
			return (formsetsModel == null) ? new Object[0] : new Object[]{formsetsModel};
		}

		public Object getParent(Object element) {
			FModel f = toFModel(element);
			return (f == null) ? null : f.getParent();
		}

		public boolean hasChildren(Object element) {
			FModel f = toFModel(element);
			return (f != null && f.getChildCount() > 0);
		}
		
	}
	
	class FormsetsTreeLabel extends LabelProvider implements IColorProvider {
		public Image getImage(Object element) {
			FModel f = toFModel(element);
			XModelObject o = (f == null) ? null : f.getModelObjectForIcon();
			return (o == null) ? null : EclipseResourceUtil.getImage(o);
		}
		public String getText(Object element) {
			FModel f = toFModel(element);
			return (f == null) ? "" : f.toString();
		}

		public Color getForeground(Object element) {
			FModel f = toFModel(element);
			return ((f != null && f.isInherited()) ? FEditorConstants.INHERITED : FEditorConstants.DEFAULT_COLOR);
		}

		public Color getBackground(Object element) {
			return Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
		}		
	}

	private FModel toFModel(Object o) {
		return (!(o instanceof FModel)) ? null : (FModel)o;
	}

	public FModel getSelectedFModel() {
		TreeItem[] ti = null;
		try { 
			ti = treeViewer.getTree().getSelection();
		} catch (Exception ex) {
			XStudioValidatorPlugin.getPluginLog().logError(ex);			
			return null;
		}
		
		return (ti == null || ti.length == 0) ? null : toFModel(ti[0].getData());
	}

	class TS implements SelectionListener {
		public void widgetSelected(SelectionEvent e) {
			updateSelection();
		}

		public void widgetDefaultSelected(SelectionEvent e) {}
	}
	
	public void select(FModel m) {
		treeViewer.setSelection(new StructuredSelection(m), true);
		updateSelection();
	}
	
	void updateSelection() {
		FModel f = getSelectedFModel();
		for (int i = 0; i < selectionListeners.length; i++)
		  if(selectionListeners[i] != null) selectionListeners[i].setSelected(f);
		 selectionProvider.fireSelectionChanged();
	}

	String getSelectionPath() {
		FModel f = getSelectedFModel();
		return (f == null) ? null : f.getPath();
	}

	void setSelectionPath(String path) {
	}
	
	AbstractSelectionProvider selectionProvider = new SP(); 

	public ISelectionProvider getSelectionProvider() {
		return selectionProvider;
	}
	
	class SP extends AbstractSelectionProvider {
		protected XModelObject getSelectedModelObject() {
			FModel f = getSelectedFModel();
			if(f == null) return null;
			XModelObject[] os = f.getModelObjects();
			return (os.length == 0) ? null : os[0];
		}
		
		protected void setSelectedModelObject(XModelObject object) {}
	}

}
