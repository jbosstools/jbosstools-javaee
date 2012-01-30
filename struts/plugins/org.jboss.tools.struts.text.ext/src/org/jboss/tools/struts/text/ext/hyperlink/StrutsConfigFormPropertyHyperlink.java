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
import org.w3c.dom.Node;

/**
 * @author Jeremy
 *
 */
public class StrutsConfigFormPropertyHyperlink extends StrutsXModelBasedHyperlink {
	
	protected String getRequestMethod() {
		return WebPromptingProvider.STRUTS_OPEN_PROPERTY;
	}

	protected Properties getRequestProperties(IRegion region) {
		Properties p = new Properties();
		String value = getName(region);
		if (value != null) {
			p.setProperty(WebPromptingProvider.PROPERTY, value);
			p.setProperty("prefix", value);
		}
		value = getFormType(region);
		if (value != null) p.setProperty(WebPromptingProvider.TYPE, value);
		return p;
	}
	
	private String getName(IRegion region) {
		if(region == null || getDocument() == null) return null;
		try {
			return Utils.trimQuotes(getDocument().get(region.getOffset(), region.getLength()));
		} catch (BadLocationException x) {
			StrutsExtensionsPlugin.getPluginLog().logError(x);
			return null;
		}
	}
	
	private String getFormType(IRegion region) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(getDocument());
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			Node n = Utils.findNodeForOffset(xmlDocument, region.getOffset());
			if (n == null || !(n instanceof Attr)) return null;
			Node node = ((Attr)n).getOwnerElement(); // form-property element
			Node parent = node.getParentNode();	// form-bean element
			Attr typeAttr = (Attr)parent.getAttributes().getNamedItem("type");
			if (typeAttr == null) return null;
			String type = Utils.getTrimmedValue(getDocument(), typeAttr);
			return type;
		} catch (BadLocationException x) {
			StrutsExtensionsPlugin.getPluginLog().logError(x);
			return null;
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
		String formPropertyName = getName(getHyperlinkRegion());
		if (formPropertyName == null)
			return  MessageFormat.format(Messages.OpenA, StrutsTextExtMessages.FormProperty);
		
		return MessageFormat.format(StrutsTextExtMessages.OpenFormProperty, formPropertyName);
	}
}
