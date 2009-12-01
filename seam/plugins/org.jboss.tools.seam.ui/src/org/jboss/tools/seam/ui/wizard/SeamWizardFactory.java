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

package org.jboss.tools.seam.ui.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.datatools.connectivity.ConnectionProfileException;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.IProfileListener;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.datatools.connectivity.db.generic.ui.NewConnectionProfileWizard;
import org.eclipse.datatools.connectivity.internal.ui.wizards.NewCPWizard;
import org.eclipse.datatools.connectivity.internal.ui.wizards.NewCPWizardCategoryFilter;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.internal.dialogs.PropertyDialog;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.jboss.tools.common.ui.IValidator;
import org.jboss.tools.common.ui.widget.editor.ButtonFieldEditor;
import org.jboss.tools.common.ui.widget.editor.ComboFieldEditor;
import org.jboss.tools.common.ui.widget.editor.CompositeEditor;
import org.jboss.tools.common.ui.widget.editor.IFieldEditor;
import org.jboss.tools.common.ui.widget.editor.IFieldEditorFactory;
import org.jboss.tools.common.ui.widget.editor.ITaggedFieldEditor;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.project.facet.SeamRuntime;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeManager;
import org.jboss.tools.seam.core.project.facet.SeamVersion;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.seam.ui.SeamUIMessages;
import org.jboss.tools.seam.ui.internal.project.facet.SeamValidatorFactory;
import org.jboss.tools.seam.ui.widget.editor.SeamRuntimeListFieldEditor.SeamRuntimeNewWizard;

/**
 * @author eskimo
 *
 */
public class SeamWizardFactory {
	
	/**
	 * @param defaultSelection
	 * @param allowAllProjects If "false" show only projects with seam nature.
	 * @return
	 */
	public static IFieldEditor createSeamProjectSelectionFieldEditor(String name, String label, String defaultSelection, 
			boolean allowAllProjects, boolean editSettings) {
		if(!editSettings) {
			return IFieldEditorFactory.INSTANCE.createButtonFieldEditor(
					name, label, defaultSelection, 
					 new SelectSeamProjectAction(allowAllProjects), SeamValidatorFactory.NO_ERRORS_VALIDATOR);
		}
		SelectSeamProjectAction buttonAction = new SelectSeamProjectAction(allowAllProjects);
		ShowProjectSettingsAction settingsAction = new ShowProjectSettingsAction();
		ButtonFieldEditor.ButtonPressedAction[] actions = new ButtonFieldEditor.ButtonPressedAction[]{
			buttonAction, settingsAction
		};
//		IFieldEditor editor = IFieldEditorFactory.INSTANCE.createButtonFieldEditor(
//				name, label, defaultSelection, 
//				actions, ValidatorFactory.NO_ERRORS_VALIDATOR);

		IFieldEditor editor = IFieldEditorFactory.INSTANCE.createButtonAndLinkFieldEditor(
				name, label, defaultSelection, 
				buttonAction, settingsAction, SeamValidatorFactory.NO_ERRORS_VALIDATOR);

		settingsAction.setEnabled(false);
		settingsAction.setEditor(editor);
		return editor;
	}

	/**
	 * @param defaultSelection
	 * @return
	 */
	public static IFieldEditor createSeamProjectSelectionFieldEditor(
			String defaultSelection) {
		return createSeamProjectSelectionFieldEditor(ISeamParameter.SEAM_PROJECT_NAME, SeamUIMessages.SEAM_WIZARD_FACTORY_SEAM_PROJECT, defaultSelection, false, true);
	}

	/**
	 * @return
	 */
	public static IFieldEditor createSeamLocalInterfaceNameFieldEditor() {
		return IFieldEditorFactory.INSTANCE.createTextEditor(
				ISeamParameter.SEAM_LOCAL_INTERFACE_NAME, SeamUIMessages.SEAM_WIZARD_FACTORY_LOCAL_INTERFACE_NAME, ""); //$NON-NLS-1$
	}

	/**
	 * @return
	 */
	public static IFieldEditor createSeamBeanNameFieldEditor() {
		return IFieldEditorFactory.INSTANCE.createTextEditor(
				ISeamParameter.SEAM_BEAN_NAME, SeamUIMessages.SEAM_WIZARD_FACTORY_BEAN_NAME, ""); //$NON-NLS-1$
	}

	/**
	 * @return
	 */
	public static IFieldEditor createSeamMethodNameFieldEditor() {
		return IFieldEditorFactory.INSTANCE.createTextEditor(
				ISeamParameter.SEAM_METHOD_NAME, SeamUIMessages.SEAM_WIZARD_FACTORY_METHOD_NAME, ""); //$NON-NLS-1$
	}

	/**
	 * @return
	 */
	public static IFieldEditor createSeamPageNameFieldEditor() {
		return IFieldEditorFactory.INSTANCE.createTextEditor(
				ISeamParameter.SEAM_PAGE_NAME, SeamUIMessages.SEAM_WIZARD_FACTORY_PAGE_NAME, ""); //$NON-NLS-1$
	}
	
	/**
	 * @return
	 */
	public static IFieldEditor createSeamMasterPageNameFieldEditor() {
		return IFieldEditorFactory.INSTANCE.createTextEditor(
				ISeamParameter.SEAM_MASTER_PAGE_NAME, SeamUIMessages.SEAM_WIZARD_FACTORY_MASTER_PAGE_NAME, ""); //$NON-NLS-1$
	}

	/**
	 * @param defaultSelection
	 * @return
	 */
	public static final IFieldEditor[] createBaseFormFieldEditors(String defaultSelection) {
		return new IFieldEditor[]{
			SeamWizardFactory.createSeamProjectSelectionFieldEditor(defaultSelection),
			SeamWizardFactory.createSeamComponentNameFieldEditor(),
			SeamWizardFactory.createSeamLocalInterfaceNameFieldEditor(),
			SeamWizardFactory.createSeamBeanNameFieldEditor(),
			SeamWizardFactory.createSeamMethodNameFieldEditor(),
			SeamWizardFactory.createSeamPageNameFieldEditor()	
		};
	}

	/**
	 * @return
	 */
	public static IFieldEditor createSeamJavaPackageSelectionFieldEditor(String defaultSelection) {
		return IFieldEditorFactory.INSTANCE.createButtonFieldEditor(
				ISeamParameter.SEAM_PACKAGE_NAME, SeamUIMessages.SEAM_WIZARD_FACTORY_PACKAGE_NAME, defaultSelection, 
				 new SelectJavaPackageAction(), SeamValidatorFactory.NO_ERRORS_VALIDATOR);
	}

	/**
	 * @param seamVersions Array of seam runtime versions. If length == 0 then use all versions
	 * @param defaultSelection
	 * @param action
	 * @return Editor to select seam runtime
	 */
	public static IFieldEditor createSeamRuntimeSelectionFieldEditor(SeamVersion[] seamVersions, String defaultValue, NewSeamRuntimeAction action) {
		return createSeamRuntimeSelectionFieldEditor(seamVersions, defaultValue, action, false);
	}

	/**
	 * @param seamVersions Array of seam runtime versions. If length == 0 then use all versions
	 * @param defaultSelection
	 * @param action
	 * @param canBeEmpty
	 * @return Editor to select seam runtime
	 */
	public static IFieldEditor createSeamRuntimeSelectionFieldEditor(SeamVersion[] seamVersions, String defaultValue, NewSeamRuntimeAction action, boolean canBeEmpty) {
		if(seamVersions.length==0) {
			seamVersions = SeamVersion.ALL_VERSIONS;
		}
		List<String> names = getRuntimeNames(seamVersions);
		if(defaultValue!=null && defaultValue.trim().length()>0 && !names.contains(defaultValue)) {
			names.add(0, defaultValue);
		}
		if(canBeEmpty) {
			names.add(0, "");
		}
		IFieldEditor jBossSeamRuntimeEditor = IFieldEditorFactory.INSTANCE
		.createComboWithButton(ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME,
				SeamUIMessages.SEAM_INSTALL_WIZARD_PAGE_SEAM_RUNTIME, names, 
				defaultValue, 
				true, action, (IValidator)null);
		return jBossSeamRuntimeEditor;
	}

	/**
	 * Creates Selection Field of Connection Profiles
	 * @param defaultValue
	 * @param canBeEmpty
	 * @return
	 */
	public static IFieldEditor createConnectionProfileSelectionFieldEditor(Object defaultValue, IValidator validator, final boolean canBeEmpty) {
		EditConnectionProfileAction editAction = new EditConnectionProfileAction(validator);
		NewConnectionProfileAction newAction = new NewConnectionProfileAction(validator);
		List<String> profiles = getConnectionProfileNameList();
		if(canBeEmpty) {
			profiles.add(0, "");
		}
		IFieldEditor connProfileSelEditor = IFieldEditorFactory.INSTANCE.createComboWithTwoButtons(
				ISeamFacetDataModelProperties.SEAM_CONNECTION_PROFILE,
				SeamUIMessages.SEAM_INSTALL_WIZARD_PAGE_CONNECTION_PROFILE,
				profiles,
				defaultValue,
				false, editAction,
				newAction,
				SeamValidatorFactory.NO_ERRORS_VALIDATOR);
		editAction.setEditor(connProfileSelEditor);
		newAction.setEditor(connProfileSelEditor);
		final ButtonFieldEditor editButton = (ButtonFieldEditor)((CompositeEditor)connProfileSelEditor).getEditors().get(2);
		editButton.setEnabled(!"".equals(defaultValue));
		if(canBeEmpty) {
			connProfileSelEditor.addPropertyChangeListener(new PropertyChangeListener(){
				public void propertyChange(PropertyChangeEvent evt) {
					boolean ediatble = !"".equals(evt.getNewValue());
					editButton.setEnabled(ediatble);
				}
			});
		}
		final ComboFieldEditor comboEditor = ((ComboFieldEditor)((CompositeEditor)connProfileSelEditor).getEditors().get(1));
		final IProfileListener profileListener = new IProfileListener() {
			private void update() {
				final List<String> profiles = getConnectionProfileNameList();
				if(canBeEmpty) {
					profiles.add(0, "");
				}
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						comboEditor.setTags((profiles.toArray(new String[0])));
					}
				});
			}

			public void profileAdded(IConnectionProfile profile) {
				update();
			}

			public void profileChanged(IConnectionProfile profile) {
				update();
			}

			public void profileDeleted(IConnectionProfile profile) {
				update();
			}
		};
		ProfileManager.getInstance().addProfileListener(profileListener);
		comboEditor.addDisposeListener(new DisposeListener(){
			public void widgetDisposed(DisposeEvent e) {
				ProfileManager.getInstance().removeProfileListener(profileListener);
			}
		});
		return connProfileSelEditor;
	}

	/**
	 * Creates Selection Field of Connection Profiles
	 * @param defaultValue
	 * @return
	 */
	public static IFieldEditor createConnectionProfileSelectionFieldEditor(Object defaultValue, IValidator validator) {
		return createConnectionProfileSelectionFieldEditor(defaultValue, validator, false);
	}

	private static class EditConnectionProfileAction extends ButtonFieldEditor.ButtonPressedAction {

		private IValidator validator;
		private IFieldEditor connProfileSelEditor;

		/**
		 * @param validator
		 */
		public EditConnectionProfileAction(IValidator validator) {
			super(SeamUIMessages.SEAM_INSTALL_WIZARD_PAGE_EDIT);
			this.validator = validator;
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.action.Action#run()
		 */
		@Override
		public void run() {
			IConnectionProfile selectedProfile = ProfileManager.getInstance()
					.getProfileByName(getFieldEditor().getValue().toString());
			String oldName = getFieldEditor().getValue().toString();

			if (selectedProfile == null) {
				return;
			}
			PropertyDialog.createDialogOn(Display.getCurrent().getActiveShell(),
							"org.eclipse.datatools.connectivity.db.generic.profileProperties", //$NON-NLS-1$
							selectedProfile).open();
			if (!oldName.equals(selectedProfile.getName())) {
				getFieldEditor().setValue(selectedProfile.getName());
				((ITaggedFieldEditor) ((CompositeEditor) connProfileSelEditor)
						.getEditors().get(1)).setTags(getConnectionProfileNameList()
						.toArray(new String[0]));
				oldName = selectedProfile.getName();
			}
			validator.validate(selectedProfile.getName(), null);
		}

		public void setEditor(IFieldEditor connProfileSelEditor) {
			this.connProfileSelEditor = connProfileSelEditor; 
		}
	};

	/**
	 * Handler for ButtonFieldEditor that shows Property Editor dialog for
	 * selected ConnectionProfile
	 * 
	 * @author eskimo
	 */
	private static class NewConnectionProfileAction extends	ButtonFieldEditor.ButtonPressedAction {

		private IValidator validator;
		private IFieldEditor connProfileSelEditor;

		public NewConnectionProfileAction(IValidator validator) {
			super(SeamUIMessages.SEAM_INSTALL_WIZARD_PAGE_NEW);
			this.validator = validator;
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jface.action.Action#run()
		 */
		@Override
		public void run() {
			IProfileListener listener = new ConnectionProfileChangeListener(validator, connProfileSelEditor);

			ProfileManager.getInstance().addProfileListener(listener);
			NewCPWizardCategoryFilter filter = new NewCPWizardCategoryFilter("org.eclipse.datatools.connectivity.db.category"); //$NON-NLS-1$
			NewCPWizard wizard = new NewCPWizard(filter, null);
			new NewConnectionProfileWizard() {
				public boolean performFinish() {
					// create profile only
					try {
						ProfileManager.getInstance().createProfile(
								getProfileName() == null ? "" //$NON-NLS-1$
										: getProfileName(),
								getProfileDescription() == null ? "" //$NON-NLS-1$
										: getProfileDescription(),
								mProviderID,
								getProfileProperties(),
								mProfilePage.getRepository() == null ? "" //$NON-NLS-1$
										: mProfilePage.getRepository()
												.getName(), false);
					} catch (ConnectionProfileException e) {
						SeamCorePlugin.getPluginLog().logError(e);
					}

					return true;
				}
			};
			WizardDialog wizardDialog = new WizardDialog(Display.getCurrent()
					.getActiveShell(), wizard);
			wizardDialog.open();
			ProfileManager.getInstance().removeProfileListener(listener);
		}

		public void setEditor(IFieldEditor connProfileSelEditor) {
			this.connProfileSelEditor = connProfileSelEditor; 
		}
	}

	private static class ConnectionProfileChangeListener implements IProfileListener {

		private IFieldEditor connProfileSelEditor;
		private IValidator validator;

		/**
		 * @param validator
		 */
		public ConnectionProfileChangeListener(IValidator validator, IFieldEditor connProfileSelEditor) {
			this.validator = validator;
			this.connProfileSelEditor = connProfileSelEditor;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.datatools.connectivity.IProfileListener#profileAdded(org.eclipse.datatools.connectivity.IConnectionProfile)
		 */
		public void profileAdded(final IConnectionProfile profile) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					connProfileSelEditor.setValue(profile.getName());
					((ITaggedFieldEditor) ((CompositeEditor) connProfileSelEditor)
							.getEditors().get(1)).setTags(getConnectionProfileNameList()
							.toArray(new String[0]));
				}
			});
			validator.validate(profile.getName(), null);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.datatools.connectivity.IProfileListener#profileChanged(org.eclipse.datatools.connectivity.IConnectionProfile)
		 */
		public void profileChanged(IConnectionProfile profile) {
			profileAdded(profile);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.datatools.connectivity.IProfileListener#profileDeleted(org.eclipse.datatools.connectivity.IConnectionProfile)
		 */
		public void profileDeleted(IConnectionProfile profile) {
			// this event never happens
		}
	}

	private static List<String> getConnectionProfileNameList() {
		IConnectionProfile[] profiles = ProfileManager.getInstance()
				.getProfilesByCategory("org.eclipse.datatools.connectivity.db.category"); //$NON-NLS-1$
		List<String> names = new ArrayList<String>();
		for (IConnectionProfile connectionProfile : profiles) {
			names.add(connectionProfile.getName());
		}
		return names;
	}

	/**
	 * @param seamVersions Array of seam runtime versions. If length == 0 then use all versions
	 * @param defaultSelection
	 * @return Editor to select seam runtime
	 */
	public static IFieldEditor createSeamRuntimeSelectionFieldEditor(SeamVersion[] seamVersions, String defaultValue) {
		return createSeamRuntimeSelectionFieldEditor(seamVersions, defaultValue, false);
	}

	/**
	 * @param seamVersions Array of seam runtime versions. If length == 0 then use all versions
	 * @param defaultSelection
	 * @param canBeEmpty
	 * @return Editor to select seam runtime
	 */
	public static IFieldEditor createSeamRuntimeSelectionFieldEditor(SeamVersion[] seamVersions, String defaultValue, boolean canBeEmpty) {
		DefaultNewSeamRuntimeAction action = new DefaultNewSeamRuntimeAction(seamVersions);
		IFieldEditor jBossSeamRuntimeEditor = createSeamRuntimeSelectionFieldEditor(seamVersions, defaultValue, action, canBeEmpty);
		action.setRuntimeSelectionEditor(jBossSeamRuntimeEditor);
		return jBossSeamRuntimeEditor;
	}

	/**
	 * @param defaultSelection
	 * @return Editor to select seam runtime of all versions
	 */
	public static IFieldEditor createSeamRuntimeSelectionFieldEditor(String defaultValue) {
		return createSeamRuntimeSelectionFieldEditor(new SeamVersion[0], defaultValue, false);
	}

	private static List<String> getRuntimeNames(SeamVersion[] seamVersions) {
		List<String> rtStrings = new ArrayList<String>();
		for (int i = 0; i < seamVersions.length; i++) {
			SeamRuntime[] rts = SeamRuntimeManager.getInstance().getRuntimes(seamVersions[i]);
			for(SeamRuntime seamRuntime : rts) {
				rtStrings.add(seamRuntime.getName());
			}
		}
		return rtStrings;
	}

	public static abstract class NewSeamRuntimeAction extends ButtonFieldEditor.ButtonPressedAction {

		public NewSeamRuntimeAction() {
			super(SeamUIMessages.SEAM_INSTALL_WIZARD_PAGE_ADD);
		}

		abstract protected SeamVersion[] getSeamVersions();

		abstract protected IFieldEditor getRuntimeSelectionEditor();

		public void run() {
			List<SeamRuntime> added = new ArrayList<SeamRuntime>();

			List<SeamVersion> versions = new ArrayList<SeamVersion>(1);
			SeamVersion[] sv = getSeamVersions();
			for (int i = 0; i < sv.length; i++) {
				versions.add(sv[i]);
			}
			if(versions.isEmpty()) {
				SeamVersion[] allVersions = SeamVersion.ALL_VERSIONS;
				for (int i = 0; i < allVersions.length; i++) {
					versions.add(allVersions[i]);
				}
			}
			Wizard wiz = new SeamRuntimeNewWizard(
					(List<SeamRuntime>) new ArrayList<SeamRuntime>(Arrays
							.asList(SeamRuntimeManager.getInstance()
									.getRuntimes())), added, versions);
			WizardDialog dialog = new WizardDialog(Display.getCurrent()
					.getActiveShell(), wiz);
			dialog.open();

			if (!added.isEmpty()) {
				SeamRuntimeManager.getInstance().addRuntime(added.get(0));
				List<String> runtimes = getRuntimeNames(sv);
				getFieldEditor().setValue(added.get(0).getName());
				((ITaggedFieldEditor) ((CompositeEditor) getRuntimeSelectionEditor())
						.getEditors().get(1)).setTags(runtimes
						.toArray(new String[0]));
			}
		}

	}

	/**
	 * Default Action for creating new Seam runtime. 
	 * @author Alexey Kazakov
	 */
	private static class DefaultNewSeamRuntimeAction extends NewSeamRuntimeAction {

		private SeamVersion[] seamVersions;
		private IFieldEditor runtimeSelectionEditor;

		public DefaultNewSeamRuntimeAction() {
			super();
		}

		/**
		 * @param seamVersions
		 */
		public DefaultNewSeamRuntimeAction(SeamVersion[] seamVersions) {
			this();
			this.seamVersions = seamVersions;
		}

		void setRuntimeSelectionEditor(IFieldEditor runtimeSelectionEditor) {
			this.runtimeSelectionEditor = runtimeSelectionEditor;
		}

		protected SeamVersion[] getSeamVersions() {
			return seamVersions;
		}

		protected IFieldEditor getRuntimeSelectionEditor() {
			return runtimeSelectionEditor;
		}
	}

	/**
	 * @return
	 */
	public static IFieldEditor createSeamComponentNameFieldEditor() {
		return IFieldEditorFactory.INSTANCE.createTextEditor(
				ISeamParameter.SEAM_COMPONENT_NAME, SeamUIMessages.SEAM_WIZARD_FACTORY_SEAM_COMPONENT_NAME, ""); //$NON-NLS-1$
	}

	/**
	 * @return
	 */
	public static IFieldEditor createSeamEntityClasNameFieldEditor() {
		return IFieldEditorFactory.INSTANCE.createTextEditor(
				ISeamParameter.SEAM_ENTITY_CLASS_NAME, SeamUIMessages.SEAM_WIZARD_FACTORY_SEAM_ENTITY_CLASS_NAME, ""); //$NON-NLS-1$
	}

	/**
	 * @param defaultSelection if "null" sets first configuration as default
	 * @return
	 */
	public static IFieldEditor createHibernateConsoleConfigurationSelectionFieldEditor(String defaultSelection) {
		HibernateConsolePlugin.getDefault();
		ConsoleConfiguration[] configs = KnownConfigurations.getInstance().getConfigurations();
		List<String> configurationNames = new ArrayList<String>();
		for (int i = 0; i < configs.length; i++) {
			configurationNames.add(configs[i].getName());
		}
		if(defaultSelection==null) {
			if(!configurationNames.isEmpty()) {
				defaultSelection = configurationNames.get(0);
			} else {
				defaultSelection = ""; //$NON-NLS-1$
			}
		}
		IFieldEditor editor = IFieldEditorFactory.INSTANCE.createComboEditor(
				ISeamParameter.HIBERNATE_CONFIGURATION_NAME, 
				SeamUIMessages.GENERATE_SEAM_ENTITIES_WIZARD_HIBERNATE_CONFIGURATION_LABEL, 
				configurationNames, defaultSelection);
		return editor;
	}

	/**
	 * @param defaultSelection full path of resource
	 * @return
	 */
	public static IFieldEditor createViewFolderFieldEditor(String defaultSelection) {
		IFieldEditor viewDirEditor = IFieldEditorFactory.INSTANCE.createBrowseWorkspaceFolderEditor(
				ISeamFacetDataModelProperties.WEB_CONTENTS_FOLDER,
				SeamUIMessages.VIEW_FOLDER_FILED_EDITOR,
				defaultSelection); 
		return viewDirEditor;
	}
}