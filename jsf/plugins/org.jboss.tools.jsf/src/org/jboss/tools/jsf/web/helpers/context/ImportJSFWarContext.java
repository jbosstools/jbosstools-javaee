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

import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.jsf.messages.JSFUIMessages;
import org.jboss.tools.jsf.model.JSFConstants;
import org.jboss.tools.jsf.project.JSFNature;
import org.jboss.tools.jsf.web.JSFWebHelper;
import org.jboss.tools.jst.web.context.AdoptWebProjectContext;
import org.jboss.tools.jst.web.context.ImportWebWarContext;
import org.jboss.tools.jst.web.model.helpers.WebAppHelper;

public class ImportJSFWarContext extends ImportWebWarContext {

	protected void createModules() {
		String ms = JSFWebHelper.getFacesConfigListAsString(webxml);
		if(ms == null || ms.length() == 0) ms = "/faces-config.xml"; //$NON-NLS-1$
		XModelObject module = createModuleInfo(webxml.getModel(), "", ms); //$NON-NLS-1$
		modules = module == null ? new XModelObject[0] : new XModelObject[]{module};
		createAllModules();
	}

	protected void loadWebXML(String body, String location) throws XModelException {
		super.loadWebXML(body, location);
		if(WebAppHelper.findServlet(webxml, JSFConstants.FACES_SERVLET_CLASS, null) == null) {
			String webXMLErrorMessage = JSFUIMessages.ImportJSFWarContext_NoJSFSupportFound; 
			throw new XModelException(webXMLErrorMessage);
		}
	}

	protected String getWebModuleEntity() {
		return "WebJSFModule"; //"JstWebModule"; //$NON-NLS-1$
	}
	
	protected AdoptWebProjectContext createAdoptContext() {
		throw new RuntimeException("Not implemented"); //$NON-NLS-1$
	}

	public String getNatureID() {
		return JSFNature.NATURE_ID;
	}

}
