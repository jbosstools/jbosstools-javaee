package org.jboss.tools.seam.ui.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.jboss.tools.common.ui.widget.editor.ButtonFieldEditor;
import org.jboss.tools.common.ui.widget.editor.CompositeEditor;
import org.jboss.tools.common.ui.widget.editor.IFieldEditor;
import org.jboss.tools.seam.ui.SeamUIMessages;

public class ShowProjectSettingsAction extends ButtonFieldEditor.ButtonPressedAction implements PropertyChangeListener {
	IFieldEditor editor;
	IFieldEditor button;

	public ShowProjectSettingsAction() {
		super(SeamUIMessages.SHOW_PROJECT_SETTINGS_ACTION);
	}
	
	public void setEditor(IFieldEditor editor) {
		this.editor = editor;
		editor.addPropertyChangeListener(this);
		CompositeEditor c = (CompositeEditor)editor;
		List<IFieldEditor> es = c.getEditors();
		button = es.get(es.size() - 1);
	}

	@Override
	public void run() {
		if(editor == null) return;
		IProject p = getSelectedProject();
		if(p == null) return;
		PreferenceDialog prefsdlg = PreferencesUtil.createPropertyDialogOn(
			PlatformUI.getWorkbench().getDisplay().getActiveShell(),
			p,
			"org.jboss.tools.seam.ui.propertyPages.SeamSettingsPreferencePage", 
			new String[]{"org.jboss.tools.seam.ui.propertyPages.SeamSettingsPreferencePage"},
			null);

        prefsdlg.open();
        Object value = editor.getValue();
        
        //firing to provoke validation
        editor.setValueAsString("");
        editor.setValue(value);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		IProject p = getSelectedProject();
		boolean enabled = p != null;
		if(isEnabled() != enabled) {
			setEnabled(enabled);
			button.setEnabled(enabled);
		}		
	}
	
	IProject getSelectedProject() {
		String s = editor.getValueAsString();
		if(s == null || s.length() == 0) return null;
		IProject p = ResourcesPlugin.getWorkspace().getRoot().getProject(s);
		if(p == null || !p.isAccessible()) return null;
		return p;
	}

}
