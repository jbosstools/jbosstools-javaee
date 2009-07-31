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
package org.jboss.tools.jsf.model.handlers;

import java.util.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.util.FindObjectHelper;
import org.jboss.tools.common.meta.action.impl.*;
import org.jboss.tools.jsf.model.*;
import org.jboss.tools.jsf.model.helpers.JSFProcessStructureHelper;
import org.jboss.tools.jst.web.model.ReferenceObject;

public class SelectOnDiagramHandler extends AbstractHandler implements JSFConstants {

    public SelectOnDiagramHandler() {}

    public boolean isEnabled(XModelObject object) {
        return (object != null && object.isActive());
    }

    public void executeHandler(XModelObject object, Properties p) throws XModelException {
        XModelObject item = getItemOnProcess(object);
        if(item == null) return;
        FindObjectHelper.findModelObject(item, FindObjectHelper.IN_EDITOR_ONLY, "Diagram"); //$NON-NLS-1$
        FindObjectHelper.findModelObject(object, FindObjectHelper.IN_EDITOR_ONLY);
    }

    public static XModelObject getItemOnProcess(XModelObject object) {
        if(object == null) return null;
        XModelObject process = JSFProcessStructureHelper.instance.getProcess(object);
        return (process == null) ? null : getItemOnProcess(process, object);
    }

    public static XModelObject getItemOnProcess(XModelObject processObject, XModelObject object) {
        if(processObject instanceof ReferenceGroupImpl) {
        	ReferenceGroupImpl g = (ReferenceGroupImpl)processObject;
        	XModelObject[] rs = g.getReferences();
        	if(rs != null && rs.length == 1 && rs[0] == object) return g;
        } else if(processObject instanceof ReferenceObject) {
            if(((ReferenceObject)processObject).getReference() == object) return processObject;
        }
        return getItemOnProcess(processObject.getChildren(), object);
    }

    public static XModelObject getItemOnProcess(XModelObject[] processObjects, XModelObject object) {
        for (int i = 0; i < processObjects.length; i++) {
            XModelObject cr = getItemOnProcess(processObjects[i], object);
            if(cr != null) return cr;
        }
    	return null;
    }

}

