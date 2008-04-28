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
package org.jboss.tools.struts.validator.ui.wizard.depends;

import java.util.*;
import org.eclipse.swt.*;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.jboss.tools.struts.validator.ui.wizard.key.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.struts.messages.StrutsUIMessages;
import org.jboss.tools.struts.validators.model.ValidatorConstants;
import org.jboss.tools.struts.validators.model.XModelEntityResolver;

import org.jboss.tools.common.model.ui.action.*;
import org.eclipse.jface.dialogs.InputDialog;
import org.jboss.tools.common.model.ui.objecteditor.*;

public class DependencyLRView implements PathListener, CommandBarListener, SelectionListener  {
	static String ADD_NEW = StrutsUIMessages.ADD_NEW;
	static String ADD = StrutsUIMessages.ADD_ARROW_RIGHT;
	static String REMOVE = StrutsUIMessages.REMOVE_ARROW_LEFT;
	static String UP = StrutsUIMessages.UP;
	static String DOWN = StrutsUIMessages.DOWN;
	static String[][] commands = new String[][]{{ADD, REMOVE}, {UP, DOWN}, {ADD_NEW}};
	protected ListModel sourceModel = new ListModel();
	protected ListModel targetModel = new ListModel();
	protected XTable sourceList = new XTable();
	protected XTable targetList = new XTable();
	protected CommandBar barNew = new CommandBar();
	protected CommandBar barAdd = new CommandBar();
	protected CommandBar barUp = new CommandBar();
	protected CommandBar[] bars = new CommandBar[]{barAdd, barUp, barNew};
	protected Properties p;
	protected Control control;
	
	public DependencyLRView() {
		for (int i = 0; i < bars.length; i++) {
			bars[i].getLayout().buttonWidth = 80;
			bars[i].getLayout().direction = SWT.VERTICAL;
			bars[i].setCommands(commands[i]);
			bars[i].addCommandBarListener(this);
		}
		sourceModel.setTable(sourceList);
		targetModel.setTable(targetList);
		sourceList.setTableProvider(sourceModel);
		targetList.setTableProvider(targetModel);
		sourceList.setMultiSelected();		
		targetList.setMultiSelected();
		sourceModel.setName("Available Rules"); //$NON-NLS-1$
		targetModel.setName("Selected Rules"); //$NON-NLS-1$
	}

	public void dispose() {
		if (sourceList!=null) sourceList.dispose();
		sourceList = null;
		if (targetList!=null) targetList.dispose();
		targetList = null;
		if (barNew!=null) barNew.dispose();
		barNew = null;
		if (barAdd!=null) barAdd.dispose();
		barAdd = null;
		if (barUp!=null) barUp.dispose();
		barUp = null;
		if (p!=null) p.clear();
		p = null;
	}
	
	public void setObject(Object data) {
		p = (Properties)data;
	}

	public void objectSelected(XModelObject object) {
		sourceModel.removeAllElements();
		if(object == null) return;
		Set<Object> s = new HashSet<Object>();
		XModelObject[] gs = XModelEntityResolver.getResolvedChildren(object, ValidatorConstants.ENT_GLOBAL);
		for (int i = 0; i < gs.length; i++) {
			XModelObject[] vs = XModelEntityResolver.getResolvedChildren(gs[i], ValidatorConstants.ENT_VALIDATOR);
			for (int j = 0; j < vs.length; j++) {
				String n = vs[j].getPathPart();
				if(s.contains(n)) continue;
				s.add(n);
				sourceModel.getList().add(n);
			}
		}
		sourceModel.fireStructureChanged();
		updateSelectionDependentActions();
	}

	private void loadTarget() {
		targetModel.removeAllElements();
		if(p == null) return;
		String value = p.getProperty("value"); //$NON-NLS-1$
		StringTokenizer st = new StringTokenizer(value, ","); //$NON-NLS-1$
		Set<String> s = new HashSet<String>();
		while(st.hasMoreTokens()) {
			String n = st.nextToken().trim();
			if(s.contains(n)) continue;
			s.add(n);
			targetModel.getList().add(n);
		}
		targetModel.fireStructureChanged();
		updateSelectionDependentActions();
	}

	public void saveTarget() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < targetModel.getList().size(); i++) {
			if(i > 0) sb.append(',');
			sb.append(targetModel.getList().get(i));
		}
		if(p != null) p.setProperty("value", sb.toString()); //$NON-NLS-1$
	}

	public void action(String name) {
		if(ADD_NEW.equals(name)) addNew();
		else if(ADD.equals(name)) addSelected();
		else if(REMOVE.equals(name)) removeSelected();
		else if(UP.equals(name)) moveUp();
		else if(DOWN.equals(name)) moveDown();
	}

	private void addNew() {
		InputDialog d = new InputDialog(control.getShell(), StrutsUIMessages.ADD_VALIDATOR, "name", "", null); //$NON-NLS-2$ //$NON-NLS-3$
		d.create();
		if(d.open() != InputDialog.OK) return;
		String s = d.getValue();
		if(s != null && targetModel.getList().indexOf(s) < 0) {
			targetModel.getList().addElement(s);
			targetModel.fireStructureChanged();
			int si = targetModel.getRowCount() - 1;
			if(si >= 0) targetList.setSelection(si);
			updateSelectionDependentActions();
		}
	}

	private void addSelected() {
		int[] is = sourceList.getTable().getSelectionIndices();
		int si = -1;
		for (int i = 0; i < is.length; i++) {
			Object s = sourceModel.getList().get(is[i]);
			if(targetModel.getList().indexOf(s) < 0) {
				targetModel.getList().add(s);
				si = targetModel.getList().size() - 1;
			}
		}
		targetModel.fireStructureChanged();
		if(si >= 0)	targetList.getTable().setSelection(si);
		updateSelectionDependentActions();
	}

	private void removeSelected() {
		int[] is = targetList.getTable().getSelectionIndices();
		Object[] os = new Object[is.length];
		for (int i = 0; i < is.length; i++) os[i] = targetModel.getList().get(is[i]);
		int s = -1;
		for (int i = 0; i < is.length; i++) {
			targetModel.getList().remove(os[i]);
			s = is[i];
			if(s >= targetModel.getColumnCount()) s = targetModel.getColumnCount() - 1;
		}
		targetModel.fireStructureChanged();
		if(s >= 0) targetList.setSelection(s);
		updateSelectionDependentActions();
	}

	private void moveUp() {
		int[] is = targetList.getTable().getSelectionIndices();
		if(is.length != 1) return;
		move(is[0], is[0] - 1);
	}

	private void moveDown() {
		int[] is = targetList.getTable().getSelectionIndices();
		if(is.length != 1) return;
		move(is[0], is[0] + 1);
	}

	private void move(int from, int to) {
		if(to < 0 || to >= targetModel.getList().size()) return;
		Object o = targetModel.getList().elementAt(from);
		targetModel.getList().removeElementAt(from);
		targetModel.getList().insertElementAt(o, to);
		targetModel.fireStructureChanged();
		targetList.getTable().setSelection(to);
		updateSelectionDependentActions();
	}
	
	public Control createControl(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		control = c;
		GridLayout g = new GridLayout(3, false);
		c.setLayout(g);
		Control slc = sourceList.createControl(c);
		slc.setLayoutData(new GridData(GridData.FILL_BOTH));
		Control bc = createBar(c);
		bc.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		Control tlc = targetList.createControl(c);
		tlc.setLayoutData(new GridData(GridData.FILL_BOTH));
		loadTarget();
		sourceList.getTable().addSelectionListener(this);
		targetList.getTable().addSelectionListener(this);
		updateSelectionDependentActions();
		return c;
	}
	
	private Control createBar(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		GridLayout g = new GridLayout(1, false);
		c.setLayout(g);
		barAdd.createControl(c);
		barNew.createControl(c);
		barUp.createControl(c);
		return c;		
	}
	
	private void updateSelectionDependentActions() {
		if(sourceList == null || !sourceList.isActive()) return;
		int targetSelection = targetList.getTable().getSelectionIndex();
		barAdd.setEnabled(ADD, canAddSelection());
		barAdd.setEnabled(REMOVE, targetSelection >= 0);
		barUp.setEnabled(UP, targetSelection > 0);
		barUp.setEnabled(DOWN, targetSelection >= 0 && targetSelection < targetList.getTable().getItemCount() - 1);
	}
	
	private boolean canAddSelection() {
		int[] is = sourceList.getTable().getSelectionIndices();
		if(is == null) return false;
		for (int i = 0; i < is.length; i++) {
			Object s = sourceModel.getList().get(is[i]);
			if(targetModel.getList().indexOf(s) < 0) {
				return true;
			}
		}
		return false;
	}

	public void widgetSelected(SelectionEvent e) {
		updateSelectionDependentActions();		
	}

	public void widgetDefaultSelected(SelectionEvent e) {
		widgetSelected(e);		
	}

}
