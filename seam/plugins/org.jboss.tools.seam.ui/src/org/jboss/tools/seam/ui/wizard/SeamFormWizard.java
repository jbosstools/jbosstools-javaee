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
import java.util.Map;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.common.ui.widget.editor.INamedElement;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.seam.ui.ISeamHelpContextIds;
import org.jboss.tools.seam.ui.SeamUIMessages;

/**
 * 
 * @author eskimo
 *
 */
public class SeamFormWizard extends SeamBaseWizard implements INewWizard {

	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(pageContainer, ISeamHelpContextIds.NEW_SEAM_FORM);
	}

	/**
	 * 
	 */
	public SeamFormWizard() {
		super(CREATE_SEAM_FORM);
		setWindowTitle(SeamUIMessages.SEAM_FORM_WIZARD_NEW_SEAM_FORM);
		setDefaultPageImageDescriptor(ImageDescriptor.createFromFile(SeamActionWizard.class, "SeamFormWizBan.png")); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		addPage(new SeamFormWizardPage1(getInitialSelection()));
	}

	private static IUndoableOperation CREATE_SEAM_FORM = new SeamFormCreateOperation();

	public static class SeamFormCreateOperation extends SeamBaseOperation {

		public SeamFormCreateOperation() {
			super(SeamUIMessages.SEAM_FORM_WIZARD_FORM_CREATING_OPERATION);
		}

		/*
		 * (non-Javadoc)
		 * @see org.jboss.tools.seam.ui.wizard.SeamBaseOperation#getFileMappings(java.util.Map)
		 */
		@Override
		public List<FileMapping> getFileMappings(Map<String, Object> vars) {
			return ACTION_MAPPING;
		}

		public static final List<FileMapping> ACTION_MAPPING = new ArrayList<FileMapping>();

		static {
			// initialize war files mapping
			ACTION_MAPPING.add(new FileMapping(
					"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/src/FormActionJavaBean.java", //$NON-NLS-1$ //$NON-NLS-2$
					"${" + IParameter.SEAM_PROJECT_SRC_ACTION + "}/${" + ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_PATH + "}/${" + IParameter.SEAM_LOCAL_INTERFACE_NAME +"}.java", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					FileMapping.TYPE.WAR,
					false));
			ACTION_MAPPING.add(new FileMapping(
					"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/test/FormTest.java", //$NON-NLS-1$ //$NON-NLS-2$
					"${" + IParameter.TEST_SOURCE_FOLDER + "}/${" + ISeamFacetDataModelProperties.TEST_CASES_PACKAGE_PATH + "}/${"+ IParameter.SEAM_LOCAL_INTERFACE_NAME +"}Test.java", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					FileMapping.TYPE.WAR,
					true));
			ACTION_MAPPING.add(new FileMapping(
					"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/test/testng.xml", //$NON-NLS-1$ //$NON-NLS-2$
					"${" + IParameter.TEST_SOURCE_FOLDER + "}/${" + ISeamFacetDataModelProperties.TEST_CASES_PACKAGE_PATH + "}/${"+ IParameter.SEAM_LOCAL_INTERFACE_NAME +"}Test.xml", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					FileMapping.TYPE.WAR,
					true));
			ACTION_MAPPING.add(new FileMapping(
					"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/view/form.xhtml", //$NON-NLS-1$ //$NON-NLS-2$
					"${" + IParameter.SEAM_PROJECT_WEBCONTENT_PATH + "}/${" + IParameter.SEAM_PAGE_NAME +"}.xhtml",	//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					FileMapping.TYPE.WAR,
					false));
			// initialize ear files mapping
			ACTION_MAPPING.add(new FileMapping(
					"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/src/FormActionBean.java", //$NON-NLS-1$ //$NON-NLS-2$
					"${" + IParameter.SEAM_PROJECT_SRC_ACTION + "}/${" + ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_PATH + "}/${" + IParameter.SEAM_BEAN_NAME +"}.java", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					FileMapping.TYPE.EAR,
					false));
			ACTION_MAPPING.add(new FileMapping(
					"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/src/FormAction.java", //$NON-NLS-1$ //$NON-NLS-2$
					"${" + IParameter.SEAM_PROJECT_SRC_ACTION + "}/${" + ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_PATH + "}/${" + IParameter.SEAM_LOCAL_INTERFACE_NAME +"}.java", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					FileMapping.TYPE.EAR,
					false));
			ACTION_MAPPING.add(new FileMapping(
					"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/test/FormTest.java", //$NON-NLS-1$ //$NON-NLS-2$
					"${" + IParameter.TEST_SOURCE_FOLDER + "}/${" + ISeamFacetDataModelProperties.TEST_CASES_PACKAGE_PATH + "}/${"+ IParameter.SEAM_LOCAL_INTERFACE_NAME +"}Test.java", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					FileMapping.TYPE.EAR,
					true));
			ACTION_MAPPING.add(new FileMapping(
					"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/test/testng.xml", //$NON-NLS-1$ //$NON-NLS-2$
					"${" + IParameter.TEST_SOURCE_FOLDER + "}/${" + ISeamFacetDataModelProperties.TEST_CASES_PACKAGE_PATH + "}/${"+ IParameter.SEAM_LOCAL_INTERFACE_NAME +"}Test.xml", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					FileMapping.TYPE.EAR,
					true));
			ACTION_MAPPING.add(new FileMapping(
					"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/view/form.xhtml", //$NON-NLS-1$ //$NON-NLS-2$
					"${" + IParameter.SEAM_PROJECT_WEBCONTENT_PATH + "}/${" + IParameter.SEAM_PAGE_NAME +"}.xhtml",	//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					FileMapping.TYPE.EAR,
					false));
		}

		/*
		 * (non-Javadoc)
		 * @see org.jboss.tools.seam.ui.wizard.SeamBaseOperation#getSessionBeanPackageName(org.eclipse.core.runtime.preferences.IEclipsePreferences, java.util.Map)
		 */
		@Override
		protected String getSessionBeanPackageName(IEclipsePreferences seamFacetPrefs, Map<String, INamedElement> wizardParams) {
			return wizardParams.get(IParameter.SEAM_PACKAGE_NAME).getValue().toString();
		}
	};
}