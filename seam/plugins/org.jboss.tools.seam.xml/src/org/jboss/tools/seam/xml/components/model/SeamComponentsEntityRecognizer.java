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

import java.io.IOException;

import org.jboss.tools.common.model.loaders.EntityRecognizer;
import org.jboss.tools.common.xml.XMLEntityResolver;
import org.jboss.tools.seam.xml.SeamXMLPlugin;

public class SeamComponentsEntityRecognizer implements EntityRecognizer, SeamComponentConstants {

    static {
        try {
            Class<?> c = SeamComponentsEntityRecognizer.class;
            XMLEntityResolver.registerPublicEntity(PUBLIC_ID_11, c, "/meta/components-1.1.dtd");
        } catch (IOException e) {
			SeamXMLPlugin.log(e);
        }
    }
    
    public SeamComponentsEntityRecognizer() {}

    public String getEntityName(String ext, String body) {
        if(body == null) return null;
    	if(body.indexOf(PUBLIC_ID_11) >= 0) {
    		return ENT_SEAM_COMPONENTS_11;
    	}
    	if(!isComponentsSchema(body)) {
    		return null;
    	}
    	
    	int i = body.indexOf("xsi:schemaLocation");
    	if(i < 0) return null;
    	int j = body.indexOf("\"", i);
    	if(j < 0) return null;
    	int k = body.indexOf("\"", j + 1);
    	if(k < 0) return null;
    	String schemaLocation = body.substring(j + 1, k);
    	boolean isSingleComponent = isSingleComponent(body);
    	
    	int i20 = schemaLocation.indexOf("2.0");
    	if(i20 >= 0) {
    		if(isSingleComponent) return ENT_SEAM_COMPONENT_20;
    		if(isMultiComponent(body)) return ENT_SEAM_COMPONENTS_20;
    	}
    	
    	int i12 = schemaLocation.indexOf("1.2");
    	if(i12 >= 0) {
    		if(isSingleComponent) return ENT_SEAM_COMPONENT_12;
    		if(isMultiComponent(body)) return ENT_SEAM_COMPONENTS_12;
    	}
        return null;
    }
    
    private boolean isComponentsSchema(String body) {
    	int i = body.indexOf("<components");
    	if(i < 0) return false;
    	int j = body.indexOf(">", i);
    	if(j < 0) return false;
    	String s = body.substring(i, j);
    	return s.indexOf("\"http://jboss.com/products/seam/components\"") > 0;
    }
    
    private boolean isMultiComponent(String body) {
    	int i = body.indexOf("<components");
    	if(i < 0) return false;
    	int j = body.indexOf(">", i);
    	if(j < 0) return false;
    	String s = body.substring(i, j);
    	return s.indexOf("\"http://jboss.com/products/seam/components\"") > 0;
    }

    private boolean isSingleComponent(String body) {
    	int i = body.indexOf("<component");
    	int is = body.indexOf("<components");
    	if(i < 0 || is >= 0) return false;
    	int j = body.indexOf(">", i);
    	if(j < 0) return false;
    	String s = body.substring(i, j);
    	return s.indexOf("\"http://jboss.com/products/seam/components\"") > 0;
    }
}
