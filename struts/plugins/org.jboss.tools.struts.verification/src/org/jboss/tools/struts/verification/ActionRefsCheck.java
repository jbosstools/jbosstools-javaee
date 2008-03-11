/*
 * ActionRefsCheck.java
 *
 * Created on July 31, 2003, 12:13 PM
 */

package org.jboss.tools.struts.verification;

import java.util.*;
import org.jboss.tools.common.verification.vrules.*;
import org.jboss.tools.common.verification.vrules.layer.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.struts.webprj.model.helpers.WebModulesHelper;
import org.jboss.tools.struts.webprj.pattern.UrlPattern;
import org.jboss.tools.struts.model.helpers.*;
import org.jboss.tools.struts.*;

/**
 *
 * @author  valera
 */
public class ActionRefsCheck implements VAction {
	protected VRule rule;
	protected VObject object;
	protected XModel model;
	protected XModelObject action;
	protected XModelObject config;
	protected String path;
	protected String module;
	protected Set modules;
	protected String pathModule = null;
	protected static StrutsProcessStructureHelper h = new StrutsProcessStructureHelper();

	/** Creates a new instance of ActionRefsCheck */
	public ActionRefsCheck() {}

	public VResult[] check(VObject object) {
		this.object = object;
		action = ((VObjectImpl)object).getModelObject();
		model = action.getModel();
		List<VResult> results = new ArrayList<VResult>();
		VResult result = isInputReferencingForwardOrEmpty() ? null : checkPath("input");
		if(result != null) results.add(result);
		result = checkPath("forward");
		if(result != null) results.add(result);
		result = checkPath("include");
		if(result != null) results.add(result);
		return (VResult[])results.toArray(new VResult[results.size()]);
	}

	public VRule getRule() {
		return rule;
	}

	public void setRule(VRule rule) {
		this.rule = rule;
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

	protected VResult checkPath(String attr) {
		path = action.getAttributeValue(attr);
		if(path == null || path.length() == 0) return null;
		config = StrutsProcessStructureHelper.instance.getParentFile(action);
		WebModulesHelper wh = WebModulesHelper.getInstance(model);
		if(TilesHelper.findTile(config, path) != null) return null;
		module = StrutsProcessStructureHelper.instance.getProcessModule(action);
		modules = wh.getModules();
		pathModule = wh.getModuleForPath(path, module);
		if(StrutsProcessHelper.isHttp(path)) return null;
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
		return (target == null) ? fire(attr, attr, kind + " " + path) : null;
	}

	protected VResult fire(String id, String attr, String value) {
		Object[] os = new Object[] {object, value};
		VResult result = rule.getResultFactory().getResult(id, object, attr, object, attr, os);
		return result;
	}

}
