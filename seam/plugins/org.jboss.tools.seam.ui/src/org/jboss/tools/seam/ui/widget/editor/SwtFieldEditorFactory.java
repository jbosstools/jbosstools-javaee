/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.seam.ui.widget.editor;

import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.jboss.tools.seam.ui.SeamUIMessages;
import org.jboss.tools.seam.ui.internal.project.facet.IValidator;
import org.jboss.tools.seam.ui.widget.editor.ButtonFieldEditor.ButtonPressedAction;

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

	/*
	 * Starting from 4022 revision it creates standart combo box, if it be necessary
	 * to use custom combo box, use another implementation of this method.
	 * PS. custom combo box looks ugly under mac os. 
	 */
	public ITaggedFieldEditor createComboEditor(String name, String label,
			List values, Object defaultValue) {
		TaggedComboFieldEditor editor = new TaggedComboFieldEditor(name,label,values, defaultValue,false);
		return editor;
	}

	/**
	 * 
	 */
	public ITaggedFieldEditor createComboEditor(String name, String label,
			List values, Object defaultValue, boolean editable) {
		TaggedComboFieldEditor editor = new TaggedComboFieldEditor(name,label,values, defaultValue,editable);
		return editor;
	}

	public ITaggedFieldEditor createRadioEditor(String name, String label,
			List<String> labels, List values, Object defaultValue) {
		TaggedRadioFieldEditor editor = new TaggedRadioFieldEditor(name,label, labels, values, defaultValue);
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
				new ButtonFieldEditor(name,createSelectFolderAction(SeamUIMessages.SWT_FIELD_EDITOR_FACTORY_BROWS),defaultValue)});
		return editor;
	}

	/**
	 * 
	 */
	public IFieldEditor createBrowseFileEditor(String name, String label, String defaultValue) {
		CompositeEditor editor = new CompositeEditor(name,label, defaultValue);
		editor.addFieldEditors(new IFieldEditor[]{new LabelFieldEditor(name,label),
				new TextFieldEditor(name,label, defaultValue),
				new ButtonFieldEditor(name,createSelectFileAction(SeamUIMessages.SWT_FIELD_EDITOR_FACTORY_BROWS),defaultValue)});
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
	 * @param buttonName
	 * @return
	 */
	public ButtonFieldEditor.ButtonPressedAction createSelectFolderAction(String buttonName) {
		return new ButtonFieldEditor.ButtonPressedAction(buttonName) {
			@Override
			public void run() {
				DirectoryDialog dialog = new DirectoryDialog(Display.getCurrent().getActiveShell());
				dialog.setFilterPath(getFieldEditor().getValueAsString());
				dialog.setMessage(SeamUIMessages.SWT_FIELD_EDITOR_FACTORY_SELECT_SEAM_HOME_FOLDER);
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
				dialog.setText(SeamUIMessages.SWT_FIELD_EDITOR_FACTORY_SELECT_SEAM_HOME_FOLDER);
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
	 */
	public ButtonFieldEditor.ButtonPressedAction createNotImplementedYetAction(String buttonName) {
		return new ButtonFieldEditor.ButtonPressedAction(buttonName) {
			@Override
			public void run() {
				new MessageDialog(Display.getCurrent().getActiveShell(), SeamUIMessages.SWT_FIELD_EDITOR_FACTORY_ERROR, 
					null, SeamUIMessages.SWT_FIELD_EDITOR_FACTORY_NOT_IMPLEMENTED_YET, MessageDialog.ERROR, new String[]{SeamUIMessages.SWT_FIELD_EDITOR_FACTORY_OK},1)
				.open();
			}
		};
	}

	/**
	 * 
	 */
	public IFieldEditor createComboWithTwoButtons(String name, String label,
			List values, Object defaultValue, boolean flat,
			ButtonPressedAction action1, ButtonPressedAction action2,
			IValidator validator) {
		CompositeEditor editor = new CompositeEditor(name,label,defaultValue);
		editor.addFieldEditors(new IFieldEditor[]{
				new LabelFieldEditor(name,label),
				new ComboFieldEditor(name,label, values, defaultValue, false),
				new ButtonFieldEditor(name, action1, defaultValue),
				new ButtonFieldEditor(name, action2, defaultValue)
		});
		return editor;
	}

	/**
	 * 
	 */
	public IFieldEditor createComboWithButton(String name, String label,
			List values, Object defaultValue, boolean flat,
			ButtonPressedAction action1,
			IValidator validator) {
		CompositeEditor editor = new CompositeEditor(name,label,defaultValue);
		editor.addFieldEditors(new IFieldEditor[]{
				new LabelFieldEditor(name,label),
				new ComboFieldEditor(name,label, values, defaultValue, false),
				new ButtonFieldEditor(name, action1, defaultValue)
		});
		return editor;
	}
}