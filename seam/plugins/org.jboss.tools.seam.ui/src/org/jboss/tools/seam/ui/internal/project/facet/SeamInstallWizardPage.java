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
package org.jboss.tools.seam.ui.internal.project.facet;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jst.j2ee.project.facet.IJ2EEModuleFacetInstallDataModelProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.wst.common.frameworks.datamodel.DataModelEvent;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelListener;
import org.eclipse.wst.common.project.facet.ui.AbstractFacetWizardPage;
import org.eclipse.wst.common.project.facet.ui.IFacetWizardPage;
import org.hibernate.eclipse.console.utils.DriverClassHelpers;
import org.jboss.tools.seam.core.internal.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.seam.ui.widget.editor.IFieldEditor;
import org.jboss.tools.seam.ui.widget.editor.IFieldEditorFactory;

/**
 * @author eskimo
 *
 */
public class SeamInstallWizardPage extends AbstractFacetWizardPage implements IFacetWizardPage, IDataModelListener{
	
	/**
	 * 
	 */
	DriverClassHelpers HIBERNATE_HELPER = new DriverClassHelpers();
	
	/**
	 * 
	 */
	IDataModel model = null;
	
	/**
	 * 
	 */
	DataModelValidatorDelegate validatorDelegate;
	
	// General group
	IFieldEditor jBossAsHomeEditor = IFieldEditorFactory.INSTANCE.createBrowseFolderEditor(
			ISeamFacetDataModelProperties.JBOSS_AS_HOME, 
			"JBoss AS Home Folder:","C:\\java\\jboss-4.0.5.GA");
	IFieldEditor jBossSeamHomeEditor = IFieldEditorFactory.INSTANCE.createBrowseFolderEditor(
			ISeamFacetDataModelProperties.JBOSS_SEAM_HOME, 
			"JBoss Seam Home Folder:","C:\\java\\jboss-seam-1.2.1.GA");
	IFieldEditor jBossAsDeployAsEditor = IFieldEditorFactory.INSTANCE.createComboEditor(
			ISeamFacetDataModelProperties.JBOSS_AS_DEPLOY_AS, 
			"Deploy as:",Arrays.asList(new String[]{"war","ear"}),"war");
	
	// Database group
	IFieldEditor jBossAsDbTypeEditor = IFieldEditorFactory.INSTANCE.createComboEditor(
			ISeamFacetDataModelProperties.DB_TYPE,
			"Database Type:",Arrays.asList(HIBERNATE_HELPER.getDialectNames()),"HSQL");
	IFieldEditor jBossHibernateDialectEditor = IFieldEditorFactory.INSTANCE.createComboEditor(
			ISeamFacetDataModelProperties.HIBERNATE_DIALECT,
			"Hibernate Dialect:",Arrays.asList(HIBERNATE_HELPER.getDialectNames()),"HSQL");
	IFieldEditor jdbcDriverClassname = IFieldEditorFactory.INSTANCE.createTextEditor(
			ISeamFacetDataModelProperties.JDBC_DRIVER_CLASS_NAME,
			"JDBC Driver Class for Your Database:","com.package1.Classname");
	IFieldEditor jdbcUrlForDb = IFieldEditorFactory.INSTANCE.createTextEditor(
			ISeamFacetDataModelProperties.JDBC_URL_FOR_DB, 
			"JDBC Url for Your Database:", "url://domain/");
	IFieldEditor dbUserName = IFieldEditorFactory.INSTANCE.createTextEditor(
			ISeamFacetDataModelProperties.DB_USER_NAME, 
			"Database User Name:", "username");
	IFieldEditor dbUserPassword = IFieldEditorFactory.INSTANCE.createTextEditor(
			ISeamFacetDataModelProperties.DB_USERP_PASSWORD, 
			"User Password:", "password");
	IFieldEditor dbSchemaName = IFieldEditorFactory.INSTANCE.createTextEditor(
			ISeamFacetDataModelProperties.DB_SCHEMA_NAME, 
			"Database Schema Name:", "schema-name");
	IFieldEditor dbCatalogName = IFieldEditorFactory.INSTANCE.createTextEditor(
			ISeamFacetDataModelProperties.DB_CATALOG_NAME, 
			"Database Catalog Name:", "catalog-name");
	IFieldEditor dbTablesExists = IFieldEditorFactory.INSTANCE.createCheckboxEditor(
			ISeamFacetDataModelProperties. DB_ALREADY_EXISTS, 
			"DB Tables already exists in database:", false);
	IFieldEditor recreateTablesOnDeploy = IFieldEditorFactory.INSTANCE.createCheckboxEditor(
			ISeamFacetDataModelProperties.RECREATE_TABLES_AND_DATA_ON_DEPLOY, 
			"Recreate database tables and data on deploy:", false);
	IFieldEditor pathToJdbcDriverJar = IFieldEditorFactory.INSTANCE.createTextEditor(
			ISeamFacetDataModelProperties. JDBC_DRIVER_JAR_PATH, 
			"File :", "url://domain/");
	
	// Code generation group
	IFieldEditor sessionBeanPkgNameditor = IFieldEditorFactory.INSTANCE.createTextEditor(
			ISeamFacetDataModelProperties.SESION_BEAN_PACKAGE_NAME,
			"Session Bean Package Name:","com.mydomain.projectname.session");
	IFieldEditor entityBeanPkgNameditor = IFieldEditorFactory.INSTANCE.createTextEditor(
			ISeamFacetDataModelProperties.SESION_BEAN_PACKAGE_NAME,
			"Entity Bean Package Name:","com.mydomain.projectname.entity");
	IFieldEditor testsPkgNameditor = IFieldEditorFactory.INSTANCE.createTextEditor(
			ISeamFacetDataModelProperties.SESION_BEAN_PACKAGE_NAME,
			"Session Bean Package Name:","com.mydomain.projectname.test");
	
	/**
	 * 
	 */
	public SeamInstallWizardPage() {
		super("Seam Facet");
		setTitle("Seam Facet");
		setDescription("Seam Facest Description");
	}
	
	/**
	 * 
	 */
	private DataModelSynchronizer sync;
	
	/**
	 * 
	 */
	@Override
	public void setWizard(IWizard newWizard) {
		super.setWizard(newWizard);
	}
	
	/**
	 * 
	 */
	public void setConfig(Object config) {
		model = (IDataModel)config;
		sync = new DataModelSynchronizer(model);
		validatorDelegate = new DataModelValidatorDelegate(model,this);
		model.addListener(this);
	}
	
	/**
	 * 
	 */
	public void transferStateToConfig() {
		
	}

	/**
	 * 
	 * @param editor
	 * @param parent
	 * @param columns
	 */
	public void registerEditor(IFieldEditor editor,Composite parent,int columns) {
		sync.register(editor);
		editor.doFillIntoGrid(parent);
	}
	
	/**
	 * 
	 */
	public void createControl(Composite parent) {
		// TODO Auto-generated method stub
		initializeDialogUnits(parent);
		Composite root = new Composite(parent, SWT.NONE);
		GridData gd = new GridData();
        
        gd.horizontalSpan = 1;
        gd.horizontalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = false;

		GridLayout gridLayout = new GridLayout(1, false);
		root.setLayout(gridLayout);
		Group generalGroup = new Group(root,SWT.NONE);
		generalGroup.setLayoutData(gd);
		generalGroup.setText("General");
		gridLayout = new GridLayout(3, false);
		
		generalGroup.setLayout(gridLayout);
		registerEditor(jBossAsHomeEditor,generalGroup, 3);
		registerEditor(jBossSeamHomeEditor,generalGroup, 3);
		registerEditor(jBossAsDeployAsEditor,generalGroup, 3);
		
		gd = new GridData();
        gd.horizontalSpan = 1;
        gd.horizontalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = false;
        
		Group databaseGroup = new Group(root,SWT.NONE);
		databaseGroup.setLayoutData(gd);
		databaseGroup.setText("Database");
		gridLayout = new GridLayout(3, false);
		databaseGroup.setLayout(gridLayout);
		registerEditor(jBossAsDbTypeEditor,databaseGroup, 3);
		registerEditor(jBossHibernateDialectEditor,databaseGroup, 3);
		registerEditor(jdbcDriverClassname,databaseGroup, 3);
		registerEditor(jdbcUrlForDb,databaseGroup,3);
		registerEditor(dbUserName,databaseGroup, 3);
		registerEditor(dbUserPassword,databaseGroup, 3);
		registerEditor(dbSchemaName,databaseGroup, 3);
		registerEditor(dbCatalogName,databaseGroup, 3);
		registerEditor(dbTablesExists,databaseGroup, 3);
		registerEditor(recreateTablesOnDeploy,databaseGroup, 3);
		registerEditor(pathToJdbcDriverJar,databaseGroup, 3);
		
		Group generationGroup = new Group(root,SWT.NONE);
		generationGroup.setLayoutData(gd);
		generationGroup.setText("Code Generation");
		gridLayout = new GridLayout(3, false);
		generationGroup.setLayout(gridLayout);
		registerEditor(sessionBeanPkgNameditor,generationGroup, 3);
		registerEditor(entityBeanPkgNameditor,generationGroup, 3);
		registerEditor(testsPkgNameditor,generationGroup, 3);
			
		setControl(root);
		
		validatorDelegate.addValidatorForProperty(jBossSeamHomeEditor.getName(), ValidatorFactory.JBOSS_SEAM_HOME_FOLDER_VALIDATOR);
		validatorDelegate.addValidatorForProperty(jBossAsHomeEditor.getName(), ValidatorFactory.JBOSS_AS_HOME_FOLDER_VALIDATOR);
		
	}

	/**
	 * 
	 */
	public void propertyChanged(DataModelEvent event) {
		if(event.getPropertyName().equals(IJ2EEModuleFacetInstallDataModelProperties.CONFIG_FOLDER)) {
			model.setStringProperty(ISeamFacetDataModelProperties.WEB_CONTENTS_FOLDER, event.getProperty()
					.toString());
		}
	}
}
