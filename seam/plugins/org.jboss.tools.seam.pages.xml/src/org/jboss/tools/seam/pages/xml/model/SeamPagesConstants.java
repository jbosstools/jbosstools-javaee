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
	public String ENT_FILE_SEAM_PAGES = "FileSeamPages"; //$NON-NLS-1$
	public String ENT_FILE_SEAM_PAGES_12 = ENT_FILE_SEAM_PAGES + SUFF_12;
	public String ENT_FILE_SEAM_PAGE_12 = "FileSeamPage" + SUFF_12;
	public String ENT_FILE_SEAM_PAGES_20 = ENT_FILE_SEAM_PAGES + SUFF_20;
	public String ENT_FILE_SEAM_PAGE_20 = "FileSeamPage" + SUFF_20;  //$NON-NLS-1$

	public String ENT_SEAM_PAGE = "SeamPage"; //$NON-NLS-1$
	public String ENT_SEAM_PAGE_12 = ENT_SEAM_PAGE + SUFF_12;
	public String ENT_SEAM_PAGE_20 = ENT_SEAM_PAGE + SUFF_20;
	
	public String ATTR_NAME = "name"; //$NON-NLS-1$
	public String ATTR_VALUE = "value"; //$NON-NLS-1$
	
	public String FOLDER_CONVERSATIONS = "Conversations";
	public String FOLDER_PAGES = "Pages";
	public String FOLDER_EXCEPTIONS = "Exceptions";

	public String ELM_PROCESS = "process";


    public String TYPE_PAGE = "page";
    public String TYPE_EXCEPTION = "exception";
    public String SUBTYPE_UNKNOWN = "unknown";

}
