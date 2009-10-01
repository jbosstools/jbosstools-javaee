/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.seam.ui.views.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.jboss.tools.common.el.core.model.ELInstance;
import org.jboss.tools.common.el.core.model.ELInvocationExpression;
import org.jboss.tools.common.el.core.model.ELModel;
import org.jboss.tools.common.el.core.model.ELUtil;
import org.jboss.tools.common.el.core.resolver.SimpleELContext;
import org.jboss.tools.common.meta.XAttribute;
import org.jboss.tools.common.meta.action.XEntityData;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.ui.attribute.AttributeContentProposalProviderFactory;
import org.jboss.tools.common.model.ui.attribute.IAttributeContentProposalProvider;
import org.jboss.tools.common.text.TextProposal;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.el.SeamELCompletionEngine;

/**
 * @author Viacheslav Kabanovich
 */
public class SeamELAttributeContentProposalProvider implements
		IAttributeContentProposalProvider {
	XModelObject object;
	XAttribute attribute;
	IFile file;
	SeamELCompletionEngine engine;

	public boolean isRelevant(XModelObject object, XAttribute attribute) {
		if(attribute == null) {
			return false;
		}
		String module = attribute.getModelEntity().getModule();
		if(module == null || !module.startsWith("Seam")) {
			return false;
		}
		String entity = attribute.getModelEntity().getName();
		if(entity.startsWith("File")) {
			return false;
		}
		return true;
	}

	public IContentProposalProvider getContentProposalProvider() {
		if(file == null || engine == null) return null;
		return new ContentProposalProvider();
	}

	public int getProposalAcceptanceStyle() {
		return ContentProposalAdapter.PROPOSAL_INSERT;
	}

	public void init(XModelObject object, XEntityData data, XAttribute attribute) {
		this.object = object;
		this.attribute = attribute;
		while(object != null && object.getFileType() != XModelObject.FILE) object = object.getParent();
		if(object != null) {
			IResource r = (IResource)object.getAdapter(IResource.class);
			if(r instanceof IFile) {
				file = (IFile)r;
			}
		}
		if(file != null) {
			IProject project = file.getProject();
			ISeamProject seamProject = SeamCorePlugin.getSeamProject(project, true);
			if(seamProject != null) {
				engine = new SeamELCompletionEngine();
			}
		}

	}

	static IContentProposal[] EMPTY = new IContentProposal[0];

	class ContentProposalProvider implements IContentProposalProvider {

		public IContentProposal[] getProposals(String contents, int position) {
			ELModel model = engine.getParserFactory().createParser().parse(contents);
			ELInstance is = ELUtil.findInstance(model, position);
			if(is == null) {
				return EMPTY;
			}
			
			String prefix = getPrefix(contents, position, 0, contents.length());
			if(prefix == null || prefix.length() == 0) {
				if(!isExpressionAllowed(contents, position)) {
					return EMPTY;
				}
			}

			if(prefix == null) prefix = "";
			
			List<TextProposal> suggestions = null;
			SimpleELContext context = new SimpleELContext();
			context.setResource(file);
			suggestions = engine.getProposals(context, prefix);
			if(suggestions == null) {
				return EMPTY;
			}
			List<TextProposal> uniqueSuggestions = engine.makeKbUnique(suggestions);
			
			List<IContentProposal> list = new ArrayList<IContentProposal>();
			for (TextProposal p: uniqueSuggestions) {
				String label = prefix + p.getReplacementString();
				IContentProposal cp = AttributeContentProposalProviderFactory.makeContentProposal(p.getReplacementString(), label, null);
				list.add(cp);
			}
			return list.toArray(new IContentProposal[0]);
		}
		
	}

	public String getPrefix(String content, int offset, int start, int end) throws StringIndexOutOfBoundsException {
		if (content == null || offset > content.length())
			return null;
		ELInvocationExpression expr = engine.findExpressionAtOffset(content, offset, start, end);
		if (expr == null)
			return null;
		return content.substring(expr.getStartPosition(), offset);
	}

	private boolean isExpressionAllowed(String contents, int position) {
		String el = contents.substring(0, position) + "a";
		ELModel model1 = engine.getParserFactory().createParser().parse(el);
		ELInvocationExpression expr = ELUtil.findExpression(model1, el.length());
		return (expr != null);		
	}

	public void dispose() {
		object = null;
		attribute = null;
		file = null;
		engine = null;
	}

	public LabelProvider getCustomLabelProbider() {
		return null;
	}

}
