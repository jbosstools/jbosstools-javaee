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

import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.FindObjectHelper;
import org.jboss.tools.struts.messages.StrutsUIMessages;
import org.jboss.tools.struts.webprj.model.helpers.WebModulesHelper;

public class OpenBundleHelper {

	public String run(XModel model, String bundle) {
		if(model == null || bundle == null) return null;
		if(bundle.length() == 0) return StrutsUIMessages.BUNDLE_ID_ISNOT_SPECIFIED;
		XModelObject c = findBundle(model, bundle);
		if(c == null) return NLS.bind(StrutsUIMessages.CANNOT_FIND_BUNDLE, bundle); //$NON-NLS-2$
		FindObjectHelper.findModelObject(c, FindObjectHelper.EVERY_WHERE);
		return null;
	}

	public XModelObject findBundle(XModel model, String bundle) {
		XModelObject[] cgs = WebModulesHelper.getInstance(model).getAllConfigs();
		for (int i = 0; i < cgs.length; i++) {
			XModelObject f = cgs[i].getChildByPath("resources"); //$NON-NLS-1$
			if(f == null) continue;
			XModelObject[] rs = f.getChildren();
			for (int j = 0; j < rs.length; j++) {
				if(!bundle.equals(rs[j].getAttributeValue("key"))) continue; //$NON-NLS-1$
				return rs[j];
			}
		}
		return null;
	}

}
