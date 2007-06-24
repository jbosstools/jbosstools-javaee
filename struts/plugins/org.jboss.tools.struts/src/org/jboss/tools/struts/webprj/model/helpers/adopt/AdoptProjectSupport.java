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

import java.util.Properties;

import org.jboss.tools.common.meta.action.impl.*;

public class AdoptProjectSupport extends SpecialWizardSupport {
    protected AdoptProjectContext context = new AdoptProjectContext();
    protected AWStep[] steps = createSteps();
    protected AdoptProjectFinisher finisher = new AdoptProjectFinisher();
    protected String presetLocation = null;
    protected boolean presetSuccess = false;

    public AdoptProjectSupport() {
        context.setSupport(this);
        for (int i = 0; i < steps.length; i++) steps[i].setSupport(this, i);
    }

    protected AWStep[] createSteps() {
        return new AWStep[]{new AdoptProjectStepLocation(),
                            new AdoptProjectStepName(),
                            new AdoptProjectStepModules(),
                            new AdoptProjectStepFolders()};
    }

    public String getTitle() {
        return "Adopt Existing Project - " + steps[getStepId()].getTitle();
    }

    public void reset() {
        if(getProperties() == null) p = new java.util.Properties();
        p.put("context", context);
		p.put("canceled", "true");
        context.reset();
        for (int i = 0; i < steps.length; i++) {
            steps[i].reset();
            steps[i].init();
        }
		steps[0].set();
        presetLocation = p.getProperty("presetWebInfPath");
        if(presetLocation == null) {
        } else {
        	setAttributeValue(0, "location", presetLocation);
        	try {
	        	action(NEXT);
			} catch (Exception e) {
				//obsolete //ignore
				return;
			}
			int i = presetLocation.lastIndexOf('/');
			String q = presetLocation.substring(0, i);
			q = q.substring(q.lastIndexOf('/') + 1);
			setAttributeValue(1, "name", q);
			setAttributeValue(1, "web.xml location", presetLocation + "/web.xml");
			setAttributeValue(0, "location", presetLocation);
			try {
				action(NEXT);
			} catch (Exception e) {
				//obsolete //ignore
				return;
			}
			presetSuccess = true;
        }
    }

    public String[] getActionNames(int stepId) {
        int i = getStepId(), last = steps.length - 1;
        if(presetSuccess && i == 2) return new String[]{NEXT, CANCEL}; 
        return (i == 0) ? new String[]{NEXT, CANCEL} :
               (i > 0 && i < last) ? new String[]{BACK, NEXT, CANCEL} :
               (i == last) ? new String[]{BACK, FINISH, CANCEL} :
                                    new String[]{};
    }

    public void action(String name) throws Exception {
        if(FINISH.equals(name)) {
            steps[getStepId()].onNext();
            finish();
            setFinished(true);
        } else if(CANCEL.equals(name)) {
            setStepId(-1);
            getProperties().setProperty("canceled", "true");
            setFinished(true);
        } else if(NEXT.equals(name)) {
            int i = steps[getStepId()].onNext();
            setStepId(i);
            steps[i].set();
        } else if(BACK.equals(name)) {
            setStepId(getStepId() - 1);
        } else {
            steps[getStepId()].action(name);
        }
    }

    protected void finish() throws Exception {
    	Properties properties = getProperties(); 
    	if ("yes".equals(properties.getProperty("returnData")))
			properties.put("context", context);
    	else
        	finisher.execute();
		p.put("canceled", "false");
    }

    public String getMessage(int stepId) {
        return steps[stepId].getMessage();
    }

    public String getAttributeMessage(int stepId, String attrname) {
        return steps[stepId].getAttributeMessage(attrname);
    }

    public String getStepImplementingClass(int stepId) {
        if(stepId == 0) return "org.jboss.tools.struts.ui.wizard.adopt.AdoptProjectStepLocationView";
        if(stepId == 2) return "org.jboss.tools.struts.ui.wizard.adopt.AdoptProjectStepModulesView";
        return super.getStepImplementingClass(stepId);
    }

    AdoptProjectContext context() {
        return context;
    }

}
