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
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.meta.action.impl.SpecialWizardSupport;
import org.jboss.tools.common.meta.action.impl.handlers.*;
import org.jboss.tools.common.meta.action.*;

public class CreateArgSupport extends SpecialWizardSupport {

    public CreateArgSupport() {}

    protected void reset() {
        if(isEditMode()) {
            XAttributeData[] d = getEntityData()[0].getAttributeData();
            for (int i = 0; i < d.length; i++) {
                String s = getTarget().getAttributeValue(d[i].getAttribute().getName());
                if(s != null) d[i].setValue(s);
            }
        }
        String n = p.getProperty("name");
        if(n != null) setAttributeValue(0, "name", n);
    }

    public void action(String name) throws Exception {
        if(OK.equals(name)) {
            execute();
            setFinished(true);
        } else if(CANCEL.equals(name)) {
            setFinished(true);
        }
    }

    private boolean isEditMode() {
       return "true".equals(action.getProperty("edit"));
    }

    private void execute() throws Exception {
        Properties p0 = extractStepData(0);
        if(isEditMode()) {
            DefaultEditHandler.edit(getTarget(), p0, getTarget().isActive());
        } else {
            Properties p = extractStepData(0);
            String arg = p.getProperty("arg");
            String entity = (arg != null) ? "ValidationA" + arg.substring(1)
            		: "ValidationArg11";
            p.remove("arg");
            XModelObject c = getTarget().getModel().createModelObject(entity, p);
            DefaultCreateHandler.addCreatedObject(getTarget(), c, getProperties());
        }
    }

}
