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

package org.jboss.tools.seam.core.internal.project.facet;

import org.eclipse.wst.common.project.facet.core.IActionConfigFactory;

public interface ISeamFacetDataModelProperties extends IActionConfigFactory {
	
	public static final String PREFIX = "ISeamFacetDataModelProperties.";
		
	public static final String JBOSS_AS_HOME = PREFIX + "JBOSS_AS_HOME";
	
	public static final String JBOSS_AS_DEPLOY_AS = PREFIX + "JBOSS_AS_DEPLOY_AS";
	
	public static final String DB_TYPE = PREFIX + "DB_TYPE";
	
	public static final String HIBERNATE_DIALECT = PREFIX + "HIBERNATE_DIALECT";
	
	public static final String JDBC_DRIVER_CLASS_NAME = PREFIX + "JDBC_DRIVER_CLASS_NAME";
	
	public static final String JDBC_URL_FOR_DB = PREFIX + "JDBC_URL_FOR_DB";
	
	public static final String DB_USER_NAME = PREFIX + "DB_USER_NAME";
	
	public static final String DB_PASSWORD = PREFIX + "DB_PASSWORD";
	
	public static final String DB_SCHEMA_NAME = PREFIX + "DB_SCHEMA_NAME";
	
	public static final String DB_CATALOG_NAME  = PREFIX + "DB_CATALOG_NAME";
	
	public static final String DB_ALREADY_EXISTS = PREFIX + "DB_ALREADY_EXISTS";
	
	public static final String RECREATE_TABLES_AND_DATA_ON_DEPLOY = PREFIX + "RECREATE_TABLES_AND_DATA_ON_DEPLOY";
	
	public static final String JDBC_DRIVER_JAR_PATH = PREFIX + "JDBC_DRIVER_JAR_PATH";
	
	public static final String SESION_BEAN_PACKAGE_NAME = PREFIX + "SESION_BEAN_PACKAGE_NAME";
	
	public static final String ENTITY_BEAN_PACKAGE_NAME = PREFIX + "ENTITY_BEAN_PACKAGE_NAME";
	
	public static final String TEST_CASES_PACKAGE_NAME = PREFIX + "TEST_CASES_PACKAGE_NAME";

	public static final String JBOSS_SEAM_HOME = PREFIX + "JBOSS_SEAM_HOME";
	
	
	
}
