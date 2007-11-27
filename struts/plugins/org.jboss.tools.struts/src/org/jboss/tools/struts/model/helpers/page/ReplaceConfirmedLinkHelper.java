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

import org.eclipse.osgi.util.NLS;

import org.jboss.tools.common.model.*;
import org.jboss.tools.struts.*;
import org.jboss.tools.struts.messages.StrutsUIMessages;
import org.jboss.tools.struts.model.helpers.*;
import org.jboss.tools.struts.webprj.pattern.UrlPattern;

public class ReplaceConfirmedLinkHelper implements StrutsConstants {

    public ReplaceConfirmedLinkHelper() {}

    public boolean replace(XModelObject link, String newpath, String attr) {
    	UrlPattern up = StrutsProcessStructureHelper.instance.getUrlPattern(link);
        String jsppath = link.getParent().getAttributeValue(ATT_PATH);
        XModelObject jsp = StrutsProcessStructureHelper.instance.findReferencedJSPInCurrentModule(link.getParent());
        if(jsp == null) return false;
        String path = link.getAttributeValue(ATT_PATH);
        String body = jsp.getAttributeValue("body"); //$NON-NLS-1$
        if(!containsPath(body, path, up)) return false;
        body = replace(body, path, newpath, attr, up);
        ServiceDialog d = link.getModel().getService();
        if(containsPath(body, path, up)) {
            String operation = (newpath.length() == 0) ? "remove" : "change"; //$NON-NLS-1$ //$NON-NLS-2$
            String msg = NLS.bind(StrutsUIMessages.LINK_IS_CONFIRMED_1,new String[]{link.getPresentationString(),operation,jsppath,operation});
            d.showDialog(StrutsUIMessages.WARNING, msg, new String[]{StrutsUIMessages.OK}, null, ServiceDialog.WARNING);
        } else {
            while(!jsp.isObjectEditable()) {
                String msg = NLS.bind(StrutsUIMessages.LINK_IS_CONFIRMED_2, link.getPresentationString(),jsppath); //$NON-NLS-1$
                int i = d.showDialog(StrutsUIMessages.QUESTION, msg, new String[]{StrutsUIMessages.RETRY, StrutsUIMessages.CANCEL}, null, ServiceDialog.QUESTION);
                if(i != 0) return true;
            }
            link.getModel().editObjectAttribute(jsp, "body", body); //$NON-NLS-1$
        }
        return true;
    }

    private static boolean containsPath(String body, String path, UrlPattern up) {
        JSPLinksParser p = new JSPLinksParser(LinkRecognizer.getInstance().getLinks());
        p.setUrlPattern(up);
        p.setSource(body);
        p.parse();
        return p.getAllLinks().contains(path);
    }

    private static int[] getPathBounds(String body, String path, UrlPattern up) {
		path = getBestPath(body, path, up);
		if(path == null) return null;
        int b = body.indexOf("\"" + path); //$NON-NLS-1$
        if(b < 0) return null;
        int e = body.indexOf('"', b + 1);
        if(e < 0) return null;
        char c = body.charAt(b + path.length() + 1);
        if(c != '?' && c != '"') return null;
        return new int[]{b + 1, e};
    }

    private static String getBestPath(String body, String path, UrlPattern up) {
        String p = path;
        if(body.indexOf(p) >= 0) return p;
        String p1 = up.getActionPath(path); 
        if(!p1.equals(p)/*path.endsWith(".do")*/) {
            p = p1; //path.substring(0, path.length() - 3);
            if(body.indexOf(p) >= 0) return p;
            if(path.startsWith("/")) { //$NON-NLS-1$
                p = path.substring(1);
                if(body.indexOf(p) >= 0) return p;
                p = p1.substring(1); 
                if(body.indexOf(p) >= 0) return p;
            } else {
                p = "/" + path; //$NON-NLS-1$
                if(body.indexOf(p) >= 0) return p;
                p = "/" + p1; //$NON-NLS-1$
                if(body.indexOf(p) >= 0) return p;
            }
        } else {
            if(path.startsWith("/")) { //$NON-NLS-1$
                p = path.substring(1);
                if(body.indexOf(p) >= 0) return p;
            } else {
                p = "/" + path; //$NON-NLS-1$
                if(body.indexOf(p) >= 0) return p;
            }
        }
        return null;
    }

    private static String replace(String body, String path, String newpath, String attr, UrlPattern up) {
        int[][] comments = getComments(body);
        StringBuffer sb = new StringBuffer();
        int i = 0;
        int l = body.length();
        while(i < l) {
            int b = body.indexOf("<", i); //$NON-NLS-1$
            if(b < 0) break;
            String n = readTagName(body, b);
            if(n == null) {
                sb.append(body.substring(i, b + 1));
                i = b + 1;
                continue;
            }
            if(!n.equals("html:link") && //$NON-NLS-1$
               !n.equals("html:form") && //$NON-NLS-1$
               !n.equals("html:frame") && //$NON-NLS-1$
               !n.equals("logic:forward") && //$NON-NLS-1$
               !n.equals("logic:redirect")) { //$NON-NLS-1$
                sb.append(body.substring(i, b + n.length() + 1));
                i = b + n.length() + 1;
                continue;
            }
            if(isWithingComments(b, comments)) {
                b = getWrappingCommentsEnd(b, comments);
                sb.append(body.substring(i, b));
                i = b;
                continue;
            }
            int c = body.indexOf('>', b);
            if(c < 0) {
                sb.append(body.substring(i, b + n.length()));
                i = b + n.length();
                continue;
            }
            int e = -1;
            if(body.charAt(c - 1) == '/') {
                e = c + 1;
            } else {
                e = body.indexOf("</" + n + ">", c); //$NON-NLS-1$ //$NON-NLS-2$
                if(e > b) e += 3 + n.length();
            }
            if(e < 0) {
                sb.append(body.substring(i, c));
                i = c;
                continue;
            }
            String header = body.substring(b, c);
            int[] bs = getPathBounds(header, path, up);
            if(bs == null) {
                sb.append(body.substring(i, e));
                i = e;
                continue;
            }
            String beg = body.substring(i, b + bs[0]);
            if(attr != null) {
                if(beg.endsWith("=\"") && !beg.endsWith(attr + "=\"")) { //$NON-NLS-1$ //$NON-NLS-2$
                    int q = beg.lastIndexOf(' ');
                    if(q >= 0) beg = beg.substring(0, q + 1) + attr + "=\""; //$NON-NLS-1$
                }
            }
            sb.append(beg)
              .append(newpath)
              .append(body.substring(b + bs[1], e));
            i = e;
        }
        sb.append(body.substring(i));
        return sb.toString();
    }

    private static int[][] getComments(String body) {
        List<int[]> l = new ArrayList<int[]>();
        parseTokens(body, l, "<!--", "-->"); //$NON-NLS-1$ //$NON-NLS-2$
        parseTokens(body, l, "<%--", "--%>"); //$NON-NLS-1$ //$NON-NLS-2$
        return (int[][])l.toArray(new int[0][]);
    }

    private static void parseTokens(String body, List<int[]> l, String start, String end) {
        int ia = 0, ib = 0;
        while(ia < body.length()) {
            ia = body.indexOf(start, ib);
            if(ia < 0) break;
            ib = body.indexOf(end, ia);
            if(ib < 0) ib = body.length(); else ib += end.length();
            l.add(new int[]{ia, ib});
        }
    }

    private static boolean isWithingComments(int p, int[][] comments) {
        for (int i = 0; i < comments.length; i++)
          if(comments[i][0] <= p && comments[i][1] > p) return true;
        return false;
    }

    private static int getWrappingCommentsEnd(int p, int[][] comments) {
        for (int i = 0; i < comments.length; i++)
          if(comments[i][0] <= p && comments[i][1] > p) return comments[i][1];
        return p;
    }

    public static String readTagName(String body, int b) {
        int l = body.length();
        ++b;
        int e = b;
        while(e < l) {
            char ch = body.charAt(e);
            if(Character.isWhitespace(ch)) return body.substring(b, e);
            if(ch == '>' || ch == '<' || ch == '%' || ch == '?' || ch == '!') return null;
            ++e;
        }
        return null;
    }

}
