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
	public static final String DOC_QUALIFIEDNAME = "faces-config";
	public static final String DOC_PUBLICID = "-//Sun Microsystems, Inc.//DTD JavaServer Faces Config 1.0//EN";
	public static final String DOC_EXTDTD = "http://java.sun.com/dtd/web-facesconfig_1_0.dtd";
	public static final String DOC_PUBLICID_11 = "-//Sun Microsystems, Inc.//DTD JavaServer Faces Config 1.1//EN";
	public static final String DOC_EXTDTD_11 = "http://java.sun.com/dtd/web-facesconfig_1_1.dtd";

	public static final String ELM_PROCESS = "process";

	public static final String ENT_FACESCONFIG  = "FacesConfig";
	public static final String ENT_FACESCONFIG_10  = ENT_FACESCONFIG;
	public static final String ENT_FACESCONFIG_11  = ENT_FACESCONFIG + "11";
	public static final String ENT_FACESCONFIG_12  = ENT_FACESCONFIG + "12";
	public static final String ENT_NAVIGATION_RULE = "JSFNavigationRule";
	public static final String ENT_NAVIGATION_CASE = "JSFNavigationCase";
	
	public static final String ENT_PROCESS = "JSFProcess";
	public static final String ENT_PROCESS_GROUP = "JSFProcessGroup";
	public static final String ENT_PROCESS_ITEM = "JSFProcessItem";
	public static final String ENT_PROCESS_ITEM_OUTPUT = "JSFProcessItemOutput";
	
	public static final String FOLDER_NAVIGATION_RULES = "Navigation Rules";	

	public static final String ATT_ID             = "id";
	public static final String ATT_NAME           = "name";
	public static final String ATT_PATH           = "path";
	public static final String ATT_TO_VIEW_ID     = "to-view-id";
	public static final String ATT_FROM_VIEW_ID   = "from-view-id";
	public static final String ATT_FROM_ACTION    = "from-action";
	public static final String ATT_FROM_OUTCOME   = "from-outcome";
	public static final String ATT_TARGET         = "target";
	

	//common struts & jsf constants	
	public static final String ENT_FILEJSP        = "FileJSP";
	public static final String ENT_FILEHTML       = "FileHTML";
	public static final String ENT_FILEXHTML       = "FileXHTML";
	
	public static final String EMPTY_NAVIGATION_RULE_NAME = "[any]";
	
}
