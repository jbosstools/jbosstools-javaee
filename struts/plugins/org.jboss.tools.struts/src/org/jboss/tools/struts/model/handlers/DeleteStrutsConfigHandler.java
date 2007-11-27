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
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.filesystems.impl.*;
import org.jboss.tools.common.model.impl.XModelImpl;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.struts.messages.StrutsUIMessages;
import org.jboss.tools.struts.webprj.model.helpers.sync.StrutsWebHelper;

public class DeleteStrutsConfigHandler extends DeleteFileHandler {

	public void executeHandler(XModelObject object, Properties p) throws Exception {
		String uri = getURI(object);
		if(uri == null) return;
		boolean unregister = false;
		if(StrutsWebHelper.isConfigRegistered(object.getModel(), uri)) {
			ServiceDialog d = object.getModel().getService();
			Properties pd = new Properties();
			String message = StrutsUIMessages.DELETE_STRUTS_CONFIGFILE + FileAnyImpl.toFileName(object);
			pd.setProperty(ServiceDialog.DIALOG_MESSAGE, message);
			pd.setProperty(ServiceDialog.CHECKBOX_MESSAGE, StrutsUIMessages.DELETE_REFERENCE_FROM_WEBXML);
			pd.put(ServiceDialog.CHECKED, new Boolean(true));
			if(!d.openConfirm(pd)) return;
			Boolean b = (Boolean)pd.get(ServiceDialog.CHECKED);
			unregister = b.booleanValue();
		}
		super.executeHandler(object, p);
		if(object.isActive()) return;
		if(unregister) {
			StrutsWebHelper.unregisterConfig(object.getModel(), uri);
		}
	}
	
	private String getURI(XModelObject object) {
		String path = XModelObjectLoaderUtil.getResourcePath(object);
		if(path == null) return null;
		String uri = "/WEB-INF" + path; //$NON-NLS-1$
		if(XModelImpl.getByRelativePath(object.getModel(), path) != object) {
			uri = path;
		}
		return uri;
	}

	public boolean getSignificantFlag(XModelObject object) {
		String uri = getURI(object);
		return uri != null && !StrutsWebHelper.isConfigRegistered(object.getModel(), uri);
	}

}
