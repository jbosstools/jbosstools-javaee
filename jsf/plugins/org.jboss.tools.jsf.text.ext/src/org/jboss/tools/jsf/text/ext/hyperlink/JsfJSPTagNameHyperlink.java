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

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlink;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.common.text.ext.hyperlink.jsp.JSPRootHyperlinkPartitioner;
import org.jboss.tools.jsf.text.ext.JSFExtensionsPlugin;
import org.jboss.tools.jst.web.project.list.WebPromptingProvider;

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
		if (xModel == null) return;
		try {	
			WebPromptingProvider provider = WebPromptingProvider.getInstance();

			Properties p = getRequestProperties(region);
			p.put(WebPromptingProvider.FILE, documentFile);

			List list = provider.getList(xModel, WebPromptingProvider.JSF_OPEN_TAG_LIBRARY, p.getProperty("prefix"), p);
			if (list != null && list.size() >= 1) {
				openFileInEditor((String)list.get(0));
				return;
			}
			String error = p.getProperty(WebPromptingProvider.ERROR); 
			if ( error != null && error.length() > 0) {
				openFileFailed();
			}
		} catch (Exception x) {
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
		
		return p;
	}
	
	private String getURI(IRegion region) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		try {
			smw.init(getDocument());
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;

			Node n = Utils.findNodeForOffset(xmlDocument, region.getOffset());

			if (!(n instanceof Element)) return null;
			
			Node node = n;

			String nodeName = node.getNodeName();
			if (nodeName.indexOf(':') == -1) return null;

			String nodePrefix = nodeName.substring(0, nodeName.indexOf(":"));
			if (nodePrefix == null || nodePrefix.length() == 0) return null;

			
			Map trackers = JSPRootHyperlinkPartitioner.getTrackersMap(getDocument(), region.getOffset());
			
			return (String)(trackers == null ? null : trackers.get(nodePrefix));
		} catch (Exception x) {
			JSFExtensionsPlugin.log("Error while getting uri from region", x);
			return null;
		} finally {
			smw.dispose();
		}
	}
	
	private String getTagName(IRegion region) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		try {
			smw.init(getDocument());
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			Node n = Utils.findNodeForOffset(xmlDocument, region.getOffset());

			if (!(n instanceof Element)) return null;
			
			Node node = n;

			String tagName = node.getNodeName();
			if (tagName.indexOf(':') == -1) return null;
			
			return tagName.substring(tagName.indexOf(':') + 1);
		} catch (Exception x) {
			JSFExtensionsPlugin.log("", x);
			return null;
		} finally {
			smw.dispose();
		}
	}
	
	/**
	 * @see com.ibm.sse.editor.AbstractHyperlink#doGetHyperlinkRegion(int)
	 */
	protected IRegion doGetHyperlinkRegion(int offset) {
		try {
			return getRegion(offset);
		} catch (Exception x) {
			JSFExtensionsPlugin.log("", x);
			return null;
		}
	}
	
	protected IRegion getRegion (int offset) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		try {
			smw.init(getDocument());
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			Node n = Utils.findNodeForOffset(xmlDocument, offset);

			if (n == null || !(n instanceof IDOMElement)) return null;
			
			IDOMElement elem = (IDOMElement)n;
			String tagName = elem.getTagName();
			int start = elem.getStartOffset();
			final int nameStart = start + (elem.isEndTag() ? "</" : "<").length();
			final int nameEnd = nameStart + tagName.length();

			if (nameStart > offset || nameEnd <= offset) return null;

			IRegion region = new IRegion () {

				public int getLength() {
					return nameEnd - nameStart;
				}

				public int getOffset() {
					return nameStart;
				}

				public boolean equals(Object arg) {
					if (!(arg instanceof IRegion)) return false;
					IRegion region = (IRegion)arg;
					
					if (getOffset() != region.getOffset()) return false;
					if (getLength() != region.getLength()) return false;
					return true;
				}

				public String toString() {
					return "IRegion [" + getOffset() +", " + getLength()+ "]";
				}
			};
			
			return region;
		} catch (Exception x) {
			JSFExtensionsPlugin.log("", x);
			return null;
		} finally {
			smw.dispose();
		}
	}
	
	protected String getAttributeValue (IDocument document, Node node, String attrName) {
		try {
			Attr attr = (Attr)node.getAttributes().getNamedItem(attrName);
			return Utils.getTrimmedValue(document, attr);
		} catch (Exception x) {
			JSFExtensionsPlugin.log("", x);
			return null;
		}
	}

}
