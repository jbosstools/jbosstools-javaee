/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 

package org.jboss.tools.seam.ui.wizard;

import org.eclipse.core.resources.IProject;
import org.jboss.tools.seam.ui.internal.project.facet.SwtFieldEditorFactory;
import org.jboss.tools.seam.ui.internal.project.facet.ValidatorFactory;
import org.jboss.tools.seam.ui.widget.editor.IFieldEditor;
import org.jboss.tools.seam.ui.widget.editor.ButtonFieldEditor.ButtonPressedAction;

/**
 * @author eskimo
 *
 */
public class SeamWizardFactory {
	public static IFieldEditor createSeamProjctSelectionFieldEditor(
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
				IParameter.SEAM_PAGE_NAME, "Master page name:", "");
	}
	
	public static final IFieldEditor[] createDefaultWizardEditors(String defaultSelection) {
		return new IFieldEditor[]{
			SeamWizardFactory.createSeamProjctSelectionFieldEditor(SeamWizardUtils.getSelectedProjectName()),
			SeamWizardFactory.createSeamLocalInterfaceNameFieldEditor(),
			SeamWizardFactory.createSeamBeanNameFieldEditor(),
			SeamWizardFactory.createSeamMethodNameFieldEditor(),
			SeamWizardFactory.createSeamPageNameFieldEditor()	
		};
	}
}
