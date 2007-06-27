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

import org.eclipse.core.runtime.FileLocator;
import org.jboss.tools.common.model.loaders.*;
import org.jboss.tools.common.model.util.*;
import org.jboss.tools.jsf.JSFModelPlugin;

public class JSFEntityRecognizer implements EntityRecognizer, JSFConstants {

    static {
        try {
            Class c = JSFEntityRecognizer.class;
            XMLEntityResolver.registerPublicEntity(DOC_PUBLICID, FileLocator.resolve(c.getResource("/meta/web-facesconfig_1_0.dtd")).toString());
			XMLEntityResolver.registerPublicEntity(DOC_PUBLICID_11, FileLocator.resolve(c.getResource("/meta/web-facesconfig_1_1.dtd")).toString());
        } catch (Exception e) {
        	JSFModelPlugin.getPluginLog().logError(e);
        }
    }
    
    public JSFEntityRecognizer() {}

    public String getEntityName(String ext, String body) {
        if(body == null) return null;
        if(body.indexOf(DOC_PUBLICID) > 0) return ENT_FACESCONFIG;
        if(body.indexOf(DOC_PUBLICID_11) > 0) return ENT_FACESCONFIG_11;
        if(is12(body)) return ENT_FACESCONFIG_12;
        return null;
    }
    
    private boolean is12(String body) {
    	int i = body.indexOf("<faces-config");
    	if(i < 0) return false;
    	int j = body.indexOf(">", i);
    	if(j < 0) return false;
    	String s = body.substring(i, j);
    	return s.indexOf("version=\"1.2\"") > 0 &&
    		s.indexOf("\"http://java.sun.com/xml/ns/javaee\"") > 0;
    }

}
