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

public class StrutsConfigControllerCheck implements VAction {
    protected VRule rule;
    VObject object; 

    public StrutsConfigControllerCheck() {}

    public VRule getRule() {
        return rule;
    }

    public void setRule(VRule rule) {
        this.rule = rule;
    }

    public VResult[] check(VObject object) {
        this.object = object;
        ValidateTypeUtil tv = new ValidateTypeUtil();
        int tvr = tv.checkClass(object, "className", null);
        if(ValidateTypeUtil.NOT_FOUND == tvr) {
            return fire("className", "className");
        }
        tvr = tv.checkClass(object, "multipartClass", null);
        if(ValidateTypeUtil.NOT_FOUND == tvr) {
            return fire("multipartClass", "multipartClass");
        }
        tvr = tv.checkClass(object, "processorClass", null);
        if(ValidateTypeUtil.NOT_FOUND == tvr) {
            return fire("processorClass", "processorClass");
        }
        return null;
    }

    protected VResult[] fire(String id, String attr) {
        Object[] os = new Object[] {object, object.getParent()};
        VResult result = rule.getResultFactory().getResult(id, object, attr, object, attr, os);
        return new VResult[] {result};
    }
    
}
