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
package org.jboss.tools.jsf.verification.vrules;

import java.util.StringTokenizer;

import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.impl.XModelImpl;
import org.jboss.tools.jsf.web.JSFWebHelper;
import org.jboss.tools.common.verification.vrules.*;
import org.jboss.tools.common.verification.vrules.layer.VObjectImpl;

public class CheckContextParam extends JSFDefaultCheck {
	static String CONFIG_FILES_PARAM = JSFWebHelper.FACES_CONFIG_DATA.param;

	public VResult[] check(VObject object) {
		XModelObject o = ((VObjectImpl)object).getModelObject();
		if(!CONFIG_FILES_PARAM.equals(o.getAttributeValue("param-name"))) return null; //$NON-NLS-1$
		String value = o.getAttributeValue("param-value"); //$NON-NLS-1$
		if(value == null || value.length() == 0) return null;
		XModel model = getXModel(object);
		XModelObject webRoot = model == null ? null : model.getByPath("FileSystems/WEB-ROOT"); //$NON-NLS-1$
		if(webRoot == null) return null;
		StringTokenizer st = new StringTokenizer(value, ","); //$NON-NLS-1$
		while(st.hasMoreTokens()) {
			String path = st.nextToken().trim();
			if(path.length() == 0) continue;
			XModelObject fc = XModelImpl.getByRelativePath(model, path);
			if(fc == null) {
				return fire(object, "config.exists", "param-value", path); //$NON-NLS-1$ //$NON-NLS-2$
			}
			String path2 = path.startsWith("/") ? path.substring(1) : path; //$NON-NLS-1$
			XModelObject fc2 = webRoot.getChildByPath(path2);
			if(fc2 == null) {
				return fire(object, "config.exists", "param-value", path); //$NON-NLS-1$ //$NON-NLS-2$
			}
			if(!fc2.getModelEntity().getName().startsWith("FacesConfig")) { //$NON-NLS-1$
				return fire(object, "config.valid", "param-value", path); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}		
		return null;
	}

}
