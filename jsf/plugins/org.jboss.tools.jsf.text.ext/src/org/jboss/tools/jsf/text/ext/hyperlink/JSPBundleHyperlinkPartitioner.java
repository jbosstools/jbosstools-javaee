/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.jsf.text.ext.hyperlink;

import org.eclipse.jface.text.IDocument;
import org.jboss.tools.common.el.core.model.ELExpression;
import org.jboss.tools.common.el.core.model.ELInvocationExpression;
import org.jboss.tools.common.el.core.resolver.ELContext;
import org.jboss.tools.common.el.core.resolver.ELResolution;
import org.jboss.tools.common.el.core.resolver.ELResolver;
import org.jboss.tools.common.el.core.resolver.ELSegment;
import org.jboss.tools.common.el.core.resolver.MessagePropertyELSegment;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlinkPartitioner;
import org.jboss.tools.common.text.ext.hyperlink.HyperlinkRegion;
import org.jboss.tools.common.text.ext.hyperlink.IHyperlinkPartitionRecognizer;
import org.jboss.tools.common.text.ext.hyperlink.IHyperlinkRegion;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.jsf.text.ext.hyperlink.JSPExprHyperlinkPartitioner.ExpressionStructure;
import org.jboss.tools.jst.text.ext.hyperlink.jsp.JSPRootHyperlinkPartitioner;
import org.jboss.tools.jst.text.ext.util.TaglibManagerWrapper;
import org.w3c.dom.Document;

/**
 * @author Jeremy
 */
public class JSPBundleHyperlinkPartitioner extends AbstractHyperlinkPartitioner implements IHyperlinkPartitionRecognizer {
	public static final String JSP_BUNDLE_PARTITION = "org.jboss.tools.common.text.ext.jsp.JSP_BUNDLE"; //$NON-NLS-1$
	
	protected String getPartitionType() {
		return JSP_BUNDLE_PARTITION;
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
			if (!recognize(document, superRegion)) return null;
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
		
	public static IHyperlinkRegion getRegion(IDocument document, final int offset) {
		ELContext context = JSPExprHyperlinkPartitioner.getELContext(document);
		if(context != null){
			ExpressionStructure eStructure = JSPExprHyperlinkPartitioner.getExpression(context, offset);
			if(eStructure != null){
				ELInvocationExpression invocationExpression = JSPExprHyperlinkPartitioner.getInvocationExpression(eStructure.reference, eStructure.expression, offset);
				if(invocationExpression != null){
					ELSegment segment = decide(context, eStructure.expression, invocationExpression, offset-eStructure.reference.getStartPosition(), offset); 
					if (segment != null) {
						IHyperlinkRegion region = new HyperlinkRegion(eStructure.reference.getStartPosition() + segment.getSourceReference().getStartPosition(), segment.getSourceReference().getLength(), null, null, null);
						return region;
					}
				}
			}
		}
		return null;
	}
	
	private static ELSegment decide(ELContext context, ELExpression expression, ELInvocationExpression invocationExpression, int offset, int globalOffset){
		for(ELResolver resolver : context.getElResolvers()){
			ELResolution resolution = resolver.resolve(context, invocationExpression, globalOffset);
			if(resolution==null) {
				continue;
			}
			ELSegment segment = resolution.findSegmentByOffset(offset);
			if(segment != null && segment.isResolved()){
				if (segment != null && segment.isResolved() && segment instanceof MessagePropertyELSegment) {
					return segment;
				}
			}
		}
		return null;
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

			return (getRegion(document, region.getOffset()) != null);
		} finally {
			smw.dispose();
		}
	}

	/**
	 * @deprecated
	 */
	protected String[] getLoadBundleTagPrefixes(IDocument document, int offset) {
		TaglibManagerWrapper tmw = new TaglibManagerWrapper();
		tmw.init(document, offset);
		if(!tmw.exists()) return null;
		
		return new String[] {tmw.getCorePrefix()};
	}
}