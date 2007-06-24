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
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.struts.StrutsProject;
import org.jboss.tools.struts.messages.StrutsUIMessages;
import org.jboss.tools.struts.webprj.model.helpers.adopt.*;

public class SyncProjectSupport extends SpecialWizardSupport {
    protected SyncProjectContext context = new SyncProjectContext();
    protected AWStep[] steps = createSteps();

    public SyncProjectSupport() {
        for (int i = 0; i < steps.length; i++) steps[i].setSupport(this, i);
    }

    public boolean isEnabled(XModelObject target) {
    	if(!super.isEnabled(target)) return false;
    	return EclipseResourceUtil.hasNature(target.getModel(), StrutsProject.NATURE_ID);
    }

    protected AWStep[] createSteps() {
        return new AWStep[]{new SyncProjectStep(), new SyncProjectStepWarning()};
    }

    public String getTitle() {
        return "Modules Configuration"; //$NON-NLS-1$
    }

    public void reset() {
        if(getProperties() == null) p = new java.util.Properties();
		p.setProperty("cancel", "true"); //$NON-NLS-1$ //$NON-NLS-2$
        p.put("context", context); //$NON-NLS-1$
        p.putAll(context.data);
        context.setModel(getTarget().getModel());
        context.update(true);
        for (int i = 0; i < steps.length; i++) {
            steps[i].reset();
            steps[i].init();
        }
        //if(!context.isWebXMLCorrect()) setStepId(1); else 
        setStepId(0);
        steps[getStepId()].set();
    }

    public String[] getActionNames(int stepId) {
        if(stepId == 1) return new String[]{OK, HELP};
        return new String[]{FINISH, CANCEL, HELP};
    }

    public void action(String name) throws Exception {
        if(FINISH.equals(name)) {
///            steps[getStepId()].onNext();
            if(!finish()) return;
			p.setProperty("cancel", "false"); //$NON-NLS-1$ //$NON-NLS-2$
            setFinished(true);
        } else if(CANCEL.equals(name) || OK.equals(name)) {
            setStepId(-1);
            setFinished(true);
		} else if(HELP.equals(name)) {
			help();
        } else {
            steps[getStepId()].action(name);
        }
    }

    protected boolean finish() throws Exception {
        return context.apply();
    }

	protected SyncValidator syncValidator = new SyncValidator();
    
	public WizardDataValidator getValidator(int step) {
		syncValidator.setSupport(this, step);
		return syncValidator;    	
	}

    public String getMessage(int stepId) {
        return steps[stepId].getMessage();
    }

    public String getAttributeMessage(int stepId, String attrname) {
        return steps[stepId].getAttributeMessage(attrname);
    }
    
    String pack = "org.jboss.tools.struts.ui.wizard.sync."; //$NON-NLS-1$

    public String getStepImplementingClass(int stepId) {
        if(stepId == 1) return pack + "SyncProjectStepWarningView"; //$NON-NLS-1$
        return pack + "SyncProjectStepView"; //$NON-NLS-1$
    }
    
    class SyncValidator extends DefaultWizardDataValidator {
		public void validate(Properties data) {
			message = null;
			if(getStepId() == 1) return;
			XModelObject selected = (XModelObject)data.get("selected"); //$NON-NLS-1$
			ArrayList<XModelObject> list = (ArrayList)data.get("modules"); //$NON-NLS-1$
			XModelObject[] modules = list.toArray(new XModelObject[0]);
			if(modules == null) return;
			message = context.getErrorMessage(modules, selected);			
		}
    }

}

class SyncProjectStep extends AWStep {
    public void reset() {}
}

class SyncProjectStepWarning extends AWStep {
    public void reset() {}

    public String getMessage() {
        SyncProjectContext context = (SyncProjectContext)support.getProperties().get("context"); //$NON-NLS-1$
        if(!context.isWebXMLFound()) return StrutsUIMessages.WEBXML_ISNOT_FOUND;
        if(!context.isWebXMLCorrect()) return StrutsUIMessages.WEBXML_ISNOT_CORRECT;
        return null;
    }

}

