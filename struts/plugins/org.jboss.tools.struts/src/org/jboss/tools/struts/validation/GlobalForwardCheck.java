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

public class GlobalForwardCheck extends ActionForwardCheck {

	public GlobalForwardCheck(ValidationErrorManager manager, String preference) {
		super(manager, preference);
		pathEmpty = StrutsValidatorMessages.GLOBAL_FORWARD_PATH_EMPTY;
		pathExists = StrutsValidatorMessages.GLOBAL_FORWARD_PATH_EXISTS;
		classNameExists = StrutsValidatorMessages.GLOBAL_FORWARD_CLASSNAME_EXISTS;
		contextRelativeCross = StrutsValidatorMessages.GLOBAL_FORWARD_CONTEXT_RELATIVE_CROSS;
		contextRelativeMono = StrutsValidatorMessages.GLOBAL_FORWARD_CONTEXT_RELATIVE_MONO;
	}

    protected boolean isRelevant(XModelObject object) {
        return object.getParent() != null && !object.getParent().getModelEntity().getName().startsWith("StrutsAction");
    }

	protected void fire(String id, String attr, String info) {
		this.attr = attr;
		String oTitle = DefaultCreateHandler.title(object, true);
		String[] os = (info == null) ? new String[] {oTitle}
					: new String[] {oTitle, info};
		fireMessage(object, id, os);
	}
}
