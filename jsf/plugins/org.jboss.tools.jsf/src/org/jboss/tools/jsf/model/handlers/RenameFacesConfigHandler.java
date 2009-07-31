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
import org.jboss.tools.common.meta.action.impl.handlers.DefaultEditHandler;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.impl.FileAnyImpl;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.jsf.web.JSFWebHelper;

public class RenameFacesConfigHandler extends DefaultEditHandler {
	
	public void executeHandler(XModelObject object, Properties prop) throws XModelException {
		//prompt file object to compute body.
		((FileAnyImpl)object).getAsText();
		String oldConfigName = FileAnyImpl.toFileName(object);
		super.executeHandler(object, prop);
		String newConfigName = FileAnyImpl.toFileName(object);
		XActionInvoker.invoke("SaveActions.Save", object, prop); //$NON-NLS-1$
		String path = XModelObjectLoaderUtil.getResourcePath(object);
		JSFWebHelper.registerFacesConfigRename(object.getModel(), oldConfigName, newConfigName, path);
	}
	
}
