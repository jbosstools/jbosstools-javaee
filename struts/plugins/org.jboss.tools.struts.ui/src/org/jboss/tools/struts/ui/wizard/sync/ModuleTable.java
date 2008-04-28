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

import org.jboss.tools.common.model.ui.attribute.XAttributeSupport;
import org.jboss.tools.common.model.ui.attribute.editor.DirectoryFieldEditorEx;
import org.jboss.tools.common.model.ui.attribute.editor.IFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.jboss.tools.common.meta.action.XAttributeData;
import org.jboss.tools.common.meta.action.XEntityData;
import org.jboss.tools.common.meta.action.impl.XEntityDataImpl;
import org.jboss.tools.common.meta.key.WizardKeys;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.jst.web.project.WebModuleConstants;

public class ModuleTable {
	protected XModelObject object = null;
	String[] attrs = {"name", 
                      "URI", 
                      "path on disk", 
	                  "java src",
					  "root"};
	//String[] dipl = {"Name", "URI", "Path on Disk", "Sources Path", "Module Root"};
	MutableModuleListTableModel list;
	protected XEntityData data;
	XAttributeData[] ad;
	Composite composite;
	FieldEditor[] f = new FieldEditor[5];
	protected XAttributeSupport as;
	String defaultLocation = null;
	
	public ModuleTable() {
		data = XEntityDataImpl.create(new String[][]{
			   {WebModuleConstants.ENTITY_WEB_MODULE}, 
			   {attrs[0]}, {attrs[1]}, {attrs[2]}, {attrs[3]}, {attrs[4]} });
		ad = data.getAttributeData();
	}
	
	public void dispose() {
		if (as!=null) as.dispose();
		as = null;
		if (f!=null) {
			for (int i=0;i<f.length;++i) {
				if (f[i]!=null) f[i].dispose();
			}
		}
	}
	
	public void setDefaultLocation(String defaultLocation) {
		this.defaultLocation = defaultLocation;
	}

	public void setModelObject(XModelObject object) {
		if(this.object == object) return;
		commit();
		this.object = object;
		if(composite != null) update();
	}
	
	public void update() {
		boolean isConfig = object != null && WebModuleConstants.ENTITY_WEB_CONFIG.equals(object.getModelEntity().getName());
		boolean isDefault = false;
		for (int i = 0; i < ad.length; i++) {
			XModelObject o = (!isConfig || i < 3) ? object : object.getParent();
			String n = ad[i].getAttribute().getName();
			String v = o == null ? "" : o.getAttributeValue(n);
			if(i == 0 && v.length() == 0 && object != null) {
				v = "<default>";
				isDefault = true;
			}
			if(v != null) ad[i].setValue(v); 
		}
		as.load();

		if(composite != null) {
			boolean deleted = object == null || "deleted".equals(object.get("state"));
//			Color c = (deleted) ? Display.getDefault().getSystemColor(SWT.COLOR_GRAY)
//								: Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
			for (int i = 0; i < f.length; i++) {
				boolean enabled = !deleted && (!isConfig || i < 3);
				f[i].setEnabled(enabled, composite);
			}
			String lt = (isDefault) ? "Web Root     " : "Module Root";
			f[4].setLabelText(lt);			
		}
		updateSrcPath();
	}
	
//	private void setForeground(Control control, Color color) {
//		control.setForeground(color);
//		if(!(control instanceof Composite)) return;
//		Composite c = (Composite)control;
//		Control[] cs = c.getChildren();
//		for (int i = 0; i < cs.length; i++) setForeground(cs[i], color);
//	}

	public void setListener(MutableModuleListTableModel list) {
		this.list = list;
	}

	private static String getString(String key) {
		return WizardKeys.getString(key.replace(' ','_'));
	}

	public Control createControl(Composite parent, XModelObject o) {
		as = new XAttributeSupport();
		as.init(o, data);
		for (int i = 0; i < ad.length; i++) {
			as.getPropertyEditorByName(attrs[i]).setLabelText(getString("ModuleTableClass_"+attrs[i]));
		}
		composite = (Composite)as.createControl(parent);
		for (int i = 0; i < f.length; i++) {
			f[i] = as.getPropertyEditorByName(attrs[i]).getFieldEditor(parent);
		}
		update();
		return composite;
	}
	
	public void commit() {
		if(this.object != null) {
			as.store();
			boolean isConfig = object != null && WebModuleConstants.ENTITY_WEB_CONFIG.equals(object.getModelEntity().getName());
			for (int i = 1; i < ad.length; i++) {
				if(isConfig && i >= 3) continue;
				this.object.setAttributeValue(ad[i].getAttribute().getName(), ad[i].getValue());
			}
			updateSrcPath();
		}
	}
	
	private void updateSrcPath() {
		if(ad[3].getValue().length() == 0 && f[3] instanceof DirectoryFieldEditorEx) {
			((DirectoryFieldEditorEx)f[3]).setLastPath(ad[4].getValue());
		}
		if(defaultLocation != null && ad[2].getValue().length() == 0 && f[2] instanceof DirectoryFieldEditorEx) {
			((DirectoryFieldEditorEx)f[2]).setLastPath(defaultLocation);
		}
	}
	
	public int getFieldInset() {
		if(!(f[0] instanceof IFieldEditor)) return 0;
		IFieldEditor f0 = (IFieldEditor)f[0];
		Control[] cs = f0.getControls(composite);
		if(cs.length < 2 || cs[1].isDisposed()) return 0;
		composite.layout();
		return cs[1].getLocation().x;		
	}
	
	public void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
		as.addPropertyChangeListener(listener);
	}
	
}
