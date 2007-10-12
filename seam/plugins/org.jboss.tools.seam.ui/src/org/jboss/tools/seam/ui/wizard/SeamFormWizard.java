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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.INewWizard;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.seam.ui.SeamUIMessages;

/**
 * 
 * @author eskimo
 *
 */
public class SeamFormWizard extends SeamBaseWizard implements INewWizard {

	/**
	 * 
	 */
	public SeamFormWizard() {
		super(CREATE_SEAM_FORM);
		setWindowTitle(SeamUIMessages.SEAM_FORM_WIZARD_NEW_SEAM_FORM);
		setDefaultPageImageDescriptor(ImageDescriptor.createFromFile(SeamActionWizard.class, "SeamFormWizBan.png")); //$NON-NLS-1$
		addPage(new SeamFormWizardPage1());
	}

	private static IUndoableOperation CREATE_SEAM_FORM = new SeamFormCreateOperation();
	
	public static class SeamFormCreateOperation extends SeamBaseOperation {
		
		public SeamFormCreateOperation() {
			super(SeamUIMessages.SEAM_FORM_WIZARD_FORM_CREATING_OPERATION);
		}
		
		public File getBeanFile(Map<String, Object> vars)  {
			return new File(getSeamFolder(vars),"src/FormActionJavaBean.java"); //$NON-NLS-1$
		}
		
		public File getTestClassFile(Map<String, Object> vars) {
			return new File(getSeamFolder(vars),"test/FormTest.java"); //$NON-NLS-1$
		}
		
		public File getTestngXmlFile(Map<String, Object> vars) {
			return new File(getSeamFolder(vars),"test/testng.xml"); //$NON-NLS-1$
		}
		
		public File getPageXhtml(Map<String, Object> vars) {
			return new File(getSeamFolder(vars),"view/form.xhtml"); //$NON-NLS-1$
		}

		/* (non-Javadoc)
		 * @see org.jboss.tools.seam.ui.wizard.SeamBaseOperation#getFileMappings(java.util.Map)
		 */
		@Override
		public List<String[]> getFileMappings(Map<String, Object> vars) {
			if("war".equals(vars.get(ISeamFacetDataModelProperties.JBOSS_AS_DEPLOY_AS))) //$NON-NLS-1$
				return FORM_WAR_MAPPING;
			else
				return FORM_EAR_MAPPING;
		}
		
		public static final List<String[]> FORM_WAR_MAPPING = new ArrayList<String[]>();
		
		public static final List<String[]> FORM_EAR_MAPPING = new ArrayList<String[]>();
		
		static {
			FORM_WAR_MAPPING.add(new String[]{
					"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/src/FormActionJavaBean.java", //$NON-NLS-1$ //$NON-NLS-2$
					"${" + IParameter.SEAM_PROJECT_LOCATION_PATH + "}/src/action/${" + ISeamFacetDataModelProperties.SESION_BEAN_PACKAGE_PATH + "}/${" + IParameter.SEAM_LOCAL_INTERFACE_NAME +"}.java"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			FORM_WAR_MAPPING.add(new String[]{
					"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/test/FormTest.java", //$NON-NLS-1$ //$NON-NLS-2$
					"${" + IParameter.SEAM_TEST_PROJECT_LOCATION_PATH + "}/test-src/${" + ISeamFacetDataModelProperties.TEST_CASES_PACKAGE_PATH + "}/${"+ IParameter.SEAM_LOCAL_INTERFACE_NAME +"}Test.java"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			FORM_WAR_MAPPING.add(new String[]{
					"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/test/testng.xml", //$NON-NLS-1$ //$NON-NLS-2$
					"${" + IParameter.SEAM_TEST_PROJECT_LOCATION_PATH + "}/test-src/${" + ISeamFacetDataModelProperties.TEST_CASES_PACKAGE_PATH + "}/${"+ IParameter.SEAM_LOCAL_INTERFACE_NAME +"}Test.xml"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			FORM_WAR_MAPPING.add(new String[]{
					"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/view/form.xhtml", //$NON-NLS-1$ //$NON-NLS-2$
					"${" + IParameter.SEAM_PROJECT_WEBCONTENT_PATH + "}/${" + IParameter.SEAM_PAGE_NAME +"}.xhtml"});	 //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		
			// initialize ear files mapping
			FORM_EAR_MAPPING.add(new String[]{
					"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/src/FormActionBean.java", //$NON-NLS-1$ //$NON-NLS-2$
					"${" + IParameter.SEAM_EJB_PROJECT_LOCATION_PATH + "}/ejbModule/${" + ISeamFacetDataModelProperties.SESION_BEAN_PACKAGE_PATH + "}/${" + IParameter.SEAM_BEAN_NAME +"}.java"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			FORM_EAR_MAPPING.add(new String[]{
					"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/src/FormAction.java", //$NON-NLS-1$ //$NON-NLS-2$
					"${" + IParameter.SEAM_EJB_PROJECT_LOCATION_PATH + "}/ejbModule/${" + ISeamFacetDataModelProperties.SESION_BEAN_PACKAGE_PATH + "}/${" + IParameter.SEAM_LOCAL_INTERFACE_NAME +"}.java"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			FORM_EAR_MAPPING.add(new String[]{
					"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/test/FormTest.java", //$NON-NLS-1$ //$NON-NLS-2$
					"${" + IParameter.SEAM_TEST_PROJECT_LOCATION_PATH + "}/test-src/${" + ISeamFacetDataModelProperties.TEST_CASES_PACKAGE_PATH + "}/${"+ IParameter.SEAM_LOCAL_INTERFACE_NAME +"}Test.java"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			FORM_EAR_MAPPING.add(new String[]{
					"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/test/testng.xml", //$NON-NLS-1$ //$NON-NLS-2$
					"${" + IParameter.SEAM_TEST_PROJECT_LOCATION_PATH + "}/test-src/${" + ISeamFacetDataModelProperties.TEST_CASES_PACKAGE_PATH + "}/${"+ IParameter.SEAM_LOCAL_INTERFACE_NAME +"}Test.xml"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			FORM_EAR_MAPPING.add(FORM_WAR_MAPPING.get(3));
		}
	};
}
