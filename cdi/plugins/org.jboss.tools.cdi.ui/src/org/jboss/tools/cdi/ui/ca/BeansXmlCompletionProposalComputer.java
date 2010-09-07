package org.jboss.tools.cdi.ui.ca;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.sse.ui.contentassist.CompletionProposalInvocationContext;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.eclipse.wst.xml.ui.internal.editor.XMLEditorPluginImageHelper;
import org.eclipse.wst.xml.ui.internal.editor.XMLEditorPluginImages;
import org.jboss.tools.cdi.internal.core.ca.BeansXmlProcessor;
import org.jboss.tools.common.el.core.resolver.ELContext;
import org.jboss.tools.common.text.TextProposal;
import org.jboss.tools.jst.jsp.contentassist.AutoContentAssistantProposal;
import org.jboss.tools.jst.jsp.contentassist.computers.XmlTagCompletionProposalComputer;
import org.jboss.tools.jst.web.kb.KbQuery;
import org.jboss.tools.jst.web.kb.PageProcessor;
import org.jboss.tools.jst.web.kb.KbQuery.Type;


public class BeansXmlCompletionProposalComputer extends XmlTagCompletionProposalComputer {

	@Override
	protected void addTagInsertionProposals(
			ContentAssistRequest contentAssistRequest, int childPosition,
			CompletionProposalInvocationContext context) {
		// TODO Auto-generated method stub
		System.out.println("addTagInsertionProposals: CDI Beans.xml proposals are to be placed here!");
		
		String prefix = getTagPrefix();
		String uri = getTagUri();
		
		String query = contentAssistRequest.getMatchString();

		if (query == null)
			query = ""; //$NON-NLS-1$
		String stringQuery = query; //$NON-NLS-1$
				
		ELContext elContext = getContext();
		IProject project = elContext == null || elContext.getResource() == null ? null :
			elContext.getResource().getProject();
		
		if (project == null)
			return;
		
		KbQuery kbQuery = createKbQuery(Type.TAG_BODY, query, stringQuery, prefix, uri);
		TextProposal[] proposals = BeansXmlProcessor.getInstance().getProposals(kbQuery, project);
		
		for (int i = 0; proposals != null && i < proposals.length; i++) {
			TextProposal textProposal = proposals[i];
			boolean useAutoActivation = true;
	
			String replacementString = textProposal.getReplacementString();
			String closingTag = textProposal.getLabel();
//			if (closingTag != null && closingTag.startsWith("<")) { //$NON-NLS-1$
//				closingTag = closingTag.substring(1);
//			}
			
//			if (!insertTagOpenningCharacter && replacementString.startsWith("<")) { //$NON-NLS-1$
				// Because the tag starting char is already in the text
//				replacementString = replacementString.substring(1);
//			}
//			if (!replacementString.endsWith("/>")) { //$NON-NLS-1$
//				replacementString += "</" + closingTag + ">"; //$NON-NLS-1$ //$NON-NLS-2$
//				useAutoActivation = false;	// JBIDE-6285: Don't invoke code assist automaticly if user inserts <tag></tag>.
//			}

		
			int replacementOffset = contentAssistRequest.getReplacementBeginPosition();
			int replacementLength = contentAssistRequest.getReplacementLength();
			int cursorPosition = getCursorPositionForProposedText(replacementString);
			Image image = textProposal.getImage();
			if (image == null) {
				image = XMLEditorPluginImageHelper.getInstance().getImage(XMLEditorPluginImages.IMG_OBJ_TAG_GENERIC);
			}

			String displayString = closingTag;
			IContextInformation contextInformation = null;
			String additionalProposalInfo = textProposal.getContextInfo();
			int relevance = textProposal.getRelevance();
			if (relevance == TextProposal.R_NONE) {
//				relevance = defaultRelevance == TextProposal.R_NONE? TextProposal.R_TAG_INSERTION : defaultRelevance;
			}

			AutoContentAssistantProposal proposal = new AutoContentAssistantProposal(useAutoActivation, replacementString, 
					replacementOffset, replacementLength, cursorPosition, image, displayString, 
					contextInformation, additionalProposalInfo, relevance);

			contentAssistRequest.addProposal(proposal);
		}

	}

	@Override
	protected void addAttributeNameProposals(
			ContentAssistRequest contentAssistRequest,
			CompletionProposalInvocationContext context) {
		// No actions required
	}

	@Override
	protected void addAttributeValueProposals(
			ContentAssistRequest contentAssistRequest,
			CompletionProposalInvocationContext context) {
		// No actions required
	}

	@Override
	protected void addTagNameProposals(
			ContentAssistRequest contentAssistRequest, int childPosition,
			CompletionProposalInvocationContext context) {
		// No actions required
	}

	@Override
	protected void addTagNameProposals(
			ContentAssistRequest contentAssistRequest, int childPosition,
			boolean insertTagOpenningCharacter,
			CompletionProposalInvocationContext context) {
		// No actions required
	}

	@Override
	protected void addAttributeValueELProposals(
			ContentAssistRequest contentAssistRequest,
			CompletionProposalInvocationContext context) {
		// No actions required
	}

	@Override
	protected void addTextELProposals(
			ContentAssistRequest contentAssistRequest,
			CompletionProposalInvocationContext context) {
		// No actions required
	}
}

