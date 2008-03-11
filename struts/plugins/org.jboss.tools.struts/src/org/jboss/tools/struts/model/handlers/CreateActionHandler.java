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
package org.jboss.tools.struts.model.handlers;

import java.util.Properties;

import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.common.meta.action.impl.AbstractHandler;
import org.jboss.tools.common.model.XModelObject;

public class CreateActionHandler extends AbstractHandler {

	public boolean isEnabled(XModelObject object) {
		return (object != null && object.isObjectEditable());
	}

	public void executeHandler(XModelObject object, Properties p) throws Exception {
		///no trial
		XActionInvoker.invoke("CreateActions.CreateAction", object, p);
	}
}