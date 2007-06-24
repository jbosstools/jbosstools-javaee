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
package org.jboss.tools.seam.ui.internal.project.facet;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.seam.ui.widget.editor.BrowseFolderFieldEditor;
import org.jboss.tools.seam.ui.widget.editor.CheckBoxFieldEditor;
import org.jboss.tools.seam.ui.widget.editor.ComboFieldEditor;
import org.jboss.tools.seam.ui.widget.editor.IFieldEditor;
import org.jboss.tools.seam.ui.widget.editor.IFieldEditorFactory;
import org.jboss.tools.seam.ui.widget.editor.TextFieldEditor;

public class SwtFieldEditorFactory implements IFieldEditorFactory {

	public IFieldEditor createCheckboxEditor(String name, String label,
			boolean defaultValue) {
		// TODO Auto-generated method stub
		return new CheckBoxFieldEditor(name,label,Boolean.valueOf(defaultValue));
	}

	public IFieldEditor createComboEditor(String name, String label,
			List values, Object defaultValue) {
		// TODO Auto-generated method stub
		return new ComboFieldEditor(name,label,values,defaultValue.toString());
	}

	public IFieldEditor createTextExitor(String name, String label, String defaultValue) {
		// TODO Auto-generated method stub
		return  new TextFieldEditor(name,label,defaultValue);
	
	}
	
	public IFieldEditor createBrowseFolderEditor(String name, String label, String defaultValue) {
		return new BrowseFolderFieldEditor(name,label,defaultValue);
	}
}
