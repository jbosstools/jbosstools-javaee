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

public class MsgEditor extends FieldDataEditor {

	protected AbstractTableHelper createHelper() {
		return new MTH();
	}

	protected int[] getColumnWidthHints() {
		return new int[]{6, 9, 4};
	}
	
	protected void executeAdd() {
		if(fmodel.getName().length() > 0) {
			Properties p = new Properties();
			p.setProperty("name", fmodel.getName());
			executeAdd("SVWAddMsg", "CreateActions.AddMsg", p);
		} else {
			executeAdd(null, "CreateActions.AddMsg", null);
		}
	}

	protected void executeEdit(XModelObject o) {
		if(fmodel.getName().length() > 0) {
			Properties p = new Properties();
			p.setProperty("name", fmodel.getName());
			XActionInvoker.invoke("SVWAddMsg", "Edit", o, p);
		} else {
			XActionInvoker.invoke("Edit", o, null);
		}
	}

}

class MTH extends FieldDataTableHelper {
	static String[] header = new String[]{"name", "key", "resource"};

	public String[] getHeader() {
		return header;
	}

	public int size() {
		return (fmodel == null) ? 0 : fmodel.getMsgs().length;
	}

	public XModelObject getModelObject(int r) {
		if(fmodel == null) return null;
		XModelObject[] cs = fmodel.getMsgs();
		return (r < 0 || r >= cs.length) ? null : cs[r];
	}

}
