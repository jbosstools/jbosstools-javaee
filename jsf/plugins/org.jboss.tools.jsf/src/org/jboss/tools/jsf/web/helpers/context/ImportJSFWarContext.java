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

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.jsf.project.JSFNature;
import org.jboss.tools.jsf.web.JSFWebHelper;
import org.jboss.tools.jst.web.context.AdoptWebProjectContext;
import org.jboss.tools.jst.web.context.ImportWebWarContext;
import org.jboss.tools.jst.web.model.helpers.WebAppHelper;

public class ImportJSFWarContext extends ImportWebWarContext {

	protected void createModules() {
		String ms = JSFWebHelper.getFacesConfigListAsString(webxml);
		if(ms == null || ms.length() == 0) ms = "/faces-config.xml";
		XModelObject module = createModuleInfo(webxml.getModel(), "", ms);
		modules = module == null ? new XModelObject[0] : new XModelObject[]{module};
		createAllModules();
	}

	protected void loadWebXML(String body, String location) throws Exception {
		super.loadWebXML(body, location);
		if(WebAppHelper.findServlet(webxml, "javax.faces.webapp.FacesServlet", null) == null) {
			String webXMLErrorMessage = "No JSF support found in the project."; 
			throw new Exception(webXMLErrorMessage);
		}
	}

	protected String getWebModuleEntity() {
		return "WebJSFModule"; //"JstWebModule";
	}
	
	protected AdoptWebProjectContext createAdoptContext() {
		throw new RuntimeException("Not implemented");
	}

	public String getNatureID() {
		return JSFNature.NATURE_ID;
	}

}
