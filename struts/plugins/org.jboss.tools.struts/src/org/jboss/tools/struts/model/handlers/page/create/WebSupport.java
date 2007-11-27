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
package org.jboss.tools.struts.model.handlers.page.create;

import org.jboss.tools.common.meta.action.impl.*;

public class WebSupport extends SpecialWizardSupport {

    public WebSupport() {}

    public void action(String name) throws Exception {
        if(OK.equals(name)) {
            setFinished(true);
        } else if(CANCEL.equals(name)) {
            setStepId(-1);
            getProperties().remove("selectedObject");
            getProperties().setProperty("SelectPage.cancelled", "true");
            setFinished(true);
        }
    }

    public String getStepImplementingClass(int stepId) {
        return "org.jboss.tools.struts.ui.wizard.selectpage.SelectPageStep";
    }

    public boolean isActionEnabled(String name) {
        if(name.equals(OK)) {
            return (getProperties().get("selectedObject") != null);
        }
        return true;
    }

}
