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
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.PropertyPage;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.SeamPreferences;
import org.jboss.tools.seam.core.project.facet.SeamRuntime;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeManager;
import org.jboss.tools.seam.ui.SeamGuiPlugin;
import org.jboss.tools.seam.ui.internal.project.facet.IValidator;
import org.jboss.tools.seam.ui.widget.editor.ButtonFieldEditor;
import org.jboss.tools.seam.ui.widget.editor.CompositeEditor;
import org.jboss.tools.seam.ui.widget.editor.IFieldEditor;
import org.jboss.tools.seam.ui.widget.editor.IFieldEditorFactory;
import org.jboss.tools.seam.ui.widget.editor.ITaggedFieldEditor;
import org.jboss.tools.seam.ui.widget.editor.SeamRuntimeListFieldEditor.SeamRuntimeNewWizard;

/**
 * @author Viacheslav Kabanovich
 */
public class SeamSettingsPreferencePage extends PropertyPage {
	IProject project;

	IFieldEditor seamEnablement;
	IFieldEditor runtime;

	public SeamSettingsPreferencePage() {
	}

	@Override
	public void setElement(IAdaptable element) {
		super.setElement(element);
		project = (IProject) getElement().getAdapter(IProject.class);
	}

	@Override
	protected Control createContents(Composite parent) {
		ISeamProject seamProject = SeamCorePlugin
				.getSeamProject(project, false);
		boolean hasSeamSupport = seamProject != null;
		
		boolean cannotBeModified = false;

		if(seamProject != null) {
			cannotBeModified = seamProject.getParentProjectName() != null;
		}
		if(!cannotBeModified) {
			cannotBeModified = isEarPartInEarSeamProject(project);
		}
		
		seamEnablement = IFieldEditorFactory.INSTANCE.createCheckboxEditor(
				SeamPreferencesMessages.SEAM_SETTINGS_PREFERENCE_PAGE_SEAM_SUPPORT, SeamPreferencesMessages.SEAM_SETTINGS_PREFERENCE_PAGE_SEAM_SUPPORT, false);
		seamEnablement.setValue(hasSeamSupport);

		SeamRuntime rs = SeamRuntimeManager.getInstance().getDefaultRuntime();

		runtime = IFieldEditorFactory.INSTANCE.createComboWithButton(SeamPreferencesMessages.SEAM_SETTINGS_PREFERENCE_PAGE_RUNTIME,
				SeamPreferencesMessages.SEAM_SETTINGS_PREFERENCE_PAGE_RUNTIME, SeamRuntimeManager.getInstance().getRuntimeNames(), 
				rs==null?"":rs.getName(),true,new NewSeamRuntimeAction(),(IValidator)null); //$NON-NLS-1$

		List<IFieldEditor> editorOrder = new ArrayList<IFieldEditor>();
		editorOrder.add(seamEnablement);
		editorOrder.add(runtime);

		if (hasSeamSupport) {
			SeamRuntime current = seamProject.getRuntime();
			if (current != null) {
				runtime.setValue(current.getName());
			} else {
				runtime.setValue("");
			}
		} else if(isEarPartInEarSeamProject(project)) {
			runtime.setValue("");
		}
		
		seamEnablement.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				Object value = evt.getNewValue();
				if (value instanceof Boolean) {
					boolean v = ((Boolean) value).booleanValue();
					updateRuntimeEnablement(v);
					validate();
				}
			}
		});

		runtime.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				validate();
			}
		});

		Composite composite = new Composite(parent, SWT.NONE);
		int columnNumber = 1;
		for (IFieldEditor fieldEditor : editorOrder) {
			if (fieldEditor.getNumberOfControls() > columnNumber)
				columnNumber = fieldEditor.getNumberOfControls();
		}
		GridLayout gl = new GridLayout(columnNumber, false);
		gl.verticalSpacing = 5;
		gl.marginTop = 3;
		gl.marginLeft = 3;
		gl.marginRight = 3;
		composite.setLayout(gl);
		for (IFieldEditor fieldEditor2 : editorOrder) {
			fieldEditor2.doFillIntoGrid(composite);
		}

		runtime.setEditable(false);
		if (!hasSeamSupport) {
			updateRuntimeEnablement(false);
		}
		
		if(cannotBeModified) {
			setEnablement(seamEnablement, false);
			setEnablement(runtime, false);
		} else if(hasDependents(project)) {
			setEnablement(seamEnablement, false);
		}

		return composite;
	}
	
	private boolean isEarPartInEarSeamProject(IProject p) {
		IProject[] ps = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (int i = 0; i < ps.length; i++) {
			IEclipsePreferences ep = new ProjectScope(ps[i]).getNode(SeamCorePlugin.PLUGIN_ID);
			if(ep == null) continue;
			String ear = ep.get("seam.ear.project", null);
			if(ear != null && ear.equals(p.getName())) return true;
		}
		return false;
	}
	
	private boolean hasDependents(IProject p) {
		ISeamProject sp = SeamCorePlugin.getSeamProject(p, false);
		if(sp == null) return false;
		IEclipsePreferences ep = SeamPreferences.getProjectPreferences(sp);
		if(ep == null) return false;
		String ear = ep.get("seam.ear.project", null);
		if(projectExists(ear)) return true;
		String ejb = ep.get("seam.ejb.project", null);
		if(projectExists(ejb)) return true;
		String test = ep.get("seam.test.project", null);
		if(projectExists(test)) return true;
		return false;
	}
	
	private boolean projectExists(String name) {
		if(name == null) return false;
		IProject p = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
		if(p != null && p.exists() && p.isAccessible()) return true;
		return false;
	}

	@Override
	public boolean performOk() {
		if (getSeamSupport()) {
			addSeamSupport();
			changeRuntime();
		} else {
			removeSeamSupport();
		}
		return true;
	}

	private void updateRuntimeEnablement(boolean enabled) {
		setEnablement(runtime, enabled);
	}
	
	void setEnablement(IFieldEditor editor, boolean enabled) {
		Object[] cs = editor.getEditorControls();
		for (int i = 0; i < cs.length; i++) {
			if (cs[i] instanceof Control) {
				((Control) cs[i]).setEnabled(enabled);
			}
		}
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
			EclipseResourceUtil.addNatureToProject(project,
					ISeamProject.NATURE_ID);
		} catch (CoreException e) {
			SeamGuiPlugin.getPluginLog().logError(e);
		}
	}

	private void changeRuntime() {
		String name = getRuntimeName();
		SeamRuntime r = SeamRuntimeManager.getInstance()
				.findRuntimeByName(name);
		if (r == null)
			return;
		ISeamProject seamProject = SeamCorePlugin
				.getSeamProject(project, false);
		seamProject.setRuntimeName(name);
	}

	private boolean getSeamSupport() {
		Object o = seamEnablement.getValue();
		return o instanceof Boolean && ((Boolean) o).booleanValue();
	}

	private String getRuntimeName() {
		return runtime.getValueAsString();
	}

	private void validate() {
		if(getSeamSupport() && (runtime.getValue()== null || "".equals(runtime.getValue()))) { //$NON-NLS-1$
//			setValid(false);
			setMessage(SeamPreferencesMessages.SEAM_SETTINGS_PREFERENCE_PAGE_SEAM_RUNTIME_IS_NOT_SELECTED, IMessageProvider.WARNING);
//			setErrorMessage(SeamPreferencesMessages.SEAM_SETTINGS_PREFERENCE_PAGE_SEAM_RUNTIME_IS_NOT_SELECTED);
		} else {
			setValid(true);
			setErrorMessage(null);
			setMessage(null, IMessageProvider.WARNING);
		}
	}

	public class NewSeamRuntimeAction extends
			ButtonFieldEditor.ButtonPressedAction {
		/**
		 * @param label
		 */
		public NewSeamRuntimeAction() {
			super(SeamPreferencesMessages.SEAM_SETTINGS_PREFERENCE_PAGE_ADD);
		}

		@Override
		public void run() {
			List<SeamRuntime> added = new ArrayList<SeamRuntime>();
			Wizard wiz = new SeamRuntimeNewWizard(
					new ArrayList<SeamRuntime>(Arrays
							.asList(SeamRuntimeManager.getInstance()
									.getRuntimes())), added);
			WizardDialog dialog = new WizardDialog(Display.getCurrent()
					.getActiveShell(), wiz);
			dialog.open();

			if (added.size() > 0) {
				SeamRuntimeManager.getInstance().addRuntime(added.get(0));
				getFieldEditor().setValue(added.get(0).getName());
				((ITaggedFieldEditor) ((CompositeEditor) runtime)
						.getEditors().get(1)).setTags(SeamRuntimeManager.getInstance().getRuntimeNames()
						.toArray(new String[0]));
				runtime.setValue(added.get(0).getName());
			}
		}
	}

}
