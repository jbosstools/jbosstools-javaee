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
package org.jboss.tools.jsf.model.handlers.tile;

import java.util.Properties;
import org.jboss.tools.common.meta.action.SpecialWizard;
import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.jsf.model.pv.JSFProjectTiles;
import org.jboss.tools.jst.web.model.helpers.WebAppHelper;

public class TilesFileRegistration implements SpecialWizard {
	Properties p;
	XModel model;
	XModelObject webxml;
	String oldPath;
	String path;
	boolean test;

	public void setObject(Object object) {
		p = (Properties)object;
		model = (XModel)p.get("model");
		webxml = WebAppHelper.getWebApp(model);
		path = p.getProperty("path");
		oldPath = p.getProperty("oldPath");
		test = "true".equals(p.getProperty("test"));
	}

	public int execute() {
		if(webxml == null) return 1;
		if(test) {
			return test();
		} else if(path == null && oldPath != null) {
			remove();
		} else if(path == null) {
			return 1;
		} else if(oldPath != null) {
			replace(); 
		} else {
			append();
		}
		XActionInvoker.invoke("SaveActions.Save", webxml, null);
		return 0;
	}
	
	void append() {
		XModelObject context = WebAppHelper.findWebAppContextParam(webxml, JSFProjectTiles.TILES_DEFINITIONS_2);
		if(context != null) {
			WebAppHelper.appendToWebAppContextParam(webxml, JSFProjectTiles.TILES_DEFINITIONS_2, path);
		} else {
			XModelObject servlet = WebAppHelper.findOrCreateServlet(webxml, JSFProjectTiles.TILES_SERVLET_CLASS, JSFProjectTiles.TILES_SERVLET_DEFAULT_NAME, 2);
			WebAppHelper.appendToWebAppInitParam(servlet, JSFProjectTiles.TILES_DEFINITIONS, path);
		}
	}
	
	void replace() {
		XModelObject context = WebAppHelper.findWebAppContextParam(webxml, JSFProjectTiles.TILES_DEFINITIONS_2);
		String[] s = null;
		XModelObject servlet = null;
		if(context != null) {
			s = WebAppHelper.getWebAppContextParamValueList(webxml, JSFProjectTiles.TILES_DEFINITIONS_2);
		} else {
			servlet = WebAppHelper.findOrCreateServlet(webxml, JSFProjectTiles.TILES_SERVLET_CLASS, JSFProjectTiles.TILES_SERVLET_DEFAULT_NAME, 2);
			s = WebAppHelper.getWebAppInitParamValueList(servlet, JSFProjectTiles.TILES_DEFINITIONS);
		}
		StringBuffer sb = new StringBuffer();
		boolean replaced = false;
		for (int i = 0; i < s.length; i++) {
			String add = null;
			if(s[i].equals(oldPath)) {
				add = path;
				replaced = true;
			} else {
				add = s[i];
			}
			if(add != null) {
				if(sb.length() > 0) sb.append(",");
				sb.append(add);
			}
		}
		if(!replaced && path != null) {
			if(sb.length() > 0) sb.append(",");
			sb.append(path);
		}
		if(context != null) {
			WebAppHelper.setWebAppContextParam(webxml, JSFProjectTiles.TILES_DEFINITIONS_2, sb.toString());
		} else {
			WebAppHelper.setWebAppInitParam(servlet, JSFProjectTiles.TILES_DEFINITIONS, sb.toString());
		}
		p.setProperty("success", "true");
	}
	
	int test() {
		if(path == null) return 1;
		XModelObject context = WebAppHelper.findWebAppContextParam(webxml, JSFProjectTiles.TILES_DEFINITIONS_2);
		String[] s = null;
		XModelObject servlet = null;
		if(context != null) {
			s = WebAppHelper.getWebAppContextParamValueList(webxml, JSFProjectTiles.TILES_DEFINITIONS_2);
		} else {
			servlet = WebAppHelper.findOrCreateServlet(webxml, JSFProjectTiles.TILES_SERVLET_CLASS, JSFProjectTiles.TILES_SERVLET_DEFAULT_NAME, 2);
			s = WebAppHelper.getWebAppInitParamValueList(servlet, JSFProjectTiles.TILES_DEFINITIONS);
		}
		for (int i = 0; i < s.length; i++) {
			if(s[i].equals(path)) {
				return 0;
			}
		}
		return 1;
	}
	
	void remove() {
		replace();
	}
	
}
