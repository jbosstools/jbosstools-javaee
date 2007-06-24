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
package org.jboss.tools.struts.webprj.model.helpers.adopt;

import java.util.*;
import java.io.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.jst.web.context.AdoptWebProjectContext;
import org.jboss.tools.jst.web.model.helpers.WebAppHelper;
import org.jboss.tools.struts.webprj.model.helpers.sync.StrutsWebHelper;

public class AdoptProjectContext extends AdoptWebProjectContext {

    public AdoptProjectContext() {}

    public static String[][] getModules(XModelObject webxml) {
        if(webxml == null) return new String[0][];
        ArrayList<String[]> l = new ArrayList<String[]>();
		XModelObject servlet = WebAppHelper.findServlet(webxml, StrutsWebHelper.ACTION_SERVLET, "action");
        XModelObject[] init = servlet.getChildren("WebAppInitParam");
        for (int j = 0; j < init.length; j++) {
            String name = init[j].getAttributeValue("param-name");
            if(!name.startsWith("config")) continue;
            name = name.substring(6);
            String uri = init[j].getAttributeValue("param-value");
            l.add(new String[]{name, uri});
        }
        return l.toArray(new String[0][]);
    }
    
    static boolean isActionServlet(XModelObject servlet) {
    	if("org.apache.struts.action.ActionServlet".equals(servlet.getAttributeValue("servlet-class"))) {
    		return true;
    	}
    	if(getWebAppInitParamForModule(servlet, "") != null) return true;
    	return false;
    }

    public static XModelObject getWebAppInitParamForModule(XModelObject servlet, String modulename) {
    	return WebAppHelper.findWebAppInitParam(servlet, "config" + modulename);
    }

	public XModelObject[] createModulesInfo(XModelObject web, File webinf) {
        String[][] ms = getModules(web);
        XModelObject[] res = new XModelObject[ms.length];
        for (int i = 0; i < ms.length; i++) {
			res[i] = createModuleInfo(web.getModel(), ms[i][0], ms[i][1], webinf);
        }
        return res;
    }

}
