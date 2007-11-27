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
package org.jboss.tools.struts.model.helpers;

import org.jboss.tools.common.meta.action.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.struts.*;
import org.jboss.tools.struts.model.*;
import java.util.*;
import org.jboss.tools.common.meta.*;
import org.jboss.tools.jst.web.model.helpers.WebProcessStructureHelper;
import org.jboss.tools.struts.webprj.model.helpers.WebModulesHelper;
import org.jboss.tools.struts.webprj.pattern.UrlPattern;

public class TilesStructureHelper extends WebProcessStructureHelper implements StrutsConstants {
	public static final TilesStructureHelper instance = new TilesStructureHelper(); 

    public TilesStructureHelper() {}

    public XModelObject[] getDefinitions(XModelObject process) {
        //if(!isProcessLoaded(process)) return new XModelObject[0];
        return process.getChildren("TilesDefinition");
    }
    
    public boolean isStruts10(XModelObject object) {
    	XModelObject config = getParentFile(object);
    	return config != null && config.getModelEntity().getName().equals(ENT_STRUTSCONFIG + VER_SUFFIX_10);
    }

    public XModelObject[] getItemOutput(XModelObject item) {
        return item.getChildren();
    }

    static String GUI_PACKAGE = "org.jboss.tools.struts.ui.diagramview.";

    public String getItemGUIClass(XModelObject item) {
        String type = item.getAttributeValue("type");
//        String subtype = item.getAttributeValue(ATT_SUBTYPE);
        return (type == null) ? null :
               (type.equals(TYPE_FORWARD)) ? GUI_PACKAGE + "GlobalForwardView" :
               (type.equals(TYPE_EXCEPTION)) ? GUI_PACKAGE + "GlobalExceptionView" :
               /*(type.equals(TYPE_ACTION) && subtype.equals(SUBTYPE_FORWARDACTION)) ? GUI_PACKAGE + "ActionForwardView" :*/
               (type.equals(TYPE_ACTION)) ? GUI_PACKAGE + "ActionView" :
               (type.equals(TYPE_PAGE)) ? GUI_PACKAGE + "PageView" :
               (type.equals("comment")) ? GUI_PACKAGE + "CommentView" :
               null;
    }

    public XActionList getLinkActionList(XModelObject itemOutput) {
       String type = itemOutput.getAttributeValue(ATT_TYPE);
       XModelMetaData meta = itemOutput.getModel().getMetaData();
       if(TYPE_LINK.equals(type)) return meta.getEntity("StrutsProcessPageTransition").getActionList();
       if(TYPE_EXCEPTION.equals(type)) return meta.getEntity("StrutsProcessExceptionTransition").getActionList();
       return meta.getEntity("StrutsProcessTransition").getActionList();
    }

    public XModelObject getReference(XModelObject object) {
    	if(!(object instanceof ReferenceObjectImpl)) return null;
        return ((ReferenceObjectImpl)object).getReference();
    }

    public XModelObject getElementTarget(XModelObject element) {
        return getParentProcess(element).getChildByPath(element.getAttributeValue(ATT_TARGET));
    }

    public XModelObject getItemOutputTarget(XModelObject itemOutput) {
        return itemOutput.getParent().getParent().getChildByPath(itemOutput.getAttributeValue("extends"));
    }

    public XModelObject getItemTarget(XModelObject itemOutput) {
        return itemOutput.getParent().getChildByPath(itemOutput.getAttributeValue("extends"));
    }
    
    /**
     * Returns true if item is action or page,
     * and either path starts with name of another module, 
     * or another config of the same module contains the action
     */
    public boolean isItemFromOtherModule(XModelObject item) {
        String type = item.getAttributeValue(ATT_TYPE);
        if(!TYPE_ACTION.equals(type) && !TYPE_PAGE.equals(type)) return false;
        String path = item.getAttributeValue("path");
        if(path == null || !path.startsWith("/")) return false;
        if(path.startsWith("//")) return true;
        int i = path.indexOf("/", 1);
        if(i >= 0) {
			String m = path.substring(0, i);
			if(WebModulesHelper.getInstance(item.getModel()).getModules().contains(m)) {
				return true;
			}
        }
		return isActionFromOtherConfigOfTheSameModule(item);
    }

	public boolean isActionFromOtherConfigOfTheSameModule(XModelObject item) {
		String type = item.getAttributeValue(ATT_TYPE);
		if(!TYPE_ACTION.equals(type) || !SUBTYPE_UNKNOWN.equals(item.getAttributeValue(ATT_SUBTYPE))) {
			return false;
		}
		String path = item.getAttributeValue("path");
		if(path == null || !path.startsWith("/")) return false;
		if(path.startsWith("//")) return false;
		int i = path.indexOf("/", 1);
		if(i >= 0) {
			String m = path.substring(0, i);
			if(WebModulesHelper.getInstance(item.getModel()).getModules().contains(m)) {
				return false;
			}
		}
		XModelObject cfg = getParentFile(item);
		if(cfg == null) return false;
		WebModulesHelper modules = WebModulesHelper.getInstance(item.getModel());
		String module = modules.getModuleForConfig(cfg);
		XModelObject[] cgs = modules.getConfigsForModule(cfg.getModel(), module);
		if(cgs.length < 2) return false;
		for (int k = 0; k < cgs.length; k++) {
			XModelObject ti = findItem(cgs[k], path, type);
			if(ti == null) continue;
			if(!SUBTYPE_UNKNOWN.equals(ti.getAttributeValue(ATT_SUBTYPE))) return true; 
		}
		return false;
	}

    public XModelObject findItemInOtherModule(XModelObject item) {
        XModelObject cfg = getParentFile(item);
        if(cfg == null) return null;
        WebModulesHelper modules = WebModulesHelper.getInstance(item.getModel());
        String current = modules.getModuleForConfig(cfg);
        String path = item.getAttributeValue("path");
        String module = modules.getModuleForPath(path, current);
        if(module == null) return null;
///		if(module == null || module.equals(current)) return null;
        cfg = modules.getConfigForModule(cfg.getModel(), module);
        if(cfg == null) return null;
        if(path.startsWith("//")) path = path.substring(1);
        else {
        	path = getUrlPattern(cfg).getModuleRelativePath(path, module);
        }
        String type = item.getAttributeValue("type");
        XModelObject targetItem = findItem(cfg, path, type);
        if(targetItem != null && !SUBTYPE_UNKNOWN.equals(targetItem.getAttributeValue(ATT_SUBTYPE))) return targetItem;
        XModelObject[] cgs = modules.getConfigsForModule(cfg.getModel(), module);
        for (int i = 0; i < cgs.length; i++) {
			XModelObject ti = findItem(cgs[i], path, type);
			if(ti == null) continue;
			if(!SUBTYPE_UNKNOWN.equals(ti.getAttributeValue(ATT_SUBTYPE))) {
				return ti; 
			} else {
				targetItem = ti;
			}
        }
        return targetItem;
    }
    
    private XModelObject findItem(XModelObject config, String path, String type) {
    	XModelObject process = (config == null) ? null : config.getChildByPath("process");
    	if(process == null) return null;
		XModelObject[] items = process.getChildren();
		for (int i = 0; i < items.length; i++) {
			if(!path.equals(items[i].getAttributeValue("path"))) continue;
			if(!type.equals(items[i].getAttributeValue("type"))) continue;
			return items[i];
		}
		return null;
    }

    public XModelObject getParentProcess(XModelObject element) {
        XModelObject p = element;
        while(p != null && p.getFileType() == XModelObject.NONE &&
              !"StrutsProcess".equals(p.getModelEntity().getName())) p = p.getParent();
        return p;
    }

    public String getProcessModule(XModelObject object) {
        XModelObject file = getParentFile(object);
        String module = (file == null) ? null : WebModulesHelper.getInstance(object.getModel()).getModuleForConfig(file);
        return (module == null) ? "" : module;
    }
    
    public UrlPattern getUrlPattern(XModelObject object) {
		return WebModulesHelper.getInstance(object.getModel()).getUrlPattern(getProcessModule(object));
    }

    public Set getTiles(XModelObject object) {
        XModelObject process = getProcess(object);
        if(process == null) return new TreeSet();
        StrutsProcessHelper h = StrutsProcessHelper.getHelper(process);
        h.updateTiles();
        return h.getTiles();
    }

    public String getModuleRelativePath(XModelObject forward) {
        return getModuleRelativePath(forward, getProcess(forward));
    }

    public String getModuleRelativePath(XModelObject forward, XModelObject process) {
        String path = forward.getAttributeValue(ATT_PATH);
        if(path != null && path.startsWith("//")) return path.substring(1);
        if(process == null) return path;
        String contextRelative = forward.getAttributeValue("contextRelative");
        if(contextRelative == null || "false".equals(contextRelative) ||
           "no".equals(contextRelative)) return path;
        String module = getProcessModule(process);
        if(module == null || module.length() == 0) return path;
        return getUrlPattern(process).getModuleRelativePath(path, module);
    }
    
    public String getContextRelativePath(XModelObject page) {
		String path = page.getAttributeValue(ATT_PATH);
		if(path != null && path.startsWith("//")) return path.substring(1);
		XModelObject process = getParentProcess(page);
		if(process == null) return path;
		String module = getProcessModule(process);
		if(module == null || module.length() == 0) return path;
		return getUrlPattern(process).getContextRelativePath(path, module);
    }

    public String getValidForwardPath(XModelObject forward, String path) {
        XModelObject process = getProcess(forward);
        if(process == null) return path;
        String contextRelative = forward.getAttributeValue("contextRelative");
        if(contextRelative == null) return path;
        String module = getProcessModule(process);
        boolean cR = ("true".equals(contextRelative) || "yes".equals(contextRelative));
		UrlPattern up = getUrlPattern(process);
//        if(cR == path.startsWith(module + "/")) return path;
//        return (cR) ? module + path : path.substring(module.length());
        return (cR) ? up.getContextRelativePath(path, module)
                    : up.getModuleRelativePath(path, module);
    }

    public String getRunTimeForwardPath(XModelObject forward, String path) {
        XModelObject process = getProcess(forward);
        if(process == null) return path;
        String contextRelative = forward.getAttributeValue("contextRelative");
        if(contextRelative == null) return path;
        String module = getProcessModule(process);
        boolean cR = ("true".equals(contextRelative) || "yes".equals(contextRelative));
        return (!cR) ? getUrlPattern(forward).getContextRelativePath(path, module) //module + path 
                     : path;
    }

    /*
     * If module is set, returns only paths of pages in module
     */

    public String getModulePagePath(XModelObject page) {
        return getPagePath0(page);
    }

    public XModelObject getPhysicalPage(XModelObject page) {
        String path = getModulePagePath(page);
        if(path != null) return page.getModel().getByPath(path);
        path = page.getAttributeValue(ATT_PATH);
        if(path.length() < 2) return null;
        int i = path.indexOf('/', 1);
        if(i >= 0) {
            XModelObject o = page.getModel().getByPath(path.substring(i + 1));
            if(o != null) return o;
        }
        return page.getModel().getByPath(path);
    }

    public XModelObject getPhysicalPage(XModelObject process, String path) {
        XModel model = process.getModel();
        String thismodule = getProcessModule(process);
        String module = WebModulesHelper.getInstance(model).getModuleForPath(path, thismodule);
        if(module == null) module = "";
		XModelObject fs = WebModulesHelper.getInstance(model).getFileSystem(module);
        String rpath = getUrlPattern(process).getModuleRelativePath(path, module);
        if(rpath.startsWith("/")) rpath = rpath.substring(1);
        if(fs != null) return fs.getChildByPath(rpath);
        return process.getModel().getByPath(path);
    }

    String getPagePath0(XModelObject page) {
    	if(!page.isActive()) return null;
        String path = page.getAttributeValue(ATT_PATH);
        if(StrutsProcessHelper.isHttp(path)) return path;
        XModelObject process = page.getParent();
        String module = getProcessModule(process);
        boolean is10 = process.getParent().getModelEntity().getName().endsWith(VER_SUFFIX_10);
        if(is10 || module == null || module.length() == 0) return path;
        return getUrlPattern(page).getModuleRelativePath(path, module);
    }

    boolean is10(XModelObject process) {
        return process.getParent().getModelEntity().getName().endsWith(VER_SUFFIX_10);
    }

    public XModelObject findReferencedJSPInCurrentModule(XModelObject page) {
        String path = page.getAttributeValue(ATT_PATH);
        if(path == null || !path.startsWith("/")) return null;
        XModelObject o = page.getModel().getByPath(path);
        if(o == null || is10(page.getParent())) return o;
		Map modules = WebModulesHelper.getInstance(page.getModel()).getWebFileSystems();
        if(modules.size() < 2) return o;
        if(path.startsWith("//")) return null;
        String xmodule = getUrlPattern(page).getModule(path, modules.keySet(), null);
        if(xmodule != null && xmodule.length() > 0) return null;
        String module = getProcessModule(page);
        XModelObject fs = (module == null) ? null : (XModelObject)modules.get(module);
        return (fs == null) ? null : fs.getChildByPath(path.substring(1));
    }

	public StrutsBreakpointManager getBreakpointManager(XModelObject process)
	{
		StrutsProcessHelper helper = StrutsProcessHelper.getHelper(process);
		return (helper != null) ? helper.getBreakpointManager() : null; 
	}
}
