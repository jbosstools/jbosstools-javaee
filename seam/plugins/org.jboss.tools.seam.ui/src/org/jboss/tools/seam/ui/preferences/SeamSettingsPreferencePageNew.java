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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.dialogs.PropertyPage;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.SeamProjectsSet;
import org.jboss.tools.seam.core.project.facet.SeamProjectPreferences;
import org.jboss.tools.seam.core.project.facet.SeamVersion;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.seam.ui.SeamGuiPlugin;
import org.jboss.tools.seam.ui.SeamUIMessages;
import org.jboss.tools.seam.ui.internal.project.facet.IValidator;
import org.jboss.tools.seam.ui.internal.project.facet.ValidatorFactory;
import org.jboss.tools.seam.ui.widget.editor.IFieldEditor;
import org.jboss.tools.seam.ui.widget.editor.IFieldEditorFactory;
import org.jboss.tools.seam.ui.wizard.IParameter;
import org.jboss.tools.seam.ui.wizard.SeamWizardFactory;
import org.jboss.tools.seam.ui.wizard.SeamWizardUtils;
import org.osgi.service.prefs.BackingStoreException;

/**
 * Seam Settings Preference Page
 * @author Alexey Kazakov
 */
public class SeamSettingsPreferencePageNew extends PropertyPage implements PropertyChangeListener {

	private Map<String,IFieldEditor> editorRegistry = new HashMap<String,IFieldEditor>();
	private IProject project;
	private IProject warProject;
	private IEclipsePreferences preferences;
	private ISeamProject warSeamProject;
	private boolean suportSeam;
	private boolean runtimeIsSelected;
	private List<Group> groups = new ArrayList<Group>();

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
			warSeamProject = SeamCorePlugin.getSeamProject(warProject, false);
		} else {
			preferences = SeamCorePlugin.getSeamPreferences(project);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite root = new Composite(parent, SWT.NONE);
		GridData gd = new GridData();

		gd.horizontalSpan = 1;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = false;

		GridLayout gridLayout = new GridLayout(1, false);
		root.setLayout(gridLayout);
		Composite generalGroup = new Composite(root, SWT.NONE);
		generalGroup.setLayoutData(gd);
		gridLayout = new GridLayout(4, false);

		generalGroup.setLayout(gridLayout);

		IFieldEditor seamSupportCheckBox = IFieldEditorFactory.INSTANCE.createCheckboxEditor(
				SeamPreferencesMessages.SEAM_SETTINGS_PREFERENCE_PAGE_SEAM_SUPPORT, SeamPreferencesMessages.SEAM_SETTINGS_PREFERENCE_PAGE_SEAM_SUPPORT, warSeamProject!=null);
		seamSupportCheckBox.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				Object value = evt.getNewValue();
				if (value instanceof Boolean) {
					boolean v = ((Boolean) value).booleanValue();
					setEnabledSeamSuport(v);
					validate();
				}
			}
		});
		registerEditor(seamSupportCheckBox, generalGroup);

		IFieldEditor seamRuntimeEditor = SeamWizardFactory.createSeamRuntimeSelectionFieldEditor(getSeamVersions(), getSeamRuntimeName());
		seamRuntimeEditor.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				Object value = evt.getNewValue();
				if(value.toString().length()>0) {
					setRuntimeIsSelected(true);
				} else {
					setRuntimeIsSelected(false);
				}
				validate();
			}
		});
		registerEditor(seamRuntimeEditor, generalGroup);

		IFieldEditor projectNameEditor = IFieldEditorFactory.INSTANCE.createUneditableTextEditor(IParameter.SEAM_PROJECT_NAME, SeamPreferencesMessages.SEAM_SETTINGS_PREFERENCES_PAGE_SEAM_PROJECT, getSeamProjectName());
		registerEditor(projectNameEditor, generalGroup);

		IFieldEditor connProfileEditor = SeamWizardFactory.createConnectionProfileSelectionFieldEditor(getConnectionProfile(), new IValidator() {
			public Map<String, String> validate(Object value, Object context) {
				SeamSettingsPreferencePageNew.this.validate();
				return ValidatorFactory.NO_ERRORS;
			}
		});
		registerEditor(connProfileEditor, generalGroup);

		gd = new GridData();
		gd.horizontalSpan = 1;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = false;

		Group deploymentGroup = new Group(root, SWT.NONE);
		groups.add(deploymentGroup);
		deploymentGroup.setLayoutData(gd);
		deploymentGroup.setText(SeamPreferencesMessages.SEAM_SETTINGS_PREFERENCES_PAGE_DEPLOYMENT);
		gridLayout = new GridLayout(4, false);
		deploymentGroup.setLayout(gridLayout);

		IFieldEditor deployTypeEditor = IFieldEditorFactory.INSTANCE.createRadioEditor(
				ISeamFacetDataModelProperties.JBOSS_AS_DEPLOY_AS,
				SeamUIMessages.SEAM_INSTALL_WIZARD_PAGE_DEPLOY_AS, 
				Arrays.asList(new String[] {ISeamFacetDataModelProperties.DEPLOY_AS_WAR.toUpperCase(), ISeamFacetDataModelProperties.DEPLOY_AS_EAR.toUpperCase()}),
				Arrays.asList(new Object[] {ISeamFacetDataModelProperties.DEPLOY_AS_WAR, ISeamFacetDataModelProperties.DEPLOY_AS_EAR}),
				getDeployAsValue());

		registerEditor(deployTypeEditor, deploymentGroup);

		IFieldEditor ejbProjectEditor = SeamWizardFactory.createSeamProjectSelectionFieldEditor(ISeamFacetDataModelProperties.SEAM_EJB_PROJECT, SeamPreferencesMessages.SEAM_SETTINGS_PREFERENCES_PAGE_EJB_PROJECT, getEjbProjectName(), true);
		registerEditor(ejbProjectEditor, deploymentGroup);

		Group viewGroup = new Group(root, SWT.NONE);
		groups.add(viewGroup);
		gd = new GridData();
		gd.horizontalSpan = 1;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = false;

		viewGroup.setLayoutData(gd);
		viewGroup.setText(SeamPreferencesMessages.SEAM_SETTINGS_PREFERENCES_PAGE_VIEW);
		gridLayout = new GridLayout(3, false);
		viewGroup.setLayout(gridLayout);

		IFieldEditor viewFolderEditor = SeamWizardFactory.createViewFolderFieldEditor(getViewFolder());
		registerEditor(viewFolderEditor, viewGroup);

		Group modelGroup = new Group(root, SWT.NONE);
		groups.add(modelGroup);
		gd = new GridData();
		gd.horizontalSpan = 1;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = false;

		modelGroup.setLayoutData(gd);
		modelGroup.setText(SeamPreferencesMessages.SEAM_SETTINGS_PREFERENCES_PAGE_MODEL);
		gridLayout = new GridLayout(3, false);
		modelGroup.setLayout(gridLayout);

		IFieldEditor modelSourceFolderEditor = IFieldEditorFactory.INSTANCE.createBrowseSourceFolderEditor(ISeamFacetDataModelProperties.ENTITY_BEAN_SOURCE_FOLDER, SeamPreferencesMessages.SEAM_SETTINGS_PREFERENCES_PAGE_SOURCE_FOLDER, getModelSourceFolder());
		registerEditor(modelSourceFolderEditor, modelGroup);

		setEnabledSeamSuport(warSeamProject!=null);
		setRuntimeIsSelected(getSeamRuntimeName().length()>0);

		return root;
	}

	private String getModelSourceFolder() {
		String folder = null;
		if(preferences!=null) {
			folder = preferences.get(ISeamFacetDataModelProperties.ENTITY_BEAN_SOURCE_FOLDER, null);
		}
		if(folder==null) {
			SeamProjectsSet set = new SeamProjectsSet(project);
			IFolder f = set.getModelFolder();
			folder = f!=null?f.getFullPath().toString():"";
		}
		return folder;
	}

	private String getViewFolder() {
		String folder = null;
		if(preferences!=null) {
			folder = preferences.get(ISeamFacetDataModelProperties.WEB_CONTENTS_FOLDER, null);
		}
		if(folder==null) {
			SeamProjectsSet set = new SeamProjectsSet(project);
			IFolder f = set.getViewsFolder();
			folder = f!=null?f.getFullPath().toString():"";
		}
		return folder;
	}

	private List<String> getProfileNameList() {
		IConnectionProfile[] profiles = ProfileManager.getInstance()
				.getProfilesByCategory("org.eclipse.datatools.connectivity.db.category"); //$NON-NLS-1$
		List<String> names = new ArrayList<String>();
		for (IConnectionProfile connectionProfile : profiles) {
			names.add(connectionProfile.getName());
		}
		return names;
	}

	private String getConnectionProfile() {
		String defaultDs = SeamProjectPreferences.getStringPreference(
				SeamProjectPreferences.SEAM_DEFAULT_CONNECTION_PROFILE);
		return getProfileNameList().contains(defaultDs)?defaultDs:""; //$NON-NLS-1$
	}

	private String getEjbProjectName() {
		if(preferences!=null) {
			return preferences.get(ISeamFacetDataModelProperties.SEAM_EJB_PROJECT, project.getName());
		}
		return project.getName();
	}

	private Object getDeployAsValue() {
		if(preferences!=null) {
			return preferences.get(ISeamFacetDataModelProperties.JBOSS_AS_DEPLOY_AS, ISeamFacetDataModelProperties.DEPLOY_AS_WAR);
		}
		return ISeamFacetDataModelProperties.DEPLOY_AS_WAR;
	}

	private void validate() {
//		if(getSeamSupport() && (runtime.getValue()== null || "".equals(runtime.getValue()))) { //$NON-NLS-1$
////			setValid(false);
//			setMessage(SeamPreferencesMessages.SEAM_SETTINGS_PREFERENCE_PAGE_SEAM_RUNTIME_IS_NOT_SELECTED, IMessageProvider.WARNING);
////			setErrorMessage(SeamPreferencesMessages.SEAM_SETTINGS_PREFERENCE_PAGE_SEAM_RUNTIME_IS_NOT_SELECTED);
//		} else {
//			setValid(true);
//			String value = runtime.getValueAsString();
//			if(Boolean.TRUE.equals(seamEnablement.getValue()) && SeamRuntimeManager.getInstance().findRuntimeByName(value) == null) {
//				setErrorMessage("Runtime " + value + " does not exist.");
//			} else {
//				setErrorMessage(null);
//				setMessage(null, IMessageProvider.WARNING);
//			}
//		}
	}

	private String getSeamRuntimeName() {
		if(preferences!=null) {
			return preferences.get(ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME, "");
		}
		return "";
	}

	private String getSeamProjectName() {
		return warProject!=null ? warProject.getName() : project.getName();
	}

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
	}

	private void registerEditor(IFieldEditor editor, Composite parent) {
		editorRegistry.put(editor.getName(), editor);
		editor.doFillIntoGrid(parent);
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
		return suportSeam;
	}

	private void setEnabledSeamSuport(boolean enabled) {
		suportSeam = enabled;
		for (String key : editorRegistry.keySet()) {
			if(key!=SeamPreferencesMessages.SEAM_SETTINGS_PREFERENCE_PAGE_SEAM_SUPPORT) {
				editorRegistry.get(key).setEnabled(enabled && runtimeIsSelected);
			}
			if(key==ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME) {
				editorRegistry.get(key).setEnabled(enabled);
			}
		}
		setEnabledGroups(enabled && runtimeIsSelected);
	}

	private void setEnabledGroups(boolean enabled) {
		for (Group group : groups) {
			group.setEnabled(enabled);
		}
	}

	private void setRuntimeIsSelected(boolean selected) {
		runtimeIsSelected = selected;
		for (String key : editorRegistry.keySet()) {
			if(key!=SeamPreferencesMessages.SEAM_SETTINGS_PREFERENCE_PAGE_SEAM_SUPPORT && key!=ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME) {
				editorRegistry.get(key).setEnabled(selected);
			}
		}
		setEnabledGroups(selected);
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

	private SeamVersion[] getSeamVersions() {
//		if(warSeamProject != null) {
//			SeamRuntime r = warSeamProject.getRuntime();
//			if(r != null) {
//				return new SeamVersion[]{r.getVersion()};
//			}
//			String jarLocation = getJBossSeamJarLocation();
//			if(jarLocation != null) {
//				String folder = new File(jarLocation).getParent();
//				String vs = SeamRuntimeListFieldEditor.SeamRuntimeWizardPage.getSeamVersion(folder);
//				SeamVersion v = findMatchingVersion(vs);
//				if(v != null) {
//					return new SeamVersion[]{v};
//				}
//			}
//		}
		return SeamVersion.ALL_VERSIONS;
	}
/*
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
*/
}