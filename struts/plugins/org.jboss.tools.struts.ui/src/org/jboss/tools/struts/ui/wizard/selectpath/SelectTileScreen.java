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
package org.jboss.tools.struts.ui.wizard.selectpath;

import java.util.*;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.jboss.tools.common.model.ui.wizards.special.AbstractSpecialWizardStep;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.jface.viewers.*;

import org.jboss.tools.common.model.*;
import org.jboss.tools.struts.ui.StrutsUIPlugin;

public class SelectTileScreen extends AbstractSpecialWizardStep {
	protected Composite control;
	protected TableViewer tv;
	protected Table table;
	protected SelectionListener selectionListener = null;
	protected XModelObject process;
	protected Text selected;
	String[] listmodel;
	ArrayList<TableItem> items = new ArrayList<TableItem>();
	
	public void dispose() {
		tv = null;
		selectionListener = null;
		if (items!=null) items.clear();
		items = null;
	}

	public Control createControl(Composite parent) {
		if(control != null && !control.isDisposed()) control.dispose();
		control = new Composite(parent, SWT.NONE);
		control.setLayout(new GridLayout());
		createTable();
		createText(control);
		control.pack();
		parent.layout();
		control.layout();
		update0();		
		return control;	
	}
	
	public void createTable() {
		table = new Table(control, SWT.FULL_SELECTION | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 200;
		table.setLayoutData(gd);
		table.addControlListener(new CA());		
		tv = new TableViewer(table, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		int style = SWT.BORDER;
		style |= SWT.LEFT; 
		TableColumn c = new TableColumn(table, style);
		c.setResizable(true);
		table.setHeaderVisible(false);
		table.setLinesVisible(false);
		table.addSelectionListener(new SL());		
	}
	
	protected void createText(Composite parent) {
		selected = new Text(parent, SWT.BORDER);
		selected.setEditable(false);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 18;
		selected.setLayoutData(gd);
		selected.setBackground(new Color(null, 255, 255, 255));
	}
	
	void update0() {
		process = (XModelObject)support.getTarget();
//		lock = true;
		Set tiles = (Set)support.getProperties().get("tiles");
		String[] ts = (tiles == null) ? new String[]{"empty.set"} : (String[])tiles.toArray(new String[0]);
		setElements(ts);
		String name = (String)support.getProperties().get("selectedPath");
		if(name != null && tiles.contains(name)) this.setSelection(name); 
		selected.setEditable(false);
//		lock = false;
	}
	
	void setElements(String[] ts) {
		listmodel = ts;
		int r = table.getSelectionIndex();
		TableItem item = null;
		for (int i = 0; i < ts.length; i++) {
			if(items.size() > i) {
				item = (TableItem)items.get(i);
			} else {
				items.add(item = new TableItem(table, SWT.BORDER));
			}
			String[] vs = new String[]{ts[i]};
			item.setText(vs);
		}
		
		for (int i = items.size() - 1; i >= ts.length ; i--) {
			item = (TableItem)items.remove(i);
			item.dispose();
		}
		tv.refresh();
		table.redraw();
		if(r >= 0) try {
			table.setSelection(r);
		} catch (Exception e) {
			StrutsUIPlugin.getPluginLog().logError(e);
		}
	}

	public void save() {
		String sp = selected.getText();
		support.getProperties().setProperty("selectedPath", sp);
		if(sp.length() > 0) {
			support.getProperties().put("selectedTile", sp);
		} else {
			support.getProperties().remove("selectedTile");
		}
	}

	class SL extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			int i = table.getSelectionIndex();
			if(i < 0) {
				selected.setText("");
			} else {
				selected.setText(listmodel[i]);
			}
			save();
			validate();			
		}		
	}
	
	void setSelection(String value) {
		for (int i = 0; i < listmodel.length; i++) {
			if(!listmodel[i].equals(value)) continue;
			table.setSelection(i);
			return;
		}
	}
	
	class CA extends ControlAdapter {
		public void controlResized(ControlEvent e) {
			int w = table.getClientArea().width;
			if(table.getColumnCount() > 0)
			  table.getColumn(0).setWidth(w - 1);
		}
	}
	
	public Point getMaximumSize() {
		return null;
	}

	public Point getMinimumSize() {
		return null;
	}

}
