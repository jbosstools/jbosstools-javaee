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
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.meta.*;
import org.jboss.tools.struts.model.helpers.StrutsProcessStructureHelper;

public class JSPAdopt implements XAdoptManager {

    public boolean isAdoptable(XModelObject target, XModelObject object) {
        if(!isAcceptableTarget(target)) return false;
        return isAdoptableAction(object);
    }

    public void adopt(XModelObject target, XModelObject object, java.util.Properties p) {
        if(isAdoptableAction(object)) adoptAction(target, object, p);
    }

    private boolean isAcceptableTarget(XModelObject target) {
        String te = target.getModelEntity().getName();
        return (te.equals("FileJSP") || te.startsWith("FileHTML"));
    }

    protected boolean isAdoptableAction(XModelObject object) {
        return object.getModelEntity().getName().startsWith("StrutsAction");
    }

    public void adoptAction(XModelObject target, XModelObject object, Properties p) {
        if(p == null) return;
        int c = -1;
        try { 
        	c = Integer.parseInt(p.getProperty("pos")); 
        } catch (Exception e) {
        	//ignore
        }
        if(c < 0) return;
        String path = object.getAttributeValue("path");
        path = StrutsProcessStructureHelper.instance.getUrlPattern(object).getActionUrl(path);
        String start = path;
        p.setProperty("start text", start);
    }

}

