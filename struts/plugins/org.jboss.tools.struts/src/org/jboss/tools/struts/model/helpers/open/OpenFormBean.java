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
import org.jboss.tools.common.model.util.FindObjectHelper;
import org.jboss.tools.struts.messages.StrutsUIMessages;
import org.jboss.tools.struts.webprj.model.helpers.WebModulesHelper;

public class OpenFormBean {

	public String run(XModel model, String name, String formProperty) {
		if(model == null || name == null) return null;
		if(name.length() == 0) return StrutsUIMessages.FORMBEAN_ISNOT_SPECIFIED;
		XModelObject c = findFormBean(model, name);
		if(c == null) return NLS.bind(StrutsUIMessages.CANNOT_FIND_FORMBEAN, name); //$NON-NLS-2$
		if(formProperty != null) {
			c = findFormProperty(c, formProperty);
			if(c == null) return NLS.bind(StrutsUIMessages.CANNOT_FIND_FORM_PROPERTY, formProperty); //$NON-NLS-2$
		}
		FindObjectHelper.findModelObject(c, FindObjectHelper.EVERY_WHERE);
		return null;
	}

	public XModelObject findFormBean(XModel model, String name) {
		XModelObject[] cgs = WebModulesHelper.getInstance(model).getAllConfigs();
		for (int i = 0; i < cgs.length; i++) {
			XModelObject f = cgs[i].getChildByPath("form-beans/" + name); //$NON-NLS-1$
			if(f != null) return f;
		}
		return null;
	}
	
	private XModelObject findFormProperty(XModelObject formBean, String name) {
		XModelObject[] cs = formBean.getChildren();
		for (int i = 0; i < cs.length; i++) {
			if(("" + cs[i].getAttributeValue("name")).equals(name)) return cs[i]; //$NON-NLS-1$ //$NON-NLS-2$
		}
		return null;
	}

}
