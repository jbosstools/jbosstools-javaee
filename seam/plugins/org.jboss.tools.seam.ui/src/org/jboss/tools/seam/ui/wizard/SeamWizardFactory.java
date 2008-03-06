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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.jboss.tools.seam.core.project.facet.SeamRuntime;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeManager;
import org.jboss.tools.seam.core.project.facet.SeamVersion;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.seam.ui.SeamUIMessages;
import org.jboss.tools.seam.ui.internal.project.facet.IValidator;
import org.jboss.tools.seam.ui.internal.project.facet.ValidatorFactory;
import org.jboss.tools.seam.ui.widget.editor.ButtonFieldEditor;
import org.jboss.tools.seam.ui.widget.editor.CompositeEditor;
import org.jboss.tools.seam.ui.widget.editor.IFieldEditor;
import org.jboss.tools.seam.ui.widget.editor.IFieldEditorFactory;
import org.jboss.tools.seam.ui.widget.editor.ITaggedFieldEditor;
import org.jboss.tools.seam.ui.widget.editor.SeamRuntimeListFieldEditor.SeamRuntimeNewWizard;

/**
 * @author eskimo
 *
 */
public class SeamWizardFactory {
	public static IFieldEditor createSeamProjectSelectionFieldEditor(
			String defaultSelection) {
		return IFieldEditorFactory.INSTANCE.createButtonFieldEditor(
				IParameter.SEAM_PROJECT_NAME, SeamUIMessages.SEAM_WIZARD_FACTORY_SEAM_PROJECT, defaultSelection, 
				 new SelectSeamProjectAction(), ValidatorFactory.NO_ERRORS_VALIDATOR);
	}

	/**
	 * @return
	 */
	public static IFieldEditor createSeamLocalInterfaceNameFieldEditor() {
		return IFieldEditorFactory.INSTANCE.createTextEditor(
				IParameter.SEAM_LOCAL_INTERFACE_NAME, SeamUIMessages.SEAM_WIZARD_FACTORY_LOCAL_INTERFACE_NAME, ""); //$NON-NLS-1$
	}

	/**
	 * @return
	 */
	public static IFieldEditor createSeamBeanNameFieldEditor() {
		return IFieldEditorFactory.INSTANCE.createTextEditor(
				IParameter.SEAM_BEAN_NAME, SeamUIMessages.SEAM_WIZARD_FACTORY_BEAN_NAME, ""); //$NON-NLS-1$
	}

	/**
	 * @return
	 */
	public static IFieldEditor createSeamMethodNameFieldEditor() {
		return IFieldEditorFactory.INSTANCE.createTextEditor(
				IParameter.SEAM_METHOD_NAME, SeamUIMessages.SEAM_WIZARD_FACTORY_METHOD_NAME, ""); //$NON-NLS-1$
	}

	/**
	 * @return
	 */
	public static IFieldEditor createSeamPageNameFieldEditor() {
		return IFieldEditorFactory.INSTANCE.createTextEditor(
				IParameter.SEAM_PAGE_NAME, SeamUIMessages.SEAM_WIZARD_FACTORY_PAGE_NAME, ""); //$NON-NLS-1$
	}
	
	/**
	 * @return
	 */
	public static IFieldEditor createSeamMasterPageNameFieldEditor() {
		return IFieldEditorFactory.INSTANCE.createTextEditor(
				IParameter.SEAM_MASTER_PAGE_NAME, SeamUIMessages.SEAM_WIZARD_FACTORY_MASTER_PAGE_NAME, ""); //$NON-NLS-1$
	}
	
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
				IParameter.SEAM_PACKAGE_NAME, SeamUIMessages.SEAM_WIZARD_FACTORY_PACKAGE_NAME, defaultSelection, 
				 new SelectJavaPackageAction(), ValidatorFactory.NO_ERRORS_VALIDATOR);
	}

	/**
	 * @param seamVersions Array of seam runtime versions. If length == 0 then use all versions
	 * @param defaultSelection
	 * @param action
	 * @return Editor to select seam runtime
	 */
	public static IFieldEditor createSeamRuntimeSelectionFieldEditor(SeamVersion[] seamVersions, String defaultValue, NewSeamRuntimeAction action) {
		IFieldEditor jBossSeamRuntimeEditor = IFieldEditorFactory.INSTANCE
		.createComboWithButton(ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME,
				SeamUIMessages.SEAM_INSTALL_WIZARD_PAGE_SEAM_RUNTIME, getRuntimeNames(seamVersions), 
				defaultValue, 
				true, action, (IValidator)null);
		return jBossSeamRuntimeEditor;
	}

	/**
	 * @param seamVersions Array of seam runtime versions. If length == 0 then use all versions
	 * @param defaultSelection
	 * @return Editor to select seam runtime
	 */
	public static IFieldEditor createSeamRuntimeSelectionFieldEditor(SeamVersion[] seamVersions, String defaultValue) {
		DefaultNewSeamRuntimeAction action = new DefaultNewSeamRuntimeAction(seamVersions);
		IFieldEditor jBossSeamRuntimeEditor = createSeamRuntimeSelectionFieldEditor(seamVersions, defaultValue, action);
		action.setRuntimeSelectionEditor(jBossSeamRuntimeEditor);
		return jBossSeamRuntimeEditor;
	}

	/**
	 * @param defaultSelection
	 * @return Editor to select seam runtime of all versions
	 */
	public static IFieldEditor createSeamRuntimeSelectionFieldEditor(String defaultValue) {
		DefaultNewSeamRuntimeAction action = new DefaultNewSeamRuntimeAction(new SeamVersion[0]);
		IFieldEditor jBossSeamRuntimeEditor = createSeamRuntimeSelectionFieldEditor(new SeamVersion[0], defaultValue, action);
		action.setRuntimeSelectionEditor(jBossSeamRuntimeEditor);
		return jBossSeamRuntimeEditor;
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

			if (added.size() > 0) {
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
				IParameter.SEAM_COMPONENT_NAME, SeamUIMessages.SEAM_WIZARD_FACTORY_SEAM_COMPONENT_NAME, ""); //$NON-NLS-1$
	}

	/**
	 * @return
	 */
	public static IFieldEditor createSeamEntityClasNameFieldEditor() {
		return IFieldEditorFactory.INSTANCE.createTextEditor(
				IParameter.SEAM_ENTITY_CLASS_NAME, SeamUIMessages.SEAM_WIZARD_FACTORY_SEAM_ENTITY_CLASS_NAME, ""); //$NON-NLS-1$
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
			if(configurationNames.size()>0) {
				defaultSelection = configurationNames.get(0);
			} else {
				defaultSelection = ""; //$NON-NLS-1$
			}
		}
		IFieldEditor editor = IFieldEditorFactory.INSTANCE.createComboEditor(
				IParameter.HIBERNATE_CONFIGURATION_NAME, 
				SeamUIMessages.GENERATE_SEAM_ENTITIES_WIZARD_HIBERNATE_CONFIGURATION_LABEL, 
				configurationNames, defaultSelection);
		return editor;
	}
}