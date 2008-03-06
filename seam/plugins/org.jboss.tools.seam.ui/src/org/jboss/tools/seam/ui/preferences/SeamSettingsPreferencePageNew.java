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
package org.jboss.tools.seam.ui.preferences;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PropertyPage;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.ui.SeamGuiPlugin;
import org.jboss.tools.seam.ui.SeamUIMessages;
import org.jboss.tools.seam.ui.widget.editor.IFieldEditor;
import org.jboss.tools.seam.ui.widget.editor.IFieldEditorFactory;
import org.jboss.tools.seam.ui.wizard.IParameter;
import org.jboss.tools.seam.ui.wizard.SeamWizardUtils;
import org.osgi.service.prefs.BackingStoreException;

/**
 * @author Alexey Kazakov
 */
public class SeamSettingsPreferencePageNew extends PropertyPage implements PropertyChangeListener {

	private Map<String,IFieldEditor> editorRegistry = new HashMap<String,IFieldEditor>();
	private List<IFieldEditor> editorOrder = new ArrayList<IFieldEditor>();
	private IProject project;
	private IProject warProject;
	private IEclipsePreferences preferences;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.PropertyPage#setElement(org.eclipse.core.runtime.IAdaptable)
	 */
	@Override
	public void setElement(IAdaptable element) {
		super.setElement(element);
		project = (IProject) getElement().getAdapter(IProject.class);
		warProject = SeamWizardUtils.getRootSeamProject(project);
		if(warProject!=null) {
			preferences = SeamCorePlugin.getSeamPreferences(warProject);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		IFieldEditor projectNameEditor = IFieldEditorFactory.INSTANCE.createUneditableTextEditor(IParameter.SEAM_PROJECT_NAME, SeamUIMessages.SEAM_SETTINGS_PREFERENCES_PAGE_SEAM_PROJECT, getSeamProjectName());
//		addEditor(projectNameEditor);

//		IFieldEditor jBossSeamHomeEditor = IFieldEditorFactory.INSTANCE
//		.createComboWithButton(ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME,
//				SeamUIMessages.SEAM_INSTALL_WIZARD_PAGE_SEAM_RUNTIME, getRuntimeNames(), 
//				getSeamRuntimeDefaultValue(), 
//				true, new NewSeamRuntimeAction(), (IValidator)null);

		Control control = new GridLayoutComposite(parent);
//		projectNameEditor.setEnabled(false);

		return control;
	}

	private String getSeamProjectName() {
		return warProject!=null ? warProject.getName() : project.getName();
	}

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
	}

	/**
	 * 
	 * @param id
	 * @param editor
	 */
	public void addEditor(IFieldEditor editor) {
		editorRegistry.put(editor.getName(), editor);
		editorOrder.add(editor);
		editor.addPropertyChangeListener(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	@Override
	public boolean performOk() {
		if (isSeamSupported()) {
			addSeamSupport();
			storeSettigs();
		} else {
			removeSeamSupport();
		}
		return true;
	}

	private void storeSettigs() {
		//TODO
//		pref.put("test", "blah-blah-blah");
		try {
			preferences.flush();
		} catch (BackingStoreException e) {
			SeamGuiPlugin.getPluginLog().logError(e);
		}
	}

	private boolean isSeamSupported() {
		//TODO
		return true;
	}

	private void removeSeamSupport() {
		try {
			EclipseResourceUtil.removeNatureFromProject(project,
					ISeamProject.NATURE_ID);
		} catch (CoreException e) {
			SeamGuiPlugin.getPluginLog().logError(e);
		}
	}

	private void addSeamSupport() {
		try {
			EclipseResourceUtil.addNatureToProject(project,	ISeamProject.NATURE_ID);
		} catch (CoreException e) {
			SeamGuiPlugin.getPluginLog().logError(e);
		}
	}

	public class GridLayoutComposite extends Composite {

		public GridLayoutComposite(Composite parent, int style) {
			super(parent, style);
			int columnNumber = 1;
			for (IFieldEditor fieldEditor : editorOrder) {
				if(fieldEditor.getNumberOfControls()>columnNumber)
					columnNumber=fieldEditor.getNumberOfControls();
			}
			GridLayout gl = new GridLayout(columnNumber, false);
			gl.verticalSpacing = 5;
			gl.marginTop = 3;
			gl.marginLeft = 3;
			gl.marginRight = 3;
			setLayout(gl);
			for (IFieldEditor fieldEditor : editorOrder) {
				fieldEditor.doFillIntoGrid(this);
			}
		}

		public GridLayoutComposite(Composite parent) {
			this(parent, SWT.NONE);
		}
	}
}