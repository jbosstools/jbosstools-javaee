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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.INewWizard;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;

/**
 * 
 * @author eskimo
 *
 */
public class SeamActionWizard extends SeamBaseWizard implements INewWizard {

	/**
	 * 
	 */
	public SeamActionWizard() {
		super(CREATE_SEAM_ACTION);
		setWindowTitle("New Seam Action");
		setDefaultPageImageDescriptor(ImageDescriptor.createFromFile(SeamActionWizard.class, "SeamFormWizBan.png"));
		addPage(new SeamActionWizardPage1());
	}

	public static IUndoableOperation CREATE_SEAM_ACTION = new SeamActionCreateOperation();
	
	/**
	 * 
	 * TODO move operations to core plugin
	 */
	public static class SeamActionCreateOperation extends SeamBaseOperation{
		
		/**
		 * @param label
		 */
		public SeamActionCreateOperation() {
			super(("Action creating operation"));
		}

		@Override
		public List<String[]> getFileMappings(Map<String, Object> vars) {
			if("war".equals(vars.get(ISeamFacetDataModelProperties.JBOSS_AS_DEPLOY_AS)))
				return ACTION_WAR_MAPPING;
			else
				return ACTION_EAR_MAPPING;
		}
		
		public static final List<String[]> ACTION_WAR_MAPPING = new ArrayList<String[]>();
		
		public static final List<String[]> ACTION_EAR_MAPPING = new ArrayList<String[]>();
		
		static {
			// initialize war files mapping
			ACTION_WAR_MAPPING.add(new String[]{
					"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/src/ActionJavaBean.java",
					"${" + IParameter.SEAM_PROJECT_LOCATION_PATH + "}/src/${" + ISeamFacetDataModelProperties.SESION_BEAN_PACKAGE_PATH + "}/${" + IParameter.SEAM_LOCAL_INTERFACE_NAME +"}.java"});
			ACTION_WAR_MAPPING.add(new String[]{
					"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/test/ActionTest.java",
					"${" + IParameter.SEAM_PROJECT_LOCATION_PATH + "}/src/${" + ISeamFacetDataModelProperties.TEST_CASES_PACKAGE_PATH + "}/${"+ IParameter.SEAM_LOCAL_INTERFACE_NAME +"}Test.java"});
			ACTION_WAR_MAPPING.add(new String[]{
					"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/test/testng.xml",
					"${" + IParameter.SEAM_PROJECT_LOCATION_PATH + "}/src/${" + ISeamFacetDataModelProperties.TEST_CASES_PACKAGE_PATH + "}/${"+ IParameter.SEAM_LOCAL_INTERFACE_NAME +"}Test.xml"});
			ACTION_WAR_MAPPING.add(new String[]{
					"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/view/action.xhtml",
					"${" + IParameter.SEAM_PROJECT_WEBCONTENT_PATH + "}/${" + IParameter.SEAM_PAGE_NAME +"}.xhtml"});	
			
			// initialize ear files mapping
			ACTION_EAR_MAPPING.add(new String[]{
					"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/src/ActionBean.java",
					"${" + IParameter.SEAM_EAR_PROJECT_LOCATION_PATH + "}/ejbModule/${" + ISeamFacetDataModelProperties.SESION_BEAN_PACKAGE_PATH + "}/${" + IParameter.SEAM_BEAN_NAME +"}.java"});
			ACTION_EAR_MAPPING.add(new String[]{
					"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/src/Action.java",
					"${" + IParameter.SEAM_EAR_PROJECT_LOCATION_PATH + "}/ejbModule/${" + ISeamFacetDataModelProperties.SESION_BEAN_PACKAGE_PATH + "}/${" + IParameter.SEAM_LOCAL_INTERFACE_NAME +"}.java"});
			ACTION_EAR_MAPPING.add(new String[]{
					"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/test/ActionTest.java",
					"${" + IParameter.SEAM_EAR_PROJECT_LOCATION_PATH + "}/ejbModule/${" + ISeamFacetDataModelProperties.TEST_CASES_PACKAGE_PATH + "}/${"+ IParameter.SEAM_LOCAL_INTERFACE_NAME +"}Test.java"});
			ACTION_EAR_MAPPING.add(new String[]{
					"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/test/testng.xml",
					"${" + IParameter.SEAM_EAR_PROJECT_LOCATION_PATH + "}/ejbModule/${" + ISeamFacetDataModelProperties.TEST_CASES_PACKAGE_PATH + "}/${"+ IParameter.SEAM_LOCAL_INTERFACE_NAME +"}Test.xml"});
			ACTION_EAR_MAPPING.add(ACTION_WAR_MAPPING.get(3));
		}
	};
}
