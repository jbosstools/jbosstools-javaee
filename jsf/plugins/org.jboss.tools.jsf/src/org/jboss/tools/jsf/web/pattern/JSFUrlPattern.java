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

import java.util.List;

public interface JSFUrlPattern {
	boolean matches(String path);
	boolean isJSFUrl(String path);
	String getJSFPath(String url);
	String getJSFUrl(String path);
	/**
	 * Fixes https://jira.jboss.org/jira/browse/JBIDE-5577
	 * <p>
	 * Searches files that could have been mapped to jsf servlet. 
	 * 
	 * @param url the jsf mapping path
	 * @return the list of possible jsf files
	 */
	List<String> getJSFPaths(String url);
}
