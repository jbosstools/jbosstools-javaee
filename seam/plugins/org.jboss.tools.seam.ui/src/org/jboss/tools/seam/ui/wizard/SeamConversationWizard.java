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
import org.jboss.tools.seam.core.SeamUtil;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.seam.ui.ISeamHelpContextIds;
import org.jboss.tools.seam.ui.SeamUIMessages;

public class SeamConversationWizard extends SeamBaseWizard implements INewWizard {
	
	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(pageContainer, ISeamHelpContextIds.NEW_SEAM_CONVERSATION);
	}
	
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

		private List<FileMapping> actionMapping;

		@Override
		public List<FileMapping> getFileMappings(Map<String, Object> vars) {
			// initialize war files mapping
			actionMapping = new ArrayList<FileMapping>();

			// seam-gen uses @interfaceName@ as class name since 2.0.1
			// but seam-gen 2.0.0 and lower ones use @beanName@ (looks like a bug)
			String version = SeamUtil.getSeamVersionFromManifest(getProject(info));
			String interfaceName = ISeamParameter.SEAM_BEAN_NAME;
			if(version!=null && version.compareTo("2.0.1")>=0) {
				interfaceName = ISeamParameter.SEAM_LOCAL_INTERFACE_NAME;
			}

			actionMapping.add(new FileMapping(
					"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/src/ConversationJavaBean.java", //$NON-NLS-1$ //$NON-NLS-2$
					"${" + ISeamParameter.SEAM_PROJECT_SRC_ACTION + "}/${" + ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_PATH + "}/${" + interfaceName +"}.java", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					FileMapping.TYPE.WAR,
					false));
			actionMapping.add(new FileMapping(
					"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/view/conversation.xhtml", //$NON-NLS-1$ //$NON-NLS-2$
					"${" + ISeamParameter.SEAM_PROJECT_WEBCONTENT_PATH + "}/${" + ISeamParameter.SEAM_PAGE_NAME +"}.xhtml",	 //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					FileMapping.TYPE.WAR,
					false));

			// initialize ear files mapping
			actionMapping.add(new FileMapping(
					"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/src/ConversationBean.java", //$NON-NLS-1$ //$NON-NLS-2$
					"${" + ISeamParameter.SEAM_PROJECT_SRC_ACTION + "}/${" + ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_PATH + "}/${" + ISeamParameter.SEAM_BEAN_NAME +"}.java", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					FileMapping.TYPE.EAR,
					false));
			actionMapping.add(new FileMapping(
					"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/src/Conversation.java", //$NON-NLS-1$ //$NON-NLS-2$
					"${" + ISeamParameter.SEAM_PROJECT_SRC_ACTION + "}/${" + ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_PATH + "}/${" + ISeamParameter.SEAM_LOCAL_INTERFACE_NAME +"}.java", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					FileMapping.TYPE.EAR,
					false));
			actionMapping.add(new FileMapping(
					"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/view/conversation.xhtml", //$NON-NLS-1$ //$NON-NLS-2$
					"${" + ISeamParameter.SEAM_PROJECT_WEBCONTENT_PATH + "}/${" + ISeamParameter.SEAM_PAGE_NAME +"}.xhtml",	 //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					FileMapping.TYPE.EAR,
					false));

			return actionMapping;
		}

		/*
		 * (non-Javadoc)
		 * @see org.jboss.tools.seam.ui.wizard.SeamBaseOperation#getSessionBeanPackageName(org.eclipse.core.runtime.preferences.IEclipsePreferences, java.util.Map)
		 */
		@Override
		protected String getSessionBeanPackageName(IEclipsePreferences seamFacetPrefs, Map<String, INamedElement> wizardParams) {
			return wizardParams.get(ISeamParameter.SEAM_PACKAGE_NAME).getValue().toString();
		}
	};
}