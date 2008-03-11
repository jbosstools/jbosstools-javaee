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
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.util.*;
import org.jboss.tools.common.meta.action.*;

public class ArgEditor extends FieldDataEditor {
	ATH ath;
	
	protected AbstractTableHelper createHelper() {
		return ath = new ATH();
	}
	
	public void set11() {
		ath.set11();
	}

	protected int[] getColumnWidthHints() {
		return new int[]{6, 2, 8, 4};
	}
	
	protected void executeAdd() {
		if(fmodel.getName().length() > 0) {
			Properties p = new Properties();
			p.setProperty("name", fmodel.getName());
			String entity = "SVWAddArgN";
			if(ath.is11) entity += "11";
			executeAdd(entity, "CreateActions.AddArg", p);
		} else {
			executeAdd(null, "CreateActions.AddArg", null);
		}
	}

	protected void executeEdit(XModelObject o) {
		String suff = (ath.is11) ? "11" : "";
		if(fmodel.getName().length() > 0) {
			Properties p = new Properties();
			p.setProperty("name", fmodel.getName());
			XActionInvoker.invoke("SVWEditArgN" + suff, "Edit", o, p);
		} else {
			XActionInvoker.invoke("SVWEditArg" + suff, "Edit", o, null);
		}
	}

}

class ATH extends FieldDataTableHelper {
	static String[] header = new String[]{"name", "arg", "key", "resource"};
	static String[] header11 = new String[]{"name", "position", "key", "resource"};
	
	boolean is11 = false;
	
	void set11() {
		is11 = true;
	}

	public String[] getHeader() {
		return (is11) ? header11 : header;
	}

	public int size() {
		return (fmodel == null) ? 0 : fmodel.getArgs().length;
	}

	public XModelObject getModelObject(int r) {
		if(fmodel == null) return null;
		XModelObject[] cs = fmodel.getArgs();
		return (r < 0 || r >= cs.length) ? null : cs[r];
	}

}
