/*******************************************************************************
 * Copyright (c) 2009 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.jsf.text.ext.hyperlink;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.jboss.tools.common.el.core.model.ELInvocationExpression;
import org.jboss.tools.common.el.core.resolver.ELContext;
import org.jboss.tools.common.el.core.resolver.ELResolution;
import org.jboss.tools.common.el.core.resolver.ELResolver;
import org.jboss.tools.common.el.core.resolver.ELSegment;
import org.jboss.tools.common.el.core.resolver.JavaMemberELSegment;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlink;
import org.jboss.tools.jsf.text.ext.JSFExtensionsPlugin;
import org.jboss.tools.jsf.text.ext.JSFTextExtMessages;
import org.jboss.tools.jsf.text.ext.hyperlink.JSPExprHyperlinkPartitioner.ExpressionStructure;

/**
 * @author Daniel
 */
public class ExpressionHyperlink extends AbstractHyperlink{
	private JavaMemberELSegment javaSegment=null;
	
	protected IRegion doGetHyperlinkRegion(int offset) {
		javaSegment = null;
		ELContext context = JSPExprHyperlinkPartitioner.getELContext(getDocument());
		ExpressionStructure eStructure = JSPExprHyperlinkPartitioner.getExpression(context, offset);
		ELInvocationExpression invocationExpression = JSPExprHyperlinkPartitioner.getInvocationExpression(eStructure.reference, eStructure.expression, offset);
		if(invocationExpression != null){
			for(ELResolver resolver : context.getElResolvers()){
				ELResolution resolution = resolver.resolve(context, invocationExpression, invocationExpression.getStartPosition());
				if(resolution==null) {
					return null;
				}
				ELSegment segment = resolution.findSegmentByOffset(offset-eStructure.reference.getStartPosition());
				if(segment != null){
					if(segment instanceof JavaMemberELSegment){
						javaSegment = (JavaMemberELSegment)segment;
						if(javaSegment.getJavaElement() != null){
							Region region = new Region(eStructure.reference.getStartPosition()+segment.getSourceReference().getStartPosition(), segment.getSourceReference().getLength());
							
							return region;
						}
					}
				}
			}
		}
		return null;
	}
	
	protected void doHyperlink(IRegion region) {
		IEditorPart part = null;
		
		if(javaSegment != null){
			if (javaSegment.getJavaElement() != null){
				try{
					part = JavaUI.openInEditor(javaSegment.getJavaElement());
				}catch(JavaModelException ex){
					JSFExtensionsPlugin.log(ex);
				}catch(PartInitException ex){
					JSFExtensionsPlugin.log(ex);
				}
				if (part != null) {
					JavaUI.revealInEditor(part, javaSegment.getJavaElement());
				} 
			}
		}
		if (part == null)
			openFileFailed();
	}

	public String getHyperlinkText() {
		return JSFTextExtMessages.OpenJavaElement;
	}

}
