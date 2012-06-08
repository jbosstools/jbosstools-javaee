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

import java.io.IOException;

import org.jboss.tools.common.model.loaders.EntityRecognizer;
import org.jboss.tools.common.model.loaders.EntityRecognizerContext;
import org.jboss.tools.common.model.loaders.XMLRecognizerContext;
import org.jboss.tools.common.xml.XMLEntityResolver;
import org.jboss.tools.seam.xml.SeamXMLPlugin;

public class SeamComponentsEntityRecognizer implements EntityRecognizer, SeamComponentConstants {

    static {
        try {
            Class<?> c = SeamComponentsEntityRecognizer.class;
            XMLEntityResolver.registerPublicEntity(PUBLIC_ID_11, c, "/meta/components-1.1.dtd"); //$NON-NLS-1$
        } catch (IOException e) {
			SeamXMLPlugin.log(e);
        }
    }
    
    public SeamComponentsEntityRecognizer() {}

    public String getEntityName(EntityRecognizerContext context) {
    	String body = context.getBody();
        if(body == null) return null;
		XMLRecognizerContext xml = context.getXMLContext();
		if(xml.isDTD()) {
			String publicId = xml.getPublicId();
			if(PUBLIC_ID_11.equals(publicId)) return ENT_SEAM_COMPONENTS_11;
			return null;
		}
    	int isSingleComponent = isSingleComponent(body);
    	if(isComponentsSchema(body) == NONE && isSingleComponent == NONE) {
    		return null;
    	}
    	int isMultiComponent = isMultiComponent(body);
    	
    	int i = body.indexOf("xsi:schemaLocation"); //$NON-NLS-1$
    	if(i < 0) return null;
    	int j = body.indexOf("\"", i); //$NON-NLS-1$
    	if(j < 0) return null;
    	int k = body.indexOf("\"", j + 1); //$NON-NLS-1$
    	if(k < 0) return null;
    	String schemaLocation = body.substring(j + 1, k);
    	
    	int i12 = schemaLocation.indexOf("1.2"); //$NON-NLS-1$
    	if(i12 >= 0) {
    		if(isSingleComponent == COM) return ENT_SEAM_COMPONENT_12;
    		if(isMultiComponent == COM) return ENT_SEAM_COMPONENTS_12;
    		return null;
    	}
    	//Let it work now for all 2.x versions
    	//If in future releases differences are essential, this should be modified
    	int i20 = schemaLocation.indexOf("-2.0"); //$NON-NLS-1$
    	int i21 = schemaLocation.indexOf("-2.1"); //$NON-NLS-1$
    	int i22 = schemaLocation.indexOf("-2.2"); //$NON-NLS-1$
    	int i23 = schemaLocation.indexOf("-2."); //$NON-NLS-1$
    	if(i21 < 0 && i20 < 0 && i22 < 0 && i23 < 0) {
    		//Try the latest known version anyway.
    		i23 = 0;
    	}
    	if(i23 >= 0) {
    		if(isSingleComponent == COM) {
    			if(i20 >= 0) {
    				return ENT_SEAM_COMPONENT_FILE_20;
    			}
    			if(i21 >= 0) {
    				return ENT_SEAM_COMPONENT_FILE_21;
    			}
    			if(i22 >= 0) {
    				return ENT_SEAM_COMPONENT_FILE_22;
    			}
    			return ENT_SEAM_COMPONENT_FILE_23;
    		}
    		if(isMultiComponent == COM) {
    			if(i20 >= 0) {
    				return ENT_SEAM_COMPONENTS_20;
    			}
    			if(i21 >= 0) {
    				return ENT_SEAM_COMPONENTS_21;
    			}
    			if(i22 >= 0) {
    				return ENT_SEAM_COMPONENTS_22;
    			}
    			return ENT_SEAM_COMPONENTS_23;
    		}
    		if(isSingleComponent == ORG && i23 >= 0) {
    			return ENT_SEAM_COMPONENT_FILE_230;
    		}
    		if(isMultiComponent == ORG && i23 >= 0) {
    			return ENT_SEAM_COMPONENTS_230;
    		}
    	}
    	
        return null;
    }

    static int NONE = 0;
    static int COM = 1;
    static int ORG = 2;

    static String COM_SCHEMA = "\"http://jboss.com/products/seam/components\""; //$NON-NLS-1$
    static String ORG_SCHEMA = "\"http://jboss.org/schema/seam/components\""; //$NON-NLS-1$
    
    private int isComponentsSchema(String body) {
    	int i = body.indexOf("<components"); //$NON-NLS-1$
    	if(i < 0) return NONE;
    	int j = body.indexOf(">", i); //$NON-NLS-1$
    	if(j < 0) return NONE;
    	String s = body.substring(i, j);
    	return s.indexOf(COM_SCHEMA) > 0 ? COM : s.indexOf(ORG_SCHEMA) > 0 ? ORG : NONE;
    }
    
    private int isMultiComponent(String body) {
    	int i = body.indexOf("<components"); //$NON-NLS-1$
    	if(i < 0) return NONE;
    	int j = body.indexOf(">", i); //$NON-NLS-1$
    	if(j < 0) return NONE;
    	String s = body.substring(i, j);
    	return s.indexOf(COM_SCHEMA) > 0 ? COM : s.indexOf(ORG_SCHEMA) > 0 ? ORG : NONE;
    }

    private int isSingleComponent(String body) {
    	int i = body.indexOf("<component"); //$NON-NLS-1$
    	int is = body.indexOf("<components"); //$NON-NLS-1$
    	if(i < 0 || is >= 0) return NONE;
    	int j = body.indexOf(">", i); //$NON-NLS-1$
    	if(j < 0) return NONE;
    	String s = body.substring(i, j);
    	return s.indexOf(COM_SCHEMA) > 0 ? COM : s.indexOf(ORG_SCHEMA) > 0 ? ORG : NONE;
    }
}
