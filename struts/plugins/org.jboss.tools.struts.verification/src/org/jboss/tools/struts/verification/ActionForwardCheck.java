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
package org.jboss.tools.struts.verification;

import java.util.*;
import org.jboss.tools.common.verification.vrules.*;
import org.jboss.tools.common.verification.vrules.layer.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.struts.*;
import org.jboss.tools.struts.model.helpers.*;
import org.jboss.tools.struts.webprj.model.helpers.WebModulesHelper;
import org.jboss.tools.struts.webprj.pattern.UrlPattern;
import org.jboss.tools.struts.model.handlers.*;

public class ActionForwardCheck implements VAction {
    protected VRule rule;
    protected VObject object;
    protected XModel model;
    protected XModelObject forward;
    protected XModelObject config;
    protected String path;
    protected String module;
    protected Set modules;
    protected String pathModule = null;
    protected static StrutsProcessStructureHelper h = new StrutsProcessStructureHelper();

    public ActionForwardCheck() {}

    public VRule getRule() {
        return rule;
    }

    public void setRule(VRule rule) {
        this.rule = rule;
    }

    protected boolean isRelevant(VObject object) {
        return object.getParent() != null && object.getParent().getEntity().getName().startsWith("StrutsAction");
    }

    public VResult[] check(VObject object) {
        if(!isRelevant(object)) return null;
        this.object = object;
        forward = ((VObjectImpl)object).getModelObject();
        model = forward.getModel();
        path = JumpByForwardPathHandler.getResourcePath("" + forward.getAttributeValue("path"));
        VResult[] rs = checkPath();
        if(rs != null) return rs;
        rs = checkClasses();
        if(rs != null) return rs;
        return null;
    }

    protected VResult[] checkClasses() {
        return checkClass("className", "class");
    }

    // compare find with JumpByForwardPathHandler

    protected boolean isAllowedEmptyPath() {
        return false;
    }

    protected VResult[] checkPath() {
        if(path.length() == 0) {
            return (isAllowedEmptyPath()) ? null : fire("empty", "path", null);
        }
        config = StrutsProcessStructureHelper.instance.getParentFile(forward);
        WebModulesHelper wh = WebModulesHelper.getInstance(model);
        if(TilesHelper.findTile(config, path) != null) return null;
        module = StrutsProcessStructureHelper.instance.getProcessModule(forward);
        modules = wh.getModules();
        pathModule = wh.getModuleForPath(path, module);
		if(pathModule == null) pathModule = "";
        if(StrutsProcessHelper.isHttp(path)) return null;
        UrlPattern up = wh.getUrlPattern(module);
		boolean isFolder = path.endsWith("/") && path.startsWith("/");
        boolean isAction = !isFolder && up.isActionUrl(path);
		boolean isPage = path.endsWith(".jsp") || path.indexOf(".htm") > 0 || path.endsWith(".css");
        String s = forward.getAttributeValue("contextRelative");
        boolean cR = ("true".equals(s) || "yes".equals(s));
        if(isFolder || isAction || isPage) {
			if(forward.getModelEntity().getAttribute("contextRelative") != null) {
				if(!cR && pathModule.length() > 0 && path.startsWith(pathModule + "/")) {
					return fire("cross", "contextRelative", null);
				}
				if(cR && pathModule.length() > 0 && !path.startsWith(pathModule + "/")) {
					return fire("mono", "contextRelative", null);
				}
			} else {
				if(!pathModule.equals(module) || (pathModule.length() > 0 && path.startsWith(pathModule + "/"))) {
					return fire("mono", "path", null);
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
        return (target == null) ? fire("exists", "path", kind) : null;
    }

    protected VResult[] checkClass(String attr, String id) {
        ValidateTypeUtil tv = new ValidateTypeUtil();
        int tvr = tv.checkClass(object, attr, "org.apache.struts.action.ActionForward");
        if(tvr == ValidateTypeUtil.NOT_FOUND) {
            return fire(id, attr, null);
        } else if(tvr == ValidateTypeUtil.NOT_UPTODATE) {
            return null;
        }
        return null;
    }

    protected VResult[] fire(String id, String attr, String info) {
        Object[] os = (info == null) ? new Object[] {object, object.getParent()}
                      : new Object[] {object, object.getParent(), info};
        VResult result = rule.getResultFactory().getResult(id, object, attr, object, attr, os);
        return new VResult[] {result};
    }

}

