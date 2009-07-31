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
import org.jboss.tools.common.meta.action.impl.AbstractHandler;
import org.jboss.tools.common.model.*;
import org.jboss.tools.jsf.model.*;

public class AddRuleToGroupHandler extends AbstractHandler implements JSFConstants {

	public boolean isEnabled(XModelObject object) {
		return object != null && object.isObjectEditable();
	}

	public void executeHandler(XModelObject object, Properties p) throws XModelException {
		String path = object.getAttributeValue(ATT_PATH);
		JSFNavigationModel nm = (JSFNavigationModel)object.getParent().getParent();
		XModelObject o = object.getModel().createModelObject(ENT_NAVIGATION_RULE, null);
		o.setAttributeValue(ATT_FROM_VIEW_ID, path);
		o.setAttributeValue("index", "" + nm.getRuleCount(path)); //$NON-NLS-1$ //$NON-NLS-2$
		object.getParent().getParent().getChildByPath(FOLDER_NAVIGATION_RULES).addChild(o);
	}

}
