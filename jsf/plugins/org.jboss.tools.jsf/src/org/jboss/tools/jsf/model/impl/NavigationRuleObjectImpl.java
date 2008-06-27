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
package org.jboss.tools.jsf.model.impl;

import org.jboss.tools.common.model.impl.OrderedObjectImpl;
import org.jboss.tools.jsf.model.JSFConstants;

public class NavigationRuleObjectImpl extends OrderedObjectImpl implements JSFConstants {
    private static final long serialVersionUID = 7691168836237996351L;
	static int lastIndex = -1;
	
	int defaultIndex = lastIndex--;

	public String getPresentationString() {
		String f = "" + getAttributeValue(ATT_FROM_VIEW_ID);
		f = "".equals(f)?JSFConstants.EMPTY_NAVIGATION_RULE_NAME:f;
		String index = "" + getAttributeValue("index");		 
		return "0".equals(index) ? f : f + " (" + index + ")";
	}

    public String getAttributeValue(String name) {
    	if("presentation".equals(name)) {
    		return getPresentationString();
    	}
    	return super.getAttributeValue(name);
    }

	public String getPathPart() {
		String path = getAttributeValue(ATT_FROM_VIEW_ID);
		if(path == null) path = "" + System.identityHashCode(this);
		return toNavigationRulePathPart(path) + ":" + getAttributeValue("index");
	}
	
	public String get(String name) {
		String v = super.get(name);
		if("index".equals(name) && (v == null || v.length() == 0)) {
			return "" + defaultIndex;
		}
		return v;
	}
	
	public static String toNavigationRulePathPart(String path) {
		return "rules:" + path.replace('/', '#');
	}
	
	public static String toFromViewId(String pathpart) {
		if(!pathpart.startsWith("rules:")) return pathpart;
		pathpart = pathpart.substring(6).replace('#', '/');
		int i = pathpart.lastIndexOf(':');
		return (i < 0) ? pathpart : pathpart.substring(0, i);
	}

}
