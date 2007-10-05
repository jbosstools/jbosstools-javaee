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
import java.util.List;

import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.jboss.tools.seam.ui.SeamUIMessages;
import org.jboss.tools.seam.ui.internal.project.facet.ValidatorFactory;
import org.jboss.tools.seam.ui.widget.editor.IFieldEditor;
import org.jboss.tools.seam.ui.widget.editor.SwtFieldEditorFactory;

/**
 * @author eskimo
 *
 */
public class SeamWizardFactory {
	public static IFieldEditor createSeamProjectSelectionFieldEditor(
			String defaultSelection) {
		return SwtFieldEditorFactory.INSTANCE.createButtonFieldEditor(
				IParameter.SEAM_PROJECT_NAME, SeamUIMessages.SEAM_WIZARD_FACTORY_SEAM_PROJECT, defaultSelection, 
				 new SelectSeamProjectAction(), ValidatorFactory.NO_ERRORS_VALIDATOR);
	}

	/**
	 * @return
	 */
	public static IFieldEditor createSeamLocalInterfaceNameFieldEditor() {
		// TODO Auto-generated method stub
		return SwtFieldEditorFactory.INSTANCE.createTextEditor(
				IParameter.SEAM_LOCAL_INTERFACE_NAME, SeamUIMessages.SEAM_WIZARD_FACTORY_LOCAL_INTERFACE_NAME, ""); //$NON-NLS-1$
	}

	/**
	 * @return
	 */
	public static IFieldEditor createSeamBeanNameFieldEditor() {
		return SwtFieldEditorFactory.INSTANCE.createTextEditor(
				IParameter.SEAM_BEAN_NAME, SeamUIMessages.SEAM_WIZARD_FACTORY_BEAN_NAME, ""); //$NON-NLS-1$
	
	}

	/**
	 * @return
	 */
	public static IFieldEditor createSeamMethodNameFieldEditor() {
		return SwtFieldEditorFactory.INSTANCE.createTextEditor(
				IParameter.SEAM_METHOD_NAME, SeamUIMessages.SEAM_WIZARD_FACTORY_METHOD_NAME, ""); //$NON-NLS-1$
	
	}

	/**
	 * @return
	 */
	public static IFieldEditor createSeamPageNameFieldEditor() {
		return SwtFieldEditorFactory.INSTANCE.createTextEditor(
				IParameter.SEAM_PAGE_NAME, SeamUIMessages.SEAM_WIZARD_FACTORY_PAGE_NAME, ""); //$NON-NLS-1$
	}
	
	/**
	 * @return
	 */
	public static IFieldEditor createSeamMasterPageNameFieldEditor() {
		return SwtFieldEditorFactory.INSTANCE.createTextEditor(
				IParameter.SEAM_MASTER_PAGE_NAME, SeamUIMessages.SEAM_WIZARD_FACTORY_MASTER_PAGE_NAME, ""); //$NON-NLS-1$
	}
	
	public static final IFieldEditor[] createBaseFormFieldEditors(String defaultSelection) {
		return new IFieldEditor[]{
			SeamWizardFactory.createSeamProjectSelectionFieldEditor(SeamWizardUtils.getSelectedProjectName()),
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
	public static IFieldEditor createSeamComponentNameFieldEditor() {
		// TODO Auto-generated method stub
		return SwtFieldEditorFactory.INSTANCE.createTextEditor(
				IParameter.SEAM_COMPONENT_NAME, SeamUIMessages.SEAM_WIZARD_FACTORY_SEAM_COMPONENT_NAME, ""); //$NON-NLS-1$
	}

	/**
	 * @return
	 */
	public static IFieldEditor createSeamEntityClasNameFieldEditor() {
		return SwtFieldEditorFactory.INSTANCE.createTextEditor(
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
		IFieldEditor editor = SwtFieldEditorFactory.INSTANCE.createComboEditor(
				IParameter.HIBERNATE_CONFIGURATION_NAME, 
				SeamUIMessages.GENERATE_SEAM_ENTITIES_WIZARD_HIBERNATE_CONFIGURATION_LABEL, 
				configurationNames, defaultSelection);
		return editor;
	}
}