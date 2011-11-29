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

import org.eclipse.osgi.util.NLS;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateHandler;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.validation.ValidationErrorManager;
import org.jboss.tools.jst.web.validation.Check;

/**
 *
 * @author  valera
 */
public class ActionTypeCheck extends Check {
    
    /** Creates a new instance of ActionTypeCheck */
    public ActionTypeCheck(ValidationErrorManager manager, String preference) {
    	super(manager, preference, "type");
    }

    public void check(XModelObject object) {
        String forward = (String)object.getAttributeValue("forward");
        String include = (String)object.getAttributeValue("include");
        if (forward.length() > 0 || include.length() > 0) return;
        String type = (String)object.getAttributeValue("type");
        String sup = "org.apache.struts.action.Action";
        ValidateTypeUtil tv = new ValidateTypeUtil();
        int tvr = tv.checkClass(object, "type", sup);
    	String oTitle = DefaultCreateHandler.title(object, true);
        if(tvr == ValidateTypeUtil.EMPTY) {
        	String message = NLS.bind(StrutsValidatorMessages.ACTION_TYPE_EMPTY, oTitle);
            fireMessage(object, message);
        } else if(tvr == ValidateTypeUtil.NOT_FOUND) {
         	String message = NLS.bind(StrutsValidatorMessages.ACTION_TYPE_EXISTS, new Object[] {oTitle, type});
            fireMessage(object, message);
        } else if(tvr == ValidateTypeUtil.WRONG_SUPER) {
         	String message = NLS.bind(StrutsValidatorMessages.ACTION_TYPE_EXTENDS, new Object[] {oTitle, type, sup});
            fireMessage(object, message);
        } else if(tvr == ValidateTypeUtil.NOT_UPTODATE) {
         	String message = NLS.bind(StrutsValidatorMessages.ACTION_TYPE_UPTODATE, new Object[] {oTitle, type});
            fireMessage(object, message);
         }
    }

}
