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
package org.jboss.tools.struts.model.helpers.page;

import java.util.*;
import org.jboss.tools.struts.model.helpers.page.link.*;
import org.jboss.tools.struts.webprj.pattern.UrlPattern;

public class JSPLinksParser {
    private Set<String> toForwards = new HashSet<String>();
    private Set<String> toActions = new HashSet<String>();
    private Set<String> toPages = new HashSet<String>();
    private String source;
    private UrlPattern pattern;
    private Links links;

    public JSPLinksParser(Links links) {
    	this.links = links;
    }
    
    public void setUrlPattern(UrlPattern pattern) {
    	this.pattern = pattern; 
    }

    public void setSource(String source) {
        source = removeToken(source, "<!--", "-->");
        source = removeToken(source, "<%", "%>");
        this.source = source;
    }

    private static String removeToken(String body, String start, String end) {
        int ia = 0, ib = (body == null) ? -1 : body.indexOf(start);
        if(ib < 0) return body;
        int l = body.length();
        StringBuffer sb = new StringBuffer();
        while(true) {
            sb.append(body.substring(ia, ib));
            ia = body.indexOf(end, ib + start.length());
            if(ia < 0) break;
            ia += end.length();
            ib = body.indexOf(start, ia);
            if(ib < 0) ib = l;
        }
        return sb.toString();
    }

	//use in future
    private Set<String> getForwards() {
        return toForwards;
    }
    
    private Set<String> getActions() {
        return toActions;
    }

	private Set<String> getPages() {
        return toPages;
    }

    public Set<String> getAllLinks() {
        Set<String> ls = new HashSet<String>();
        Iterator<String> it = getActions().iterator();
        while(it.hasNext()) ls.add(it.next());
        it = getForwards().iterator();
        while(it.hasNext()) ls.add(it.next());
        it = getPages().iterator();
        while(it.hasNext()) ls.add(it.next());
        return ls;
    }

    public boolean isForward(String s) {
        return toForwards.contains(s); 
    }

    public void parse() {
        if (source == null) return;
///        parseHTMLTags(0, source.length());
		parseAdvanced(0, source.length());
        //what else?
    }

    private void parseAdvanced(int b, int e) {
    	if(links == null) return;
		Set tags = links.getTags();
		while(b < e) {
			int i = source.indexOf("<", b);
			if(i < 0) return;
			String n = ReplaceConfirmedLinkHelper.readTagName(source, i);
			int j = source.indexOf('>', i);
			if (j < 0) j = e;
			if(n != null) {
				b = i + 1 + n.length();
				if(tags.contains(n)) {
					parseTagAdvanced(n, b, j);
				}
			}
			b = j;
		}
    }
    
    private void parseTagAdvanced(String n, int b, int e) {
		Link[] ls = links.getLinks(n);
		for (int k = 0; k < ls.length; k++) {
			String attr = ls[k].getAttribute();
			String referTo = ls[k].getReferTo();
			if("action".equals(referTo)) {
				if(processAttrAction(attr, b, e) != null) return;
			} else if("forward".equals(referTo)) {
				if(processAttrForward(attr, b, e) != null) return;
			} else if("page".equals(referTo)) {
				if(processAttrPage(attr, b, e) != null) return;							 
			} else {
				if(processAttrAction(attr, b, e) != null) return;
			}
		}
    }

/*
    private void parseHTMLTags(int b, int e) {
        while(b < e) {
            int i = source.indexOf("<", b);
            if(i < 0) return;
            String n = ReplaceConfirmedLinkHelper.readTagName(source, i);
            int j = source.indexOf('>', i);
            if (j < 0) j = e;
            if(n != null) {
                b = i + 1 + n.length();
                if(isHTMLTag(n)) parseHTMLTag(n, b, j);
                else if(isLogicTag(n)) parseLogicTag(n, b, j);
                else if("logic:redirect".equals(n)) processAttrForward("forward", b, j);
            }
            b = j;
        }
    }

    private boolean isHTMLTag(String n) {
        return (n.equals("html:form") || n.equals("html:link") || n.equals("html:frame"));
    }

    private boolean isLogicTag(String n) {
        return (n.equals("logic:forward"));
    }

    private void parseHTMLTag(String n, int b, int e) {
        if(processAttrAction(b, e) != null) return;
        if(n.equals("html:form")) return;
        if(processAttrForward("forward", b, e) != null) return;
        if(processAttrPage(b, e) != null) return;
    }

    private void parseLogicTag(String n, int b, int e) {
        processAttrForward("name", b, e);
    }
*/

	private String processAttrAction(String name, int b, int e) {
		String s = readAttributeR(name, b, e); //true
		if(s == null || s.length() == 0) return s;
		if(pattern.isActionUrl(s)) {
			s = pattern.getActionUrl(s);
			toActions.add(s);
		} else {
			if(!s.startsWith("/")) s = "/" + s; 
			toPages.add(s);
		}				  
		return s;
	}

    private String processAttrForward(String attr, int b, int e) {
        String s = readAttributeR(attr, b, e);
        if(s != null) toForwards.add(s);
        return s;
    }

    private String processAttrPage(String attr, int b, int e) {
        String s = readAttributeR(attr, b, e); //true
        if(s == null || s.length() == 0) return s;
        if(s.startsWith("http:")) {
        	toPages.add(s);
        } else {
			if(!s.startsWith("/") && s.length () > 0) s = "/" + s;
			if(pattern.isActionUrl(s)/*s.endsWith(".do")*/) toActions.add(s); else toPages.add(s);
        }
        return s;
    }

    private String readAttributeR(String attr, int b, int e) {
        String s = readAttribute(attr, b, e);
        if(s == null || s.length() == 0) return null;
        int i = s.indexOf('?');
        if(i > 0) s = s.substring(0, i);
        return s;
    }

    private String readAttribute(String attr, int b, int e) {
        int i = indexOfAttribute(attr, b, e);
        if(i < 0) return null;
        return readAttrValue(i + attr.length(), e);
    }

    private boolean startsWith(String w, int b, int e) {
        int l = w.length();
        if(e - b < l) return false;
        for (int i = 0; i < l; i++)
          if(source.charAt(b + i) != w.charAt(i)) return false;
        return true;
    }

    private int indexOfAttribute(String w, int b, int e) {
        int l = e - w.length();
        char q = '\0';
        for (int i = b; i < l; i++) {
            char c = source.charAt(i);
            if(q != '\0') {
                if(c == q) q = '\0';
            } else if(c == '"' || c == '\'') {
                q = c;
            } else if(startsWith(w, i, e)) return i;
        }
        return -1;
    }

    private String readAttrValue(int b, int e) {
        char q = '\0';
        int ab = b, ae = e;
        for (int i = b; i < e; i++) {
            char c = source.charAt(i);
            if(q == '\0') {
                if(c == '"' || c == '\'') q = c;
                ab = i + 1;
            } else if(c == q) {
                ae = i;
                return source.substring(ab, ae);
            }
        }
        return "";
    }

    public boolean areLinksModified(JSPLinksParser p) {
        if(!areSetsEqual(toActions, p.getActions())) return true;
        if(!areSetsEqual(toForwards, p.getForwards())) return true;
        if(!areSetsEqual(toPages, p.getPages())) return true;
        return false;
    }

    private boolean areSetsEqual(Set s1, Set s2) {
        if(s1.size() != s2.size()) return false;
        Iterator it = s1.iterator();
        while(it.hasNext()) if(!s2.contains(it.next())) return false;
        it = s2.iterator();
        while(it.hasNext()) if(!s1.contains(it.next())) return false;
        return true;
    }

}
