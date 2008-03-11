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
import org.eclipse.swt.graphics.Color;
import org.jboss.tools.common.model.ui.objecteditor.*;

public class ListModel implements XTableProvider {
	protected Vector<Object> list = new Vector<Object>();
	protected XTable table;
	String name = "";
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void dispose() {
		if (list!=null) list.clear();
		list = null;
		table = null;
	}

	public void setTable(XTable table) {
		this.table = table;
	}
	
	public Vector<Object> getList() {
		return list;
	}
	
	public void removeAllElements() {
		list.clear();
	}
	
	public int getColumnCount() {
		return 1;
	}

	public int getRowCount() {
		return list.size();
	}

	public String getColumnName(int c) {
		return name;
	}

	public String getValueAt(int r, int c) {
		return list.get(r).toString();
	}

	public Color getColor(int r) {
		return null;
	}
	
	public void fireStructureChanged() {
		if(table != null) table.update();
	}

	public int getWidthHint(int c) {
		return 10;
	}

	public Object getDataAt(int r) {
		return null;
	}
}
