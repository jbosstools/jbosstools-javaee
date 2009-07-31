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

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlink;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlinkPartitioner;
import org.jboss.tools.common.text.ext.hyperlink.HyperlinkRegion;
import org.jboss.tools.common.text.ext.hyperlink.IHyperlinkPartitionRecognizer;
import org.jboss.tools.common.text.ext.hyperlink.IHyperlinkRegion;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.common.text.ext.hyperlink.jsp.JSPRootHyperlinkPartitioner;
import org.jboss.tools.jsf.text.ext.JSFExtensionsPlugin;
import org.jboss.tools.jst.web.project.list.WebPromptingProvider;

/**
 * @author Jeremy
 */
public class JSPBeanHyperlinkPartitioner extends AbstractHyperlinkPartitioner implements IHyperlinkPartitionRecognizer {
	public static final String JSP_BEAN_PARTITION = "org.jboss.tools.common.text.ext.jsp.JSP_BEAN"; //$NON-NLS-1$

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
			if (!recognize(document, superRegion)) return null;
			IHyperlinkRegion r = getRegion(document, superRegion.getOffset());
			if (r == null) return null;
			r = getWordRegion(document, superRegion.getOffset());
			if (r == null) return null;

			String axis = getAxis(document, superRegion);
			String contentType = superRegion.getContentType();
			String type = JSP_BEAN_PARTITION;
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
	
	public static IHyperlinkRegion getWordRegion (IDocument document, final int offset) {
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
				if (!Character.isJavaIdentifierPart(sb.charAt(bStart))) {
					bStart++;
					break;
				}
			
				if (bStart == 0) break;
				bStart--;
			}
			// find end of bean property
			int bEnd = offset - start;
			while (bEnd < sb.length()) { 
				if (!Character.isJavaIdentifierPart(sb.charAt(bEnd)))
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

	public static IHyperlinkRegion getRegionPart(IDocument document, final int offset) {
		if(document == null) return null;
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(document);
		try {

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
						sb.charAt(bStart) != '.' && sb.charAt(bStart) != '[' && sb.charAt(bStart) != ']') {
					bStart++;
					break;
				}
			
				if (bStart == 0) break;
				bStart--;
			}
			// find end of bean property
			int bEnd = offset - start;
			while (bEnd < sb.length()) { 
				if (!Character.isJavaIdentifierPart(sb.charAt(bEnd)))
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
			if(start < 0 || end < start || start > offset) return null;

			String attrText = document.get(start, end - start);

			StringBuffer sb = new StringBuffer(attrText);
			//find start of bean property
			int bStart = offset - start;
			while (bStart >= 0) { 
				if (!Character.isJavaIdentifierPart(sb.charAt(bStart)) &&
						sb.charAt(bStart) != '.' && sb.charAt(bStart) != '[' && sb.charAt(bStart) != ']') {
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
						sb.charAt(bEnd) != '.' && sb.charAt(bEnd) != '[' && sb.charAt(bEnd) != ']')
					break;
				bEnd++;
			}
			
			int propStart = bStart + start;
			int propLength = bEnd - bStart;
			
			if (propStart > offset || propStart + propLength < offset) return null;
			
			IHyperlinkRegion region = new HyperlinkRegion(propStart, propLength);
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
	public boolean recognize(IDocument document, IHyperlinkRegion region) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(document);
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return false;
			
			Utils.findNodeForOffset(xmlDocument, region.getOffset());

			IHyperlinkRegion r = getRegion(document, region.getOffset());
			if (r == null) return false;

			
			String propText = document.get(r.getOffset(), r.getLength());
						
			WebPromptingProvider provider = WebPromptingProvider.getInstance();

			IFile file = smw.getFile();
			XModel xModel = AbstractHyperlink.getXModel(file);
			if (xModel == null) return false;
			
			List beanList = provider.getList(xModel, WebPromptingProvider.JSF_MANAGED_BEANS, "", null); //$NON-NLS-1$
			String beanName = null;
			for (int i = 0; beanList != null && i < beanList.size(); i++) {
				String bn = (String)beanList.get(i);
				if (propText.trim().equals(bn) ||
						propText.trim().startsWith(bn + ".")) { //$NON-NLS-1$
					beanName = bn;
					break;
				}
			}
			
			if (beanName == null) return false;
			
			return true;
		} catch (BadLocationException x) {
			JSFExtensionsPlugin.log("", x); //$NON-NLS-1$
			return false;
		} finally {
			smw.dispose();
		}
	}
	
}
