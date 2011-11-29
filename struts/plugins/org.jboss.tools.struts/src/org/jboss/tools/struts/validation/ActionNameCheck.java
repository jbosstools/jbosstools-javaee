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
public class ActionNameCheck extends Check {

    /** Creates a new instance of ActionNameCheck */
    public ActionNameCheck(ValidationErrorManager manager, String preference) {
    	super(manager, preference, "name");
    }
    
    public void check(XModelObject object) {
        String name = (String)object.getAttributeValue("name");
        if (name.length() == 0) {
            String valid = (String)object.getAttributeValue("validate");
            if ("yes".equals(valid) || "true".equals(valid)) {
            	String message = NLS.bind(StrutsValidatorMessages.ACTION_NAME_EMPTY,  new Object[] {DefaultCreateHandler.title(object, true)});
           		fireMessage(object, message);
            }
        } else {
            XModelObject bean = object.getParent().getParent()
                .getChildByPath("form-beans").getChildByPath(name);
            if (bean == null) {
            	String message = NLS.bind(StrutsValidatorMessages.ACTION_NAME_EXISTS,  new Object[] {DefaultCreateHandler.title(object, true), name});
           		fireMessage(object, message);
            }
        }
    }
    
}
