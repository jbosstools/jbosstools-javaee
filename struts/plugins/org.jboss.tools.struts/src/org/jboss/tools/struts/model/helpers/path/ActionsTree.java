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
package org.jboss.tools.struts.model.helpers.path;

import java.util.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.filesystems.FileSystemsHelper;
import org.jboss.tools.common.model.impl.trees.*;
import org.jboss.tools.struts.StrutsConstants;
import org.jboss.tools.struts.model.helpers.*;
import org.jboss.tools.struts.webprj.model.helpers.WebModulesHelper;
import org.jboss.tools.struts.webprj.pattern.*;

public class ActionsTree extends DefaultSiftedTree implements StrutsConstants {
    protected XModelObject thisConfig = null;
    protected Map<String,XModelObject> configs = new TreeMap<String,XModelObject>();
	protected Map<XModelObject,XModelObject[]> allconfigs = new HashMap<XModelObject,XModelObject[]>();
    protected Map<String,String> modules = new TreeMap<String,String>();
    protected WebModulesHelper helper = null;
    protected XModelObject root;
    protected boolean isThis = false;
    protected UrlPattern up;    

    public ActionsTree() {}

	public void dispose() {
		if (configs!=null) configs.clear();
	    configs = null;
		if (allconfigs!=null) allconfigs.clear();
		allconfigs = null;
		if (modules!=null) modules.clear();
	    modules = null;
	}

    public void setConstraint(Object object) {
        if(object instanceof Boolean) {
            isThis = ((Boolean)object).booleanValue();
            return;
        }
        XModel model = ((XModelObject)object).getModel();
        thisConfig = StrutsProcessStructureHelper.instance.getParentFile((XModelObject)object);
        up = StrutsProcessStructureHelper.instance.getUrlPattern((XModelObject)object);
        if(thisConfig != null && !thisConfig.getModelEntity().getName().startsWith(ENT_STRUTSCONFIG)) thisConfig = null;
        configs.clear();
        allconfigs.clear();
        modules.clear();
        root = FileSystemsHelper.getFileSystems(model);
        helper = WebModulesHelper.getInstance(model);
        String s = (thisConfig == null) ? "" : helper.getModuleForConfig(thisConfig);
        if(s == null) s = "";
        if(thisConfig != null) {
        	XModelObject[] cgs = helper.getConfigsForModule(thisConfig.getModel(), s);
        	if(cgs.length == 0) cgs = new XModelObject[]{thisConfig};
            configs.put(s, thisConfig);
            if(cgs.length > 1) allconfigs.put(thisConfig, cgs);
            for (int i = 0; i < cgs.length; i++)
            	modules.put(cgs[i].getPath(), s);
        }
        if(isThis) return;
        XModelObject[] ms = model.getByPath("Web").getChildren();
        for (int i = 0; i < ms.length; i++) {
            XModelObject cg =  model.getByPath(ms[i].getAttributeValue("model path"));
            if(cg == null || cg == thisConfig) continue;
            s = helper.getModuleForConfig(cg);
            if(s == null) s = "";
            if(configs.containsKey(s)) continue;
			XModelObject[] cgs = helper.getConfigsForModule(cg.getModel(), s);
			if(cgs.length == 0) cgs = new XModelObject[]{cg};
            configs.put(s, cg);
            if(cgs.length > 1) allconfigs.put(cg, cgs);
			for (int j = 0; j < cgs.length; j++)
            	modules.put(cgs[j].getPath(), s);
        }
    }

    public XModelObject getRoot() {
        return (isThis) ? thisConfig : root;
    }

    public boolean hasChildren(XModelObject object) {
        if(object == null || !object.isActive()) return false;
        return (object == root || modules.containsKey(object.getPath()));
    }

    public XModelObject[] getChildren(XModelObject object) {
        if(!hasChildren(object)) return new XModelObject[0];
        if(object == root) return (XModelObject[])configs.values().toArray(new XModelObject[0]);
        XModelObject am = object.getChildByPath("action-mappings");
        if(am == null) return new XModelObject[0];
        XModelObject[] cgs = (XModelObject[])allconfigs.get(object);
        if(cgs == null) return (am == null) ? new XModelObject[0] : am.getChildren();
        List<XModelObject> list = new ArrayList<XModelObject>();
        for (int i = 0; i < cgs.length; i++) {
			am = cgs[i].getChildByPath("action-mappings");
			if(am == null) continue;
			XModelObject[] cs = am.getChildren();
			for (int j = 0; j < cs.length; j++) list.add(cs[j]);
        }
        return list.toArray(new XModelObject[0]);
    }

    public String getPath(XModelObject o) {
        if(hasChildren(o)) return "";
		String p = "" + o.getAttributeValue(ATT_PATH);
		boolean slash = p.startsWith("/");
		p = up.getActionUrl(p);
		if(!slash && p.startsWith("/")) p = p.substring(1);
        String path = p;
        if(!o.isActive()) return path;
        XModelObject mp = o.getParent().getParent();
        if(mp == thisConfig ) return path;
        String module = (String)modules.get(mp.getPath());
        if(module == null) module = "";
        return up.getContextRelativePath(path, module);
    }

    public String getPresentation(XModelObject o) {
        if(o == root) return "Modules";
        String module = (String)modules.get(o.getPath());
        if(module != null) return (module.length() == 0) ? "<default>" : module;
        return o.getPresentationString();
    }

    public String getModule(XModelObject selectedAction) {
        if(!selectedAction.isActive()) return "";
        return (String)modules.get(selectedAction.getParent().getParent().getPath());
    }

    public XModelObject find(String path) {
		if(!path.startsWith("/")) path = "/" + path;
    	path = up.getActionPath(path);
        int i = path.indexOf("/", 1);
        String pathmodule = (i < 0) ? "" : path.substring(0, i);
        path = path.replace('/', '#');
        if(pathmodule.length() > 0) {
        	XModelObject a = findInModule(pathmodule, path.substring(i), false);
            if(a != null) return a;
        }
		XModelObject a = (thisConfig == null) ? null : findInConfig(thisConfig, path);
        if(a != null) return a;
		a = findInModule("", path, true);
        return a;
    }
    
    private XModelObject findInModule(String pathmodule, String path, boolean notThis) {
		XModelObject cg = (XModelObject)configs.get(pathmodule);
		if(cg == null) return null;
		if(notThis && cg == thisConfig) return null;
		return findInConfig(cg, path);
    }

    private XModelObject findInConfig(XModelObject cg, String path) {
		if(cg == null) return null;
		XModelObject c = cg.getChildByPath("action-mappings/" + path);
		if(c != null) return c;
		XModelObject[] cgs = (XModelObject[])allconfigs.get(cg);
		if(cgs == null) return null;
		for (int i = 0; i < cgs.length; i++) {
			c = cgs[i].getChildByPath("action-mappings/" + path);
			if(c != null) return c;
		}
		return null;    	
	}

	public XModelObject getParent(XModelObject object) {
		if(object == null) return null;
		String entity = object.getModelEntity().getName();
		if(entity.startsWith("StrutsAction") && object.isActive()) {
			XModelObject pcg = object.getParent().getParent();
			String path = pcg.getPath();
			String module = (String)modules.get(path);
			if(module == null) return pcg;
			XModelObject mcg = (XModelObject)configs.get(module);
			return mcg != null ? mcg : pcg;
		} 
		if(!isThis && entity.startsWith("StrutsConfig")) return root;
		return object.getParent();
	}

}
