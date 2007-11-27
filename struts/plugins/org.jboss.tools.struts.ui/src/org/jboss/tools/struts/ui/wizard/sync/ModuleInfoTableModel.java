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

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.ui.objecteditor.*;

public class ModuleInfoTableModel implements XTableProvider {
	protected XModelObject object = null;
	String[] attrs = {"name", "URI", "path on disk", "java src", "root"};
	MutableModuleListTableModel list;

	public void dispose() {
		if (list!=null) list.dispose();
		list = null;
	}

	public void setModelObject(XModelObject object) {
		if(this.object == object) return;
		this.object = object;
	}

	public void setListener(MutableModuleListTableModel list) {
		this.list = list;
	}

	public int getRowCount() {
		return 5;
	}

	public int getColumnCount() {
		return 2;
	}

	public String getColumnName(int c) {
		return (c == 0) ? "name" : "value";
	}

	public boolean isCellEditable(int r, int c) {
		return (object != null && c == 1 && r >= 2);
	}

	public String getValueAt(int r, int c) {
		if(object == null) return "";
		if(c == 0) {
			if(r < 4) return attrs[r];
			String n = object.getAttributeValue("name");
			return (n.length() == 0) ? "web root" : "module root";
		} else {
			String v = object.getAttributeValue(attrs[r]);
			if(r == 0 && v.length() == 0) return "<default>";
			return v;
		}
	}

	public void setValueAt(Object aValue, int r, int c) {
		if(object == null || !isCellEditable(r, c)) return;
		object.setAttributeValue(attrs[r], "" + aValue);
		///list.update();
	}

	public XModelObject getModelObject() {
		return object;
	}

	public boolean isNotUniqueValue(int r, int c) {
		return (list != null && list.isNotUniqueValue(attrs[r], getValueAt(r, c)));
	}

	public Color getColor(int r) {
		XModelObject o = getModelObject();
		boolean valid = (o == null) || (o.getObject("error") == null && ModuleInfoValidator.isModuleDataValid(o));
		Color color = (valid) ? Display.getDefault().getSystemColor(SWT.COLOR_BLACK) :
					  Display.getDefault().getSystemColor(SWT.COLOR_RED);
		return color;
	}
	
	int[] hints = new int[]{5, 15};

	public int getWidthHint(int c) {
		return hints[c];
	}

	public Object getDataAt(int r) {
		if(r < 2 || getModelObject() == null || "deleted".equals(object.get("state"))) return null;
		return new XAttributeInfo(getModelObject(), attrs[r]);
	}
	
}
