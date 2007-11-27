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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.viewers.StructuredSelection;
import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.ui.objecteditor.XChildrenEditor;
import org.jboss.tools.common.model.util.AbstractTableHelper;
import org.jboss.tools.struts.validator.ui.XStudioValidatorPlugin;

public class KeysEditor extends XChildrenEditor implements PathListener {
	private PropertyChangeListener listener;
	boolean isObjectEditable = true;
	String initialSelection = null;
	
	public KeysEditor() {
		setMnemonicEnabled(true);
	}

	public void dispose() {
		super.dispose();
		listener = null;
	}

	public void setListener(PropertyChangeListener listener) {
		this.listener = listener;
	}
	
	public void setInitialSelection(String s) {
		initialSelection = s;
	}
	
	protected boolean areUpDounActionsEnabled() {
		return true;
	}

	protected int[] getColumnWidthHints() {
		return new int[]{15, 25};
	}
  
	protected AbstractTableHelper createHelper() {
		return new KeysTableHelper();
	}

	public void objectSelected(XModelObject object) {
		setObject(object);
		try { 
			update();
			if(initialSelection != null && object != null) {
				XModelObject c = object.getChildByPath(initialSelection);
				if(c != null) {
					getSelectionProvider().setSelection(new StructuredSelection(c));
				}
			}
		} catch (Exception e) {
			XStudioValidatorPlugin.getPluginLog().logError(e);		
		}
	}

	protected String getAddActionPath() {
		return "CreateActions.CreateProperty";
	}

	protected void edit() {
		XModelObject o = helper.getModelObject(xtable.getSelectionIndex());
		if(o != null) callAction(o, "Properties.Edit");
	}
	
	protected void onSelectionChanged() {
		super.onSelectionChanged();
		onSelectionChanged2();
	}
	
	private void onSelectionChanged2() {
		if(listener == null) return;
		int i = xtable.getSelectionIndex();
		String s = (i < 0) ? null : helper.getValueAt(i, 0);
		listener.propertyChange(new PropertyChangeEvent(this, "key", "", s));
	}
	
	public void save() {
		XModelObject o = helper.getModelObject();
		if(o != null) XActionInvoker.invoke("SaveActions.Save", o, null);
	}

}

class KeysTableHelper extends AbstractTableHelper {
	static String[] header = new String[]{"name", "value"};

	public String[] getHeader() {
		return header;
	}

}
