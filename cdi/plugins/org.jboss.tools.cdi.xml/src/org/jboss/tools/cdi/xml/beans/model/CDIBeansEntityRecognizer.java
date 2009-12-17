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

import org.jboss.tools.common.model.loaders.EntityRecognizerExtension;

public class CDIBeansEntityRecognizer implements EntityRecognizerExtension, CDIBeansConstants {

    public CDIBeansEntityRecognizer() {}

    public String getEntityName(String ext, String body) {
        if(body == null) return null;
    	if(isComponentsSchema(body)) {
    		return ENT_CDI_BEANS;
    	}    	
        return null;
    }

	public String getEntityName(String fileName, String ext, String body) {
        if(body == null) return null;
		String result = getEntityName(ext, body);
		if(result != null) {
			return result;
		}
		if("beans.xml".equals(fileName) && body.indexOf("<beans") >= 0) {
			return ENT_CDI_BEANS;
		}
		return null;
	}    

    private boolean isComponentsSchema(String body) {
    	int i = body.indexOf("<beans"); //$NON-NLS-1$
    	if(i < 0) return false;
    	int j = body.indexOf(">", i); //$NON-NLS-1$
    	if(j < 0) return false;
    	String s = body.substring(i, j);
    	return s.indexOf("\"" + BEANS_NAMESPACE + "\"") > 0; //$NON-NLS-1$
    }

}
