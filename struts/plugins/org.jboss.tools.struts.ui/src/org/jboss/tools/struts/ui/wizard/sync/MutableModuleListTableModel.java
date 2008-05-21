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

import java.util.ArrayList;
import java.util.List;

import org.jboss.tools.common.model.ui.objecteditor.XTableProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.jst.web.project.WebModuleConstants;

public class MutableModuleListTableModel implements XTableProvider {
	protected String[] attrs = {"Name", "URI", "Deleted"};
	List objects = new ArrayList();

	public void dispose() {
		objects = null;
	}
	
	public void setModelObject(List objects) {
		this.objects = objects;
	}

	public XModelObject getModelObject(int r) {
		return (objects == null) ? null : (XModelObject)objects.get(r);
	}

	public int getColumnCount() {
		return attrs.length;
	}

	public int getRowCount() {
		return (objects == null) ? 0 : objects.size();
	}

	public String getColumnName(int c) {
		return attrs[c];
	}

	public String getValueAt(int r, int c) {
		XModelObject o = getModelObject(r);
		if(o == null) return "";
		if(c == 0 && o.getModelEntity().getName().equals(WebModuleConstants.ENTITY_WEB_CONFIG)) {
			return "";
		}
		String v = (c == 0) ? o.getAttributeValue("name") :
				   (c == 1) ? o.getAttributeValue("URI") :
				   ("deleted".equals(o.get("state"))) ? "true" : "";
		if(c == 0 && v.length() == 0) v = "<default>";
		return v;
	}

	public Object getDataAt(int r) {
		return null;
	}

	public Color getColor(int r) {
		XModelObject o = getModelObject(r);
		boolean valid = o.getObject("error") == null && ModuleInfoValidator.isModuleDataValid(o);
		boolean deleted = o != null && "deleted".equals(o.get("state"));
		Color color = (deleted) ? Display.getDefault().getSystemColor(SWT.COLOR_GRAY) : 
					  (valid) ? Display.getDefault().getSystemColor(SWT.COLOR_BLACK) :
		              Display.getDefault().getSystemColor(SWT.COLOR_RED);
		return color;
	}
	
	int[] hints = new int[]{10, 20, 5};

	public int getWidthHint(int c) {
		return hints[c];
	}

	public boolean isNotUniqueValue(String attr, String value) {
		int q = 0;
		for (int i = 0; i < getRowCount(); i++) {
			XModelObject o = getModelObject(i);
			if(value.equals(o.getAttributeValue(attr))) ++q;
		}
		return q >= 2;
	}
	
}
