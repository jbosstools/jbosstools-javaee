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
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.jboss.tools.common.el.core.model.ELInvocationExpression;
import org.jboss.tools.common.el.core.resolver.ELContext;
import org.jboss.tools.common.el.core.resolver.ELResolution;
import org.jboss.tools.common.el.core.resolver.ELSegment;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.PositionHolder;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlink;
import org.jboss.tools.common.text.ext.util.StructuredSelectionHelper;
import org.jboss.tools.jsf.text.ext.JSFTextExtMessages;
import org.jboss.tools.jsf.text.ext.hyperlink.JSPExprHyperlinkPartitioner.ExpressionStructure;

/**
 * 
 * @author Victor V. Rubezhny
 *
 */
public class JSF2CCAttrsHyperlink extends AbstractHyperlink {
	IRegion fLastRegion = null;

	@Override
	protected IRegion doGetHyperlinkRegion(int offset) {
		fLastRegion = JSF2CCAttrsHyperlinkPartitioner.getRegion(getDocument(), offset);
		return fLastRegion;
	}
	
	@Override
	protected void doHyperlink(IRegion region) {
		XModelObject attrObject = null;
		IRegion attrRegion = null;
		
		ELContext context = JSPExprHyperlinkPartitioner.getELContext(getDocument());
		if(context != null){
			ExpressionStructure eStructure = JSPExprHyperlinkPartitioner.getExpression(context, getOffset());
			if(eStructure != null){
				ELInvocationExpression invocationExpression = JSPExprHyperlinkPartitioner.getInvocationExpression(eStructure.reference, eStructure.expression, getOffset());
				if(invocationExpression != null){
					
					ELResolution resolution = JSF2CCAttrsHyperlinkPartitioner.getResolution(context, invocationExpression, getOffset()-eStructure.reference.getStartPosition(), getOffset());
					if (resolution == null)
						return;
					
					ELSegment segment = resolution.findSegmentByOffset(getOffset()-eStructure.reference.getStartPosition());
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

						attrObject = JSF2CCAttrsHyperlinkPartitioner.findJSF2CCAttributeXModelObject(text, getFile());
						if (attrObject != null) {
							PositionHolder h = PositionHolder.getPosition(attrObject, null);
							h.update();
							if (h.getStart() == -1 || h.getEnd() == -1) {
								openFileFailed();
								return;
							}
							attrRegion = new Region(h.getStart(), h.getEnd() - h.getStart());
						}
					}
				}
			}
		}
		if (attrObject != null && attrRegion != null) {
			IFile file = (IFile)attrObject.getAdapter(IFile.class);
			if (file != null) {
				if (openFileInEditor(file) != null) {
					StructuredSelectionHelper.setSelectionAndRevealInActiveEditor(attrRegion);
					return;
				}
			}
		}

		openFileFailed();
	}

	@Override
	public String getHyperlinkText() {
		return JSFTextExtMessages.OpenJsf2CCAttribute;
	}

}
