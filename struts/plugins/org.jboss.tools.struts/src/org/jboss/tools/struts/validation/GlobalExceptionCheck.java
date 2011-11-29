/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.struts.validation;

import org.jboss.tools.common.validation.ValidationErrorManager;

public class GlobalExceptionCheck extends GlobalForwardCheck {

    public GlobalExceptionCheck(ValidationErrorManager manager, String preference) {
    	super(manager, preference);
    }

    protected void checkClasses() {
        boolean b = checkClass("className", StrutsValidatorMessages.GLOBAL_EXCEPTION_CLASSNAME_EXISTS);
        if(b) {
        	checkClass("handler", StrutsValidatorMessages.GLOBAL_EXCEPTION_HANDLER_EXISTS);
        }
    }

    protected boolean isAllowedEmptyPath() {
        return true;
    }

}
