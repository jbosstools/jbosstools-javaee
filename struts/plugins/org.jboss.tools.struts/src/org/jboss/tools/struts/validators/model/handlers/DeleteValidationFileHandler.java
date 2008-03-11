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
package org.jboss.tools.struts.validators.model.handlers;

import java.util.Properties;
import org.jboss.tools.common.model.ServiceDialog;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.impl.DeleteFileHandler;
import org.jboss.tools.common.model.filesystems.impl.FileAnyImpl;
import org.jboss.tools.struts.messages.StrutsUIMessages;
import org.jboss.tools.struts.validators.model.helpers.ValidatorRegistrationHelper;

public class DeleteValidationFileHandler extends DeleteFileHandler {

	public void executeHandler(XModelObject object, Properties p) throws Exception {
		boolean unregister = false;
		if(ValidatorRegistrationHelper.getInstance().isRegistered(object.getModel(), object)) {
			String checkboxMessage = StrutsUIMessages.DELETE_REFERENCE;

			ServiceDialog d = object.getModel().getService();
			Properties pd = new Properties();
			String message = StrutsUIMessages.DELETE + FileAnyImpl.toFileName(object);
			pd.setProperty(ServiceDialog.DIALOG_MESSAGE, message);
			pd.setProperty(ServiceDialog.CHECKBOX_MESSAGE, checkboxMessage);
			pd.put(ServiceDialog.CHECKED, new Boolean(true));
			if(!d.openConfirm(pd)) return;
			Boolean b = (Boolean)pd.get(ServiceDialog.CHECKED);
			unregister = b.booleanValue();
		}
    	String oldPath = ((FileAnyImpl)object).getAbsolutePath();
		super.executeHandler(object, p);
		if(object.isActive()) return;
		if(unregister) {
			ValidatorRegistrationHelper.getInstance().unregister(object.getModel(), oldPath);
		}
	}
	
	public boolean getSignificantFlag(XModelObject object) {
		return !ValidatorRegistrationHelper.getInstance().isRegistered(object.getModel(), object);
	}

}
