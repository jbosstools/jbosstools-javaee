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
import org.jboss.tools.struts.model.*;

public class SelectInNavigatorHandler extends AbstractHandler {

    public SelectInNavigatorHandler() {}

    public boolean isEnabled(XModelObject object) {
        return (object != null && object.isActive() && getItemInConfig(object) != null);
    }

    public void executeHandler(XModelObject object, Properties p) throws Exception {
        XModelObject item = getItemInConfig(object);
        if(item != null) FindObjectHelper.findModelObject(item, FindObjectHelper.EVERY_WHERE);
    }

    public static XModelObject getItemInConfig(XModelObject object) {
        if(!(object instanceof ReferenceObjectImpl)) return null;
        ReferenceObjectImpl ro = (ReferenceObjectImpl)object;
        return ro.getReference();
    }

}
