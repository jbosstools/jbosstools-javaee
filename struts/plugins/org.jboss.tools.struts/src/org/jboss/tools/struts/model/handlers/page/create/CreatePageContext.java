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
package org.jboss.tools.struts.model.handlers.page.create;

import java.util.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.filesystems.*;
import org.jboss.tools.struts.*;
import org.jboss.tools.struts.webprj.model.helpers.WebModulesHelper;

public class CreatePageContext implements StrutsConstants {
    protected CreatePageSupport support;
    protected XModelObject process = null;
    protected Map<String,String> roots = new HashMap<String,String>();
    protected XModelObject selectedFileSystem = null;
    private Set<String> pages = new HashSet<String>();
    protected XModelObject preselected = null;
    protected String thisModuleRoot = null;

    public CreatePageContext() {}

    public void setSupport(CreatePageSupport support) {
        this.support = support;
    }

    public void setProcess(XModelObject o) {
        while(o != null && o.getFileType() != XFileObject.FILE) o = o.getParent();
        process = o.getChildByPath("process");
    }

    public void reset() {
        if(support != null) process = support.getTarget();
        resetRoots();
        resetPages();
        if(support != null) support.getProperties().put("context", this);
        checkPreselected();
    }

    private void checkPreselected() {
        preselected = (XModelObject)support.getProperties().get("preselectedObject");
        if(preselected == null) return;
        support.getProperties().put("selectedObject", preselected);
        XModelObject o = preselected;
        while(o != null && o.getFileType() != XFileObject.SYSTEM) o = o.getParent();
        selectedFileSystem = o;
        if(selectedFileSystem != null && roots.get(selectedFileSystem.getPathPart()) == null) {
        	if("WEB-INF".equals(selectedFileSystem.getPathPart())) {
        		String path = "FileSystems/WEB-ROOT" + preselected.getPath().substring("FileSystems".length());
        		XModelObject p2 = o.getModel().getByPath(path);
        		if(p2 != null) {
        			preselected = p2;
        	        support.getProperties().put("selectedObject", p2);
        			selectedFileSystem = o.getModel().getByPath("FileSystems/WEB-ROOT");
        		}
        	}
        }
        if(selectedFileSystem != null) support.getProperties().put("selectedFileSystem", selectedFileSystem);
    }

    public boolean isConfig10() {
        return process.getParent().getModelEntity().getName().endsWith(VER_SUFFIX_10);
    }

    public String getRoot() {
        return (selectedFileSystem == null) ? null : (String)roots.get(selectedFileSystem.getPathPart());
    }

    public String getThisRoot() {
        return (thisModuleRoot == null) ? "" : thisModuleRoot;
    }

    public boolean isPreselected() {
        return preselected != null;
    }

    public String getRootInfo(XModelObject fs) {
        String module = (String)roots.get(fs.getPathPart());
        return (module != null) ? module + " (" + fs.getPathPart() + ")" : fs.getPathPart();
    }

    public void setSelectedFileSystem(XModelObject o) {
        selectedFileSystem = o;
    }

    public XModelObject getSelectedFileSystem() {
        return selectedFileSystem;
    }

    public String getSelectedRoot() {
        return (selectedFileSystem == null) ? null : selectedFileSystem.getPathPart();
    }

    public void resetRoots() {
        thisModuleRoot = WebModulesHelper.getInstance(process.getModel()).getModuleForConfig(process.getParent());
        if(thisModuleRoot == null) thisModuleRoot = "";

        selectedFileSystem = null;
        Map map = WebModulesHelper.getInstance(process.getModel()).getWebFileSystems();
		for(Iterator it = map.keySet().iterator(); it.hasNext();) {
			String module = it.next().toString();
			XModelObject fs = (XModelObject)map.get(module); 
			boolean isWebroot = (module.length() == 0);
			boolean isThisModule = (thisModuleRoot.length() > 0 && thisModuleRoot.equals(module));
			if(isThisModule) {
				roots.put(fs.getPathPart(), module);
				selectedFileSystem = fs;
			} else if(isWebroot) {
				if(selectedFileSystem == null)  selectedFileSystem = fs;
			} else if(module != null && module.length() > 0) {
				roots.put(fs.getPathPart(), module);
			}
		}
    }

    public void update() {
    }

    private void resetPages() {
        pages.clear();
        XModelObject[] ps = process.getChildren();
        for (int i = 0; i < ps.length; i++) {
            String path = ps[i].getAttributeValue("path");
            if(path != null) pages.add(path.toLowerCase());
        }
    }

    public boolean pageExists(String path) {
        return pages.contains(path);
    }

    public boolean isPage(String v) {
    	if(v.startsWith("http:")) return true;
        return v.endsWith(".jsp") || v.endsWith(".htm") || v.endsWith(".html")
        	   || v.endsWith(CreatePageSupport.getExtension())
               || v.endsWith(".tld") ||
               (!v.endsWith("/") && v.indexOf('.') < 0);
    }

    public String revalidatePath(String path) { /*4598*/
        if(getRoot() != null || !path.startsWith("/")) return path;
        return setRootByPath(path);
    }

    ///

    public String setRootByPath(String path) {
        Iterator it = roots.keySet().iterator();
        while(it.hasNext()) {
            String fsp = (String)it.next();
            String m = (String)roots.get(fsp);
            if(path.startsWith(m + "/")) {
                setSelectedFileSystem(process.getModel().getByPath("FileSystems/" + fsp));
                return path.substring(m.length());
            }
        }
        return path;
    }

}

