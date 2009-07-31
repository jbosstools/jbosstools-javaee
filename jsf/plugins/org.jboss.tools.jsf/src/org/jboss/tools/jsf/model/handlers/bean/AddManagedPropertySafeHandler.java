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
package org.jboss.tools.jsf.model.handlers.bean;

import java.util.*;

import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.common.meta.action.impl.AbstractHandler;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;

public class AddManagedPropertySafeHandler extends AbstractHandler {

	public boolean isEnabled(XModelObject object) {
		if(object == null || !object.isObjectEditable()) return false;
		if(!"properties".equals(object.getAttributeValue("content-kind"))) return false; //$NON-NLS-1$ //$NON-NLS-2$
		return true;
	}

	public void executeHandler(XModelObject object, Properties p) throws XModelException {
		if(!isEnabled(object)) return;
		XActionInvoker.invoke("CreateActions.CreateProperty", object, p); //$NON-NLS-1$
	}

}
