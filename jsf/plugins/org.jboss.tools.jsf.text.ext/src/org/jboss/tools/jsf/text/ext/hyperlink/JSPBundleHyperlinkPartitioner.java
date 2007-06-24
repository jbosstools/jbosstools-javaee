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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlinkPartitioner;
import org.jboss.tools.common.text.ext.hyperlink.HyperlinkRegion;
import org.jboss.tools.common.text.ext.hyperlink.IHyperlinkPartitionRecognizer;
import org.jboss.tools.common.text.ext.hyperlink.IHyperlinkRegion;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.TaglibManagerWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.common.text.ext.hyperlink.jsp.JSPRootHyperlinkPartitioner;
import org.jboss.tools.jsf.text.ext.JSFExtensionsPlugin;

/**
 * @author Jeremy
 */
public class JSPBundleHyperlinkPartitioner extends AbstractHyperlinkPartitioner implements IHyperlinkPartitionRecognizer {
	public static final String JSP_BUNDLE_PARTITION = "org.jboss.tools.common.text.ext.jsp.JSP_BUNDLE";

	/**
	 * @see com.ibm.sse.editor.hyperlink.AbstractHyperlinkPartitioner#parse(org.eclipse.jface.text.IDocument, com.ibm.sse.editor.extensions.hyperlink.IHyperlinkRegion)
	 */
	protected IHyperlinkRegion parse(IDocument document, IHyperlinkRegion superRegion) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		try {
			smw.init(document);
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			Utils.findNodeForOffset(xmlDocument, superRegion.getOffset());
			if (!recognize(document, superRegion)) return null;
			IHyperlinkRegion r = getRegion(document, superRegion.getOffset());
			if (r == null) return null;
			
			String axis = getAxis(document, superRegion);
			String contentType = superRegion.getContentType();
			String type = JSP_BUNDLE_PARTITION;
			int length = r.getLength() - (superRegion.getOffset() - r.getOffset());
			int offset = superRegion.getOffset();
			
			IHyperlinkRegion region = new HyperlinkRegion(offset, length, axis, contentType, type);
			return region;
		} catch (Exception x) {
			JSFExtensionsPlugin.log("", x);
			return null;
		} finally {
			smw.dispose();
		}
	}

	protected String getAxis(IDocument document, IHyperlinkRegion superRegion) {
		if (superRegion.getAxis() == null || superRegion.getAxis().length() == 0) {
			return JSPRootHyperlinkPartitioner.computeAxis(document, superRegion.getOffset()) + "/";
		}
		return superRegion.getAxis();
	}
	
	public static IHyperlinkRegion getRegion(IDocument document, final int offset) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		try {
			smw.init(document);
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			Node n = Utils.findNodeForOffset(xmlDocument, offset);

			if (n == null || !(n instanceof Attr || n instanceof Text)) return null;

			int start = Utils.getValueStart(n);
			int end = Utils.getValueEnd(n);
			if(start < 0 || start > end || start > offset) return null;
			String attrText = document.get(start, end - start);

			StringBuffer sb = new StringBuffer(attrText);
			//find start of bean property
			int bStart = offset - start;
			while (bStart >= 0) { 
				if (!Character.isJavaIdentifierPart(sb.charAt(bStart)) &&
						sb.charAt(bStart) != '.' && sb.charAt(bStart) != '[' && sb.charAt(bStart) != ']' &&
						sb.charAt(bStart) != '\'') {
					bStart++;
					break;
				}
			
				if (bStart == 0) break;
				bStart--;
			}
			// find end of bean property
			int bEnd = offset - start;
			while (bEnd < sb.length()) { 
				if (!Character.isJavaIdentifierPart(sb.charAt(bEnd)) &&
						sb.charAt(bEnd) != '.' && sb.charAt(bEnd) != '[' && sb.charAt(bEnd) != ']' &&
						sb.charAt(bEnd) != '\'')
					break;
				bEnd++;
			}
			
			int propStart = bStart + start;
			int propLength = bEnd - bStart;
			
			if (propStart > offset || propStart + propLength < offset) return null;
			
			IHyperlinkRegion region = new HyperlinkRegion(propStart, propLength, null, null, null);
			return region;
		} catch (Exception x) {
			JSFExtensionsPlugin.log("", x);
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
		try {
			smw.init(document);
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return false;
			
			Utils.findNodeForOffset(xmlDocument, region.getOffset());

			IHyperlinkRegion r = getRegion(document, region.getOffset());
			if (r == null) return false;
			
			String bundleProp = document.get(r.getOffset(), r.getLength());
			
			// get var name
			int dotIndex = bundleProp.indexOf(".");
			int bracketIndex = bundleProp.indexOf("[");

			boolean useDot = false;
			boolean useBracket = false;
			
			if (dotIndex != -1) useDot = true;
			if (bracketIndex != -1) {
				if (!useDot || (useDot && dotIndex > bracketIndex)) 
					useBracket = true;
			}
			if (useDot && bundleProp.indexOf(".", dotIndex + 1) != -1)
					useDot = false;

			if (!useDot && !useBracket) return false;

			String sVar = null;
			String sProp = null;
			if (useDot) {
				sVar = bundleProp.substring(0, dotIndex);
				sProp = bundleProp.substring(dotIndex + 1);
			}
			if (useBracket) {
				sVar = bundleProp.substring(0, bracketIndex);
				int startProp = bracketIndex + 1;
				int endProp = bundleProp.indexOf("]");
				if (endProp == -1) endProp = bundleProp.length() - 1;
				sProp = Utils.trimQuotes(bundleProp.substring(startProp, endProp));
			}
			
			
			if (sVar != null && sProp != null) return true;
			
			TaglibManagerWrapper tmw = new TaglibManagerWrapper();
			tmw.init(document, region.getOffset());
			if(!tmw.exists()) return false;
			
			String prefix = tmw.getCorePrefix();
			
			if (prefix == null) return false;
			
			// Find loadBundle tag
			List<Element> lbTags = new ArrayList<Element>();
			NodeList list = xmlDocument.getElementsByTagName(prefix + ":loadBundle");
			for (int i = 0; list != null && i < list.getLength(); i++) {
				Element el = (Element)list.item(i);
				int end = Utils.getValueEnd(el);
				if (end >= 0 && end < region.getOffset()) {
					lbTags.add(el);
				}
			}

			Element lbTag = null;
			for (int i = 0; i < lbTags.size(); i++) {
				Element el = lbTags.get(i);
				Attr var = el.getAttributeNode("var");
				
				if (bundleProp.startsWith(var.getValue())) {
					lbTag = el;
					break;
				}
			}
			if (lbTag == null) return false;
			
			return true;
		} catch (Exception x) {
			JSFExtensionsPlugin.log("", x);
			return false;
		} finally {
			smw.dispose();
		}
	}

}