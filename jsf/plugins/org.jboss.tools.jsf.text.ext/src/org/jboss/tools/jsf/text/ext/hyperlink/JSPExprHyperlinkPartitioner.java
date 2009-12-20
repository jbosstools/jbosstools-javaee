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

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.jboss.tools.common.el.core.ELReference;
import org.jboss.tools.common.el.core.model.ELExpression;
import org.jboss.tools.common.el.core.resolver.ELContext;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlinkPartitioner;
import org.jboss.tools.common.text.ext.hyperlink.HyperlinkRegion;
import org.jboss.tools.common.text.ext.hyperlink.IExclusiblePartitionerRecognition;
import org.jboss.tools.common.text.ext.hyperlink.IHyperlinkPartitionRecognizer;
import org.jboss.tools.common.text.ext.hyperlink.IHyperlinkRegion;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.jst.text.ext.hyperlink.jsp.JSPRootHyperlinkPartitioner;
import org.jboss.tools.jst.web.kb.PageContextFactory;

/**
 * @author Jeremy and Daniel
 */
@SuppressWarnings("restriction")
public class JSPExprHyperlinkPartitioner extends AbstractHyperlinkPartitioner implements IHyperlinkPartitionRecognizer, IExclusiblePartitionerRecognition {
	public static final String JSP_EXPRESSION_PARTITION = "org.jboss.tools.common.text.ext.jsp.JSP_EXPRESSION"; //$NON-NLS-1$

	protected String getPartitionType() {
		return JSP_EXPRESSION_PARTITION;
	}
	
	/**
	 * @see com.ibm.sse.editor.hyperlink.AbstractHyperlinkPartitioner#parse(org.eclipse.jface.text.IDocument, com.ibm.sse.editor.extensions.hyperlink.IHyperlinkRegion)
	 */
	protected IHyperlinkRegion parse(IDocument document, IHyperlinkRegion superRegion) {
		IHyperlinkRegion r = getRegion(document, superRegion.getOffset());
		if (r == null) return null;

		String axis = getAxis(document, superRegion);
		String contentType = superRegion.getContentType();
		String type = getPartitionType();
		int length = r.getLength() - (superRegion.getOffset() - r.getOffset());
		int offset = superRegion.getOffset();
		
		IHyperlinkRegion region = new HyperlinkRegion(offset, length, axis, contentType, type);
		return region;
	}
	
	protected String getAxis(IDocument document, IHyperlinkRegion superRegion) {
		if (superRegion.getAxis() == null || superRegion.getAxis().length() == 0) {
			return JSPRootHyperlinkPartitioner.computeAxis(document, superRegion.getOffset()) + "/"; //$NON-NLS-1$
		}		
		return superRegion.getAxis();
	}
	
	private IHyperlinkRegion getRegion(IDocument document, final int offset) {
		ELExpression expression = getExpression(document, offset);
		if(expression != null){
			IHyperlinkRegion region = new HyperlinkRegion(expression.getStartPosition(), expression.getLength(), null, null, null);
			return region;
		}
		return null;
	}
	
	public static ELExpression getExpression(IDocument document, final int offset){
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(document);
		try {
			IFile file = smw.getFile();
			ELContext context = PageContextFactory.getInstance().createPageContext(file);
			if(context != null){
				
				ELReference[] references = context.getELReferences();
				
				for(ELReference reference : references){
					for(ELExpression expression : reference.getEl()){
						if (reference.getStartPosition()+expression.getStartPosition() <= offset && reference.getStartPosition()+expression.getEndPosition() >= offset)
							return expression;
					}
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
		
		return (getRegion(document, region.getOffset()) != null);
	}

	public boolean excludes(String partitionType, IDocument document, IHyperlinkRegion superRegion) {
		return false;
	}

	public String getExclusionPartitionType() {
		return getPartitionType();
	}

}
