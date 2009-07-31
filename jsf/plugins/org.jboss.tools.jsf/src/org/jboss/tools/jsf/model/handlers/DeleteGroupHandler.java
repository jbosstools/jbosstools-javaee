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
import org.jboss.tools.common.meta.action.impl.*;
import org.jboss.tools.common.meta.action.impl.handlers.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.filesystems.impl.DeleteFileHandler;
import org.jboss.tools.jsf.messages.JSFUIMessages;
import org.jboss.tools.jsf.model.*;
import org.jboss.tools.jsf.model.helpers.JSFProcessHelper;

public class DeleteGroupHandler extends AbstractHandler implements JSFConstants {
	public boolean isEnabled(XModelObject object) {
		if(object == null || !object.isObjectEditable()) return false;
		if(!ENT_PROCESS_GROUP.equals(object.getModelEntity().getName())) return false;
		ReferenceGroupImpl g = (ReferenceGroupImpl)object;
		return g.getReferences().length > 0 || "true".equals(g.getAttributeValue("persistent")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public boolean getSignificantFlag(XModelObject object) {
		return getPage(object) == null;
	}
	
	private XModelObject getPage(XModelObject object) {
		if(object == null) return null;
		String path = object.getAttributeValue(ATT_PATH);
		return (path == null || path.length() == 0) ? null : object.getModel().getByPath(path);
	}

	public void executeHandler(XModelObject object, Properties p) throws XModelException {
		if(!isEnabled(object)) return;
		ReferenceGroupImpl g = (ReferenceGroupImpl)object;

		XModelObject page = getPage(object);
		boolean deletePage = (page != null);
		Boolean pb = p == null ? null : (Boolean)p.get("deletePage"); //$NON-NLS-1$
		if(pb != null) {
			deletePage = deletePage && pb.booleanValue();
		} else if(deletePage) {
			String message = MessageFormat.format(JSFUIMessages.DeleteGroupHandler_Delete, DefaultCreateHandler.title(object, false));
			int q = confirmPageDelete(object.getModel(), message);
			if(q < 0) return;
			deletePage = (q == 0);
			if(p != null) p.put("deletePage", Boolean.valueOf(q == 0)); //$NON-NLS-1$
		}
		g.getModel().changeObjectAttribute(g, "persistent", "false"); //$NON-NLS-1$ //$NON-NLS-2$
		XModelObject[] cs = g.getReferences();
		for (int i = cs.length - 1; i >= 0; i--) {
			DefaultRemoveHandler.removeFromParent(cs[i]);
		}
		if(cs.length == 0) {
			JSFProcessHelper.getHelper(object.getParent()).updateProcess();
			
		}
		if(deletePage) {
			new DeleteFileHandler().executeHandler(page, null);
		} 
	}
	
	private int confirmPageDelete(XModel model, String message) {
		ServiceDialog d = model.getService();
		Properties dp = new Properties();
		dp.setProperty(ServiceDialog.DIALOG_MESSAGE, message);
		dp.put(ServiceDialog.CHECKED, Boolean.FALSE);
		dp.setProperty(ServiceDialog.CHECKBOX_MESSAGE, JSFUIMessages.DeleteGroupHandler_DeleteFileFromDisk);
		if(!d.openConfirm(dp)) return -1;
		Boolean b = (Boolean)dp.get(ServiceDialog.CHECKED);
		return (b != null && b.booleanValue()) ? 0 : 1;
	}

}
