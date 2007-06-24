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

import java.util.Properties;

import org.eclipse.jface.text.IRegion;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.TaglibManagerWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.common.text.ext.hyperlink.XModelBasedHyperlink;
import org.jboss.tools.jsf.text.ext.JSFExtensionsPlugin;
import org.jboss.tools.jst.web.project.list.WebPromptingProvider;

/**
 * @author Jeremy
 *
 */
public class LoadBundleHyperlink extends XModelBasedHyperlink {
	
	protected String getRequestMethod() {
		return WebPromptingProvider.JSF_OPEN_BUNDLE;
	}

	protected Properties getRequestProperties(IRegion region) {
		Properties p = new Properties();

		String value = getBundleBasename(region);
		value = (value == null? "" : value);
		p.setProperty(WebPromptingProvider.BUNDLE, value);

		value = getPageLocale(region);
		if (value != null) {
			p.setProperty(WebPromptingProvider.LOCALE, value);
		}

		return p;
	}
	
	private String getBundleBasename(IRegion region) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		try {
			smw.init(getDocument());
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			Node n = Utils.findNodeForOffset(xmlDocument, region.getOffset());
			if (!(n instanceof Attr) ) return null; 
			
			Element lbTag = ((Attr)n).getOwnerElement();
			Attr lbTagBasename = lbTag.getAttributeNode("basename");

			if (lbTagBasename == null || lbTagBasename.getNodeValue() == null ||
					lbTagBasename.getNodeValue().trim().length() == 0) return null;
			return lbTagBasename.getNodeValue();
			
		} catch (Exception x) {
			JSFExtensionsPlugin.log("", x);
			return null;
		} finally {
			smw.dispose();
		}
	}
	
	private static final String VIEW_TAGNAME = "view";
	private static final String LOCALE_ATTRNAME = "locale";
	private static final String PREFIX_SEPARATOR = ":";

	private String getPageLocale(IRegion region) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		try {
			TaglibManagerWrapper tmw = new TaglibManagerWrapper();
			tmw.init(getDocument(), region.getOffset());
			
			if (!tmw.exists()) return null;
			String prefix = tmw.getCorePrefix();
			if (prefix == null || prefix.length() == 0) return null;

			String nodeToFind = prefix + PREFIX_SEPARATOR + VIEW_TAGNAME; 
			
			smw.init(getDocument());
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
				if(!(el.getParentNode() instanceof Element)) {
					break;
				}
				el = (Element)el.getParentNode();
			}
			
			if (jsfCoreViewTag == null || !jsfCoreViewTag.hasAttribute(LOCALE_ATTRNAME)) return null;
			
			String locale = Utils.trimQuotes(((Attr)jsfCoreViewTag.getAttributeNode(LOCALE_ATTRNAME)).getValue());
			if (locale == null || locale.length() == 0) return null;
			return locale;
		} catch (Exception x) {
			JSFExtensionsPlugin.log("", x);
			return null;
		} finally {
			smw.dispose();
		}
	}
	
}
