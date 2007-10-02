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
				IParameter.SEAM_PROJECT_NAME, "Seam Project:", defaultSelection, 
				 new SelectSeamProjectAction(), ValidatorFactory.NO_ERRORS_VALIDATOR);
	}

	/**
	 * @return
	 */
	public static IFieldEditor createSeamLocalInterfaceNameFieldEditor() {
		// TODO Auto-generated method stub
		return SwtFieldEditorFactory.INSTANCE.createTextEditor(
				IParameter.SEAM_LOCAL_INTERFACE_NAME, "Local interface name:", "");
	}

	/**
	 * @return
	 */
	public static IFieldEditor createSeamBeanNameFieldEditor() {
		return SwtFieldEditorFactory.INSTANCE.createTextEditor(
				IParameter.SEAM_BEAN_NAME, "Bean name:", "");
	
	}

	/**
	 * @return
	 */
	public static IFieldEditor createSeamMethodNameFieldEditor() {
		return SwtFieldEditorFactory.INSTANCE.createTextEditor(
				IParameter.SEAM_METHOD_NAME, "Method name:", "");
	
	}

	/**
	 * @return
	 */
	public static IFieldEditor createSeamPageNameFieldEditor() {
		return SwtFieldEditorFactory.INSTANCE.createTextEditor(
				IParameter.SEAM_PAGE_NAME, "Page name:", "");
	}
	
	/**
	 * @return
	 */
	public static IFieldEditor createSeamMasterPageNameFieldEditor() {
		return SwtFieldEditorFactory.INSTANCE.createTextEditor(
				IParameter.SEAM_MASTER_PAGE_NAME, "Master page name:", "");
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
				IParameter.SEAM_COMPONENT_NAME, "Seam component name:", "");
	}

	/**
	 * @return
	 */
	public static IFieldEditor createSeamEntityClasNameFieldEditor() {
		return SwtFieldEditorFactory.INSTANCE.createTextEditor(
				IParameter.SEAM_ENTITY_CLASS_NAME, "Seam entity class name:", "");
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
				defaultSelection = "";
			}
		}
		IFieldEditor editor = SwtFieldEditorFactory.INSTANCE.createComboEditor(
				IParameter.HIBERNATE_CONFIGURATION_NAME, 
				SeamUIMessages.GENERATE_SEAM_ENTITIES_WIZARD_HIBERNATE_CONFIGURATION_LABEL, 
				configurationNames, defaultSelection);
		return editor;
	}
}