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
import org.jboss.tools.common.model.*; 
import org.jboss.tools.jst.web.model.helpers.WebAppHelper;
import org.jboss.tools.struts.webprj.model.helpers.sync.StrutsWebHelper;

public class PatternLoader {
	public static UrlPattern DEFAULT_PATTERN = new PostfixUrlPattern();
	private Map<String,UrlPattern> servletPatterns = new HashMap<String,UrlPattern>();
	private Map<String,UrlPattern> configPatterns = new HashMap<String,UrlPattern>();
	private long timeStamp = -1;
	
	public UrlPattern getUrlPatternForModule(String module) {
		if(module == null) return DEFAULT_PATTERN;
		UrlPattern p = (UrlPattern)configPatterns.get(module);
		return (p != null) ? p : DEFAULT_PATTERN;
	}

    public void revalidate(XModelObject webxml) {
    	if(webxml == null || webxml.getChildren().length == 0) return;
    	if(timeStamp == webxml.getTimeStamp()) return;
		timeStamp = webxml.getTimeStamp();
		XModelObject[] mappings = WebAppHelper.getServletMappings(webxml);
		servletPatterns.clear();
		configPatterns.clear();
		if(mappings.length == 0) return;
		for (int i = 0; i < mappings.length; i++) {
			String servlet = mappings[i].getAttributeValue("servlet-name");
			String pattern = mappings[i].getAttributeValue("url-pattern");
			UrlPattern up = load(pattern);
			if(up != null) servletPatterns.put(servlet, up);
		}
		if(servletPatterns.size() == 0) return;
		XModelObject servlet = WebAppHelper.findServlet(webxml, StrutsWebHelper.ACTION_SERVLET, "action");
		String servletName = servlet.getAttributeValue("servlet-name");
		if(servletPatterns.containsKey(servletName)) {
			UrlPattern up = (UrlPattern)servletPatterns.get(servletName);
			XModelObject[] init = servlet.getChildren("WebAppInitParam");
			for (int j = 0; j < init.length; j++) {
				String module = init[j].getAttributeValue("param-name");
				if(!module.startsWith("config")) continue;
				module = module.substring(6);
				configPatterns.put(module, up);
			}
		}
    }
    
    UrlPattern load(String p) {
    	if(p == null || p.length() == 0) return null;
    	if(p.startsWith("*.")) {
			String n = p.substring(2);
			for (int i = 0; i < n.length(); i++)
			  if(!Character.isJavaIdentifierPart(n.charAt(i))) return null;
			if(n.equals("do")) return null; //default pattern
			PostfixUrlPattern up = new PostfixUrlPattern();
			up.setPostfix(p.substring(1));
			return up;
    	} else if(p.endsWith("/*") && p.startsWith("/") && p.length() > 2) {
    		String n = p.substring(1, p.length() - 2);
    		for (int i = 0; i < n.length(); i++)
    		  if(!Character.isJavaIdentifierPart(n.charAt(i))) return null;
    		PrefixUrlPattern up = new PrefixUrlPattern();
    		up.setPrefix(p.substring(0, p.length() - 1));
    		return up;
    	}    	
    	return null;
    }
    
}
