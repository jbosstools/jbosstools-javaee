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
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.filesystems.XFileObject;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.meta.*;
import org.jboss.tools.jsf.model.pv.JSFProjectBean;
import org.jboss.tools.jsf.project.JSFNature;
import org.jboss.tools.jsf.web.JSFWebProject;
import org.jboss.tools.jsf.web.pattern.JSFUrlPattern;
import org.jboss.tools.jst.web.project.WebProject;
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

    public void adopt(XModelObject target, XModelObject object, java.util.Properties p) {
        if(isAdoptableProperty(object)) adoptProperty(target, object, p);
		else if(isAdoptablePropertyReference(object)) adoptPropertyReference(target, object, p);
		else if(isAdoptablePage(object)) adoptPage(target, object, p);
        else if(isAdoptableFile(object)) adoptFile(target, object, p);
		else if(isAdoptableMapEntry(object)) adoptMapEntry(target, object, p);
    }

    static String PAGE_TARGET = ".FileJSP.FileHTML.FileXHTML.FacesConfig.";
    static String PAGE_ENTITY = ".FileJSP.FileXHTML.";

	private boolean isAcceptableTarget(XModelObject target) {
		String entity = "." + target.getModelEntity().getName() + ".";
		return PAGE_TARGET.indexOf(entity) >= 0;
	}

    protected boolean isAdoptableProperty(XModelObject object) {
        return object.getModelEntity().getName().startsWith("JSFManagedProperty");
    }

    public void adoptProperty(XModelObject target, XModelObject object, Properties p) {
        if(p == null) return;
        int c = -1;
        try {
        	c = Integer.parseInt(p.getProperty("pos"));
        } catch (Exception e) {
        	//ignore
        }
        if(c < 0) return;
		String bean = object.getParent().getAttributeValue("managed-bean-name");
        String name = object.getAttributeValue("property-name");
        String start = "#{" + bean + "." + name + "}";
        p.setProperty("start text", start);
    }

	protected boolean isAdoptablePage(XModelObject object) {
		String entity = "." + object.getModelEntity().getName() + ".";
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
        int pos = -1;
        try {
        	pos = Integer.parseInt(p.getProperty("pos"));
        } catch (Exception e) {
        	//ignore
        }
		if(res.startsWith("/") && pos >= 0 && isInsideResponseRedirect(p.getProperty("text"), pos)) {
			res = res.substring(1);
		}
		p.setProperty("start text", res);
		p.setProperty("end text", "");
	}
	
	static String NO_JSF_URL = 
		"+include+jsp:include+jsp:directive.include+ui:include+ui:composition+ui:decorate+";
	
	boolean applyPattern(Properties p) {
		if(p == null) return true;
		String tag = p.getProperty("context:tagName");
		if(NO_JSF_URL.indexOf("+" + tag + "+") >= 0) return false;		
		return true;
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
        p.setProperty("start text", res);
        p.setProperty("end text", "");
    }

	protected boolean isAdoptablePropertyReference(XModelObject object) {
		String entity = "." + object.getModelEntity().getName() + ".";
		return ".JSFProjectBeanProperty.JSFProjectBeanMethod.".indexOf(entity) >= 0;
	}

	public void adoptPropertyReference(XModelObject target, XModelObject object, Properties p) {
		if(p == null) return;
		int c = -1;
		try {
			c = Integer.parseInt(p.getProperty("pos"));
		} catch (Exception e) {
			//ignore
		}
		if(c < 0) return;
		String s = object.getAttributeValue("name");
		XModelObject o = object;
		while(o != null && isAdoptablePropertyReference(o)) {
			o = o.getParent();
			if(o != null) {
				String part = o.getAttributeValue("name");
				if(o instanceof JSFProjectBean) {
					XModelObject[] list = ((JSFProjectBean)o).getBeanList();
					if(list.length > 1) {
						part = SelectBeanSupport.run((JSFProjectBean)o);
						if(part == null) return;
					}
				}
				s = part + "." + s;
			}
		}
		String start = "#{" + s + "}";
		p.setProperty("start text", start);
	}
	
	public boolean isAdoptableMapEntry(XModelObject object) {
		return "JSFMapEntry".equals(object.getModelEntity().getName());
	}

	public void adoptMapEntry(XModelObject target, XModelObject object, Properties p) {
		String key = object.getAttributeValue("key");
		XModelObject g = object.getParent().getParent();
		String entity = g.getModelEntity().getName();
		String start = null;
		if("JSFManagedBean".equals(entity)) {
			String bean = g.getAttributeValue("managed-bean-name");
			start = "#{" + bean + "." + key + "}";
		} else if("JSFManagedProperty".equals(entity)) {
			XModelObject h = g.getParent();
			String bean = h.getAttributeValue("managed-bean-name");
			String property = g.getAttributeValue("property-name");
			start = "#{" + bean + "." + property + "." + key + "}";
		}
		if(start != null) {
			p.setProperty("start text", start);
		}
	}

	static boolean isInsideResponseRedirect(String text, int off) {
		if(off < 0) return false;
		String START = "response.sendRedirect(\"";
		String END = "\")";
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
