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
package org.jboss.tools.struts.model.handlers;

import java.util.*;

import org.eclipse.osgi.util.NLS;

import org.jboss.tools.struts.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.meta.action.impl.handlers.*;
import org.jboss.tools.common.model.util.*;
import org.jboss.tools.jst.web.project.WebModuleConstants;
import org.jboss.tools.struts.webprj.model.helpers.WebModulesHelper;
import org.jboss.tools.struts.messages.StrutsUIMessages;
import org.jboss.tools.struts.model.helpers.*;

public class SetupModuleHandler implements StrutsConstants {

    public static void setupModule(XModelObject object, String module, String fsname) {
        XModel model = object.getModel();
        if (module.length() > 0 && module.charAt(0) != '/') {
            throw new RuntimeException(StrutsUIMessages.MODULE_NAME);
        }
        String prevMod = StrutsProcessStructureHelper.instance.getProcessModule(object);

        XModelObject fsOld = (prevMod == null) ? null : getFileSystem(model, prevMod);
        XModelObject fsNew = null;
        if (fsname != null && fsname.length() > 0) {
            fsNew = model.getByPath("FileSystems/"+fsname);
        }
        if (fsNew != null) {
            Properties info = getProperties(fsNew.getAttributeValue("info"));
            if ("Web".equals(info.getProperty(CONTENT_TYPE)) && 
                  !module.equals(info.getProperty(STRUTS_MODULE))) {
                if (!unmarkModule(model, "Setup Module", info.getProperty(STRUTS_MODULE))) return;
            }
            if (fsNew == fsOld) fsOld = null;
            XModelObject fsPrev = getFileSystem(model, module);
            if (fsNew != fsPrev && fsPrev != null) {
                Properties info2 = getProperties(fsPrev.getAttributeValue("info"));
                info2.remove(CONTENT_TYPE);
                info2.remove(STRUTS_MODULE);
                model.changeObjectAttribute(fsPrev, "info", getString(info2));
            }
            info.setProperty(CONTENT_TYPE, "Web");
            if (module.length() > 0) {
                info.setProperty(STRUTS_MODULE, module);
            } else {
                info.remove(STRUTS_MODULE);
            }
            model.changeObjectAttribute(fsNew, "info", getString(info));
        } else {
            XModelObject webRoot = getWebRoot(object.getModel());
            if(webRoot != null && module.length() > 0) {
            	XModelObject w = object.getModel().getByPath("Web/" + module.replace('/', '#'));
            	if(w != null) {
            		String s = w.getAttributeValue(WebModuleConstants.ATTR_ROOT_FS);
					XModelObject fs = (s.length() == 0) ? null : object.getModel().getByPath("FileSystems/" + s);
					if(fs != null) return;
            	}
                XModelObject fs = createFileSystem(webRoot, module);
                fsname = fs.getAttributeValue("name");
            } else if(webRoot != null) {
				fsname = webRoot.getAttributeValue("name"); 
            }
        }
        if (fsOld != null && module.equals(prevMod)) {
            Properties info = getProperties(fsOld.getAttributeValue("info"));
            info.remove(CONTENT_TYPE);
            info.remove(STRUTS_MODULE);
            model.changeObjectAttribute(fsOld, "info", getString(info));
        }
        if (object.getPath() != null && fsname != null)
          WebModulesHelper.getInstance(model).setModule(model, prevMod, module, XModelObjectLoaderUtil.getResourcePath(object.getParent()), fsname);
    }

    static XModelObject getFileSystem(XModel model, String module) {
    	return WebModulesHelper.getInstance(model).getFileSystem(module);
    }

    static XModelObject getWebRoot(XModel model) {
		return WebModulesHelper.getInstance(model).getWebRoot();
    }

    public static Properties getProperties(XModelObject fs) {
        return getProperties(fs.getAttributeValue("info"));
    }

    public static Properties getProperties(String s) {
        Properties props = new Properties();
        StringTokenizer st = new StringTokenizer(s, ",");
        while (st.hasMoreElements()) {
            String t = st.nextToken();
            int i = t.indexOf('=');
            if (i < 0) {
                props.setProperty(t, "");
            } else {
                String n = t.substring(0, i).trim();
                String v = t.substring(i + 1).trim();
                props.setProperty(n, v);
            }
        }
        return props;
    }

    public static String getString(Properties props) {
        Iterator it = props.entrySet().iterator();
        String s = "";
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            s += (s.length() > 0 ? "," : "") + entry.getKey() + "=" + entry.getValue();
        }
        return s;
    }

    private static XModelObject createFileSystem(XModelObject webroot, String module) {
        XModel model = webroot.getModel();
        String mn = module;
        if(mn.startsWith("/")) mn = mn.substring(1);
		String subpath = mn;
		createFolder(webroot, subpath);
        XModelObject fs = model.createModelObject("FileSystemFolder", null);
        String location = webroot.getAttributeValue("location") + "/" + subpath;
        fs.setAttributeValue("location", location);
        XModelObject fsp = webroot.getParent();
		mn = mn.replace('/', '#');
        String fsname = XModelObjectUtil.createNewChildName(mn, fsp);
        fs.setAttributeValue("name", fsname);
        Properties p = getProperties(fs);
        p.setProperty(CONTENT_TYPE, "Web");
        p.setProperty(STRUTS_MODULE, module);
        fs.setAttributeValue("info", getString(p));
        DefaultCreateHandler.addCreatedObject(fsp, fs, -1);
        return fs;
    }
    
    private static XModelObject createFolder(XModelObject parent, String path) {
    	int i = path.indexOf('/');
    	String part = (i < 0) ? path : path.substring(0, i);
		XModelObject folder = parent.getChildByPath(part);
		if(folder == null) {
			folder = parent.getModel().createModelObject("FileFolder", null);
			folder.setAttributeValue("name", part);
			DefaultCreateHandler.addCreatedObject(parent, folder, -1);
		}
		if(i < 0) return folder;
		path = path.substring(i + 1);
		return createFolder(folder, path);    	
    }

    public static boolean unmarkModule(XModel model, String title, String module) {
        ServiceDialog d = model.getService();
        String msg = null;
        if (module != null && module.length() > 0) {
            msg = NLS.bind(StrutsUIMessages.FILE_SYSTEM_IS_USED_AS_ROOT_FOR_STRUTS_MODULE, module); //$NON-NLS-2$
        } else {
            msg = StrutsUIMessages.FILE_SYSTEM_IS_USED_AS_ROOT_FOR_WEB_APPLICATION;
        }
        int i = d.showDialog(title, msg, new String[] {StrutsUIMessages.YES, StrutsUIMessages.NO}, null, ServiceDialog.WARNING);
        return i == 0;
    }

}
