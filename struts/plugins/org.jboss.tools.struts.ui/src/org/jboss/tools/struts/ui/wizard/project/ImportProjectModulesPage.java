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
package org.jboss.tools.struts.ui.wizard.project;

import java.beans.PropertyChangeEvent;
import java.util.*;
import org.jboss.tools.struts.ui.wizard.sync.ModuleTable;
import org.jboss.tools.struts.ui.wizard.sync.MutableModuleListTableModel;
import org.jboss.tools.common.model.ui.action.*;
import org.jboss.tools.common.model.ui.objecteditor.XTable;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.struts.ui.wizard.addstruts.AddStrutsSupportWizard;
import org.jboss.tools.jst.web.context.ImportWebDirProjectContext;

public class ImportProjectModulesPage extends WizardPage implements java.beans.PropertyChangeListener {
	private ModuleTable moduleTable = new ModuleTable();
	private ModuleListTableModel listModel = new ModuleListTableModel();
	private XTable list = new XTable();
	boolean lock = false;
	
	Label addStrutsSupportBarSeparator = null;
	CommandBar addStrutsSupportBar;

	String addStrutsSupportCommand = "         Add Struts Support         ";
	private ImportWebDirProjectContext context;

	public ImportProjectModulesPage(ImportWebDirProjectContext context) {
		super("");		 //$NON-NLS-1$
		this.context = context;
		list.setAutoResize(true);
		list.setTableProvider(listModel);
	}
	
	public void dispose() {
		super.dispose();
		if (moduleTable!=null) moduleTable.dispose();
		moduleTable = null;
		if (list!=null) list.dispose();
		list = null;
		if (addStrutsSupportBar!=null) addStrutsSupportBar.dispose();
		addStrutsSupportBar = null;
	}
	
	Composite parent;
	Composite composite; 

	public void createControl(Composite parent) {
		this.parent = parent;
		GridData gd;
		Composite composite = new Composite(parent, SWT.NONE);
		this.composite = composite;
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 10;
		layout.verticalSpacing = 10;
		layout.marginHeight = 4;
		layout.marginWidth = 4;
		composite.setLayout(layout);
		//composite.setBackground(new Color(null,255,0,0));

		Control c1 = list.createControl(composite);
		GridData g1 = new GridData(GridData.FILL_HORIZONTAL);
		g1.horizontalSpan = 2;
		g1.heightHint = 80;
		g1.widthHint = 550;
		c1.setLayoutData(g1);
		list.getTable().addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					updateSelection();
					validate();
				}
			}
		);

		// separator
		Label separator = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		separator.setLayoutData(gd);
		
		gd = new GridData(GridData.FILL_BOTH);
		///gd.heightHint = 600;
		composite.setLayoutData(gd);
		
		Control c2 = moduleTable.createControl(composite, context.getTarget()); //support.getTarget());
		GridLayout l = (GridLayout)((Composite)c2).getLayout();
		l.marginWidth = 0;
		l.marginHeight = 0;
		GridData d = new GridData(GridData.FILL_BOTH);
		d.horizontalSpan = 2;
		c2.setLayoutData(d);
		
		setControl(composite);
		moduleTable.addPropertyChangeListener(this);

		validate(); 
	}
	
	private void createSupportBar() {
		if(addStrutsSupportBarSeparator != null && !addStrutsSupportBarSeparator.isDisposed()) return;
		addStrutsSupportBarSeparator = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
		addStrutsSupportBarSeparator.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_BLUE));
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		addStrutsSupportBarSeparator.setLayoutData(gd);
		addStrutsSupportBar = new CommandBar();
		addStrutsSupportBar.setCommands(new String[]{addStrutsSupportCommand});
		addStrutsSupportBar.addCommandBarListener(new CreateConfigOperation());
		addStrutsSupportBar.createControl(composite);
		addStrutsSupportBar.getLayout().left = moduleTable.getFieldInset();
		composite.update();
		composite.layout();
		composite.redraw();
	}
	
	private void removeSupportBar() {
		if(addStrutsSupportBarSeparator != null) {
			if(!addStrutsSupportBarSeparator.isDisposed()) addStrutsSupportBarSeparator.dispose();
			addStrutsSupportBarSeparator = null;
		}
		if(addStrutsSupportBar != null) {
			addStrutsSupportBar.dispose();
			addStrutsSupportBar = null;
			composite.update();
			composite.layout();
			composite.redraw();
		}
	}

	public void setDialogSize() {
		getShell().setSize(600, 550);
	}	

	public void setVisible(boolean visible)	{
		if (visible) {
			listModel.setModelObject(getModules());
			list.update();
			list.getTable().setSelection(0);
			updateSelection();
		} else {	
			moduleTable.commit();
		}
//		if(visible) {
//			addStrutsSupportBarSeparator.setVisible(true);
//			addStrutsSupportBar.getControl().setVisible(true);
//		}
		if(visible) {
			validate();
		} 
		super.setVisible(visible);
		if(visible) {
//			setDialogSize();
			validate();
		} 
	}
	
	void updateSelection() {
		if(lock) return;
		lock = true;
		moduleTable.commit();
		moduleTable.setModelObject(getSelectedObject());
		lock = false;
	}
	
	public XModelObject getSelectedObject() {
		int i = list.getTable().getSelectionIndex();
		return (i < 0) ? null : listModel.getModelObject(i);
	}
	
	private class ModuleListTableModel extends MutableModuleListTableModel {
		public ModuleListTableModel() {
			attrs = new String[]{"Name", "URI"};
		}
		int[] hints = new int[]{10,20};
		public int getWidthHint(int c) {
			return hints[c];
		}

	}
	
	public void propertyChange(PropertyChangeEvent arg0) {
		if(lock) return;
		moduleTable.commit();
		validate();
		list.update();
	}
	
	public void validate() {
		XModelObject[] modules = getModules().toArray(new XModelObject[0]);
		String message = context.getModulesErrorMessage(modules, getSelectedObject());
		boolean hasToAddStrutsSupport = message == null && modules.length == 0;
		if(hasToAddStrutsSupport) {
			createSupportBar();
		} else {
			removeSupportBar();
		}
		if(hasToAddStrutsSupport) {
			message = "The project should contain at least one Struts module to be imported";
		} 
		setPageComplete(message == null);
		setErrorMessage(message);
		list.update();
	}
	
	class CreateConfigOperation implements CommandBarListener {
		public void action(String command) {
			int i = AddStrutsSupportWizard.run(getControl().getShell(), context);
			if(i != WizardDialog.OK) return;
			listModel.setModelObject(getModules());
			list.update();
			list.getTable().setSelection(0);
			updateSelection();
			validate();
		}
	}
	
	private List<XModelObject> getModules() {
		List<XModelObject> list = new ArrayList<XModelObject>();
		XModelObject[] os = context.getAllModules();
		for (int i = 0; i < os.length; i++) {
			list.add(os[i]); 
		}
		return list;
	}
	
}
