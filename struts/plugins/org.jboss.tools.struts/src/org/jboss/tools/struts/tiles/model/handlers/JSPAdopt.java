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
package org.jboss.tools.struts.tiles.model.handlers;

import java.util.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.meta.*;

public class JSPAdopt implements XAdoptManager {

    public JSPAdopt() {}

    public boolean isAdoptable(XModelObject target, XModelObject object) {
        return isAdoptableTag(target, object);
    }

    public void adopt(XModelObject target, XModelObject object, java.util.Properties p) {
        if(isAdoptableTag(target, object)) adoptTag(target, object, p);
    }

    private boolean isAcceptableTarget(XModelObject target) {
        String te = target.getModelEntity().getName();
        return (te.equals("FileJSP") || te.startsWith("FileHTML"));
    }

    protected boolean isAdoptableTag(XModelObject target, XModelObject object) {
        String entity = object.getModelEntity().getName();
        boolean ae = "TilesDefinition".equals(entity) || "TilesPut".equals(entity);
        return (ae && isAcceptableTarget(target));
    }

    public void adoptTag(XModelObject target, XModelObject object, Properties p) {
        if(p == null) return;
        String start_text = getStartText(object);
        if(start_text != null) p.setProperty("start text", start_text);
    }

    private String getStartText(XModelObject object) {
        String entity = object.getModelEntity().getName();
        String n = object.getAttributeValue("name");
        if("TilesDefinition".equals(entity)) {
            return "<tiles:insert definition=\"" + n + "\" flush=\"true\"/>";
        } else if("TilesPut".equals(entity)) {
            return (isString(object))
                ? "<tiles:getAsString name=\"" + n + "\"/>"
                : "<tiles:insert attribute=\"" + n + "\"/>";
        }
        return null;
    }

    private boolean isString(XModelObject object) {
        String type = object.getAttributeValue("type");
        if("string".equals(type)) return true;
        if(type.length() > 0) return false;
        String value = object.getAttributeValue("value");
        if(value.indexOf(".") >= 0) return false;
        return true;
    }

}

