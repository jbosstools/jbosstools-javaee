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
package org.jboss.tools.jsf.web.helpers.context;

import java.io.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.jsf.web.JSFWebHelper;
import org.jboss.tools.jst.web.context.AdoptWebProjectContext;

public class AdoptJSFProjectContext extends AdoptWebProjectContext {

    public AdoptJSFProjectContext() {}

	protected String getWebModuleEntity() {
		return "WebJSFModule";
	}

    private String[] getModules(XModelObject web) {
        if(web == null) return new String[0];
		String ms = JSFWebHelper.getFacesConfigListAsString(web);
		return (ms == null || ms.length() == 0) ? new String[]{"/faces-config.xml"} : new String[]{ms};
    }
    
	public XModelObject[] createModulesInfo(XModelObject web, File webinf) {
        String[] ms = getModules(web);
        XModelObject[] res = new XModelObject[ms.length];
        for (int i = 0; i < ms.length; i++) {
			res[i] = createModuleInfo(web.getModel(), "", ms[0], webinf);
        }
        return res;
    }

}
