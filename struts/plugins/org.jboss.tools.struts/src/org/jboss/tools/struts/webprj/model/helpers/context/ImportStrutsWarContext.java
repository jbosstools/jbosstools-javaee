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
package org.jboss.tools.struts.webprj.model.helpers.context;

import java.util.ArrayList;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.struts.StrutsProject;
import org.jboss.tools.jst.web.context.ImportWebWarContext;
import org.jboss.tools.jst.web.project.WebModuleConstants;
import org.jboss.tools.struts.webprj.model.helpers.adopt.AdoptProjectContext;

public class ImportStrutsWarContext extends ImportWebWarContext {

	protected void createModules() {
		String[][] ms = AdoptProjectContext.getModules(webxml);
		ArrayList<XModelObject> list = new ArrayList<XModelObject>();
		for (int i = 0; i < ms.length; i++) {
			XModelObject module = createModuleInfo(webxml.getModel(), ms[i][0], ms[i][1]);
			if(module != null) list.add(module);
		}
		modules = list.toArray(new XModelObject[0]);
		createAllModules();
	}

	protected void loadWebXML(String body, String location) throws Exception {
		super.loadWebXML(body, location);
		String[][] ms = AdoptProjectContext.getModules(webxml);
		if(ms == null || ms.length == 0) {
			String webXMLErrorMessage = "No Struts support found in the project."; 
			throw new Exception(webXMLErrorMessage);
		}
	}

	protected String getWebModuleEntity() {
		return WebModuleConstants.ENTITY_WEB_MODULE;
	}

	public String getNatureID() {
		return StrutsProject.NATURE_ID;
	}

}
