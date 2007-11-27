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
package org.jboss.tools.struts.validators.model;

import org.jboss.tools.common.model.impl.*;
import org.jboss.tools.struts.StrutsModelPlugin;

public class ValidatorArg11Impl extends RegularObjectImpl {
	private static final long serialVersionUID = 6138417845239684494L;

    public String name() {
        String n = get("name");
        String p = getModelEntity().getXMLSubPath();
        return (n == null || n.length() == 0) ? p : p + " for " + n;
    }

    public String getPathPart() {
        String n = get("name");
        String k = get("key");
        String p = get("position");
        return "" + n + ":" + k + ":" + p;
    }
    
    int MAX_AVAILABLE_POSITION = 3;

    public String getMainIconName() {
    	String pos = getAttributeValue("position");
    	int p = -1;
    	if(pos != null && pos.length() > 0) {
    		try {
    			p = Integer.parseInt(pos);
    		} catch (Exception e) {
                StrutsModelPlugin.getPluginLog().logError(e);
    		}
    	}
    	if(p < 0) p = 0; else if(p > MAX_AVAILABLE_POSITION) p = MAX_AVAILABLE_POSITION;
    	return "" + super.getMainIconName() + p;
    }

}

