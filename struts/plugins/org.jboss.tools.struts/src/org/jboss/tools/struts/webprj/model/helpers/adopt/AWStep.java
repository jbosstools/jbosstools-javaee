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

import org.jboss.tools.common.meta.action.impl.*;

public abstract class AWStep {
    protected SpecialWizardSupport support;
    protected int id;

    protected AdoptProjectContext context;

    public void setSupport(SpecialWizardSupport support, int id) {
       setId(id);
       setSupport(support);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSupport(SpecialWizardSupport support) {
        this.support = support;
    }

    // get context from support
    public void reset() {
       context = ((AdoptProjectSupport)support).context();
    }

    public int onNext() throws Exception {
        support.extractStepData(id);
        validate();
        return id + 1;
    }

    public void init() {}

    public void set() {}

    protected void validate() throws Exception {}

    public void action(String name) throws Exception {}

    public String getMessage() {
        return null;
    }

    public String getTitle() {
        return "";
    }

    public String getAttributeMessage(String name) {
        return null;
    }

}

