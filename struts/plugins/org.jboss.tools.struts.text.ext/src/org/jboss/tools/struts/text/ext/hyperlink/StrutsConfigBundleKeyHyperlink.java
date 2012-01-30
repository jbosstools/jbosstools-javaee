/*******************************************************************************
 * Copyright (c) 2007-2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.struts.text.ext.hyperlink;

import java.text.MessageFormat;
import java.util.Properties;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.jboss.tools.common.text.ext.hyperlink.xpl.Messages;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.jst.web.project.list.WebPromptingProvider;
import org.jboss.tools.struts.text.ext.StrutsExtensionsPlugin;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * @author Jeremy
 */
public class StrutsConfigBundleKeyHyperlink extends StrutsXModelBasedHyperlink {
	
	protected String getRequestMethod() {
		return WebPromptingProvider.STRUTS_OPEN_KEY;
	}

	protected Properties getRequestProperties(IRegion region) {
		Properties p = new Properties();

		String value = getKey(region);
		value = (value == null? "" : value);
		p.setProperty(WebPromptingProvider.KEY, value);
		p.setProperty("prefix", value);

		value = getBundle(region);
		value = (value == null? "" : value);
		p.setProperty(WebPromptingProvider.BUNDLE, value);
		
		return p;
	}
	
	private String getBundle(IRegion region) {
		if(region == null) return "";
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(getDocument());
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			Node n = Utils.findNodeForOffset(xmlDocument, region.getOffset());
			if (n == null || !(n instanceof Attr)) return null;
			Node node = ((Attr)n).getOwnerElement();
			Attr bundleAttr = (Attr)node.getAttributes().getNamedItem("bundle");
			if(bundleAttr == null) return "";
			return Utils.getTrimmedValue(getDocument(), bundleAttr);
		} catch (BadLocationException x) {
			StrutsExtensionsPlugin.getPluginLog().logError(x);
			return "";
		} finally {
			smw.dispose();
		}
	}

	private String getKey(IRegion region) {
		if(region == null || getDocument() == null) return "";
		try {
			return Utils.trimQuotes(getDocument().get(region.getOffset(), region.getLength()));
		} catch (BadLocationException x) {
			StrutsExtensionsPlugin.getPluginLog().logError(x);
			return "";
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see IHyperlink#getHyperlinkText()
	 */
	public String getHyperlinkText() {
		String baseName = getBundle(getHyperlinkRegion());
		String propertyName = getKey(getHyperlinkRegion());
		if (baseName == null || propertyName == null)
			return  MessageFormat.format(Messages.OpenA, Messages.BundleProperty);
		
		return MessageFormat.format(Messages.OpenBundleProperty, propertyName, baseName);
	}
}
