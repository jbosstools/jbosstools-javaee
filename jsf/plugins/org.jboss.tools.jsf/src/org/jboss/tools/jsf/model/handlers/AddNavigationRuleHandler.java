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
import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateHandler;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.jsf.model.JSFNavigationModel;

public class AddNavigationRuleHandler extends DefaultCreateHandler {

	protected void setOtherProperties(XModelObject object, Properties p) {
		String fvi = p.getProperty("from-view-id"); //$NON-NLS-1$
		JSFNavigationModel m = (JSFNavigationModel)object.getParent();
		int i = m.getRuleCount(fvi);
		p.setProperty("index", "" + i); //$NON-NLS-1$ //$NON-NLS-2$
		/*TRIAL_JSF*/
	}
/*TRIAL_JSF_CLASS*/
}
