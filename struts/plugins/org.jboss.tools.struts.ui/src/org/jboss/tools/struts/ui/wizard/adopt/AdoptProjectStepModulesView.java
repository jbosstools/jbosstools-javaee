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
package org.jboss.tools.struts.ui.wizard.adopt;

import org.eclipse.core.runtime.IAdaptable;
import org.jboss.tools.struts.ui.wizard.sync.ModuleTable;
import org.jboss.tools.common.model.ui.attribute.editor.PropertyEditor;
import org.jboss.tools.common.model.ui.attribute.editor.TableSelectionEditor;
import org.jboss.tools.common.model.ui.wizards.special.AbstractSpecialWizardStep;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.ui.widgets.DefaultSettings;
import org.jboss.tools.jst.web.project.WebModuleConstants;
import org.jboss.tools.struts.webprj.model.helpers.adopt.AdoptProjectContext;

public class AdoptProjectStepModulesView extends AbstractSpecialWizardStep implements WebModuleConstants {
	protected PropertyEditor tableSelectionEditor;
	protected LocalTableAdapter  tableAdapter;
	protected ModuleTable moduleTable = new ModuleTable();
	protected AdoptProjectContext context = null;
	
    public AdoptProjectStepModulesView() {
		tableSelectionEditor = new TableSelectionEditor(DefaultSettings.getDefault());
		tableAdapter = new LocalTableAdapter();
		tableSelectionEditor.setInput(tableAdapter);
		tableSelectionEditor.setLabelText("Provide information about your modules");
    }
    
	public Control createControl(Composite parent) {
		GridData gd;
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 10;
		layout.verticalSpacing = 10;
		layout.marginHeight = 10;
		layout.marginWidth = 10;
		composite.setLayout(layout);

		// table
		FieldEditor editor = tableSelectionEditor.getFieldEditor(composite);
		editor.fillIntoGrid(composite, 2);
		
		// separator
		Label separator = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		separator.setLayoutData(gd);
		
		gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 400;
		composite.setLayoutData(gd);
		
		Control c2 = moduleTable.createControl(composite, support.getTarget());
		GridLayout l = (GridLayout)((Composite)c2).getLayout();
		l.marginWidth = 0;
		GridData d = new GridData(GridData.FILL_BOTH);
		d.horizontalSpan = 2;
		c2.setLayoutData(d);
		return composite;
	}

	public void save() {
		if(tableAdapter != null) tableAdapter.store();
	}
	
	public void dispose() {
		super.dispose();
		if (tableAdapter!=null) tableAdapter.dispose();
		tableAdapter = null;
		if (tableSelectionEditor!=null) tableSelectionEditor.dispose();
		tableSelectionEditor = null;
		if (moduleTable!=null) moduleTable.dispose();
		moduleTable = null;
	}

	protected AdoptProjectContext getContext() {
		return (AdoptProjectContext)support.getProperties().get("context");
	}

	class LocalTableAdapter extends LabelProvider implements IAdaptable, 
																IStructuredContentProvider, 
																ITableLabelProvider, 
																ISelectionProvider, 
																ISelectionChangedListener {		
		XModelObject[] modules;
		XModelObject selectedModule = null;
		
		// IStructuredContentProvider
		TableViewer table;
		public Object[] getElements(Object inputElement) {
			context = (AdoptProjectContext)support.getProperties().get("context");
			modules = context.getModules();
			return modules;
		}

		public void dispose() {}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			if (newInput==null) return;
			
			table = ((TableViewer)viewer);
			TableColumn column = new TableColumn(table.getTable(), SWT.NONE);
			column.setText("Name");
			column.setWidth(120);
			column = new TableColumn(table.getTable(), SWT.NONE);
			column.setText("URI");
			column.setWidth(415);
			table.getTable().setHeaderVisible(true);
			table.getTable().setLinesVisible(true);
			table.getTable().showSelection();			
		}
		
		// ITableLabelProvider
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}
		
		public String getColumnText(Object element, int columnIndex) {
			String result = "<default>";
			XModelObject modelObject = (XModelObject)element;
			switch (columnIndex) {
				case 0: {
					result = modelObject.getAttributeValue(ATTR_NAME);
					if ((result==null)||(result.length()==0)) result = "<default>";
					break;
				}
				case 1: {
					result = modelObject.getAttributeValue(ATTR_URI); 
					break;
				}
			}
			return result;
		}

		// IAdaptable
		public Object getAdapter(Class adapter) {
			if(adapter != null && adapter.isAssignableFrom(this.getClass()))
				return this;
			Assert.isTrue(true, "LocalTableAdapter instance itself cannot provide adapter for "+adapter.getName());
			return null;
		}
		// ISelectionProvider
		public void addSelectionChangedListener(ISelectionChangedListener listener) {
		}
		public ISelection getSelection() {
			StructuredSelection selection = new StructuredSelection(new Object[]{modules[0]});
			return selection;
		}
		public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		}
		public void setSelection(ISelection selection) {
		}

		// ISelectionChangedListener
		public void selectionChanged(SelectionChangedEvent event) {
			StructuredSelection selection = (StructuredSelection)event.getSelection();
			setSelectedModule((XModelObject)selection.getFirstElement());
		}
		
		public void setSelectedModule(XModelObject modelObject) {
			moduleTable.setModelObject(modelObject);
		}
		
		public void store() {
			moduleTable.commit();
		}

	}
	
	public Point getMaximumSize() {
		return null;
	}

	public Point getMinimumSize() {
		return null;
	}

}
