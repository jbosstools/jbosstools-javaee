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

import org.jboss.tools.common.model.loaders.EntityRecognizer;
import org.jboss.tools.common.model.loaders.EntityRecognizerContext;

public class CDIBeansEntityRecognizer implements EntityRecognizer, CDIBeansConstants {

    public CDIBeansEntityRecognizer() {}

    public String getEntityName(EntityRecognizerContext context) {
    	String body = context.getBody();
        if(body == null) return null;
        String result = getSchemaEntityName(body);
    	if(result != null) {
    		return result;
    	}    	
		if("beans.xml".equals(context.getFileName()) && body.indexOf("<beans") >= 0) {
			return ENT_CDI_BEANS;
		}
		return null;
	}    

    private String getSchemaEntityName(String body) {
    	int i = body.indexOf("<beans"); //$NON-NLS-1$
    	if(i < 0) return null;
    	int j = body.indexOf(">", i); //$NON-NLS-1$
    	if(j < 0) return null;
    	String s = body.substring(i, j);
    	if(s.indexOf("\"" + BEANS_NAMESPACE + "\"") > 0) { //$NON-NLS-1$
    		return ENT_CDI_BEANS;
    	}
    	if(s.indexOf("\"" + BEANS_NAMESPACE_11 + "\"") > 0) { //$NON-NLS-1$
    		return ENT_CDI_BEANS_11;
    	}
    	return null;
    }

}
