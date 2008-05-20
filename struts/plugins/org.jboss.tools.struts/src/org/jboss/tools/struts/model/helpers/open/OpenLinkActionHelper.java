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
package org.jboss.tools.struts.model.helpers.open;

import org.eclipse.osgi.util.NLS;

import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.FindObjectHelper;
import org.jboss.tools.struts.messages.StrutsUIMessages;
import org.jboss.tools.struts.model.helpers.StrutsProcessStructureHelper;
import org.jboss.tools.struts.webprj.model.helpers.WebModulesHelper;
import org.jboss.tools.struts.webprj.pattern.UrlPattern;

public class OpenLinkActionHelper {

	public String run(XModel model, String url, String module) {
		if(model == null || url == null) return null;
		if(url.length() == 0) return StrutsUIMessages.ACTION_ISNOT_SPECIFIED;
		if(!url.startsWith("/") && url.length () > 0) url = "/" + url; //$NON-NLS-1$ //$NON-NLS-2$
		XModelObject c = findAction(model, url, module);
		if(c == null) return NLS.bind(StrutsUIMessages.CANNOT_FIND_ACTION, url); //$NON-NLS-2$
		FindObjectHelper.findModelObject(c, FindObjectHelper.IN_EDITOR_ONLY);
		return null;
	}

	public XModelObject findAction(XModel model, String url, String module) {
		XModelObject[] cgs = (module == null) 
		  ? WebModulesHelper.getInstance(model).getAllConfigs()
		  : WebModulesHelper.getInstance(model).getConfigsForModule(model, module);
		for (int i = 0; i < cgs.length; i++) {
			UrlPattern pattern = StrutsProcessStructureHelper.instance.getUrlPattern(cgs[i]);
			String action = pattern.getActionPath(url);
			XModelObject f = cgs[i].getChildByPath("action-mappings"); //$NON-NLS-1$
			if(f == null) continue;
			XModelObject[] rs = f.getChildren();
			for (int j = 0; j < rs.length; j++) {
				if(!action.equals(rs[j].getAttributeValue("path"))) continue; //$NON-NLS-1$
				return rs[j];
			}
		}
		return null;
	}

}
