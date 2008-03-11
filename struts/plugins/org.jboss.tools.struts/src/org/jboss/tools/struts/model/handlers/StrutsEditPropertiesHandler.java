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
import org.jboss.tools.common.meta.action.*;
import org.jboss.tools.common.meta.action.impl.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.struts.model.helpers.StrutsEditPropertiesContext;

public class StrutsEditPropertiesHandler extends AbstractHandler {
//    private SpecialWizard wizard = SpecialWizardFactory.createSpecialWizard("org.jboss.tools.struts.ui.wizard.editproperties.StrutsEditPropertiesWizard");

    public boolean isEnabled(XModelObject object) {
        return /*wizard != null &&*/ object != null;
    }

    public void executeHandler(XModelObject object, Properties p) throws Exception {
        if(!isEnabled(object)) return;
		SpecialWizard wizard = SpecialWizardFactory.createSpecialWizard("org.jboss.tools.struts.ui.wizard.editproperties.StrutsEditPropertiesWizard");
        StrutsEditPropertiesContext context = createContext();
        XEntityData[] entityData = this.getEntityData(object);
        context.setObject(object, (entityData == null || entityData.length < 1 ? null : entityData[0]));
        if(p == null) p = new Properties();
        p.put("context", context); // context storage
        String title = action.getProperty("title");
        if(title != null) p.setProperty("title", title);
        else p.setProperty("title", "Properties");
        wizard.setObject(p);
        wizard.execute();
    }

//    protected abstract String getWizardName();

    protected StrutsEditPropertiesContext createContext() {
        return new StrutsEditPropertiesContext();
    }
}
