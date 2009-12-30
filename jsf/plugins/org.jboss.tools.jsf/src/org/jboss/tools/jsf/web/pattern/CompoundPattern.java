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

public class CompoundPattern implements JSFUrlPattern {
	JSFUrlPattern[] patterns = PatternLoader.DEFAULT_PATTERNS;
	
	public void setPatterns(JSFUrlPattern[] patterns) {
		this.patterns = patterns;
	}

	public boolean matches(String path) {
		if(path == null || path.length() == 0) return false;
		for (int i = 0; i < patterns.length; i++) {
			if(patterns[i].matches(path)) return true;
		}
		return false;
	}

	public boolean isJSFUrl(String path) {
		if(path == null || path.length() == 0) return false;
		for (int i = 0; i < patterns.length; i++) {
			if(patterns[i].isJSFUrl(path)) return true;
		}
		return false;
	}

	public String getJSFPath(String url) {
		if(url == null || url.length() == 0) return url;
		for (int i = 0; i < patterns.length; i++) {
			if(patterns[i].matches(url)) return patterns[i].getJSFPath(url);
		}
		return patterns[0].getJSFPath(url);
	}

	public String getJSFUrl(String path) {
		if(path == null || path.length() == 0) return path;
		for (int i = 0; i < patterns.length; i++) {
			if(patterns[i].matches(path)) return path;
		}
		return patterns[0].getJSFUrl(path);
	}
	
	/**
	 * Stub implementation
	 * @see org.jboss.tools.jsf.web.pattern.JSFUrlPattern#getJSFPaths(java.lang.String)
	 */
	public List<String> getJSFPaths(String url) {
		return new ArrayList<String>();
	}

}
