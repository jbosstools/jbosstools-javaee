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

public class GlobalForwardCheck extends ActionForwardCheck {

    public GlobalForwardCheck() {}

    protected boolean isRelevant(VObject object) {
        return object.getParent() != null && !object.getParent().getEntity().getName().startsWith("StrutsAction");
    }

    protected VResult[] fire(String id, String attr, String info) {
        Object[] os = (info == null) ? new Object[] {object}
                      : new Object[] {object, info};
        VResult result = rule.getResultFactory().getResult(id, object, attr, object, attr, os);
        return new VResult[] {result};
    }
}
