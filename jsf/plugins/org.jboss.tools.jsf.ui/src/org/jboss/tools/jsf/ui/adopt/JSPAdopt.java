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

import org.eclipse.jface.text.source.SourceViewer;
import org.jboss.tools.common.meta.XAdoptManager;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.jsf.project.JSFNature;
import org.jboss.tools.jsf.ui.JsfUiPlugin;
import org.jboss.tools.jst.web.project.list.WebPromptingProvider;

public class JSPAdopt implements XAdoptManager {

	public boolean isAdoptable(XModelObject target, XModelObject object) {
		return (isAdoptableProperty(object) || 
            (isAdoptableNature(target) && isAdoptableBundle(object)));
	}

	protected boolean isAdoptableProperty(XModelObject object) {
		String entity = object.getModelEntity().getName();
		return "Property".equals(entity);
	}

    protected boolean isAdoptableNature(XModelObject target) {
        try {
            String nature = (String)target.getModel().getProperties().get("nature");
            return JSF_NATURE_STRING.equalsIgnoreCase(nature);
        } catch (Exception x) {
        	JsfUiPlugin.getPluginLog().logError("Error in checking nature", x);
        }
        return false;
    }

    protected boolean isAdoptableBundle(XModelObject object) {
        return "FilePROPERTIES".equals(object.getModelEntity().getName());
    }

	public void adopt(XModelObject target, XModelObject object, Properties p) {
        if(isAdoptableProperty(object)) adoptProperty(target, object, p);
        if(isAdoptableBundle(object)) adoptBundle(target, object, p);
	}

	public void adoptProperty(XModelObject target, XModelObject object, Properties p) {
		String name = object.getAttributeValue("name");
		String bundle = getBundle(object.getParent());
		int pos = -1;
		try {
			pos = Integer.parseInt(p.getProperty("pos"));
		} catch (Exception e) {}
		SourceViewer viewer = (SourceViewer)p.get("viewer");
		
		JSPTokenizer tokenizer = new JSPTokenizer();
		Token root = null;
		try {
			root = tokenizer.parse(viewer.getDocument());
		} catch (Exception e) {
			JsfUiPlugin.getPluginLog().logError(e);
			return;
		}
		Token t = root.firstChild;
		while(t != null) {
			t = t.nextSibling;
		}
		
        int contextIndex = getContextIndex(tokenizer, pos);
        if(pos < 0) return;
		String jsfCorePrefix = JSPAdoptHelper.getPrefixForURI(root, JSF_CORE_TAGLIB_URI, JSF_CORE_TAGLIB_PREFIX_DEFAULT);
		String jsfHtmlPrefix = JSPAdoptHelper.getPrefixForURI(root, JSF_HTML_TAGLIB_URI, JSF_HTML_TAGLIB_PREFIX_DEFAULT);
		String prefix = "???";
		String varValue = JSPAdoptHelper.getLoadedBundleVar(root, jsfCorePrefix, bundle);
		if (varValue != null && varValue.length() > 0) prefix = varValue;

		if(prefix.equals("???")) {
			WebPromptingProvider fProvider = WebPromptingProvider.getInstance();
			List l = fProvider.getList(target.getModel(), WebPromptingProvider.JSF_REGISTERED_BUNDLES, null, null);
			int map_index = 1;
			Map map = l.size() <= map_index ? null : (Map)l.get(map_index);
			if(map != null && map.containsKey(bundle)) prefix = map.get(bundle).toString();
		}
		
		tokenizer.isInTagAttributeValue(pos);
		
		if(prefix.equals("???")) {
			int bp = JSPAdoptHelper.getPositionForBundle(root, jsfCorePrefix);
			if(bp < 0) return;
			prefix = JSPAdoptHelper.getNameForNewBundle(root, jsfCorePrefix);
			String loadBundle = "\n" + createLoadBundleTag(jsfCorePrefix, bundle, prefix);
			try {
				viewer.getDocument().replace(bp, 0, loadBundle);
				if(pos >= bp) pos += loadBundle.length();
			} catch (Exception e) {
				JsfUiPlugin.getPluginLog().logError(e);
			}
		}

        String start = getPropertyReference(prefix, name);
        if (contextIndex == 1) {
            start = "<" + jsfHtmlPrefix + ":outputText value=\"" + start + "\"/>";
        }
		try {
			viewer.getDocument().replace(pos, 0, start);
			viewer.setSelectedRange(pos, 0);
			viewer.getTextWidget().setFocus();
		} catch (Exception e) {}
		p.remove("start text");
	}
	
	private String getPropertyReference(String prefix, String name) {
		return (!isJavaName(name)) ? "#{" + prefix + "['" + name + "']}" : "#{" + prefix + "." + name + "}"; 
	}
	
	private boolean isJavaName(String name) {
		if(name.length() == 0) return true;
		char ch = name.charAt(0);
		if(!Character.isJavaIdentifierStart(ch)) return false;
		for (int i = 1; i < name.length(); i++) {
			ch = name.charAt(i);
			if(!Character.isJavaIdentifierPart(ch)) return false;
		}
		return true;
	}
	
	private int getContextIndex(JSPTokenizer tokenizer, int pos) {
		if(tokenizer.root == null) return -1;
		if (tokenizer.isInTagAttributeValue(pos)) return 0;
		try {
			Token e = tokenizer.getTokenAt(pos);
			if(e == null) return -1;
			if (e.kind == JSPTokenizer.TEXT || (pos == e.off)) {
				return 1;
			}
		} catch (Exception x) {
			JsfUiPlugin.getPluginLog().logError(x);
		}
		return -1;
	}
    
    private static final String JSF_NATURE_STRING = JSFNature.NATURE_ID;
    
    private String createLoadBundleTag(String jsfCorePrefix, String bundlePath, String var) {
    	return "<" + jsfCorePrefix + ":loadBundle basename=\"" + bundlePath + "\" var=\"" + var + "\" />";
    }

	public void adoptBundle(XModelObject target, XModelObject object, Properties p) {
		if(p == null) return;
		String bundle = getBundle(object);

		int pos = -1;
		try {
			pos = Integer.parseInt(p.getProperty("pos"));
		} catch (Exception e) {}
		SourceViewer viewer = (SourceViewer)p.get("viewer");

		JSPTokenizer tokenizer = new JSPTokenizer();
		Token root = null;
		try {
			root = tokenizer.parse(viewer.getDocument());
		} catch (Exception e) {
			JsfUiPlugin.getPluginLog().logError(e);
			return;
		}

		int contextIndex = getContextIndex(tokenizer, pos);
		String start = bundle;
		if (contextIndex == 1) {
			String jsfCorePrefix = JSPAdoptHelper.getPrefixForURI(root, JSF_CORE_TAGLIB_URI, JSF_CORE_TAGLIB_PREFIX_DEFAULT);
			start = createLoadBundleTag(jsfCorePrefix, bundle, "|");
		}
		p.setProperty("start text", start);
		p.setProperty("end text", "");
	}
    
    private final static String JSF_CORE_TAGLIB_URI = "http://java.sun.com/jsf/core";
    private final static String JSF_CORE_TAGLIB_PREFIX_DEFAULT = "f";
    private final static String JSF_HTML_TAGLIB_URI = "http://java.sun.com/jsf/html";
    private final static String JSF_HTML_TAGLIB_PREFIX_DEFAULT = "h";
    
	String getBundle(XModelObject file) {
		String bundle = XModelObjectLoaderUtil.getResourcePath(file);
		if(bundle == null) bundle = "";
		if(bundle.endsWith(".properties")) bundle = bundle.substring(0, bundle.length() - 11);
		bundle = bundle.substring(1).replace('/', '.');
		int i = bundle.indexOf('_');
		if(i >= 0) bundle = bundle.substring(0, i);
		return bundle;
	}

}
