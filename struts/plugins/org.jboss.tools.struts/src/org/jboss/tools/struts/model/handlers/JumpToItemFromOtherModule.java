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
import org.jboss.tools.struts.model.helpers.*;
import org.jboss.tools.jst.web.model.ReferenceObject;

public class JumpToItemFromOtherModule extends AbstractHandler implements StrutsConstants {
    
    public boolean isEnabled(XModelObject object) {
        return (object != null && object.isActive() &&
		StrutsProcessStructureHelper.instance.isItemFromOtherModule(object));
    }

    public void executeHandler(XModelObject object, Properties p) throws Exception {
        XModelObject item = StrutsProcessStructureHelper.instance.findItemInOtherModule(object);
        if(item == null) return;
        if(item instanceof ReferenceObject) {
        	XModelObject r = ((ReferenceObject)item).getReference();
        	if(r != null) FindObjectHelper.findModelObject(r, FindObjectHelper.IN_EDITOR_ONLY);
        }
        FindObjectHelper.findModelObject(item, FindObjectHelper.IN_EDITOR_ONLY);
    }


}

