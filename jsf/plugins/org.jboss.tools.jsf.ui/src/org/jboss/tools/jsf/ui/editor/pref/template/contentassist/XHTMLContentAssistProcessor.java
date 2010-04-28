/*******************************************************************************
 * Copyright (c) 2007-2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.ui.editor.pref.template.contentassist;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.jboss.tools.jsf.ui.JsfUiPlugin;
import org.jboss.tools.jsf.ui.editor.pref.template.TemplateContextTypeIdsXHTML;
import org.w3c.dom.Node;

/**
 * XHTML Contetn assist processor, processor which add's templates
 * proposals to CA
 * 
 * @author mareshkau
 * 
 */
public class XHTMLContentAssistProcessor implements IContentAssistProcessor,
		IPropertyChangeListener {

	protected IPreferenceStore fPreferenceStore = null;
	protected boolean isXHTML = false;
	private XHTMLTemplateCompletionProcessor fTemplateProcessor = null;

	public XHTMLContentAssistProcessor() {

		super();
	}

	/**
	 * Add the proposals for a completely empty document
	 */
	// protected void addEmptyDocumentProposals(List<ICompletionProposal>
	// contentAssistRequest) {
	// addTemplates(contentAssistRequest, TemplateContextTypeIdsXHTML.NEW);
	// }

	// protected void addStartDocumentProposals(List<ICompletionProposal>
	// contentAssistRequest) {
	// if (isXHTML)
	// addEmptyDocumentProposals(contentAssistRequest);
	// }

	// protected void addTagInsertionProposals(List<ICompletionProposal>
	// contentAssistRequest, int childPosition) {
	// addTemplates(contentAssistRequest, TemplateContextTypeIdsXHTML.TAG);
	// }

	/**
	 * Adds templates to the list of proposals
	 * 
	 * @param contentAssistRequest
	 * @param context
	 */
	// private void addTemplates(List<ICompletionProposal> contentAssistRequest,
	// String context) {
	// addTemplates(contentAssistRequest, context,
	// contentAssistRequest.getReplacementBeginPosition());
	// }

	/**
	 * Adds templates to the list of proposals
	 * 
	 * @param contentAssistRequest
	 * @param context
	 * @param startOffset
	 */
	private void addTemplates(ITextViewer fTextViewer,
			List<ICompletionProposal> contentAssistRequest,
			List<String> fTemplateContexts, int startOffset) {
		if (contentAssistRequest == null)
			return;

		// if already adding template proposals for a certain context type, do
		// not add again
		if (getTemplateCompletionProcessor() != null) {
			for (String context : fTemplateContexts) {
				getTemplateCompletionProcessor().setContextType(context);
				ICompletionProposal[] proposals = getTemplateCompletionProcessor()
						.computeCompletionProposals(fTextViewer, startOffset);
				for (int i = 0; i < proposals.length; ++i) {
					contentAssistRequest.add(proposals[i]);
				}
			}
		}
	}

	protected boolean beginsWith(String aString, String prefix) {
		if (aString == null || prefix == null || prefix.length() == 0)
			return true;
		int minimumLength = Math.min(prefix.length(), aString.length());
		String beginning = aString.substring(0, minimumLength);
		return beginning.equalsIgnoreCase(prefix);
	}

	// protected List<ICompletionProposal>
	// computeCompletionProposals(ITextViewer textViewer, int documentPosition)
	// {
	// List<ICompletionProposal> request=new ArrayList<ICompletionProposal>();
	// addTemplates(textViewer,request, TemplateContextTypeIdsXHTML.ALL,
	// documentPosition);
	// return request;
	// }

	/**
	 * Return a list of proposed code completions based on the specified
	 * location within the document that corresponds to the current cursor
	 * position within the text-editor control.
	 * 
	 * @param documentPosition
	 *            a location within the document
	 * @return an array of code-assist items
	 */
	public ICompletionProposal[] computeCompletionProposals(
			ITextViewer textViewer, int documentPosition) {
		List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();
		List<String> fContextTypes = new ArrayList<String>();
		//TODO Maksim Areshkau, analize and position here
		fContextTypes.add(TemplateContextTypeIdsXHTML.ALL);
		fContextTypes.add(TemplateContextTypeIdsXHTML.TAG);
		fContextTypes.add(TemplateContextTypeIdsXHTML.NEW);
		fContextTypes.add(TemplateContextTypeIdsXHTML.ATTRIBUTE);
		fContextTypes.add(TemplateContextTypeIdsXHTML.ATTRIBUTE_VALUE);
  		addTemplates(textViewer, result, fContextTypes,
				documentPosition);
		return result.toArray(new ICompletionProposal[0]);
	}

	protected String getEmptyTagCloseString() {
		if (isXHTML)
			return " />"; //$NON-NLS-1$
		return ">"; //$NON-NLS-1$
	}

	private XHTMLTemplateCompletionProcessor getTemplateCompletionProcessor() {
		if (this.fTemplateProcessor == null) {
			this.fTemplateProcessor = new XHTMLTemplateCompletionProcessor();
		}
		return this.fTemplateProcessor;
	}

	/**
	 * Determine if this Document is an XHTML Document. Oprates solely off of
	 * the Document Type declaration
	 */
	protected boolean getXHTML(Node node) {
		// TODO Maksim Areshkau, implement it
		return true;
	}

	protected void init() {
		getPreferenceStore().addPropertyChangeListener(this);
	}

	public void release() {
		getPreferenceStore().removePropertyChangeListener(this);
	}

	protected boolean stringsEqual(String a, String b) {
		return a.equalsIgnoreCase(b);
	}

	public void propertyChange(PropertyChangeEvent event) {
		String property = event.getProperty();
	}

	protected IPreferenceStore getPreferenceStore() {
		if (this.fPreferenceStore == null)
			this.fPreferenceStore = JsfUiPlugin.getDefault().getPreferenceStore();

		return this.fPreferenceStore;
	}

	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int documentPosition, IndexedRegion indexedNode, ITextRegion region) {
		return computeCompletionProposals(viewer, documentPosition);
	}

	public IContextInformation[] computeContextInformation(ITextViewer viewer,
			int offset) {

		return new IContextInformation[0];
	}

	public char[] getCompletionProposalAutoActivationCharacters() {
		return new char[0];
	}

	public char[] getContextInformationAutoActivationCharacters() {
		return new char[0];
	}

	public IContextInformationValidator getContextInformationValidator() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}

}
