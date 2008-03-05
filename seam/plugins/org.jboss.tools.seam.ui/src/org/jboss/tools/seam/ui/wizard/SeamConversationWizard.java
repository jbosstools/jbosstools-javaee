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
import org.eclipse.ui.INewWizard;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.seam.ui.SeamUIMessages;
import org.jboss.tools.seam.ui.widget.editor.INamedElement;

public class SeamConversationWizard extends SeamBaseWizard implements INewWizard {

	public SeamConversationWizard() {
		super(CREATE_SEAM_CONVERSATION);
		setWindowTitle(SeamUIMessages.SEAM_CONVERSATION_WIZARD_CREATE_NEW_CONVERSATION);
		setDefaultPageImageDescriptor(ImageDescriptor.createFromFile(SeamConversationWizard.class, "SeamWebProjectWizBan.png"));		
	}

	@Override
	public void addPages() {
		super.addPages();
		addPage(new SeamConversationWizardPage1(getInitialSelection()));
	}

	public static final IUndoableOperation CREATE_SEAM_CONVERSATION = new SeamConversationCreateOperation();

	/**
	 * TODO move operations to core plugin
	 */
	public static class SeamConversationCreateOperation extends SeamBaseOperation{

		/**
		 * @param label
		 */
		public SeamConversationCreateOperation() {
			super((SeamUIMessages.SEAM_CONVERSATION_WIZARD_ENTITY_CREATING_OPERATION));
		}

		@Override
		public List<String[]> getFileMappings(Map<String, Object> vars) {
			if("war".equals(vars.get(ISeamFacetDataModelProperties.JBOSS_AS_DEPLOY_AS))) //$NON-NLS-1$
				return ACTION_WAR_MAPPING;
			else
				return ACTION_EAR_MAPPING;
		}

		public static final List<String[]> ACTION_WAR_MAPPING = new ArrayList<String[]>();

		public static final List<String[]> ACTION_EAR_MAPPING = new ArrayList<String[]>();

		static {
			// initialize war files mapping
			ACTION_WAR_MAPPING.add(new String[]{
					"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/src/ConversationJavaBean.java", //$NON-NLS-1$ //$NON-NLS-2$
					"${" + IParameter.SEAM_PROJECT_SRC_ACTION + "}/${" + ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_PATH + "}/${" + IParameter.SEAM_BEAN_NAME +"}.java"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			ACTION_WAR_MAPPING.add(new String[]{
					"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/view/conversation.xhtml", //$NON-NLS-1$ //$NON-NLS-2$
					"${" + IParameter.SEAM_PROJECT_WEBCONTENT_PATH + "}/${" + IParameter.SEAM_PAGE_NAME +"}.xhtml"});	 //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

			// initialize ear files mapping
			ACTION_EAR_MAPPING.add(new String[]{
					"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/src/ConversationBean.java", //$NON-NLS-1$ //$NON-NLS-2$
					"${" + IParameter.SEAM_PROJECT_SRC_ACTION + "}/${" + ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_PATH + "}/${" + IParameter.SEAM_BEAN_NAME +"}.java"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			ACTION_EAR_MAPPING.add(new String[]{
					"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/src/Conversation.java", //$NON-NLS-1$ //$NON-NLS-2$
					"${" + IParameter.SEAM_PROJECT_SRC_ACTION + "}/${" + ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_PATH + "}/${" + IParameter.SEAM_LOCAL_INTERFACE_NAME +"}.java"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			ACTION_EAR_MAPPING.add(ACTION_WAR_MAPPING.get(1));
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