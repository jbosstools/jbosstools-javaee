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
import org.jboss.tools.common.model.impl.XModelImpl;
import org.jboss.tools.common.model.util.FindObjectHelper;
import org.jboss.tools.struts.messages.StrutsUIMessages;
import org.jboss.tools.struts.model.pv.StrutsProjectsRoot;
import org.jboss.tools.struts.model.pv.StrutsProjectsTree;
import org.jboss.tools.jst.web.model.pv.WebProjectNode;
import org.jboss.tools.jst.web.project.WebProject;
import org.jboss.tools.jst.web.tld.TaglibMapping;

public class OpenTagLib {
	
	public String run(XModel model, String uri, String tag, String attribute) {
		if(model == null || uri == null) return null;
		if(uri.length() == 0) return StrutsUIMessages.URI_ISNOT_SPECIFIED;
		XModelObject t = findTagLib(model, uri);
		if(t == null) return NLS.bind(StrutsUIMessages.CANNOT_FIND_TAGLIBRARY, uri);
		if(tag != null) {
			if(tag.length() == 0) return StrutsUIMessages.TAG_ISNOT_SPECIFIED;
			t = findTag(t, tag);
			if(t == null) return NLS.bind(StrutsUIMessages.CANNOT_FIND_TAG_IN_LIBRARY, tag, uri ); 
		}
		FindObjectHelper.findModelObject(t, FindObjectHelper.IN_EDITOR_ONLY);
		if(attribute != null) {
			if(attribute.length() == 0) return StrutsUIMessages.ATTRIBUTE_ISNOT_SPECIFIED;
			t = findAttribute(t, attribute);
			if(t == null) return NLS.bind(StrutsUIMessages.CANNOT_FIND_ATTRIBUTE_IN_TAG, attribute, tag);
		}
		FindObjectHelper.findModelObject(t, FindObjectHelper.IN_EDITOR_ONLY);
		return null;
	}
	
	private XModelObject findTagLib(XModel model, String uri) {
		TaglibMapping t = WebProject.getInstance(model).getTaglibMapping();
		XModelObject o = t.getTaglibObject(uri);
		if(o != null) return o;		
		StrutsProjectsRoot root = StrutsProjectsTree.getProjectsRoot(model);
		if(root == null) return null;
		WebProjectNode n = (WebProjectNode)root.getChildByPath("Tag Libraries"); //$NON-NLS-1$
		if(n == null) return null;
		XModelObject[] os = n.getTreeChildren();
		for (int i = 0; i < os.length; i++) {
			if(uri.equals(os[i].getAttributeValue("uri"))) return os[i]; //$NON-NLS-1$
		}
		if(uri.endsWith(".tld")) { //$NON-NLS-1$
			o = XModelImpl.getByRelativePath(model, uri);
			if(o != null) return o;
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
