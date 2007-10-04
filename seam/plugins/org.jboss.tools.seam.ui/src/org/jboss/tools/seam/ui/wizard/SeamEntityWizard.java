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
import org.jboss.tools.seam.ui.widget.editor.INamedElement;

/**
 * @author eskimo
 *
 */
public class SeamEntityWizard extends SeamBaseWizard implements INewWizard {

	/**
	 * 
	 */
	public SeamEntityWizard() {
		super(CREATE_SEAM_ENTITY);
		setWindowTitle("New Seam Entity");
		addPage(new SeamEntityWizardPage1());
	}

	
	// TODO move operations to core plugin
	public static final IUndoableOperation CREATE_SEAM_ENTITY = new SeamEntityCreateOperation();
		/**
		 * 
		 * TODO move operations to core plugin
		 */
		public static class SeamEntityCreateOperation extends SeamBaseOperation{
			
			@Override
			protected void loadCustomVariables(Map<String, Object> vars) {
				String entityClassname = vars.get(IParameter.SEAM_ENTITY_CLASS_NAME).toString();
				String seamComponentName = entityClassname.substring(0,1).toLowerCase()+entityClassname.substring(1);
				vars.put(IParameter.SEAM_COMPONENT_NAME,seamComponentName);
			}

			/**
			 * @param label
			 */
			public SeamEntityCreateOperation() {
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
						"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/src/Entity.java",
						"${" + IParameter.SEAM_PROJECT_LOCATION_PATH + "}/src/model/${" + ISeamFacetDataModelProperties.ENTITY_BEAN_PACKAGE_PATH + "}/${" + IParameter.SEAM_ENTITY_CLASS_NAME +"}.java"});
				ACTION_WAR_MAPPING.add(new String[]{
						"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/src/EntityHome.java",
						"${" + IParameter.SEAM_PROJECT_LOCATION_PATH + "}/src/action/${" + ISeamFacetDataModelProperties.SESION_BEAN_PACKAGE_PATH + "}/${" + IParameter.SEAM_ENTITY_CLASS_NAME +"}Home.java"});
				ACTION_WAR_MAPPING.add(new String[]{
						"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/src/EntityList.java",
						"${" + IParameter.SEAM_PROJECT_LOCATION_PATH + "}/src/action/${" + ISeamFacetDataModelProperties.SESION_BEAN_PACKAGE_PATH + "}/${" + IParameter.SEAM_ENTITY_CLASS_NAME +"}List.java"});

				ACTION_WAR_MAPPING.add(new String[]{
						"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/view/edit.xhtml",
						"${" + IParameter.SEAM_PROJECT_WEBCONTENT_PATH + "}/${" + IParameter.SEAM_PAGE_NAME +"}.xhtml"});
				ACTION_WAR_MAPPING.add(new String[]{
						"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/view/list.xhtml",
						"${" + IParameter.SEAM_PROJECT_WEBCONTENT_PATH + "}/${" + IParameter.SEAM_MASTER_PAGE_NAME +"}.xhtml"});	
				
		
				ACTION_EAR_MAPPING.add(new String[]{
						"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/src/Entity.java",
						"${" + IParameter.SEAM_EJB_PROJECT_LOCATION_PATH + "}/ejbModule/${" + ISeamFacetDataModelProperties.ENTITY_BEAN_PACKAGE_PATH + "}/${" + IParameter.SEAM_ENTITY_CLASS_NAME +"}.java"});
				ACTION_EAR_MAPPING.add(new String[]{
						"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/src/EntityHome.java",
						"${" + IParameter.SEAM_EJB_PROJECT_LOCATION_PATH + "}/ejbModule/${" + ISeamFacetDataModelProperties.SESION_BEAN_PACKAGE_PATH + "}/${" + IParameter.SEAM_ENTITY_CLASS_NAME +"}Home.java"});
				ACTION_EAR_MAPPING.add(new String[]{
						"${" + ISeamFacetDataModelProperties.JBOSS_SEAM_HOME + "}/seam-gen/src/EntityList.java",
						"${" + IParameter.SEAM_EJB_PROJECT_LOCATION_PATH + "}/ejbModule/${" + ISeamFacetDataModelProperties.SESION_BEAN_PACKAGE_PATH + "}/${" + IParameter.SEAM_ENTITY_CLASS_NAME +"}List.java"});

				ACTION_EAR_MAPPING.add(ACTION_WAR_MAPPING.get(3));
				ACTION_EAR_MAPPING.add(ACTION_WAR_MAPPING.get(4));
			}
		};

}
