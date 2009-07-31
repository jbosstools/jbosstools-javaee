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
import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.common.meta.action.impl.*;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateHandler;
import org.jboss.tools.common.model.*;

public class SetFactoryHandler extends AbstractHandler {
	static String NAME_FACTORY = "factory"; //$NON-NLS-1$
	static String ENTITY_FACTORY = "JSFFactory"; //$NON-NLS-1$

	public boolean isEnabled(XModelObject object) {
		return object != null && object.isObjectEditable();
	}

	public void executeHandler(XModelObject object, Properties p) throws XModelException {
		XModelObject child = object.getChildByPath(NAME_FACTORY);
		if(child == null) child = object.getModel().createModelObject(ENTITY_FACTORY, null);
		long ts = child.getTimeStamp();
		XActionInvoker.invoke("EditActions.Edit", child, p); //$NON-NLS-1$
		if(!child.isActive() && ts != child.getTimeStamp()) {
			DefaultCreateHandler.addCreatedObject(object, child, p);
		}
	}

}
