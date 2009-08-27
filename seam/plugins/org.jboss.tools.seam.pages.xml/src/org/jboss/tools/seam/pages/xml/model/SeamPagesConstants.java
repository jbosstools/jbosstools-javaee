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
package org.jboss.tools.seam.pages.xml.model;

public interface SeamPagesConstants {
	public String PUBLIC_ID_12 = "-//JBoss/Seam Pages Configuration DTD 1.2//EN"; //$NON-NLS-1$
	public String SYSTEM_ID_12 = "http://jboss.com/products/seam/pages-1.2.dtd"; //$NON-NLS-1$
	public String DOC_QUALIFIEDNAME = "pages";
	
	public String SUFF_12 = "12"; //$NON-NLS-1$
	public String SUFF_20 = "20"; //$NON-NLS-1$
	public String SUFF_21 = "21"; //$NON-NLS-1$
	public String SUFF_22 = "22"; //$NON-NLS-1$
	public String ENT_FILE_SEAM_PAGES = "FileSeamPages"; //$NON-NLS-1$
	public String ENT_FILE_SEAM_PAGES_12 = ENT_FILE_SEAM_PAGES + SUFF_12;
	public String ENT_FILE_SEAM_PAGE = "FileSeamPage"; //$NON-NLS-1$
	public String ENT_FILE_SEAM_PAGE_12 = ENT_FILE_SEAM_PAGE + SUFF_12;
	public String ENT_FILE_SEAM_PAGES_20 = ENT_FILE_SEAM_PAGES + SUFF_20;
	public String ENT_FILE_SEAM_PAGE_20 = ENT_FILE_SEAM_PAGE + SUFF_20;
	public String ENT_FILE_SEAM_PAGES_21 = ENT_FILE_SEAM_PAGES + SUFF_21;
	public String ENT_FILE_SEAM_PAGE_21 = ENT_FILE_SEAM_PAGE + SUFF_21;
	public String ENT_FILE_SEAM_PAGES_22 = ENT_FILE_SEAM_PAGES + SUFF_22;
	public String ENT_FILE_SEAM_PAGE_22 = ENT_FILE_SEAM_PAGE + SUFF_22;

	public String ENT_SEAM_PAGE = "SeamPage"; //$NON-NLS-1$
	public String ENT_SEAM_PAGE_12 = ENT_SEAM_PAGE + SUFF_12;
	public String ENT_SEAM_PAGE_20 = ENT_SEAM_PAGE + SUFF_20;
	
	public String ENT_NAVIGATION = "SeamPageNavigation"; //$NON-NLS-1$
	public String ENT_NAVIGATION_RULE = "SeamPageNavigationRule"; //$NON-NLS-1$

	public String ENT_EXCEPTION = "SeamPageException";

	public String ENT_RULE = "SeamPageRule";
	public String ENT_RULE_12 = "SeamPageRule" + SUFF_12;
	public String ENT_RULE_20 = "SeamPageRule" + SUFF_20;

	public String ENT_PARAM = "SeamPageParam";
	public String ENT_PARAM_12 = ENT_PARAM + SUFF_12;
	public String ENT_PARAM_20 = ENT_PARAM + SUFF_20;

	public String ATTR_NAME = "name"; //$NON-NLS-1$
	public String ATTR_PATH = "path"; //$NON-NLS-1$
	public String ATTR_VALUE = "value"; //$NON-NLS-1$
	public String ATTR_TYPE = "type"; //$NON-NLS-1$
	public String ATTR_TARGET = "target"; //$NON-NLS-1$
	public String ATTR_ID = "id"; //$NON-NLS-1$
	public String ATTR_SUBTYPE = "subtype"; //$NON-NLS-1$

	public String SUBTYPE_CUSTOM = "custom"; //$NON-NLS-1$

	public String ATTR_VIEW_ID = "view id"; //$NON-NLS-1$
	
	public String FOLDER_CONVERSATIONS = "Conversations"; //$NON-NLS-1$
	public String FOLDER_PAGES = "Pages"; //$NON-NLS-1$
	public String FOLDER_EXCEPTIONS = "Exceptions"; //$NON-NLS-1$

	public String ELM_DIAGRAM = "process"; //$NON-NLS-1$
	public String ENT_DIAGRAM = "SeamPagesDiagram"; //$NON-NLS-1$
	public String ENT_DIAGRAM_ITEM = "SeamPagesDiagramItem"; //$NON-NLS-1$
	public String ENT_DIAGRAM_ITEM_OUTPUT = "SeamPagesDiagramItemOutput"; //$NON-NLS-1$

    public String TYPE_PAGE = "page"; //$NON-NLS-1$
    public String TYPE_EXCEPTION = "exception"; //$NON-NLS-1$
    public String SUBTYPE_UNKNOWN = "unknown"; //$NON-NLS-1$

	//common struts & jsf constants //TODO compare to JSF, move to common	
	public static final String ENT_FILEJSP        = "FileJSP";
	public static final String ENT_FILEHTML       = "FileHTML";
	public static final String ENT_FILEXHTML       = "FileXHTML";
}
