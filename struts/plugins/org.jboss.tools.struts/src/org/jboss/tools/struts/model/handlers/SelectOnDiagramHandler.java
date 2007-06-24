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
import org.jboss.tools.common.model.util.FindObjectHelper;
import org.jboss.tools.common.meta.action.impl.*;
import org.jboss.tools.struts.*;
import org.jboss.tools.struts.model.*;
import org.jboss.tools.struts.model.helpers.*;

public class SelectOnDiagramHandler extends AbstractHandler implements StrutsConstants {

    public SelectOnDiagramHandler() {}

    public boolean isEnabled(XModelObject object) {
        return (object != null && object.isActive());
    }

    public void executeHandler(XModelObject object, Properties p) throws Exception {
        XModelObject item = getItemOnProcess(object);
        if(item == null) return;
        FindObjectHelper.findModelObject(item, FindObjectHelper.IN_EDITOR_ONLY, "Diagram");
        FindObjectHelper.findModelObject(object, FindObjectHelper.IN_EDITOR_ONLY);
        
    }

    public static XModelObject getItemOnProcess(XModelObject object) {
        if(object == null) return null;
        XModelObject process = StrutsProcessStructureHelper.instance.getProcess(object);
        return (process == null) ? null : getItemOnProcess(process, object);
    }

    public static XModelObject getItemOnProcess(XModelObject parent, XModelObject object) {
        XModelObject[] cs = parent.getChildren();
        for (int i = 0; i < cs.length; i++) {
            if(!(cs[i] instanceof ReferenceObjectImpl)) continue;
            ReferenceObjectImpl r = (ReferenceObjectImpl)cs[i];
            if(r.getReference() == object) return cs[i];
            XModelObject cr = getItemOnProcess(cs[i], object);
            if(cr != null) return cr;
        }
        return null;
    }

}

