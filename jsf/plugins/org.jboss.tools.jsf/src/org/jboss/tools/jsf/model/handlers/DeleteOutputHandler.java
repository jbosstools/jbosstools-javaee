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
package org.jboss.tools.jsf.model.handlers;

import java.util.*;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultRemoveHandler;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.jsf.JSFPreference;
import org.jboss.tools.jsf.model.JSFConstants;
import org.jboss.tools.jsf.model.helpers.JSFProcessStructureHelper;

public class DeleteOutputHandler extends DefaultRemoveHandler {

	public void executeHandler(XModelObject object, Properties p) throws Exception {
		XModelObject caseObject = JSFProcessStructureHelper.instance.getReference(object);
		if(caseObject == null) return;
		XModelObject group = object.getParent().getParent();
		XModelObject ruleObject = caseObject.getParent();
		super.removeFromParent(caseObject);
		boolean q = "yes".equals(JSFPreference.DO_NOT_CREATE_EMPTY_RULE.getValue());
		if(q && ruleObject.getChildren(JSFConstants.ENT_NAVIGATION_CASE).length == 0) {
			group.getModel().changeObjectAttribute(group, "persistent", "true");
			super.removeFromParent(ruleObject); 
		}		
	}

}
