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
package org.jboss.tools.seam.ui.wizard;

import org.jboss.tools.common.ui.wizard.IParameter;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;

/**
 * @author Alexey Kazakov
 */
public interface ISeamParameter extends IParameter, ISeamFacetDataModelProperties {

	public static String SEAM_PROJECT_NAME = "seam.project.name"; //$NON-NLS-1$
	public static String SEAM_COMPONENT_NAME = "component.name"; //$NON-NLS-1$
	public static String SEAM_LOCAL_INTERFACE_NAME = "interface.name"; //$NON-NLS-1$
	public static String SEAM_BEAN_NAME = "bean.name"; //$NON-NLS-1$
	public static String SEAM_METHOD_NAME = "method.name"; //$NON-NLS-1$
	public static String SEAM_PACKAGE_NAME = "seam.package.name"; //$NON-NLS-1$
	public static String SEAM_PAGE_NAME = "page.name"; //$NON-NLS-1$
	public static String SEAM_MASTER_PAGE_NAME = "masterPage.name"; //$NON-NLS-1$
	public static String SEAM_ENTITY_CLASS_NAME = "entity.name"; //$NON-NLS-1$
	public static String SEAM_PROJECT_LOCATION_PATH = "seam.project.location"; //$NON-NLS-1$
	public static String SEAM_PROJECT_WEBCONTENT_PATH = "seam.project.webcontent"; //$NON-NLS-1$
	public static String SEAM_PROJECT_SRC_ACTION = "seam.project.action"; //$NON-NLS-1$
	public static String SEAM_PROJECT_SRC_MODEL = "seam.project.model"; //$NON-NLS-1$	
	public static String HIBERNATE_CONFIGURATION_NAME = "hibernate.configuratrion.name"; //$NON-NLS-1$

	public static String SEAM_EAR_PROJECT_LOCATION_PATH = "seam.ear.project.location"; //$NON-NLS-1$
	public static String SEAM_TEST_PROJECT_LOCATION_PATH = "seam.test.project.location"; //$NON-NLS-1$
	public static String SEAM_EJB_PROJECT_LOCATION_PATH = "seam.ejb.project.location"; //$NON-NLS-1$
}