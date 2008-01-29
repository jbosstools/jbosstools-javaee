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
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.dialogs.PropertyPage;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.SeamPreferences;
import org.jboss.tools.seam.core.project.facet.SeamRuntime;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeManager;
import org.jboss.tools.seam.core.project.facet.SeamVersion;
import org.jboss.tools.seam.ui.SeamGuiPlugin;
import org.jboss.tools.seam.ui.internal.project.facet.IValidator;
import org.jboss.tools.seam.ui.widget.editor.ButtonFieldEditor;
import org.jboss.tools.seam.ui.widget.editor.CompositeEditor;
import org.jboss.tools.seam.ui.widget.editor.IFieldEditor;
import org.jboss.tools.seam.ui.widget.editor.IFieldEditorFactory;
import org.jboss.tools.seam.ui.widget.editor.ITaggedFieldEditor;
import org.jboss.tools.seam.ui.widget.editor.LabelFieldEditor;
import org.jboss.tools.seam.ui.widget.editor.SeamRuntimeListFieldEditor;
import org.jboss.tools.seam.ui.widget.editor.ButtonFieldEditor.ButtonPressedAction;
import org.jboss.tools.seam.ui.widget.editor.SeamRuntimeListFieldEditor.SeamRuntimeNewWizard;

/**
 * @author Viacheslav Kabanovich
 */
public class SeamSettingsPreferencePage extends PropertyPage {
	IProject project;

	IFieldEditor seamEnablement;
	IFieldEditor runtime;
	IFieldEditor installedRuntimes;
	
	ISeamProject seamProject;

	public SeamSettingsPreferencePage() {
	}

	@Override
	public void setElement(IAdaptable element) {
		super.setElement(element);
		project = (IProject) getElement().getAdapter(IProject.class);
	}

	boolean hasSeamSupport() {
		return seamProject != null;
	}

	@Override
	protected Control createContents(Composite parent) {
		seamProject = SeamCorePlugin.getSeamProject(project, false);

		boolean cannotBeModified = false;

		if(seamProject != null) {
			cannotBeModified = seamProject.getParentProjectName() != null;
		}
		if(!cannotBeModified) {
			cannotBeModified = isEarPartInEarSeamProject(project);
		}

		seamEnablement = IFieldEditorFactory.INSTANCE.createCheckboxEditor(
				SeamPreferencesMessages.SEAM_SETTINGS_PREFERENCE_PAGE_SEAM_SUPPORT, SeamPreferencesMessages.SEAM_SETTINGS_PREFERENCE_PAGE_SEAM_SUPPORT, false);
		seamEnablement.setValue(hasSeamSupport());

		SeamRuntime rs = SeamRuntimeManager.getInstance().getRuntimeForProject(project);

		List<String> namesAsList = getNameList();

		runtime = IFieldEditorFactory.INSTANCE.createComboWithButton(SeamPreferencesMessages.SEAM_SETTINGS_PREFERENCE_PAGE_RUNTIME,
				SeamPreferencesMessages.SEAM_SETTINGS_PREFERENCE_PAGE_RUNTIME, namesAsList, 
				rs==null?"":rs.getName(),true,new NewSeamRuntimeAction(),(IValidator)null); //$NON-NLS-1$

		ButtonPressedAction action = new ButtonPressedAction(SeamPreferencesMessages.SEAM_SETTINGS_PREFERENCE_PAGE_INSTALLED_RUNTIMES) {
			public void run() {
				PreferenceDialog prefsdlg = PreferencesUtil.createPreferenceDialogOn(
					PlatformUI.getWorkbench().getDisplay().getActiveShell(),
					SeamPreferencePage.SEAM_PREFERENCES_ID, 
					new String[] {SeamPreferencePage.SEAM_PREFERENCES_ID}, null
				);

				prefsdlg.open();

				String v = runtime.getValueAsString();
				List<String> list = getNameList();
				((ITaggedFieldEditor) ((CompositeEditor) runtime)
						.getEditors().get(1)).setTags(list.toArray(new String[0]));
				if(v != null && list.contains(v)) {
					runtime.setValue(v);
				} else {
					setCurrentValue();
				}
			}
		};
		
		installedRuntimes = new ButtonFieldEditor(SeamPreferencesMessages.SEAM_SETTINGS_PREFERENCE_PAGE_INSTALLED_RUNTIMES, action, null);

		List<IFieldEditor> editorOrder = new ArrayList<IFieldEditor>();
		editorOrder.add(seamEnablement);
		editorOrder.add(runtime);
//		editorOrder.add(installedRuntimes);

		setCurrentValue();
		
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
		
		LabelFieldEditor filler = new LabelFieldEditor("filler", "");
		Object[] fs = filler.getEditorControls(composite);
		if(fs != null && fs.length > 0 && fs[0] instanceof Label) {
			Label l = (Label)fs[0];
			GridData d = new GridData();
			d.horizontalSpan = columnNumber - 1;
			l.setLayoutData(d);
		}
		
		Object[] cs = installedRuntimes.getEditorControls(composite);
		if(cs != null && cs.length > 0 && cs[0] instanceof Button) {
			Button b = (Button)cs[0];
			GridData d = new GridData();
			d.horizontalAlignment = SWT.END;
			b.setLayoutData(d);
		}

		runtime.setEditable(false);
		if (!hasSeamSupport()) {
			updateRuntimeEnablement(false);
		}
		
		if(cannotBeModified) {
			setEnablement(seamEnablement, false);
			setEnablement(runtime, false);
		} else if(hasDependents(project)) {
			setEnablement(seamEnablement, false);
		}
		validate();

		return composite;
	}
	
	private List<String> getNameList() {
		Set<String> names = new TreeSet<String>();
		names.addAll(getAvailableRuntimeNames());
		if(hasSeamSupport()) {
			String currentName = seamProject.getRuntimeName();
			if(currentName != null) names.add(currentName);
		}
		List<String> namesAsList = new ArrayList<String>();
		namesAsList.addAll(names);
		return namesAsList;
	}
	
	private void setCurrentValue() {
		if (hasSeamSupport()) {
			String currentName = seamProject.getRuntimeName();
			if (currentName != null) {
				runtime.setValue(currentName);
			} else {
				runtime.setValue("");
			}
		} else if(isEarPartInEarSeamProject(project)) {
			runtime.setValue("");
		}
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
			String value = runtime.getValueAsString();
			if(Boolean.TRUE.equals(seamEnablement.getValue()) && SeamRuntimeManager.getInstance().findRuntimeByName(value) == null) {
				setErrorMessage("Runtime " + value + " does not exist.");
			} else {
				setErrorMessage(null);
				setMessage(null, IMessageProvider.WARNING);
			}
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
						.getEditors().get(1)).setTags(getAvailableRuntimeNames()
						.toArray(new String[0]));
				runtime.setValue(added.get(0).getName());
			}
		}
	}

	private List<String> getAvailableRuntimeNames() {
		if(hasNature("org.eclipse.jdt.core.javanature")
				&& !hasNature("org.eclipse.wst.common.project.facet.core.nature")) {
			return SeamRuntimeManager.getInstance().getAllRuntimeNames();
		}
		if(seamProject != null) {
			SeamRuntime r = seamProject.getRuntime();
			if(r != null) {
				SeamRuntime[] rs = SeamRuntimeManager.getInstance().getRuntimes(r.getVersion());
				return toNames(rs);
			}
			String jarLocation = getJBossSeamJarLocation();
			if(jarLocation != null) {
				String folder = new File(jarLocation).getParent();
				String vs = SeamRuntimeListFieldEditor.SeamRuntimeWizardPage.getSeamVersion(folder);
				SeamVersion v = findMatchingVersion(vs);
				if(v != null) {
					SeamRuntime[] rs = SeamRuntimeManager.getInstance().getRuntimes(v);
					return toNames(rs);
				}
			}
		}
		return SeamRuntimeManager.getInstance().getRuntimeNames();
	}
	
	private List<String> toNames(SeamRuntime[] rs) {
		List<String> list = new ArrayList<String>();
		if(rs != null) for (int i = 0; i < rs.length; i++) list.add(rs[i].getName());
		return list;
	}
	
	private boolean hasNature(String natureId) {
		try {
			return project != null && project.isAccessible() && project.hasNature(natureId);
		} catch (CoreException e) {
			return false;
		}
	}
	
	private String getJBossSeamJarLocation() {
		IJavaProject jp = EclipseResourceUtil.getJavaProject(project);
		if(jp == null) return null;
		IClasspathEntry[] es = null;
		try {
			es = jp.getResolvedClasspath(true);
		} catch (JavaModelException e) {
			//ignore
			return null;
		}
		if(es == null) return null;
		for (int i = 0; i < es.length; i++) {
			IPath p = es[i].getPath();
			if(p != null && p.lastSegment().equalsIgnoreCase("jboss-seam.jar")) {
				IFile f = ResourcesPlugin.getWorkspace().getRoot().getFile(p);
				if(f != null && f.exists()) return f.getLocation().toString();
			}
		}
		return null;
	}
	
	private SeamVersion findMatchingVersion(String vs) {
		if(vs == null) return null;
		if(vs.matches(SeamVersion.SEAM_1_2.toString().replace(".", "\\.") + ".*")) {
			return SeamVersion.SEAM_1_2;
		}
		if(vs.matches(SeamVersion.SEAM_2_0.toString().replace(".", "\\.") + ".*")) {
			return SeamVersion.SEAM_2_0;
		}
		return null;
	}

}
