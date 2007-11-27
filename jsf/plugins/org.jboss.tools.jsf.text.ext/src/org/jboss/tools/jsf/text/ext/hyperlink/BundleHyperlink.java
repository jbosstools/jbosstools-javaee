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
package org.jboss.tools.jsf.text.ext.hyperlink;

import java.util.*;

import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.jboss.tools.common.model.XModel;
import org.jboss.tools.jsf.text.ext.JSFExtensionsPlugin; 
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.TaglibManagerWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.common.text.ext.hyperlink.XModelBasedHyperlink;
import org.jboss.tools.jst.web.project.list.WebPromptingProvider;
import org.jboss.tools.jst.web.tld.TaglibData;
import org.jboss.tools.jst.web.tld.VpeTaglibManager;
import org.jboss.tools.jst.web.tld.VpeTaglibManagerProvider;

/**
 * @author Jeremy
 */
public class BundleHyperlink extends XModelBasedHyperlink {

	private String getBundleProperty(IRegion region) {
		try { 
			String fullText = getDocument().get(region.getOffset(), region.getLength());
			
			// get var name
			int dotIndex = fullText.indexOf(".");
			int bracketIndex = fullText.indexOf("[");

			boolean useDot = false;
			boolean useBracket = false;
			
			if (dotIndex != -1) useDot = true;
			if (bracketIndex != -1) {
				if (!useDot || (useDot && dotIndex > bracketIndex)) 
					useBracket = true;
					useDot = false;
			}
			if (useDot && fullText.indexOf(".", dotIndex + 1) != -1)
					useDot = false;

			if (!useDot && !useBracket) return null;

			String sProp = null;
			if (useDot) {
				sProp = fullText.substring(dotIndex + 1);
			}
			if (useBracket) {
				int startProp = bracketIndex + 1;
				int endProp = fullText.indexOf("]");
				if (endProp == -1) endProp = fullText.length() - 1;
				sProp = Utils.trimQuotes(fullText.substring(startProp, endProp));
			}
			return sProp;
		} catch (Exception x) {
			JSFExtensionsPlugin.log("", x);
			return null;
		}

	}
	
	private String getBundleBasename(IRegion region) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		try {
			smw.init(getDocument());
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			String bundleProp = getDocument().get(region.getOffset(), region.getLength());
			
			String prefix = getPrefix(region);
			if(prefix == null) return null;
			
			// Find loadBundle tag
			List<Element> lbTags = new ArrayList<Element>();
			NodeList list = xmlDocument.getElementsByTagName(prefix + ":loadBundle");
			for (int i = 0; list != null && i < list.getLength(); i++) {
				Element el = (Element)list.item(i);
				int end = Utils.getValueEnd(el);
				if (end >= 0 && end < region.getOffset()) {
					lbTags.add(el);
				}
			}
	
			for (int i = 0; i < lbTags.size(); i++) {
				Element el = (Element)lbTags.get(i);
				Attr var = el.getAttributeNode("var");
				if (bundleProp.startsWith("" + var.getValue() + ".")
					|| bundleProp.startsWith("" + var.getValue() + "['")
					|| bundleProp.equals(var.getValue())) {
					return ((Attr) el.getAttributeNode("basename")).getNodeValue();
				}
			}
			String bundleVar = bundleProp;
			if(bundleVar.indexOf('.') >= 0) bundleVar = bundleVar.substring(0, bundleVar.indexOf('.'));
			if(bundleVar.indexOf("['") >= 0) bundleVar = bundleVar.substring(0, bundleVar.indexOf("['"));
			XModel xmodel = smw.getXModel();
			List list2 = WebPromptingProvider.getInstance().getList(xmodel, WebPromptingProvider.JSF_REGISTERED_BUNDLES, null, new Properties());
			if(list2 == null || list2.size() < 1) return null;
			Map map = (Map)list2.get(0);
			return (String)map.get(bundleVar);
		} catch (Exception x) {
			JSFExtensionsPlugin.log("", x);
			return null;
		} finally {
			smw.dispose();
		}
	}
	
	private String getPrefix(IRegion region) {
		TaglibManagerWrapper tmw = new TaglibManagerWrapper();
		tmw.init(getDocument(), region.getOffset());
		if(tmw.exists()) {
			return tmw.getCorePrefix();
		} else {
			VpeTaglibManager taglibManager = getTaglibManager();
			if(taglibManager == null) return null;
			TaglibData[] data = (TaglibData[])taglibManager.getTagLibs().toArray(new TaglibData[0]);
			for (int i = 0; i < data.length; i++) {
				if("http://java.sun.com/jsf/core".equals(data[i].getUri())) return data[i].getPrefix();
			}			
		}		
		return null;
	}
	
	private VpeTaglibManager getTaglibManager() {
		IEditorPart editor = JSFExtensionsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if(editor instanceof VpeTaglibManagerProvider) {
			return ((VpeTaglibManagerProvider)editor).getTaglibManager();
		}
		return null;
	}
	
	private static final String VIEW_TAGNAME = "view";
	private static final String LOCALE_ATTRNAME = "locale";
	private static final String PREFIX_SEPARATOR = ":";

	private String getPageLocale(IRegion region) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		try {
			smw.init(getDocument());

			TaglibManagerWrapper tmw = new TaglibManagerWrapper();
			tmw.init(getDocument(), region.getOffset());
			if(!tmw.exists()) return null;
			String prefix = tmw.getCorePrefix();
	
			if (prefix == null || prefix.length() == 0) return null;

			String nodeToFind = prefix + PREFIX_SEPARATOR + VIEW_TAGNAME; 
			
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			Node n = Utils.findNodeForOffset(xmlDocument, region.getOffset());
			if (!(n instanceof Attr) ) return null; 

			Element el = ((Attr)n).getOwnerElement();
			
			Element jsfCoreViewTag = null;
			while (el != null) {
				if (nodeToFind.equals(el.getNodeName())) {
					jsfCoreViewTag = el;
					break;
				}
				el = (Element)el.getParentNode();
			}
			
			if (jsfCoreViewTag == null || !jsfCoreViewTag.hasAttribute(LOCALE_ATTRNAME)) return null;
			
			String locale = Utils.trimQuotes((jsfCoreViewTag.getAttributeNode(LOCALE_ATTRNAME)).getValue());
			if (locale == null || locale.length() == 0) return null;
			return locale;
		} catch (Exception x) {
			JSFExtensionsPlugin.log("", x);
			return null;
		} finally {
			smw.dispose();
		}
	}

	/** 
	 * @see com.ibm.sse.editor.AbstractHyperlink#doGetHyperlinkRegion(int)
	 */
	protected IRegion doGetHyperlinkRegion(int offset) {
		IRegion region = JSPBundleHyperlinkPartitioner.getRegion(getDocument(), offset);
		return region;
	}

	protected String getRequestMethod() {
		return WebPromptingProvider.JSF_OPEN_KEY;
	}

	protected Properties getRequestProperties(IRegion region) {
		Properties p = new Properties();
		String value = getBundleBasename(region);
		if (value != null) {
			p.put(WebPromptingProvider.BUNDLE, value);
		}
		
		value = getBundleProperty(region);
		if (value != null) {
			p.put(WebPromptingProvider.KEY, value);
		}
		
		value = getPageLocale(region);
		if (value != null) {
			p.setProperty(WebPromptingProvider.LOCALE, value);
		}

		return p;
	}

}
