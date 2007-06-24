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
import org.jboss.tools.common.meta.action.impl.handlers.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.filesystems.impl.DeleteFileHandler;
import org.jboss.tools.jsf.messages.JSFUIMessages;
import org.jboss.tools.jsf.model.JSFConstants;

public class DeleteRuleHandler extends DefaultRemoveHandler implements JSFConstants {

	public boolean getSignificantFlag(XModelObject object) {
		return getPage(object) == null;
	}
	
	private XModelObject getPage(XModelObject object) {
		if(object == null) return null;
		String path = object.getAttributeValue(ATT_FROM_VIEW_ID);
		return (path == null || path.length() == 0) ? null : object.getModel().getByPath(path);
	}

	public void executeHandler(XModelObject object, java.util.Properties p) throws Exception {
		if(!isEnabled(object)) return;
		XModelObject page = getPage(object);
		if(page == null) {
			super.executeHandler(object, p);
			return;
		}
		ServiceDialog d = object.getModel().getService();
		Properties dp = new Properties();
		dp.setProperty(ServiceDialog.DIALOG_MESSAGE, JSFUIMessages.DELETE + DefaultCreateHandler.title(object, false) + "?");
		dp.put(ServiceDialog.CHECKED, new Boolean(false));
		dp.setProperty(ServiceDialog.CHECKBOX_MESSAGE, JSFUIMessages.DELETE_FILE_FROM_DISK);
		if(!d.openConfirm(dp)) return;
		Boolean b = (Boolean)dp.get(ServiceDialog.CHECKED);
		boolean delete = (b != null) && b.booleanValue();
		super.executeHandler(object, p);
		if(delete) {
			new DeleteFileHandler().executeHandler(page, null);
		} 
	}

}
