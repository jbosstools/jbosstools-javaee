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
package org.jboss.tools.cdi.xml.beans.model;

import org.jboss.tools.common.meta.XAttribute;
import org.jboss.tools.common.meta.XModelEntity;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.w3c.dom.Element;

public class CDIBeansLoaderUtil extends XModelObjectLoaderUtil implements CDIBeansConstants {
	
	public CDIBeansLoaderUtil() {}

	protected boolean isSaveable(XModelEntity entity, String n, String v, String dv) {
		if(v == null) return false;
		if(v.length() == 0 || v.equals(dv)) {
			XAttribute attr = entity.getAttribute(n);
			return (attr != null && "always".equals(attr.getProperty("save"))); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return super.isSaveable(entity, n, v, dv);
	}


    public boolean save(Element parent, XModelObject o) {
    	if(!needToSave(o)) return true;
    	return super.save(parent, o);
    }

    protected boolean needToSave(XModelObject o) {
    	String s = o.getModelEntity().getProperty("saveDefault"); //$NON-NLS-1$
    	if(!"false".equals(s)) return true; //$NON-NLS-1$
    	if(hasSetAttributes(o)) return true;
    	if(o.getChildren().length > 0) return true;
    	return false;
    }

    private boolean hasSetAttributes(XModelObject o) {
    	XAttribute[] as = o.getModelEntity().getAttributes();
    	for (int i = 0; i < as.length; i++) {
    		String xml = as[i].getXMLName();
    		// it would be more safe to check isSavable
    		if(xml == null || xml.length() == 0 || "NAME".equals(xml)) continue; //$NON-NLS-1$
    		String v = o.getAttributeValue(as[i].getName());
    		if(v != null && v.length() > 0) return true;
    	}
    	String finalComment = o.get("#final-comment"); //$NON-NLS-1$
    	if(finalComment != null && finalComment.length() > 0) return true;
    	return false;
    }

    public void loadAttributes(Element element, XModelObject o) {
    	super.loadAttributes(element, o);
    	String entity = o.getModelEntity().getName();
    	if("CDIWeldInclude".equals(entity) || "CDIWeldExclude".equals(entity)) {
    		String namePattern = "";
    		String name = element.getAttribute("name");
    		if(name != null && name.length() > 0) {
    			namePattern = name;
    			o.setAttributeValue("is regular expression", "false");
    		}
    		String pattern = element.getAttribute("pattern");
    		if(pattern != null && pattern.length() > 0) {
    			namePattern = pattern;
    			o.setAttributeValue("is regular expression", "true");
    		}
    		o.setAttributeValue("name", namePattern);
    	}
    }
   
    public void saveAttribute(Element element, String xmlname, String value) {
        int i = xmlname.indexOf('|');
        if(i >= 0) return;
        super.saveAttribute(element, xmlname, value);
    }

    public void saveAttributes(Element element, XModelObject o) {
    	super.saveAttributes(element, o);
    	String entity = o.getModelEntity().getName();
    	if("CDIWeldInclude".equals(entity) || "CDIWeldExclude".equals(entity)) {
    		boolean isRegEx = "true".equals(o.getAttributeValue("is regular expression"));
    		String attr = isRegEx ? "pattern" : "name";
    		String name = o.getAttributeValue("name");
    		element.setAttribute(attr, name);
    	}
    }
   
}
