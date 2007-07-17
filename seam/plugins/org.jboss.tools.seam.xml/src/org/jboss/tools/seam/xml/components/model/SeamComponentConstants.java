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
package org.jboss.tools.seam.xml.components.model;

public interface SeamComponentConstants {
	public String PUBLIC_ID_11 = "-//JBoss/Seam Component Configuration DTD 1.1//EN";
	public String SYSTEM_ID_11 = "http://jboss.com/products/seam/components-1.1.dtd";
	
	public String ENT_SEAM_COMPONENTS = "FileSeamComponents";
	public String ENT_SEAM_COMPONENTS_11 = ENT_SEAM_COMPONENTS + "11";
	public String ENT_SEAM_COMPONENTS_12 = ENT_SEAM_COMPONENTS + "12";
	public String ENT_SEAM_COMPONENT_12 = "FileSeamComponent" + "12";
	public String ENT_SEAM_COMPONENT = "SeamComponent";
	public String ENT_SEAM_FACTORY = "SeamFactory";
	
	public String ENT_SEAM_PROPERTY = "SeamProperty";
	public String ENT_SEAM_PROPERTY_LIST = "SeamPropertyList";
	public String ENT_SEAM_PROPERTY_MAP = "SeamPropertyMap";

	public String ENT_SEAM_LIST_ENTRY = "SeamListEntry";
	public String ENT_SEAM_MAP_ENTRY = "SeamMapEntry";
	
	public String ENT_SEAM_EVENT = "SeamEvent";
	public String ENT_SEAM_ACTION = "SeamAction";
	
	public String ATTR_NAME = "name";
	public String ATTR_KEY = "key";
	public String ATTR_VALUE = "value";
	public String ATTR_TYPE = "type";
	public String ATTR_EXPR = "expression";

}
