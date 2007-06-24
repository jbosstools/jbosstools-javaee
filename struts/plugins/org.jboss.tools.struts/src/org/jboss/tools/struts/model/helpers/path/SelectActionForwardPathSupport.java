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
import org.jboss.tools.common.model.filesystems.XFileObject;
import org.jboss.tools.common.meta.action.impl.*;
import org.jboss.tools.struts.*;
import org.jboss.tools.struts.model.helpers.*;

public class SelectActionForwardPathSupport extends SpecialWizardSupport implements StrutsConstants {
    Set tiles = new TreeSet();
    ActionsTree actions = null;
    int tools = 6;

    public SelectActionForwardPathSupport() {}

    public String getTitle() {
        if(p != null && p.getProperty("title") != null) return p.getProperty("title");
        return super.getTitle(); 
    }

    protected void reset() {
        getProperties().put("contextProcess", getTarget());
        try {
            tools = Integer.parseInt(p.getProperty("tools"));
        } catch (Exception e) {
        	StrutsModelPlugin.getPluginLog().logError(e);
        }
        loadTiles();
        loadSelection();
    }

    private void loadTiles() {
        tiles.clear();
        if((tools & 4) != 0) {
            Set ts = (Set)p.get("tiles");
            if(ts != null) tiles.addAll(ts);
        }
        actions = (ActionsTree)p.get("actionsTree");
//        getProperties().put("tiles", tiles);
    }

    private void loadSelection() {
        String p = getProperties().getProperty("selectedPath");
        if(p == null || p.length() == 0) {
			getProperties().remove("selectedObject");
			getProperties().remove("selectedTile");
			getProperties().remove("selectedAction");
        	return;
        } 
        if(tiles.contains(p)) {
            getProperties().setProperty("selectedTab", "Tiles");
            getProperties().setProperty("selectedTile", p);
            getProperties().remove("selectedObject");
        } else if(actions != null && isAction(p)) {
            XModelObject o = (XModelObject)getProperties().get("selectedAction");
            if(o != null && p.equals(actions.getPath(o))) return;
            getProperties().setProperty("selectedTab", "Actions");
            o = (actions == null) ? null : actions.find(p);
            if(o != null) getProperties().put("selectedAction", o);
            else getProperties().remove("selectedAction");
        } else {
            getProperties().setProperty("selectedTab", "Pages");
            XModelObject so = StrutsProcessStructureHelper.instance.getPhysicalPage(getTarget(), p);
            if(so == null) {
                so = findInProcess(p);
                if(so != null) so = StrutsProcessStructureHelper.instance.getPhysicalPage(so);
            }
            if(so != null) {
                getProperties().put("selectedObject", so);
                while(so != null && so.getFileType() != XFileObject.SYSTEM) so = so.getParent();
                if(so != null) getProperties().put("selectedFileSystem", so);
            }
        }
    }

    private boolean isAction(String path) {
    	return StrutsProcessStructureHelper.instance.getUrlPattern(getTarget()).isActionUrl(path);
    }

    private XModelObject findInProcess(String p) {
        XModelObject[] cs = getTarget().getChildren();
        for (int i = 0; i < cs.length; i++)
          if(p.equals(cs[i].getAttributeValue(ATT_PATH)) &&
            TYPE_PAGE.equals(cs[i].getAttributeValue(ATT_TYPE))) return cs[i];
        return null;
    }

    public String getStepImplementingClass(int stepId) {
        String pkg = "org.jboss.tools.struts.ui.wizard.selectpath.";
        return (tools == 7) ? pkg + "SelectGlobalForwardPathStep" :
               (tools == 3) ? pkg + "SelectExceptionPathStep" :
               (tools == 2) ? pkg + "SelectURLPathStep" :
               (tools == 1) ? pkg + "SelectActionPathStep" :
                              pkg + "SelectActionForwardPathStep";
    }

    public void action(String name) throws Exception {
        if(OK.equals(name)) {
            setFinished(true);
            setStepId(-1);
        } else if(CANCEL.equals(name)) {
            setStepId(-1);
            getProperties().remove("selectedPath");
            setFinished(true);
        }
    }

    public boolean isActionEnabled(String name) {
        if(name.equals(OK)) {
            String tab = getProperties().getProperty("selectedTab");
            String pn = ("Actions".equals(tab)) ? "selectedAction" :
                        ("Pages".equals(tab)) ? "selectedObject" : "selectedTile";
            return (getProperties().get(pn) != null);
        }
        return true;
    }

}

