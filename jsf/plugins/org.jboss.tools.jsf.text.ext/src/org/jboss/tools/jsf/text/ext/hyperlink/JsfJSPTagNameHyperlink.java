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
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlink;
import org.jboss.tools.jst.text.ext.hyperlink.jsp.JSPRootHyperlinkPartitioner;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.jsf.text.ext.JSFTextExtMessages;
import org.jboss.tools.jst.web.project.list.WebPromptingProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Jeremy
 */
public class JsfJSPTagNameHyperlink extends AbstractHyperlink {

	/**
	 * @see com.ibm.sse.editor.AbstractHyperlink#doHyperlink(org.eclipse.jface.text.IRegion)
	 */
	protected void doHyperlink(IRegion region) {
		IFile documentFile = getFile();
		XModel xModel = getXModel(documentFile);
		if (xModel == null) {
			openFileFailed();
			return;
		}
		WebPromptingProvider provider = WebPromptingProvider.getInstance();

		Properties p = getRequestProperties(region);
		p.put(WebPromptingProvider.FILE, documentFile);

		List<Object> list = provider.getList(xModel, WebPromptingProvider.JSF_OPEN_TAG_LIBRARY, p.getProperty("prefix"), p); //$NON-NLS-1$
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
			p.setProperty("prefix", value); //$NON-NLS-1$
		}
		value = getTagName(region);
		if (value != null) {
			p.setProperty(WebPromptingProvider.NAME, value);
		}
		
		return p;
	}
	
	private String getURI(IRegion region) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(getDocument());
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;

			Node n = Utils.findNodeForOffset(xmlDocument, region.getOffset());

			if (!(n instanceof Element)) return null;
			
			Node node = n;

			String nodeName = node.getNodeName();
			if (nodeName.indexOf(':') == -1) return null;

			String nodePrefix = nodeName.substring(0, nodeName.indexOf(":")); //$NON-NLS-1$
			if (nodePrefix == null || nodePrefix.length() == 0) return null;

			
			Map trackers = JSPRootHyperlinkPartitioner.getTrackersMap(getDocument(), region.getOffset());
			
			return (String)(trackers == null ? null : trackers.get(nodePrefix));
		} finally {
			smw.dispose();
		}
	}
	
	private String getTagName(IRegion region) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(getDocument());
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			Node n = Utils.findNodeForOffset(xmlDocument, region.getOffset());

			if (!(n instanceof Element)) return null;
			
			Node node = n;

			String tagName = node.getNodeName();
			if (tagName.indexOf(':') == -1) return null;
			
			return tagName.substring(tagName.indexOf(':') + 1);
		} finally {
			smw.dispose();
		}
	}
	
	IRegion fLastRegion = null;
	/**
	 * @see com.ibm.sse.editor.AbstractHyperlink#doGetHyperlinkRegion(int)
	 */
	protected IRegion doGetHyperlinkRegion(int offset) {
		fLastRegion = getRegion(offset);
		return fLastRegion;
	}
	
	protected IRegion getRegion (int offset) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(getDocument());
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			Node n = Utils.findNodeForOffset(xmlDocument, offset);

			if (n == null || !(n instanceof IDOMElement)) return null;
			
			IDOMElement elem = (IDOMElement)n;
			String tagName = elem.getTagName();
			int start = elem.getStartOffset();
			final int nameStart = start + (elem.isEndTag() ? "</" : "<").length(); //$NON-NLS-1$ //$NON-NLS-2$
			final int nameEnd = nameStart + tagName.length();

			if (nameStart > offset || nameEnd <= offset) return null;
			
			return new Region(nameStart,nameEnd - nameStart);
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
		String tagName = getTagName(fLastRegion);
		if (tagName == null)
			return JSFTextExtMessages.OpenTagLibraryForATag;
		
		return MessageFormat.format(JSFTextExtMessages.OpenTagLibraryForTagName, tagName);
	}

}
