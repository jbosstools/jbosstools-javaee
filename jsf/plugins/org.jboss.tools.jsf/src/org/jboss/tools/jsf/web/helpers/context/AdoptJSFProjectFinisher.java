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
package org.jboss.tools.jsf.web.helpers.context;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.impl.XModelClassLoader;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.jst.web.context.IImportWebProjectContext;
import org.jboss.tools.jst.web.project.WebModuleConstants;

public class AdoptJSFProjectFinisher {
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
	
    public void execute() throws Exception {
        filesystems.clear();
        workspace = context.getWebInfLocation();
        fss = model.getByPath("FileSystems");
        web = model.getByPath("Web");

        createBuildFileSystem();
        createWebInfFileSystem();
        createWebXMLFileSystem();
        createWebRootFileSystem();
        createSrcFileSystems();
        createClassesFileSystem();
        createLibFileSystems();
        
        String an = context.getApplicationName();
        if(an == null) an = context.getProjectName();
        fss.setAttributeValue("application name", an);
        
        fss.setModified(true);
        appendModules();
        model.save();
        XModelClassLoader cl = (XModelClassLoader)model.getModelClassLoader();
        cl.validate();
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
        getOrCreateFileSystem("WEB-INF", "%redhat.workspace%", true);
    }

    private void createWebXMLFileSystem() {
		File webxml = new File(context.getWebXmlLocation());
        String webxmlfolder = webxml.getParent();
        String relative = getRelativePath(workspace, webxmlfolder);
        if(relative == null || relative.startsWith("/..")) {
            String loc = (relative == null) ? webxmlfolder : "%redhat.workspace%" + relative;
            getOrCreateFileSystem("web-xml", loc, true);
            web.setAttributeValue("model path", "/" + webxml.getName());
        } else {
            web.setAttributeValue("model path", relative + "/" + webxml.getName());
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

    private void createSrcFileSystems() {
        createWebRootSrcFileSystem();
    }

    private void createWebRootSrcFileSystem() {
        XModelObject m = getDefaultModule();
        String[] srcs = context.getExistingSources();
        List<String> l = new ArrayList<String>();
		if(srcs != null) for (int i = 0; i < srcs.length; i++) l.add(srcs[i]);
		String s = m.getAttributeValue("java src");
		if(!l.contains(s)) l.add(s);
        srcs = l.toArray(new String[0]);
        String srcAttr = "";
        int j = 0;
        for (int i = 0; i < srcs.length; i++) {
			srclocation = srcs[i];
			if(srclocation.length() == 0) continue;
			String loc = getFileSystemLocation(workspace, srclocation);
			++j;
			String fsn = (j == 1) ? "src" : "src" + j; 
			getOrCreateFileSystem(fsn, loc, false);
			if(j > 1) srcAttr += ",";
			srcAttr += fsn;
        }
        m.setAttributeValue("src file system", srcAttr);
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
            getOrCreateFileSystem("lib-" + n, loc + "/" + n, "hidden=yes", true, true);
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
        XModelObject[] ms = web.getChildren("WebJSFModule");
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
        return (relative == null) ? path : "%redhat.workspace%" + relative;
    }

}
