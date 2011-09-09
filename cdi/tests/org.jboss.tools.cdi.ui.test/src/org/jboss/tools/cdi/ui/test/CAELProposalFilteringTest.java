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
package org.jboss.tools.cdi.ui.test;

import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.jboss.tools.cdi.core.test.tck.TCKTest;
import org.jboss.tools.common.base.test.contentassist.CATestUtil;
import org.jboss.tools.jst.jsp.contentassist.AutoContentAssistantProposal;
import org.jboss.tools.jst.jsp.test.ca.ContentAssistantTestCase;

/**
 * Test case testing http://jira.jboss.com/jira/browse/JBIDE-9633 issue.
 * 
 * @author Victor V. Rubezhny
 *
 */
public class CAELProposalFilteringTest extends TestCase {

	private IProject project = null;
	private ContentAssistantTestCase caTest = new ContentAssistantTestCase();
	private static final String JSP_PAGE_NAME = "WebContent/test.jsp";
	private static final String XHTML_PAGE_NAME = "WebContent/elValidation1.xhtml";
	
	public CAELProposalFilteringTest() {
		super();
	}
	
	public void setUp() {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject(TCKTest.PROJECT_NAME);
		caTest.setProject(project);
	}

	public void testCAELProposalFilteringInJSP () {
		caTest.openEditor(JSP_PAGE_NAME);
		try {
			doCAELProposalFilteringTest(JSP_PAGE_NAME, "rendered=\"#{(game.", "value : String - Game", "value");
			doCAELProposalFilteringTest(JSP_PAGE_NAME, "value=\"#{sheep.", "name : String - Sheep", "name");
		} finally {
			caTest.closeEditor();
		}
	}
	
	public void testCAELProposalFilteringInXHTML () {
		caTest.openEditor(XHTML_PAGE_NAME);
		try {
			doCAELProposalFilteringTest(XHTML_PAGE_NAME, "value=\"#{sheep.", "name : String - Sheep", "name");
		} finally {
			caTest.closeEditor();
		}
	}
	
	@SuppressWarnings("restriction")
	private void doCAELProposalFilteringTest(String pageName, String prefix, String proposalToApply, String compareString) {
		IDocument document = caTest.getDocument();
		String documentContent = document.get();
		int start = (documentContent == null ? -1 : documentContent.indexOf(prefix));
		assertFalse("Required text '" + prefix + "' not found in document", (start == -1));
		int offsetToTest = start + prefix.length();
		
		String documentContentToCompare = documentContent.substring(0, start + prefix.length()) +
			compareString + documentContent.substring(start + prefix.length());
		
		List<ICompletionProposal> res = CATestUtil.collectProposals(caTest.getContentAssistant(), caTest.getViewer(), offsetToTest);

		assertTrue("Content Assistant returned no proposals", (res != null && res.size() > 0));

		boolean bPropoosalToApplyFound = false;
		for (ICompletionProposal p : res) {
			if (!(p instanceof AutoContentAssistantProposal)) 
				continue;
			AutoContentAssistantProposal proposal = (AutoContentAssistantProposal)p;
			String proposalString = proposal.getDisplayString();

			if (proposalToApply.equals(proposalString)) {
				if (!bPropoosalToApplyFound) {
					bPropoosalToApplyFound = true;
					proposal.apply(document);
				} else {
					fail("Duplicate proposal '" + proposalToApply + "' found in CA Proposal List");
				}
			}
		}
		assertTrue("The proposal to apply not found.", bPropoosalToApplyFound);

//		JobUtils.waitForIdle();

		String documentUpdatedContent = document.get();
		assertTrue("The proposal replacement is failed.", documentContentToCompare.equals(documentUpdatedContent));
	}
	
}