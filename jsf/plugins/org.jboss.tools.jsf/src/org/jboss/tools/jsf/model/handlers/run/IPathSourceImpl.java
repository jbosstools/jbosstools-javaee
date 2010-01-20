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
package org.jboss.tools.jsf.model.handlers.run;

import org.jboss.tools.common.model.*;
import org.jboss.tools.jsf.model.JSFConstants;
import org.jboss.tools.jsf.model.helpers.JSFProcessStructureHelper;
import org.jboss.tools.jsf.web.JSFWebProject;
import org.jboss.tools.jst.web.browser.*;

public class IPathSourceImpl implements IPathSource, JSFConstants {
	IBrowserContext context;
	
	public void setBrowserContext(IBrowserContext context) {
		this.context = context;
	}	
	
	public String computeURL(XModelObject object) {
		XModel model = object.getModel();
		boolean isRoot = model.getRoot() == object;
		if(isRoot) {
			String result = context.getBrowserPrefix(object.getModel());
			if(result != null && !result.endsWith("/")) result += "/"; //$NON-NLS-1$ //$NON-NLS-2$
			return result;
		}
		if(!isRelevant(object)) return null;
		String f = getPath(object);
		if(f == null) return null;
		String bp = context.getBrowserPrefix(model);
		if(bp == null) return null;
		String path = JSFWebProject.getInstance(model).getUrlPattern().getJSFUrl(f);
		String url = (path == null) ? null
				: (bp.endsWith("/") && path.startsWith("/"))  //$NON-NLS-1$ //$NON-NLS-2$
				    ? bp + path.substring(1)
				    : bp + path;
		return url;
	}
	private boolean isRelevant(XModelObject object) {
		if(object.getFileType() == XModelObject.FILE) return false; // no
		XModelObject o = JSFProcessStructureHelper.instance.getParentFile(object);
		return o != null && o.getModelEntity().getName().startsWith(ENT_FACESCONFIG);
	}
	
    public static String getPath(XModelObject object) {
		String f = null;
		String entity = object.getModelEntity().getName();
		if(ENT_NAVIGATION_RULE.equals(entity) || ENT_NAVIGATION_RULE_20.equals(entity)) {
			f = object.getAttributeValue(ATT_FROM_VIEW_ID);
		} else if(ENT_NAVIGATION_CASE.equals(entity) || ENT_NAVIGATION_CASE_20.equals(entity)) {
			f = object.getAttributeValue(ATT_TO_VIEW_ID);
		} else if(ENT_PROCESS_GROUP.equals(entity)) {
			f = object.getAttributeValue(ATT_PATH);
		} else if(ENT_PROCESS_ITEM.equals(entity)) {
			f = object.getAttributeValue(ATT_PATH);
		} else if(ENT_PROCESS_ITEM_OUTPUT.equals(entity)) {
			f = object.getAttributeValue(ATT_PATH);
		}
		if(f == null || f.length() == 0 || f.indexOf('*') >= 0) return null;
		return f;
    }

}
