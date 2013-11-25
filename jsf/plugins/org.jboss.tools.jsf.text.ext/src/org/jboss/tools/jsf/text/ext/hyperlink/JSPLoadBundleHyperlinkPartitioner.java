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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlinkPartitioner;
import org.jboss.tools.common.text.ext.hyperlink.HyperlinkRegion;
import org.jboss.tools.common.text.ext.hyperlink.IHyperlinkPartitionRecognizer;
import org.jboss.tools.common.text.ext.hyperlink.IHyperlinkRegion;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.jsf.text.ext.JSFExtensionsPlugin;
import org.jboss.tools.jst.web.ui.internal.text.ext.hyperlink.jsp.JSPRootHyperlinkPartitioner;
import org.jboss.tools.jst.web.ui.internal.text.ext.util.TaglibManagerWrapper;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Jeremy
 */
public class JSPLoadBundleHyperlinkPartitioner extends AbstractHyperlinkPartitioner implements IHyperlinkPartitionRecognizer {
	public static final String JSP_LOADBUNDLE_PARTITION = "org.jboss.tools.common.text.ext.jsp.JSP_LOADBUNDLE"; //$NON-NLS-1$
	
	protected String getPartitionType() {
		return JSP_LOADBUNDLE_PARTITION;
	}

	/**
	 * @see com.ibm.sse.editor.hyperlink.AbstractHyperlinkPartitioner#parse(org.eclipse.jface.text.IDocument, com.ibm.sse.editor.extensions.hyperlink.IHyperlinkRegion)
	 */
	protected IHyperlinkRegion parse(IDocument document, int offset, IHyperlinkRegion superRegion) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(document);
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			if (!recognize(document, offset, superRegion)) return null;
			IHyperlinkRegion r = getRegion(document, offset);
			if (r == null) return null;
			
			String axis = getAxis(document, offset);
			String contentType = superRegion.getContentType();
			String type = getPartitionType();
			
			return new HyperlinkRegion(r.getOffset(), r.getLength(), axis, contentType, type);
		} finally {
			smw.dispose();
		}
	}

	protected String getAxis(IDocument document, int offset) {
		return JSPRootHyperlinkPartitioner.computeAxis(document, offset) + "/"; //$NON-NLS-1$
	}
	
	public static IHyperlinkRegion getRegion(IDocument document, final int offset) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		try {
			smw.init(document);
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			Node n = Utils.findNodeForOffset(xmlDocument, offset);

			if (n == null || !(n instanceof Attr)) return null;
			
			int start = Utils.getValueStart(n);
			int end = Utils.getValueEnd(n);
			
			if (start < 0 || start > offset) return null;

			String attrText = document.get(start, end - start);
			StringBuffer sb = new StringBuffer(attrText);

			//find start of bean property
			int bStart = offset - start;
			while (bStart >= 0) { 
				if (!Character.isJavaIdentifierPart(sb.charAt(bStart)) && sb.charAt(bStart) != '.') {
					bStart++;
					break;
				}
			
				if (bStart == 0) break;
				bStart--;
			}
			// find end of bean property
			int bEnd = offset - start;
			while (bEnd < sb.length()) { 
				if (!Character.isJavaIdentifierPart(sb.charAt(bEnd)) && sb.charAt(bEnd) != '.')
					break;
				bEnd++;
			}
			
			int propStart = bStart + start;
			int propLength = bEnd - bStart;
			
			if (propStart > offset || propStart + propLength < offset) return null;
			
			IHyperlinkRegion region = new HyperlinkRegion(propStart, propLength, null, null, null);
			return region;
		} catch (BadLocationException x) {
			JSFExtensionsPlugin.log("", x); //$NON-NLS-1$
			return null;
		} finally {
			smw.dispose();
		}
	}

	/**
	 * @see com.ibm.sse.editor.extensions.hyperlink.IHyperlinkPartitionRecognizer#recognize(org.eclipse.jface.text.IDocument, com.ibm.sse.editor.extensions.hyperlink.IHyperlinkRegion)
	 */
	public boolean recognize(IDocument document, int offset, IHyperlinkRegion region) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(document);
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return false;
			
			Node n = Utils.findNodeForOffset(xmlDocument, offset);
			if (!(n instanceof Attr)) return false;

			IHyperlinkRegion r = getRegion(document, offset);
			if (r == null) return false;

			Attr attr = (Attr)n;
			String attrName = attr.getNodeName();
			if (!"var".equals(attrName) && !"basename".equals(attrName)) return false; //$NON-NLS-1$ //$NON-NLS-2$
			
			Element lbTag = attr.getOwnerElement();
			String name = lbTag.getTagName();
			int column = name.indexOf(":"); //$NON-NLS-1$
			if (column == -1) return false;
			String usedPrefix = name.substring(0, column);
			if (usedPrefix == null || usedPrefix.trim().length() == 0) return false;
			
			String[] prefixes = getLoadBundleTagPrefixes(document, offset);
			if (prefixes == null) return true; //xhtml

			boolean prefixIsAbleToBeUsed = false;
			for (String prefix : prefixes) {
				if (usedPrefix.equals(prefix)) {
					prefixIsAbleToBeUsed = true;
					break;
				}
			}
			if (!prefixIsAbleToBeUsed)
				return false;

			Attr lbTagVar = lbTag.getAttributeNode("var"); //$NON-NLS-1$
			Attr lbTagBasename = lbTag.getAttributeNode("basename"); //$NON-NLS-1$

			if (lbTagVar == null || lbTagVar.getNodeValue() == null ||
					lbTagVar.getNodeValue().trim().length() == 0) return false;
			if (lbTagBasename == null || lbTagBasename.getNodeValue() == null ||
					lbTagBasename.getNodeValue().trim().length() == 0) return false;
			
			return true;
		} finally {
			smw.dispose();
		}
	}

	protected String[] getLoadBundleTagPrefixes(IDocument document, int offset) {
		TaglibManagerWrapper tmw = new TaglibManagerWrapper();
		tmw.init(document, offset);
		if(!tmw.exists()) return null;
		
		return new String[] {tmw.getCorePrefix()};
	}
}