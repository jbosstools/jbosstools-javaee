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
package org.jboss.tools.struts.plugins.model.handlers;

import java.util.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.options.PreferenceModelUtilities;
import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.common.meta.action.impl.*;
import org.jboss.tools.struts.messages.StrutsUIMessages;
import org.jboss.tools.struts.model.handlers.*;

public class AddMessagesThroughStrutsResourcesHandler extends AbstractHandler {

	public boolean isEnabled(XModelObject object) {
		boolean b = validateActionName();
		if(object == null && !object.isObjectEditable()) return false;
		if(OpenMessageResourcesHandler.getResourceObject(object) == null) return false;
		return b;
	}
	
	boolean validateActionName() {
		XModelObject rs = AddMessagesHandler.getTemplate(PreferenceModelUtilities.getPreferenceModel(), 0);
		String dn = (rs != null) ? StrutsUIMessages.ADD + AddMessagesHandler.getTemplateName(rs)
				: StrutsUIMessages.ADD_MESSAGES_FROM_TEMPLATE;
		((XActionImpl)action).setDisplayName(dn);
		return rs != null;
	}

	public void executeHandler(XModelObject object, Properties p) throws Exception {
		XModelObject[] os = OpenMessageResourcesHandler.getResourceObject(object);
		if(os == null || os.length == 0) return;
		object = os[0];
		XModelObject rso = AddMessagesHandler.getTemplate(PreferenceModelUtilities.getPreferenceModel(), 0);
		if(rso == null) return;
		String text = rso.getAttributeValue("text");
		XModelObject rs = AddMessagesHandler.loadResource(object.getModel(), text);
		XModelObject[] ps = rs.getChildren();
		long t = object.getTimeStamp();
		for (int i = 0; i < ps.length; i++)
		  object.addChild(ps[i]);
		if(t != object.getTimeStamp()) {
			object.setModified(true);
		} 
		XActionInvoker.invoke("Open", object, new Properties());
	}

}
