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
package org.jboss.tools.seam.pages.xml.model;

import java.io.IOException;

import org.jboss.tools.common.model.loaders.EntityRecognizer;
import org.jboss.tools.common.model.loaders.EntityRecognizerContext;
import org.jboss.tools.common.model.loaders.XMLRecognizerContext;
import org.jboss.tools.common.xml.XMLEntityResolver;
import org.jboss.tools.seam.pages.xml.SeamPagesXMLPlugin;

public class SeamPagesEntityRecognizer implements EntityRecognizer, SeamPagesConstants {

    static {
        try {
            Class<?> c = SeamPagesEntityRecognizer.class;
            XMLEntityResolver.registerPublicEntity(PUBLIC_ID_12, c, "/meta/pages-1.2.dtd"); //$NON-NLS-1$
        } catch (IOException e) {
			SeamPagesXMLPlugin.log(e);
        }
    }
    
    public SeamPagesEntityRecognizer() {}

    public String getEntityName(EntityRecognizerContext context) {
    	String body = context.getBody();
        if(body == null) return null;
		XMLRecognizerContext xml = context.getXMLContext();
		if(xml.isDTD()) {
			String publicId = xml.getPublicId();
			String name = xml.getRootName();
			if(PUBLIC_ID_12.equals(publicId)) {
				if("page".equals(name)) return ENT_FILE_SEAM_PAGE_12;
				if("pages".equals(name)) return ENT_FILE_SEAM_PAGES_12;
			}
			return null;
		} else {
	        Parser p = new Parser(body);
	        if(!p.recognized) {
	        	return null;
	        } else if(p.is20) {
	        	return p.isSingle ? ENT_FILE_SEAM_PAGE_20 : ENT_FILE_SEAM_PAGES_20;
	        } else if(p.is21) {
	        	return p.isSingle ? ENT_FILE_SEAM_PAGE_21 : ENT_FILE_SEAM_PAGES_21;
	        } else {
	        	return p.isSingle ? ENT_FILE_SEAM_PAGE_22 : ENT_FILE_SEAM_PAGES_22;
	        }
		}
    }

    class Parser {
    	boolean recognized = false;
    	boolean isSingle = false;
    	boolean is20 = false;
    	boolean is21 = false;

    	Parser(String body) {
    		int i = body.indexOf("<page"); //$NON-NLS-1$
    		if(i < 0) return;
    		int i2 = body.indexOf("<pages"); //$NON-NLS-1$
    		if(i2 < 0) {
    			isSingle = true; 
    		}
    	    int j = body.indexOf(">", i); //$NON-NLS-1$
    	    if(j < 0) return;
    	    String s = body.substring(i, j);
    	    if(s.indexOf("\"http://jboss.com/products/seam/pages\"") < 0) { //$NON-NLS-1$
    	    	return;
    	    }
    	    if(s.indexOf("2.0") >= 0) is20 = true;
    	    if(s.indexOf("2.1") >= 0) is21 = true;
    		recognized = true;
    	}
    }
    
}
