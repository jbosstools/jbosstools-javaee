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
import org.jboss.tools.common.meta.action.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.util.*;
import org.jboss.tools.common.meta.action.impl.*;
import org.jboss.tools.common.meta.action.impl.handlers.*;
import org.jboss.tools.struts.messages.StrutsUIMessages;

public class AddFormsetSupport extends SpecialWizardSupport {

    public AddFormsetSupport() {}

    public String getMessage(int stepId) {
        return StrutsUIMessages.LEAVE_FIELDS_EMPTY;
    }

    public void action(String name) throws Exception {
        if(CANCEL.equals(name)) {
            setFinished(true);
        } else if(OK.equals(name)) {
            finish();
            setFinished(true);
        }
    }

    private void finish() throws Exception {
        Properties p0 = extractStepData(0);
        XModelObject fs = findFormset(getTarget(), p0);
        if(fs != null) {
            ServiceDialog d = getTarget().getModel().getService();
            String mes = StrutsUIMessages.ALREADY_EXISTS + fs.getPresentationString() + " already exists. Do you want to create additional one?"; //$NON-NLS-2$
            int i = d.showDialog(StrutsUIMessages.ADD_FORMSET, mes, new String[]{StrutsUIMessages.OK, StrutsUIMessages.CANCEL}, null, ServiceDialog.WARNING);
            if(i != 0) return;
        }
        String entity = getEntityData()[0].getModelEntity().getName();
        XModelObject c = XModelObjectLoaderUtil.createValidObject(getTarget().getModel(), entity, p0);
        if("false".equals(getTarget().get("expanded"))) //$NON-NLS-1$ //$NON-NLS-2$
          XActionInvoker.invoke("SetExpanded", getTarget(), null); //$NON-NLS-1$
        DefaultCreateHandler.addCreatedObject(getTarget(), c, getProperties());
    }

    private XModelObject findFormset(XModelObject f, Properties p) {
        String u = p.getProperty("language", "") + ":" + p.getProperty("country", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
        XModelObject[] cs = f.getChildren();
        for (int i = 0; i < cs.length; i++) {
            String u2 = cs[i].get("language") + ":" + cs[i].get("country"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            if(u2.equals(u)) return cs[i];
        }
        return null;
    } 

}
