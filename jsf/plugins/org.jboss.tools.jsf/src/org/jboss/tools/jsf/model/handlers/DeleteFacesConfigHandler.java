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

import java.text.MessageFormat;
import java.util.Properties;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.filesystems.impl.*;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.jsf.messages.JSFUIMessages;
import org.jboss.tools.jsf.web.JSFWebHelper;

public class DeleteFacesConfigHandler extends DeleteFileHandler {

	public void executeHandler(XModelObject object, Properties p) throws XModelException {
		String path = XModelObjectLoaderUtil.getResourcePath(object);
		if(object.getModel().getByPath("FileSystems/WEB-INF" + path) == object) { //$NON-NLS-1$
			path = "/WEB-INF" + path; //$NON-NLS-1$
		}
		boolean unregister = false;
		if(JSFWebHelper.isRegisterFacesConfig(object.getModel(), path)) {
			ServiceDialog d = object.getModel().getService();
			Properties pd = new Properties();
			String message = MessageFormat.format(JSFUIMessages.DeleteFacesConfigHandler_DeleteFacesConfig, FileAnyImpl.toFileName(object));
			pd.setProperty(ServiceDialog.DIALOG_MESSAGE, message);
			pd.setProperty(ServiceDialog.CHECKBOX_MESSAGE, JSFUIMessages.DeleteFacesConfigHandler_DeleteReferenceFromWebXML);
			pd.put(ServiceDialog.CHECKED, Boolean.TRUE);
			if(!d.openConfirm(pd)) return;
			Boolean b = (Boolean)pd.get(ServiceDialog.CHECKED);
			unregister = b.booleanValue();
		}
		super.executeHandler(object, p);
		if(object.isActive()) return;
		if(unregister) {
			JSFWebHelper.unregisterFacesConfig(object.getModel(), path);
		}
	}

	public boolean getSignificantFlag(XModelObject object) {
		String path = XModelObjectLoaderUtil.getResourcePath(object);
		return !JSFWebHelper.isRegisterFacesConfig(object.getModel(), path);
	}

}
