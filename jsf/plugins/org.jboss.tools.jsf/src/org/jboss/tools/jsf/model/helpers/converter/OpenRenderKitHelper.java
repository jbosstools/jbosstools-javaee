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
package org.jboss.tools.jsf.model.helpers.converter;

import java.util.Properties;

import org.eclipse.osgi.util.NLS;

import org.jboss.tools.common.meta.action.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.util.FindObjectHelper;
import org.jboss.tools.jsf.messages.JSFUIMessages;
import org.jboss.tools.jsf.model.pv.*;
import org.jboss.tools.jst.web.model.pv.WebProjectNode;

public class OpenRenderKitHelper {

	public String run(XModel model, String renderkitId) {
		if(model == null || renderkitId == null) return null;
		if(renderkitId.length() == 0) return JSFUIMessages.OpenRenderKitHelper_RENDER_KIT_ID_NOT_SPECIFIED;
		XModelObject c = findRenderKit(model, renderkitId);
		if(c == null) return NLS.bind(JSFUIMessages.CANNOT_FIND_RENDER_KIT, renderkitId);
		FindObjectHelper.findModelObject(c, FindObjectHelper.IN_EDITOR_ONLY);
		return null;
	}

	String openClass(XModelObject c, String renderkitId) {
		String className = c.getAttributeValue("render-kit-class"); //$NON-NLS-1$
		if(className == null || className.length() == 0) return NLS.bind(JSFUIMessages.ATTRIBUTE_RENDER_KIT_CLASS_FOR_RENDER_KIT_ISNOT_SPECIFIED, renderkitId); 
		XAction xaction = XActionInvoker.getAction("OpenSource", c); //$NON-NLS-1$
		if(xaction != null && xaction.isEnabled(c)) {
			Properties p = new Properties();
			p.setProperty("ignoreWarning", "true"); //$NON-NLS-1$ //$NON-NLS-2$
			XActionInvoker.invoke("OpenSource", c, p); //$NON-NLS-1$
			return p.getProperty("error"); //$NON-NLS-1$
		}
		return null;
	}

	public XModelObject findRenderKit(XModel model, String renderkitId) {
		JSFProjectsRoot root = JSFProjectsTree.getProjectsRoot(model);
		if(root == null) return null;
		WebProjectNode n = (WebProjectNode)root.getChildByPath("Configuration"); //$NON-NLS-1$
		if(n == null) return null;
		XModelObject[] os = n.getTreeChildren();
		for (int i = 0; i < os.length; i++) {
			XModelObject r = os[i].getChildByPath("Render Kits/" + renderkitId); //$NON-NLS-1$
			if(r != null) return r;
		}
		return null;
	}
}
