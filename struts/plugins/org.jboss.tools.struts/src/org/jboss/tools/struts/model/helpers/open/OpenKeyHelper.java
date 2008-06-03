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

import java.util.ArrayList;

import org.eclipse.osgi.util.NLS;

import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.FindObjectHelper;
import org.jboss.tools.struts.messages.StrutsUIMessages;
import org.jboss.tools.struts.model.handlers.OpenMessageResourcesHandler;
import org.jboss.tools.struts.webprj.model.helpers.WebModulesHelper;

public class OpenKeyHelper {

	public String run(XModel model, String bundle, String key, String locale) {
		if(model == null || (key == null && bundle == null)) return null;
		if(key != null && key.length() == 0) return StrutsUIMessages.KEY_ISNOT_SPECIFIED;
		XModelObject c = findKey(model, bundle, key, locale);
		if(c == null) {
			if(key != null) return NLS.bind(StrutsUIMessages.CANNOT_FIND_PROPERTY, key); //$NON-NLS-2$
			if(bundle != null) return NLS.bind(StrutsUIMessages.CANNOT_FIND_BUNDLE, bundle); //$NON-NLS-2$
			return StrutsUIMessages.SET_REQUIRED_ATTRIBUTES;
		}
		FindObjectHelper.findModelObject(c, FindObjectHelper.IN_EDITOR_ONLY);
		return null;
	}

	public XModelObject findKey(XModel model, String bundle, String key, String locale) {
		XModelObject[] cgs = WebModulesHelper.getInstance(model).getAllConfigs();
		for (int i = 0; i < cgs.length; i++) {
			XModelObject f = cgs[i].getChildByPath("resources"); //$NON-NLS-1$
			if(f == null) continue;
			XModelObject[] rs = f.getChildren();
			for (int j = 0; j < rs.length; j++) {
				if((bundle != null && bundle.length() > 0) && !bundle.equals(rs[j].getAttributeValue("key"))) continue; //$NON-NLS-1$
				XModelObject[] os = OpenMessageResourcesHandler.getResourceObject(rs[j]);
				XModelObject r = os == null || os.length == 0 ? null : os[0];
				if(r == null) continue;
				XModelObject[] rls = getBundles(r, locale);
				if(key == null) return rls[0];
				for (int k = 0; k < rls.length; k++) {
					XModelObject ko = rls[k].getChildByPath(key);
					if(ko != null) return ko;
				}
			}
		}
		return null;
	}
	
	private XModelObject[] getBundles(XModelObject r, String locale) {
		ArrayList<XModelObject> l = new ArrayList<XModelObject>();
		if(locale != null) {
			String part = (r.getAttributeValue("name") + "_" + locale + ".properties").toLowerCase();  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			XModelObject o = r.getParent().getChildByPath(part);
			if(o != null) l.add(o);
		}
		l.add(r);
		return l.toArray(new XModelObject[0]);
	}

}
