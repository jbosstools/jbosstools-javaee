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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.ui.IEditorPart;
import org.jboss.tools.common.el.core.model.ELExpression;
import org.jboss.tools.common.el.core.model.ELInvocationExpression;
import org.jboss.tools.common.el.core.resolver.ELContext;
import org.jboss.tools.common.el.core.resolver.ELResolution;
import org.jboss.tools.common.el.core.resolver.ELResolver;
import org.jboss.tools.common.el.core.resolver.ELSegment;
import org.jboss.tools.common.el.core.resolver.JavaMemberELSegment;
import org.jboss.tools.common.text.ext.ExtensionsPlugin;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlink;
import org.jboss.tools.jsf.text.ext.JSFExtensionsPlugin;
import org.jboss.tools.jst.web.kb.PageContextFactory;

/**
 * @author Daniel
 */
public class JSPExprHyperlink extends AbstractHyperlink{
	private String hyperlinkText = ""; //$NON-NLS-1$
	private ELExpression expression;
	ELInvocationExpression invocationExpression;
	
	protected IRegion doGetHyperlinkRegion(int offset) {
		expression = JSPExprHyperlinkPartitioner.getExpression(getDocument(), offset);
		invocationExpression = getInvocationExpression(expression, offset);
		if(invocationExpression != null){
			Region region = new Region(invocationExpression.getStartPosition(), invocationExpression.getLength());
			return region;
		}
		return null;
	}
	
	private ELInvocationExpression getInvocationExpression(ELExpression expression, int offset){
		if(expression == null)
			return null;
		
		for(ELInvocationExpression ie : expression.getInvocations()){
			if (expression.getStartPosition()+ie.getStartPosition() <= offset && expression.getStartPosition()+ie.getEndPosition() >= offset) {
				return ie;
			}
		}
		return null;
	}

	protected void doHyperlink(IRegion region) {
		IEditorPart part = null;
		if (region == null)
			return;

		try {
			IDocument document = getDocument();
			hyperlinkText = document
					.get(region.getOffset(), region.getLength());
		} catch (BadLocationException ex) {
			ExtensionsPlugin.getPluginLog().logError(ex);
		}
		if(invocationExpression != null){
			ELContext context = PageContextFactory.getInstance().createPageContext(getFile());
			if(context != null){
				for(ELResolver resolver : context.getElResolvers()){
					ELResolution resolution = resolver.resolve(context, expression, invocationExpression.getStartPosition());
					ELSegment segment = resolution.findSegmentByOffset(invocationExpression.getStartPosition());
					if(segment != null){
						if(segment instanceof JavaMemberELSegment){
							JavaMemberELSegment javaSegment = (JavaMemberELSegment)segment;
							if(javaSegment.getJavaElement() != null){
								try{
									IResource resource = javaSegment.getJavaElement().getCorrespondingResource();
									if(resource != null && resource instanceof IFile)
										part = openFileInEditor((IFile)javaSegment.getJavaElement().getCorrespondingResource());
								}catch(JavaModelException ex){
									JSFExtensionsPlugin.log(ex);
								}
							}
						}
					}
				}
			}
		}
		if (part == null)
			openFileFailed();
	}

	public String getHyperlinkText() {
		return hyperlinkText;
	}

}
