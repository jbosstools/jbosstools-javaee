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
package org.jboss.tools.struts.validators.model;

import org.w3c.dom.Element;

import org.jboss.tools.common.meta.*;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.loaders.impl.SimpleWebFileLoader;
import org.jboss.tools.common.model.util.*;

public class FileValidatorLoader extends SimpleWebFileLoader {

    protected XModelObjectLoaderUtil createUtil() {
        return new FVLoaderUtil();
    }

}

class FVLoaderUtil extends XModelObjectLoaderUtil {

    protected boolean isSaveable(XModelEntity entity, String n, String v, String dv) {
        if(v == null) return false;
		if(v.length() == 0 || v.equals(dv)) {
			XAttribute attr = entity.getAttribute(n);
			return (attr != null && "always".equals(attr.getProperty("save")));
		}
        return super.isSaveable(entity, n, v, dv);
    }

    protected String getChildEntity(XModelEntity entity, Element e) {
    	String n = e.getNodeName();
    	if(entity.getName().equals("ValidationField11") && n.startsWith("arg") && n.length() > 3) {
    		return "ValidationArg11";
    	}
    	return super.getChildEntity(entity, e);
    }

    public void loadAttributes(Element element, XModelObject o) {
    	super.loadAttributes(element, o);
    	if(o.getModelEntity().getName().equals("ValidationArg11")) {
    		String n = element.getNodeName();
    		if(n.startsWith("arg") && n.length() > 3) {
    			String pos = n.substring(3);
    			int p = -1;
    			try { 
    				p = Integer.parseInt(pos); 
    			} catch (Exception e) {
    				//ignore
    			}
    			if(p < 0) p = 0;
    			o.setAttributeValue("position", "" + p);
    		}    		
    	}
    }

}
