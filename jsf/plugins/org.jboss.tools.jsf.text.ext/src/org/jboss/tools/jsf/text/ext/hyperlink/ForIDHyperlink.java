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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlink;
import org.jboss.tools.common.text.ext.hyperlink.xpl.Messages;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.StructuredSelectionHelper;
import org.jboss.tools.jsf.text.ext.JSFExtensionsPlugin;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Jeremy
 */
public class ForIDHyperlink extends AbstractHyperlink {

	/** 
	 * @see com.ibm.sse.editor.AbstractHyperlink#doHyperlink(org.eclipse.jface.text.IRegion)
	 */
	protected void doHyperlink(IRegion region) {
		String forID = getForId(region);
		IRegion elementByID = findElementByID(forID);
		if (elementByID != null) {
			StructuredSelectionHelper.setSelectionAndRevealInActiveEditor(elementByID);
		} else {
			openFileFailed();
		}
	}
	
	private IRegion findElementByID (String id) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(getDocument());
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;

			IDOMElement element = findElementByID(xmlDocument.getChildNodes(), id);
			if (element != null) {
				final int offset = element.getStartOffset();
				final int length = element.getStartStructuredDocumentRegion().getLength();
				return new Region(offset,length);
			}
			return null;
		} finally {
			smw.dispose();
		}
	}
	
	private IDOMElement findElementByID(NodeList list, String id) {
		if(list == null || id == null) return null;
		for (int i = 0; i < list.getLength(); i++) {
			Node n = list.item(i);
			if(!(n instanceof IDOMElement)) continue;

			IDOMElement element = (IDOMElement)n;
			Attr idAttr = element.getAttributeNode("id"); //$NON-NLS-1$
			if (idAttr != null) {
				String val = trimQuotes(idAttr.getNodeValue());
				if (id.equals(val)) {
					return element;
				}
			}
					
			if (element.hasChildNodes()) {
				IDOMElement child = findElementByID(element.getChildNodes(), id);
				if (child != null) return child;
			}
		}
		return null;
	}

	String getForId(IRegion region) {
		if(region == null) return null;
		IDocument document = getDocument();
		if(document == null) return null;
		try {
			return trimQuotes(document.get(region.getOffset(), region.getLength()));
		} catch (BadLocationException x) {
			JSFExtensionsPlugin.log("", x); //$NON-NLS-1$
			return null;
		}
	}
	
	private String trimQuotes(String word) {
		if(word == null) return null;
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
		String forId = getForId(getHyperlinkRegion());
		if (forId == null)
			return  MessageFormat.format(Messages.BrowseFor, Messages.Id);
		
		return MessageFormat.format(Messages.BrowseForId, forId);
	}
}