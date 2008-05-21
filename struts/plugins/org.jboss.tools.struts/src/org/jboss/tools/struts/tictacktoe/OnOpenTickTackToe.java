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
package org.jboss.tools.struts.tictacktoe;

import org.jboss.tools.common.meta.action.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.util.FindObjectHelper;
import org.jboss.tools.struts.StrutsModelPlugin;

public class OnOpenTickTackToe implements SpecialWizard {
    protected XModel model = null;

    public OnOpenTickTackToe() {}

    public void setObject(Object object) {
        model = (XModel)object;
    }

    public int execute() {
        try {
            XModelObject cg = model.getByPath("FileSystems/WEB-INF/struts-config.xml");
            waitForProcess(cg);
            FindObjectHelper.findModelObject(cg, 0);
        } catch (Exception e) {
            StrutsModelPlugin.getPluginLog().logError(e);
        }
        XModelObject cg = model.getByPath("XStudio/Options/tomcat server");
        if(cg != null) {
            XActionInvoker.invoke("ExecutionActions.Start", cg, null);
        }
        return 0;
    }

    void waitForProcess(XModelObject cg) {
        int i = 0;
        while(i < 10) {
            if(cg.getChildren().length > 0) return;
            try {
            	Thread.sleep(200);
            } catch (InterruptedException ie) {
                StrutsModelPlugin.getPluginLog().logInfo(ie.getMessage(), ie);
        	} catch (Exception e) {
                StrutsModelPlugin.getPluginLog().logError(e);
        	}
        }
    }

    public static void main(String[] args) {
///        OnOpenTickTackToe instance = new OnOpenTickTackToe();
///        instance.setObject(XModelFactory.getDefaultInstance());
///        instance.execute();
    }

}
