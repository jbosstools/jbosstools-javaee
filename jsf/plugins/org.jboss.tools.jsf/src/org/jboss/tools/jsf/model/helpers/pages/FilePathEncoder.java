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
package org.jboss.tools.jsf.model.helpers.pages;

import java.util.Properties;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.jsf.web.JSFWebProject;
import org.jboss.tools.jst.web.tld.IFilePathEncoder;
import org.jboss.tools.jst.web.tld.TaglibData;
import org.jboss.tools.jst.web.tld.VpeTaglibManager;

public class FilePathEncoder implements IFilePathEncoder {

	public String encode(String path, XModelObject f, String tag,
			VpeTaglibManager taglibs, Properties context) {
		if(tag != null && tag.startsWith("/")) {
			tag = tag.substring(1);
			if(tag.indexOf('@') >= 0) tag = tag.substring(0, tag.indexOf('@'));
		}		
		String pathType = context.getProperty(PATH_TYPE);
		String pathAddition = context.getProperty(PATH_ADDITION);
		if(!isPath(path) && (pathAddition == null || !path.startsWith(pathAddition))) return path;
		int d = tag.indexOf(":");
		String prefix = (d < 0) ? "" : tag.substring(0, d);
		if(d > 0) tag = tag.substring(d + 1);
		if(prefix.equals("jsp")) return path;
		String uri = getUri(prefix, taglibs);
		if(!RELATIVE_PATH.equals(pathType)) {
			JSFWebProject wp = JSFWebProject.getInstance(f.getModel());
			if(uri != null && wp.getUrlPattern().isJSFUrl(path) 
				&& path.indexOf('/') >= 0 && !doNotEncodeToJSFURL(uri, tag)) {
				path = wp.getUrlPattern().getJSFUrl(path);
			}
			if(pathAddition != null) path = pathAddition + path;

		} else {
			String current = XModelObjectLoaderUtil.getResourcePath(f.getParent());
			if(current == null || current.length() == 0) current = "";
			String fake = "FAKE:/" + path;
			String root = "FAKE:/" + current;
			String p = FileUtil.getRelativePath(root, fake);
			if(p != null) {
				path = p;
				if(path.startsWith("/")) path = path.substring(1);
			}
		}
		return path;
	}

	public String decode(String path, XModelObject f, String tag,
			VpeTaglibManager taglibs, Properties context) {
		if(tag != null && tag.startsWith("/")) {
			tag = tag.substring(1);
			if(tag.indexOf('@') >= 0) tag = tag.substring(0, tag.indexOf('@'));
		}		
		String pathType = context.getProperty(PATH_TYPE);
		String pathAddition = context.getProperty(PATH_ADDITION);
		if(!isPath(path) && (pathAddition == null || !path.startsWith(pathAddition))) return path;
		int d = tag.indexOf(":");
		String prefix = (d < 0) ? "" : tag.substring(0, d);
		if(d > 0) tag = tag.substring(d);
		if(prefix.equals("jsp")) return path;
		String uri = getUri(prefix, taglibs);
		if(!RELATIVE_PATH.equals(pathType)) {
			if(pathAddition != null && path.startsWith(pathAddition)) {
				path = path.substring(pathAddition.length());
			}
			JSFWebProject wp = JSFWebProject.getInstance(f.getModel());
			if(uri != null && wp.getUrlPattern().isJSFUrl("/" + path) && path.indexOf('.') >= 0) {
				if(!path.startsWith("/")) path = "/" + path;
				path = wp.getUrlPattern().getJSFPath(path);
			}
		} else {
			String current = XModelObjectLoaderUtil.getResourcePath(f.getParent());
			if(current == null || current.length() == 0) current = "";
			if(!path.startsWith("/")) path = "/" + path;
			while(path.startsWith("/..")) {
				path = path.substring(3);
				d = current.lastIndexOf('/');
				if(d >= 0) current = current.substring(0, d);
			}
			path = current + path;
		}
		return path;
	}
	
	String getUri(String prefix, VpeTaglibManager taglibs) {
		if(taglibs == null) return null;
		TaglibData[] data = taglibs.getTagLibs().toArray(new TaglibData[0]);
		for (int i = 0; i < data.length; i++) {
			if(prefix.equals(data[i].getPrefix())) return data[i].getUri();
		}
		return null;
	}
	
	boolean isPath(String path) {
		if(path == null || path.length() == 0) return false;
		if(path.indexOf('{') > 0) return false;
		if(path.indexOf('}') > 0) return false;
		if(path.indexOf('#') > 0) return false;
		return true;
	}
	
	static String NO_JSF_URL = ".composition.decorate.include.";
	
	boolean doNotEncodeToJSFURL(String uri, String tag) {
		if(NO_JSF_URL.indexOf("." + tag + ".") >= 0 
				&& "http://java.sun.com/jsf/facelets".equals(uri)) {
			return true;
		}
		return false;		
	}

}
