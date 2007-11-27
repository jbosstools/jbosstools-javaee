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

import org.jboss.tools.common.model.*;
import org.jboss.tools.common.meta.action.impl.*;
import org.jboss.tools.struts.model.helpers.*;
import org.jboss.tools.struts.webprj.pattern.UrlPattern;

public class SelectPathSupport extends SpecialWizardSupport {

    public SelectPathSupport() {}

    protected void reset() {
        getProperties().put("contextProcess", getTarget());
        loadSelection();
    }
    
    private UrlPattern getUrlPattern() {
    	return StrutsProcessStructureHelper.instance.getUrlPattern(getTarget());
    }

    private void loadSelection() {
        String p = getProperties().getProperty("selectedPath");
        getProperties().remove("selectedAction");
        getProperties().remove("selectedObject");
        if(p == null || p.length() == 0) return;
        boolean isAction = getUrlPattern().isActionUrl(p); //p.endsWith(".do") || p.indexOf('.') < 0)
        XModelObject so = findInProcess(p, isAction);
        if(isAction) { 
            getProperties().setProperty("selectedTab", "Actions");
            if(so != null) getProperties().put("selectedAction", so);
        } else {
            getProperties().setProperty("selectedTab", "Pages");
            if(so != null) so = StrutsProcessStructureHelper.instance.getPhysicalPage(so);
            else so = StrutsProcessStructureHelper.instance.getPhysicalPage(getTarget(), p);
            if(so != null) getProperties().put("selectedObject", so);
        }
    }

    private XModelObject findInProcess(String p, boolean isAction) {
    	if(isAction) p = getUrlPattern().getActionPath(p); //if(p.endsWith(".do")) p = p.substring(0, p.length() - 3);
        XModelObject[] cs = getTarget().getChildren();
        for (int i = 0; i < cs.length; i++)
          if(p.equals(cs[i].getAttributeValue("path"))) return cs[i];
        return null;
    }

    public void action(String name) throws Exception {
        if(OK.equals(name)) {
            setFinished(true);
        } else if(CANCEL.equals(name)) {
            setStepId(-1);
            getProperties().remove("selectedPath");
            setFinished(true);
        }
    }

    public String getStepImplementingClass(int stepId) {
    	//TODO check if this is needed
        return super.getStepImplementingClass(stepId);
    }

    public boolean isActionEnabled(String name) {
        if(name.equals(OK)) {
            String tab = getProperties().getProperty("selectedTab");
            String pn = ("Actions".equals(tab)) ? "selectedAction" : "selectedObject";
            return (getProperties().get(pn) != null);
        }
        return true;
    }

}
