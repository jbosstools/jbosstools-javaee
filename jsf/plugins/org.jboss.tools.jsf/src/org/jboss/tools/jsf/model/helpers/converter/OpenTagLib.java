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

import org.eclipse.osgi.util.NLS;

import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.util.FindObjectHelper;
import org.jboss.tools.jsf.messages.JSFUIMessages;
import org.jboss.tools.jsf.model.pv.*;
import org.jboss.tools.jst.web.model.pv.*;

public class OpenTagLib {
	
	public String run(XModel model, String uri, String tag, String attribute) {
		if(model == null || uri == null) return null;
		if(uri.length() == 0) return JSFUIMessages.URI_ISNOT_SPECIFIED;
		XModelObject t = findTagLib(model, uri);
		if(t == null) return NLS.bind(JSFUIMessages.CANNOT_FIND_TAG_LIBRARY, uri);
		if(tag != null) {
			if(tag.length() == 0) return JSFUIMessages.TAG_ISNOT_SPECIFIED;
			t = findTag(t, tag);
			if(t == null) return NLS.bind(JSFUIMessages.CANNOT_FIND_TAG_IN_LIBRARY, tag, uri);
		}
		FindObjectHelper.findModelObject(t, FindObjectHelper.IN_EDITOR_ONLY);
		if(attribute != null) {
			if(attribute.length() == 0) return JSFUIMessages.ATTRIBUTE_ISNOT_SPECIFIED;
			t = findAttribute(t, attribute);
			if(t == null) return NLS.bind(JSFUIMessages.CANNOT_FIND_ATTRIBUTE_IN_TAG, attribute, tag);
		}
		FindObjectHelper.findModelObject(t, FindObjectHelper.IN_EDITOR_ONLY);
		return null;
	}
	
	private XModelObject findTagLib(XModel model, String uri) {
		JSFProjectsRoot root = JSFProjectsTree.getProjectsRoot(model);
		if(root == null) return null;
		WebProjectNode n = (WebProjectNode)root.getChildByPath("Tag Libraries");
		if(n == null) return null;
		XModelObject[] os = n.getTreeChildren();
		for (int i = 0; i < os.length; i++) {
			if(uri.equals(os[i].getAttributeValue("uri"))) return os[i];
		}
		return null;
	}
	
	private XModelObject findTag(XModelObject taglib, String name) {
		return taglib.getChildByPath(name);
	}

	private XModelObject findAttribute(XModelObject tag, String name) {
		return tag.getChildByPath(name);
	}

}
