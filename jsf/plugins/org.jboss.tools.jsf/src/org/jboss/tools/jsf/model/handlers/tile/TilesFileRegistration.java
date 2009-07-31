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
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.plugin.ModelPlugin;
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
		model = (XModel)p.get("model"); //$NON-NLS-1$
		webxml = WebAppHelper.getWebApp(model);
		path = p.getProperty("path"); //$NON-NLS-1$
		oldPath = p.getProperty("oldPath"); //$NON-NLS-1$
		test = "true".equals(p.getProperty("test")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public int execute() {
		if(webxml == null) return 1;
		try {
			return doExecute();
		} catch (XModelException e) {
			ModelPlugin.getPluginLog().logError(e);
			if(p != null) p.put("exception", e); //$NON-NLS-1$
			return 1;
		}
	}
	
	private int doExecute() throws XModelException {
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
		XActionInvoker.invoke("SaveActions.Save", webxml, null); //$NON-NLS-1$
		return 0;
	}
	
	void append() throws XModelException {
		XModelObject context = WebAppHelper.findWebAppContextParam(webxml, JSFProjectTiles.TILES_DEFINITIONS_2);
		if(context != null) {
			WebAppHelper.appendToWebAppContextParam(webxml, JSFProjectTiles.TILES_DEFINITIONS_2, path);
		} else {
			XModelObject servlet = WebAppHelper.findOrCreateServlet(webxml, JSFProjectTiles.TILES_SERVLET_CLASS, JSFProjectTiles.TILES_SERVLET_DEFAULT_NAME, 2);
			WebAppHelper.appendToWebAppInitParam(servlet, JSFProjectTiles.TILES_DEFINITIONS, path);
		}
	}
	
	void replace() throws XModelException {
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
				if(sb.length() > 0) sb.append(","); //$NON-NLS-1$
				sb.append(add);
			}
		}
		if(!replaced && path != null) {
			if(sb.length() > 0) sb.append(","); //$NON-NLS-1$
			sb.append(path);
		}
		if(context != null) {
			WebAppHelper.setWebAppContextParam(webxml, JSFProjectTiles.TILES_DEFINITIONS_2, sb.toString());
		} else {
			WebAppHelper.setWebAppInitParam(servlet, JSFProjectTiles.TILES_DEFINITIONS, sb.toString());
		}
		p.setProperty("success", "true"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	int test() throws XModelException {
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
	
	void remove() throws XModelException {
		replace();
	}
	
}
