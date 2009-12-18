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
package org.jboss.tools.jsf.model.pv;

import java.util.*;

import org.eclipse.core.resources.IResource;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.FileSystemsHelper;
import org.jboss.tools.jst.web.model.helpers.WebAppHelper;
import org.jboss.tools.jst.web.tld.model.TLDUtil;

public class JSFProjectTagLibs extends JSFProjectResourceBundles {
	private static final long serialVersionUID = 7805053632320764494L;

	protected Iterator<XModelObject> getRoots() {
		List<XModelObject> list = new ArrayList<XModelObject>();
		XModelObject r = getModel().getByPath("FileSystems/WEB-INF");
		if(r != null) list.add(r);
		XModelObject fss = getModel().getByPath("FileSystems");
		if(fss == null) return list.iterator();
		XModelObject[] fs = fss.getChildren("FileSystemJar");
		for (int i = 0; i < fs.length; i++) {
			if(!fs[i].getAttributeValue("name").startsWith("lib-")) continue;
			r = fs[i].getChildByPath("META-INF");
			if(r != null) list.add(r);
		}
		return list.iterator();
	}

	protected List<XModelObject> collect(Iterator<XModelObject> rs) {
		List<XModelObject> list = super.collect(rs);

		List<XModelObject> faceletTaglibs = getFaceletTaglibs();
		if(faceletTaglibs != null) for (XModelObject faceletTaglib: faceletTaglibs) {
			if(faceletTaglib.getAttributeValue("uri") != null) {
				list.add(faceletTaglib);
			}
		}

		Iterator<XModelObject> it = list.iterator();
		Set<String> tlds = new HashSet<String>();
		Set<String> facelets = new HashSet<String>();
		while(it.hasNext()) {
			XModelObject o = it.next();
			String uri = o.getAttributeValue("uri");
			boolean isFacelet = TLDUtil.isFaceletTaglib(o);
			if(uri != null && uri.length() == 0 && isFacelet) {
				uri = o.getAttributeValue("library-class");
			}
			Set<String> set = isFacelet ? facelets : tlds;
			if(set.contains(uri)) {
				it.remove();
			} else {
				set.add(uri);
			}
		}
		return list;
	}

	private List<XModelObject> getFaceletTaglibs() {
		XModelObject webxml = getModel().getByPath("/web.xml");
		XModelObject webRoot = FileSystemsHelper.getWebRoot(getModel());
		if(webxml == null || webRoot == null) return null;
		XModelObject cp = WebAppHelper.findWebAppContextParam(webxml, "facelets.LIBRARIES");
		if(cp == null) cp = WebAppHelper.findWebAppContextParam(webxml, "javax.faces.FACELETS_LIBRARIES");
		if(cp == null) return null;
		String value = cp.getAttributeValue("param-value");
		if(value == null || value.length() == 0) return null;
		List<XModelObject> result = new ArrayList<XModelObject>();
		StringTokenizer st = new StringTokenizer(value, ";,");
		while(st.hasMoreTokens()) {
			String path = st.nextToken();
			if(path.startsWith("/")) path = path.substring(1);
			XModelObject o = webRoot.getChildByPath(path);
			if(o != null) result.add(o);
		}
		return result;
	}

	static String TLD_ENTITIES = ".FileTLD_PRO.FileTLD_1_2.FileTLD_2_0.FileTLD_2_1.";

	protected boolean acceptFile(XModelObject o) {
		if("META-INF".equals(o.getParent().getAttributeValue("name"))) {
			if(TLDUtil.isFaceletTaglib(o)) return true;
		}
		return isTLDFile(o);
	}
	
	public static boolean isTLDFile(XModelObject o) {
		String entity = "." + o.getModelEntity().getName();
		return TLD_ENTITIES.indexOf(entity) >= 0;
	}

	public Object getAdapter(Class adapter) {
		if(adapter == IResource.class) {
			XModelObject o = getModel().getByPath("FileSystems/WEB-INF");
			return (o != null) ? o.getAdapter(adapter) : null;
		}
		return super.getAdapter(adapter);
	}

}
