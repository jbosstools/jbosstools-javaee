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
package org.jboss.tools.struts.ui.wizard.sync;

import java.beans.PropertyChangeEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.jboss.tools.common.model.ui.action.CommandBar;
import org.jboss.tools.common.model.ui.action.CommandBarListener;
import org.jboss.tools.common.model.ui.objecteditor.XTable;
import org.jboss.tools.common.model.ui.wizards.special.AbstractSpecialWizardStep;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.jst.web.project.WebModuleConstants;
import org.jboss.tools.struts.ui.Messages;
import org.jboss.tools.struts.ui.StrutsUIPlugin;
import org.jboss.tools.struts.webprj.model.helpers.sync.SyncProjectContext;

public class SyncProjectStepView extends AbstractSpecialWizardStep implements java.beans.PropertyChangeListener {
	protected ArrayList objects = new ArrayList();
	protected Composite panel;
	protected MutableModuleListTableModel listmodel = new MutableModuleListTableModel();
	protected XTable list = new XTable();
///	protected ModuleInfoTableModel tablemodel = new ModuleInfoTableModel(); 
///	protected XTable table = new XTable();
	protected SyncBar bar = new SyncBar();
	protected ModuleTable moduleTable = new ModuleTable();
	boolean isDataChanged = false;

	public SyncProjectStepView() {
		list.setAutoResize(true);
		list.setTableProvider(listmodel);
		bar.getCommandBar().addCommandBarListener(new CL());
///		tablemodel.setListener(listmodel);
///		table.setTableProvider(tablemodel);
		moduleTable.setListener(listmodel);
	}

	public Control createControl(Composite parent) {
		panel = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		panel.setLayout(gridLayout);
		Control c1 = createListControl(panel);
		GridData g1 = new GridData(GridData.FILL_BOTH);
		g1.heightHint = 150;
		c1.setLayoutData(g1);
		
		Composite cs = new Composite(panel, SWT.NONE);
		gridLayout = new GridLayout();
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 10;
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		cs.setLayout(gridLayout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		cs.setLayoutData(gd);
		Label separator = new Label(cs, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
///		Control c2 = createTableControl(panel);
		Control c2 = moduleTable.createControl(panel, support.getTarget());
		GridData g2 = new GridData(GridData.FILL_HORIZONTAL);
		///g2.heightHint = 80;
		g2.widthHint = 600;
		c2.setLayoutData(g2);
		update();
		
		moduleTable.addPropertyChangeListener(this); 
		return panel;
	}
	
	protected Control createListControl(Composite parent) {
		GridData gd;
		Control control;
		
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = 10;
		gridLayout.marginWidth = 10;
		gridLayout.horizontalSpacing = 10;
		gridLayout.verticalSpacing = 10;
		composite.setLayout(gridLayout);
		
		Composite c = new Composite(composite, SWT.NONE);
		
		gridLayout = new GridLayout(3, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		c.setLayout(gridLayout);
		
		control = list.createControl(c);
		list.getTable().addSelectionListener(new SL());
		gd = new GridData(GridData.FILL_BOTH);
		control.setLayoutData(gd);

		control = new Label(c, SWT.NONE);
		gd = new GridData();
		gd.widthHint = 5;
		control.setLayoutData(gd);
		
		control = bar.getCommandBar().createControl(c);
		bar.getCommandBar().getLayout().buttonWidth = convertHorizontalDLUsToPixels(control, IDialogConstants.BUTTON_WIDTH); 
		gd = new GridData(GridData.FILL_VERTICAL);
		gd.widthHint = convertHorizontalDLUsToPixels(control, IDialogConstants.BUTTON_WIDTH);
		control.setLayoutData(gd);
		
		gd = new GridData(GridData.FILL_BOTH);
		c.setLayoutData(gd);

		gd = new GridData(GridData.FILL_BOTH);
		composite.setLayoutData(gd);
		return composite;
	}
	
	public void update() {
		lock = true;
		listmodel.setModelObject(objects = (ArrayList)support.getProperties().get("modules")); //$NON-NLS-1$
		SyncProjectContext context = (SyncProjectContext)support.getProperties().get("context"); //$NON-NLS-1$
		XModelObject webxml = context.getWebXML();
		bar.addEnabled = (webxml != null && webxml.isObjectEditable());
		if(webxml != null) {
			IFile f = (IFile)webxml.getAdapter(IFile.class);
			if(f != null) moduleTable.setDefaultLocation(f.getLocation().toString());
		}
		
		try {
			list.update();
		} catch (Exception e) {
			StrutsUIPlugin.getPluginLog().logError(e);
		}

		lock = false;
		int sel = -1;
		for (int i = 0; i < objects.size(); i++) {
			XModelObject o = listmodel.getModelObject(i);
			if(sel < 0 && !ModuleInfoValidator.isModuleDataValid(o)) sel = i;
		}
		if(sel < 0 && objects.size() > 0) sel = 0;
		if(sel >= 0) {
			if(list.getControl() != null) {
				list.getTable().deselectAll();
				list.getTable().select(sel);
			} 
	    } 
	    if(list.getTable() != null)	updateSelection();
	}

	protected int convertHorizontalDLUsToPixels(Control control, int dlus) {
		GC gc= new GC(control);
		gc.setFont(control.getFont());
		int averageWidth= gc.getFontMetrics().getAverageCharWidth();
		gc.dispose();

		double horizontalDialogUnitSize = averageWidth * 0.25;

		return (int)Math.round(dlus * horizontalDialogUnitSize);
	}


	boolean lock = false;
	
	public void dispose() {
		super.dispose();
		objects = null;
		if (listmodel!=null) listmodel.dispose();
		listmodel = null;
		if (list!=null) list.dispose();
		list = null;
		if (bar!=null) bar.dispose();
		bar = null;
		if (moduleTable!=null) moduleTable.dispose();
		moduleTable = null;
	}
	
	public XModelObject getSelectedObject() {
		int i = list.getTable().getSelectionIndex();
		return (i < 0) ? null : listmodel.getModelObject(i);
	}

	class SL extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			updateSelection();
			validate();
		}
	}
	
	void updateSelection() {
		if(lock) return;
		lock = true;
		save();
///		tablemodel.setModelObject(getSelectedObject());
		bar.setModelObject(getSelectedObject());
		moduleTable.update();
///		table.update();
		moduleTable.setModelObject(getSelectedObject());
		lock = false;
	}

	public void save() {
		stopEditing();
	}
	
	public void stopEditing() {
///		if(table.getViewer() != null && table.getViewer().isCellEditorActive()) {
///			XCellEditor editor = (XCellEditor)table.getViewer().getCellEditors()[1];
///			editor.fireApplyEditorValue();
///		}
		moduleTable.commit();
	}
	
	class CL implements CommandBarListener {
		public void action(String name) {
			if(SyncBar.ADD.equals(name)) {
				XActionInvoker.invoke("WebPrjAddModuleHelper", "Add", support.getTarget(), support.getProperties()); //$NON-NLS-1$ //$NON-NLS-2$
				list.update();
				moduleTable.update();
				bar.updateControl();
				validate();
				return;
			}
			XModelObject s = getSelectedObject();
			if(s == null) return;
			if(SyncBar.RESTORE.equals(name)) {
				String uri = s.getAttributeValue("URI"); //$NON-NLS-1$
				String path = s.getAttributeValue("path on disk"); //$NON-NLS-1$
				if(uri.length() == 0 && path.length() > 0) {
					int i = path.lastIndexOf('/');
					if(i >= 0) s.setAttributeValue("URI", "/WEB-INF/" + path.substring(i + 1)); //$NON-NLS-1$ //$NON-NLS-2$
				}
				s.set("state", "restored"); //$NON-NLS-1$ //$NON-NLS-2$
			} else if(SyncBar.DELETE.equals(name)) {
				int si = list.getTable().getSelectionIndex();
				if(si < 0) return;
				XModelObject so = listmodel.getModelObject(si);
				boolean isConfig = WebModuleConstants.ENTITY_WEB_CONFIG.equals(so.getModelEntity().getName());
				String msg = (!isConfig) ?
				  MessageFormat.format(Messages.SyncProjectStepView_DeleteModule, listmodel.getValueAt(si, 0))
				  : MessageFormat.format(Messages.SyncProjectStepView_DeleteURI, listmodel.getValueAt(si, 1));
				MessageDialog d = new MessageDialog(panel.getShell(), Messages.SyncProjectStepView_Confirmation, null, msg, MessageDialog.QUESTION, new String[]{Messages.SyncProjectStepView_OK, Messages.SyncProjectStepView_Cancel}, 0);
				int i = d.open();
				if(i != 0) return;
				s.set("state", "deleted"); //$NON-NLS-1$ //$NON-NLS-2$
				XModelObject[] sc = s.getChildren();
				for (int k = 0; k < sc.length; k++) sc[k].set("state", "deleted"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			list.update();
			bar.updateControl();
			moduleTable.update();
			validate();
///			table.update();
		}
	}
	
	public Point getMaximumSize() {
		return null;
	}

	public Point getMinimumSize() {
		return new Point(600,SWT.DEFAULT);
	}

	public void propertyChange(PropertyChangeEvent arg0) {
		if(lock) return;
		moduleTable.commit();
		validate();
	}
	
	public void validate() {
		Properties p = new Properties();
		p.put("modules", objects); //$NON-NLS-1$
		XModelObject s = getSelectedObject();
		if(s == null) p.remove("selected"); //$NON-NLS-1$
		else p.put("selected", s);  //$NON-NLS-1$
		isDataChanged = true;
		if(validator != null) {
			validator.validate(p);
		}
		list.update();
		if(validator != null) {
			wizard.dataChanged(validator, p);
		}
	}
	
	public boolean isDataChanged() {
		return isDataChanged;
	}
}

class SyncBar {
	static String ADD = Messages.SyncProjectStepView_Add;
	static String DELETE = Messages.SyncProjectStepView_Delete;
	static String RESTORE = Messages.SyncProjectStepView_Restore;
	static String[] ADD_DELETE = new String[]{ADD, DELETE};
	protected CommandBar bar = new CommandBar();
	protected XModelObject selected = null;
	protected boolean addEnabled = true;

	public SyncBar() {
		bar.getLayout().direction = SWT.VERTICAL;
		bar.getLayout().left = 0;
		bar.getLayout().right = 0;
		bar.getLayout().top = 0;
		bar.getLayout().bottom = 0;
		bar.setCommands(ADD_DELETE);
		updateControl();
	}
	
	public void dispose() {
		if (bar!=null) bar.dispose();
		bar = null;
	}

	public void setModelObject(XModelObject selected) {
		if(this.selected == selected) return;
		this.selected = selected;
		updateControl();
	}

	public CommandBar getCommandBar() {
		return bar;
	}

	void updateControl() {
		bar.setEnabled(ADD, addEnabled);
		if(selected == null) {
			bar.setEnabled(RESTORE, false);
			bar.rename(RESTORE, DELETE);
			bar.setEnabled(DELETE, false);
		} else {
			boolean u = ("deleted".equals(selected.get("state"))); //$NON-NLS-1$ //$NON-NLS-2$
			if(u) {
				bar.rename(DELETE, RESTORE);
				bar.setEnabled(RESTORE, u);
			} else {
				bar.rename(RESTORE, DELETE);
				bar.setEnabled(DELETE, !u);
			}
		}
	}
	
}
