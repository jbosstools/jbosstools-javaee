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
package org.jboss.tools.struts.verification;

import org.jboss.tools.common.verification.vrules.*;
import org.jboss.tools.common.verification.vrules.layer.*;
import org.jboss.tools.jst.web.model.AbstractWebFileImpl;

public class StrutsConfigCheck implements VAction {
    protected VRule rule;

    public StrutsConfigCheck() {}

    public VRule getRule() {
        return rule;
    }

    public void setRule(VRule rule) {
        this.rule = rule;
    }

    public VResult[] check(VObject object) {
        VObjectImpl vi = (VObjectImpl)object;
        if(!(vi.getModelObject() instanceof AbstractWebFileImpl)) return null;
		AbstractWebFileImpl f = (AbstractWebFileImpl)vi.getModelObject();
        if(!f.isIncorrect()) return null;
        String errors = "\n" + f.get("errors");
        VResult result = rule.getResultFactory().getResult("valid", object, "name", object, "name", new Object[]{object, errors});
        return new VResult[] {result};
    }
    
}
