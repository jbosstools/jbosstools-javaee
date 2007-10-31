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
 * Seam facet properties
 * @author eskimo
 *
 */
public interface ISeamFacetDataModelProperties extends IActionConfigFactory {
	
	public static final String SEAM_PROJECT_NAME = "project.name"; //$NON-NLS-1$
	
	public static final String SEAM_PROJECT_INSTANCE =  "seam.project.instance"; //$NON-NLS-1$
	
	public static final String SEAM_CONNECTION_PROFILE = "seam.project.connection.profile"; //$NON-NLS-1$
	
	public static final String SEAM_RUNTIME_NAME = "seam.runtime.name"; //$NON-NLS-1$
	
	public static final String JBOSS_AS_HOME = "jboss.home"; //$NON-NLS-1$
	
	public static final String JBOSS_AS_DEPLOY_AS = "seam.project.deployment.type"; //$NON-NLS-1$
	
	public static final String DB_TYPE = "database.type"; //$NON-NLS-1$
	
	public static final String HIBERNATE_DIALECT = "hibernate.dialect"; //$NON-NLS-1$
	
	public static final String JDBC_DRIVER_CLASS_NAME = "hibernate.connection.driver_class"; //$NON-NLS-1$
	
	public static final String JDBC_URL_FOR_DB = "hibernate.connection.url"; //$NON-NLS-1$
	
	public static final String DB_USER_NAME = "hibernate.connection.username"; //$NON-NLS-1$
	
	public static final String DB_USER_PASSWORD = "hibernate.connection.password"; //$NON-NLS-1$
	
	public static final String DB_SCHEMA_NAME = "schema.property"; //$NON-NLS-1$
	
	public static final String DB_CATALOG_NAME  = "catalog.property"; //$NON-NLS-1$
	
	public static final String DB_ALREADY_EXISTS = "database.exists"; //$NON-NLS-1$
	
	public static final String RECREATE_TABLES_AND_DATA_ON_DEPLOY = "database.drop"; //$NON-NLS-1$
	
	public static final String HIBERNATE_HBM2DDL_AUTO = "hibernate.hbm2ddl.auto"; //$NON-NLS-1$
	
	public static final String JDBC_DRIVER_JAR_PATH = "driver.file"; //$NON-NLS-1$
	
	public static final String SESION_BEAN_PACKAGE_NAME = "action.package"; //$NON-NLS-1$
	
	// why is this a property when it is always derivable from sesion_bean_package_name ?
	public static final String SESION_BEAN_PACKAGE_PATH = "action.package.path"; //$NON-NLS-1$
	
	public static final String ENTITY_BEAN_PACKAGE_NAME = "model.package"; //$NON-NLS-1$

	// why is this a property when it is always derivable from entity_bean_package_name ?
	public static final String ENTITY_BEAN_PACKAGE_PATH = "model.package.path"; //$NON-NLS-1$
	
	public static final String TEST_CASES_PACKAGE_NAME = "test.package"; //$NON-NLS-1$
	
	// why is this a property when it is always derivable from test_package_path ?
	public static final String TEST_CASES_PACKAGE_PATH = "test.package.path"; //$NON-NLS-1$

	public static final String JBOSS_SEAM_HOME = "seam.home.folder"; //$NON-NLS-1$
	
	public static final String WEB_CONTENTS_FOLDER = "seam.project.web.root.folder"; //$NON-NLS-1$
	
	public static final String SEAM_EJB_PROJECT = "seam.ejb.project"; //$NON-NLS-1$
	
	public static final String SEAM_TEST_PROJECT = "seam.test.project"; //$NON-NLS-1$
	
	public static final String SEAM_EAR_PROJECT = "seam.ear.project"; //$NON-NLS-1$
	
	public static final String DEPLOY_AS_WAR = "war"; //$NON-NLS-1$
	
	public static final String DEPLOY_AS_EAR = "ear";	 //$NON-NLS-1$
	
	public static final String JBOSS_AS_TARGET_SERVER = "seam.project.deployment.target"; //$NON-NLS-1$
	
	public static final String JBOSS_AS_TARGET_RUNTIME = "seam.project.deployment.runtime"; //$NON-NLS-1$
	
	
}
