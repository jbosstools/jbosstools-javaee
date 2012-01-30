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
public class StrutsValidationPropertyHyperlink extends StrutsXModelBasedHyperlink {
	
	private static final String NAME_ATTRNAME = "name";
	
	protected String getRequestMethod() {
		return WebPromptingProvider.STRUTS_OPEN_FORM_BEAN;
	}

	protected Properties getRequestProperties(IRegion region) {
		Properties p = new Properties();

		String value = getFormName(region);
		if (value != null) {
			p.setProperty("prefix", value);
		}
		
		value = getProperty(region);
		if (value != null) {
			p.setProperty(WebPromptingProvider.PROPERTY, value);
		}
		
		return p;
	}	

	private String getProperty(IRegion region) {
		if(region == null || getDocument() == null) return null;
		try {
			return Utils.trimQuotes(getDocument().get(region.getOffset(), region.getLength()));
		} catch (BadLocationException x) {
			StrutsExtensionsPlugin.getPluginLog().logError(x);
			return null;
		}
	}
	
	private String getFormName(IRegion region) {
		if(region == null) return null;
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(getDocument());
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			Node n = Utils.findNodeForOffset(xmlDocument, region.getOffset());
			if (n == null || !(n instanceof Attr)) return null;
			
			Node node = ((Attr)n).getOwnerElement();
			Node parentNode = node.getParentNode();
			
			return getAttributeValue(getDocument(), parentNode, NAME_ATTRNAME);
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
		String propertyName = getProperty(getHyperlinkRegion());
		String formName = getFormName(getHyperlinkRegion());
		if (propertyName == null || formName == null)
			return  MessageFormat.format(Messages.OpenA, StrutsTextExtMessages.ValidationProperty);
		
		return MessageFormat.format(StrutsTextExtMessages.OpenValidationProperty, propertyName, formName);
	}
}
