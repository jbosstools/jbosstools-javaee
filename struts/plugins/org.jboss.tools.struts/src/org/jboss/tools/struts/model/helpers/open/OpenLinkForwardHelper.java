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

import org.eclipse.osgi.util.NLS;

import org.jboss.tools.common.model.*;
import org.jboss.tools.struts.messages.StrutsUIMessages;
import org.jboss.tools.struts.model.handlers.JumpByForwardPathHandler;
import org.jboss.tools.struts.webprj.model.helpers.WebModulesHelper;

public class OpenLinkForwardHelper {

	public String run(XModel model, String forward) {
		if(model == null || forward == null) return null;
		if(forward.length() == 0) return StrutsUIMessages.FORWARD_ISNOT_SPECIFIED;
		XModelObject c = findForward(model, forward);
		if(c == null) return NLS.bind(StrutsUIMessages.CANNOT_FIND_FORWARD,forward);
		JumpByForwardPathHandler.doOpenTarget(c);
		return null;
	}

	public XModelObject findForward(XModel model, String forward) {
		XModelObject[] cgs = WebModulesHelper.getInstance(model).getAllConfigs();
		for (int i = 0; i < cgs.length; i++) {
			XModelObject f = cgs[i].getChildByPath("global-forwards"); //$NON-NLS-1$
			if(f == null) continue;
			XModelObject[] rs = f.getChildren();
			for (int j = 0; j < rs.length; j++) {
				if(!forward.equals(rs[j].getAttributeValue("name"))) continue; //$NON-NLS-1$
				return rs[j];
			}
		}
		return null;
	}

}
