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

package org.jboss.tools.seam.internal.core.project.facet;

import java.util.Map;

import org.apache.tools.ant.types.FilterSet;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
/**
 * 
 * @author eskimo
 *
 */
public class SeamFacetFilterSetFactory {
	
	public static FilterSet JDBC_TEMPLATE;
	public static FilterSet PROJECT_TEMPLATE;
	public static FilterSet FILTERS_TEMPLATE;
	public static FilterSet HIBERNATE_DIALECT_TEMPLATE;
	
	static {
		JDBC_TEMPLATE = new FilterSet();
		JDBC_TEMPLATE.addFilter("jdbcUrl","${hibernate.connection.url}");
		JDBC_TEMPLATE.addFilter("driverClass","${hibernate.connection.driver_class}");
		JDBC_TEMPLATE.addFilter("username","${hibernate.connection.username}");
		JDBC_TEMPLATE.addFilter("password","${hibernate.connection.password}");
		JDBC_TEMPLATE.addFilter("catalogProperty","${catalog.property}");
		JDBC_TEMPLATE.addFilter("schemaProperty","${schema.property}");
		
		PROJECT_TEMPLATE = new FilterSet();
		PROJECT_TEMPLATE.addFilter("projectName","${project.name}");
		PROJECT_TEMPLATE.addFilter("jbossHome","${jboss.home}");
		PROJECT_TEMPLATE.addFilter("hbm2ddl","${hibernate.hbm2ddl.auto}");
		PROJECT_TEMPLATE.addFilter("driverJar","${driver.file}");
		PROJECT_TEMPLATE.addFilter("jndiPattern","${project.name}/#{ejbName}/local");
		PROJECT_TEMPLATE.addFilter("embeddedEjb","false");
		
		FILTERS_TEMPLATE = new FilterSet();
		FILTERS_TEMPLATE.addFilter("interfaceName","${interface.name}");
		FILTERS_TEMPLATE.addFilter("beanName","${bean.name}");
		FILTERS_TEMPLATE.addFilter("entityName","${entity.name}");
		FILTERS_TEMPLATE.addFilter("methodName","${method.name}");
		FILTERS_TEMPLATE.addFilter("componentName","${component.name}");
		FILTERS_TEMPLATE.addFilter("pageName","${page.name}");
		FILTERS_TEMPLATE.addFilter("masterPageName","${masterPage.name}");
		FILTERS_TEMPLATE.addFilter("actionPackage","${action.package}");
		FILTERS_TEMPLATE.addFilter("modelPackage","${model.package}");
		FILTERS_TEMPLATE.addFilter("testPackage","${test.package}");
		FILTERS_TEMPLATE.addFilter("listName","${component.name}List");
		FILTERS_TEMPLATE.addFilter("homeName","${component.name}Home");
		FILTERS_TEMPLATE.addFilter("query","${query.text}");
		
		
		HIBERNATE_DIALECT_TEMPLATE = new FilterSet();
		HIBERNATE_DIALECT_TEMPLATE.addFilter("hibernate.dialect","${hibernate.dialect}");
		
		
	}
	
	public static FilterSet createJdbcFilterSet(IDataModel values) {
		return aplayProperties((FilterSet)JDBC_TEMPLATE.clone(), values);
	}
	public static FilterSet createProjectFilterSet(IDataModel values){
		return aplayProperties((FilterSet)PROJECT_TEMPLATE.clone(), values);
	}
	
	public static FilterSet createFiltersFilterSet(IDataModel values) {
		return aplayProperties((FilterSet)FILTERS_TEMPLATE.clone(), values);
	}
	
	public static FilterSet createHibernateDialectFilterSet(IDataModel values) {
		return aplayProperties((FilterSet)HIBERNATE_DIALECT_TEMPLATE.clone(), values);
	}
	
	public static FilterSet createFiltersFilterSet(Map values) {
		return aplayProperties((FilterSet)FILTERS_TEMPLATE.clone(), values);
	}
	
	private static FilterSet aplayProperties(FilterSet template,IDataModel values) {
		FilterSet result = new FilterSet();
		for (Object filter : template.getFilterHash().keySet()) {
			String value = template.getFilterHash().get(filter).toString();
			for (Object property : values.getAllProperties()) {
				if(value.contains("${"+property.toString()+"}")) {
					value = value.replace("${"+property.toString()+"}",values.getProperty(property.toString()).toString());
				}
			}
			result.addFilter(filter.toString(), value);
		}
		return result;
	}
	
	private static FilterSet aplayProperties(FilterSet template,Map values) {
		FilterSet result = new FilterSet();
		for (Object filter : template.getFilterHash().keySet()) {
			String value = template.getFilterHash().get(filter).toString();
			for (Object property : values.keySet()){
				if(value.contains("${"+property.toString()+"}")) {
					value = value.replace("${"+property.toString()+"}",values.get(property.toString()).toString());
				}
			}
			result.addFilter(filter.toString(), value);
		}
		return result;
	}
}