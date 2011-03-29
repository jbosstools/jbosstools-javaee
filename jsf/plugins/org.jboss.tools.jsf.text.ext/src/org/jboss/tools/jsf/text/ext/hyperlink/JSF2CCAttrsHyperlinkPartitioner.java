/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.text.ext.hyperlink;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.text.IDocument;
import org.jboss.tools.common.el.core.model.ELExpression;
import org.jboss.tools.common.el.core.model.ELInvocationExpression;
import org.jboss.tools.common.el.core.resolver.ELContext;
import org.jboss.tools.common.el.core.resolver.ELResolution;
import org.jboss.tools.common.el.core.resolver.ELResolver;
import org.jboss.tools.common.el.core.resolver.ELSegment;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlinkPartitioner;
import org.jboss.tools.common.text.ext.hyperlink.HyperlinkRegion;
import org.jboss.tools.common.text.ext.hyperlink.IHyperlinkPartitionRecognizer;
import org.jboss.tools.common.text.ext.hyperlink.IHyperlinkRegion;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.jsf.text.ext.hyperlink.JSPExprHyperlinkPartitioner.ExpressionStructure;
import org.jboss.tools.jst.text.ext.hyperlink.jsp.JSPRootHyperlinkPartitioner;
import org.w3c.dom.Document;

/**
 * 
 * @author Victor V. Rubezhny
 *
 */
public class JSF2CCAttrsHyperlinkPartitioner extends AbstractHyperlinkPartitioner implements IHyperlinkPartitionRecognizer {
	public static final String JSF2_CC_ATTRIBUTE_PARTITION = "org.jboss.tools.common.text.ext.jsp.JSF2_CC_ATTRIBUTE"; //$NON-NLS-1$
	
	protected String getPartitionType() {
		return JSF2_CC_ATTRIBUTE_PARTITION;
	}

	@Override
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

	@Override
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
					ELSegment segment = decide(context, eStructure.expression, invocationExpression, offset-eStructure.reference.getStartPosition(), offset, getFile(document)); 
					if (segment != null) {
						IHyperlinkRegion region = new HyperlinkRegion(eStructure.reference.getStartPosition() + segment.getSourceReference().getStartPosition(), segment.getSourceReference().getLength(), null, null, null);
						return region;
					}
				}
			}
		}
		return null;
	}

	static String[] vs = {"cc.attrs", "compositeComponent.attrs"};

	public static XModelObject findJSF2CCAttributeXModelObject(String varName, IFile file) {
		XModelObject xModelObject = EclipseResourceUtil.createObjectForResource(file);
		if(xModelObject == null) return null;
		if(!"FileJSF2Component".equals(xModelObject.getModelEntity().getName())) return null;

		IJavaProject javaProject = EclipseResourceUtil.getJavaProject(file.getProject());
		XModelObject is = xModelObject.getChildByPath("Interface");
		if(is != null && javaProject != null) {	
			for (int i = 0; i < vs.length; i++) {
				if (vs[i].equals(varName)) return is;
			}
			XModelObject[] cs = is.getChildren("JSF2ComponentAttribute");

			for (int i = 0; i < cs.length; i++) {
				String name = cs[i].getAttributeValue("name");
				String[] names = {vs[0] + "." + name, vs[1] + "." + name};
				for (String n: names) {
					if (n.equals(varName)) return cs[i];
				}
			}
		}
		return null;
	}

	private static IFile getFile(IDocument document) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		try {
			smw.init(document);
			return smw.getFile();
		} finally {
			smw.dispose();
		}
	}
	
	private static ELSegment decide(ELContext context, ELExpression expression, ELInvocationExpression invocationExpression, int offset, int globalOffset, IFile file){
		ELResolution resolution = getResolution(context, invocationExpression, offset, globalOffset);
		if (resolution == null)
			return null;
		
		ELSegment segment = resolution.findSegmentByOffset(offset);
		if(segment != null && segment.isResolved()){
			// Find text for the part of operand
			StringBuffer sbBuffer = new StringBuffer(); 
			for (ELSegment s : resolution.getSegments()) {
				sbBuffer.append(s.getToken().getText());
				if (s == segment) {
					break;
				}
				sbBuffer.append('.'); // Use default separator for ELs here
			}
			
			String text = sbBuffer.toString();
			if (findJSF2CCAttributeXModelObject(text, file) != null) {
				return segment;
			}
		}
		return null;
	}
	
	static ELResolution getResolution(ELContext context, ELInvocationExpression invocationExpression, int offset, int globalOffset) {
		for(ELResolver resolver : context.getElResolvers()){
			ELResolution resolution = resolver.resolve(context, invocationExpression, globalOffset);
			if(resolution==null) {
				continue;
			}
			ELSegment segment = resolution.findSegmentByOffset(offset);
			if(segment != null && segment.isResolved()){
				return resolution;
			}
		}
		return null;

	}

}
