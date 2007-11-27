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
package org.jboss.tools.struts.model.pv.handler;

import java.util.*;

import org.eclipse.osgi.util.NLS;

import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.common.meta.action.impl.AbstractHandler;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultRemoveHandler;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.filesystems.impl.DeleteFileHandler;
import org.jboss.tools.struts.messages.StrutsUIMessages;
import org.jboss.tools.struts.model.pv.StrutsProjectModule;
import org.jboss.tools.jst.web.model.helpers.WebAppHelper;
import org.jboss.tools.struts.webprj.model.helpers.WebModulesHelper;
import org.jboss.tools.struts.webprj.model.helpers.adopt.AdoptProjectContext;
import org.jboss.tools.struts.webprj.model.helpers.sync.StrutsWebHelper;
import org.jboss.tools.struts.webprj.model.helpers.sync.SyncProjectContext;

public class DeleteModuleHandler extends AbstractHandler {

	public void executeHandler(XModelObject object, Properties p) throws Exception {
		StrutsProjectModule pm = (StrutsProjectModule)object;
		XModelObject m = pm.getModule();
		if(m == null) return;
		String modulename = m.getAttributeValue("name");		 //$NON-NLS-1$
		boolean deleteChildren = false;
		XModelObject[] cs = pm.getTreeChildren(); 

		ServiceDialog d = object.getModel().getService();
		String message = NLS.bind(StrutsUIMessages.DELETE_MODULE,SyncProjectContext.getModuleDisplayName(modulename)); //$NON-NLS-2$
		if(cs.length > 0) {
			String cbm = StrutsUIMessages.DELETE_STRUTS_CONFIGFILE;
			if(cs.length > 1) cbm += "s"; //$NON-NLS-1$
			Properties dp = new Properties();
			dp.setProperty(ServiceDialog.DIALOG_MESSAGE, message);
			dp.setProperty(ServiceDialog.CHECKBOX_MESSAGE, cbm);
			dp.put(ServiceDialog.CHECKED, new Boolean(false));
			if(!d.openConfirm(dp)) return;
			deleteChildren = ((Boolean)dp.get(ServiceDialog.CHECKED)).booleanValue();
		} else {
			int q = d.showDialog(StrutsUIMessages.CONFIRMATION, message, new String[]{StrutsUIMessages.OK, StrutsUIMessages.CANCEL}, null, ServiceDialog.QUESTION);
			if(q != 0) return;
		}

		XModelObject webxml = WebAppHelper.getWebApp(object.getModel());
		XModelObject servlet = StrutsWebHelper.getServlet(webxml);
		XModelObject initParam = AdoptProjectContext.getWebAppInitParamForModule(servlet, modulename);
		if(initParam != null) {
			DefaultRemoveHandler.removeFromParent(initParam);
		}
		if(m != null && m.isActive()) {
			DefaultRemoveHandler.removeFromParent(m);
		}
		if(webxml != null && webxml.isModified()) {
			XActionInvoker.invoke("SaveActions.Save", webxml, null); //$NON-NLS-1$
		} else {
			XModelObject web = object.getModel().getByPath("Web"); //$NON-NLS-1$
			XModelObject[] ms = (web == null) ? new XModelObject[0] : web.getChildren(WebModulesHelper.ENT_STRUTS_WEB_MODULE);
			for (int i = 0; i < ms.length; i++) {
	            String n = ms[i].getAttributeValue(WebModulesHelper.ATTR_NAME);
				if(modulename.equals(n)) {
					DefaultRemoveHandler.removeFromParent(ms[i]);
				}
			}
		}
		if(deleteChildren) for (int i = 0; i < cs.length; i++) {
			if(cs[i].isActive()) new DeleteFileHandler().executeHandler(cs[i], null);
		}
	}

	public boolean isEnabled(XModelObject object) {
		return object instanceof StrutsProjectModule && object.isActive();
	}

}
