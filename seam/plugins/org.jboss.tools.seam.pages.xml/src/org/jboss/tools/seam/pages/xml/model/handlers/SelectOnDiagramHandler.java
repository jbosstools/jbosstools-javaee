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
package org.jboss.tools.seam.pages.xml.model.handlers;

import java.util.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.util.FindObjectHelper;
import org.jboss.tools.common.meta.action.impl.*;
import org.jboss.tools.jst.web.model.ReferenceObject;
import org.jboss.tools.seam.pages.xml.model.SeamPagesConstants;
import org.jboss.tools.seam.pages.xml.model.helpers.SeamPagesProcessStructureHelper;

public class SelectOnDiagramHandler extends AbstractHandler implements SeamPagesConstants {

    public SelectOnDiagramHandler() {}

    public boolean isEnabled(XModelObject object) {
        return (object != null && object.isActive());
    }

    public void executeHandler(XModelObject object, Properties p) throws XModelException {
    	if(object == null) return;
    	String entity = object.getModelEntity().getName();
    	if(entity.startsWith(SeamPagesConstants.ENT_NAVIGATION_RULE)
    			|| entity.startsWith(SeamPagesConstants.ENT_RULE)) {
    		object = object.getChildByPath("target");
    	}
        XModelObject item = getItemOnProcess(object);
        if(item == null) return;
        FindObjectHelper.findModelObject(item, FindObjectHelper.IN_EDITOR_ONLY, "Diagram");
        FindObjectHelper.findModelObject(object, FindObjectHelper.IN_EDITOR_ONLY);
    }

    public static XModelObject getItemOnProcess(XModelObject object) {
        if(object == null) return null;
        XModelObject process = SeamPagesProcessStructureHelper.instance.getProcess(object);
        return (process == null) ? null : getItemOnProcess(process, object);
    }

    public static XModelObject getItemOnProcess(XModelObject processObject, XModelObject object) {
        if(processObject instanceof ReferenceObject) {
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

