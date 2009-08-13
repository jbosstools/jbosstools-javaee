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
package org.jboss.tools.jsf.model.helpers.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.osgi.util.NLS;

import org.jboss.tools.common.meta.action.XAction;
import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.util.FindObjectHelper;
import org.jboss.tools.jsf.messages.JSFUIMessages;
import org.jboss.tools.jsf.model.pv.*;
import org.jboss.tools.jst.web.model.pv.WebProjectNode;

public class OpenValidatorHelper {
	
	public String run(XModel model, String validatorId) {
		if(model == null || validatorId == null) return null;
		if(validatorId.length() == 0) return JSFUIMessages.VALIDATOR_ID_ISNOT_SPECIFIED;
		XModelObject c = findValidator(model, validatorId);
		if(c == null) return NLS.bind(JSFUIMessages.CANNOT_FIND_VALIDATOR, validatorId);
		FindObjectHelper.findModelObject(c, FindObjectHelper.IN_EDITOR_ONLY);
		return null;
	}
	
	String openClass(XModelObject c, String validatorId) {
		String className = c.getAttributeValue("validator-class");
		if(className == null || className.length() == 0) return NLS.bind(JSFUIMessages.ATTRIBUTE_VALIDATOR_CLASS_FOR_CONVERTER_ISNOT_SPECIFIED, validatorId); 
		XAction xaction = XActionInvoker.getAction("OpenSource", c);
		if(xaction != null && xaction.isEnabled(c)) {
			Properties p = new Properties();
			p.setProperty("ignoreWarning", "true");
			XActionInvoker.invoke("OpenSource", c, p);
			return p.getProperty("error");
		}
		return null;
	}

	public XModelObject findValidator(XModel model, String validatorId) {
		JSFProjectsRoot root = JSFProjectsTree.getProjectsRoot(model);
		if(root == null) return null;
		WebProjectNode n = (WebProjectNode)root.getChildByPath(JSFProjectTreeConstants.CONFIGURATION);
		if(n == null) return null;
		XModelObject[] os = n.getTreeChildren();
		for (int i = 0; i < os.length; i++) {
			XModelObject r = os[i].getChildByPath("Validators/" + validatorId);
			if(r != null) return r;
		}
		return null;
	}

	public List<Object> getValidatorIDs(XModel model) {
		JSFProjectsRoot root = JSFProjectsTree.getProjectsRoot(model);
		if(root == null) return JSFPromptingProvider.EMPTY_LIST;
		WebProjectNode n = (WebProjectNode)root.getChildByPath(JSFProjectTreeConstants.CONFIGURATION);
		if(n == null) return JSFPromptingProvider.EMPTY_LIST;
		XModelObject[] os = n.getTreeChildren();
		List<Object> list = new ArrayList<Object>();
		for (int i = 0; i < os.length; i++) {
			XModelObject c = os[i].getChildByPath("Validators");
			if(c == null) continue;
			XModelObject[] cs = c.getChildren();
			for (int j = 0; j < cs.length; j++) {
				String id = cs[j].getAttributeValue("validator-id");
				if(id != null && id.length() > 0) list.add(id);
			}
		}
		return list;
	}

}
