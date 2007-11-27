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

import org.jboss.tools.common.meta.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.util.*;
import org.jboss.tools.common.meta.action.impl.handlers.*;
import org.jboss.tools.struts.StrutsConstants;

public class MessageResourcesFolderAdopt implements XAdoptManager {

    public MessageResourcesFolderAdopt() {}

    public boolean isAdoptable(XModelObject target, XModelObject object) {
        return object != null && "FilePROPERTIES".equals(object.getModelEntity().getName());
    }

    public void adopt(XModelObject target, XModelObject object, java.util.Properties p) {
        String path = XModelObjectLoaderUtil.getResourcePath(object);
        String ext = ".properties";
        if(path == null || !path.toLowerCase().endsWith(ext) || !path.startsWith("/")) return;
        path = path.substring(1, path.length() - ext.length()).replace('/', '.');
        XModelObject c = findResource(target, path);
        if(c != null) {
            FindObjectHelper.findModelObject(c, 0);
        } else {
            c = object.getModel().createModelObject(StrutsConstants.ENT_MSGRES + StrutsConstants.VER_SUFFIX_11, null);
            c.setAttributeValue("parameter", path);
            DefaultCreateHandler.addCreatedObject(target, c, p);
        }
    }

    private XModelObject findResource(XModelObject target, String parameter) {
        XModelObject[] cs = target.getChildren();
        for (int i = 0; i < cs.length; i++)
          if(parameter.equals(cs[i].getAttributeValue("parameter"))) return cs[i];
        return null;
    }

}
