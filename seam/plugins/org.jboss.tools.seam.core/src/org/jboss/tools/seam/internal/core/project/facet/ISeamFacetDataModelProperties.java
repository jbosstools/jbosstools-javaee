/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 

package org.jboss.tools.seam.internal.core.project.facet;

import org.eclipse.wst.common.project.facet.core.IActionConfigFactory;

/**
 * Seam facet properties collected in Seam Web Project Wizard.
 * Only properties listed below are saved in Seam WebProject Preferences:
 * 		ISeamFacetDataModelProperties.SESSION_BEAN_SOURCE_FOLDER, 
 * 		ISeamFacetDataModelProperties.SESION_BEAN_PACKAGE_NAME,
 * 		ISeamFacetDataModelProperties.ENTITY_BEAN_SOURCE_FOLDER, 
 * 		ISeamFacetDataModelProperties.ENTITY_BEAN_PACKAGE_NAME,
 *  	ISeamFacetDataModelProperties.SEAM_CONNECTION_PROFILE,
 * 		ISeamFacetDataModelProperties.JBOSS_AS_DEPLOY_AS,
 * 		ISeamFacetDataModelProperties.SEAM_TEST_PROJECT,
 * 		ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME,
 * 		ISeamFacetDataModelProperties.SEAM_SETTINGS_VERSION,
 * 		ISeamFacetDataModelProperties.TEST_SOURCE_FOLDER,
 * 		ISeamFacetDataModelProperties.TEST_CASES_PACKAGE_NAME,
 * 		ISeamFacetDataModelProperties.TEST_CREATING,
 * 		ISeamFacetDataModelProperties.SEAM_EJB_PROJECT,
 * 		ISeamFacetDataModelProperties.SEAM_EAR_PROJECT
 * @author eskimo
 */
public interface ISeamFacetDataModelProperties extends IActionConfigFactory {

	/**
	 * Seam Facet ID constant
	 */
	String SEAM_FACET_ID = "jst.seam"; //$NON-NLS-1$

	/**
	 * Seam 1.2 Facet Version constant
	 */
	String SEAM_FACET_VERSION_12 = "1.2"; //$NON-NLS-1$

	/**
	 * Seam 2.0 Facet Version constant
	 */
	String SEAM_FACET_VERSION_20 = "2.0"; //$NON-NLS-1$
	
	/**
	 * Seam 2.1 Facet Version constant
	 */
	String SEAM_FACET_VERSION_21 = "2.1"; //$NON-NLS-1$

	// Seam Preferences names constants

	/**
	 * Source folder used session objects: actions, forms, conversations, beans 
	 * web pages backing beans
	 */
	String SESSION_BEAN_SOURCE_FOLDER = "action.sources"; //$NON-NLS-1$

	/**
	 * Package name used session objects: actions, forms, conversations, beans 
	 * web pages backing beans
	 */
	String SESSION_BEAN_PACKAGE_NAME = "action.package"; //$NON-NLS-1$

	/**
	 * Source folder used entity beans: actions, forms, conversations, beans 
	 * web pages backing beans
	 */
	String ENTITY_BEAN_SOURCE_FOLDER = "model.sources"; //$NON-NLS-1$
	
	/**
	 * This flag indicates that we should create test project.
	 * Can be "true" or "false".
	 */
	String TEST_PROJECT_CREATING = "seam.test.project.creating"; //$NON-NLS-1$

	/**
	 * Package name for Entity Beans classes
	 */
	String ENTITY_BEAN_PACKAGE_NAME = "model.package"; //$NON-NLS-1$

	/**
	 * Connection profile name
	 */
	String SEAM_CONNECTION_PROFILE = "seam.project.connection.profile"; //$NON-NLS-1$

	/**
	 * Selected deployment type
	 */
	String JBOSS_AS_DEPLOY_AS = "seam.project.deployment.type"; //$NON-NLS-1$

	/**
	 * Test project name that was created for Seam Web Project
	 */
	String SEAM_TEST_PROJECT = "seam.test.project"; //$NON-NLS-1$	

	/**
	 * Seam runtime name that should be used to create seam artifacts:
	 * forms, actions, conversations and entities
	 */
	String SEAM_RUNTIME_NAME = "seam.runtime.name"; //$NON-NLS-1$

	/**
	 * Seam settings version.
	 */
	String SEAM_SETTINGS_VERSION = "seam.project.settings.version";

	/**
	 * Seam settings version 1.0.
	 */
	String SEAM_SETTINGS_VERSION_1_0 = "1.0";

	/**
	 * Seam settings version 1.1.
	 */
	String SEAM_SETTINGS_VERSION_1_1 = "1.1";

	/**
	 * Source folder where tests for seam artifacts should be placed inside the 
	 * test project
	 */
	String TEST_SOURCE_FOLDER = "test.sources"; //$NON-NLS-1$

	/**
	 * Package where tests for seam artifacts should be placed inside the 
	 * test project
	 */
	String TEST_CASES_PACKAGE_NAME = "test.package"; //$NON-NLS-1$

	/**
	 * This flag indicates that we should create seam tests.
	 * Can be "true" or "false".
	 */
	String TEST_CREATING = "seam.test.creating"; //$NON-NLS-1$

	/**
	 * Parent war project for EJB and Test projects.
	 */
	String SEAM_PARENT_PROJECT = "seam.parent.project"; //$NON-NLS-1$

	/**
	 * Ejb project name created from Seam Web Project in EAR deployment configuration
	 */
	String SEAM_EJB_PROJECT = "seam.ejb.project"; //$NON-NLS-1$

	/**
	 * Ear project name created for Seam Web Project in EAR deployment configuration
	 */
	String SEAM_EAR_PROJECT = "seam.ear.project"; //$NON-NLS-1$

	// Seam Facet Wizard Page parameters constants

	/**
	 * Project name token
	 */
	String SEAM_PROJECT_NAME = "project.name"; //$NON-NLS-1$

	/**
	 * Selected Seam Project
	 */
	String SEAM_PROJECT_INSTANCE =  "seam.project.instance"; //$NON-NLS-1$

	/**
	 * Path to JBoss AS server
	 */
	String JBOSS_AS_HOME = "jboss.home"; //$NON-NLS-1$

	/**
	 * Selected Database type
	 */
	String DB_TYPE = "database.type"; //$NON-NLS-1$

	/**
	 * Selected hibernate dialect
	 */
	String HIBERNATE_DIALECT = "hibernate.dialect"; //$NON-NLS-1$

	/**
	 * JDBC driver class name token
	 */
	String JDBC_DRIVER_CLASS_NAME = "hibernate.connection.driver_class"; //$NON-NLS-1$

	/**
	 * JDBC Connection URL token
	 */
	String JDBC_URL_FOR_DB = "hibernate.connection.url"; //$NON-NLS-1$

	/**
	 * Connection user name token
	 */
	String DB_USER_NAME = "hibernate.connection.username"; //$NON-NLS-1$

	/**
	 * Connection user name password
	 */
	String DB_USER_PASSWORD = "hibernate.connection.password"; //$NON-NLS-1$

	/**
	 * Schema name
	 */
	String DB_SCHEMA_NAME = "schema.property"; //$NON-NLS-1$

	/**
	 * If it is empty schema.property is empty
	 * If is not empty schema.property should be set to
	 * &#xa; &lt;property name=&quot;hibernate.default_schema&quot; value=&quot;${hibernate.default_schema}&quot;/&gt;  
	 *   
	 */
	String DB_DEFAULT_SCHEMA_NAME = "hibernate.default_schema"; //$NON-NLS-1$

	/**
	 * Catalog name
	 */
	String DB_CATALOG_NAME  = "catalog.property"; //$NON-NLS-1$

	/**
	 * If it is empty catalog.property is empty
	 * If is not empty catalog.property should be set to
	 * &#xa; &lt;property name=&quot;hibernate.default_catalog&quot; value=&quot;${hibernate.default_catalog}&quot;/&gt;  
	 */
	String DB_DEFAULT_CATALOG_NAME = "hibernate.default_catalog"; //$NON-NLS-1$

	/**
	 * DB tables already exists in database check box value
	 */
	String DB_ALREADY_EXISTS = "database.exists"; //$NON-NLS-1$

	/**
	 * Recreate database tables and data on deploy check box value
	 */
	String RECREATE_TABLES_AND_DATA_ON_DEPLOY = "database.drop"; //$NON-NLS-1$

	/**
	 * TODO
	 */
	String HIBERNATE_HBM2DDL_AUTO = "hibernate.hbm2ddl.auto"; //$NON-NLS-1$

	/**
	 * Driver file name 
	 */
	String JDBC_DRIVER_JAR_PATH = "driver.file"; //$NON-NLS-1$

	// TODO: should be moved to org.jboss.tools.seam.ui.wizard.IParameter
	// why is this a property when it is always derivable from sesion_bean_package_name ?
	String SESSION_BEAN_PACKAGE_PATH = "action.package.path"; //$NON-NLS-1$

	// TODO: should be moved to org.jboss.tools.seam.ui.wizard.IParameter
	// why is this a property when it is always derivable from entity_bean_package_name ?
	String ENTITY_BEAN_PACKAGE_PATH = "model.package.path"; //$NON-NLS-1$

	// TODO: should be moved to org.jboss.tools.seam.ui.wizard.IParameter
	// why is this a property when it is always derivable from test_package_path ?
	String TEST_CASES_PACKAGE_PATH = "test.package.path"; //$NON-NLS-1$

	/**
	 * Selected Seam Runtime home folder
	 */
	String JBOSS_SEAM_HOME = "seam.home.folder"; //$NON-NLS-1$

	/**
	 * Seam project web contents root folder
	 */
	String WEB_CONTENTS_FOLDER = "seam.project.web.root.folder"; //$NON-NLS-1$

	/**
	 * WAR deployment constant 
	 */
	String DEPLOY_AS_WAR = "war"; //$NON-NLS-1$

	/**
	 * EAR deployment constant
	 */
	String DEPLOY_AS_EAR = "ear";	 //$NON-NLS-1$

	/**
	 * Selected Server Runtime 
	 */
	String JBOSS_AS_TARGET_SERVER = "seam.project.deployment.target"; //$NON-NLS-1$

	/**
	 * Selected Server
	 */
	String JBOSS_AS_TARGET_RUNTIME = "seam.project.deployment.runtime"; //$NON-NLS-1$

	/**
	 * Default action source folder name;
	 */
	String DEFAULT_ACTION_SRC_FOLDER_NAME = "hot"; //$NON-NLS-1$

	/**
	 * Default model source folder name;
	 */
	String DEFAULT_MODEL_SRC_FOLDER_NAME = "main"; //$NON-NLS-1$

	String CREATE_EAR_PROJECTS = "create.ear.projects"; //$NON-NLS-1$
	
	String CONFIGURE_DEFAULT_SEAM_RUNTIME = "configure.default.seam.runtime"; //$NON-NLS-1$
	
	String CONFIGURE_WAR_PROJECT = "configure.war.project"; //$NON-NLS-1$

	String SEAM_SETTINGS_CHANGED_BY_USER = "seam.settings.changed.by.user"; //$NON-NLS-1$
}