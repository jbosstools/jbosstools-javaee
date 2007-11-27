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

import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.struts.messages.StrutsUIMessages;
import org.jboss.tools.struts.webprj.model.helpers.WebModulesHelper;

public class OpenResourceParameterHelper {

	public String run(XModel model, String parameter) {
		if(model == null || parameter == null) return null;
		if(parameter.length() == 0) return StrutsUIMessages.PARAMETER_ISNOT_SPECIFIED;
		XModelObject c = findBundle(model, parameter);
		if(c == null) return NLS.bind(StrutsUIMessages.CANNOT_FIND_PARAMETER, parameter); //$NON-NLS-2$
		XActionInvoker.invoke("Open", c, null); //$NON-NLS-1$
		return null;
	}

	public XModelObject findBundle(XModel model, String bundle) {
		XModelObject[] cgs = WebModulesHelper.getInstance(model).getAllConfigs();
		for (int i = 0; i < cgs.length; i++) {
			XModelObject f = cgs[i].getChildByPath("resources"); //$NON-NLS-1$
			if(f == null) continue;
			XModelObject[] rs = f.getChildren();
			for (int j = 0; j < rs.length; j++) {
				if(!bundle.equals(rs[j].getAttributeValue("parameter"))) continue; //$NON-NLS-1$
				return rs[j];
			}
		}
		return null;
	}

}
