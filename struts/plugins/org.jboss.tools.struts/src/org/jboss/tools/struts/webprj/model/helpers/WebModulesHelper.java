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
package org.jboss.tools.struts.webprj.model.helpers;

import java.util.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.filesystems.FileSystemsHelper;
import org.jboss.tools.common.model.filesystems.impl.FileAnyImpl;
import org.jboss.tools.common.model.filesystems.impl.FileSystemImpl;
import org.jboss.tools.common.model.util.*;
import org.jboss.tools.common.meta.action.impl.handlers.*;
import org.jboss.tools.jst.web.project.*;
import org.jboss.tools.struts.webprj.pattern.*;
import org.jboss.tools.struts.webprj.resource.ResourceMapping;

public class WebModulesHelper implements WebModuleConstants {
	public static String ENT_STRUTS_WEB_MODULE = ENTITY_WEB_MODULE;

    public static WebModulesHelper getInstance(XModel model) {
		WebModulesHelper instance = (WebModulesHelper)model.getManager("WebModulesHelper");
        if(instance == null) {
            instance = new WebModulesHelper();
			instance.setModel(model);
            model.addManager("WebModulesHelper", instance);
        }
        instance.update();
        return instance;
    }

	private WebProject webProject;
    protected long timestamp = -700;
    private Set<String> modules = new HashSet<String>();
    private Map<String,String> configs = new HashMap<String,String>();
    private PatternLoader patterns = new PatternLoader();
    private ResourceMapping resources = new ResourceMapping(this);
    
    private void setModel(XModel model) {
    	webProject = WebProject.getInstance(model);    	
    }
    
    public XModel getModel() {
    	return webProject.getModel();
    }
    
    public WebProject getWebProject() {
    	return webProject;
    }

    public Set<String> getModules() {
        return modules;
    }

    public String getModuleForConfig(XModelObject cfg) {
        return (String)configs.get(XModelObjectLoaderUtil.getResourcePath(cfg));
    }

    public String getModuleForPath(String path, String current) {
        return (path == null || !path.startsWith("/")) ? null :
               (path.startsWith("//")) ? "" :
               patterns.getUrlPatternForModule(current).getModule(path, modules, current);
    }
    
    public XModelObject getConfigForModule(XModel model, String module) {
        XModelObject m = model.getByPath("Web/" + WebModuleImpl.toWebModulePathPart(module));
        return (m == null) ? null : model.getByPath(m.getAttributeValue("model path"));
    }
    
    static XModelObject[] EMPTY = new XModelObject[0];

	public XModelObject[] getConfigsForModule(XModel model, String module) {
		XModelObject m = model.getByPath("Web/" + WebModuleImpl.toWebModulePathPart(module));
		if(m == null) return EMPTY;
		XModelObject[] cs = m.getChildren();
		String p = m.getAttributeValue(ATTR_MODEL_PATH);
		XModelObject c = p.length() == 0 ? null : model.getByPath(p);
		if(cs.length == 0) {
			return c == null ? EMPTY : new XModelObject[]{c};
		}
		List<XModelObject> list = new ArrayList<XModelObject>();
		if(c != null) list.add(c);
		for (int i = 0; i < cs.length; i++) {
			p = cs[i].getAttributeValue(ATTR_MODEL_PATH);
			c = p.length() == 0 ? null : model.getByPath(p);
			if(c != null) list.add(c);
		}
		return list.toArray(new XModelObject[0]);
	}

    public XModelObject getRootFileSystemForModule(XModel model, String module) {
        XModelObject m = model.getByPath("Web/" + WebModuleImpl.toWebModulePathPart(module));
        return (m == null) ? null : model.getByPath("FileSystems/" + m.getAttributeValue(ATTR_ROOT_FS));
    }
    
    public String getWebRootLocation() {
    	XModelObject fs = FileSystemsHelper.getWebRoot(getModel());
    	if(fs == null) fs = getRootFileSystemForModule(getModel(), "");
    	if(!(fs instanceof FileSystemImpl)) return null;
		return ((FileSystemImpl)fs).getAbsoluteLocation();
    }
    
    public PatternLoader getPatternLoader() {
    	return patterns;
    }
    
	public UrlPattern getUrlPattern(String module) {
		return patterns.getUrlPatternForModule(module);
	}
	
	public ResourceMapping getResourceMapping() {
		return resources;
	}

    public void update() {
        XModelObject root = getModel().getByPath("Web");
        long ts = (root == null) ? -111L : root.getTimeStamp();
        if(timestamp != ts) {
			timestamp = ts;
			synchronized(this) {
        		reload(root);
			}
        } 
    }
    
    private void reload(XModelObject root) {
        modules.clear();
        configs.clear();
        timestamp = (root == null) ? -111L : root.getTimeStamp();
        if(root == null) return;
        XModelObject[] ms = root.getChildren(ENT_STRUTS_WEB_MODULE);
        for (int i = 0; i < ms.length; i++) {
            String n = ms[i].getAttributeValue(ATTR_NAME);
            String c = ms[i].getAttributeValue(ATTR_MODEL_PATH);
            modules.add(n);
            configs.put(c, n);
            XModelObject[] cs = ms[i].getChildren();
            for (int k = 0; k < cs.length; k++) {
				c = cs[k].getAttributeValue(ATTR_MODEL_PATH);
				configs.put(c, n);
            }
        }
    }

    public XModelObject setModule(XModel model, String oldname, String name, String modelpath, String rootfilesystem) {
        XModelObject web = model.getByPath("Web");
        XModelObject module = (oldname == null || oldname.length() == 0) ? null : web.getChildByPath(oldname);
        if(module == null) module = web.getChildByPath(name);
        XModelObject src = FileSystemsHelper.getFileSystem(model, "src");
        if(module == null) {
            Properties p = new Properties();
            p.setProperty("name", name);
            p.setProperty(ATTR_MODEL_PATH, modelpath);
            p.setProperty(ATTR_ROOT_FS, rootfilesystem);
            if(src != null) p.setProperty(ATTR_SRC_FS, "src");
            module = model.createModelObject(ENT_STRUTS_WEB_MODULE, p);
            DefaultCreateHandler.addCreatedObject(web, module, -1);
        } else {
            model.changeObjectAttribute(module, "name", name);
            model.changeObjectAttribute(module, ATTR_MODEL_PATH, modelpath);
            model.changeObjectAttribute(module, ATTR_ROOT_FS, rootfilesystem);
            if(src != null) model.changeObjectAttribute(module, ATTR_SRC_FS, "src");
        }
        reload(web);
        return module;
    }
    
    public XModelObject[] getAllConfigs() {
    	update();
		XModelObject web = getModel().getByPath("Web");
		if(web == null) return new XModelObject[0];
   		List<XModelObject> list = new ArrayList<XModelObject>();
		XModelObject[] ms = web.getChildren(ENT_STRUTS_WEB_MODULE);
		for (int i = 0; i < ms.length; i++) {
//			String n = ms[i].getAttributeValue(ATTR_NAME);
			String c = ms[i].getAttributeValue(ATTR_MODEL_PATH);
			if(c.length() == 0) continue;
			XModelObject o = getModel().getByPath(c);
			if(o != null) list.add(o);
            XModelObject[] cs = ms[i].getChildren();
            for (int k = 0; k < cs.length; k++) {
				c = cs[k].getAttributeValue(ATTR_MODEL_PATH);
				o = getModel().getByPath(c);
				if(o != null) list.add(o);
            }
		}
		return list.toArray(new XModelObject[0]);
    }
    
    public XModelObject getFileSystem(String module) {
    	XModelObject m = getModel().getByPath("Web/" + module.replace('/', '#'));
    	return (m == null) ? null : getModel().getByPath("FileSystems/" + m.getAttributeValue(ATTR_ROOT_FS));
    }
    
    public XModelObject getWebRoot() {
    	XModelObject fs = getFileSystem("");
    	return fs != null ? fs : FileSystemsHelper.getWebRoot(getModel());
    }
    
    public String getModuleForFileSystem(XModelObject fs) {
		XModelObject web = getModel().getByPath("Web");
		if(web == null) return "";
		XModelObject[] ms = web.getChildren(ENT_STRUTS_WEB_MODULE);
		String n = fs.getAttributeValue("name");
		for (int i = 0; i < ms.length; i++) {
			if(n.equals(ms[i].getAttributeValue(ATTR_ROOT_FS))) return ms[i].getAttributeValue("name");
		}
		return "";
    }

	// key - module name, value file system model object
	public Map<String,XModelObject> getWebFileSystems() {
		Map<String,XModelObject> map = new HashMap<String,XModelObject>();
		XModelObject web = getModel().getByPath("Web");
		if(web == null) return map;
		XModelObject[] ms = web.getChildren(ENT_STRUTS_WEB_MODULE);
		for (int i = 0; i < ms.length; i++) {
			String module = ms[i].getAttributeValue("name");
			String fsn = ms[i].getAttributeValue(ATTR_ROOT_FS);
			XModelObject fs = FileSystemsHelper.getFileSystem(getModel(), fsn);
			if(fs != null) map.put(module, fs);
		}
		return map;
	}

	public String getQualifiedPagePath(XModelObject page) {
		if(!page.isActive()) return null;
		XModelObject fs = page.getParent();
		String relative = "/" + FileAnyImpl.toFileName(page);
		while(fs != null && fs.getFileType() != XModelObject.SYSTEM) {
			relative = "/" + fs.getAttributeValue("name") + relative;
			fs = fs.getParent();
		} 
		return (fs == null) ? null : getModuleForFileSystem(fs) + relative;
	}

}
