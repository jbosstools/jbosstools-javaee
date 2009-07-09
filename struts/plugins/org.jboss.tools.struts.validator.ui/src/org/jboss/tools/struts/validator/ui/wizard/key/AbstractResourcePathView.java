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
package org.jboss.tools.struts.validator.ui.wizard.key;

import java.text.MessageFormat;
import java.util.*;
import org.jboss.tools.common.model.ui.action.CommandBar;
import org.jboss.tools.common.model.ui.action.CommandBarListener;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.jboss.tools.common.model.*;
import org.jboss.tools.struts.validator.ui.Messages;

public abstract class AbstractResourcePathView implements CommandBarListener, SelectionListener {
	protected CommandBar commandBar = new CommandBar();
	protected Combo combo;
	protected String selected = null;
	protected PathListener listener = null;
	protected XModel model;
	protected XModelObject object;
	private boolean lock = false;
	
	public AbstractResourcePathView() {}

	public void addPathListener(PathListener listener) {
		this.listener = listener;
	}
	
	public Control createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		layout.horizontalSpacing = 10;
		layout.marginHeight = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);
		
		Label label = new Label(composite, SWT.NONE);
		label.setText(MessageFormat.format(Messages.AbstractResourcePathView_Label, getDisplayName()));

		combo = new Combo(composite, SWT.BORDER | SWT.READ_ONLY);
		GridData d = new GridData(GridData.FILL_HORIZONTAL);
		combo.setLayoutData(d);
		populateCombo();
		if(combo.getItemCount() > 0) combo.setText(combo.getItem(0));
		
		combo.addSelectionListener(this);
		
		commandBar.setMnemonicEnabled(true);
		commandBar.setCommands(new String[]{Messages.AbstractResourcePathView_Browse});
		commandBar.addCommandBarListener(this);
		commandBar.getLayout().setMargins(0, 0, 0, 0);
		commandBar.getLayout().buttonWidth = convertHorizontalDLUsToPixels(parent, IDialogConstants.BUTTON_WIDTH);
		commandBar.createControl(composite);
		
		return composite;
	}

	protected abstract Vector<String> history();
	protected abstract String[] getRequiredValues();
	
	protected int convertHorizontalDLUsToPixels(Control control, int dlus) {
		GC gc= new GC(control);
		gc.setFont(control.getFont());
		int averageWidth= gc.getFontMetrics().getAverageCharWidth();
		gc.dispose();	
		double horizontalDialogUnitSize = averageWidth * 0.25;	
		return (int)Math.round(dlus * horizontalDialogUnitSize);
	}

	protected abstract String getDisplayName();

	public void setObject(Object data) {
		lock = true;
		Properties p = (Properties)data;
		model = (XModel)p.get("model"); //$NON-NLS-1$
		object = (XModelObject)p.get("object"); //$NON-NLS-1$
		lock = false;
		updateSelection();
	}

	public void updateSelection() {
		if(lock || combo == null) return;
		int i = combo.getSelectionIndex();		
		String s = (i < 0) ? null : combo.getItem(i);
		if(selected == s || (selected != null && selected.equals(s))) return;
		selected = s;
		fireSelectionChanged();
	}

	private void fireSelectionChanged() {
		if(listener != null) listener.objectSelected(getSelectedFile());
	}

	protected XModelObject getSelectedFile() {
		int i = combo.getSelectionIndex();		
		String s = (i < 0) ? null : combo.getItem(i);
		return (s == null || model == null) ? null : model.getByPath(s);
	}

	protected void addValue(String s) {
		lock = true;
		Vector<String> history = history();
		history.remove(s);
		history.insertElementAt(s, 0);
		populateCombo();
		if(history.size() > 0) combo.setText(s);
		lock = false;
		updateSelection();
	}
	
	protected void populateCombo() {
		Vector<String> history = history();
		combo.removeAll();
		String[] vs = getRequiredValues();
		Set<String> used = new HashSet<String>();
		for (int i = 0; i < vs.length; i++) {
			if(used.contains(vs[i])) continue;
			used.add(vs[i]);
			combo.add(vs[i]);
		} 
		for (int i = 0; i < history.size(); i++) {
			String v = history.elementAt(i).toString();
			if(used.contains(v)) continue;
			used.add(v);
			combo.add(v);
		}
	}

	public void widgetSelected(SelectionEvent e) {
		updateSelection();
	}

	public void widgetDefaultSelected(SelectionEvent e) {}

}
