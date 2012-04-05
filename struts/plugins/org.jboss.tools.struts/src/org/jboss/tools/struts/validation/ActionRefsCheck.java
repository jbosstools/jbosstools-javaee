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

import org.jboss.tools.common.validation.ValidationErrorManager;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateHandler;
import org.jboss.tools.common.model.*;
import org.jboss.tools.jst.web.validation.Check;
import org.jboss.tools.struts.webprj.model.helpers.WebModulesHelper;
import org.jboss.tools.struts.webprj.pattern.UrlPattern;
import org.jboss.tools.struts.model.helpers.*;
import org.jboss.tools.struts.*;

/**
 *
 * @author  valera
 */
public class ActionRefsCheck extends Check {
	protected XModelObject object;
	protected XModel model;
	protected XModelObject action;
	protected XModelObject config;
	protected String path;
	protected String module;
	protected Set modules;
	protected String pathModule = null;
	protected static StrutsProcessStructureHelper h = new StrutsProcessStructureHelper();

	/** Creates a new instance of ActionRefsCheck */
	public ActionRefsCheck(ValidationErrorManager manager, String preference) {
    	super(manager, preference, "");
    }

	public void check(XModelObject object) {
		this.object = object;
		action = object;
		model = action.getModel();

		if(!isInputReferencingForwardOrEmpty()) {
			checkPath(StrutsValidatorMessages.ACTION_INPUT, "input");
		}
		checkPath(StrutsValidatorMessages.ACTION_FORWARD, "forward");
		checkPath(StrutsValidatorMessages.ACTION_INCLUDE, "include");
	}

	boolean isInputReferencingForwardOrEmpty() {
		String v = action.getAttributeValue("input");
		if(v == null || v.length() == 0) return true;
		if(v.indexOf('/') < 0 && v.indexOf('.') < 0) {
			XModelObject f = action.getChildByPath(v);
			if(f != null) return true;
			XModelObject cfg = action.getParent().getParent();
			f = cfg.getChildByPath("global-forwards/" + v);
			if(f != null) return true;
			WebModulesHelper h = WebModulesHelper.getInstance(action.getModel());
			XModelObject[] cfgs = h.getConfigsForModule(cfg.getModel(), h.getModuleForConfig(cfg));
			if(cfgs == null || cfgs.length < 2) return false;
			for (int i = 0; i < cfgs.length; i++) {
				f = cfgs[i].getChildByPath("global-forwards/" + v);
				if(f != null) return true;
			}
		}
		return false;
	}

	protected void checkPath(String id, String attr) {
		this.attr = attr;
		path = action.getAttributeValue(attr);
		if(path == null || path.length() == 0) return;
		config = StrutsProcessStructureHelper.instance.getParentFile(action);
		WebModulesHelper wh = WebModulesHelper.getInstance(model);
		if(TilesHelper.findTile(config, path) != null) return;
		module = StrutsProcessStructureHelper.instance.getProcessModule(action);
		modules = wh.getModules();
		pathModule = wh.getModuleForPath(path, module);
		if(StrutsProcessHelper.isHttp(path)) return;
		UrlPattern up = wh.getUrlPattern(module);
		boolean isFolder = path.endsWith("/") && path.startsWith("/");
		boolean isAction = !isFolder && up.isActionUrl(path);
		boolean isPage = path.endsWith(".jsp") || path.indexOf(".htm") > 0 || path.endsWith(".css");
		XModelObject target = null;
		String kind = "tile";
		if(isFolder) {
			kind = "resource folder";
			path = path.substring(0, path.length() - 1);
			target = model.getByPath(path);
		} else if(isAction) {
			kind = "action";
			path = up.getActionPath(path);
			XModelObject[] cgs = wh.getConfigsForModule(model, pathModule);
			if(cgs.length > 0) {
				if(pathModule.length() > 0 && path.startsWith(pathModule)) path = path.substring(pathModule.length());
			}
			for (int i = 0; i < cgs.length && target == null; i++) {
				target = cgs[i].getChildByPath(StrutsConstants.ELM_ACTIONMAP + "/" + path.replace('/', '#'));
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
		if(target == null) {
			fire(id, attr, kind + " " + path);
		}
	}

	protected void fire(String id, String attr, String value) {
		fireMessage(object, id, DefaultCreateHandler.title(object, true), value);
	}

}
