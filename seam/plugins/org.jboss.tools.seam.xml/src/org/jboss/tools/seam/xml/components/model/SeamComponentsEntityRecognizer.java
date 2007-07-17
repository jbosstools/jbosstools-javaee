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

import org.eclipse.core.runtime.FileLocator;
import org.jboss.tools.common.model.loaders.*;
import org.jboss.tools.common.xml.XMLEntityResolver;
import org.jboss.tools.seam.xml.SeamXMLPlugin;

public class SeamComponentsEntityRecognizer implements EntityRecognizer, SeamComponentConstants {

    static {
        try {
            Class<?> c = SeamComponentsEntityRecognizer.class;
            XMLEntityResolver.registerPublicEntity(PUBLIC_ID_11, FileLocator.resolve(c.getResource("/meta/components-1.1.dtd")).toString());
        } catch (Exception e) {
			SeamXMLPlugin.log(e);
        }
    }
    
    public SeamComponentsEntityRecognizer() {}

    public String getEntityName(String ext, String body) {
        if(body == null) return null;
    	if(body.indexOf(PUBLIC_ID_11) >= 0) {
    		return ENT_SEAM_COMPONENTS_11;
    	}
        if(is12(body)) return ENT_SEAM_COMPONENTS_12;
        if(is12Component(body)) return ENT_SEAM_COMPONENT_12;
        return null;
    }
    
    private boolean is12(String body) {
    	int i = body.indexOf("<components");
    	if(i < 0) return false;
    	int j = body.indexOf(">", i);
    	if(j < 0) return false;
    	String s = body.substring(i, j);
    	return s.indexOf("\"http://jboss.com/products/seam/components\"") > 0;
    }

    private boolean is12Component(String body) {
    	int i = body.indexOf("<component");
    	int is = body.indexOf("<components");
    	if(i < 0 || is >= 0) return false;
    	int j = body.indexOf(">", i);
    	if(j < 0) return false;
    	String s = body.substring(i, j);
    	return s.indexOf("\"http://jboss.com/products/seam/components\"") > 0;
    }
}
