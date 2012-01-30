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
import org.jboss.tools.struts.text.ext.StrutsTextExtMessages;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Jeremy
 *
 */
public class StrutsActionHyperlink extends StrutsXModelBasedHyperlink {
	
	protected String getRequestMethod() {
		return WebPromptingProvider.STRUTS_OPEN_LINK_ACTION;
	}

	protected Properties getRequestProperties(IRegion region) {
		Properties p = new Properties();

		p.setProperty(WebPromptingProvider.MODULE, getModule(region));
		p.setProperty("prefix", getAction(region));
		return p;
	}
	
	private String getAction(IRegion region) {
		if(region == null || getDocument() == null) return "";
		try {
			return Utils.trimQuotes(getDocument().get(region.getOffset(), region.getLength()));
		} catch (BadLocationException x) {
			StrutsExtensionsPlugin.getPluginLog().logError(x);
			return "";
		}
	}
	
	private String getModule(IRegion region) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(getDocument());
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			Node n = Utils.findNodeForOffset(xmlDocument, region.getOffset());
			if (!(n instanceof Attr)) return null;
			if (n == null || !(n instanceof Attr)) return null;
			Element node = ((Attr)n).getOwnerElement();
			Attr bundleAttr = (Attr)node.getAttributes().getNamedItem("module");
			if(bundleAttr == null) return "";
			return Utils.getTrimmedValue(getDocument(), bundleAttr);
		} catch (BadLocationException x) {
			StrutsExtensionsPlugin.getPluginLog().logError(x);
			return "";
		} finally {
			smw.dispose();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IHyperlink#getHyperlinkText()
	 */
	public String getHyperlinkText() {
		String actionName = getAction(getHyperlinkRegion());
		if (actionName == null)
			return  MessageFormat.format(Messages.OpenAn, StrutsTextExtMessages.Action);
		
		return MessageFormat.format(StrutsTextExtMessages.OpenAction, actionName);
	}
}
