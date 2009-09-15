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

import java.io.File;
import java.util.*;
import org.jboss.tools.common.meta.action.impl.AbstractHandler;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.filesystems.XFileObject;
import org.jboss.tools.common.model.util.*;
import org.jboss.tools.struts.StrutsConstants;
import org.jboss.tools.struts.StrutsModelPlugin;
import org.jboss.tools.struts.model.helpers.StrutsProcessStructureHelper;
import org.jboss.tools.jst.web.browser.AbstractBrowserContext;
import org.jboss.tools.struts.webprj.model.helpers.WebModulesHelper;

public abstract class RunHandler extends AbstractHandler implements StrutsConstants {

    public boolean isEnabled(XModelObject object) {
        if(object == null || !object.isActive()) return false;
        if(object == object.getModel().getRoot()) return true;
        String type = object.getAttributeValue(ATT_TYPE);
        String entity = object.getModelEntity().getName();
        if(TYPE_PAGE.equals(type)) {
            if(!"true".equals(object.get("confirmed"))) return false;
            object = StrutsProcessStructureHelper.instance.getPhysicalPage(object);
            if(object == null) return false;
        }
        if(object.getFileType() == XFileObject.FILE) {
            String ext = "." + object.getAttributeValue("extension") + ".";
            return ".jsp.htm.html.tld.".indexOf(ext) >= 0 && isWebPage(object);
        }
        if(TYPE_ACTION.equals(type)) return !SUBTYPE_UNKNOWN.equals(object.getAttributeValue(ATT_SUBTYPE));
        if(entity.startsWith("StrutsAction")) return true;
        if(IPathSourceImpl.isForward(object)) return true;
        return false;
    }
    
    public void executeHandler(XModelObject object, Properties p) throws XModelException {
        if(!isEnabled(object)) return;
        getContext().execute(object);
    }
    
    protected abstract AbstractBrowserContext getContext();

    private boolean isWebPage(XModelObject o) {
        XModelObject fs = o;
        while(fs != null && fs.getFileType() != XFileObject.SYSTEM) fs = fs.getParent();
        return isRootUnderWebRoot(fs);
    }

    private boolean isRootUnderWebRoot(XModelObject fs) {
		if(fs == null) return false;
		XModelObject wr = WebModulesHelper.getInstance(fs.getModel()).getFileSystem("");
        if(wr == null) return false;
        if(wr == fs) return true;
		Map<String,XModelObject> map = WebModulesHelper.getInstance(fs.getModel()).getWebFileSystems();
		if(!map.containsValue(fs)) return false;
        String p1 = getCanonicalLocation(wr);
        String p2 = getCanonicalLocation(fs);
        return p1 != null && p2 != null && p2.startsWith(p1 + "/");
    }

    private String getCanonicalLocation(XModelObject fs) {
        String path = XModelObjectUtil.getExpandedValue(fs, "location", null);
        try {
            return new File(path).getCanonicalPath().replace('\\', '/').toLowerCase();
        } catch (Exception e) {
        	StrutsModelPlugin.getPluginLog().logError(e);
            return null;
        }
    }
    
}
