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

import org.jboss.tools.common.model.*;
import org.jboss.tools.jsf.model.JSFConstants;
import org.jboss.tools.jst.web.browser.wtp.RunOnServerContext;
import org.jboss.tools.jst.web.model.helpers.WebAppHelper;

public class PatternLoader {
	static String DEFAULT_FILE_SUFFIX = ".jsp";
	public static JSFUrlPattern DEFAULT_PATTERN = new PostfixUrlPattern();
	static JSFUrlPattern[] DEFAULT_PATTERNS = new JSFUrlPattern[]{DEFAULT_PATTERN};
	CompoundPattern pattern = new CompoundPattern();
	private long timeStamp = -1;
	
	public JSFUrlPattern getUrlPattern() {
		return pattern.patterns.length == 1 ? pattern.patterns[0] : pattern;
	}

	public void revalidate(XModelObject webxml) {
		if(webxml == null || webxml.getChildren().length == 0) return;
    	if(timeStamp == webxml.getTimeStamp()) return;
		timeStamp = webxml.getTimeStamp();
		XModelObject[] mappings = WebAppHelper.getServletMappings(webxml);
		if(mappings.length == 0) {
			pattern.setPatterns(DEFAULT_PATTERNS);
			return;
		}
		String fileSuffix = getFileSuffix(webxml);
		ArrayList<JSFUrlPattern> list = new ArrayList<JSFUrlPattern>();
		String servletName = getFacesServletName(webxml);
		for (int i = 0; i < mappings.length; i++) {
			String servlet = mappings[i].getAttributeValue("servlet-name");
			if(!servletName.equals(servlet)) continue;
			String pattern = mappings[i].getAttributeValue("url-pattern");
			JSFUrlPattern up = load(pattern);
			if(up instanceof PostfixUrlPattern) {
				((PostfixUrlPattern)up).setFileSuffix(fileSuffix);
			}
			if(up != null) {
				list.add(up);
			}
		}
		if(list.size() == 0 && fileSuffix.equals(DEFAULT_FILE_SUFFIX)) {
			pattern.setPatterns(DEFAULT_PATTERNS);
		} else if(list.size() == 0) {
			JSFUrlPattern[] ps = new JSFUrlPattern[1];
			ps[0] = new PostfixUrlPattern();
			((PostfixUrlPattern)ps[0]).setFileSuffix(fileSuffix);
			pattern.setPatterns(ps);
		} else {
			pattern.setPatterns((JSFUrlPattern[])list.toArray(new JSFUrlPattern[0])); 
		}
		RunOnServerContext.getInstance().revalidate();
    }
    
    String getFacesServletName(XModelObject webxml) {
    	XModelObject s = WebAppHelper.findServlet(webxml, JSFConstants.FACES_SERVLET_CLASS, null);
    	return (s != null) ? s.getAttributeValue("servlet-name") : "FacesServlet";
    }
    
    String getFileSuffix(XModelObject webxml) {
    	String[] list = WebAppHelper.getWebAppContextParamValueList(webxml, "javax.faces.DEFAULT_SUFFIX");
    	return list == null || list.length == 0 || !list[0].startsWith(".") ? DEFAULT_FILE_SUFFIX : list[0];
    }
    
    JSFUrlPattern load(String p) {
    	if(p == null || p.length() == 0) return null;
    	int s = p.indexOf(',');
    	if(s > 0) p = p.substring(0, s);
    	if(p.startsWith("*.")) {
			String n = p.substring(2);
			for (int i = 0; i < n.length(); i++)
			  if(!Character.isJavaIdentifierPart(n.charAt(i))) return null;
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
