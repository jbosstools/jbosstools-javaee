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
package org.jboss.tools.struts.webprj.model.helpers.sync;

import org.jboss.tools.common.meta.action.impl.handlers.DefaultRemoveHandler;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.jst.web.model.helpers.WebAppHelper;

public class StrutsWebHelper {
    public static String ACTION_SERVLET = "org.apache.struts.action.ActionServlet";

    public static XModelObject findServlet(XModelObject webxml) {
    	return WebAppHelper.findServlet(webxml, ACTION_SERVLET, "action");
    }

    public static XModelObject getServlet(XModelObject webxml) {
    	return WebAppHelper.findOrCreateServlet(webxml, ACTION_SERVLET, "action", -1);
    }

    public static void revalidateInitParam(XModelObject servlet, String modulename, String uri) {
    	WebAppHelper.setWebAppInitParam(servlet, "config" + modulename, uri);
    }

    public static void revalidateInitParam(XModel model, String modulename, String uri) {
        XModelObject webxml = WebAppHelper.getWebApp(model);
        if(webxml == null) return;
        XModelObject servlet = getServlet(webxml);
        if(servlet != null) revalidateInitParam(servlet, modulename, uri);
    }

	public static String registerConfig(XModelObject servlet, String modulename, String uri) {
		XModelObject init = WebAppHelper.appendToWebAppInitParam(servlet, "config" + modulename, uri);
		return init == null ? "" : init.getAttributeValue("param-value");
	}

	public static String registerConfig(XModel model, String modulename, String uri) {
		XModelObject webxml = WebAppHelper.getWebApp(model);
		if(webxml == null) return null;
		XModelObject servlet = getServlet(webxml);
		return (servlet != null) ? registerConfig(servlet, modulename, uri) : null;
	}
	

	public static boolean isConfigRegistered(XModel model, String uri) {
		XModelObject webxml = WebAppHelper.getWebApp(model);
		if(webxml == null) return false;
		XModelObject servlet = WebAppHelper.findServlet(webxml, ACTION_SERVLET, "action");
		if(servlet == null) return false;
        XModelObject[] init = servlet.getChildren("WebAppInitParam");
		for (int i = 0; i < init.length; i++) {
			String n = init[i].getAttributeValue("param-name");
			if(!n.startsWith("config")) continue;
			String v = init[i].getAttributeValue("param-value");
			if(("," + v + ",").indexOf("," + uri + ",") >= 0) return true;
		}		
		return false;
	}
	
	public static void unregisterConfig(XModel model, String uri) {
		XModelObject webxml = WebAppHelper.getWebApp(model);
		XModelObject servlet = WebAppHelper.findServlet(webxml, ACTION_SERVLET, "action");
		if(servlet == null) return;
        XModelObject[] init = servlet.getChildren("WebAppInitParam");
		for (int i = 0; i < init.length; i++) {
			String n = init[i].getAttributeValue("param-name");
			if(!n.startsWith("config")) continue;
			String v = init[i].getAttributeValue("param-value");
			String vg = "," + v + ",";
			int k = vg.indexOf("," + uri + ",");
			if(k < 0) continue;
			vg = vg.substring(1, k + 1) + vg.substring(k + uri.length() + 2);
			if(vg.endsWith(",")) vg = vg.substring(0, vg.length() - 1);
			if(vg.length() == 0) {
				DefaultRemoveHandler.removeFromParent(init[i]);
			} else {
				init[i].getModel().changeObjectAttribute(init[i], "param-value", vg);
			}			
		}		
	}

}
