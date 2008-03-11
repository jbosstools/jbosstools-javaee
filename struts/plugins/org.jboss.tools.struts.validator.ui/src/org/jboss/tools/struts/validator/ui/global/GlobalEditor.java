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
package org.jboss.tools.struts.validator.ui.global;

import java.util.*;
import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.jboss.tools.common.meta.action.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.event.*;
import org.jboss.tools.struts.messages.StrutsUIMessages;
import org.jboss.tools.struts.validators.model.ValidatorConstants;
import org.jboss.tools.struts.validators.model.XModelEntityResolver;

import org.jboss.tools.struts.validator.ui.*;
import org.jboss.tools.common.model.ui.action.*;
import org.jboss.tools.struts.validator.ui.formset.FEditorConstants;
import org.jboss.tools.struts.validator.ui.internal.ValidatorManager;
import org.jboss.tools.struts.validator.ui.internal.ValidatorCommand;

public class GlobalEditor implements SelectionListener, CommandBarListener  {
	static String ADD = StrutsUIMessages.ADD;
	static String DELETE = StrutsUIMessages.DELETE;
	protected Composite control; 
	protected ComboModel combomodel = new ComboModel();
	protected Combo combo;
	protected CommandBar bar = new CommandBar();
	protected XModelObject root = null;
	protected XModelObject selected = null;
	protected boolean lock = false;
	protected GlobalEditorListener listener = null;

	public void dispose() {
		listener = null;
		if (combomodel!=null) combomodel.dispose();
		combomodel = null;
		if (bar!=null) bar.dispose();
		bar = null;
	}
	
	public void setObject(XModelObject root) {
		this.root = root;
	}

	public Control createControl(Composite parent) {
		control = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout(5, false);
		gl.marginWidth = 0;
		gl.marginHeight = 3;
		gl.horizontalSpacing = 5;
		gl.verticalSpacing = 0;
		control.setLayout(gl);

		Label label = new Label(control, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = gl.numColumns;
		label.setLayoutData(gd);

		label = new Label(control, SWT.SEPARATOR | SWT.VERTICAL);
		gd = new GridData();
		gd.horizontalIndent = 2;
		gd.heightHint = 18;
		label.setLayoutData(gd);

		label = new Label(control, SWT.NONE);
		label.setText("Current Global Section"); //$NON-NLS-1$

		combo = new Combo(control, SWT.DROP_DOWN | SWT.READ_ONLY);
		combomodel.setCombo(combo);
		gd = new GridData();
		gd.widthHint = 65;
		combo.setLayoutData(gd);

		label = new Label(control, SWT.SEPARATOR | SWT.VERTICAL);
		gd = new GridData();
		gd.heightHint = 18;
		label.setLayoutData(gd);

		createCommadBar();

		label = new Label(control, SWT.SEPARATOR | SWT.HORIZONTAL);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = gl.numColumns;
		label.setLayoutData(gd);

		update();

		combo.addSelectionListener(this);
		return control;	
	}
	
	protected void createCommadBar() {
		bar.getLayout().asToolBar = true;
		bar.getLayout().iconsOnly = true;
		bar.setCommands(new String[]{ADD, DELETE});
		bar.setImage(ADD, FEditorConstants.IMAGE_CREATE);
		bar.setImage(DELETE, FEditorConstants.IMAGE_DELETE);
		bar.createControl(control);
		bar.addCommandBarListener(this);
	}
	
	public Control getControl() {
		return control;
	}

	public void addGlobalEditorListener(GlobalEditorListener listener) {
		this.listener = listener;
	}

	public void widgetSelected(SelectionEvent e) {
		if(lock) return;
		XModelObject o = (XModelObject)combomodel.getSelectedItem();
		if(o == selected) return;
		if(listener != null) listener.setSelected(selected = o);
	}

	public void widgetDefaultSelected(SelectionEvent e) {}
	
	public void update() {
		lock = true;
		XModelObject[] cs = (root == null) 
		    ? new XModelObject[0]
			: XModelEntityResolver.getResolvedChildren(root, ValidatorConstants.ENT_GLOBAL);
		if(!isEqualData(cs)) {
			XModelObject s = (XModelObject)combomodel.getSelectedItem();
			combomodel.removeAllElements();
			for (int i = 0; i < cs.length; i++) combomodel.addElement(cs[i]);
			if(s != null && combomodel.getIndexOf(s) >= 0) combomodel.setSelectedItem(s);
			if(s != combomodel.getSelectedItem()) s = null;
			if(s == null && cs.length != 0) {
				combomodel.setSelectedItem(s = cs[0]);
			}
			if(selected != s) {
				listener.setSelected(selected = s);
			}
			combo.redraw();
		}
		updateCommandsEnabled();
		lock = false;
	}

	private boolean isEqualData(XModelObject[] cs) {
		if(combomodel.getSize() != cs.length) return false;
		for (int i = 0; i < cs.length; i++)
		  if(combomodel.getElementAt(i) != cs[i]) return false;
		return true;
	}

	public void action(String name) {
		if(root == null || !root.isActive()) return;
		if(ADD.equals(name)) {
			Set set = getKeys();
			invoke("CreateActions.AddGlobal", root); //$NON-NLS-1$
			Object added = getAddedKey(set);
			if(added != null) {
				combomodel.setSelectedItem(added);
				widgetSelected(null);
			}			 
		} else if(DELETE.equals(name)) {
			if(selected != null)
			invoke("DeleteActions.Delete", selected); //$NON-NLS-1$
		}
	}

	private void invoke(String actionname, XModelObject object) {
		XActionInvoker.invoke(actionname, object, null);
	}

	public void structureChanged(XModelTreeEvent event) {
		if(root == null) return;
		if(event.kind() == XModelTreeEvent.CHILD_ADDED) {
			if(root != event.getModelObject()) return;
			XModelObject added = (XModelObject)event.getInfo();
			combomodel.setSelectedItem(added);
		}
	}
	class ComboModel extends DefaultComboModel {
		public String getPresentation(Object object) {
			if(!(object instanceof XModelObject)) return super.getPresentation(object);
			XModelObject o = (XModelObject)object; 
			String s = o.getPresentationString();
			if(list.size() > 1) {
				int i = getIndexOf(object);
				if(i > 0) {
				    s += " (" + (i + 1) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			return s; 
		}		
	}

	protected Set<Object> getKeys() {
		Set<Object> set = new HashSet<Object>();
		for (int i = 0; i < combomodel.getSize(); i++) 
		 	set.add(combomodel.getElementAt(i));			
		return set;
	}

	protected Object getAddedKey(Set set) {
		for (int i = 0; i < combomodel.getSize(); i++) {
			Object o = combomodel.getElementAt(i);
			if(!set.contains(o)) return o;
		}
		return null;
	}

	public void updateCommandsEnabled() {
		boolean enabled = root != null && root.isObjectEditable();
		bar.setEnabled(ADD, enabled);
		bar.setEnabled(DELETE, enabled);
	}

	public ValidatorCommand getCommand(int cmd) {
		return ValidatorManager.getDefault().getCommand(cmd, this); 
	}

	public void setCommand(int cmd) {
		ValidatorManager.getDefault().setCommand(new ValidatorCommand(cmd, this)); 
	}

	public boolean isGlobalSelected() {
		return selected != null;
	}
}