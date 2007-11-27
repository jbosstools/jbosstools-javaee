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

public class PrefixUrlPattern implements UrlPattern {
	protected String prefix = "/do/";
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}	

	public boolean isActionUrl(String path) {
		return (path.startsWith(prefix) || path.indexOf(".") < 0);
	}
	
	public String getActionPath(String url) {
		if(url == null || url.length() == 0) return url;
		return (url.startsWith(prefix)) ? url.substring(prefix.length() - 1) : url;
	}
	
	public String getActionUrl(String path) {
		return (path == null || path.length() == 0 || path.startsWith(prefix)) 
		       ? path : (!path.startsWith("/")) ? prefix + path : prefix + path.substring(1);
	}
	
	public String getModule(String path, Set modules, String thisModule) {
		if(path == null || path.length() < 2 || path.startsWith("//")) return "";
		int i0 = path.startsWith(prefix) ? prefix.length() : 1;
		int i = path.indexOf('/', i0);
		if(i < 0) return thisModule;
		String m = path.substring(i0 - 1, i);
		return (modules.contains(m)) ? m : thisModule; 
	}
	
	public String getContextRelativePath(String path, String module) {
		if(path == null || module.length() == 0) return path;
		if(path.startsWith(prefix)) {
			String p = prefix + module.substring(1) + "/";
			return (path.startsWith(p)) ? path : p + path.substring(prefix.length());
		}
		return (path.startsWith(module + "/")) ? path : module + path; 
	}
	
	public String getModuleRelativePath(String path, String module) {
		if(path == null || module.length() == 0) return path;
		if(path.startsWith(prefix)) {
			String p = prefix + module.substring(1) + "/";
			return (!path.startsWith(p)) ? path : prefix + path.substring(p.length());
		}
		return (!path.startsWith(module+ "/")) ? path : path.substring(module.length());		
	}
	
	public String toString() {
		return "PrefixUrlPattern:" + prefix;
	}
		
}
