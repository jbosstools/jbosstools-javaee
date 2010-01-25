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
package org.jboss.tools.jsf.model;

public interface JSFConstants {
	public static final String DOC_QUALIFIEDNAME = "faces-config"; //$NON-NLS-1$
	public static final String DOC_PUBLICID = "-//Sun Microsystems, Inc.//DTD JavaServer Faces Config 1.0//EN"; //$NON-NLS-1$
	public static final String DOC_EXTDTD = "http://java.sun.com/dtd/web-facesconfig_1_0.dtd"; //$NON-NLS-1$
	public static final String DOC_PUBLICID_11 = "-//Sun Microsystems, Inc.//DTD JavaServer Faces Config 1.1//EN"; //$NON-NLS-1$
	public static final String DOC_EXTDTD_11 = "http://java.sun.com/dtd/web-facesconfig_1_1.dtd"; //$NON-NLS-1$
	
	public static final String JAVAEE_URI = "http://java.sun.com/xml/ns/javaee"; //$NON-NLS-1$

	public static final String ELM_PROCESS = "process"; //$NON-NLS-1$

	public static final String SUFF_11 = "11"; //$NON-NLS-1$
	public static final String SUFF_12 = "12"; //$NON-NLS-1$
	public static final String SUFF_20 = "20"; //$NON-NLS-1$

	public static final String ENT_FACESCONFIG  = "FacesConfig"; //$NON-NLS-1$
	public static final String ENT_FACESCONFIG_10  = ENT_FACESCONFIG;
	public static final String ENT_FACESCONFIG_11  = ENT_FACESCONFIG + SUFF_11;
	public static final String ENT_FACESCONFIG_12  = ENT_FACESCONFIG + SUFF_12;
	public static final String ENT_FACESCONFIG_20  = ENT_FACESCONFIG + SUFF_20;

	public static final String ENT_NAVIGATION_RULES = "JSFNavigationRules"; //$NON-NLS-1$
	
	public static final String ENT_NAVIGATION_RULE = "JSFNavigationRule"; //$NON-NLS-1$
	public static final String ENT_NAVIGATION_CASE = "JSFNavigationCase"; //$NON-NLS-1$
	public static final String ENT_NAVIGATION_RULE_20 = ENT_NAVIGATION_RULE + SUFF_20;
	public static final String ENT_NAVIGATION_CASE_20 = ENT_NAVIGATION_CASE + SUFF_20;
	
	public static final String ENT_PROCESS = "JSFProcess"; //$NON-NLS-1$
	public static final String ENT_PROCESS_GROUP = "JSFProcessGroup"; //$NON-NLS-1$
	public static final String ENT_PROCESS_ITEM = "JSFProcessItem"; //$NON-NLS-1$
	public static final String ENT_PROCESS_ITEM_OUTPUT = "JSFProcessItemOutput"; //$NON-NLS-1$
	
	public static final String FOLDER_NAVIGATION_RULES = "Navigation Rules";	 //$NON-NLS-1$
	public static final String FOLDER_MANAGED_BEANS = "Managed Beans"; //$NON-NLS-1$
	public static final String FOLDER_REFENCED_BEANS = "Referenced Beans"; //$NON-NLS-1$
	public static final String FOLDER_BEHAVIORS = "Behaviors"; //$NON-NLS-1$
	public static final String FOLDER_COMPONENTS = "Components"; //$NON-NLS-1$
	public static final String FOLDER_CONVERTERS = "Converters"; //$NON-NLS-1$
	public static final String FOLDER_RENDER_KITS = "Render Kits"; //$NON-NLS-1$
	public static final String FOLDER_VALIDATORS = "Validators"; //$NON-NLS-1$
	public static final String FOLDER_ORDERINGS = "Orderings"; //$NON-NLS-1$
	public static final String FOLDER_EXTENSIONS = "Extensions"; //$NON-NLS-1$
	
	public static final String ATT_ID             = "id"; //$NON-NLS-1$
	public static final String ATT_NAME           = "name"; //$NON-NLS-1$
	public static final String ATT_PATH           = "path"; //$NON-NLS-1$
	public static final String ATT_TO_VIEW_ID     = "to-view-id"; //$NON-NLS-1$
	public static final String ATT_FROM_VIEW_ID   = "from-view-id"; //$NON-NLS-1$
	public static final String ATT_FROM_ACTION    = "from-action"; //$NON-NLS-1$
	public static final String ATT_FROM_OUTCOME   = "from-outcome"; //$NON-NLS-1$
	public static final String ATT_TARGET         = "target"; //$NON-NLS-1$
	

	//common struts & jsf constants	
	public static final String ENT_FILEJSP        = "FileJSP"; //$NON-NLS-1$
	public static final String ENT_FILEHTML       = "FileHTML"; //$NON-NLS-1$
	public static final String ENT_FILEXHTML       = "FileXHTML"; //$NON-NLS-1$
	
	public static final String EMPTY_NAVIGATION_RULE_NAME = "[any]"; //$NON-NLS-1$

	public static final String FACES_SERVLET_CLASS = "javax.faces.webapp.FacesServlet"; //$NON-NLS-1$
}
