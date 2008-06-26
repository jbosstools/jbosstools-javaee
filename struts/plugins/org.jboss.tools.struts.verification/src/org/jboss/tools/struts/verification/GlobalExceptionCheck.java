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

public class GlobalExceptionCheck extends GlobalForwardCheck {

    public GlobalExceptionCheck() {}

    protected VResult[] checkClasses() {
        VResult[] rs = checkClass("className", "class");
        if(rs != null) return rs;
        return checkClass("handler", "handler");
    }

    protected boolean isAllowedEmptyPath() {
        return true;
    }

}
 