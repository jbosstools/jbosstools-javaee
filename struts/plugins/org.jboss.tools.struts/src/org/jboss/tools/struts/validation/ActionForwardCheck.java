/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.struts.validation;

import java.util.*;

import org.eclipse.osgi.util.NLS;
import org.jboss.tools.common.validation.ValidationErrorManager;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateHandler;
import org.jboss.tools.common.model.*;
import org.jboss.tools.jst.web.validation.Check;
import org.jboss.tools.struts.*;
import org.jboss.tools.struts.model.helpers.*;
import org.jboss.tools.struts.webprj.model.helpers.WebModulesHelper;
import org.jboss.tools.struts.webprj.pattern.UrlPattern;
import org.jboss.tools.struts.model.handlers.*;

public class ActionForwardCheck extends Check {
	XModelObject object;
    protected XModel model;
    protected XModelObject forward;
    protected XModelObject config;
    protected String path;
    protected String module;
    protected Set modules;
    protected String pathModule = null;
    protected static StrutsProcessStructureHelper h = new StrutsProcessStructureHelper();

    protected String pathEmpty = StrutsValidatorMessages.ACTION_FORWARD_PATH_EMPTY;
    protected String pathExists = StrutsValidatorMessages.ACTION_FORWARD_PATH_EXISTS;
    protected String classNameExists = StrutsValidatorMessages.ACTION_FORWARD_CLASSNAME_EXISTS;
    protected String contextRelativeCross = StrutsValidatorMessages.ACTION_FORWARD_CONTEXT_RELATIVE_CROSS;
    protected String contextRelativeMono = StrutsValidatorMessages.ACTION_FORWARD_CONTEXT_RELATIVE_MONO;

    public ActionForwardCheck(ValidationErrorManager manager, String preference) {
    	super(manager, preference, "");
    }

    protected boolean isRelevant(XModelObject object) {
        return object.getParent() != null && object.getParent().getModelEntity().getName().startsWith("StrutsAction");
    }

     public void check(XModelObject object) {
        this.object = object;
        if(!isRelevant(object)) {
        	return;
        }
        forward = object;
        model = forward.getModel();
        path = JumpByForwardPathHandler.getResourcePath("" + forward.getAttributeValue("path"));
        if(!checkPath()) return;
        checkClasses();
    }

    protected void checkClasses() {
        checkClass("className", classNameExists);
    }

    // compare find with JumpByForwardPathHandler

    protected boolean isAllowedEmptyPath() {
        return false;
    }

    protected boolean checkPath() {
        if(path.length() == 0) {
        	if(isAllowedEmptyPath()) {
        		return true;
        	} else {
        		fire(pathEmpty, "path", null);
        		return false;
        	}
        }
        config = StrutsProcessStructureHelper.instance.getParentFile(forward);
        WebModulesHelper wh = WebModulesHelper.getInstance(model);
        if(TilesHelper.findTile(config, path) != null) return true;
        module = StrutsProcessStructureHelper.instance.getProcessModule(forward);
        modules = wh.getModules();
        pathModule = wh.getModuleForPath(path, module);
		if(pathModule == null) pathModule = "";
        if(StrutsProcessHelper.isHttp(path)) return true;
        UrlPattern up = wh.getUrlPattern(module);
		boolean isFolder = path.endsWith("/") && path.startsWith("/");
        boolean isAction = !isFolder && up.isActionUrl(path);
		boolean isPage = path.endsWith(".jsp") || path.indexOf(".htm") > 0 || path.endsWith(".css");
        String s = forward.getAttributeValue("contextRelative");
        boolean cR = ("true".equals(s) || "yes".equals(s));
        if(isFolder || isAction || isPage) {
			if(forward.getModelEntity().getAttribute("contextRelative") != null) {
				if(!cR && pathModule.length() > 0 && path.startsWith(pathModule + "/")) {
					fire(contextRelativeCross, "contextRelative", null);
					return false;
				}
				if(cR && pathModule.length() > 0 && !path.startsWith(pathModule + "/")) {
					fire(contextRelativeMono, "contextRelative", null);
					return false;
				}
			} else {
				if(!pathModule.equals(module) || (pathModule.length() > 0 && path.startsWith(pathModule + "/"))) {
					fire(contextRelativeMono, "path", null);
					return false;
				}
			}
        }
        XModelObject target = null;
        String kind = "tile";
		if(isFolder) {
			kind = "resource folder";
			target = model.getByPath(path.substring(0, path.length() - 1));
		} else if(isAction) {
            kind = "action";
            path = up.getActionPath(path);
            if(!cR) {
                target = config.getChildByPath(StrutsConstants.ELM_ACTIONMAP + "/" + path.replace('/', '#'));
                if(target == null) {
					XModelObject[] cgs = wh.getConfigsForModule(model, pathModule);
					for (int i = 0; i < cgs.length && target == null; i++) {
						if(cgs[i] == config) continue;
						target = cgs[i].getChildByPath(StrutsConstants.ELM_ACTIONMAP + "/" + path.replace('/', '#'));
					}
                }
            } else {
                XModelObject[] cgs = wh.getConfigsForModule(model, pathModule);
				if(cgs.length > 0) {
					if(pathModule.length() > 0 && path.startsWith(pathModule)) path = path.substring(pathModule.length());
				}
                for (int i = 0; i < cgs.length && target == null; i++) {
                    target = cgs[i].getChildByPath(StrutsConstants.ELM_ACTIONMAP + "/" + path.replace('/', '#'));
                }
            }
        } else /*if(isPage) */{
            kind = (isPage) ? "page" : "tile or page";
			if(path.endsWith(".css")) kind = "resource";
            XModelObject fs = wh.getRootFileSystemForModule(model, pathModule);
            if(fs != null) {
                if(pathModule.length() > 0 && path.startsWith(pathModule)) path = path.substring(pathModule.length());
                if(path.startsWith("/")) path = path.substring(1);
                target = fs.getChildByPath(path);
            }
        }
		if(target != null) {
			return true;
		}
		fire(pathExists, "path", kind);
		return false;
    }

    protected boolean checkClass(String attr, String id) {
        ValidateTypeUtil tv = new ValidateTypeUtil();
        int tvr = tv.checkClass(object, attr, "org.apache.struts.action.ActionForward");
        if(tvr == ValidateTypeUtil.NOT_FOUND) {
            fire(id, attr, null);
            return false;
        } else if(tvr == ValidateTypeUtil.NOT_UPTODATE) {
            //not implemented
        }
        return true;
    }

    protected void fire(String id, String attr, String info) {
    	this.attr = attr;
    	String oTitle = DefaultCreateHandler.title(object, true);
    	String pTitle = DefaultCreateHandler.title(object.getParent(), true);
        String[] os = (info == null) ? new String[] {oTitle, pTitle}
                      : new String[] {oTitle, pTitle, info};
        fireMessage(object, id, os);
    }

}

