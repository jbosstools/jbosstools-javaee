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
import org.jboss.tools.common.text.ext.hyperlink.IHyperlinkRegion;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.jsf.text.ext.JSFExtensionsPlugin;
import org.jboss.tools.jst.text.ext.hyperlink.jsp.JSPRootHyperlinkPartitioner;
import org.jboss.tools.jst.text.ext.util.TaglibManagerWrapper;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * @author Jeremy
 */
public class JSPForwardHyperlinkPartitioner extends AbstractHyperlinkPartitioner /*implements IHyperlinkPartitionRecognizer */{
	public static final String JSP_FORWARD_PARTITION = "org.jboss.tools.common.text.ext.jsp.JSP_FORWARD"; //$NON-NLS-1$

	private static final String EL_DOLLAR_PREFIX = "${"; //$NON-NLS-1$
	private static final String EL_SUFFIX = "}"; //$NON-NLS-1$
	private static final String EL_SHARP_PREFIX = "#{"; //$NON-NLS-1$

	/**
	 * @see com.ibm.sse.editor.hyperlink.AbstractHyperlinkPartitioner#parse(org.eclipse.jface.text.IDocument, com.ibm.sse.editor.extensions.hyperlink.IHyperlinkRegion)
	 */
	protected IHyperlinkRegion parse(IDocument document, int offset, IHyperlinkRegion superRegion) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(document);
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			IHyperlinkRegion r = getRegion(document, offset);
			if (r == null) return null;
			
			String axis = getAxis(document, offset);
			String contentType = superRegion.getContentType();
			String type = JSP_FORWARD_PARTITION;
			
			return new HyperlinkRegion(r.getOffset(), r.getLength(), axis, contentType, type);
		} finally {
			smw.dispose();
		}
	}

	protected String getAxis(IDocument document, int offset) {
		return JSPRootHyperlinkPartitioner.computeAxis(document, offset) + "/"; //$NON-NLS-1$
	}

	public IHyperlinkRegion getRegion(IDocument document, final int offset) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(document);
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			Node n = Utils.findNodeForOffset(xmlDocument, offset);

			if (n == null || !(n instanceof Attr || n instanceof Text)) return null;
			
			int start = Utils.getValueStart(n);
			int end = Utils.getValueEnd(n);
			
			String text = document.get(start, end - start);
			StringBuffer sb = new StringBuffer(text);

			int bStart = 0; 
			int bEnd = sb.length();
			
			// In case of attribute value we need to skip leading and ending quotes && whitespaces
			while (bStart < bEnd && (sb.charAt(bStart) == '"' || sb.charAt(bStart) == '\'' ||
					sb.charAt(bStart) == 0x09 || sb.charAt(bStart) == 0x0A ||
					sb.charAt(bStart) == 0x0D || sb.charAt(bStart) == 0x20)) {
				bStart++;
			}
			
			while (bEnd - 1 > bStart && (sb.charAt(bEnd - 1) == '"' || sb.charAt(bEnd - 1) == '\'' ||
					sb.charAt(bEnd - 1) == 0x09 || sb.charAt(bEnd - 1) == 0x0A ||
					sb.charAt(bEnd - 1) == 0x0D || sb.charAt(bEnd - 1) == 0x20)) {
				bEnd--;
			}
			if (start + bStart > offset || start + bEnd - 1 < offset) return null;

			int elStart = sb.indexOf(EL_SHARP_PREFIX) == -1 ? sb.indexOf(EL_DOLLAR_PREFIX) : sb.indexOf(EL_SHARP_PREFIX);
			if (elStart != -1  && elStart >= bStart && elStart < bEnd) {
				int elEnd = sb.indexOf(EL_SUFFIX, elStart);
				bStart = (elEnd == -1 || elEnd > bEnd) ? bEnd : elEnd + 1;
			}
			
			//find start and end of path property
			while (bStart >= 0) { 
				if (!Character.isJavaIdentifierPart(sb.charAt(bStart)) &&
						sb.charAt(bStart) != '\\' && sb.charAt(bStart) != '/' &&
						sb.charAt(bStart) != ':' && sb.charAt(bStart) != '-' &&
						sb.charAt(bStart) != '.' && sb.charAt(bStart) != '_' &&
						sb.charAt(bStart) != '%' && sb.charAt(bStart) != '?' &&
						sb.charAt(bStart) != '&' && sb.charAt(bStart) != '=') {
					bStart++;
					break;
				}
			
				if (bStart == 0) break;
				bStart--;
			}
			// find end of bean property
			bEnd = bStart;
			while (bEnd < sb.length()) { 
				if (!Character.isJavaIdentifierPart(sb.charAt(bEnd)) &&
						sb.charAt(bEnd) != '\\' && sb.charAt(bEnd) != '/' &&
						sb.charAt(bEnd) != ':' && sb.charAt(bEnd) != '-' &&
						sb.charAt(bEnd) != '.' && sb.charAt(bEnd) != '_' &&
						sb.charAt(bEnd) != '%' && sb.charAt(bEnd) != '?' &&
						sb.charAt(bEnd) != '&' && sb.charAt(bEnd) != '=') {
					break;
				}
				bEnd++;
			}

			int propStart = bStart + start;
			int propLength = bEnd - bStart;
			if (propStart > offset + 1 || propStart + propLength < offset) return null;
			
			return new HyperlinkRegion(propStart, propLength);
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
	public boolean recognize(IDocument document, IHyperlinkRegion region) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(document);
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return false;
			
			Node n = Utils.findNodeForOffset(xmlDocument, region.getOffset());
			if (!(n instanceof Attr)) return false;

			IHyperlinkRegion r = getRegion(document, region.getOffset());
			if (r == null) return false;

			Attr attr = (Attr)n;
			String attrName = attr.getNodeName();
			if (!"var".equals(attrName) && !"basename".equals(attrName)) return false; //$NON-NLS-1$ //$NON-NLS-2$
			
			Element lbTag = attr.getOwnerElement();
			String name = lbTag.getTagName();
			int column = name.indexOf(":"); //$NON-NLS-1$
			if (column == -1) return false;
			String prefix = name.substring(0, column);
			if (prefix == null || prefix.trim().length() == 0) return false;
			
			TaglibManagerWrapper tmw = new TaglibManagerWrapper();
			tmw.init(document, region.getOffset());
			if(!tmw.exists()) return false;

			if (!prefix.equals(tmw.getCorePrefix())) return false;

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
}
