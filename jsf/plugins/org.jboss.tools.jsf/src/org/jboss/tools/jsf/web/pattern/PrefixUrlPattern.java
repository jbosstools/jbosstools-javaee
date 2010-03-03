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
package org.jboss.tools.jsf.web.pattern;

import java.util.ArrayList;
import java.util.List;

public class PrefixUrlPattern implements JSFUrlPattern {
	protected String prefix = "/faces/";
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}	

	public boolean matches(String path) {
		return path.startsWith(prefix);
	}
		
	public boolean isJSFUrl(String path) {
		return (path.startsWith(prefix) || path.indexOf(".") < 0);
	}
	
	public String getJSFPath(String url) {
		if(url == null || url.length() == 0) return url;
		return (url.startsWith(prefix)) ? url.substring(prefix.length() - 1) : url;
	}
	
	public String getJSFUrl(String path) {
		if(path == null || path.length() == 0 || path.startsWith(prefix)) return path;
		if(path.startsWith("/")) path = path.substring(1);
		path = prefix + path;
		return path;
	}
	
	/**
	 * Stub implementation
	 * @see org.jboss.tools.jsf.web.pattern.JSFUrlPattern#getJSFPaths(java.lang.String)
	 */
	public List<String> getJSFPaths(String url) {
		List<String> result = new ArrayList<String>();
		String path = getJSFPath(url);
		if(path != null && path.length() > 0) result.add(path);
		return result;
	}

	public String toString() {
		return "PrefixUrlPattern:" + prefix;
	}
		
}
