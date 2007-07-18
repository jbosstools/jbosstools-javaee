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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.types.FilterSet;
import org.apache.tools.ant.types.FilterSetCollection;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.INewWizard;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.project.facet.AntCopyUtils;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.seam.internal.core.project.facet.SeamFacetFilterSetFactory;

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
		//setDefaultPageImageDescriptor();
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
					"${" + IParameter.SEAM_PROJECT_LOCATION_PATH + "}/src/${" + ISeamFacetDataModelProperties.SESION_BEAN_PACKAGE_PATH + "}/${" + IParameter.SEAM_LOCAL_INTERFACE_NAME +"}.java/"});
			ACTION_WAR_MAPPING.add(new String[]{
					"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/test/ActionTest.java",
					"${" + IParameter.SEAM_PROJECT_LOCATION_PATH + "}/src/${" + ISeamFacetDataModelProperties.TEST_CASES_PACKAGE_PATH + "}/${"+ IParameter.SEAM_LOCAL_INTERFACE_NAME +"}Test.java/"});
			ACTION_WAR_MAPPING.add(new String[]{
					"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/test/testng.xml",
					"${" + IParameter.SEAM_PROJECT_LOCATION_PATH + "}/src/${" + ISeamFacetDataModelProperties.TEST_CASES_PACKAGE_PATH + "}/${"+ IParameter.SEAM_LOCAL_INTERFACE_NAME +"}Test.xml/"});
			ACTION_WAR_MAPPING.add(new String[]{
					"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/view/action.xhtml",
					"${" + IParameter.SEAM_PROJECT_WEBCONTENT_PATH + "}/${" + IParameter.SEAM_PAGE_NAME +"}.xhtml"});	
			
			// initialize ear files mapping
			ACTION_EAR_MAPPING.add(new String[]{
					"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/src/ActionBean.java",
					"${" + IParameter.SEAM_PROJECT_LOCATION_PATH + "}/src/${" + ISeamFacetDataModelProperties.SESION_BEAN_PACKAGE_PATH + "}/${" + IParameter.SEAM_BEAN_NAME +"}.java/"});
			ACTION_EAR_MAPPING.add(new String[]{
					"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/src/Action.java",
					"${" + IParameter.SEAM_PROJECT_LOCATION_PATH + "}/src/${" + ISeamFacetDataModelProperties.SESION_BEAN_PACKAGE_PATH + "}/${" + IParameter.SEAM_LOCAL_INTERFACE_NAME +"}.java/"});
			ACTION_EAR_MAPPING.add(ACTION_WAR_MAPPING.get(1));
			ACTION_EAR_MAPPING.add(ACTION_WAR_MAPPING.get(2));
			ACTION_EAR_MAPPING.add(ACTION_WAR_MAPPING.get(3));
		}
	};
}
