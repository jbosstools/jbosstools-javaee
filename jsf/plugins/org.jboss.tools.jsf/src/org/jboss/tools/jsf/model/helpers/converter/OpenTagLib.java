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

import org.eclipse.core.resources.IProject;
import org.eclipse.osgi.util.NLS;

import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.model.util.FindObjectHelper;
import org.jboss.tools.jsf.messages.JSFUIMessages;
import org.jboss.tools.jsf.model.pv.*;
import org.jboss.tools.jst.web.kb.IKbProject;
import org.jboss.tools.jst.web.kb.KbProjectFactory;
import org.jboss.tools.jst.web.kb.internal.KbObject;
import org.jboss.tools.jst.web.kb.taglib.ITagLibrary;
import org.jboss.tools.jst.web.model.pv.*;

public class OpenTagLib {
	
	public String run(XModel model, String uri, String tag, String attribute) {
		if(model == null || uri == null) return null;
		if(uri.length() == 0) return JSFUIMessages.URI_ISNOT_SPECIFIED;
		XModelObject t = findTagLib(model, uri, tag, attribute);
		if(t == null) return NLS.bind(JSFUIMessages.CANNOT_FIND_TAG_LIBRARY, uri);
		FindObjectHelper.findModelObject(t, FindObjectHelper.IN_EDITOR_ONLY);
		if(tag != null) {
			if(tag.length() == 0) return JSFUIMessages.TAG_ISNOT_SPECIFIED;
			t = findTag(t, tag);
			if(t == null) return NLS.bind(JSFUIMessages.CANNOT_FIND_TAG_IN_LIBRARY, tag, uri);
			FindObjectHelper.findModelObject(t, FindObjectHelper.IN_EDITOR_ONLY);
		}
		if(attribute != null) {
			if(attribute.length() == 0) return JSFUIMessages.ATTRIBUTE_ISNOT_SPECIFIED;
			t = findAttribute(t, attribute);
			if(t == null) return NLS.bind(JSFUIMessages.CANNOT_FIND_ATTRIBUTE_IN_TAG, attribute, tag);
			FindObjectHelper.findModelObject(t, FindObjectHelper.IN_EDITOR_ONLY);
		}
		return null;
	}
	
	private XModelObject findTagLib(XModel model, String uri, String tag, String attribute) {
		XModelObject some = null;
		JSFProjectsRoot root = JSFProjectsTree.getProjectsRoot(model);
		if(root == null) return null;
		WebProjectNode n = (WebProjectNode)root.getChildByPath("Tag Libraries");
		if(n == null) return null;
		XModelObject[] os = n.getTreeChildren();
		for (int i = 0; i < os.length; i++) {
			if(uri.equals(os[i].getAttributeValue("uri"))) {
				if(isOk(os[i], tag, attribute)) {
					return os[i];
				} else {
					some = os[i];
				}
			}
		}

		IProject project = EclipseResourceUtil.getProject(root);
		if(project != null) {
			IKbProject kb = KbProjectFactory.getKbProject(project, true);
			if(kb != null) {
				ITagLibrary[] ls = kb.getTagLibraries(uri);
				for (int i = 0; i < ls.length; i++) {
					Object id = ((KbObject)ls[i]).getId();
					if(id instanceof XModelObject) {
						XModelObject lib = (XModelObject)id;
						if(isOk(lib, tag, attribute)) {
							return lib;
						}
					}					
				}
			}
		}
		return some;
	}

	private boolean isOk(XModelObject lib, String tag, String attribute) {
		if(tag != null) {
			XModelObject t = findTag(lib, tag);
			if(t == null) return false;
			if(t != null && attribute != null) {
				XModelObject a = findAttribute(t, attribute);
				if(a == null) return false;
			}
		}
		return true;
	}
	
	private XModelObject findTag(XModelObject taglib, String name) {
		return taglib.getChildByPath(name);
	}

	private XModelObject findAttribute(XModelObject tag, String name) {
		return tag.getChildByPath(name);
	}

}
