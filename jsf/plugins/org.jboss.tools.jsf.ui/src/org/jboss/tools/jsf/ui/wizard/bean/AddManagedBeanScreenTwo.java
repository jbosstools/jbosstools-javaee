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
package org.jboss.tools.jsf.ui.wizard.bean;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.jface.viewers.*;
import org.jboss.tools.common.model.ui.attribute.adapter.DefaultValueAdapter;
import org.jboss.tools.common.model.ui.attribute.editor.*;
import org.jboss.tools.common.model.ui.objecteditor.*;
import org.jboss.tools.common.model.ui.wizards.special.AbstractSpecialWizardStep;
import org.jboss.tools.common.meta.action.impl.SpecialWizardSupport;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.jsf.model.handlers.bean.AddManagedBeanPropertiesContext;

public class AddManagedBeanScreenTwo extends AbstractSpecialWizardStep {
	Composite composite = null;
	XTable table;
	AddManagedBeanPropertiesContext context = null;
	TableProviderImpl provider = new TableProviderImpl();
	DefaultValueAdapter valueAdapter = new DefaultValueAdapter();
	XModelObject auxproperty = null;
	PropertyEditor editor;
	
	public AddManagedBeanScreenTwo() {
		createTable();
		valueAdapter.setAutoStore(true);
	}
	
	private void createTable() {
		if(table != null) return;
		table = new XTable();
		table.setTableProvider(provider);
	}

	public void dispose() {
		super.dispose();
		if (valueAdapter!=null) valueAdapter.dispose();
		valueAdapter = null;
		if (editor!=null) editor.dispose();
		editor = null;
		if (provider!=null) provider.dispose();
		provider = null;
		if (table!=null) table.dispose();
		table = null;
	}
	
	public void setSupport(SpecialWizardSupport support, int i) {
		super.setSupport(support, i);
		auxproperty = support.getTarget().getModel().createModelObject("JSFManagedProperty", null); //$NON-NLS-1$
		valueAdapter.setAttribute(auxproperty.getModelEntity().getAttribute("value")); //$NON-NLS-1$
		valueAdapter.setModelObject(auxproperty);
		editor = PropertyEditorFactory.createPropertyEditor(valueAdapter, auxproperty.getModelEntity().getAttribute("value"), auxproperty); //$NON-NLS-1$
		editor.setInput(valueAdapter);
	}

	public Control createControl(Composite parent) {
		composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		context = (AddManagedBeanPropertiesContext)support.getProperties().get("propertiesContext"); //$NON-NLS-1$
		provider.setContext(context);
		if(table == null) createTable();
		Table t = (Table)table.createControl(composite);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 250;
		t.setLayoutData(gd);
		table.update();
		t.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				clicked(e);
			}
		});
		t.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if(e.keyCode == 32) {
					Table t = table.getTable();
					int[] is = t.getSelectionIndices();
					boolean changed = false;
					if(is != null) for (int i = 0; i < is.length; i++) {
						changed = true;
						context.setEnabled(is[i], !context.isEnabled(is[i]));
					}
					if(changed) table.update();
				}
			}
		});
		table.getViewer().setColumnProperties(new String[]{"name", "value"}); //$NON-NLS-1$ //$NON-NLS-2$
		table.getViewer().setCellModifier(new ValueCellModifier());
		table.getViewer().setCellEditors(new CellEditor[]{null, editor.getCellEditor(t)});
		return composite;
	}
	
	void clicked(MouseEvent e) {
		int s = table.getSelectionIndex();
		if(s < 0) return;
		Table t = table.getTable();
		TableItem[] is = t.getItems();
		for (int i = 0; i < is.length; i++) {
			if(is[i].getBounds(0).contains(e.x, e.y)) {
				context.setEnabled(i, !context.isEnabled(i));
				table.update();
				return;
			}
		}
	}
	
	class ValueCellModifier implements ICellModifier {
		public boolean canModify(Object element, String property) {
			return "value".equals(property); //$NON-NLS-1$
		}

		public Object getValue(Object element, String property) {
			Integer i = (Integer)element;
			String v = (i == null) ? null : context.getValue(i.intValue());
			valueAdapter.setValue(v);
			return v;
		}

		public void modify(Object element, String property, Object value) {
			TableItem item = (TableItem)element;
			Integer i = (Integer)item.getData();
			if(i == null) return;
			String v = (value == null) ? "" : value.toString(); //$NON-NLS-1$
			context.setValue(i.intValue(), v);
			if(v.length() > 0) context.setEnabled(i.intValue(), true);
			table.update();
		}
		
	}	
	
}

class TableProviderImpl implements XTableProvider, XTableImageProvider {
	Image IMAGE_ENABLED = EclipseResourceUtil.getImage("images/common/check.gif"); //$NON-NLS-1$
	Image IMAGE_DISABLED = EclipseResourceUtil.getImage("images/common/uncheck.gif"); //$NON-NLS-1$
	String[] header = new String[] {"name", "value"};
	AddManagedBeanPropertiesContext context;
	
	public TableProviderImpl() {}
	
	public void setContext(AddManagedBeanPropertiesContext context) {
		this.context = context;
	}

	public int getColumnCount() {
		return 2;
	}

	public int getRowCount() {
		return context == null ? 0 : context.size();
	}

	public String getColumnName(int c) {
		return header[c];
	}

	public String getValueAt(int r, int c) {
		return c == 0 ? context.getName(r) : context.getValue(r);
	}

	public Object getDataAt(int r) {
		return Integer.valueOf(r);
	}

	public Color getColor(int r) {
		return null;
	}

	public int getWidthHint(int c) {
		return c == 0 ? 30 : 70;
	}

	public Image getImage(int r) {
		return (context.isEnabled(r)) ? IMAGE_ENABLED : IMAGE_DISABLED;
	}

	public void dispose() {
	}
}
