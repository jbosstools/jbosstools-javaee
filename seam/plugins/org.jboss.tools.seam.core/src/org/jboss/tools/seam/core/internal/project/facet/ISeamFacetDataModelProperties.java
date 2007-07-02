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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.wst.common.project.facet.core.IActionConfigFactory;

public interface ISeamFacetDataModelProperties extends IActionConfigFactory {
	
	public static final String PREFIX = "ISeamFacetDataModelProperties.";
		
	public static final String JBOSS_AS_HOME = "jboss.home";
	
	public static final String JBOSS_AS_DEPLOY_AS = "JBOSS_AS_DEPLOY_AS";
	
	public static final String DB_TYPE = "database.type";
	
	public static final String HIBERNATE_DIALECT = "hibernate.dialect";
	
	public static final String JDBC_DRIVER_CLASS_NAME = "hibernate.connection.driver_class";
	
	public static final String JDBC_URL_FOR_DB = "hibernate.connection.url";
	
	public static final String DB_USER_NAME = "hibernate.connection.username";
	
	public static final String DB_USERP_PASSWORD = "hibernate.connection.password";
	
	public static final String DB_SCHEMA_NAME = "schema.property";
	
	public static final String DB_CATALOG_NAME  = "catalog.property";
	
	public static final String DB_ALREADY_EXISTS = "database.exists";
	
	public static final String RECREATE_TABLES_AND_DATA_ON_DEPLOY = "database.drop";
	
	public static final String JDBC_DRIVER_JAR_PATH = "driver.file";
	
	public static final String SESION_BEAN_PACKAGE_NAME = "action.package";
	
	public static final String ENTITY_BEAN_PACKAGE_NAME = "model.package";
	
	public static final String TEST_CASES_PACKAGE_NAME = "test.package";

	public static final String JBOSS_SEAM_HOME = "JBOSS_SEAM_HOME";
	
	public static final String WEB_CONTENTS_FOLDER = PREFIX + "WEB_CONTENTS_FOLDER";
	
}
