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

public class AddNavigationCaseHandler extends DefaultCreateHandler {

	protected void setOtherProperties(XModelObject object, Properties p) {
		String path = p.getProperty("to-view-id"); //$NON-NLS-1$
		path = AddViewSupport.revalidatePath(path);
		p.setProperty("to-view-id", path); //$NON-NLS-1$
		/*TRIAL_JSF*/
	}
	/*TRIAL_JSF_CLASS*/
}
