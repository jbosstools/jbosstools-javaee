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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.source.SourceViewer;
import org.jboss.tools.common.meta.XAdoptManager;
import org.jboss.tools.common.model.XModelException;
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
		return "Property".equals(entity); //$NON-NLS-1$
	}

    protected boolean isAdoptableNature(XModelObject target) {
    	if(target == null) return false;
        String nature = (String)target.getModel().getProperties().get("nature"); //$NON-NLS-1$
        return JSF_NATURE_STRING.equalsIgnoreCase(nature);
    }

    protected boolean isAdoptableBundle(XModelObject object) {
        return "FilePROPERTIES".equals(object.getModelEntity().getName()); //$NON-NLS-1$
    }

	public void adopt(XModelObject target, XModelObject object, Properties p) throws XModelException {
        if(isAdoptableProperty(object)) adoptProperty(target, object, p);
        if(isAdoptableBundle(object)) adoptBundle(target, object, p);
	}

	public void adoptProperty(XModelObject target, XModelObject object, Properties p) {
		String name = object.getAttributeValue("name"); //$NON-NLS-1$
		String bundle = getBundle(object.getParent());
		int pos = -1;
		try {
			String s = p == null ? null : p.getProperty("pos"); //$NON-NLS-1$
			if(s != null && s.length() > 0) pos = Integer.parseInt(s);
		} catch (NumberFormatException e) {
			JsfUiPlugin.getPluginLog().logError(e);
		}
		SourceViewer viewer = (SourceViewer)p.get("viewer"); //$NON-NLS-1$
		if(viewer == null) {
			JsfUiPlugin.getPluginLog().logError("Viewer is null", new NullPointerException("Viewer is null")); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		if(viewer.getDocument() == null) {
			return;
		}
		
		JSPTokenizer tokenizer = new JSPTokenizer();
		Token root = tokenizer.parse(viewer.getDocument());
		Token t = root.firstChild;
		while(t != null) {
			t = t.nextSibling;
		}
		
        int contextIndex = getContextIndex(tokenizer, pos);
        if(pos < 0) return;
		String jsfCorePrefix = JSPAdoptHelper.getPrefixForURI(root, JSF_CORE_TAGLIB_URI, JSF_CORE_TAGLIB_PREFIX_DEFAULT);
		String jsfHtmlPrefix = JSPAdoptHelper.getPrefixForURI(root, JSF_HTML_TAGLIB_URI, JSF_HTML_TAGLIB_PREFIX_DEFAULT);
		String prefix = "???"; //$NON-NLS-1$
		String varValue = JSPAdoptHelper.getLoadedBundleVar(root, jsfCorePrefix, bundle);
		if (varValue != null && varValue.length() > 0) prefix = varValue;

		if(prefix.equals("???")) { //$NON-NLS-1$
			WebPromptingProvider fProvider = WebPromptingProvider.getInstance();
			List<Object> l = fProvider.getList(target.getModel(), WebPromptingProvider.JSF_REGISTERED_BUNDLES, null, null);
			int map_index = 1;
			Map map = l.size() <= map_index ? null : (Map)l.get(map_index);
			if(map != null && map.containsKey(bundle)) prefix = map.get(bundle).toString();
		}
		
		tokenizer.isInTagAttributeValue(pos);
		
		if(prefix.equals("???")) { //$NON-NLS-1$
			int bp = JSPAdoptHelper.getPositionForBundle(root, jsfCorePrefix);
			if(bp < 0) return;
			prefix = JSPAdoptHelper.getNameForNewBundle(root, jsfCorePrefix);
			String loadBundle = "\n" + createLoadBundleTag(jsfCorePrefix, bundle, prefix); //$NON-NLS-1$
			try {
				viewer.getDocument().replace(bp, 0, loadBundle);
				if(pos >= bp) pos += loadBundle.length();
			} catch (BadLocationException e) {
				JsfUiPlugin.getPluginLog().logError(e);
			}
		}

        String start = getPropertyReference(prefix, name);
        if (contextIndex == 1) {
            start = "<" + jsfHtmlPrefix + ":outputText value=\"" + start + "\"/>"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
		try {
			viewer.getDocument().replace(pos, 0, start);
			viewer.setSelectedRange(pos, 0);
			viewer.getTextWidget().setFocus();
		} catch (BadLocationException e) {
			JsfUiPlugin.getPluginLog().logError(e);
		}
		p.remove("start text"); //$NON-NLS-1$
	}
	
	private String getPropertyReference(String prefix, String name) {
		return (!isJavaName(name)) ? "#{" + prefix + "['" + name + "']}" : "#{" + prefix + "." + name + "}";  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
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
		Token e = tokenizer.getTokenAt(pos);
		if(e == null) return -1;
		if (e.kind == JSPTokenizer.TEXT || (pos == e.off)) {
			return 1;
		}
		return -1;
	}
    
    private static final String JSF_NATURE_STRING = JSFNature.NATURE_ID;
    
    private String createLoadBundleTag(String jsfCorePrefix, String bundlePath, String var) {
    	return "<" + jsfCorePrefix + ":loadBundle basename=\"" + bundlePath + "\" var=\"" + var + "\" />"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    }

	public void adoptBundle(XModelObject target, XModelObject object, Properties p) {
		if(p == null) return;
		String bundle = getBundle(object);

		int pos = -1;
		try {
			String s = p == null ? null : p.getProperty("pos"); //$NON-NLS-1$
			if(s != null && s.length() > 0) pos = Integer.parseInt(s);
		} catch (NumberFormatException e) {
			JsfUiPlugin.getPluginLog().logError(e);
		}
		SourceViewer viewer = (SourceViewer)p.get("viewer"); //$NON-NLS-1$
		
		if(viewer == null || viewer.getDocument() == null) return;

		JSPTokenizer tokenizer = new JSPTokenizer();
		Token root = tokenizer.parse(viewer.getDocument());

		int contextIndex = getContextIndex(tokenizer, pos);
		String start = bundle;
		if (contextIndex == 1) {
			String jsfCorePrefix = JSPAdoptHelper.getPrefixForURI(root, JSF_CORE_TAGLIB_URI, JSF_CORE_TAGLIB_PREFIX_DEFAULT);
			start = createLoadBundleTag(jsfCorePrefix, bundle, "|"); //$NON-NLS-1$
		}
		p.setProperty("start text", start); //$NON-NLS-1$
		p.setProperty("end text", ""); //$NON-NLS-1$ //$NON-NLS-2$
	}
    
    private final static String JSF_CORE_TAGLIB_URI = "http://java.sun.com/jsf/core"; //$NON-NLS-1$
    private final static String JSF_CORE_TAGLIB_PREFIX_DEFAULT = "f"; //$NON-NLS-1$
    private final static String JSF_HTML_TAGLIB_URI = "http://java.sun.com/jsf/html"; //$NON-NLS-1$
    private final static String JSF_HTML_TAGLIB_PREFIX_DEFAULT = "h"; //$NON-NLS-1$
    
	String getBundle(XModelObject file) {
		String bundle = XModelObjectLoaderUtil.getResourcePath(file);
		if(bundle == null) bundle = ""; //$NON-NLS-1$
		if(bundle.endsWith(".properties")) bundle = bundle.substring(0, bundle.length() - 11); //$NON-NLS-1$
		bundle = bundle.substring(1).replace('/', '.');
		int i = bundle.indexOf('_');
		if(i >= 0) bundle = bundle.substring(0, i);
		return bundle;
	}

}
