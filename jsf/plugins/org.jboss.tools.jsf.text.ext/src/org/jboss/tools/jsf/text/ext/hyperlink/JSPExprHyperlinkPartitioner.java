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

import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMText;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlinkPartitioner;
import org.jboss.tools.common.text.ext.hyperlink.HyperlinkRegion;
import org.jboss.tools.common.text.ext.hyperlink.IExclusiblePartitionerRecognition;
import org.jboss.tools.common.text.ext.hyperlink.IHyperlinkPartitionRecognizer;
import org.jboss.tools.common.text.ext.hyperlink.IHyperlinkRegion;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.common.text.ext.hyperlink.jsp.JSPRootHyperlinkPartitioner;

/**
 * @author Jeremy
 */
public class JSPExprHyperlinkPartitioner extends AbstractHyperlinkPartitioner implements IHyperlinkPartitionRecognizer, IExclusiblePartitionerRecognition {
	public static final String JSP_EXPRESSION_PARTITION = "org.jboss.tools.common.text.ext.jsp.JSP_EXPRESSION"; //$NON-NLS-1$

	protected String getPartitionType() {
		return JSP_EXPRESSION_PARTITION;
	}
	
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
			String type = getPartitionType();
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
	

	private IHyperlinkRegion getRegion(IDocument document, final int offset) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(document);
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			Node n = Utils.findNodeForOffset(xmlDocument, offset);

			if (n == null || !(n instanceof IDOMAttr || n instanceof IDOMText)) return null;

			int valStart = Utils.getValueStart(n);
			int valEnd = Utils.getValueEnd(n);
			if(valStart < 0 || valStart > offset) return null;

			String valText = (n instanceof IDOMAttr)? 
							((IDOMAttr)n).getValueRegionText():
								((IDOMText)n).getData();
			if (valText == null) 
					return null;
			
			int startBracket = 0;
			int exprStart = 0;
			int exprLength = 0;
			while (startBracket != -1) {
				int v = valText.indexOf("#{", startBracket + exprLength); //$NON-NLS-1$
				if (v == -1) v = valText.indexOf("${", startBracket + exprLength); //$NON-NLS-1$
				if (v == -1) return null;
				startBracket = v;

				int endBracket = valText.indexOf("}", startBracket + 2); //$NON-NLS-1$
				exprStart = valStart + startBracket + 2;
				int exprEnd = (endBracket == -1 ? valEnd - 1: valStart + endBracket);
				int lineBreaker = valText.indexOf('\n', startBracket + 2);
				int lineBreaker1 = valText.indexOf('\r', startBracket + 2);
				if (lineBreaker != -1 && lineBreaker + valStart < exprEnd) exprEnd = valStart + lineBreaker;
				if (lineBreaker1 != -1 && lineBreaker1 + valStart < exprEnd) exprEnd =  valStart + lineBreaker1;
				exprLength = exprEnd - exprStart;
				
				if(exprLength==0) { 
					return null;
				} else if (exprStart <= offset && exprEnd >= offset) {
					int start = exprStart;
					int length = exprLength;
	
					IHyperlinkRegion region = new HyperlinkRegion(start, length, null, null, null);
					return region;
				}
			}
			return null;
		} finally {
			smw.dispose();
		}
		
	}


	/**
	 * @see com.ibm.sse.editor.extensions.hyperlink.IHyperlinkPartitionRecognizer#recognize(org.eclipse.jface.text.IDocument, com.ibm.sse.editor.extensions.hyperlink.IHyperlinkRegion)
	 */
	public boolean recognize(IDocument document, IHyperlinkRegion region) {
		if(document == null || region == null) return false;
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(document);
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return false;
			
			Utils.findNodeForOffset(xmlDocument, region.getOffset());

			return (getRegion(document, region.getOffset()) != null);
		} finally {
			smw.dispose();
		}
	}

	public boolean excludes(String partitionType, IDocument document, IHyperlinkRegion superRegion) {
		return false;
	}

	public String getExclusionPartitionType() {
		return getPartitionType();
	}

}
