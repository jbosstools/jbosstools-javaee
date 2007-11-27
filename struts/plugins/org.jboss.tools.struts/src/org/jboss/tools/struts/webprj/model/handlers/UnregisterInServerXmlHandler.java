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
package org.jboss.tools.struts.webprj.model.handlers;

import java.util.*;
import org.jboss.tools.common.meta.action.impl.AbstractHandler;
import org.jboss.tools.common.meta.action.impl.XActionImpl;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.jst.web.server.RegistrationHelper;

public class UnregisterInServerXmlHandler extends AbstractHandler {
	String textTemplate = null;

	public boolean isEnabled(XModelObject object) {
		if(textTemplate == null) {
			textTemplate = action.getDisplayName();
		}
		if(textTemplate != null) {
			String t = textTemplate;
			int i = t.indexOf("server.xml");
			if(i >= 0) {
				t = t.substring(0, i) + "Server"/*ServerXmlHelper.getDefaultServer(2)*/ + t.substring(i + "server.xml".length());
				((XActionImpl)action).setDisplayName(t);
			}
		}
		return object != null && isRegistered(object);
	}

	public void executeHandler(XModelObject object, Properties p) throws Exception {
//		String name = "/" + object.getAttributeValue("application name");
		RegistrationHelper.unregister(EclipseResourceUtil.getProject(object));
	}
	
	boolean isRegistered(XModelObject object) {
///		String name = "/" + object.getAttributeValue("application name");
		return RegistrationHelper.isRegistered(EclipseResourceUtil.getProject(object));
	}
	
}
