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
package org.jboss.tools.struts.model.handlers.page;

import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.struts.StrutsConstants;
import org.jboss.tools.struts.model.ReferenceObjectImpl;
import org.jboss.tools.struts.model.handlers.page.create.CreatePageSupport;
import org.jboss.tools.struts.model.helpers.StrutsProcessStructureHelper;
import org.jboss.tools.jst.web.browser.*;
import org.jboss.tools.struts.webprj.model.helpers.WebModulesHelper;
import org.jboss.tools.struts.webprj.pattern.UrlPattern;

public class IPathSourceImpl implements IPathSource, StrutsConstants {
	IBrowserContext context;
	
	public void setBrowserContext(IBrowserContext context) {
		this.context = context;
	}

	public String computeURL(XModelObject object) {
		String type = object.getAttributeValue(ATT_TYPE);
		boolean isRoot = object.getModel().getRoot() == object;
		if(!isRoot && !isRelevant(object)) return null;
		boolean isForward = !isRoot && isForward(object);
		boolean isPage = !isRoot && TYPE_PAGE.equals(type);
		boolean isAction = !isRoot && isAction(object);
		if(isPage) object = StrutsProcessStructureHelper.instance.getPhysicalPage(object);
		XModel model = object.getModel();
		String bp = context.getBrowserPrefix(model);
		String path = (isRoot) ? "/"
			   : (isForward) ? getCompleteForwardPath(object)
			   : (isAction) ? getCompleteActionPath(object)
					  : WebModulesHelper.getInstance(object.getModel()).getQualifiedPagePath(object);
		String url = (path == null) ? null
				: (bp.endsWith("/") && path.startsWith("/")) 
				    ? bp + path.substring(1)
				    : bp + path;
		return url;
	}
	private boolean isRelevant(XModelObject object) {
		if(object.getFileType() == XModelObject.FILE) {
			String ext = ("" + object.getAttributeValue("extension")).toLowerCase();
			if(ext.equals(CreatePageSupport.getExtension().substring(1))) return true;
			return ext.indexOf("jsp") >= 0;
		}
		XModelObject o = StrutsProcessStructureHelper.instance.getParentFile(object);
		return o != null && o.getModelEntity().getName().startsWith(ENT_STRUTSCONFIG);
	}

    private static boolean isAction(XModelObject object) {
        String type = object.getAttributeValue(ATT_TYPE);
        return TYPE_ACTION.equals(type) || object.getModelEntity().getName().startsWith("StrutsAction");
    }

    private static String getCompleteForwardPath(XModelObject object) {
        String path = object.getAttributeValue(ATT_PATH);
        if(object instanceof ReferenceObjectImpl) {
            XModelObject o = ((ReferenceObjectImpl)object).getReference();
            if(o != null) object = o;
        }
        return StrutsProcessStructureHelper.instance.getRunTimeForwardPath(object, path);
    }

    private static String getCompleteActionPath(XModelObject item) {
        String path = item.getAttributeValue(ATT_PATH);
        String module = StrutsProcessStructureHelper.instance.getProcessModule(item);
        UrlPattern up = StrutsProcessStructureHelper.instance.getUrlPattern(item);
        path = up.getActionUrl(path); //(path.endsWith(".do")) ? path : path + ".do";        
        if(module.length() > 0 && !SUBTYPE_UNKNOWN.equals(item.getAttributeValue(ATT_SUBTYPE))) {
			path = up.getContextRelativePath(path, module); //path = module + path;
        }
        return path; 
    }

    static boolean isForward(XModelObject object) {
        String type = object.getAttributeValue(ATT_TYPE);
        return TYPE_FORWARD.equals(type) || object.getModelEntity().getName().startsWith("StrutsForward");
    }

}
