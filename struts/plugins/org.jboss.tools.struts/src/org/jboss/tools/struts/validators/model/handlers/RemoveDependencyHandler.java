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
package org.jboss.tools.struts.validators.model.handlers;

import java.util.*;

import org.eclipse.osgi.util.NLS;

import org.jboss.tools.common.model.*;
import org.jboss.tools.common.meta.action.impl.AbstractHandler;
import org.jboss.tools.struts.messages.StrutsUIMessages;

public class RemoveDependencyHandler extends AbstractHandler {

    public RemoveDependencyHandler() {}

    public boolean isEnabled(XModelObject object) {
        return object != null && object.isObjectEditable();
    }

    public void executeHandler(XModelObject object, Properties p) throws Exception {
        if(!isEnabled(object)) return;
        if(p == null) return;
        String nm = p.getProperty("dependency name"); //$NON-NLS-1$
        if(nm == null) return;
        String v = "," + object.getAttributeValue("depends") + ","; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        String wn = "," + nm + ","; //$NON-NLS-1$ //$NON-NLS-2$
        int i = v.indexOf(wn);
        if(i < 0) return;
        v = v.substring(0, i) + v.substring(i + wn.length() - 1);
        while(v.startsWith(",")) v = v.substring(1); //$NON-NLS-1$
        while(v.endsWith(",")) v = v.substring(0, v.length() - 1); //$NON-NLS-1$
        ServiceDialog d = object.getModel().getService();
        int q = d.showDialog(StrutsUIMessages.CONFIRMATION, NLS.bind(StrutsUIMessages.DELETE_RULE, nm), new String[]{StrutsUIMessages.OK, StrutsUIMessages.CANCEL}, null, ServiceDialog.QUESTION);        if(q == 0) object.getModel().changeObjectAttribute(object, "depends", v); //$NON-NLS-1$
    }

}
