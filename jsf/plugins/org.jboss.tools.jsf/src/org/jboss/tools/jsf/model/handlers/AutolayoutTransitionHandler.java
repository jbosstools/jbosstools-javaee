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
import org.jboss.tools.common.meta.action.impl.*;

public class AutolayoutTransitionHandler extends AbstractHandler {

    public AutolayoutTransitionHandler() {}

    public boolean isEnabled(XModelObject object) {
        if(object == null || !object.isObjectEditable()) return false;
        return object.getAttributeValue(getAttr(object)).length() > 0;
    }

    public void executeHandler(XModelObject object, Properties p) throws XModelException {
        if(!isEnabled(object)) return;
        object.getModel().changeObjectAttribute(object, getAttr(object), ""); //$NON-NLS-1$
    }

    private String getAttr(XModelObject object) {
        return "shape"; //$NON-NLS-1$
    }

}
