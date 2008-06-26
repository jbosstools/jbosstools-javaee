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
import org.jboss.tools.common.model.impl.trees.*;
import org.jboss.tools.struts.StrutsConstants;
import org.jboss.tools.struts.model.helpers.*;
import org.jboss.tools.struts.webprj.pattern.UrlPattern;

public class ActionTree extends DefaultSiftedTree implements StrutsConstants {
    protected XModelObject contextProcess;
    protected UrlPattern up;

    public ActionTree() {}

	public void dispose() {}
	
    public XModelObject getRoot() {
        return contextProcess;
    }

    public void setConstraint(Object object) {
        contextProcess = StrutsProcessStructureHelper.instance.getParentProcess((XModelObject)object);
        up = StrutsProcessStructureHelper.instance.getUrlPattern(contextProcess);
    }

    public boolean hasChildren(XModelObject object) {
        return (object == contextProcess);
    }

    public XModelObject[] getChildren(XModelObject object) {
        if(object != contextProcess) return new XModelObject[0];
        List<XModelObject> l = new ArrayList<XModelObject>();
        XModelObject[] cs = contextProcess.getChildren();
        for (int i = 0; i < cs.length; i++) if(accept(cs[i])) l.add(cs[i]);
        return l.toArray(new XModelObject[0]);
    }

    private boolean accept(XModelObject c) {
        String type = c.getAttributeValue(ATT_TYPE);
        return TYPE_ACTION.equals(type);
    }

    public String getPath(XModelObject o) {
        if(o == contextProcess) return "";
        String p = "" + o.getAttributeValue(ATT_PATH);
        boolean slash = p.startsWith("/");
        p = up.getActionUrl(p);
        if(!slash && p.startsWith("/")) p = p.substring(1);
        return p;
    }
}
