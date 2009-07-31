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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlinkPartitioner;
import org.jboss.tools.common.text.ext.hyperlink.HyperlinkRegion;
import org.jboss.tools.common.text.ext.hyperlink.IHyperlinkRegion;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.common.text.ext.hyperlink.jsp.JSPRootHyperlinkPartitioner;
import org.jboss.tools.jsf.text.ext.JSFExtensionsPlugin;

/**
 * @author Jeremy
 */
public class JSPForIDHyperlinkPartitioner extends AbstractHyperlinkPartitioner /*implements IHyperlinkPartitionRecognizer */{
	public static final String JSP_FOR_ID_PARTITION = "org.jboss.tools.common.text.ext.jsp.JSP_FOR_ID"; //$NON-NLS-1$

	/**
	 * @see com.ibm.sse.editor.hyperlink.AbstractHyperlinkPartitioner#parse(org.eclipse.jface.text.IDocument, com.ibm.sse.editor.extensions.hyperlink.IHyperlinkRegion)
	 */
	protected IHyperlinkRegion parse(IDocument document, IHyperlinkRegion superRegion) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(document);
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			Utils.findNodeForOffset(xmlDocument, superRegion.getOffset());
			IHyperlinkRegion r = getRegion(document, superRegion.getOffset());
			if (r == null) return null;
			
			String axis = getAxis(document, superRegion);
			String contentType = superRegion.getContentType();
			String type = JSP_FOR_ID_PARTITION;
			int length = r.getLength() - (superRegion.getOffset() - r.getOffset());
			int offset = superRegion.getOffset();
			
			IHyperlinkRegion region = new HyperlinkRegion(offset, length, axis, contentType, type);
			return region;
		} finally {
			smw.dispose();
		}
	}

	protected String getAxis(IDocument document, IHyperlinkRegion superRegion) {
		if (superRegion.getAxis() == null || superRegion.getAxis().length() == 0) {
			return JSPRootHyperlinkPartitioner.computeAxis(document, superRegion.getOffset()) + "/"; //$NON-NLS-1$
		}
		return superRegion.getAxis();
	}
	
	public static IHyperlinkRegion getRegion(IDocument document, final int offset) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(document);
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			Node n = Utils.findNodeForOffset(xmlDocument, offset);

			if (n == null || !(n instanceof Attr)) return null;
			
			int start = Utils.getValueStart(n);
			int end = Utils.getValueEnd(n);
			
			if (start > offset) return null;

			String attrText = document.get(start, end - start);
			StringBuffer sb = new StringBuffer(attrText);

			//find start and end of path property
			int bStart = 0;
			int bEnd = attrText.length() - 1;

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

			int propStart = bStart + start;
			int propLength = bEnd - bStart;
			
			
			if (propStart > offset || propStart + propLength < offset) return null;

			// Find an ID (suppose that there may be a list of commas-separated IDs)
			int bIdStart = offset;
			int bIdEnd = offset;
			
			//find start of bean property
			while (bIdStart >= propStart) { 
				if (!Character.isJavaIdentifierPart(sb.charAt(bIdStart - start)) &&
						sb.charAt(bIdStart - start) != ' ') {
					bIdStart++;
					break;
				}
			
				if (bIdStart == 0) break;
				bIdStart--;
			}
			
			// find end of bean property
			while (bIdEnd < propStart + propLength) { 
				if (!Character.isJavaIdentifierPart(sb.charAt(bIdEnd - start)) &&
						sb.charAt(bIdEnd - start) != ' ')
					break;
				bIdEnd++;
			}
			
			// Skip leading spaces
			while (bIdStart < bIdEnd) {
				if (Character.isJavaIdentifierPart(sb.charAt(bIdStart - start))) {
					break;
				}
			
				bIdStart++;
			}

			// Skip trailing spaces
			while (bIdEnd > bIdStart) {
				if (Character.isJavaIdentifierPart(sb.charAt(bIdEnd - 1 - start))) {
					break;
				}
			
				bIdEnd--;
			}

			
			int idStart = bIdStart;
			int idLength = bIdEnd - bIdStart;
			
			if (idStart > offset || idStart + idLength < offset) return null;
			
			
			
			IHyperlinkRegion region = new HyperlinkRegion(idStart, idLength, null, null, null);
			return region;
		} catch (BadLocationException x) {
			JSFExtensionsPlugin.log("", x); //$NON-NLS-1$
			return null;
		} finally {
			smw.dispose();
		}
		
	}

}
