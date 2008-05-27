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

    public String getEntityName(String ext, String body) {
        if(body == null) return null;
    	if(body.indexOf(PUBLIC_ID_12) >= 0) {
    		if(body.indexOf("<page") > 0 && body.indexOf("<pages") < 0) return ENT_FILE_SEAM_PAGE_12;
    		return ENT_FILE_SEAM_PAGES_12;
    	}
    	if(!isPagesSchema(body)) {
    		return null;
    	}
    	
    	int i = body.indexOf("xsi:schemaLocation"); //$NON-NLS-1$
    	if(i < 0) return null;
    	int j = body.indexOf("\"", i); //$NON-NLS-1$
    	if(j < 0) return null;
    	int k = body.indexOf("\"", j + 1); //$NON-NLS-1$
    	if(k < 0) return null;
    	String schemaLocation = body.substring(j + 1, k);
    	boolean isSinglePage = isSinglePage(body);
    	
    	int i20 = schemaLocation.indexOf("2.0"); //$NON-NLS-1$
    	if(i20 >= 0) {
    		if(isSinglePage) return ENT_FILE_SEAM_PAGE_20;
    		if(isMultiPage(body)) return ENT_FILE_SEAM_PAGES_20;
    	}
    	
        return null;
    }
    
    private boolean isPagesSchema(String body) {
    	int i = body.indexOf("<pages"); //$NON-NLS-1$
    	if(i < 0) return false;
    	int j = body.indexOf(">", i); //$NON-NLS-1$
    	if(j < 0) return false;
    	String s = body.substring(i, j);
    	return s.indexOf("\"http://jboss.com/products/seam/pages\"") > 0; //$NON-NLS-1$
    }
    
    private boolean isMultiPage(String body) {
    	int i = body.indexOf("<pages"); //$NON-NLS-1$
    	if(i < 0) return false;
    	int j = body.indexOf(">", i); //$NON-NLS-1$
    	if(j < 0) return false;
    	String s = body.substring(i, j);
    	return s.indexOf("\"http://jboss.com/products/seam/pages\"") > 0; //$NON-NLS-1$
    }

    private boolean isSinglePage(String body) {
    	int i = body.indexOf("<page"); //$NON-NLS-1$
    	int is = body.indexOf("<pages"); //$NON-NLS-1$
    	if(i < 0 || is >= 0) return false;
    	int j = body.indexOf(">", i); //$NON-NLS-1$
    	if(j < 0) return false;
    	String s = body.substring(i, j);
    	return s.indexOf("\"http://jboss.com/products/seam/pages\"") > 0; //$NON-NLS-1$
    }
}
