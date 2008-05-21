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

import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.util.*;
import org.jboss.tools.common.meta.action.*;

public class VarEditor extends FieldDataEditor {

	protected AbstractTableHelper createHelper() {
		return new VTH();
	}

	protected int[] getColumnWidthHints() {
		return new int[]{1, 2};
	}
	
	protected void executeAdd() {
		executeAdd(null, "CreateActions.AddVar", null);
	}

	protected void executeEdit(XModelObject o) {
		XActionInvoker.invoke("Edit", o, null);
	}

}

class VTH extends FieldDataTableHelper {
	static String[] header = new String[]{"var-name", "var-value"};

	public String[] getHeader() {
		return header;
	}

	public int size() {
		return (fmodel == null) ? 0 : fmodel.getVars().length;
	}

	public XModelObject getModelObject(int r) {
		if(fmodel == null) return null;
		XModelObject[] cs = fmodel.getVars();
		return (r < 0 || r >= cs.length) ? null : cs[r];
	}

}
