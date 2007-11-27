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

import java.util.Properties;
import java.util.Set;

import org.jboss.tools.common.model.ui.objecteditor.*;
import org.jboss.tools.struts.validator.ui.formset.model.DependencyModel;
import org.jboss.tools.struts.validator.ui.formset.model.FModel;
import org.eclipse.swt.graphics.Color;

import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateHandler;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.FindObjectHelper;

public abstract class FieldDataEditor extends XChildrenEditor implements ActionNames {
	protected DependencyModel fmodel;
	
	public FieldDataEditor() {
		bar.getLayout().buttonWidth = 70;
	}

	public void dispose() {
		super.dispose();
		if (fmodel!=null) fmodel.dispose();
		fmodel = null;
	}

	public void setFModel(FModel fmodel) {
		if(this.fmodel == fmodel) return;
		this.fmodel = (DependencyModel)fmodel;
		getFieldDataTableHelper().setFModel(fmodel);
		update();
	}

	public FieldDataTableHelper getFieldDataTableHelper() {
		return (FieldDataTableHelper)helper;
	}

	protected Color getItemColor(int i) {
		if(helper == null || fmodel == null) return DEFAULT_COLOR;
		XModelObject o = helper.getModelObject(i);
		boolean isInherited = (o != null && fmodel.isChildInherited(o));
		return (isInherited) ? FEditorConstants.INHERITED : DEFAULT_COLOR;
	}

	protected void updateBar() {
		boolean enabled = (helper != null && fmodel != null && fmodel.isEditable());
		bar.setEnabled(ActionNames.ADD, enabled);
		XModelObject o = (helper == null) ? null : helper.getModelObject(xtable.getSelectionIndex());
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
			bar.setEnabled(ActionNames.DELETE, enabled);
			bar.setEnabled(DEFAULT, enabled);
			bar.setEnabled(ActionNames.EDIT, enabled);
			bar.setEnabled(OVERWRITE, enabled);
		}
	}

	public void action(String name) {
		if(name.equals(ActionNames.ADD)) add();
		else {
			XModelObject o = helper.getModelObject(xtable.getSelectionIndex());
			if(o == null) return;
			if(name.equals(ActionNames.EDIT)) executeEdit(o);
			else if(name.equals(OVERWRITE)) executeOverwrite();
			else if(name.equals(ActionNames.DELETE)) callAction(o, "DeleteActions.Delete");
			else if(name.equals(DEFAULT)) callAction(o, "DeleteActions.ResetDefault");
		}
	}

	protected void add() {
		Set set = getKeys();
		executeAdd();
		update();
		int i = getAddedKey(set);
		if(i >= 0) xtable.setSelection(i);
	}

	protected void executeAdd() {}

	protected void executeEdit(XModelObject o) {
		callAction(o, "Properties.Properties");
	}

	protected void executeOverwrite() {
		if(helper == null || fmodel == null) return;
		XModelObject o = helper.getModelObject(xtable.getSelectionIndex());
		if(o == null) return;
		FModel pf = fmodel.getParent();
		XModelObject[] targets = getTarget(pf);
		if(targets == null) return;
		XModelObject target = targets[0], po = targets[1], co = targets[2];
		XModelObject copy = o.copy();
		String name = copy.getAttributeValue("name");
		if(name != null && name.length() == 0 && fmodel.getName().length() > 0)
		  copy.setAttributeValue("name", fmodel.getName());
		long q = copy.getTimeStamp();
		executeEdit(copy);
		if(q == copy.getTimeStamp()) return;
		if(po != null) {
			target.addChild(copy);
			DefaultCreateHandler.addCreatedObject(po, co, FindObjectHelper.IN_EDITOR_ONLY);
		} else {
			DefaultCreateHandler.addCreatedObject(target, copy, FindObjectHelper.IN_EDITOR_ONLY);
		}
	}

	protected void executeAdd(String entity, String actionpath, Properties p) {
		if(helper == null || fmodel == null) return;
		FModel pf = (fmodel instanceof DependencyModel) ? fmodel.getParent() : fmodel;
		XModelObject[] targets = getTarget(pf);
		if(targets == null) return;
		XModelObject target = targets[0], po = targets[1], co = targets[2];
		long ts = target.getTimeStamp();
		if(p == null) p = new Properties();
		p.put("shell", bar.getControl().getShell());
		if(entity != null) {
			XActionInvoker.invoke(entity, actionpath, target, p);
		} else {
			XActionInvoker.invoke(actionpath, target, p);
		}
		if(ts == target.getTimeStamp()) return;
		if(po != null && co != null)
		  DefaultCreateHandler.addCreatedObject(po, co, FindObjectHelper.IN_EDITOR_ONLY);
	}

	static XModelObject[] getTarget(FModel pf) {
		XModelObject[] os = pf.getModelObjects();
		if(os.length == 0) return null;
		if(!pf.isInherited()) {
			return new XModelObject[]{os[0], null, null};
		} else if(!pf.getParent().isInherited()) {
			XModelObject[] pos = pf.getParent().getModelObjects();
			if(pos.length == 0) return null;
			XModelObject pcopy = os[0].copy(0);
			return new XModelObject[]{pcopy, pos[0], pcopy};
		} else {
			XModelObject[] gos = pf.getParent().getParent().getModelObjects();
			if(gos.length == 0) return null;
			XModelObject[] pos = pf.getParent().getModelObjects();
			if(pos.length == 0) return null;
			XModelObject pcopy = os[0].copy(0);
			XModelObject gcopy = pos[0].copy(0);
			gcopy.addChild(pcopy);
			return new XModelObject[]{pcopy, gos[0], gcopy};
		}
	}


}
