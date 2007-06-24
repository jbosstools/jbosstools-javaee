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
import org.jboss.tools.common.meta.action.*;
import org.jboss.tools.common.meta.action.impl.*;
import org.jboss.tools.common.meta.action.impl.handlers.*;
import org.jboss.tools.struts.model.helpers.*;
import org.jboss.tools.struts.*;
import org.jboss.tools.struts.model.handlers.*;
import org.jboss.tools.struts.webprj.model.helpers.*;
import org.jboss.tools.struts.webprj.pattern.UrlPattern;
import org.jboss.tools.struts.model.handlers.page.create.*;
import org.jboss.tools.common.model.undo.*;

public class CreateActionForwardSupport extends SpecialWizardSupport implements StrutsConstants {
    Set<String> tiles = new TreeSet<String>();
    ActionsTree actions = null;
    int tools = 6;
    boolean isException = false;

    public CreateActionForwardSupport() {}

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

    protected void reset() {
        tools = 6;
        isException = "yes".equals(action.getProperty("isException"));
        boolean isGlobalForward = "yes".equals(action.getProperty("isGlobalForward"));
        if(isGlobalForward) tools = 7;
        else if(isException) tools = 3;
        if(isException) p.setProperty("isException", "yes"); 
		if(isGlobalForward) p.setProperty("isGlobalForward", "yes"); 
        if((tools & 1) != 0) {
            actions = new ActionsTree();
            actions.setModel(getTarget().getModel());
            actions.setConstraint(getTarget());
            p.put("actionsTree", actions);
        } else {
            actions = null;
            p.remove("actionsTree");
        }
        p.setProperty("tools", "" + tools);
        setAttributeContext(0, "path", this);
        resetTiles();
    }

    private void resetTiles() {
        tiles.clear();
        tiles.addAll(StrutsProcessStructureHelper.instance.getTiles(getTarget()));
        p.put("tiles", tiles);
    }

    public void action(String name) throws Exception {
        if(OK.equals(name)) {
            finish();
            setFinished(true);
        } else if(CANCEL.equals(name)) {
            setStepId(-1);
            getProperties().remove("selectedPath");
            setFinished(true);
        } else if(name.equals("...:path")) {
            status = validatePath();
            p.setProperty("selectedPath", getAttributeValue(0, "path"));
            XActionInvoker.invoke("StrutsCreateActionForwardStep", "EditPath", getTarget(), p);
            String path = p.getProperty("selectedPath");
            if(path != null) {
                setAttributeValue(0, "path", path);
                String tab = p.getProperty("selectedTab");
                if("Pages".equals(tab)) status = PAGE;
                if("Tiles".equals(tab)) status = TILE;
                if("Actions".equals(tab)) status = ACTION;
                if(!isException) validateContextRelative();
            }
        }
    }

    private void validateContextRelative() {
        if(getTarget().getModelEntity().getName().endsWith("10")) return;
        if(status == PAGE) {
            setAttributeValue(0, "contextRelative", "");
        } else {
            String module = getSelectedModule();
            String thismodule = WebModulesHelper.getInstance(getTarget().getModel()).getModuleForConfig(StrutsProcessStructureHelper.instance.getParentFile(getTarget()));
            String cR = getAttributeValue(0, "contextRelative");
            if(thismodule != null && thismodule.equals(module)) {
                if("true".equals(cR) || "yes".equals(cR))
                  setAttributeValue(0, "contextRelative", "");
            } else {
                if(!"true".equals(cR) && !"yes".equals(cR))
                  setAttributeValue(0, "contextRelative", "true");
            }
        }
    }

    private String getSelectedModule() {
        if(status == TILE) return null;
        if(status == PAGE) {
            XModelObject fs = (XModelObject)getProperties().get("selectedFileSystem");
            return (fs == null) ? "" : WebModulesHelper.getInstance(fs.getModel()).getModuleForFileSystem(fs);
        }
        XModelObject a = (XModelObject)getProperties().get("selectedAction");
        return (a == null || actions == null) ? "" : actions.getModule(a);
    }

    protected void finish() throws Exception {
        XUndoManager undo = getTarget().getModel().getUndoManager();
        XTransactionUndo u = new XTransactionUndo("create forward in " + DefaultCreateHandler.title(getTarget(), false), XTransactionUndo.ADD);
        undo.addUndoable(u);
        try {
            transaction();
        } catch (Exception e) {
            undo.rollbackTransactionInProgress();
            throw e;
        } finally {
            u.commit();
        }
    }

    static int TILE = 0;
    static int PAGE = 1;
    static int ACTION = 2;

    int status = 0;

    protected int validatePath() {
        String path = getAttributeValue(0, "path");
        if(path.equals(p.getProperty("selectedPath"))) {
            return status;
        }
        if(tiles.contains(path)) return TILE;
        int res = -1;
        CreatePageContext context = new CreatePageContext();
        context.setProcess(getTarget());
        context.resetRoots();
        UrlPattern up = StrutsProcessStructureHelper.instance.getUrlPattern(getTarget());
        if(path.indexOf(".") >= 0 && context.isPage(path)) res = PAGE;
        else if(up.isActionUrl(path)) res = ACTION;
        else {
            p.setProperty("selectedTab", "Tiles");
            p.setProperty("selectedPath", path);
            return TILE;
        }
        if(!path.startsWith("/") && path.length() > 0 && !StrutsProcessHelper.isHttp(path)) path = "/" + path;
        if(res == ACTION) {
            path = up.getActionPath(path);
            String root = context.getRoot();
            if(root != null) path = up.getModuleRelativePath(path, root);
        } else if(res == PAGE) {
            String oldRoot = context.getRoot();
            String jsppath = context.setRootByPath(path);
            String newRoot = context.getRoot();
            if(!isEqualRoots(oldRoot, newRoot) && newRoot != null) {
              path = newRoot + jsppath;
            } else {
              path = jsppath;
                //TODO: if contextRelative=true, always add root.
            }
        }
        setAttributeValue(0, "path", path);
        return res;
    }

    protected void transaction() throws Exception {
        status = validatePath();
        XModelObject object = getTarget();
        Properties p0 = extractStepData(0);
        String path = p0.getProperty(ATT_PATH);
        CreatePageContext context = new CreatePageContext();
        context.setProcess(object);
        context.resetRoots();
        if(status == PAGE && !StrutsProcessHelper.isHttp(path)) {
            String oldRoot = context.getRoot();
/*4599*/    String jsppath = context.setRootByPath(path);
            String newRoot = context.getRoot();
            XModelObject fs = context.getSelectedFileSystem();
            if(fs != null && isEqualRoots(oldRoot, newRoot))
              CreatePageSupport.createFile(fs, jsppath, null);
        }
        XModelObject o = object.getModel().createModelObject(getChildEntity(), p0);
        //Do not assign shape for item output.
        if(!getTarget().getModelEntity().getName().startsWith(ENT_ACTION)) {
        	CreateConfigElementHandler.setItemShape(o, p);
        }
        DefaultCreateHandler.addCreatedObject(object, o, getProperties());
        XModelObject process = StrutsProcessStructureHelper.instance.getProcess(object);
        StrutsProcessHelper.getHelper(process).updatePages();
        
        StrutsProcessHelper.getHelper(process).autolayout();
    }

    private String getChildEntity() {
    	String parentEntity = getTarget().getModelEntity().getName();
        String base = (isException) ? ENT_EXCEPTION : ENT_FORWARD;
        String suffix = (parentEntity.endsWith(VER_SUFFIX_11)) ? VER_SUFFIX_11
                      : (parentEntity.endsWith(VER_SUFFIX_12)) ? ((isException) ? VER_SUFFIX_11 : VER_SUFFIX_12)
                      : base + VER_SUFFIX_10;
        return base + suffix;
    }

    private boolean isEqualRoots(String r1, String r2) {
        return (r1 == null) ? r2 == null : r1.equals(r2);
    }
    
	private Validator validator = new Validator();
    
	public WizardDataValidator getValidator(int step) {
		validator.setSupport(this, step);
		return validator;    	
	}
	
	class Validator extends DefaultWizardDataValidator {
		public void validate(Properties data) {
			String entity = getChildEntity();
			if(!checkChild(support.getTarget(), entity, data)) return;
			super.validate(data);
		}
	}

}

