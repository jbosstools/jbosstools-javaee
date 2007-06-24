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
package org.jboss.tools.struts.model.handlers;

import java.util.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.common.meta.action.impl.handlers.*;
import org.jboss.tools.jst.web.model.helpers.WebAppHelper;
import org.jboss.tools.jst.web.project.WebModuleConstants;
import org.jboss.tools.jst.web.project.WebProject;
import org.jboss.tools.struts.webprj.model.helpers.WebModulesHelper;
import org.jboss.tools.struts.webprj.model.helpers.sync.StrutsWebHelper;

public class RenameStrutsConfigHandler extends DefaultEditHandler {
	
	public void executeHandler(XModelObject object, Properties prop) throws Exception {
		WebModulesHelper wh = WebModulesHelper.getInstance(object.getModel());
		String module = "" + wh.getModuleForConfig(object);
		XModelObject m = object.getModel().getByPath("Web/" + module.replace('/', '#'));
		String path = XModelObjectLoaderUtil.getResourcePath(object);
		XModelObject[] cgs = m.getChildren();
		XModelObject cg = null;
		for (int i = 0; i < cgs.length; i++) {
			if(path.equals(cgs[i].getAttributeValue(WebModuleConstants.ATTR_MODEL_PATH))) cg = cgs[i]; 
		}
		if(m != null && !path.equals(m.getAttributeValue(WebModuleConstants.ATTR_MODEL_PATH))) m = null;
		String oldURI = WebProject.getInstance(object.getModel()).getPathInWebRoot(object);
		if(oldURI == null) oldURI = "/WEB-INF" + XModelObjectLoaderUtil.getResourcePath(object);
///		if(XModelImpl.getByRelativePath(object.getModel(), oldURI) != object) oldURI = null;
		super.executeHandler(object, prop);
		String resourcePath = XModelObjectLoaderUtil.getResourcePath(object);
		String newURI = WebProject.getInstance(object.getModel()).getPathInWebRoot(object);
		if(newURI == null) newURI = "/WEB-INF" + resourcePath;
		boolean meq = (m != null && m.getAttributeValue("URI").equals(oldURI));
		boolean ceq = (cg != null && cg.getAttributeValue("URI").equals(oldURI));
		boolean replaceInWebXML = meq || ceq;
		if(m != null) m.getModel().changeObjectAttribute(m, WebModuleConstants.ATTR_MODEL_PATH, resourcePath);
		if(cg != null) cg.getModel().changeObjectAttribute(cg, WebModuleConstants.ATTR_MODEL_PATH, resourcePath);
		XActionInvoker.invoke("SaveActions.Save", object, prop);
		object.getModel().update();
		if(replaceInWebXML) {
			if(meq) m.getModel().changeObjectAttribute(m, WebModuleConstants.ATTR_URI, newURI);
			if(ceq) cg.getModel().changeObjectAttribute(cg, WebModuleConstants.ATTR_URI, newURI);
			renameConfigInWebXML(object.getModel(), module, oldURI, newURI);
		}
	}
	
	private void renameConfigInWebXML(XModel model, String module, String oldURI, String newURI) {
		XModelObject webxml = WebAppHelper.getWebApp(model);
		if(webxml == null) return;
		XModelObject servlet = StrutsWebHelper.findServlet(webxml);
		String[] s = WebAppHelper.getWebAppInitParamValueList(servlet, "config" + module);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length; i++) {
			if(sb.length() > 0) sb.append(",");
			if(s[i].equals(oldURI)) sb.append(newURI); else sb.append(s[i]);
		}
		WebAppHelper.setWebAppInitParam(servlet, "config" + module, sb.toString());
		if(webxml.isModified()) {
			XActionInvoker.invoke("SaveActions.Save", webxml, new Properties());
		}
	}
	
}
