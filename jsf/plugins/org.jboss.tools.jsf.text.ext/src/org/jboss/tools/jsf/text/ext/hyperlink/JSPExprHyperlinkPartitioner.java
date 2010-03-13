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
import org.jboss.tools.common.el.core.model.ELInvocationExpression;
import org.jboss.tools.common.el.core.resolver.ELContext;
import org.jboss.tools.common.el.core.resolver.ELResolution;
import org.jboss.tools.common.el.core.resolver.ELResolver;
import org.jboss.tools.common.el.core.resolver.ELSegment;
import org.jboss.tools.common.el.core.resolver.JavaMemberELSegment;
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
	public static final String EXPRESSION_PARTITION = "org.jboss.tools.common.text.ext.jsp.EXPRESSION"; //$NON-NLS-1$
	
	private boolean jspExpression = false;

	protected String getPartitionType() {
		if(jspExpression)
			return EXPRESSION_PARTITION;
		else
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
		jspExpression = false;
		ELContext context = getELContext(document);
		if(context != null){
			ExpressionStructure eStructure = getExpression(context, offset);
			if(eStructure != null){
				ELInvocationExpression invocationExpression = getInvocationExpression(eStructure.reference, eStructure.expression, offset);
				if(invocationExpression != null){
					jspExpression = decide(context, eStructure.expression, invocationExpression, offset-eStructure.reference.getStartPosition());
					if(jspExpression){
						IHyperlinkRegion region = new HyperlinkRegion(invocationExpression.getStartPosition(), invocationExpression.getLength(), null, null, null);
						return region;
					}
				}
				IHyperlinkRegion region = new HyperlinkRegion(eStructure.expression.getStartPosition(), eStructure.expression.getLength(), null, null, null);
				return region;
			}
		}
		return null;
	}
	
	public static ExpressionStructure getExpression(ELContext context, final int offset){
		ELReference[] references = context.getELReferences();
		
		for(ELReference reference : references){
			for(ELExpression expression : reference.getEl()){
				if (reference.getStartPosition()+expression.getStartPosition() <= offset && reference.getStartPosition()+expression.getEndPosition() > offset)
					return new ExpressionStructure(reference, expression);
			}
		}
		return null;
	}
	
	public static ELContext getELContext(IDocument document){
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(document);
		try {
			IFile file = smw.getFile();
			ELContext context = PageContextFactory.getInstance().createPageContext(file);
			return context;
		} finally {
			smw.dispose();
		}
	}
	
	public static ELInvocationExpression getInvocationExpression(ELReference reference, ELExpression expression, int offset){
		if(expression == null || reference == null)
			return null;
		
		for(ELInvocationExpression ie : expression.getInvocations()){
			if (reference.getStartPosition()+ie.getStartPosition() <= offset && reference.getStartPosition()+ie.getEndPosition() > offset) {
				return ie;
			}
		}
		return null;
	}
	
	public boolean decide(ELContext context, ELExpression expression, ELInvocationExpression invocationExpression, int offset){
		for(ELResolver resolver : context.getElResolvers()){
			ELResolution resolution = resolver.resolve(context, invocationExpression, invocationExpression.getStartPosition());
			if(resolution==null) {
				return false;
			}
			ELSegment segment = resolution.findSegmentByOffset(offset);
			if(segment != null){
				if(segment instanceof JavaMemberELSegment){
					JavaMemberELSegment javaSegment = (JavaMemberELSegment)segment;
					if(javaSegment.getJavaElement() != null){
						return true;
					}
				}
			}
		}
		return false;
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
	
	public static class ExpressionStructure{
		public ELReference reference;
		public ELExpression expression;
		
		public ExpressionStructure(ELReference reference, ELExpression expression){
			this.reference = reference;
			this.expression = expression;
		}
		
	}

}
