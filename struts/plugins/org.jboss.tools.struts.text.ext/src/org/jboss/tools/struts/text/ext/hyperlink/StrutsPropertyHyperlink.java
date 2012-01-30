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
public class StrutsPropertyHyperlink extends StrutsXModelBasedHyperlink {
	
	protected String getRequestMethod() {
		return WebPromptingProvider.STRUTS_OPEN_PROPERTY;
	}

	protected Properties getRequestProperties(IRegion region) {
		Properties p = new Properties();
		String value = getProperty(region);
		if (value != null) {
			p.setProperty(WebPromptingProvider.PROPERTY, value);
			p.setProperty("prefix", value);
		}
		value = getFormAction(region);
		if (value != null) p.setProperty(WebPromptingProvider.ACTION, value);
		value = getFormType(region);
		if (value != null) p.setProperty(WebPromptingProvider.TYPE, value);
		return p;
	}
	
	private String getProperty(IRegion region) {
		if(region == null || getDocument() == null) return "";
		try {
			return Utils.trimQuotes(getDocument().get(region.getOffset(), region.getLength()));
		} catch (BadLocationException x) {
			StrutsExtensionsPlugin.getPluginLog().logError(x);
			return "";
		}
	}
	
	private String getFormType(IRegion region) {
		if(region == null) return null;
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(getDocument());
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			Node n = Utils.findNodeForOffset(xmlDocument, region.getOffset());

			if (n == null || !(n instanceof Attr)) return null;
			
			Node node = ((Attr)n).getOwnerElement();
			String nodeName = node.getNodeName();
			String prefix = nodeName.substring(0, nodeName.indexOf(':'));
			
			String formName = prefix + ":form";
			Node formNode = null;
			
			Node parent = node.getParentNode();
			for (; parent != null; parent = parent.getParentNode() ) {
				if (formName.equals(parent.getNodeName())) {
					formNode = parent;
					break;
				}
			}

			if (formNode == null) return null;
			
			Attr typeAttr = (Attr)formNode.getAttributes().getNamedItem("type");
			if (typeAttr == null) return null;
			return Utils.getTrimmedValue(getDocument(), typeAttr);
		} catch (BadLocationException x) {
			StrutsExtensionsPlugin.getPluginLog().logError(x);
			return null;
		} finally {
			smw.dispose();
		}
	}
	
	private String getFormAction (IRegion region) {
		if(region == null) return null;
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(getDocument());
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			Node n = Utils.findNodeForOffset(xmlDocument, region.getOffset());

			if (n == null || !(n instanceof Attr)) return null;
			
			Node node = ((Attr)n).getOwnerElement();
			String nodeName = node.getNodeName();
			String prefix = nodeName.substring(0, nodeName.indexOf(':'));
			
			String formName = prefix + ":form";
			Node formNode = null;
			
			Node parent = node.getParentNode();
			for (; parent != null; parent = parent.getParentNode() ) {
				if (formName.equals(parent.getNodeName())) {
					formNode = parent;
					break;
				}
			}

			if (formNode == null) return null;
			
			Attr actionAttr = (Attr)formNode.getAttributes().getNamedItem("action");
			if (actionAttr == null) return null;
			return Utils.getTrimmedValue(getDocument(), actionAttr);
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
		String propertyName = getProperty(getHyperlinkRegion());
		String actionName = getFormAction(getHyperlinkRegion());
		if (propertyName == null || actionName == null)
			return  MessageFormat.format(Messages.OpenA, StrutsTextExtMessages.Property);
		
		return MessageFormat.format(StrutsTextExtMessages.OpenPropertyForFormAction, propertyName, actionName);
	}
}