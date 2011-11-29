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
import org.jboss.tools.jst.web.model.AbstractWebFileImpl;
import org.jboss.tools.jst.web.validation.Check;

public class StrutsConfigCheck extends Check {

    public StrutsConfigCheck(ValidationErrorManager manager, String preference) {
    	super(manager, preference, null);
    }

    public void check(XModelObject object) {
        if(!(object instanceof AbstractWebFileImpl)) return;
		AbstractWebFileImpl f = (AbstractWebFileImpl)object;
        if(!f.isIncorrect()) return;
       	String oTitle = DefaultCreateHandler.title(object, true);
        String errors = "\n" + f.get("errors");
        String message = NLS.bind(StrutsValidatorMessages.CONFIG_VALID, oTitle, errors);
        fireMessage(object, message);
    }
    
}
