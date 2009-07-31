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
package org.jboss.tools.jsf.model.handlers;

import java.util.*;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.source.ISourceViewer;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.filesystems.XFileObject;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.meta.*;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.model.pv.JSFProjectBean;
import org.jboss.tools.jsf.project.JSFNature;
import org.jboss.tools.jsf.web.JSFWebProject;
import org.jboss.tools.jsf.web.pattern.JSFUrlPattern;
import org.jboss.tools.jst.web.project.WebProject;
import org.jboss.tools.jst.web.tld.TaglibData;
import org.jboss.tools.jst.web.tld.VpeTaglibManager;
import org.jboss.tools.jst.web.tld.VpeTaglibManagerProvider;
import org.jboss.tools.jst.web.tld.model.TLDUtil;

public class JSPAdopt implements XAdoptManager {

    public boolean isAdoptable(XModelObject target, XModelObject object) {
        if(!isAcceptableTarget(target)) return false;
        return isAdoptableProperty(object) ||
		       isAdoptablePropertyReference(object) || 
               isAdoptablePage(object) ||
               isAdoptableFile(object) ||
               isAdoptableMapEntry(object);
    }

    public void adopt(XModelObject target, XModelObject object, java.util.Properties p) throws XModelException {
        if(isAdoptableProperty(object)) adoptProperty(target, object, p);
		else if(isAdoptablePropertyReference(object)) adoptPropertyReference(target, object, p);
		else if(isAdoptablePage(object)) adoptPage(target, object, p);
        else if(isAdoptableFile(object)) adoptFile(target, object, p);
		else if(isAdoptableMapEntry(object)) adoptMapEntry(target, object, p);
    }

    static String PAGE_TARGET = ".FileJSP.FileHTML.FileXHTML.FacesConfig."; //$NON-NLS-1$
    static String PAGE_ENTITY = ".FileJSP.FileXHTML."; //$NON-NLS-1$

	private boolean isAcceptableTarget(XModelObject target) {
		String entity = "." + target.getModelEntity().getName() + "."; //$NON-NLS-1$ //$NON-NLS-2$
		return PAGE_TARGET.indexOf(entity) >= 0;
	}

    protected boolean isAdoptableProperty(XModelObject object) {
        return object.getModelEntity().getName().startsWith("JSFManagedProperty"); //$NON-NLS-1$
    }

    public void adoptProperty(XModelObject target, XModelObject object, Properties p) {
        if(p == null) return;
        int c = getPos(p);
        if(c < 0) return;
		String bean = object.getParent().getAttributeValue("managed-bean-name"); //$NON-NLS-1$
        String name = object.getAttributeValue("property-name"); //$NON-NLS-1$
        String start = "#{" + bean + "." + name + "}"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        p.setProperty("start text", start); //$NON-NLS-1$
    }

	protected boolean isAdoptablePage(XModelObject object) {
		String entity = "." + object.getModelEntity().getName() + "."; //$NON-NLS-1$ //$NON-NLS-2$
		if(PAGE_ENTITY.indexOf(entity) < 0  || !EclipseResourceUtil.hasNature(object.getModel(), JSFNature.NATURE_ID)) return false;
		String path = WebProject.getInstance(object.getModel()).getPathInWebRoot(object);
		return path != null;
	}

	public void adoptPage(XModelObject target, XModelObject object, Properties p) {
		if(p == null) return;
		String res = WebProject.getInstance(object.getModel()).getPathInWebRoot(object);
		if(res == null) return;
		if(applyPattern(p)) {
			JSFUrlPattern pattern = JSFWebProject.getInstance(object.getModel()).getPatternLoader().getUrlPattern();
			res = pattern.getJSFUrl(res);
		}
        int pos = getPos(p);
		if(res.startsWith("/") && pos >= 0 && isInsideResponseRedirect(p.getProperty("text"), pos)) { //$NON-NLS-1$ //$NON-NLS-2$
			res = res.substring(1);
		}
		p.setProperty("start text", res); //$NON-NLS-1$
		p.setProperty("end text", ""); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	static String NO_JSF_URL = 
		"+include+jsp:include+jsp:directive.include+ui:include+ui:composition+ui:decorate+s:decorate+"; //$NON-NLS-1$
	static Map<String, String> PREFIXES = new HashMap<String, String>();
	{
		PREFIXES.put("http://jboss.com/products/seam/taglib", "s"); //$NON-NLS-1$ //$NON-NLS-2$
		PREFIXES.put("http://java.sun.com/jsf/facelets", "ui"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	boolean applyPattern(Properties p) {
		if(p == null) return true;
		String tag = p.getProperty("context:tagName"); //$NON-NLS-1$
		if(tag == null) return true;
		int q = tag.indexOf(':');
		if(q >= 0) {
			String dp = tag.substring(0, q);
			ISourceViewer sv = (ISourceViewer)p.get("viewer"); //$NON-NLS-1$
			String uri = getURI(sv, dp);
			if(uri != null) {
				String dp1 = PREFIXES.get(uri);
				if(dp1 != null && !dp1.equals(dp)) {
					tag = dp1 + tag.substring(q);
				}
			}
		}
		if(NO_JSF_URL.indexOf("+" + tag + "+") >= 0) return false;		 //$NON-NLS-1$ //$NON-NLS-2$
		return true;
	}
	private static String getURI(ISourceViewer viewer, String prefix) {
		VpeTaglibManager tldManager = null;
		if((tldManager == null) && (viewer instanceof VpeTaglibManagerProvider)) {
			tldManager = ((VpeTaglibManagerProvider)viewer).getTaglibManager();
			if(tldManager != null) {
				List list = tldManager.getTagLibs();
				for (int i = 0; i < list.size(); i++) {
					TaglibData data = (TaglibData)list.get(i);
					if(prefix.equals(data.getPrefix())) {
						return data.getUri();
					}
				}
			}			
		}
		return null;
	}

    protected boolean isAdoptableFile(XModelObject object) {
    	if(XFileObject.FILE != object.getFileType()) return false;
    	if(TLDUtil.isTaglib(object)) return false;
    	IResource c = (IResource)object.getAdapter(IResource.class);
    	if(c == null) return false;
    	String webroot = WebProject.getInstance(object.getModel()).getWebRootLocation();
    	if(webroot == null) return false;
    	IPath path = c.getLocation();
    	// if this happens it means that something is wrong 
    	// with reference to IResource in object or its parent
    	// that may need attention.
    	if(path == null || path.toString() == null) return false;
    	String f = path.toString().replace('\\', '/').toLowerCase();
    	webroot = webroot.replace('\\', '/').toLowerCase();
        return f.startsWith(webroot);
    }

    public void adoptFile(XModelObject target, XModelObject object, Properties p) {
        if(p == null) return;
		String webroot = WebProject.getInstance(object.getModel()).getWebRootLocation();
		IResource c = (IResource)object.getAdapter(IResource.class);
		String f = c.getLocation().toString().replace('\\', '/');
		String res = f.substring(webroot.length());
        p.setProperty("start text", res); //$NON-NLS-1$
        p.setProperty("end text", ""); //$NON-NLS-1$ //$NON-NLS-2$
    }

	protected boolean isAdoptablePropertyReference(XModelObject object) {
		String entity = "." + object.getModelEntity().getName() + "."; //$NON-NLS-1$ //$NON-NLS-2$
		return ".JSFProjectBeanProperty.JSFProjectBeanMethod.".indexOf(entity) >= 0; //$NON-NLS-1$
	}

	public void adoptPropertyReference(XModelObject target, XModelObject object, Properties p) {
		if(p == null) return;
        int c = getPos(p);
		if(c < 0) return;
		String s = object.getAttributeValue("name"); //$NON-NLS-1$
		XModelObject o = object;
		while(o != null && isAdoptablePropertyReference(o)) {
			o = o.getParent();
			if(o != null) {
				String part = o.getAttributeValue("name"); //$NON-NLS-1$
				if(o instanceof JSFProjectBean) {
					XModelObject[] list = ((JSFProjectBean)o).getBeanList();
					if(list.length > 1) {
						part = SelectBeanSupport.run((JSFProjectBean)o);
						if(part == null) return;
					}
				}
				s = part + "." + s; //$NON-NLS-1$
			}
		}
		String start = "#{" + s + "}"; //$NON-NLS-1$ //$NON-NLS-2$
		p.setProperty("start text", start); //$NON-NLS-1$
	}
	
	int getPos(Properties p) {
		int c = -1;
		if(p == null) return -1;
		String s = p.getProperty("pos"); //$NON-NLS-1$
		if(s == null || s.trim().length() == 0) return c;
		try {
			c = Integer.parseInt(s.trim());
		} catch (NumberFormatException e) {
			JSFModelPlugin.getPluginLog().logError(e);
		}
		return c;
	}
	
	public boolean isAdoptableMapEntry(XModelObject object) {
		return "JSFMapEntry".equals(object.getModelEntity().getName()); //$NON-NLS-1$
	}

	public void adoptMapEntry(XModelObject target, XModelObject object, Properties p) {
		String key = object.getAttributeValue("key"); //$NON-NLS-1$
		XModelObject g = object.getParent().getParent();
		String entity = g.getModelEntity().getName();
		String start = null;
		if("JSFManagedBean".equals(entity)) { //$NON-NLS-1$
			String bean = g.getAttributeValue("managed-bean-name"); //$NON-NLS-1$
			start = "#{" + bean + "." + key + "}"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		} else if("JSFManagedProperty".equals(entity)) { //$NON-NLS-1$
			XModelObject h = g.getParent();
			String bean = h.getAttributeValue("managed-bean-name"); //$NON-NLS-1$
			String property = g.getAttributeValue("property-name"); //$NON-NLS-1$
			start = "#{" + bean + "." + property + "." + key + "}"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}
		if(start != null) {
			p.setProperty("start text", start); //$NON-NLS-1$
		}
	}

	static boolean isInsideResponseRedirect(String text, int off) {
		if(off < 0) return false;
		String START = "response.sendRedirect(\""; //$NON-NLS-1$
		String END = "\")"; //$NON-NLS-1$
		int i = 0;
		while(i < text.length() && i < off) {
			int i1 = text.indexOf(START, i);
			if(i1 < 0 || i1 + START.length() > off) return false;
			int i2 = text.indexOf(END, i1 + START.length());
			if(i2 < 0 || i2 >= off) return true;
			i = i2 + END.length();
		}
		return false;
	}
}
