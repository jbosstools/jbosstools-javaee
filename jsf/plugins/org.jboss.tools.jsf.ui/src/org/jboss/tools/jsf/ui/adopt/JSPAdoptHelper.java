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
package org.jboss.tools.jsf.ui.adopt;

import java.util.*;

public class JSPAdoptHelper {

	public static String getPrefixForURI(Token root, String uri, String defaultValue) {
		String p = getPrefixForURI(root, uri);
		if (p == null || p.length() == 0) {
			p = getPrefixForURIFromXMLNS(root, uri);
		}
		return (p == null || p.length() == 0) ? defaultValue : p;
	}

    public static String getPrefixForURI(Token root, String uri) {
    	Token t = root.firstChild;
    	for (; t != null; t = t.nextSibling) {
    		if(t.kind != JSPTokenizer.DIRECTIVE) continue;
    		if(!"taglib".equals(t.name) || t.attributes == null) continue;
    		if(!uri.equalsIgnoreCase(t.attributes.getProperty("uri"))) continue;
    		String prefix = t.attributes.getProperty("prefix");
    		return prefix == null ? "?" : prefix;    		
    	}
        return null;
    }
    
    public static String getPrefixForURIFromXMLNS(Token root, String uri) {
    	Token t = root.firstChild;
    	for (; t != null; t = t.nextSibling) {
    		if(t.kind == JSPTokenizer.TAG && "jsp:root".equals(t.name) && t.attributes != null) {
	    		Enumeration names = t.attributes.keys();
	    		while (names != null && names.hasMoreElements()) {
	    			String name = (String)names.nextElement();
	    			String value = (String)t.attributes.get(name);
	    			if (value == null) continue;
	    			if (!value.equals(uri)) continue;
	    			if (!name.startsWith("xmlns:")) continue;
	    			String prefix = name.substring("xmlns:".length());
	        		return prefix == null ? "?" : prefix;    		
	    		}
    		} else {
    			String prefix = getPrefixForURIFromXMLNS(t, uri);
    			if (prefix != null && prefix.length() > 0) return prefix;
    		}
    	}
        return null;
    }

    
    public static String getLoadedBundleVar(Token root, String jsfCorePrefix, String baseName) {
        if (root == null || 
            jsfCorePrefix == null || jsfCorePrefix.length() == 0 || 
            baseName == null || baseName.length() == 0) return null;
        String tagName = jsfCorePrefix + ":loadBundle";        
        for (Token t = root.firstChild; t != null; t = t.nextSibling) {
            if (t.kind == JSPTokenizer.TAG && tagName.equals(t.name) && t.attributes != null) { 
	            if (!baseName.equals(t.attributes.getProperty("basename"))) continue;
	    		String v = t.attributes.getProperty("var");
	            return v == null ? "?" : v;
            } else {
            	String v = getLoadedBundleVar(t, jsfCorePrefix, baseName);
            	if (v != null && v.length() > 0) return v;
            }
        }
        return null;
    }

    public static int getPositionForBundle(Token root, String jsfCorePrefix) {
    	if(isXHTML(root)) {
    		
    	}
    	return getPositionForBundle(root, jsfCorePrefix, 0);
    }
    
    public static boolean isXHTML(Token root) {
    	return root.firstChild != null && root.firstChild.kind == JSPTokenizer.DOCTYPE 
 	   && root.firstChild.attributes.getProperty("public") != null
	   && root.firstChild.attributes.getProperty("public").startsWith("-//W3C//DTD XHTML");
    }
    
    private static int getPositionForBundle(Token root, String jsfCorePrefix, int off) {
		int q = off;
        for (Token t = root.firstChild; t != null; t = t.nextSibling) {
			if (t.kind == JSPTokenizer.TAG) {
				if(t.name.equals("jsp:root")) {
					q = t.off + t.length;
					return getPositionForBundle(t, jsfCorePrefix, q);
				}
				if(t.name.equalsIgnoreCase("html")) {
					if(isXHTML(root)) return t.off + t.length;
					return q;
				} else if(t.name.equals(jsfCorePrefix + ":loadBundle")) {
					q = t.off + t.length;
				}
			} else if(t.kind == JSPTokenizer.DIRECTIVE) {
				if(t.name.equals("include")) {
					return q;
				} else {
					q = t.off + t.length;
				}
			}
		}    	
    	return q;
    }
    
    public static String getNameForNewBundle(Token root, String jsfCorePrefix) {
		String n = jsfCorePrefix + ":loadBundle";
		Set<String> set = new HashSet<String>();
        for (Token t = root.firstChild; t != null; t = t.nextSibling) {
            if (t.kind != JSPTokenizer.TAG) continue;
			if(!t.name.equals(n) || t.attributes == null) continue;
			String v = t.attributes.getProperty("var");
			if(v != null) set.add(v);
		}
		String v = "msg";
		if(set.contains(v)) {
			int i = 1;
			while(set.contains(v + i)) ++i;
			v += i;
		}
		return v;
    }
    
    public final static String JSF_CORE_TAGLIB_URI = "http://java.sun.com/jsf/core";
    public final static String JSF_CORE_TAGLIB_PREFIX_DEFAULT = "f";
    public final static String JSF_HTML_TAGLIB_URI = "http://java.sun.com/jsf/html";
    public final static String JSF_HTML_TAGLIB_PREFIX_DEFAULT = "h";
    
    public static String cutOffQuotes(String text) {
        if (text == null) return null;
        StringBuffer buffer = new StringBuffer(text);
        boolean doCutOff = false;
        if (buffer.charAt(0) == '"' && buffer.charAt(buffer.length() - 1) == '"') doCutOff = true;
        if (buffer.charAt(0) == '\'' && buffer.charAt(buffer.length() - 1) == '\'') doCutOff = true;
        return (doCutOff ? buffer.substring(1, buffer.length() - 1).toString() : text);
    }
    
}
