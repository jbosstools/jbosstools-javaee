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
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.common.model.filesystems.FileSystemsHelper;
import org.jboss.tools.common.model.filesystems.XFileObject;
import org.jboss.tools.common.model.impl.trees.*;
import org.jboss.tools.struts.model.helpers.*;
import org.jboss.tools.struts.webprj.model.helpers.WebModulesHelper;

public class WebTree extends DefaultSiftedTree {
    protected XModelObject fsr;
    protected XModelObject web;
    protected XModelObject[] modules = new XModelObject[0];
    protected Properties moduleNames = new Properties();
    protected String thismodule = null;
    protected boolean isThis = false;

    public WebTree() {}

	public void dispose() {
		if (moduleNames!=null) moduleNames.clear();
		moduleNames = null;
	}

	public XModelObject getRoot() {
        return (isThis && modules.length == 1) ? modules[0] :  fsr; 
    }

    public void setConstraint(Object object) {
        if(object instanceof Boolean) {
            isThis = ((Boolean)object).booleanValue();
            return;
        }
        modules = new XModelObject[0];
        fsr = FileSystemsHelper.getFileSystems(model);
        moduleNames.clear();
        if(fsr == null) return;
        XModelObject contextProcess = StrutsProcessStructureHelper.instance.getProcess((XModelObject)object);
        thismodule = (contextProcess == null) ? null : StrutsProcessStructureHelper.instance.getProcessModule(contextProcess);
        Vector<XModelObject> v = new Vector<XModelObject>();
        web = null;
        Map map = WebModulesHelper.getInstance(model).getWebFileSystems();
        for (Iterator it = map.keySet().iterator(); it.hasNext();) {
        	String module = it.next().toString();
        	XModelObject fs = (XModelObject)map.get(module);
        	if(fs == null) continue;
            boolean isWebroot = (module.length() == 0);
            boolean isThisModule = (thismodule != null && thismodule.length() > 0 && thismodule.equals(module));
            if(isThisModule) {
                v.insertElementAt(fs, 0);
                moduleNames.setProperty(fs.getPathPart(), "" + module);
            } else if(module != null && module.length() > 0) {
                if(!isThis) {
                    v.addElement(fs);
                    moduleNames.setProperty(fs.getPathPart(), "" + module);
                }
            } else if(isWebroot) {
                web = fs;
            }
        }
        if(web != null) if(!isThis || v.size() == 0) v.insertElementAt(web, 0);
        modules = v.toArray(new XModelObject[0]);
    }

    public boolean hasChildren(XModelObject object) {
        if(object.getFileType() < XFileObject.FOLDER && object != fsr && object != web) return false;
        return super.hasChildren(object);
    }

    public XModelObject[] getChildren(XModelObject object) {
        if(!hasChildren(object)) return new XModelObject[0];
        if(object == fsr) {
            return modules;
        }
        List<XModelObject> l = new ArrayList<XModelObject>();
///        if(object == web) {
///            for (int i = 0; i < modules.length; i++) l.add(modules[i]);
///        }
        XModelObject[] cs = object.getChildren();
        for (int i = 0; i < cs.length; i++) if(accept(cs[i])) l.add(cs[i]);
        return l.toArray(new XModelObject[0]);
    }

    private boolean accept(XModelObject c) {
        if(c.getFileType() == XFileObject.FOLDER) {
            String overlapped = c.get("overlapped");
            if(overlapped != null && overlapped.length() > 0) {
            	String overlappedSystem = c.get("overlappedSystem");
            	if(!"FileSystems/WEB-INF".equals(overlappedSystem)) return false;
            } 
        } else if(c.getFileType() == XFileObject.FILE) {
        	String nm = c.getAttributeValue("name");
        	if(nm.length() == 0) return false;
        }
        return true;
    }

    public String getPath(XModelObject o) {
        if(web == null && (o == fsr || o.getParent() == fsr)) return "";
        String s = XModelObjectLoaderUtil.getResourcePath(o);
        String p = o.getPath();
        if(p == null) return "";
        int b = "FileSystems/".length(), e = p.length() - s.length();
        if(e < b) return "";
        p = p.substring(b, e);
           if(o.getFileType() == XFileObject.FOLDER) s += "/";
        String f = moduleNames.getProperty(p);
        return (f == null || f.length() == 0 || f.equals(thismodule)) ? s : f + s;
    }

}

