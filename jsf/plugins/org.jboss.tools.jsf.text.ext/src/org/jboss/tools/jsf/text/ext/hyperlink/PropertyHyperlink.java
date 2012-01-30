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
package org.jboss.tools.jsf.text.ext.hyperlink;

import java.text.MessageFormat;
import java.util.Properties;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlink;
import org.jboss.tools.common.text.ext.hyperlink.xpl.Messages;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.jsf.text.ext.JSFExtensionsPlugin;
import org.jboss.tools.jsf.text.ext.JSFTextExtMessages;
import org.jboss.tools.jst.web.project.list.WebPromptingProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * @author Jeremy
 */
public class PropertyHyperlink extends AbstractHyperlink {

	/** 
	 * @see com.ibm.sse.editor.AbstractHyperlink#doHyperlink(org.eclipse.jface.text.IRegion)
	 */
	protected void doHyperlink(IRegion region) {
		XModel xModel = getXModel();
		if (xModel == null) {
			openFileFailed();
			return;
		}
		
		String propertyName = getPropertyName(region);
		String beanClassName = getBeanClassName(region);
		WebPromptingProvider provider = WebPromptingProvider.getInstance();

		Properties p = new Properties();
		p.setProperty("ignoreWarning", "true"); //$NON-NLS-1$ //$NON-NLS-2$
		p.setProperty("property", propertyName); //$NON-NLS-1$
		beanClassName = beanClassName.replace('.', '/') + ".class"; //$NON-NLS-1$
			
		provider.getList(xModel, WebPromptingProvider.JSF_OPEN_CLASS_PROPERTY, beanClassName, p);
		String error = p.getProperty(WebPromptingProvider.ERROR); 
		if ( error != null && error.length() > 0) {
			openFileFailed();
		}
	}

	private String getPropertyName(IRegion region) {
		if(getDocument() == null || region == null) return null;
		try {
			return trimQuotes(getDocument().get(region.getOffset(), region.getLength()));
		} catch (BadLocationException x) {
			JSFExtensionsPlugin.log("", x); //$NON-NLS-1$
			return null;
		}
	}
	
	private String getBeanClassName(IRegion region) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(getDocument());
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			Node n = Utils.findNodeForOffset(xmlDocument, region.getOffset());
			
			if (n instanceof Text) n = n.getParentNode();
			n = n.getParentNode().getParentNode();
			
			Element beanElement = (Element)n;
			String beanName = beanElement.getNodeName();
			String beanClassElementName = beanName + "-class"; //$NON-NLS-1$
			NodeList children = beanElement.getElementsByTagName(beanClassElementName);
			Element beanClassElement = null;
			for (int i = 0; children != null && i < children.getLength(); i++) {
				if (children.item(i) instanceof Element) {
					beanClassElement = (Element)children.item(i);
					break;
				}
			}
			if (beanClassElement == null) return null;
			children = beanClassElement.getChildNodes();
			String beanClassName = null;
			for (int i = 0; children != null && i < children.getLength(); i++) {
				if (children.item(i) instanceof Text) {
					Text xmlText = (Text)children.item(i);
					int start = Utils.getValueStart(xmlText);
					int end = Utils.getValueEnd(xmlText);
					if(start < 0) continue;
					beanClassName = getDocument().get(start, end - start);
					break;
				}
			}
			if (beanClassName == null) return null;
			
			beanClassName = trimQuotes(beanClassName);
			
			return (beanClassName.length() == 0 ? null : beanClassName);
		} catch (BadLocationException x) {
			JSFExtensionsPlugin.log("", x); //$NON-NLS-1$
			return null;
		} finally {
			smw.dispose();
		}
	}
	
	private String trimQuotes(String word) {
		String attrText = word;
		int bStart = 0;
		int bEnd = word.length() - 1;
		StringBuffer sb = new StringBuffer(attrText);
			//find start and end of path property
		while (bStart < bEnd && 
				(sb.charAt(bStart) == '\'' || sb.charAt(bStart) == '\"' ||
						Character.isWhitespace(sb.charAt(bStart)))) { 
			bStart++;
		}
		while (bEnd > bStart && 
				(sb.charAt(bEnd) == '\'' || sb.charAt(bEnd) == '\"' ||
						Character.isWhitespace(sb.charAt(bEnd)))) { 
			bEnd--;
		}
		bEnd++;
		return sb.substring(bStart, bEnd);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IHyperlink#getHyperlinkText()
	 */
	public String getHyperlinkText() {
		String beanClassName = getBeanClassName(getHyperlinkRegion());
		String propertyName = getPropertyName(getHyperlinkRegion());
		if (beanClassName == null || propertyName == null)
			return  MessageFormat.format(Messages.OpenA, JSFTextExtMessages.BeanProperty);
		
		return MessageFormat.format(JSFTextExtMessages.OpenBeanProperty, beanClassName, propertyName);
	}
}
