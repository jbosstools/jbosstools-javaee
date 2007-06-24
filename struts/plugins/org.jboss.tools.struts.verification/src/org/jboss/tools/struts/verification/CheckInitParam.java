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
package org.jboss.tools.struts.verification;

import java.util.StringTokenizer;

import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.impl.XModelImpl;
import org.jboss.tools.common.verification.vrules.VObject;
import org.jboss.tools.common.verification.vrules.VResult;
import org.jboss.tools.common.verification.vrules.layer.VObjectImpl;
import org.jboss.tools.jst.web.verification.vrules.WebDefaultCheck;

public class CheckInitParam extends WebDefaultCheck {

	public VResult[] check(VObject object) {
		XModelObject o = ((VObjectImpl)object).getModelObject();
		String paramName = o.getAttributeValue("param-name");
		if(paramName == null) return null;
		if(!paramName.equals("config") && !paramName.startsWith("config/")) return null;

		String value = o.getAttributeValue("param-value");
		if(value == null || value.length() == 0) return null;
		XModel model = getXModel(object);
		XModelObject webRoot = model == null ? null : model.getByPath("FileSystems/WEB-ROOT");
		if(webRoot == null) return null;
		StringTokenizer st = new StringTokenizer(value, ",");
		while(st.hasMoreTokens()) {
			String path = st.nextToken().trim();
			if(path.length() == 0) continue;
			XModelObject fc = XModelImpl.getByRelativePath(model, path);
			if(fc == null) {
				return fire(object, "config.exists", "param-value", path);
			}
			String path2 = path.startsWith("/") ? path.substring(1) : path;
			XModelObject fc2 = webRoot.getChildByPath(path2);
			if(fc2 == null) {
				return fire(object, "config.exists", "param-value", path);
			}
			if(!fc2.getModelEntity().getName().startsWith("StrutsConfig")) {
				return fire(object, "config.valid", "param-value", path);
			}
		}		
		return null;
	}

}
