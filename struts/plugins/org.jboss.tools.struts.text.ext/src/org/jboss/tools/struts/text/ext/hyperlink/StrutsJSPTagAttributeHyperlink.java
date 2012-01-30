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
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlink;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.jst.text.ext.util.TaglibManagerWrapper;
import org.jboss.tools.jst.web.project.list.WebPromptingProvider;
import org.jboss.tools.struts.text.ext.StrutsExtensionsPlugin;
import org.jboss.tools.struts.text.ext.StrutsTextExtMessages;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Jeremy
 */
public class StrutsJSPTagAttributeHyperlink extends AbstractHyperlink {

	/** 
	 * @see com.ibm.sse.editor.AbstractHyperlink#doHyperlink(org.eclipse.jface.text.IRegion)
	 */
	protected void doHyperlink(IRegion region) {
		IFile file = getFile();
		XModel xModel = getXModel(file);
		if (xModel == null){ 
			openFileFailed();
			return;
		}
		WebPromptingProvider provider = WebPromptingProvider.getInstance();

		Properties p = getRequestProperties(region);
		p.put(WebPromptingProvider.FILE, file);

		List<Object> list = provider.getList(xModel, WebPromptingProvider.STRUTS_OPEN_TAG_LIBRARY, p.getProperty("prefix"), p);
		if (list != null && list.size() >= 1) {
			openFileInEditor((String)list.get(0));
			return;
		}
		String error = p.getProperty(WebPromptingProvider.ERROR); 
		if ( error != null && error.length() > 0) {
			openFileFailed();
		}
	}
	
	protected Properties getRequestProperties(IRegion region) {
		Properties p = new Properties();
		
		String value = getURI(region);
		if (value != null) {
			p.setProperty("prefix", value);
		}
		value = getTagName(region);
		if (value != null) {
			p.setProperty(WebPromptingProvider.NAME, value);
		}
		value = getTagAttributeName(region);
		if (value != null) {
			p.setProperty(WebPromptingProvider.ATTRIBUTE, value);
		}
		
		return p;
	}
	
	private String getURI(IRegion region) {
		if(region == null) return null;
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(getDocument());
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			Node n = Utils.findNodeForOffset(xmlDocument, region.getOffset());
			if (n instanceof Attr) {
				n = ((Attr)n).getOwnerElement();
			}
			if (!(n instanceof Element)) return null;
			Node node = n.getParentNode();
			String nodeName = node.getNodeName();
			if (nodeName.indexOf(':') == -1) return null;
			String nodePrefix = nodeName.substring(0, nodeName.indexOf(":"));
			if (nodePrefix == null || nodePrefix.length() == 0) return null;

			TaglibManagerWrapper tmw = new TaglibManagerWrapper();
			tmw.init(getDocument(), region.getOffset());
			if(!tmw.exists()) return null;
			
			return tmw.getUri(nodePrefix);
		} finally {
			smw.dispose();
		}
	}
	
	private String getTagName(IRegion region) {
		if(region == null) return null;
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(getDocument());
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			Node n = Utils.findNodeForOffset(xmlDocument, region.getOffset());
			if (n instanceof Attr) {
				n = ((Attr)n).getOwnerElement();
			}
			if (!(n instanceof Element)) return null;
			Node node = (Node)n.getParentNode();
			String tagName = node.getNodeName();
			if (tagName.indexOf(':') == -1) return null;
			return tagName.substring(tagName.indexOf(':') + 1);
		} finally {
			smw.dispose();
		}
	}
	
	private String getTagAttributeName(IRegion region) {
		if(region == null || getDocument() == null) return null;
		try {
			return Utils.trimQuotes(getDocument().get(region.getOffset(), region.getLength()));
		} catch (BadLocationException x) {
			StrutsExtensionsPlugin.getPluginLog().logError(x);
			return null;
		}
	}
	
	protected String getAttributeValue (IDocument document, Node node, String attrName) {
		if(document == null || node == null || attrName == null) return null;
		try {
			Attr attr = (Attr)node.getAttributes().getNamedItem(attrName);
			return Utils.getTrimmedValue(document, attr);
		} catch (BadLocationException x) {
			StrutsExtensionsPlugin.getPluginLog().logError(x);
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IHyperlink#getHyperlinkText()
	 */
	public String getHyperlinkText() {
		String tagName = getTagName(getHyperlinkRegion());
		String attrName = getTagAttributeName(getHyperlinkRegion());
		if (tagName == null || attrName == null)
			return StrutsTextExtMessages.OpenTagLibraryForAnAttribute;

		return MessageFormat.format(StrutsTextExtMessages.OpenTagLibraryForAttributeName, attrName, tagName);
	}
}
