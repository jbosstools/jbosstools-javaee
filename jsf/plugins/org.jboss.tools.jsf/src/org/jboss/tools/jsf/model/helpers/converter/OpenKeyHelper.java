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
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.osgi.util.NLS;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.model.util.FindObjectHelper;
import org.jboss.tools.jsf.messages.JSFUIMessages;
import org.jboss.tools.jsf.model.pv.JSFProjectTreeConstants;
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
		String pathPrefix = "/" + bundle.replace('.', '/');

		IProject project = EclipseResourceUtil.getProject(model.getRoot());
		Set<IFolder> srcs = EclipseResourceUtil.getAllVisibleSourceFolders(project);
		Set<XModelObject> srcObjects = new HashSet<XModelObject>();
		for (IFolder f: srcs) {
			if(f.getProject() != project) {
				XModelObject src = EclipseResourceUtil.createObjectForResource(f);				
				if(src != null) srcObjects.add(src);
			}
		}		
		
		while(locale != null && locale.length() > 0) {
			String path = pathPrefix + "_" + locale + ".properties"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			XModelObject o = model.getByPath(path);
			if(o != null) l.add(o);
			for (XModelObject src: srcObjects) {
				o = src.getChildByPath(path.substring(1));
				if(o != null) l.add(o);
			}
			int i = locale.lastIndexOf('_');
			if(i < 0) break;
			locale = locale.substring(0, i);
		}
		String path = pathPrefix + ".properties"; //$NON-NLS-1$ //$NON-NLS-2$
		XModelObject o = model.getByPath(path);
		if(o != null) l.add(o);
		for (XModelObject src: srcObjects) {
			o = src.getChildByPath(path.substring(1));
			if(o != null) {
				l.add(o);
			}
		}
		if(!l.isEmpty()) {
			if(o == null) o = l.get(0);
			int i = bundle.lastIndexOf('.');
			String name = (i < 0) ? bundle : bundle.substring(i + 1);
			XModelObject[] ps = o.getParent().getChildren();
			for (XModelObject c: ps) {
				if(!l.contains(c)) {
					String pp = c.getPathPart();
					if(pp.endsWith(".properties") && (pp.startsWith(name + ".") || pp.startsWith(name + "_"))) {
						l.add(c);
					}
				}
				
			}
		}
		return l.toArray(new XModelObject[0]);
	}

	public XModelObject findKey(XModel model, XModelObject bundle, String key) {
		return bundle.getChildByPath(key);
	}
	
	public static String getDeafultLocale(XModel model) {
		String facesConfigLocale = getDeafultLocaleFromFacesConfig(model);
		if (facesConfigLocale.length() == 0) {
			Locale locale = Locale.getDefault();
			facesConfigLocale = locale == null || locale.toString().length() == 0 ? null : locale.toString();
		}
		return facesConfigLocale;
	}
	
	/**
	 * Gets the default locale from faces config file.
	 * 
	 * @param model XModel
	 * @return locale string or empty string if no locale was found
	 */
	public static String getDeafultLocaleFromFacesConfig(XModel model) {
		String facesConfigLocale = ""; //$NON-NLS-1$
		JSFProjectsRoot root = JSFProjectsTree.getProjectsRoot(model);
		WebProjectNode conf = root == null ? null : (WebProjectNode)root.getChildByPath(JSFProjectTreeConstants.CONFIGURATION);
		XModelObject[] fs = conf == null ? new XModelObject[0] : conf.getTreeChildren();
		for (int i = 0; i < fs.length; i++) {
			XModelObject o = fs[i].getChildByPath("application/Locale Config"); //$NON-NLS-1$
			String res = (o == null) ? "" : o.getAttributeValue("default-locale"); //$NON-NLS-1$ //$NON-NLS-2$
			if(res != null && res.length() > 0) {
				facesConfigLocale = res;
			}
		}
		return facesConfigLocale;
	}
}
