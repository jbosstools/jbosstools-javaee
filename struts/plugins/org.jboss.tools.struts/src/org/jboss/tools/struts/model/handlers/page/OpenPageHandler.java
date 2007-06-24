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

import java.util.Map;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.impl.XModelImpl;
import org.jboss.tools.common.meta.action.impl.handlers.*;
import org.jboss.tools.struts.*;
import org.jboss.tools.struts.model.helpers.StrutsProcessStructureHelper;
import org.jboss.tools.struts.webprj.model.helpers.WebModulesHelper;

public class OpenPageHandler extends DefaultRedirectHandler implements StrutsConstants {

    public OpenPageHandler() {}

    protected XModelObject getTrueSource(XModelObject source) {
        if(!TYPE_PAGE.equals(source.getAttributeValue(ATT_TYPE))) return null;
        if(SUBTYPE_TILE.equals(source.getAttributeValue(ATT_SUBTYPE))) {
        	return null;
        }

        String path = source.getAttributeValue(ATT_PATH);
        if(path == null || !path.startsWith("/")) return null;
        
        XModelObject o = null;
        
        XModelObject cfg = StrutsProcessStructureHelper.instance.getParentFile(source);
        String module = WebModulesHelper.getInstance(source.getModel()).getModuleForConfig(cfg);
        if(module != null && module.startsWith("/")) {
        	String fullPath = path;
        	if(!path.startsWith(module)) fullPath = module + path;
        	o = XModelImpl.getByRelativePath(source.getModel(), fullPath);
        }
        
        if(o == null) o = XModelImpl.getByRelativePath(source.getModel(), path);
        if(o == null) return null;
        Map map = WebModulesHelper.getInstance(source.getModel()).getWebFileSystems();
        XModelObject fs = o;
        while(fs != null && fs.getFileType() != XModelObject.SYSTEM) fs = fs.getParent();
        return (fs != null && map.containsValue(fs)) ? o : null;

//        return StrutsProcessStructureHelper.instance.findReferencedJSPInCurrentModule(source);
    }

}
