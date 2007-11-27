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
package org.jboss.tools.struts.model.handlers;

import java.util.*;

import org.eclipse.core.resources.IResource;
import org.eclipse.osgi.util.NLS;

import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.impl.XModelImpl;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.model.util.FindObjectHelper;
import org.jboss.tools.common.meta.action.*;
import org.jboss.tools.common.meta.action.impl.*;
import org.jboss.tools.struts.*;
import org.jboss.tools.struts.messages.StrutsUIMessages;
import org.jboss.tools.struts.model.handlers.page.create.CreatePageSupport;
import org.jboss.tools.struts.model.helpers.*;
import org.jboss.tools.struts.webprj.model.helpers.*;
import org.jboss.tools.struts.webprj.pattern.UrlPattern;

public class JumpByForwardPathHandler extends AbstractHandler implements StrutsConstants {

    public boolean isEnabled(XModelObject object) {
        String attr = getJumpAttributeName();
        return (object != null && object.isActive() &&
                object.getAttributeValue(attr) != null &&
                object.getAttributeValue(attr).length() > 0);
    }

    public void executeHandler(XModelObject object, Properties p) throws Exception {
        if(!isEnabled(object)) return;
        String attr = getJumpAttributeName();
        XModelObject target = findForwardTarget(object, attr);
        String path = object.getAttributeValue(attr);
        if(target == null) {
            ServiceDialog d = object.getModel().getService();
            d.showDialog(StrutsUIMessages.WARNING, NLS.bind(StrutsUIMessages.CANNOT_FIND_OBJECT_BY_PATH, path), new String[]{StrutsUIMessages.OK}, null, ServiceDialog.WARNING); //$NON-NLS-3$
        } else {
        	doOpenTarget(target);
        }
    }

    private String getJumpAttributeName() {
        String attr = action.getProperty("linkAttr"); //$NON-NLS-1$
        return (attr == null) ? "path" : attr; //$NON-NLS-1$
    }
    
    public static void doOpenTarget(XModelObject target) {
        FindObjectHelper.findModelObject(target, FindObjectHelper.IN_EDITOR_ONLY);
        String entity = target.getModelEntity().getName();
        if(entity.startsWith(ENT_ACTION) ||
        	//OpenOns for global forwards
           (entity.startsWith(ENT_FORWARD) && target.getParent() != null
           	&& target.getParent().getModelEntity().getName().startsWith("StrutsGlobalForwards"))) { //$NON-NLS-1$
           	XActionInvoker.invoke("Select", target, null);  //$NON-NLS-1$
        }
    }

    public static String getResourcePath(String path) {
        int i = path.indexOf('#');
        if(i >= 0) path = path.substring(0, i);
        i = path.indexOf('?');
        if(i >= 0) path = path.substring(0, i);
        return path;
    }
    
    private static XModelObject findForwardTarget(XModelObject object, String attr) {
    	String pathValue = object.getAttributeValue(attr);
    	return findWithContext(pathValue, object);
    }

    public static XModelObject findWithContext(String pathValue, XModelObject object) {
        String path = getResourcePath("" + pathValue); //$NON-NLS-1$
        String cRs = object.getAttributeValue("contextRelative"); //$NON-NLS-1$
        boolean cR = cRs == null || cRs.equals("true") || cRs.equals("yes"); //$NON-NLS-1$ //$NON-NLS-2$
        if(TilesHelper.getTiles(object).containsKey(path)) {
            return TilesHelper.findTile(object, path);
        }
        WebModulesHelper wh = WebModulesHelper.getInstance(object.getModel());
        UrlPattern up = StrutsProcessStructureHelper.instance.getUrlPattern(object);
        if(up.isActionUrl(path)) {
        	path = up.getActionPath(path);
            if(!cR) {
                return StrutsProcessStructureHelper.instance.getParentFile(object).getChildByPath(ELM_ACTIONMAP + "/" + path.replace('/', '#')); //$NON-NLS-1$
            } else {
                String s = StrutsProcessStructureHelper.instance.getProcessModule(object);
                String mod = wh.getModuleForPath(path, s);
                XModelObject[] cgs = wh.getConfigsForModule(object.getModel(), mod);
                if(cgs.length == 0) return null;
                if(mod.length() > 0 && path.startsWith(mod)) path = path.substring(mod.length());
                XModelObject t0 = null;
                for (int i = 0; i < cgs.length; i++) {
                	XModelObject t = cgs[i].getChildByPath(ELM_ACTIONMAP + "/" + path.replace('/', '#')); //$NON-NLS-1$
                	if(t == null) continue;
                	if(SUBTYPE_UNKNOWN.equals(t.getAttributeValue(ATT_SUBTYPE))) {
                		t0 = t; 
                	} else {
                		return t;
                	}
                }
                return t0;
            }
        } else if(path.endsWith(".jsp") || path.indexOf(".htm") > 0 //$NON-NLS-1$ //$NON-NLS-2$
        		|| path.endsWith(CreatePageSupport.getExtension())) {
            if(!cR) {
            	XModelObject fo = XModelImpl.getByRelativePath(object.getModel(), path);
            	if(fo != null) return fo;
                return object.getModel().getByPath(path);
            } else {
                String s = StrutsProcessStructureHelper.instance.getProcessModule(object);
                String mod = wh.getModuleForPath(path, s);
                XModelObject fs = wh.getRootFileSystemForModule(object.getModel(), mod);
                if(fs == null) return null;
                if(mod.length() > 0 && path.startsWith(mod)) path = path.substring(mod.length());
                if(path.startsWith("/")) path = path.substring(1); //$NON-NLS-1$
                XModelObject fo = fs.getChildByPath(path);

                if(!EclipseResourceUtil.isOverlapped(fo)) return fo;
                IResource r = EclipseResourceUtil.getResource(fo);
                if(r == null) return fo;
                XModelObject fo2 = EclipseResourceUtil.getObjectByResource(r);

                return (fo2 != null) ? fo2 : fo;
            }
        }
        return null;
    }

}

