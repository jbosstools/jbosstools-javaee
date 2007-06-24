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
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.jboss.tools.common.model.ui.swt.util.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.meta.action.*;
import org.jboss.tools.common.meta.action.impl.handlers.*;
import org.jboss.tools.common.model.util.AbstractTableHelper;
import org.jboss.tools.common.model.util.FindObjectHelper;

import org.jboss.tools.struts.validator.ui.formset.model.*;
import org.jboss.tools.common.model.ui.objecteditor.*;

public class FConstantsEditor extends XChildrenEditor implements ActionNames {
	protected FormsetsModel formsetsModel;
	protected FConstantsModel fmodel;
	
	public FConstantsEditor() {
		bar.getLayout().buttonWidth = 80;
	}
	
	public void dispose() {
		super.dispose();
		if (formsetsModel!=null) formsetsModel.dispose();
		formsetsModel = null;
		if (fmodel!=null) fmodel.dispose();
		fmodel = null;
	}

	public Control createControl(Composite parent) {
		control = new Group(parent, SWT.NONE);
		BorderLayout bl = new BorderLayout();
		control.setLayout(bl);
		bl.northHeight = 3;
		xtable.createControl(control);		
		bl.centerComposite = xtable.getControl();
		createCommandBar();
		enableSelectionListener();
		update();
		return control;	
	}

	protected AbstractTableHelper createHelper() {
		return new TH();
	}

	public void setFModel(FModel fmodel) {
		if(this.fmodel == fmodel) return;
		this.formsetsModel = (fmodel == null) ? null : (FormsetsModel)fmodel.getParent();
		this.fmodel = (FConstantsModel)fmodel;
		((TH)helper).setFModel(fmodel);
		update();
	}

	protected Color getItemColor(int i) {
		if(helper == null || fmodel == null) return DEFAULT_COLOR;
		XModelObject o = helper.getModelObject(i);
		boolean isInherited = (o != null && fmodel.isChildInherited(o));
		return (isInherited) ? FEditorConstants.INHERITED : DEFAULT_COLOR;
	}

	protected void updateBar() {
		boolean enabled = fmodel != null && fmodel.isEditable(); 
		XModelObject o = (helper == null) ? null : helper.getModelObject(xtable.getSelectionIndex());
		bar.setEnabled(ActionNames.ADD, enabled);
		if(o == null || fmodel == null) {
			bar.rename(DEFAULT, ActionNames.DELETE);
			bar.setEnabled(ActionNames.DELETE, false);
			bar.rename(OVERWRITE, ActionNames.EDIT);
			bar.setEnabled(ActionNames.EDIT, false);
			return;
		} else {
			boolean inh = fmodel.isChildInherited(o);
			boolean ovr = fmodel.isChildOverriding(o);
			if(inh) bar.rename(ActionNames.EDIT, OVERWRITE); else bar.rename(OVERWRITE, ActionNames.EDIT);
			if(ovr) bar.rename(ActionNames.DELETE, DEFAULT); else bar.rename(DEFAULT, ActionNames.DELETE);
			bar.setEnabled(ActionNames.DELETE, !inh && enabled);
			bar.setEnabled(DEFAULT, !inh && enabled);
			bar.setEnabled(ActionNames.EDIT, enabled);
			bar.setEnabled(OVERWRITE, enabled);
		}
	}

	public void action(String name) {
		if(name.equals(ActionNames.ADD)) {
			actionAdd();
		} else {
			XModelObject o = helper.getModelObject(xtable.getSelectionIndex());
			if(o == null) return;
			if(name.equals(ActionNames.DELETE)) callAction(o, "DeleteActions.Delete");
			else if(name.equals(ActionNames.EDIT)) callAction(o, "Properties.Properties");
			else if(name.equals(OVERWRITE)) executeOverwrite();
			else if(name.equals(DEFAULT)) callAction(o, "DeleteActions.ResetDefault");
		}
	}

	private void actionAdd() {
		XModelObject[] fs = formsetsModel.getCurrentFormsets();
		if(fs.length == 0) return;
		Set set = getKeys();
		callAction(fs[0], "CreateActions.AddConstant");
		update();
		int i = getAddedKey(set);
		if(i >= 0) xtable.setSelection(i);
	}

	protected void executeOverwrite() {
		if(helper == null || fmodel == null) return;
		XModelObject o = helper.getModelObject(xtable.getSelectionIndex());
		if(o == null) return;
		XModelObject[] os = fmodel.getParent().getModelObjects();
		if(os.length == 0) return;
		XModelObject copy = o.copy(0);
		long q = copy.getTimeStamp();
		XActionInvoker.invoke("SVWAddConstant", "Edit", copy, null);
		if(q == copy.getTimeStamp()) return;
		DefaultCreateHandler.addCreatedObject(os[0], copy, FindObjectHelper.IN_EDITOR_ONLY);
	}

}

class TH extends AbstractTableHelper {
	static String[] header = new String[]{"constant-name", "constant-value"};
	protected FModel fmodel = null;

	public void setFModel(FModel fmodel) {
		this.fmodel = fmodel;
	}

	public String[] getHeader() {
		return header;
	}

	public int size() {
		return (fmodel == null) ? 0 : fmodel.getModelObjects().length;
	}

	public XModelObject getModelObject(int r) {
		if(fmodel == null) return null;
		XModelObject[] cs = fmodel.getModelObjects();
		return (r < 0 || r >= cs.length) ? null : cs[r];
	}

}
