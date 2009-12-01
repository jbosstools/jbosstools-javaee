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
import org.jboss.tools.seam.core.SeamProjectsSet;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.seam.ui.ISeamHelpContextIds;
import org.jboss.tools.seam.ui.SeamUIMessages;

/**
 * @author eskimo
 *
 */
public class SeamEntityWizard extends SeamBaseWizard implements INewWizard {

	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(pageContainer, ISeamHelpContextIds.NEW_SEAM_ENTITY);
	}

	/**
	 * 
	 */
	public SeamEntityWizard() {
		super(CREATE_SEAM_ENTITY);
		setWindowTitle(SeamUIMessages.SEAM_ENTITY_WIZARD_NEW_SEAM_ENTITY);
		setDefaultPageImageDescriptor(ImageDescriptor.createFromFile(SeamEntityWizard.class, "SeamWebProjectWizBan.png"));
	}

	@Override
	public void addPages() {
		addPage(new SeamEntityWizardPage1(getInitialSelection()));
	}

	// TODO move operations to core plugin
	public static final IUndoableOperation CREATE_SEAM_ENTITY = new SeamEntityCreateOperation();
		/**
		 * 
		 * TODO move operations to core plugin
		 */
		public static class SeamEntityCreateOperation extends SeamBaseOperation{

			@Override
			protected boolean shouldTouchServer(SeamProjectsSet seamPrjSet) {
				return true;
			}

			@Override
			protected void loadCustomVariables(Map<String, Object> vars) {
				String entityClassname = vars.get(ISeamParameter.SEAM_ENTITY_CLASS_NAME).toString();
				String seamComponentName = entityClassname.substring(0,1).toLowerCase()+entityClassname.substring(1);
				vars.put(ISeamParameter.SEAM_COMPONENT_NAME,seamComponentName);
			}

			/**
			 * @param label
			 */
			public SeamEntityCreateOperation() {
				super((SeamUIMessages.SEAM_ENTITY_WIZARD_ENTITY_CREATING_OPERATION));
			}

			@Override
			public List<FileMapping> getFileMappings(Map<String, Object> vars) {
				return ACTION_MAPPING;
			}

			public static final List<FileMapping> ACTION_MAPPING = new ArrayList<FileMapping>();

			static {
				// initialize war files mapping
				ACTION_MAPPING.add(new FileMapping(
						"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/view/edit.xhtml", //$NON-NLS-1$ //$NON-NLS-2$
						"${" + ISeamParameter.SEAM_PROJECT_WEBCONTENT_PATH + "}/${" + ISeamParameter.SEAM_PAGE_NAME +"}.xhtml", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						FileMapping.TYPE.WAR,
						false));
				ACTION_MAPPING.add(new FileMapping(
						"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/view/list.xhtml", //$NON-NLS-1$ //$NON-NLS-2$
						"${" + ISeamParameter.SEAM_PROJECT_WEBCONTENT_PATH + "}/${" + ISeamParameter.SEAM_MASTER_PAGE_NAME +"}.xhtml",	 //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						FileMapping.TYPE.WAR,
						false));
				ACTION_MAPPING.add(new FileMapping(
						"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/src/Entity.java", //$NON-NLS-1$ //$NON-NLS-2$
						"${" + ISeamParameter.SEAM_PROJECT_SRC_MODEL + "}/${" + ISeamFacetDataModelProperties.ENTITY_BEAN_PACKAGE_PATH + "}/${" + ISeamParameter.SEAM_ENTITY_CLASS_NAME +"}.java", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						FileMapping.TYPE.WAR,
						false));
				ACTION_MAPPING.add(new FileMapping(
						"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/src/EntityHome.java", //$NON-NLS-1$ //$NON-NLS-2$
						"${" + ISeamParameter.SEAM_PROJECT_SRC_ACTION + "}/${" + ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_PATH + "}/${" + ISeamParameter.SEAM_ENTITY_CLASS_NAME +"}Home.java", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						FileMapping.TYPE.WAR,
						false));
				ACTION_MAPPING.add(new FileMapping(
						"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/src/EntityList.java", //$NON-NLS-1$ //$NON-NLS-2$
						"${" + ISeamParameter.SEAM_PROJECT_SRC_ACTION + "}/${" + ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_PATH + "}/${" + ISeamParameter.SEAM_ENTITY_CLASS_NAME +"}List.java", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						FileMapping.TYPE.WAR,
						false));
				// initialize ear files mapping
				ACTION_MAPPING.add(new FileMapping(
						"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/view/edit.xhtml", //$NON-NLS-1$ //$NON-NLS-2$
						"${" + ISeamParameter.SEAM_PROJECT_WEBCONTENT_PATH + "}/${" + ISeamParameter.SEAM_PAGE_NAME +"}.xhtml", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						FileMapping.TYPE.EAR,
						false));
				ACTION_MAPPING.add(new FileMapping(
						"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/view/list.xhtml", //$NON-NLS-1$ //$NON-NLS-2$
						"${" + ISeamParameter.SEAM_PROJECT_WEBCONTENT_PATH + "}/${" + ISeamParameter.SEAM_MASTER_PAGE_NAME +"}.xhtml",	 //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						FileMapping.TYPE.EAR,
						false));
				ACTION_MAPPING.add(new FileMapping(
						"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/src/Entity.java", //$NON-NLS-1$ //$NON-NLS-2$
						"${" + ISeamParameter.SEAM_PROJECT_SRC_MODEL + "}/${" + ISeamFacetDataModelProperties.ENTITY_BEAN_PACKAGE_PATH + "}/${" + ISeamParameter.SEAM_ENTITY_CLASS_NAME +"}.java", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						FileMapping.TYPE.EAR,
						false));
				ACTION_MAPPING.add(new FileMapping(
						"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/src/EntityHome.java", //$NON-NLS-1$ //$NON-NLS-2$
						"${" + ISeamParameter.SEAM_PROJECT_SRC_ACTION + "}/${" + ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_PATH + "}/${" + ISeamParameter.SEAM_ENTITY_CLASS_NAME +"}Home.java", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						FileMapping.TYPE.EAR,
						false));
				ACTION_MAPPING.add(new FileMapping(
						"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/src/EntityList.java", //$NON-NLS-1$ //$NON-NLS-2$
						"${" + ISeamParameter.SEAM_PROJECT_SRC_ACTION + "}/${" + ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_PATH + "}/${" + ISeamParameter.SEAM_ENTITY_CLASS_NAME +"}List.java", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						FileMapping.TYPE.EAR,
						false));
			}

			/*
			 * (non-Javadoc)
			 * @see org.jboss.tools.seam.ui.wizard.SeamBaseOperation#getEntityBeanPackageName(org.eclipse.core.runtime.preferences.IEclipsePreferences, java.util.Map)
			 */
			@Override
			protected String getEntityBeanPackageName(IEclipsePreferences seamFacetPrefs, Map<String, INamedElement> wizardParams) {
				return wizardParams.get(ISeamParameter.SEAM_PACKAGE_NAME).getValue().toString();
			}
		};
}