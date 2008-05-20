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
package org.jboss.tools.jsf.ui.wizard.bean;

import java.beans.PropertyChangeEvent;

import org.jboss.tools.common.model.ui.IValueProvider;
import org.jboss.tools.common.model.ui.attribute.editor.CheckBoxFieldEditor;
import org.jboss.tools.common.model.ui.wizards.special.SpecialWizardStep;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.widgets.*;

import org.jboss.tools.jsf.model.handlers.bean.AddManagedPropertySupport;

public class AddManagedBeanPropertyScreen extends SpecialWizardStep {
	static int INDENT = 18;

	public Control createControl(Composite parent) {
		stepControl = attributes.createControl(parent);
		shiftFields();
		updateFieldEnablement();
		return stepControl;
	}
	
	private void shiftFields() {
		FieldEditor f = attributes.getFieldEditorByName("generate getter");
		if(f == null) return;
		CheckBoxFieldEditor cb = (CheckBoxFieldEditor)f;
		cb.setIndent(INDENT);
		f = attributes.getFieldEditorByName("generate setter");
		cb = (CheckBoxFieldEditor)f;
		cb.setIndent(INDENT);
	}
	
	boolean lock = false;

	public void propertyChange(PropertyChangeEvent event) {
		if(lock) return;
		if(event.getSource() == attributes.getPropertyEditorAdapterByName("property-name")) {
			processNameChange((String)event.getNewValue());
		}
		super.propertyChange(event);
	}

	void processNameChange(String newValue) {
		lock = true;
		try {
			AddManagedPropertySupport ps = (AddManagedPropertySupport)support;
			String type = ps.getFieldType(newValue);
			type = revalidateType(type);
			IValueProvider vp = attributes.getPropertyEditorAdapterByName("property-class");
			if(type != null) vp.setValue(type);
		} finally {
			lock = false;
		}
	}
	
	String revalidateType(String type) {
		if(type == null || type.length() == 0 || type.indexOf(".") >= 0) return type;
		if(type.equals("byte")) return "java.lang.Byte";
		if(type.equals("boolean")) return "java.lang.Boolean";
		if(type.equals("char")) return "java.lang.Character";
		if(type.equals("double")) return "java.lang.Double";
		if(type.equals("float")) return "java.lang.Float";
		if(type.equals("int")) return "java.lang.Integer";
		if(type.equals("long")) return "java.lang.Long";
		if(type.equals("short")) return "java.lang.Short";
		return type;
	}

}
