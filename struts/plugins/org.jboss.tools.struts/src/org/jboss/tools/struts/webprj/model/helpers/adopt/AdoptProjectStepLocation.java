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
package org.jboss.tools.struts.webprj.model.helpers.adopt;

import java.io.*;

import org.eclipse.osgi.util.NLS;

import org.jboss.tools.common.meta.action.impl.SpecialWizardSupport;
import org.jboss.tools.common.model.ServiceDialog;
import org.jboss.tools.struts.messages.StrutsUIMessages;
import org.jboss.tools.jst.web.project.helpers.NewWebProjectHelper;

public class AdoptProjectStepLocation extends AWStep {

    public String getTitle() {
        return StrutsUIMessages.LOCATION;
    }

    public String getAttributeMessage(String name) {
        return "Location*"; //$NON-NLS-1$
    }

    public String getMessage() {
        return StrutsUIMessages.ENTER_PATH_TO_THE_FOLDER_WHERE_STRUTSCONFIGXML_IS_LOCATED;
    }

    public int onNext() throws Exception {
        support.extractStepData(id);
        String location = support.getAttributeValue(id, "location"); //$NON-NLS-1$
        File f = new File(location);
        if(f.isFile()) f = f.getParentFile();
        else if(f.isDirectory() && !f.getName().equals("WEB-INF")) { //$NON-NLS-1$
            File f2 = new File(f, "WEB-INF"); //$NON-NLS-1$
            if(f2.isDirectory()) f = f2;
        }
        if(!f.exists() || !f.isDirectory())
          throw new RuntimeException("Please, specify existing folder"); //$NON-NLS-1$
        location = f.getAbsolutePath();
        support.setAttributeValue(id, "location", location); //$NON-NLS-1$

        String wrk = null; //obsolete code removed
        if(wrk != null) {
            int i = support.getTarget().getModel().getService().showDialog(support.getTitle(),
                    NLS.bind(StrutsUIMessages.FOLDER_ALREADY_CONTAINS_ADOPTED_PROJECT, location),
                    new String[] {SpecialWizardSupport.BACK, StrutsUIMessages.REOPEN, StrutsUIMessages.OVERWRITE}, null, ServiceDialog.WARNING);
            if (i <= 0) return 0;
            if (i == 1) {
                support.setFinished(true);
                return 0;
            }
        }
        context.setWebInfLocation(location);
        return 1;
    }

}
