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
import org.jboss.tools.common.meta.action.impl.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.struts.StrutsConstants;
import org.jboss.tools.struts.messages.StrutsUIMessages;

public class CleanActionForwardAttrHandler extends AbstractHandler implements StrutsConstants {

    public CleanActionForwardAttrHandler() {}

    public void executeHandler(XModelObject object, Properties p) throws Exception {
        String attr = getAttribute(object);
        if(attr != null) object.getModel().changeObjectAttribute(object, attr, ""); //$NON-NLS-1$
    }

    public boolean getSignificantFlag(XModelObject object) {
        return true;
    }

    public boolean isEnabled(XModelObject object) {
        if(object == null) return false;
        String attr = getAttribute(object);
        if(attr == null) return false;
        ((XActionImpl)action).setDisplayName(StrutsUIMessages.CLEAN_ATTRIBUTE + attr);
        return true;
    }

    private String getAttribute(XModelObject object) {
        String v = object.getAttributeValue(ATT_FORWARD);
        if(v != null && v.length() > 0) return ATT_FORWARD;
        v = object.getAttributeValue(ATT_INCLUDE);
        if(v != null && v.length() > 0) return ATT_INCLUDE;
        v = object.getAttributeValue("parameter"); //$NON-NLS-1$
        if(v != null && v.length() > 0) return "parameter"; //$NON-NLS-1$
        return null;

    }

}
