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
package org.jboss.tools.struts.webprj.pattern;

import java.util.*;

public class PostfixUrlPattern implements UrlPattern {
	protected String postfix = ".do";
	
	public void setPostfix(String postfix) {
		this.postfix = postfix;
	}	

	public boolean isActionUrl(String path) {
		return (path.endsWith(postfix) || path.indexOf(".") < 0);
	}
	
	public String getActionPath(String url) {
		if(url == null || url.length() == 0) return url;
		return (url.endsWith(postfix)) ? url.substring(0, url.length() - postfix.length()) : url;
	}
	
	public String getActionUrl(String path) {
		if(path == null || path.length() == 0) return path;
		if(!path.endsWith(postfix)) path += postfix;
		if(!path.startsWith("/")) path = "/" + path;
		return path;
	}
	
	public String getModule(String path, Set modules, String thisModule) {
		if(path == null || path.length() < 2 || path.startsWith("//")) return "";
		int i = path.indexOf("/", 1);
		if(i < 0) return thisModule;
		String m = path.substring(0, i);
		return (modules.contains(m)) ? m : thisModule; 
	}
	
	public String getContextRelativePath(String path, String module) {
		return (path == null || module.length() == 0 || path.startsWith(module))
		       ? path : module + path; 
	}
	
	public String getModuleRelativePath(String path, String module) {
		return (path == null || module.length() == 0 || !path.startsWith(module + "/")) 
		       ? path : path.substring(module.length());		
	}
	
	public String toString() {
		return "PostfixUrlPattern:" + postfix;
	}
		
}
