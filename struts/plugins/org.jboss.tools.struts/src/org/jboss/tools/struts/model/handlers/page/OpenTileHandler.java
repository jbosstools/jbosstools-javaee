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
package org.jboss.tools.struts.model.handlers.page;

import java.util.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.util.FindObjectHelper;
import org.jboss.tools.common.meta.action.impl.*;
import org.jboss.tools.struts.*;
import org.jboss.tools.struts.model.helpers.*;

public class OpenTileHandler extends AbstractHandler implements StrutsConstants {

    public boolean isEnabled(XModelObject object) {
        return (object != null &&
               TYPE_PAGE.equals(object.getAttributeValue(ATT_TYPE)) &&
               SUBTYPE_TILE.equals(object.getAttributeValue(ATT_SUBTYPE)));
    }

    public void executeHandler(XModelObject object, Properties p) throws Exception {
        String path = object.getAttributeValue(ATT_PATH);
        XModelObject o = (XModelObject)TilesHelper.getTiles(object).get(path);
        if(o == null) return;
        FindObjectHelper.findModelObject(o, FindObjectHelper.IN_EDITOR_ONLY);
    }

}

