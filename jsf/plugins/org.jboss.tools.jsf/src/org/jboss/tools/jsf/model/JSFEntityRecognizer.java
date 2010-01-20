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

import java.io.IOException;

import org.jboss.tools.common.model.loaders.*;
import org.jboss.tools.common.xml.XMLEntityResolver;
import org.jboss.tools.jsf.JSFModelPlugin;

public class JSFEntityRecognizer implements EntityRecognizer, JSFConstants {

    static {
        try {
            Class<?> c = JSFEntityRecognizer.class;
            XMLEntityResolver.registerPublicEntity(DOC_PUBLICID, c, "/meta/web-facesconfig_1_0.dtd"); //$NON-NLS-1$
			XMLEntityResolver.registerPublicEntity(DOC_PUBLICID_11, c, "/meta/web-facesconfig_1_1.dtd"); //$NON-NLS-1$
        } catch (IOException e) {
        	JSFModelPlugin.getPluginLog().logError(e);
        }
    }
    
    public JSFEntityRecognizer() {}

    public String getEntityName(String ext, String body) {
        if(body == null) return null;
        int i = body.indexOf("<!DOCTYPE"); //$NON-NLS-1$
        if(i >= 0) {
        	int j = body.indexOf(">", i); //$NON-NLS-1$
        	if(j < 0) return null;
        	String dt = body.substring(i, j);
        	if(dt.indexOf("faces-config") < 0) return null; //$NON-NLS-1$
        	if(dt.indexOf(DOC_PUBLICID) > 0) return ENT_FACESCONFIG;
        	if(dt.indexOf(DOC_PUBLICID_11) > 0) return ENT_FACESCONFIG_11;
        	if(dt.indexOf("SYSTEM") > 0 && dt.indexOf("web-facesconfig_1_1.dtd") > 0) return ENT_FACESCONFIG_11; //$NON-NLS-1$ //$NON-NLS-2$
        }
        String versionSuffix = getVersion(body);
        if(SUFF_12.equals(versionSuffix)) {
        	return ENT_FACESCONFIG_12;
        }
        if(SUFF_20.equals(versionSuffix)) {
        	return ENT_FACESCONFIG_20;
        }
        return null;
    }
    
    private String getVersion(String body) {
    	int i = body.indexOf("<faces-config"); //$NON-NLS-1$
    	if(i < 0) return null;
    	int j = body.indexOf(">", i); //$NON-NLS-1$
    	if(j < 0) return null;
    	String s = body.substring(i, j);
    	String uriValue = "" + '"' + JAVAEE_URI + '"';
    	if(s.indexOf(uriValue) < 0) return null;
    	if(s.indexOf("version=\"1.2\"") > 0) return SUFF_12; //$NON-NLS-1$
    	if(s.indexOf("version=\"2.0\"") > 0) return SUFF_20; //$NON-NLS-1$
    	return null;
    }

}
