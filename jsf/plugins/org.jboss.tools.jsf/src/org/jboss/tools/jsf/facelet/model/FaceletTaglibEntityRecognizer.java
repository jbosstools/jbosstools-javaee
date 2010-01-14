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
package org.jboss.tools.jsf.facelet.model;

import org.jboss.tools.common.model.loaders.EntityRecognizer;

/**
 * @author Viacheslav Kabanovich
 */
public class FaceletTaglibEntityRecognizer implements EntityRecognizer, FaceletTaglibConstants {

    public String getEntityName(String ext, String body) {
        if (body == null) return null;
        if (body.indexOf(DOC_PUBLICID) > 0) return ENT_FACELET_TAGLIB;
        if(is20(body)) return ENT_FACELET_TAGLIB_20;
        return null;
    }

    private boolean is20(String body) {
    	int i = body.indexOf("<facelet-taglib"); //$NON-NLS-1$
    	if(i < 0) return false;
    	int j = body.indexOf(">", i); //$NON-NLS-1$
    	if(j < 0) return false;
    	String s = body.substring(i, j);
    	return s.indexOf("version=\"2.0\"") > 0 && //$NON-NLS-1$
    		s.indexOf("\"http://java.sun.com/xml/ns/javaee\"") > 0; //$NON-NLS-1$
    }
}
