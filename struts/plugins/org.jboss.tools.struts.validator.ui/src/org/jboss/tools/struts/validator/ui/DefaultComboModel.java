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
package org.jboss.tools.struts.validator.ui;

import java.util.*;
import org.eclipse.swt.widgets.*;

public class DefaultComboModel {
	protected Combo combo;
	protected ArrayList<Object> list = new ArrayList<Object>();
	
	public DefaultComboModel() {}
	
	public void dispose() {
		if (list!=null) list.clear();
		list = null;
	}
	
	public void setCombo(Combo combo) {
		this.combo = combo;
	}
	
	public int getIndexOf(Object object) {
		for (int i = 0; i < list.size(); i++) if(list.get(i) == object) return i;
		return -1;  	
	}
	public void removeAllElements() {
		if(combo != null) combo.removeAll();
		list.clear(); 
	}
	public Object getSelectedItem() {
		return (combo == null) ? null : getElementAt(combo.getSelectionIndex());
	}
	public void setSelectedItem(Object o) {
		if(combo == null) return;
		int i = getIndexOf(o);
		if(i < 0) combo.setText(""); else combo.setText(combo.getItem(i));
	}
	public Object getElementAt(int i) {
		return (i < 0 || i >= list.size()) ? null : list.get(i);
	}
	public int getSize() {
		return list.size();
	}
	public void addElement(Object o) {
		list.add(o);
		if(combo != null) combo.add(getPresentation(o));			
	}
	
	public String getPresentation(Object object) {
		return (object == null) ? "" : object.toString();
	}
	
	public void refresh() {
		if(combo == null || isUpToDate()) return;
		Object selected = getSelectedItem();
		combo.removeAll();
		int s = getSize();
		for (int i = 0; i < s; i++) 
		  combo.add(getPresentation(getElementAt(i)));		
		if(selected != null) setSelectedItem(selected);
		combo.pack(true);
	}
	
	private boolean isUpToDate() {
		int s = getSize();
		if(combo.getItemCount() != s) return false;
		for (int i = 0; i < s; i++) {
			String s1 = getPresentation(getElementAt(i));
			String s2 = combo.getItem(i);
			if(s1 == null || !s1.equals(s2)) return false;
		}
		return true;
	}

}
