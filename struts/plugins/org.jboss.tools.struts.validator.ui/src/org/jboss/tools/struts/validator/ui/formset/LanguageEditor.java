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

import java.util.*;
import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.struts.validator.ui.*;

public class LanguageEditor implements SelectionListener, ActionNames {
	protected Composite control;
	protected ComboModel combomodel = new ComboModel();
	protected Combo combo;
	protected FormsetsEditor formsetsEditor = null;
	protected boolean lock = false;
	
	public LanguageEditor() {}
	
	public Control createControl(Composite parent) {
		control = new Composite(parent, SWT.NONE);
		control.setLayout(new GridLayout(2, false));
		Label label = new Label(control, SWT.NONE);
		label.setText("Language/Country");
		combo = new Combo(control, SWT.DROP_DOWN | SWT.READ_ONLY);
		combomodel.setCombo(combo);
		combo.addSelectionListener(this);
		return control;	
	}
	
	public Control getControl() {
		return control;
	}
	
	public void update() {
		if(combo == null || combo.isDisposed()) return;
		if(lock) return;
		lock = true;
		XModelObject[] ls = formsetsEditor.formsetsModel.getDistinctFormsets();
		XModelObject lc = (XModelObject)getNewItem(ls, combomodel);
		if(lc == null) lc = (XModelObject)combomodel.getSelectedItem();
		if(isChanged(ls, combomodel)) lc = (XModelObject)setBoxValues(ls, combomodel, lc);
		else combomodel.refresh();
		if(lc != null) formsetsEditor.formsetsModel.setLanguage(lc.getAttributeValue("language"), lc.getAttributeValue("country"));
		boolean _isBoxEnabled = ls.length > 1 || (ls.length == 1 && (ls[0].getAttributeValue("language").length() > 0 || ls[0].getAttributeValue("country").length() > 0));
		if(_isBoxEnabled != control.isVisible() || !control.getParent().isVisible()) {
			control.setVisible(_isBoxEnabled);
			control.setEnabled(_isBoxEnabled);
			control.getParent().getParent().layout(); 
			control.getParent().redraw();
		}
		lock = false;
	}

	private Object getNewItem(Object[] ls, ComboModel combomodel) {
		try {
			int sz = combomodel.getSize();
			
			if (sz >= ls.length)
				return null;
			
			Set<Object> set = new HashSet<Object>();
			
			for (int i = 0; i < sz; i++)
				set.add(combomodel.getElementAt(i).toString());
			
			for (int i = 0; i < ls.length; i++)
				if (!set.contains(ls[i].toString()))
					return ls[i];
		} catch (Exception e) {
			XStudioValidatorPlugin.getPluginLog().logError(e);			
		}
		return null;
	}

	private boolean isChanged(Object[] vs, ComboModel combomodel) {
		if(combomodel.getSize() != vs.length) return true;
		for (int i = 0; i < vs.length; i++)
		  if(vs[i] != combomodel.getElementAt(i)) return true;
		return false;
	}

	private Object setBoxValues(Object[] vs,ComboModel combomodel, Object selected) {
		boolean e = false;
		combomodel.removeAllElements();
		for (int i = 0; i < vs.length; i++) {
			if(vs[i] == selected) e = true;
			combomodel.addElement(vs[i]);
		}
		if(!e) selected = (vs.length > 0) ? vs[0] : null;
		if(selected != null) combomodel.setSelectedItem(selected);
		return selected;
	}

	public void widgetSelected(SelectionEvent e) {
		if(lock) return;
		update();
		lock = false;
	}


	public void widgetDefaultSelected(SelectionEvent e) {}
	
	class ComboModel extends DefaultComboModel {
		public String getPresentation(Object object) {
			if(!(object instanceof XModelObject)) return super.getPresentation(object);
			XModelObject o = (XModelObject)object; 
			String lg = o.getAttributeValue("language");
			String ct = o.getAttributeValue("country");
			return (lg.length() + ct.length() == 0) ? "default" : lg + "_" + ct;
		}		
	}
}
