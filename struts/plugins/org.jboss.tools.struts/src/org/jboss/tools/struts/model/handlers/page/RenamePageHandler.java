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
package org.jboss.tools.struts.model.handlers.page;

import java.util.*;

import org.eclipse.osgi.util.NLS;

import org.jboss.tools.common.meta.action.impl.handlers.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.filesystems.*;
import org.jboss.tools.common.model.filesystems.impl.FolderImpl;
import org.jboss.tools.struts.messages.StrutsUIMessages;
import org.jboss.tools.struts.model.StrutsProcessImpl;
import org.jboss.tools.struts.model.helpers.*;

public class RenamePageHandler extends DefaultEditHandler {

    public RenamePageHandler() {}

    public boolean isEnabled(XModelObject object) {
        return object.isObjectEditable() && "page".equals(object.getAttributeValue("type")); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void executeHandler(XModelObject object, Properties prop) throws XModelException {
        Properties p = extractProperties(data[0]);
        String oldpath = object.getAttributeValue("path"); //$NON-NLS-1$
        String path = p.getProperty("path"); //$NON-NLS-1$
        if(path.equals("/")) throw new XModelException(StrutsUIMessages.RenamePageHandler_PathIsNotValid); //$NON-NLS-1$
        boolean isTile = oldpath != null && !oldpath.startsWith("/"); //$NON-NLS-1$
        if(!isTile && !path.startsWith("/")) path = "/" + path; //$NON-NLS-1$ //$NON-NLS-2$
        if(path.startsWith("/") && path.indexOf('.') < 0) path += ".jsp"; //$NON-NLS-1$ //$NON-NLS-2$

		if(oldpath.equals(path)) return;
        XModelObject file = object.getModel().getByPath(oldpath);
        XModelObject newfile = object.getModel().getByPath(path);
        if(file != null && (newfile == null || newfile == file)) {
            XModelObject pf = file.getParent(); 
            int i = 0;
            while(i == 0 && !file.isObjectEditable()) {
                ServiceDialog d = object.getModel().getService();
                i = d.showDialog(StrutsUIMessages.WARNING, NLS.bind(StrutsUIMessages.FILE_IS_READONLY,oldpath), new String[]{StrutsUIMessages.RETRY, StrutsUIMessages.CANCEL}, null, ServiceDialog.WARNING); //$NON-NLS-3$
            }
            if(i != 0) return;

            String body = file.getAttributeValue("body"); //$NON-NLS-1$
			XModelObject fs = file;
            while(fs.getFileType() != XFileObject.SYSTEM) fs = fs.getParent();

            DefaultRemoveHandler.removeFromParent(file);
			((FolderImpl)pf).save();
			((FolderImpl)pf).update();

            org.jboss.tools.struts.model.handlers.page.create.CreatePageSupport.createFile(fs, path, body);

            pf.getModel().update();
        }
        XModelObject existingPage = findExistingPage(object.getParent(), path);
        if(existingPage == null) {
        	StrutsProcessHelper.getHelper(object.getParent()).resetPage(object, oldpath, path);
        	object.getModel().changeObjectAttribute(object, "path", path); //$NON-NLS-1$
        }
        changeMatchingAttributesInHierarhy(StrutsProcessStructureHelper.instance.getParentFile(object), oldpath, path);
        if(existingPage != null) {
        	object.removeFromParent();
        }
    }
    
    private XModelObject findExistingPage(XModelObject process, String path) {
    	XModelObject[] cs = process.getChildren();
    	for (int i = 0; i < cs.length; i++) {
    		String type = cs[i].getAttributeValue("type"); //$NON-NLS-1$
    		if("forward".equals(type)) continue; //$NON-NLS-1$
    		String p = cs[i].getAttributeValue("path"); //$NON-NLS-1$
    		if(path.equals(p)) return cs[i];
    	}
    	return null;
    }
    
    public static void changeMatchingAttributesInHierarhy(XModelObject object, String oldpath, String newpath) throws XModelException {
    	RenamePageHandler.changeMatchingAttributes(object, oldpath, newpath);
    	XModelObject[] os = object.getChildrenForSave();
    	for (int i = 0; i < os.length; i++) {
    		if(object instanceof StrutsProcessImpl) continue;
    		changeMatchingAttributesInHierarhy(os[i], oldpath, newpath);
    	}
    }
    
	static String[] PATH_ATTRIBUTES = new String[]{"path", "include", "forward", "input"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    
	public static void changeMatchingAttributes(XModelObject ref, String oldpath, String path) throws XModelException {
		for (int i = 0; i < PATH_ATTRIBUTES.length; i++) {
			if(oldpath.equals(ref.getAttributeValue(PATH_ATTRIBUTES[i]))) {
				ref.getModel().changeObjectAttribute(ref, PATH_ATTRIBUTES[i], path); 
			}
		}
	}

}

