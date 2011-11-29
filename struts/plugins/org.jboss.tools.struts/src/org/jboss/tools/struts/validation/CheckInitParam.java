/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.struts.validation;

import java.util.StringTokenizer;

import org.eclipse.osgi.util.NLS;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.impl.XModelImpl;
import org.jboss.tools.common.validation.ValidationErrorManager;
import org.jboss.tools.jst.web.validation.Check;

public class CheckInitParam extends Check {

	public CheckInitParam(ValidationErrorManager manager, String preference) {
    	super(manager, preference, "param-value");
    }

	public void check(XModelObject object) {
		XModelObject o = object;
		String paramName = object.getAttributeValue("param-name");
		if(paramName == null) return;
		XModelObject parent = object.getParent();
		if(parent == null || !"WebAppServlet".equals(parent.getModelEntity().getName())) {
			return;
		}
		if(!paramName.equals("config") && !paramName.startsWith("config/")) return;

		String value = o.getAttributeValue("param-value");
		if(value == null || value.length() == 0) return;
		XModel model = object.getModel();
		XModelObject webRoot = model == null ? null : model.getByPath("FileSystems/WEB-ROOT");
		if(webRoot == null) return;
		StringTokenizer st = new StringTokenizer(value, ",");
		while(st.hasMoreTokens()) {
			String path = st.nextToken().trim();
			if(path.length() == 0) continue;
			XModelObject fc = XModelImpl.getByRelativePath(model, path);
			if(fc == null) {
				String message = NLS.bind(StrutsValidatorMessages.RESOURCE_EXISTS, path);
				fireMessage(object, message);
			}
			String path2 = path.startsWith("/") ? path.substring(1) : path;
			XModelObject fc2 = webRoot.getChildByPath(path2);
			if(fc2 == null) {
				String message = NLS.bind(StrutsValidatorMessages.RESOURCE_EXISTS, path);
				fireMessage(object, message);
			} else if(!fc2.getModelEntity().getName().startsWith("StrutsConfig")) {
				String message = NLS.bind(StrutsValidatorMessages.CONFIG_VALID, "param-value", path);
				fireMessage(object, message);
			}
		}		
	}

}
