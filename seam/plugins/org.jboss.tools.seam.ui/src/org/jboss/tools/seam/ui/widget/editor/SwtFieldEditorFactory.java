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
package org.jboss.tools.seam.ui.widget.editor;

import java.util.List;

import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.jboss.tools.seam.ui.internal.project.facet.IValidator;

/**
 * 
 * @author eskimo
 *
 */
public class SwtFieldEditorFactory implements IFieldEditorFactory {

	/**
	 * 
	 */
	public IFieldEditor createCheckboxEditor(String name, String label,
			boolean defaultValue) {
		CompositeEditor editor = new CompositeEditor(name,label, defaultValue);
		editor.addFieldEditors(new IFieldEditor[]{new LabelFieldEditor(name,label),
				new CheckBoxFieldEditor(name,label,Boolean.valueOf(defaultValue))});
		return editor;
	}

	/**
	 * 
	 */
	public ITaggedFieldEditor createComboEditor(String name, String label,
			List values, Object defaultValue) {
		TaggedComboFieldEditor editor = new TaggedComboFieldEditor(name,label,values, defaultValue,true);
		return editor;
	}

	/**
	 * 
	 */
	public ITaggedFieldEditor createComboEditor(String name, String label,
			List values, Object defaultValue, boolean flat) {
		TaggedComboFieldEditor editor = new TaggedComboFieldEditor(name,label,values, defaultValue,flat);
		return editor;
	}
	
	/**
	 * 
	 */
	public IFieldEditor createTextEditor(String name, String label, String defaultValue) {
		CompositeEditor editor = new CompositeEditor(name,label, defaultValue);
		editor.addFieldEditors(new IFieldEditor[]{new LabelFieldEditor(name,label),
				new TextFieldEditor(name,label, defaultValue)});
		return editor;
	}
	
	/**
	 * 
	 */
	public IFieldEditor createUneditableTextEditor(String name, String label, String defaultValue) {
		CompositeEditor editor = new CompositeEditor(name,label, defaultValue);
		editor.addFieldEditors(new IFieldEditor[]{new LabelFieldEditor(name,label),
				new TextFieldEditor(name,label, defaultValue,false)});
		return editor;
	}
	
	/**
	 * 
	 */
	public IFieldEditor createBrowseFolderEditor(String name, String label, String defaultValue) {
		CompositeEditor editor = new CompositeEditor(name,label, defaultValue);
		editor.addFieldEditors(new IFieldEditor[]{new LabelFieldEditor(name,label),
				new TextFieldEditor(name,label, defaultValue),
				new ButtonFieldEditor(name,createSelectFolderAction("Browse"),defaultValue)});
		return editor;
	}
	
	
	/**
	 * 
	 */
	public IFieldEditor createBrowseFileEditor(String name, String label, String defaultValue) {
		CompositeEditor editor = new CompositeEditor(name,label, defaultValue);
		editor.addFieldEditors(new IFieldEditor[]{new LabelFieldEditor(name,label),
				new TextFieldEditor(name,label, defaultValue),
				new ButtonFieldEditor(name,createSelectFileAction("Browse"),defaultValue)});
		return editor;
	}
	
	
	public IFieldEditor createButtonFieldEditor(String name, String label, String defaultValue, ButtonFieldEditor.ButtonPressedAction action, IValidator validator ) {
		CompositeEditor editor = new CompositeEditor(name,label, defaultValue);
		editor.addFieldEditors(new IFieldEditor[]{new LabelFieldEditor(name,label),
				new TextFieldEditor(name,label, defaultValue),
				new ButtonFieldEditor(name,action,defaultValue)});
		return editor;
	}
	/**
	 * 
	 * @param buttonName
	 * @return
	 */
	public ButtonFieldEditor.ButtonPressedAction createSelectFolderAction(String buttonName) {
		return new ButtonFieldEditor.ButtonPressedAction(buttonName) {
			@Override
			public void run() {
				DirectoryDialog dialog = new DirectoryDialog(Display.getCurrent().getActiveShell());
				dialog.setFilterPath(getFieldEditor().getValueAsString());
				dialog.setMessage("Select Seam Home Folder");
				dialog.setFilterPath(getFieldEditor().getValueAsString());
				String directory = dialog.open();
				if(directory!=null) {
					getFieldEditor().setValue(directory);
				}
			}
		};
	}
	
	/**
	 * 
	 * @param buttonName
	 * @return
	 */
	public ButtonFieldEditor.ButtonPressedAction createSelectFileAction(String buttonName) {
		return new ButtonFieldEditor.ButtonPressedAction(buttonName) {
			@Override
			public void run() {
				FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell());
				dialog.setFilterPath(getFieldEditor().getValueAsString());
				dialog.setText("Select Seam Home Folder");
				dialog.setFilterPath(getFieldEditor().getValueAsString());
				String directory = dialog.open();
				if(directory!=null) {
					getFieldEditor().setValue(directory);
				}
			}
		};
	}
}
