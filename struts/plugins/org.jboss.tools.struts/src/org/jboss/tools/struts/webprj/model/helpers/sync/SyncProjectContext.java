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
package org.jboss.tools.struts.webprj.model.helpers.sync;

import java.io.File;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.osgi.util.NLS;

import org.jboss.tools.common.meta.action.SpecialWizard;
import org.jboss.tools.common.meta.action.SpecialWizardFactory;
import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateHandler;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultRemoveHandler;
import org.jboss.tools.common.model.ServiceDialog;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelConstants;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.XFileObject;
import org.jboss.tools.common.model.undo.XTransactionUndo;
import org.jboss.tools.common.model.undo.XUndoManager;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.model.util.XMLUtil;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.common.model.util.XModelObjectUtil;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.common.model.project.IModelNature;
import org.jboss.tools.common.model.project.IWatcherContributor;
import org.jboss.tools.struts.StrutsConstants;
import org.jboss.tools.struts.StrutsModelPlugin;
import org.jboss.tools.struts.StrutsProjectUtil;
import org.jboss.tools.struts.messages.StrutsUIMessages;
import org.jboss.tools.jst.web.model.helpers.WebAppHelper;
import org.jboss.tools.jst.web.project.WebModuleConstants;
import org.jboss.tools.jst.web.project.WebModuleImpl;
import org.jboss.tools.struts.webprj.model.helpers.WebModulesHelper;
import org.jboss.tools.struts.webprj.model.helpers.adopt.AdoptProjectContext;
import org.jboss.tools.struts.webprj.model.helpers.adopt.AdoptProjectFinisher;

public class SyncProjectContext implements WebModuleConstants, IWatcherContributor {
	AdoptProjectContext adoptContext = new AdoptProjectContext();

    protected XModel model;
    protected XModelObject webxml;
    protected XModelObject web;
    protected File webinf;
    protected Properties data = new Properties();
    protected Map<String,String> describedModules = new HashMap<String,String>();
    protected Map<String,XModelObject> installedModules = new HashMap<String,XModelObject>();
    protected ArrayList<XModelObject> modules = new ArrayList<XModelObject>();
    protected Map<String,XModelObject> modulesMap = new TreeMap<String,XModelObject>();
    protected String workspace;

    public SyncProjectContext() {
        data.put("describedModules", describedModules); //$NON-NLS-1$
        data.put("installedModules", installedModules); //$NON-NLS-1$
        data.put("modules", modules); //$NON-NLS-1$
    }

    public void setModel(XModel model) {
        this.model = model;
        webxml = WebAppHelper.getWebApp(model);
        web = model.getByPath("Web"); //$NON-NLS-1$
		workspace = new File(XModelConstants.getWorkspace(model)).getAbsolutePath().replace('\\', '/');
        /// hardcode
        webinf = new File(XModelConstants.getWorkspace(model));
    }
    
    XModelObject getWebObject() {
    	if(web != null || model == null) return web;
    	web = model.getByPath("Web"); //$NON-NLS-1$
    	return web;
    }

    public XModelObject getWebXML() {
    	return webxml;
    }

    public void update(boolean merge) {
        updateDescribedModulesInfo();
        updateInstalledModulesInfo();
        if(merge) updateModulesInfo();
    }

    public boolean isWebXMLFound() {
        return (webxml != null);
    }

    public boolean isWebXMLCorrect() {
        return (webxml != null && !"yes".equals(webxml.get("isIncorrect"))); //$NON-NLS-1$ //$NON-NLS-2$
    }

    private boolean updateDescribedModulesInfo() {
        boolean modified = false;
        String[][] ms = AdoptProjectContext.getModules(webxml);
        Map<String,String> d = new HashMap<String, String>();
        d.putAll(describedModules);
        for (int i = 0; i < ms.length; i++) {
            String s = (String)d.remove(ms[i][0]);
            if(s != null && s.equals(ms[i][1])) continue;
            modified = true;
            describedModules.put(ms[i][0], ms[i][1]);
        }
        modified |= d.size() > 0;
        Iterator<String> it = d.keySet().iterator();
        if(it==null) return false;
        while(it.hasNext()) describedModules.remove(it.next());
        return modified;
    }

    private boolean updateInstalledModulesInfo() {
        boolean modified = false;
        if(getWebObject() == null) {
        	return false;
        }
        XModelObject[] cs = web.getChildren(WebModulesHelper.ENT_STRUTS_WEB_MODULE);
        Map<String,XModelObject> d = new HashMap<String, XModelObject>();
        d.putAll(installedModules);
        for (int i = 0; i < cs.length; i++) {
            String n = cs[i].getAttributeValue("name"); //$NON-NLS-1$
            XModelObject c = (XModelObject)d.remove(n);
            if(c == cs[i]) continue;
            modified = true;
            installedModules.put(n, cs[i]);
        }
        modified |= d.size() > 0;
        Iterator<String> it = d.keySet().iterator();
        while(it.hasNext()) installedModules.remove(it.next());
        return modified;
    }

    private void updateModulesInfo() {
        modulesMap.clear();
        Map<String,XModelObject> d = new HashMap<String, XModelObject>();
        d.putAll(installedModules);
        Iterator<String> it = describedModules.keySet().iterator();
        while(it.hasNext()) {
            String n = it.next();
            String uri = describedModules.get(n);
			WebModuleImpl m = (WebModuleImpl)d.remove(n);
            if(m == null) {
                m = (WebModuleImpl)adoptContext.createModuleInfo(model, n, uri, webinf, false);
                m.set("state", "not installed"); //$NON-NLS-1$ //$NON-NLS-2$
            } else {
                setAbsolutePaths(m);
				m = (WebModuleImpl)m.copy();
                ///m.setAttributeValue(ATTR_URI, uri);
                m.setURI(uri);
                m.set("state", "installed"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            modulesMap.put(n, m);
        }
        it = d.keySet().iterator();
        while(it.hasNext()) {
            String n = it.next();
            XModelObject m = d.get(n);
            setAbsolutePaths(m);
            m = m.copy();
            m.set("state", "deleted"); //$NON-NLS-1$ //$NON-NLS-2$
            modulesMap.put(n, m);
        }
        refillModuleList();
    }

    private void refillModuleList() {
        modules.clear();
        String[] ms = (String[])modulesMap.keySet().toArray(new String[0]);
        for (int i = 0; i < ms.length; i++) {
        	XModelObject o = (XModelObject)modulesMap.get(ms[i]);
        	modules.add(o);
        	XModelObject[] oc = o.getChildren();
        	for (int k = 0; k < oc.length; k++) modules.add(oc[k]);
        } 
    }

    private void setAbsolutePaths(XModelObject m) {
        String rfs = m.getAttributeValue(ATTR_ROOT_FS);
        XModelObject fs = model.getByPath("FileSystems/" + rfs); //$NON-NLS-1$
        String rp = (fs == null) ? "" : XModelObjectUtil.getExpandedValue(fs, "location", null); //$NON-NLS-1$ //$NON-NLS-2$
        m.setAttributeValue(ATTR_ROOT, toCanonicalPath(rp));

        String dp = getPathOnDisk(m.getAttributeValue(ATTR_MODEL_PATH));
        m.setAttributeValue(ATTR_DISK_PATH, toCanonicalPath(dp));

        String sfs = m.getAttributeValue(ATTR_SRC_FS);
        fs = (sfs == null) ? null : model.getByPath("FileSystems/" + sfs); //$NON-NLS-1$
        String sp = (fs == null) ? getDefaultJavaSrc() : XModelObjectUtil.getExpandedValue(fs, "location", null); //$NON-NLS-1$
        m.setAttributeValue(ATTR_SRC_PATH, toCanonicalPath(sp));
        
        XModelObject[] cs = m.getChildren();        
        for (int i = 0; i < cs.length; i++) {
			dp = getPathOnDisk(cs[i].getAttributeValue(ATTR_MODEL_PATH));
			cs[i].setAttributeValue(ATTR_DISK_PATH, toCanonicalPath(dp));
        }
    }

    private String getDefaultJavaSrc() {
		String[] src = EclipseResourceUtil.getJavaProjectSrcLocations((IProject)model.getProperties().get("project")); //$NON-NLS-1$
		return (src.length == 1) ? src[0] : ""; //$NON-NLS-1$
    }

    private static String toCanonicalPath(String path) {
        if(path == null) return path;
        int k = -1;
        while((k = path.indexOf("//")) >= 0) path = path.substring(0, k) + path.substring(k + 1); //$NON-NLS-1$
        if(path.indexOf("/..") < 0) return path; //$NON-NLS-1$
        while(true) {
            int i = path.indexOf("/.."); //$NON-NLS-1$
            if(i < 0) return path;
            String pp = path.substring(0, i);
            int j = pp.lastIndexOf("/"); //$NON-NLS-1$
            if(j < 0) return path;
            path = path.substring(0, j) + path.substring(i + 3);
        }
    }

    String getPathOnDisk(String modelPath) {
        XModelObject o = model.getByPath(modelPath);
        XModelObject fs = o;
        while(fs != null && fs.getFileType() != XFileObject.SYSTEM) fs = fs.getParent();
        if(fs == null) return ""; //$NON-NLS-1$
        return XModelObjectUtil.getExpandedValue(fs, "location", null) + modelPath; //$NON-NLS-1$
    }

    void addModule(String name, String path) throws XModelException {
        File f = new File(path);
        if(!f.isFile()) throw new XModelException(MessageFormat.format(StrutsUIMessages.SyncProjectContext_FileDoesNotExist, path));
        String uri = "/WEB-INF/" + f.getName(); //$NON-NLS-1$
        if(name.length() > 0 && !name.startsWith("/")) name = "/" + name; //$NON-NLS-1$ //$NON-NLS-2$
        XModelObject m = (XModelObject)modulesMap.get(name);
        if(m != null) {
			if("deleted".equals(m.get("state"))) { //$NON-NLS-1$ //$NON-NLS-2$
        		if(m != null) throw new XModelException(MessageFormat.format(StrutsUIMessages.SyncProjectContext_ModuleExists, getModuleDisplayName(name)));
			}
			checkStrutsConfig(path);
			XModelObject cc = m.getModel().createModelObject(WebModuleConstants.ENTITY_WEB_CONFIG, null);
			cc.setAttributeValue(ATTR_URI, uri);
			cc.setAttributeValue(ATTR_DISK_PATH, path);
			cc.set("state", "added"); //$NON-NLS-1$ //$NON-NLS-2$
			XModelObject cc1 = m.getChildByPath(cc.getPathPart());
			if(cc1 != null) {
				if("deleted".equals(cc1.get("state"))) { //$NON-NLS-1$ //$NON-NLS-2$
					XModelObjectLoaderUtil.mergeAttributes(cc1, cc, false);
					cc1.set("state", "added"); //$NON-NLS-1$ //$NON-NLS-2$
				} else {
					throw new XModelException(StrutsUIMessages.SyncProjectContext_ConfigFileIsUsed);
				}
			} else {
				m.addChild(cc);
			}
			refillModuleList();
			return;
        }
        m = findModuleByPathOnDisk(path);
        if(m != null) {
            if(!"deleted".equals(m.get("state"))) //$NON-NLS-1$ //$NON-NLS-2$
				throw new XModelException(StrutsUIMessages.SyncProjectContext_PathUsedByAnotherModule);
           	modules.remove(m);
           	modulesMap.remove(name);
        } else {
            checkStrutsConfig(path);
        }
        m = adoptContext.createModuleInfo(model, name, uri, webinf, true);
        if(m.getAttributeValue(ATTR_SRC_PATH).length() == 0) {
        	String src = getDefaultJavaSrc();
        	if(src != null && src.length() > 0) m.setAttributeValue(ATTR_SRC_PATH, src);
        }
        m.setAttributeValue(ATTR_DISK_PATH, path);
        m.set("state", "added"); //$NON-NLS-1$ //$NON-NLS-2$
        modulesMap.put(name, m);
        refillModuleList();
    }
    
    String getNewModuleError(String name, String path) {
        File f = new File(path);
        if(!f.isFile()) return "File " + path + " does not exist."; //$NON-NLS-1$ //$NON-NLS-2$
//        String uri = "/WEB-INF/" + f.getName();
        if(name.length() > 0 && !name.startsWith("/")) name = "/" + name; //$NON-NLS-1$ //$NON-NLS-2$
        XModelObject m = (XModelObject)modulesMap.get(name);
        if(m != null) {
			if("deleted".equals(m.get("state"))) { //$NON-NLS-1$ //$NON-NLS-2$
        		if(m != null) return "Module " + getModuleDisplayName(name) + " exists."; //$NON-NLS-1$ //$NON-NLS-2$
			}
			try {
				checkStrutsConfig(path);
			} catch (Exception e) {
                //Do not log this exception. It is thrown to be showed 
				//in wizard as user input error.
				return e.getMessage();
			}
        }
        m = findModuleByPathOnDisk(path);
        if(m != null) {
            if(!"deleted".equals(m.get("state"))) //$NON-NLS-1$ //$NON-NLS-2$
				return "The path is used by another module."; //$NON-NLS-1$
        } else {
			try {
				checkStrutsConfig(path);
			} catch (Exception e) {
                //Do not log this exception. It is thrown to be showed 
				//in wizard as user input error.
				return e.getMessage();
			}
        }
        return null;
    }

    private XModelObject findModuleByPathOnDisk(String path) {
        for (int i = 0; i < modules.size(); i++) {
            XModelObject m = (XModelObject)modules.get(i);
            String p1 = ("" + path).replace('\\', '/'); //$NON-NLS-1$
            String p2 = ("" + m.getAttributeValue(ATTR_DISK_PATH)).replace('\\', '/'); //$NON-NLS-1$
            if(p1.equalsIgnoreCase(p2)) return m;
        }
        return null;
    }

    private void checkStrutsConfig(String path) throws XModelException {
        File f = new File(path);
        if(!f.isFile()) throw new XModelException(StrutsUIMessages.SyncProjectContext_NotAPathToAFile);
        String s = FileUtil.readFile(f);
        boolean is11 = s.indexOf(StrutsConstants.DOC_PUBLICID_11) >= 0;
        boolean is12 = s.indexOf(StrutsConstants.DOC_PUBLICID_12) >= 0;
        if(!is11 && !is12)
          throw new XModelException(StrutsUIMessages.SyncProjectContext_FileIsNotStruts11Or12);
        String[] es = XMLUtil.getXMLErrors(new StringReader(s), false); //never validate dtd
        if(es != null && es.length > 0) {
          String version = (is11) ? "1.1" : "1.2"; //$NON-NLS-1$ //$NON-NLS-2$
          throw new XModelException(MessageFormat.format(
				StrutsUIMessages.SyncProjectContext_ConfigFileNotCorrect, version, es[0]));
        }
    }

    public boolean apply() throws XModelException {
        validateAttributes();
        if(!checkNewPaths()) return false;
        XUndoManager undo = model.getUndoManager();
        XTransactionUndo u = new XTransactionUndo("Synchronize modules", XTransactionUndo.EDIT); //$NON-NLS-1$
        undo.addUndoable(u);
        try {
            transaction();
        } catch (Exception e) {
            undo.rollbackTransactionInProgress();
            throw new XModelException(e);
        } finally {
            u.commit();
        }
        if(webxml != null && webxml.isModified()) {
        	XActionInvoker.invoke("SaveActions.Save", webxml, null); //$NON-NLS-1$
        }
        return true;
    }

    XModelObject servlet = null;
    HashMap<String,XModelObject> oldFileSystems = new HashMap<String,XModelObject>();
    HashMap<String,XModelObject> newFileSystems = new HashMap<String,XModelObject>();
    Map<String,String> replacedSrc = new HashMap<String,String>();

    private void transaction() throws Exception {
    	XModelObject fss = model.getByPath("FileSystems"); //$NON-NLS-1$
    	if(fss == null) return;
    	long ts0 = fss.getTimeStamp() + model.getByPath("Web").getTimeStamp(); //$NON-NLS-1$
        ///validateAttributes();
        collectOldFileSystems();
        servlet = StrutsWebHelper.getServlet(webxml);
        for (String n: modulesMap.keySet()) {
//            String view_n = getModuleDisplayName(n);
            XModelObject o = (XModelObject)modulesMap.get(n);
            if(executeDeleteA(o) == 1) continue;
            String uri = ((WebModuleImpl)o).getURI();
///            if(!uri.startsWith("/")) throw new Exception("Incorrect URI " + uri + " for module " + view_n + ".");
            StrutsWebHelper.revalidateInitParam(servlet, n, uri);
            revalidateModule(o);
        }
        executeDelete();
		SortFileSystems.sort(model);
        model.update();
		long ts1 = fss.getTimeStamp() + model.getByPath("Web").getTimeStamp(); //$NON-NLS-1$
		if(ts1 != ts0) updateEclipseClassPath();
    }
    
    private boolean checkNewPaths() {
        Set<String> checkedPaths = new HashSet<String>();
        for (String n: modulesMap.keySet()) {
//            String view_n = getModuleDisplayName(n);
            XModelObject o = (XModelObject)modulesMap.get(n);
			if("deleted".equals(o.get("state"))) continue; //$NON-NLS-1$ //$NON-NLS-2$
			String rootPath = o.getAttributeValue(ATTR_ROOT);
			String srcPath = o.getAttributeValue(ATTR_SRC_PATH);
			if(!checkFolder(rootPath, checkedPaths) 
					|| !checkFolder(srcPath, checkedPaths)) return false;
        }    	
    	return true;
    }
    private boolean checkFolder(String path, Set<String> checkedPaths) {
        ServiceDialog d = model.getService();
		if(path != null && path.length() > 0 && !new File(path).isDirectory()) {
	    	if(checkedPaths.contains(path)) return true;
			checkedPaths.add(path);
			int q = d.showDialog(StrutsUIMessages.QUESTION, NLS.bind(StrutsUIMessages.FOLDER_DOESNT_EXIST, path), new String[]{StrutsUIMessages.YES, StrutsUIMessages.NO}, null, ServiceDialog.QUESTION); //$NON-NLS-3$
			if(q != 0) return false;
		}
    	return true;
    }
    
    private int executeDeleteA(XModelObject o) {
		XModelObject[] cs = o.getChildren();
		for (int i = 0; i < cs.length; i++) {
			if("deleted".equals(cs[i].get("state"))) { //$NON-NLS-1$ //$NON-NLS-2$
				cs[i].removeFromParent();
			}
		}
		if(!"deleted".equals(o.get("state"))) return 0; //$NON-NLS-1$ //$NON-NLS-2$
		cs = o.getChildren();
		if(cs.length == 0) return 1;
		cs[0].removeFromParent();
		o.setAttributeValue(ATTR_URI, cs[0].getAttributeValue(ATTR_URI));
		o.setAttributeValue(ATTR_DISK_PATH, cs[0].getAttributeValue(ATTR_DISK_PATH));
		o.set("state", "added"); //$NON-NLS-1$ //$NON-NLS-2$
		return 2;		
    }

    private void updateEclipseClassPath() {
		SpecialWizard w = SpecialWizardFactory.createSpecialWizard("org.jboss.tools.common.model.project.ClassPathUpdateWizard"); //$NON-NLS-1$
		if(w != null) try {
			Properties p = new Properties();
			p.put("model", model); //$NON-NLS-1$
			p.put("replacedSrc", replacedSrc); //$NON-NLS-1$
			w.setObject(p);
			w.execute();
		} catch (Exception e) {
            StrutsModelPlugin.getPluginLog().logError(e);
		}
    }

    private void executeDelete() {
        for (XModelObject fs: oldFileSystems.values()) {
            if(fs != null && fs.isActive()) {
            	if("WEB-ROOT".equals(fs.getAttributeValue("name"))) continue; //$NON-NLS-1$ //$NON-NLS-2$
                DefaultRemoveHandler.removeFromParent(fs);
            }
        }
        oldFileSystems.clear();
        newFileSystems.clear();
        for (String n: modulesMap.keySet()) {
            XModelObject o = modulesMap.get(n);
            if("deleted".equals(o.get("state"))) deleteModule(n); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    private void deleteModule(String n) {
        XModelObject init = AdoptProjectContext.getWebAppInitParamForModule(servlet, n);
        if(init != null && init.isActive()) DefaultRemoveHandler.removeFromParent(init);
        XModelObject m = (XModelObject)installedModules.get(n);
        if(m != null && m.isActive()) DefaultRemoveHandler.removeFromParent(m);
    }


    private void revalidateModule(XModelObject mc) throws Exception {
        String n = mc.getAttributeValue("name"); //$NON-NLS-1$
        XModelObject m = (XModelObject)installedModules.get(n);
        if(m != null && (m.getChildren().length > 0 || mc.getChildren().length > 0)) {
        	m.removeFromParent();
        	m = null;
        } 
        if(m == null) {
            m = mc.copy();
            DefaultCreateHandler.addCreatedObject(web, m, -1);
        } else if(getWebObject() != null) {
            m = web.getChildByPath(n.replace('/', '#'));
			model.changeObjectAttribute(m, ATTR_URI, mc.getAttributeValue(ATTR_URI));
            model.changeObjectAttribute(m, ATTR_ROOT, mc.getAttributeValue(ATTR_ROOT));

            String oldsrc = m.getAttributeValue(ATTR_SRC_PATH);
            model.changeObjectAttribute(m, ATTR_SRC_PATH, mc.getAttributeValue(ATTR_SRC_PATH));
			String newsrc = m.getAttributeValue(ATTR_SRC_PATH);
			if(oldsrc != null && oldsrc.length() > 0 && !oldsrc.equals(newsrc) && replacedSrc.containsKey(oldsrc)) {
				replacedSrc.put(oldsrc, newsrc);
			}
			replacedSrc.remove(newsrc);

            model.changeObjectAttribute(m, ATTR_DISK_PATH, mc.getAttributeValue(ATTR_DISK_PATH));
        }
        revalidateModuleFileSystem(m);
        revalidateConfigFileSystem(m);
        revalidateSrcFileSystem(m);
    }

    private XModelObject createFileSystem(String name, String location, String info) throws XModelException {
        XModelObject fs = model.createModelObject("FileSystemFolder", null); //$NON-NLS-1$
        fs.setAttributeValue("name", name); //$NON-NLS-1$
        fs.setAttributeValue("location", location); //$NON-NLS-1$
        if(info != null) fs.setAttributeValue("info", info); //$NON-NLS-1$
        DefaultCreateHandler.addCreatedObject(model.getByPath("FileSystems"), fs, -1); //$NON-NLS-1$
        newFileSystems.put(location, fs);
        return fs;
    }

    private XModelObject adoptOrCreateFileSystem(String name, String location, String info) throws XModelException {
        XModelObject fs = model.getByPath("FileSystems/" + name); //$NON-NLS-1$
        if(fs != null && location.equals(fs.getAttributeValue("location"))) { //$NON-NLS-1$
            if(info != null) model.changeObjectAttribute(fs, "info", info); //$NON-NLS-1$
            newFileSystems.put(location, fs);
            return fs;
        }
        if(fs != null) DefaultRemoveHandler.removeFromParent(fs);
        return createFileSystem(name, location, info);
    }

    private void revalidateModuleFileSystem(XModelObject m) throws Exception {
		if(m.getModelEntity().getName().equals(WebModuleConstants.ENTITY_WEB_CONFIG)) return; //$NON-NLS-1$
        String n = m.getAttributeValue("name"); //$NON-NLS-1$
        boolean isWebRoot = (n.length() == 0);
        String fsn = m.getAttributeValue(ATTR_ROOT_FS);
        if(fsn == null || fsn.length() == 0 || m.getModel().getByPath("FileSystems/" + fsn) == null) { //$NON-NLS-1$
            fsn = (isWebRoot) ? "WEB-ROOT" : n.substring(1).replace('/', '#'); //$NON-NLS-1$
        }
        String root = m.getAttributeValue(ATTR_ROOT);
        if(root.length() == 0) return;
        File f = new File(root);
        if(f.isFile()) throw new Exception("Path " + root + " is not a folder."); //$NON-NLS-1$ //$NON-NLS-2$
        if(!f.exists()) f.mkdirs();
		if(!f.isDirectory()) throw new Exception("Cannot create " + fsn + " at location " + root); //$NON-NLS-1$ //$NON-NLS-2$
        String loc = AdoptProjectFinisher.getFileSystemLocation(workspace, root);
        String info = (isWebRoot) ? "Content-Type=Web" : "Content-Type=Web,Struts-Module=" + n; //$NON-NLS-1$ //$NON-NLS-2$
        XModelObject fs = revalidateModuleFileSystem(fsn, loc, info);
        m.getModel().changeObjectAttribute(m, ATTR_ROOT_FS, fs.getAttributeValue("name")); //$NON-NLS-1$
    }

    private XModelObject revalidateModuleFileSystem(String name, String location, String info) throws XModelException {
        XModelObject fs = (XModelObject)oldFileSystems.get(location);
        if(fs != null && name.equals(fs.getAttributeValue("name"))) { //$NON-NLS-1$
            if(info != null) model.changeObjectAttribute(fs, "info", info); //$NON-NLS-1$
            oldFileSystems.remove(location);
            newFileSystems.put(location, fs);
            return fs;
        }
        fs = (XModelObject)newFileSystems.get(location);
        if(fs != null && name.equals(fs.getAttributeValue("name"))) { //$NON-NLS-1$
            if(info != null) model.changeObjectAttribute(fs, "info", info); //$NON-NLS-1$
            return fs;
        }
        return adoptOrCreateFileSystem(name, location, info);
    }

    private void revalidateConfigFileSystem(XModelObject m) throws Exception {
        String dp = m.getAttributeValue(ATTR_DISK_PATH);
        if(!new File(dp).isFile()) throw new Exception("File " + dp + " is not found."); //$NON-NLS-1$ //$NON-NLS-2$
        String loc = AdoptProjectFinisher.getRelativePath(workspace, dp);
	    if(loc != null && loc.startsWith("/..")) loc = loc.substring(3); //$NON-NLS-1$
	    if(loc.startsWith("/..")) { //$NON-NLS-1$
	    	int i = loc.lastIndexOf("/"); //$NON-NLS-1$
	    	String fsp = XModelConstants.WORKSPACE_REF + "/.." + loc.substring(0, i); //$NON-NLS-1$ //$NON-NLS-2$
	    	loc = loc.substring(i);
	    	XModelObject f = m.getModel().getByPath(loc);
	    	if(f == null) {
	    		String n = "config-fs-" + (loc.endsWith(".xml") ? loc.substring(1, loc.length() - 4) : loc.substring(1)); //$NON-NLS-1$ //$NON-NLS-2$
	    		n = XModelObjectUtil.createNewChildName(n, m.getModel().getByPath("FileSystems"));	    	 //$NON-NLS-1$
	    		createFileSystem(n, fsp, null);
	    	}
	    }
        m.getModel().changeObjectAttribute(m, ATTR_MODEL_PATH, loc);
        XModelObject[] cs = m.getChildren();
        for (int i = 0; i < cs.length; i++) revalidateConfigFileSystem(cs[i]);
    }

    private void revalidateSrcFileSystem(XModelObject m) throws Exception {
    	if(m.getModelEntity().getName().equals(WebModuleConstants.ENTITY_WEB_CONFIG)) return; //$NON-NLS-1$
        String n = m.getAttributeValue("name"); //$NON-NLS-1$
        boolean isWebRoot = (n.length() == 0);
        String fsn = (isWebRoot) ? "src" : "src-" + n.substring(1); //$NON-NLS-1$ //$NON-NLS-2$
        String root = m.getAttributeValue(ATTR_SRC_PATH);
        if(root.length() == 0) return;
        File f = new File(root);
        if(f.isFile()) throw new Exception("Path " + root + " is not a folder."); //$NON-NLS-1$ //$NON-NLS-2$
        if(!f.exists()) f.mkdirs();
        if(!f.isDirectory()) throw new Exception("Cannot create " + fsn + " " + root); //$NON-NLS-1$ //$NON-NLS-2$
        String loc = AdoptProjectFinisher.getFileSystemLocation(workspace, root);
        XModelObject fs = revalidateSrcFileSystem(fsn, loc, null);
        m.getModel().changeObjectAttribute(m, ATTR_SRC_FS, fs.getAttributeValue("name")); //$NON-NLS-1$
    }

    private XModelObject revalidateSrcFileSystem(String name, String location, String info) throws XModelException {
        XModelObject fs = (XModelObject)oldFileSystems.remove(location);
        if(fs != null) {
            newFileSystems.put(location, fs);
            return fs;
        }
        fs = (XModelObject)newFileSystems.get(location);
        if(fs != null) return fs;
        return adoptOrCreateFileSystem(name, location, info);
    }

    private void validateAttributes() throws XModelException {
        validateAttr(ATTR_URI);
        validateAttr(ATTR_DISK_PATH);
        validateAttr(ATTR_ROOT);
    }

    private void validateAttr(String attr) throws XModelException {
        Set<String> set = new HashSet<String>();
        for (int i = 0; i < modules.size(); i++) {
            XModelObject o = (XModelObject)modules.get(i);
            if("deleted".equals(o.get("state"))) continue; //$NON-NLS-1$ //$NON-NLS-2$
            String v = o.getAttributeValue(attr);
            if(v == null) continue;
            if(v.length() == 0) throw new XModelException(MessageFormat.format(StrutsUIMessages.SyncProjectContext_AttributeIsRequired, attr));
            if(set.contains(v)) throw new XModelException(MessageFormat.format(
					StrutsUIMessages.SyncProjectContext_MoreThanOneModuleHasAttributeValue, attr, v));
            set.add(v);
        }
    }

    public static String getModuleDisplayName(String name) {
        return (name.length() == 0) ? "<default>" : name; //$NON-NLS-1$
    }

    private void collectOldFileSystems() {
		replacedSrc.clear();
        oldFileSystems.clear();
        newFileSystems.clear();
        for (String n: installedModules.keySet()) {
            XModelObject m = (XModelObject)installedModules.get(n);
            collectOldFileSystem(m, ATTR_ROOT_FS);
            collectOldFileSystem(m, ATTR_SRC_FS);
            String s = m.getAttributeValue(ATTR_SRC_PATH);
            if(s != null && s.length() > 0) replacedSrc.put(s, ""); //$NON-NLS-1$
        }
    }

    private void collectOldFileSystem(XModelObject m, String attr) {
        String nm = m.getAttributeValue(attr);
        if(nm == null || nm.length() == 0) return;
        XModelObject fs = model.getByPath("FileSystems/" + nm); //$NON-NLS-1$
        if(fs != null) oldFileSystems.put(fs.getAttributeValue("location"), fs); //$NON-NLS-1$
    }

    // for Watcher


    private void removeObsoleteModules() {
		Iterator<String> it = installedModules.keySet().iterator();
		while(it.hasNext()) {
			String n = (String)it.next();
			String uri = (String)describedModules.get(n);
			if(uri == null) {
				XModelObject m = (XModelObject)installedModules.get(n);
				String cgp = m.getAttributeValue(ATTR_MODEL_PATH);
				if(cgp.length() == 0 || model.getByPath(cgp) == null ||
				  (isWebXMLCorrect() && !webxml.isModified())) {
					m.removeFromParent();
					it.remove();
				}
			}
		}
    }

	private String checkCorrectness() {
		if(!isWebXMLCorrect()) return StrutsUIMessages.WEBXML_ISNOT_CORRECT;
		removeObsoleteModules();
		if(describedModules.size() != installedModules.size())
			return StrutsUIMessages.MODULES_DESCRIBED_IN_WEBXML_ARENT_SYNCHRONIZED;
		for (String n: installedModules.keySet()) {
			String uri = (String)describedModules.get(n);
			if(uri == null) return NLS.bind(StrutsUIMessages.MODULE_DOESNT_DEFINE_URI,getModuleDisplayName(n));
			WebModuleImpl m = (WebModuleImpl)installedModules.get(n);
			String uri_m = m.getURI();
			if(!uri.equals(uri_m)) {
				if(uri.length() > 0 && uri_m.length() == 0) {
					m.setURI(uri);
					m.setModified(true);
					///m.getModel().changeObjectAttribute(m, ATTR_URI, uri);
				} else if(uri.length() > 0) {
					m.setURI(uri);
					m.setModified(true);
					///m.getModel().changeObjectAttribute(m, ATTR_URI, uri);
				} else
				return NLS.bind(StrutsUIMessages.URI_FOR_MODULE_ISNOTT_SYNCHRONIZED,getModuleDisplayName(n));
			}
			String fsn = m.getAttributeValue(ATTR_ROOT_FS);
			if(model.getByPath("FileSystems/" + fsn) == null) //$NON-NLS-1$
				return NLS.bind(StrutsUIMessages.ROOT_FOR_MODULE_ISNOT_FOUND, getModuleDisplayName(n)); //$NON-NLS-2$
			String cgp = m.getAttributeValue(ATTR_MODEL_PATH);
			if(cgp.length() == 0 || model.getByPath(cgp) == null) {
				boolean v = false;
				if(uri_m.startsWith("/WEB-INF/")) { //$NON-NLS-1$
					cgp = uri_m.substring("/WEB-INF".length()); //$NON-NLS-1$
					if(model.getByPath(cgp) != null) {
						m.setAttributeValue(ATTR_MODEL_PATH, cgp);
						v = true;
					}
				}
				if(!v) return NLS.bind(StrutsUIMessages.CONFIG_FOR_MODULE_IS_MISSING,getModuleDisplayName(n)); //$NON-NLS-2$
			}
			XModelObject[] cs = m.getChildren();
			for (int i = 0; i < cs.length; i++) {
				cgp = cs[i].getAttributeValue(ATTR_MODEL_PATH);
				if(cgp.length() == 0 || model.getByPath(cgp) == null)
					return NLS.bind(StrutsUIMessages.CONFIG_FOR_URI_ISNOT_FOUND,cs[i].getAttributeValue(ATTR_URI));
			}
		}
		return null;
	}

	ModulesDataValidator modulesValidator = new ModulesDataValidator();

	protected String checkWebXML() {
		if(webxml == null) return StrutsUIMessages.WEBXML_ISNOT_FOUND;
		if(!isWebXMLCorrect()) return StrutsUIMessages.WEBXML_ISNOT_CORRECT;
		return null;
	}

	protected String getProjectLocation() {
		return model.getProperties().getProperty(IModelNature.ECLIPSE_PROJECT); //$NON-NLS-1$
	}

	public String getErrorMessage(XModelObject[] modules, XModelObject selected) {
		String s = checkWebXML();
		if(s != null) return s;
		modulesValidator.setProject(getProjectLocation());
		return modulesValidator.getErrorMessage(modules, selected);
	}
	
	// IWatcherContributor
	public void init(XModel model) {
		this.model = model;
	}

	public boolean isActive() {
		return EclipseResourceUtil.hasNature(model, StrutsProjectUtil.STRUTS_NATURE_ID);
	}

	public void update() {
		setModel(model);
		update(false);
	}

	public String getError() {
		try {
			return checkCorrectness();
		} catch (Exception e) {
            //Do not log this exception. It is thrown to be showed 
			//in wizard as user input error.
			return e.getMessage();
		}
	}

	public void updateProject() {
		WebModulesHelper wh = WebModulesHelper.getInstance(model);
		wh.getPatternLoader().revalidate(getWebXML());
		wh.getResourceMapping().revalidate();
		wh.getWebProject().getTaglibMapping().revalidate(getWebXML());
	}

}
