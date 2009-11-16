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
		JDBC_TEMPLATE.addFilter("jdbcUrl","${hibernate.connection.url}"); //$NON-NLS-1$ //$NON-NLS-2$
		JDBC_TEMPLATE.addFilter("driverClass","${hibernate.connection.driver_class}"); //$NON-NLS-1$ //$NON-NLS-2$
		JDBC_TEMPLATE.addFilter("username","${hibernate.connection.username}"); //$NON-NLS-1$ //$NON-NLS-2$
		JDBC_TEMPLATE.addFilter("password","${hibernate.connection.password}"); //$NON-NLS-1$ //$NON-NLS-2$
		JDBC_TEMPLATE.addFilter("catalogProperty","${catalog.property}"); //$NON-NLS-1$ //$NON-NLS-2$
		JDBC_TEMPLATE.addFilter("schemaProperty","${schema.property}"); //$NON-NLS-1$ //$NON-NLS-2$
		
		PROJECT_TEMPLATE = new FilterSet();
		PROJECT_TEMPLATE.addFilter("projectName","${project.name}"); //$NON-NLS-1$ //$NON-NLS-2$
		PROJECT_TEMPLATE.addFilter("jbossHome","${jboss.home}"); //$NON-NLS-1$ //$NON-NLS-2$
		PROJECT_TEMPLATE.addFilter("hbm2ddl","${hibernate.hbm2ddl.auto}"); //$NON-NLS-1$ //$NON-NLS-2$
		PROJECT_TEMPLATE.addFilter("driverJar","${driver.file}"); //$NON-NLS-1$ //$NON-NLS-2$
		PROJECT_TEMPLATE.addFilter("debug","true");		 //$NON-NLS-1$ //$NON-NLS-2$
		//todo: keep this local for seam2 ?
		PROJECT_TEMPLATE.addFilter("skin","blueSky"); //$NON-NLS-1$ //$NON-NLS-2$
		PROJECT_TEMPLATE.addFilter("connectionProfile","${seam.project.connection.profile}"); //$NON-NLS-1$ //$NON-NLS-2$
		
		FILTERS_TEMPLATE = new FilterSet();
		FILTERS_TEMPLATE.addFilter("interfaceName","${interface.name}"); //$NON-NLS-1$ //$NON-NLS-2$
		FILTERS_TEMPLATE.addFilter("beanName","${bean.name}"); //$NON-NLS-1$ //$NON-NLS-2$
		FILTERS_TEMPLATE.addFilter("entityName","${entity.name}"); //$NON-NLS-1$ //$NON-NLS-2$
		FILTERS_TEMPLATE.addFilter("methodName","${method.name}"); //$NON-NLS-1$ //$NON-NLS-2$
		FILTERS_TEMPLATE.addFilter("componentName","${component.name}"); //$NON-NLS-1$ //$NON-NLS-2$
		FILTERS_TEMPLATE.addFilter("pageName","${page.name}"); //$NON-NLS-1$ //$NON-NLS-2$
		FILTERS_TEMPLATE.addFilter("masterPageName","${masterPage.name}"); //$NON-NLS-1$ //$NON-NLS-2$
		FILTERS_TEMPLATE.addFilter("actionPackage","${action.package}"); //$NON-NLS-1$ //$NON-NLS-2$
		FILTERS_TEMPLATE.addFilter("modelPackage","${model.package}"); //$NON-NLS-1$ //$NON-NLS-2$
		FILTERS_TEMPLATE.addFilter("testPackage","${test.package}"); //$NON-NLS-1$ //$NON-NLS-2$
		FILTERS_TEMPLATE.addFilter("listName","${component.name}List"); //$NON-NLS-1$ //$NON-NLS-2$
		FILTERS_TEMPLATE.addFilter("homeName","${component.name}Home"); //$NON-NLS-1$ //$NON-NLS-2$
		FILTERS_TEMPLATE.addFilter("query","${query.text}"); //$NON-NLS-1$ //$NON-NLS-2$

		HIBERNATE_DIALECT_TEMPLATE = new FilterSet();
		HIBERNATE_DIALECT_TEMPLATE.addFilter("hibernate.dialect","${hibernate.dialect}"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public static FilterSet createJdbcFilterSet(IDataModel values, boolean encodeValuesForPropertyFiles) {
		return applyProperties((FilterSet)JDBC_TEMPLATE.clone(), values, encodeValuesForPropertyFiles);
	}

	public static FilterSet createProjectFilterSet(IDataModel values, boolean encodeValuesForPropertyFiles){
		return applyProperties((FilterSet)PROJECT_TEMPLATE.clone(), values, encodeValuesForPropertyFiles);
	}

	public static FilterSet createFiltersFilterSet(IDataModel values, boolean encodeValuesForPropertyFiles) {
		return applyProperties((FilterSet)FILTERS_TEMPLATE.clone(), values, encodeValuesForPropertyFiles);
	}

	public static FilterSet createHibernateDialectFilterSet(IDataModel values, boolean encodeValuesForPropertyFiles) {
		return applyProperties((FilterSet)HIBERNATE_DIALECT_TEMPLATE.clone(), values, encodeValuesForPropertyFiles);
	}

	public static FilterSet createFiltersFilterSet(Map values, boolean encodeValuesForPropertyFiles) {
		return applyProperties((FilterSet)FILTERS_TEMPLATE.clone(), values, false);
	}

	public static FilterSet createJdbcFilterSet(IDataModel values) {
		return createJdbcFilterSet(values, false);
	}

	public static FilterSet createProjectFilterSet(IDataModel values){
		return createProjectFilterSet(values, false);
	}

	public static FilterSet createFiltersFilterSet(IDataModel values) {
		return createFiltersFilterSet(values, false);
	}

	public static FilterSet createHibernateDialectFilterSet(IDataModel values) {
		return createHibernateDialectFilterSet(values, false);
	}

	public static FilterSet createFiltersFilterSet(Map values) {
		return createFiltersFilterSet(values, false);
	}

	private static FilterSet applyProperties(FilterSet template, IDataModel values, boolean encodeValuesForPropertyFiles) {
		FilterSet result = new FilterSet();
		for (Object filter : template.getFilterHash().keySet()) {
			String value = template.getFilterHash().get(filter).toString();
			for (Object property : values.getAllProperties()) {
				if(value.contains("${"+property.toString()+"}")) { //$NON-NLS-1$ //$NON-NLS-2$
					Object propertyValue = values.getProperty(property.toString());
					if(encodeValuesForPropertyFiles && propertyValue!=null) {
						propertyValue = propertyValue.toString().replace("\\", "\\\\");
					}
					value = value.replace("${"+property.toString()+"}",propertyValue==null?"":propertyValue.toString()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
			}
			result.addFilter(filter.toString(), value);
		}
		return result;
	}

	private static FilterSet applyProperties(FilterSet template, Map values, boolean encodeValuesForPropertyFiles) {
		FilterSet result = new FilterSet();
		for (Object filter : template.getFilterHash().keySet()) {
			String value = template.getFilterHash().get(filter).toString();
			for (Object property : values.keySet()){
				if(value.contains("${"+property.toString()+"}")) { //$NON-NLS-1$ //$NON-NLS-2$
					Object propertyValue = values.get(property.toString());
					if(encodeValuesForPropertyFiles && propertyValue!=null) {
						propertyValue = propertyValue.toString().replace("\\", "\\\\");
					}
					value = value.replace("${"+property.toString()+"}",propertyValue==null?"":propertyValue.toString()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
			}
			result.addFilter(filter.toString(), value);
		}
		return result;
	}
}