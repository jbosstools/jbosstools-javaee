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
import org.jboss.tools.common.meta.action.impl.handlers.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.undo.*;
import org.jboss.tools.struts.*;
import org.jboss.tools.struts.model.helpers.*;
import org.jboss.tools.struts.model.handlers.page.create.*;
import org.jboss.tools.struts.webprj.pattern.UrlPattern;

public class CreateForwardHandler extends CreateConfigElementHandler implements StrutsConstants {

    public CreateForwardHandler() {}

    public boolean isEnabled(XModelObject object) {
        if(!super.isEnabled(object)) return false;
        return !isImmutableAction(object);
    }

    private boolean isImmutableAction(XModelObject object) {
        String entity = object.getModelEntity().getName();
        if(!entity.startsWith(ENT_ACTION)) return false;
        if(object.getAttributeValue(ATT_FORWARD).length() > 0) return true;
        String jtype = object.getAttributeValue(ATT_TYPE);
        if("org.apache.struts.actions.SwitchAction".equals(jtype)) return true;
        if("org.apache.struts.actions.ForwardAction".equals(jtype)) return true;
        return false;
    }

    public void executeHandler(XModelObject object, Properties prop) throws Exception {
        if(!isEnabled(object)) return;
        XUndoManager undo = object.getModel().getUndoManager();
        XTransactionUndo u = new XTransactionUndo("create element in " + DefaultCreateHandler.title(object, false), XTransactionUndo.ADD);
        undo.addUndoable(u);
        try {
            transaction(object, prop);
        } catch (Exception e) {
            undo.rollbackTransactionInProgress();
            throw e;
        } finally {
            u.commit();
        }
    }

    protected void setOtherProperties(XModelObject object, Properties p) {
        validatePathAttr(object, p);
    }

    protected void validatePathAttr(XModelObject object, Properties p) {
        String path = p.getProperty(ATT_PATH);
        if(path == null || path.length() == 0) return;
		UrlPattern up = StrutsProcessStructureHelper.instance.getUrlPattern(object);
		boolean isHttp = StrutsProcessHelper.isHttp(path);
        boolean isAction = !isHttp && up.isActionUrl(path);
        if(isAction) path = up.getActionUrl(path);
        CreatePageContext context = new CreatePageContext();
        context.setProcess(object);
        context.resetRoots();
        boolean isPage = isHttp || (path.indexOf(".") >= 0 && context.isPage(path));
        boolean isTile = (!isPage && !isAction);
        if(!isTile && !isHttp) {
            if(!path.startsWith("/")) path = "/" + path;
            if(isPage) {
                String oldRoot = context.getRoot();
                String jsppath = context.setRootByPath(path);
                String newRoot = context.getRoot();
                if(!isEqualRoots(oldRoot, newRoot) && newRoot != null)
                  path = newRoot + jsppath;
                else
                  path = jsppath;
                //TODO: if contextRelative=true, always add root.
            }
        }

        HUtil.find(data, 0, ATT_PATH).setValue(path);
        p.setProperty(ATT_PATH, path);
    }

    protected void transaction(XModelObject object, Properties prop) throws Exception {
        executeHandler0(object, prop);
        Properties p = extractProperties(data[0]);
        String path = p.getProperty(ATT_PATH);
        UrlPattern up = StrutsProcessStructureHelper.instance.getUrlPattern(object);
        boolean isHttp = StrutsProcessHelper.isHttp(path);
        boolean isAction = (!isHttp && up.isActionUrl(path));
        CreatePageContext context = new CreatePageContext();
        context.setProcess(object);
        context.resetRoots();
        boolean isPage = isHttp || (path.indexOf(".") >= 0 && context.isPage(path));
//        boolean isTile = (!isPage && !isAction);
        if(isAction) {
            path = up.getActionPath(path);
            String root = context.getRoot();
            if(root != null) path = up.getModuleRelativePath(path, root);
        }
        if(prop != null) prop.setProperty(ATT_PATH, path); else prop = p;
        if(isPage && !isHttp) {
            String oldRoot = context.getRoot();
/*4599*/    String jsppath = context.setRootByPath(path);
            String newRoot = context.getRoot();
            XModelObject fs = context.getSelectedFileSystem();
            if(fs != null && isEqualRoots(oldRoot, newRoot))
              CreatePageSupport.createFile(fs, jsppath, null);
        }
        setShape(object, prop);
        try {
        XModelObject process = StrutsProcessStructureHelper.instance.getProcess(object);
        StrutsProcessHelper.getHelper(process).updatePages();
        } catch (Exception e) {
        	StrutsModelPlugin.getPluginLog().logError(e);
        }
    }

    private boolean isEqualRoots(String r1, String r2) {
        return (r1 == null) ? r2 == null : r1.equals(r2);
    }

}
