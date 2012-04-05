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

import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateHandler;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.validation.ValidationErrorManager;
import org.jboss.tools.jst.web.validation.Check;

public class StrutsConfigControllerCheck extends Check {
    XModelObject object; 

    public StrutsConfigControllerCheck(ValidationErrorManager manager, String preference) {
    	super(manager, preference, "");
    }

    public void check(XModelObject object) {
        this.object = object;
        ValidateTypeUtil tv = new ValidateTypeUtil();
        int tvr = tv.checkClass(object, "className", null);
        if(ValidateTypeUtil.NOT_FOUND == tvr) {
            fire(StrutsValidatorMessages.CONTROLLER_CLASSNAME_EXISTS, "className");
            return;
        }
        tvr = tv.checkClass(object, "multipartClass", null);
        if(ValidateTypeUtil.NOT_FOUND == tvr) {
            fire(StrutsValidatorMessages.CONTROLLER_MULTIPART_CLASS_EXISTS, "multipartClass");
            return;
        }
        tvr = tv.checkClass(object, "processorClass", null);
        if(ValidateTypeUtil.NOT_FOUND == tvr) {
            fire(StrutsValidatorMessages.CONTROLLER_PROCESSOR_CLASS_EXISTS, "processorClass");
            return;
        }
    }

    protected void fire(String id, String attr) {
    	this.attr = attr;    	
    	String oTitle = DefaultCreateHandler.title(object, true);
    	String pTitle = DefaultCreateHandler.title(object.getParent(), true);
        fireMessage(object, id, oTitle, pTitle);
    }
    
}
