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
import org.jboss.tools.seam.pages.xml.model.helpers.SeamPagesDiagramStructureHelper;

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
        XModelObject item = getItemOnDiagram(object);
        if(item == null) return;
        FindObjectHelper.findModelObject(item, FindObjectHelper.IN_EDITOR_ONLY, "Diagram");
        FindObjectHelper.findModelObject(object, FindObjectHelper.IN_EDITOR_ONLY);
    }

    public static XModelObject getItemOnDiagram(XModelObject object) {
        if(object == null) return null;
        XModelObject diagram = SeamPagesDiagramStructureHelper.instance.getProcess(object);
        return (diagram == null) ? null : getItemOnDiagram(diagram, object);
    }

    public static XModelObject getItemOnDiagram(XModelObject diagramObject, XModelObject object) {
        if(diagramObject instanceof ReferenceObject) {
            if(((ReferenceObject)diagramObject).getReference() == object) return diagramObject;
        }
        return getItemOnDiagram(diagramObject.getChildren(), object);
    }

    public static XModelObject getItemOnDiagram(XModelObject[] diagramObjects, XModelObject object) {
        for (int i = 0; i < diagramObjects.length; i++) {
            XModelObject cr = getItemOnDiagram(diagramObjects[i], object);
            if(cr != null) return cr;
        }
    	return null;
    }

}

