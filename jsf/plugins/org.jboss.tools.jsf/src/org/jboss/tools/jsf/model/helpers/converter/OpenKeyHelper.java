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
import java.util.Locale;

import org.eclipse.osgi.util.NLS;

import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.FindObjectHelper;
import org.jboss.tools.jsf.messages.JSFUIMessages;
import org.jboss.tools.jsf.model.pv.JSFProjectsRoot;
import org.jboss.tools.jsf.model.pv.JSFProjectsTree;
import org.jboss.tools.jst.web.model.pv.WebProjectNode;

public class OpenKeyHelper {

	public String run(XModel model, String bundle, String locale) {
		if(model == null) return null;
		if(bundle == null || bundle.length() == 0) return JSFUIMessages.BUNDLE_IS_NOT_SPECIFIED;
		XModelObject[] bs = findBundles(model, bundle, locale);
		if(bs.length == 0) return NLS.bind(JSFUIMessages.CANNOT_FIND_BUNDLE, bundle);
		FindObjectHelper.findModelObject(bs[0], FindObjectHelper.EVERY_WHERE);
		return null;
	}

	public String run(XModel model, String bundle, String key, String locale) {
		if(model == null) return null;
		if(key == null || key.length() == 0) return JSFUIMessages.KEY_ISNOT_SPECIFIED;
		if(bundle == null || bundle.length() == 0) return JSFUIMessages.BUNDLE_IS_NOT_SPECIFIED;
		XModelObject[] bs = findBundles(model, bundle, locale);
		if(bs.length == 0) return NLS.bind(JSFUIMessages.CANNOT_FIND_BUNDLE, bundle);
		XModelObject c = null;
		for (int i = 0; i < bs.length && c == null; i++) {
			c = findKey(model, bs[i], key);
		}
		if(c == null) return NLS.bind(JSFUIMessages.CANNOT_FIND_PROPERTY, key);
		FindObjectHelper.findModelObject(c, FindObjectHelper.IN_EDITOR_ONLY);
		return null;
	}
	
	public XModelObject[] findBundles(XModel model, String bundle, String locale) {
		ArrayList<XModelObject> l = new ArrayList<XModelObject>();
		if(locale == null || locale.length() == 0) locale = getDeafultLocale(model);
		while(locale != null && locale.length() > 0) {
			String path = "/" + bundle.replace('.', '/') + "_" + locale + ".properties"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			XModelObject o = model.getByPath(path);
			if(o != null) l.add(o);
			int i = locale.lastIndexOf('_');
			if(i < 0) break;
			locale = locale.substring(0, i);
		}
		String path = "/" + bundle.replace('.', '/') + ".properties"; //$NON-NLS-1$ //$NON-NLS-2$
		XModelObject o = model.getByPath(path);
		if(o != null) l.add(o);
		return l.toArray(new XModelObject[0]);
	}

	public XModelObject findKey(XModel model, XModelObject bundle, String key) {
		return bundle.getChildByPath(key);
	}
	
	public String getDeafultLocale(XModel model) {
		JSFProjectsRoot root = JSFProjectsTree.getProjectsRoot(model);
		WebProjectNode conf = root == null ? null : (WebProjectNode)root.getChildByPath("Configuration"); //$NON-NLS-1$
		XModelObject[] fs = conf == null ? new XModelObject[0] : conf.getTreeChildren();
		for (int i = 0; i < fs.length; i++) {
			XModelObject o = fs[i].getChildByPath("application/Locale Config"); //$NON-NLS-1$
			String res = (o == null) ? "" : o.getAttributeValue("default-locale"); //$NON-NLS-1$ //$NON-NLS-2$
			if(res != null && res.length() > 0) return res;
		}
		Locale locale = Locale.getDefault();
		return locale == null || locale.toString().length() == 0 ? null : locale.toString();
	}

}
