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

import org.eclipse.core.resources.IFile;
import org.eclipse.osgi.util.NLS;

import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.impl.XModelImpl;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.model.util.FindObjectHelper;
import org.jboss.tools.struts.messages.StrutsUIMessages;
import org.jboss.tools.struts.webprj.model.helpers.WebModulesHelper;
import org.jboss.tools.struts.webprj.pattern.UrlPattern;

public class OpenLinkPageHelper {
	OpenLinkActionHelper openAction = new OpenLinkActionHelper();

	public String run(IFile file, XModel model, String page) {
		if(model == null || page == null) return null;
		if(page.length() == 0) return StrutsUIMessages.PAGE_ISNOT_SPECIFIED;
        if(page.startsWith("http:")) {
        	//open http?
        } else {
			UrlPattern pattern = WebModulesHelper.getInstance(model).getUrlPattern("");
			if(!page.startsWith("/") && !pattern.isActionUrl("/" + page)) {
				XModelObject f = EclipseResourceUtil.getObjectByResource(file);
				if(f == null) return null;
				XModelObject q = f.getParent().getChildByPath(page);
				if(q != null) {
					FindObjectHelper.findModelObject(q, FindObjectHelper.IN_EDITOR_ONLY);
					return null;
				}				
			}
			if(!page.startsWith("/") && page.length () > 0) page = "/" + page;
			if(pattern.isActionUrl(page)) {
				return openAction.run(model, page, null);
			} else {
				XModelObject c = XModelImpl.getByRelativePath(model, page);
				if(c == null) return NLS.bind(StrutsUIMessages.CANNOT_FIND_PAGE, page);
				FindObjectHelper.findModelObject(c, FindObjectHelper.IN_EDITOR_ONLY);
				return null;
			}
        }
		return null;
	}

}
