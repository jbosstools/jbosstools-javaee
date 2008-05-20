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
package org.jboss.tools.struts.model.helpers.open;

import java.util.Properties;
import org.jboss.tools.common.model.java.handlers.OpenJavaSourceHandler;
import org.eclipse.osgi.util.NLS;

import org.jboss.tools.common.model.*;
import org.jboss.tools.struts.StrutsModelPlugin;
import org.jboss.tools.struts.messages.StrutsUIMessages;

public class OpenProperty {

	public String run(XModel model, String type, String action, String property) {
		if(type == null && action != null) {
			OpenLinkActionHelper h = new OpenLinkActionHelper();
			XModelObject a = h.findAction(model, action, null);
			if(a == null) return NLS.bind(StrutsUIMessages.CANNOT_FIND_ACTION, action); //$NON-NLS-2$
			XModelObject b = findFormBean(a);
			if(b == null) return NLS.bind(StrutsUIMessages.CANNOT_FIND_FORM_BEAN_FOR_ACTION, action); //$NON-NLS-2$
			type = b.getAttributeValue("type"); //$NON-NLS-1$
			if(type == null || type.length() == 0) return NLS.bind(StrutsUIMessages.TYPE_OF_FORMBEAN_ISNOT_SET, b.getAttributeValue("name"));
		}
		if(type == null || type.length() == 0) return StrutsUIMessages.TYPE_ISNOT_SET;
		Properties p = new Properties();
		if(property != null) p.setProperty("property", property); //$NON-NLS-1$
		p.setProperty("ignoreWarning", "true"); //$NON-NLS-1$ //$NON-NLS-2$
		try {
			type = type.replace('.', '/') + ".java"; //$NON-NLS-1$
			OpenJavaSourceHandler.open(model, type, p);
			return p.getProperty("error"); //$NON-NLS-1$
		} catch (Exception e) {
			StrutsModelPlugin.getPluginLog().logError(e);
			return e.getMessage();
		}
	}
	
	XModelObject findFormBean(XModelObject action) {
		String name = action.getAttributeValue("name"); //$NON-NLS-1$
		if(name == null || name.length() == 0) return null;
		return action.getParent().getParent().getChildByPath("form-beans/" + name); //$NON-NLS-1$
	}

}
