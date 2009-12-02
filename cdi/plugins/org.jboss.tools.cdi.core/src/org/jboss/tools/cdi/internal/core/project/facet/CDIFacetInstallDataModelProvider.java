/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.internal.core.project.facet;

import org.eclipse.wst.common.componentcore.datamodel.FacetInstallDataModelProvider;

/**
 * Data model provider for CDI facet wizard page
 * 
 * @author Alexey Kazakov
 * 
 */
public class CDIFacetInstallDataModelProvider extends FacetInstallDataModelProvider implements ICDIFacetDataModelProperties {
	/**
	 * Returns default value for a given property
	 * 
	 * @param propertyName name of property which default value requested
	 * @return default value 
	 */
	public Object getDefaultProperty(String propertyName) {
		if (propertyName.equals(FACET_ID)) {
			return ICDIFacetDataModelProperties.CDI_FACET_ID;
		}
		return super.getDefaultProperty(propertyName);
	}
}