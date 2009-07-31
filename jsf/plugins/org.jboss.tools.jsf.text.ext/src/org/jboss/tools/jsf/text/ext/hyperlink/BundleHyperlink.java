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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IEditorPart;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.text.ext.hyperlink.XModelBasedHyperlink;
import org.jboss.tools.common.text.ext.hyperlink.xpl.Messages;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.TaglibManagerWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.jsf.text.ext.JSFExtensionsPlugin;
import org.jboss.tools.jst.web.project.list.WebPromptingProvider;
import org.jboss.tools.jst.web.tld.TaglibData;
import org.jboss.tools.jst.web.tld.VpeTaglibManager;
import org.jboss.tools.jst.web.tld.VpeTaglibManagerProvider;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Jeremy
 */
public class BundleHyperlink extends XModelBasedHyperlink {

	private String getBundleProperty(IRegion region) {
		if(getDocument() == null) return null;
		try { 
			String fullText = getDocument().get(region.getOffset(), region.getLength());
			
			// get var name
			int dotIndex = fullText.indexOf("."); //$NON-NLS-1$
			int bracketIndex = fullText.indexOf("["); //$NON-NLS-1$

			boolean useDot = false;
			boolean useBracket = false;
			
			if (dotIndex != -1) useDot = true;
			if (bracketIndex != -1) {
				if (!useDot || (useDot && dotIndex > bracketIndex)) 
					useBracket = true;
					useDot = false;
			}
			if (useDot && fullText.indexOf(".", dotIndex + 1) != -1) //$NON-NLS-1$
					useDot = false;

			if (!useDot && !useBracket) return null;

			String sProp = null;
			if (useDot) {
				sProp = fullText.substring(dotIndex + 1);
			}
			if (useBracket) {
				int startProp = bracketIndex + 1;
				int endProp = fullText.indexOf("]"); //$NON-NLS-1$
				if (endProp == -1) endProp = fullText.length() - 1;
				sProp = Utils.trimQuotes(fullText.substring(startProp, endProp));
			}
			return sProp;
		} catch (BadLocationException x) {
			JSFExtensionsPlugin.log("", x); //$NON-NLS-1$
			return null;
		}

	}
	
	private String getBundleBasename(IRegion region) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(getDocument());
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			String bundleProp = getDocument().get(region.getOffset(), region.getLength());
			
			String[] prefixes = getLoadBundleTagPrefixes(region);
			if(prefixes == null) return null;
			
			// Find loadBundle tag
			List<Element> lbTags = new ArrayList<Element>();
			for (String prefix : prefixes) {
				NodeList list = xmlDocument.getElementsByTagName(prefix + ":loadBundle"); //$NON-NLS-1$
				for (int i = 0; list != null && i < list.getLength(); i++) {
					Element el = (Element)list.item(i);
					int end = Utils.getValueEnd(el);
					if (end >= 0 && end < region.getOffset()) {
						lbTags.add(el);
					}
				}
			}
			for (int i = 0; i < lbTags.size(); i++) {
				Element el = (Element)lbTags.get(i);
				Attr var = el.getAttributeNode("var"); //$NON-NLS-1$
				if (bundleProp.startsWith("" + var.getValue() + ".") //$NON-NLS-1$ //$NON-NLS-2$
					|| bundleProp.startsWith("" + var.getValue() + "['") //$NON-NLS-1$ //$NON-NLS-2$
					|| bundleProp.equals(var.getValue())) {
					return ((Attr) el.getAttributeNode("basename")).getNodeValue(); //$NON-NLS-1$
				}
			}
			String bundleVar = bundleProp;
			if(bundleVar.indexOf('.') >= 0) bundleVar = bundleVar.substring(0, bundleVar.indexOf('.'));
			if(bundleVar.indexOf("['") >= 0) bundleVar = bundleVar.substring(0, bundleVar.indexOf("['")); //$NON-NLS-1$ //$NON-NLS-2$
			XModel xmodel = smw.getXModel();
			List list2 = WebPromptingProvider.getInstance().getList(xmodel, WebPromptingProvider.JSF_REGISTERED_BUNDLES, null, new Properties());
			if(list2 == null || list2.size() < 1) return null;
			Map map = (Map)list2.get(0);
			return (String)map.get(bundleVar);
		} catch (BadLocationException x) {
			JSFExtensionsPlugin.log("", x); //$NON-NLS-1$
			return null;
		} finally {
			smw.dispose();
		}
	}

	protected String[] getLoadBundleTagPrefixes(IRegion region) {
		TaglibManagerWrapper tmw = new TaglibManagerWrapper();
		tmw.init(getDocument(), region.getOffset());
		if(tmw.exists()) {
			return new String[] { tmw.getCorePrefix() };
		} else {
			VpeTaglibManager taglibManager = getTaglibManager();
			if(taglibManager == null) return null;
			TaglibData[] data = (TaglibData[])taglibManager.getTagLibs().toArray(new TaglibData[0]);
			ArrayList<String> prefixes = new ArrayList<String>();
			for (int i = 0; i < data.length; i++) {
				if("http://java.sun.com/jsf/core".equals(data[i].getUri()))  //$NON-NLS-1$
					prefixes.add(data[i].getPrefix());
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
	
	private static final String VIEW_TAGNAME = "view"; //$NON-NLS-1$
	private static final String LOCALE_ATTRNAME = "locale"; //$NON-NLS-1$
	private static final String PREFIX_SEPARATOR = ":"; //$NON-NLS-1$

	private String getPageLocale(IRegion region) {
		if(getDocument() == null || region == null) return null;

		StructuredModelWrapper smw = new StructuredModelWrapper();
		try {
			smw.init(getDocument());
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			String[] prefixes = getLoadBundleTagPrefixes(region);
			if(prefixes == null) return null;

			Node n = Utils.findNodeForOffset(xmlDocument, region.getOffset());
			if (!(n instanceof Attr) ) return null; 

			Element el = ((Attr)n).getOwnerElement();
			
			Element jsfCoreViewTag = null;
			for (String prefix : prefixes) {
				String nodeToFind = prefix + PREFIX_SEPARATOR + VIEW_TAGNAME; 
	
				while (el != null) {
					if (nodeToFind.equals(el.getNodeName())) {
						jsfCoreViewTag = el;
						break;
					}
					Node parent = el.getParentNode();
					el = (parent instanceof Element ? (Element)parent : null); 
				}
			}
			
			if (jsfCoreViewTag == null || !jsfCoreViewTag.hasAttribute(LOCALE_ATTRNAME)) return null;
			
			String locale = Utils.trimQuotes((jsfCoreViewTag.getAttributeNode(LOCALE_ATTRNAME)).getValue());
			if (locale == null || locale.length() == 0) return null;
			return locale;
		} finally {
			smw.dispose();
		}
	}

	IRegion fLastRegion = null;
	
	/** 
	 * @see com.ibm.sse.editor.AbstractHyperlink#doGetHyperlinkRegion(int)
	 */
	protected IRegion doGetHyperlinkRegion(int offset) {
		fLastRegion = JSPBundleHyperlinkPartitioner.getRegion(getDocument(), offset);
		return fLastRegion;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see IHyperlink#getHyperlinkText()
	 */
	public String getHyperlinkText() {
		String baseName = getBundleBasename(fLastRegion); 
		String propertyName = getBundleProperty(fLastRegion);
		if (baseName == null || propertyName == null)
			return  MessageFormat.format(Messages.OpenA, Messages.BundleProperty);
		
		return MessageFormat.format(Messages.OpenBundleProperty, propertyName, baseName);
	}

}
