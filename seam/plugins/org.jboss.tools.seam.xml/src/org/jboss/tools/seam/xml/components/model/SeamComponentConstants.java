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
package org.jboss.tools.seam.xml.components.model;

public interface SeamComponentConstants {
	public String PUBLIC_ID_11 = "-//JBoss/Seam Component Configuration DTD 1.1//EN"; //$NON-NLS-1$
	public String SYSTEM_ID_11 = "http://jboss.com/products/seam/components-1.1.dtd"; //$NON-NLS-1$
	
	public String SUFF_11 = "11"; //$NON-NLS-1$
	public String SUFF_12 = "12"; //$NON-NLS-1$
	public String SUFF_20 = "20"; //$NON-NLS-1$
	public String SUFF_21 = "21"; //$NON-NLS-1$
	public String SUFF_22 = "22"; //$NON-NLS-1$
	public String SUFF_23 = "23"; //$NON-NLS-1$
	public String SUFF_230 = "230"; //$NON-NLS-1$
	public String PREF_FILE_SEAM_COMPONENT = "FileSeamComponent"; //$NON-NLS-1$
	public String ENT_SEAM_COMPONENTS = "FileSeamComponents"; //$NON-NLS-1$
	public String ENT_SEAM_COMPONENTS_11 = ENT_SEAM_COMPONENTS + SUFF_11;
	public String ENT_SEAM_COMPONENTS_12 = ENT_SEAM_COMPONENTS + SUFF_12;
	public String ENT_SEAM_COMPONENTS_20 = ENT_SEAM_COMPONENTS + SUFF_20;
	public String ENT_SEAM_COMPONENTS_21 = ENT_SEAM_COMPONENTS + SUFF_21;
	public String ENT_SEAM_COMPONENTS_22 = ENT_SEAM_COMPONENTS + SUFF_22;
	public String ENT_SEAM_COMPONENTS_23 = ENT_SEAM_COMPONENTS + SUFF_23;
	public String ENT_SEAM_COMPONENTS_230 = ENT_SEAM_COMPONENTS + SUFF_230;
	public String ENT_SEAM_COMPONENT_12 = PREF_FILE_SEAM_COMPONENT + SUFF_12;
	public String ENT_SEAM_COMPONENT_FILE_20 = PREF_FILE_SEAM_COMPONENT + SUFF_20;
	public String ENT_SEAM_COMPONENT_FILE_21 = PREF_FILE_SEAM_COMPONENT + SUFF_21;
	public String ENT_SEAM_COMPONENT_FILE_22 = PREF_FILE_SEAM_COMPONENT + SUFF_22;
	public String ENT_SEAM_COMPONENT_FILE_23 = PREF_FILE_SEAM_COMPONENT + SUFF_23;
	public String ENT_SEAM_COMPONENT_FILE_230 = PREF_FILE_SEAM_COMPONENT + SUFF_230;
	public String ENT_SEAM_COMPONENT = "SeamComponent"; //$NON-NLS-1$
	public String ENT_SEAM_COMPONENT_20 = ENT_SEAM_COMPONENT + SUFF_20;
	public String ENT_SEAM_FACTORY = "SeamFactory";  //$NON-NLS-1$
	public String ENT_SEAM_FACTORY_20 = ENT_SEAM_FACTORY + SUFF_20;
	
	public String ENT_SEAM_PROPERTY = "SeamProperty"; //$NON-NLS-1$
	public String ENT_SEAM_PROPERTY_LIST = "SeamPropertyList"; //$NON-NLS-1$
	public String ENT_SEAM_PROPERTY_MAP = "SeamPropertyMap"; //$NON-NLS-1$

	public String ENT_SEAM_LIST_ENTRY = "SeamListEntry"; //$NON-NLS-1$
	public String ENT_SEAM_MAP_ENTRY = "SeamMapEntry"; //$NON-NLS-1$
	
	public String ENT_SEAM_EVENT = "SeamEvent"; //$NON-NLS-1$
	public String ENT_SEAM_ACTION = "SeamAction"; //$NON-NLS-1$
	public String ENT_SEAM_EVENT_20 = ENT_SEAM_EVENT + SUFF_20;
	public String ENT_SEAM_ACTION_20 = ENT_SEAM_ACTION + SUFF_20;

	public String ENT_SEAM_IMPORT = "SeamImport"; //$NON-NLS-1$

	public String ATTR_NAME = "name"; //$NON-NLS-1$
	public String ATTR_KEY = "key"; //$NON-NLS-1$
	public String ATTR_VALUE = "value"; //$NON-NLS-1$
	public String ATTR_TYPE = "type"; //$NON-NLS-1$
	public String ATTR_EXPR = "expression"; //$NON-NLS-1$
	public String ATTR_EXEC = "execute"; //$NON-NLS-1$
	public String ATTR_CLASS = "class"; //$NON-NLS-1$

}
