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
package org.jboss.tools.struts.webprj.model.helpers.adopt;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Map;

import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelConstants;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.impl.Libs;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.jst.web.context.IImportWebProjectContext;
import org.jboss.tools.jst.web.project.WebModuleConstants;
import org.jboss.tools.jst.web.project.helpers.NewWebProjectHelper;
import org.jboss.tools.struts.webprj.model.helpers.sync.SortFileSystems;

public class AdoptProjectFinisher {
	protected IImportWebProjectContext context = null;
    protected XModel model = null;
    protected XModelObject web;
    protected XModelObject fss;
    protected String workspace = null;
    protected String srclocation = "";
    protected Map<String,XModelObject> filesystems = new HashMap<String,XModelObject>();

	public void setContext(XModel model, IImportWebProjectContext context) {
		this.context = context;
		this.model = model;
	}
	
    public void execute() throws XModelException {
        filesystems.clear();
        workspace = context.getWebInfLocation();

        fss = model.getByPath("FileSystems");
        web = model.getByPath("Web");

        createBuildFileSystem();
        createWebInfFileSystem();
        createWebXMLFileSystem();
        createInfFileSystems();
        createWebRootFileSystem();
        createModuleFileSystems();
        createSrcFileSystems();
        createClassesFileSystem();
        createLibFileSystems();
        
        String an = context.getApplicationName();
        if(an == null) an = context.getProjectName();
        fss.setAttributeValue("application name", an);
        
        fss.setModified(true);
        appendModules();
		SortFileSystems.sort(model);
        model.save();
		NewWebProjectHelper.updateOverlapped(model);
    }

    private XModelObject getOrCreateFileSystem(String name, String location, boolean req) {
        return getOrCreateFileSystem(name, location, null, req, false);
    }

    private XModelObject getOrCreateFileSystem(String name, String location, String info) {
        return getOrCreateFileSystem(name, location, info, true, false);
    }

    private XModelObject getOrCreateFileSystem(String name, String location, String info, boolean req, boolean jar) {
        XModelObject fs = (req) ? null : (XModelObject)filesystems.get(location);
        if(fs != null) return fs;
        String entity = (!jar) ? "FileSystemFolder" : "FileSystemJar";
        fs = model.createModelObject(entity, null);
        fs.setAttributeValue("name", name);
        fs.setAttributeValue("location", location);
        if(info != null) fs.setAttributeValue("info", info);
        fss.addChild(fs);
        filesystems.put(location, fs);
        return fs;
    }

    private void createWebInfFileSystem() {
        getOrCreateFileSystem("WEB-INF", XModelConstants.WORKSPACE_REF, true);
    }

    private void createWebXMLFileSystem() {
		File webxml = new File(context.getWebXmlLocation());
        String webxmlfolder = webxml.getParent();
        String relative = getRelativePath(workspace, webxmlfolder);
        if(relative == null || relative.startsWith("/..")) {
            String loc = (relative == null) ? webxmlfolder : XModelConstants.WORKSPACE_REF + relative;
            getOrCreateFileSystem("web-xml", loc, true);
            web.setAttributeValue("model path", "/" + webxml.getName());
        } else {
            web.setAttributeValue("model path", relative + "/" + webxml.getName());
        }
    }

    private void createInfFileSystems() {
        XModelObject m = getDefaultModule();
        if(m != null) createInfFileSystem(m, "default");
        XModelObject[] ms = context.getModules();
        for (int i = 0; i < ms.length; i++) {
            if(ms[i].getAttributeValue("name").length() == 0) continue;
            createInfFileSystem(ms[i], ms[i].getAttributeValue("name").substring(1));
        }
    }

    private void createInfFileSystem(XModelObject m, String fsn) {
        File config = new File(m.getAttributeValue("path on disk"));
        String configLoc = config.getParent();
        String relative = getRelativePath(workspace, configLoc);
        if(relative == null || relative.startsWith("/..")) {
            String loc = (relative == null) ? configLoc : XModelConstants.WORKSPACE_REF + relative;
            getOrCreateFileSystem(fsn + "-config-xml", loc, false);
            m.setAttributeValue("model path", "/" + config.getName());
        } else {
            m.setAttributeValue("model path", relative + "/" + config.getName());
        }
        XModelObject[] cs = m.getChildren();
        for (int i = 0; i < cs.length; i++) {
			createInfFileSystem(cs[i], fsn);
        }
    }

    private XModelObject getDefaultModule() {
        XModelObject[] ms = context.getModules();
        for (int i = 0; i < ms.length; i++)
          if(ms[i].getAttributeValue("name").length() == 0) return ms[i];
        return null;
    }

    private void createWebRootFileSystem() {
        XModelObject m = getDefaultModule();
        if(m == null) return;
        String loc = getFileSystemLocation(workspace, m.getAttributeValue("root"));
        m.setAttributeValue(WebModuleConstants.ATTR_ROOT_FS, "WEB-ROOT");
        getOrCreateFileSystem("WEB-ROOT", loc, "Content-Type=Web");
    }

    private void createModuleFileSystems() {
        XModelObject[] ms = context.getModules();
        for (int i = 0; i < ms.length; i++) {
            if(ms[i].getAttributeValue("name").length() == 0) continue;
            createModuleFileSystem(ms[i]);
        }
    }

    private void createModuleFileSystem(XModelObject m) {
        String n = m.getAttributeValue("name");
        String fsn = n.substring(1).replace('/', '#');
        String loc = getFileSystemLocation(workspace, m.getAttributeValue("root"));
        m.setAttributeValue(WebModuleConstants.ATTR_ROOT_FS, fsn);
        getOrCreateFileSystem(fsn, loc, "Content-Type=Web,Struts-Module=" + n);
    }

    private void createSrcFileSystems() {
        createWebRootSrcFileSystem();
        XModelObject[] ms = context.getModules();
        for (int i = 0; i < ms.length; i++) {
            if(ms[i].getAttributeValue("name").length() == 0) continue;
            createModuleSrcFileSystem(ms[i]);
        }
    }

    private void createWebRootSrcFileSystem() {
        XModelObject m = getDefaultModule();
        srclocation = m.getAttributeValue("java src");
        if(srclocation.length() == 0) return;
        String loc = getFileSystemLocation(workspace, srclocation);
        String fsn = "src";
        m.setAttributeValue("src file system", fsn);
        getOrCreateFileSystem("src", loc, false);
    }

    private void createModuleSrcFileSystem(XModelObject m) {
        String src = m.getAttributeValue("java src");
        String n = m.getAttributeValue("name").substring(1);
        if(src.length() == 0 || src.equalsIgnoreCase(srclocation)) return;
        String loc = getFileSystemLocation(workspace, src);
        String fsn = "src-" + n;
        m.setAttributeValue("src file system", fsn);
        getOrCreateFileSystem(fsn, loc, false);
    }

    private void createLibFileSystems() {
		String lib = context.getLibLocation();
        if(lib == null || lib.length() == 0) return;
        File f = new File(lib);
        if(!f.isDirectory()) return;
        String loc = getFileSystemLocation(workspace, lib);
		getOrCreateFileSystem("lib", loc, true);
        File[] jars = f.listFiles(new FileFilter() {
            public boolean accept(File file) {
                if (!file.isFile()) return false;
                String name = file.getName().toLowerCase();
                return name.endsWith(".jar") || name.endsWith(".zip");
            }
        });
        if(jars != null) for (int i = 0; i < jars.length; i++) {
            String n = jars[i].getName();
            getOrCreateFileSystem(Libs.LIB_PREFIX + n, loc + "/" + n, "hidden=yes", true, true);
        }
    }

    private void createClassesFileSystem() {
		String classes = context.getClassesLocation();
        if(classes == null || classes.length() == 0) return;
        if(!new File(classes).isDirectory()) return;
        String loc = getFileSystemLocation(workspace, classes);
        getOrCreateFileSystem("classes", loc, false);
    }

    private void createBuildFileSystem() {
		String build = context.getBuildXmlLocation();
		if(build == null || build.length() == 0) return;
		File f = new File(build);
		if(f.isFile()) {
			f = f.getParentFile();
			build = f.getAbsolutePath();
		}
        if(!new File(build).isDirectory()) return;
        String loc = getFileSystemLocation(workspace, build);
        getOrCreateFileSystem("build", loc, false);
    }

    protected void appendModules() {
        XModelObject[] ms = web.getChildren(WebModuleConstants.ENTITY_WEB_MODULE);
        for (int i = 0; i < ms.length; i++) ms[i].removeFromParent();
        ms = context.getModules();
        for (int i = 0; i < ms.length; i++) web.addChild(ms[i]);
        web.setModified(true);
    }

    public static String getRelativePath(String rootpath, String path) {
    	return FileUtil.getRelativePath(rootpath, path);
    }

    public static String getFileSystemLocation(String rootpath, String path) {
        path = path.replace('\\', '/');
        String relative = getRelativePath(rootpath, path);
        return (relative == null) ? path : XModelConstants.WORKSPACE_REF + relative;
    }

}

