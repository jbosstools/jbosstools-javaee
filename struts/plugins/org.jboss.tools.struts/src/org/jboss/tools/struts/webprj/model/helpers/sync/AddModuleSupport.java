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
package org.jboss.tools.struts.webprj.model.helpers.sync;

import java.util.*;

import org.jboss.tools.common.meta.action.impl.*;
import org.jboss.tools.struts.messages.StrutsUIMessages;

public class AddModuleSupport extends SpecialWizardSupport {
    SyncProjectContext context;

    public void reset() {
        context = (SyncProjectContext)p.get("context"); //$NON-NLS-1$
    }

    public void action(String name) throws Exception {
        if(OK.equals(name)) {
            finish();
            setFinished(true);
            context = null;
        } else if(CANCEL.equals(name)) {
            setStepId(-1);
            setFinished(true);
            context = null;
        }
    }

    protected void finish() throws Exception {
        Properties p0 = extractStepData(0);
        String name = p0.getProperty("name"); //$NON-NLS-1$
        String path = p0.getProperty("path").replace('\\', '/'); //$NON-NLS-1$
        context.addModule(name, path);
    }

    static String MESSAGE = StrutsUIMessages.ENTER_UNIQUE_MODULE_NAME;
      
    static String HTML_MESSAGE = "<html><body>" + "<font style=\"font-family:arial;font-size:12;\">" + //$NON-NLS-1$ //$NON-NLS-2$
                          MESSAGE + "</font></body></html>"; //$NON-NLS-1$

    public String getMessage(int stepId) {
        return MESSAGE;
    }

	public String getStepImplementingClass(int stepId) {
		return "org.jboss.tools.struts.ui.wizard.sync.AddModuleStepView"; //$NON-NLS-1$
	}
	
	DefaultWizardDataValidator moduleValidator = new ModuleValidator();

    public WizardDataValidator getValidator(int step) {
    	moduleValidator.setSupport(this, step);
		return moduleValidator;    	
    }
    
    class ModuleValidator extends DefaultWizardDataValidator {
    	public void validate(Properties data) {
    		super.validate(data);
    		if(message != null) return;
            String name = data.getProperty("name"); //$NON-NLS-1$
            String path = data.getProperty("path").replace('\\', '/'); //$NON-NLS-1$
            message = context.getNewModuleError(name, path);
    	}
    	
    }

}
