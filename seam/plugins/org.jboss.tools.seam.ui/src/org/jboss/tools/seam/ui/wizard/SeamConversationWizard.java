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

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.INewWizard;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.seam.ui.wizard.SeamEntityWizard.SeamEntityCreateOperation;

public class SeamConversationWizard extends SeamBaseWizard implements INewWizard {

	public SeamConversationWizard() {
		super(CREATE_SEAM_CONVERSATION);
		setWindowTitle("reate New Conversation");
		addPage(new SeamConversationWizardPage1());
	}

	public static final IUndoableOperation CREATE_SEAM_CONVERSATION = new SeamConversationCreateOperation();
	/**
	 * 
	 * TODO move operations to core plugin
	 */
	public static class SeamConversationCreateOperation extends SeamBaseOperation{
		
		/**
		 * @param label
		 */
		public SeamConversationCreateOperation() {
			super(("Entity creating operation"));
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
					"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/src/ConversationBean.java",
					"${" + IParameter.SEAM_PROJECT_LOCATION_PATH + "}/src/action/${" + ISeamFacetDataModelProperties.SESION_BEAN_PACKAGE_PATH + "}/${" + IParameter.SEAM_BEAN_NAME +"}.java"});
			ACTION_WAR_MAPPING.add(new String[]{
					"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/src/Conversation.java",
					"${" + IParameter.SEAM_PROJECT_LOCATION_PATH + "}/src/action/${" + ISeamFacetDataModelProperties.SESION_BEAN_PACKAGE_PATH + "}/${" + IParameter.SEAM_LOCAL_INTERFACE_NAME +"}.java"});
			ACTION_WAR_MAPPING.add(new String[]{
					"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/view/conversation.xhtml",
					"${" + IParameter.SEAM_PROJECT_WEBCONTENT_PATH + "}/${" + IParameter.SEAM_PAGE_NAME +"}.xhtml"});	
			
			// initialize ear files mapping
			ACTION_EAR_MAPPING.add(new String[]{
					"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/src/ConversationBean.java",
					"${" + IParameter.SEAM_EJB_PROJECT_LOCATION_PATH + "}/ejbModule/${" + ISeamFacetDataModelProperties.SESION_BEAN_PACKAGE_PATH + "}/${" + IParameter.SEAM_BEAN_NAME +"}.java"});
			ACTION_EAR_MAPPING.add(new String[]{
					"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/src/Conversation.java",
					"${" + IParameter.SEAM_EJB_PROJECT_LOCATION_PATH + "}/ejbModule/${" + ISeamFacetDataModelProperties.SESION_BEAN_PACKAGE_PATH + "}/${" + IParameter.SEAM_LOCAL_INTERFACE_NAME +"}.java"});
			ACTION_EAR_MAPPING.add(ACTION_WAR_MAPPING.get(2));
		}
	};

}
