/*******************************************************************************
 * Copyright (c) 2012-2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.jsp.ca.test;

import java.util.List;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.jboss.tools.common.base.test.contentassist.CATestUtil;
import org.jboss.tools.jst.web.ui.internal.editor.contentassist.AutoContentAssistantProposal;
import org.jboss.tools.jst.jsp.test.ca.ContentAssistantTestCase;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ProjectImportTestSetup;

/**
 * The JUnit test case for issue JBIDE-12177
 * 
 * @author Victor V. Rubezhny
 *
 */
public class CAJsfPredictiveTagNameProposalsTest extends ContentAssistantTestCase {
	private static final String PROJECT_NAME = "JSF2KickStartWithoutLibs";
	private static final String PAGE_NAME = "WebContent/pages/inputname.xhtml";

	private static final String INSERT_BEFORE = "</ui:composition>";
	private static final String INSERTION = "<define";
	private static final String PROPOSAL_TO_APPLY = "<ui:define";
	
	public void setUp() throws Exception {
		System.out.println("CAJsfPredictiveTagNameProposalsTest.setUp");
		project = ProjectImportTestSetup.loadProject(PROJECT_NAME);
	}

	public void testJsfPredictiveTagNameProposals() {
		System.out.println("CAJsfPredictiveTagNameProposalsTest.testJsfPredictiveTagNameProposals");
		assertNotNull("Test project '" + PROJECT_NAME + "' is not prepared", project);
		
		openEditor(PAGE_NAME);
		try {
			// Find start of <ui:composition> tag
			String documentContent = document.get();

			int start = (documentContent == null ? -1 : documentContent.indexOf(INSERT_BEFORE));
			assertFalse("Required text '" + INSERT_BEFORE + "' not found in document", (start == -1));

			String newDocumentContent = documentContent.substring(0, start) + 
					INSERTION + ' ' + documentContent.substring(start);

			int offsetToTest = start + INSERTION.length();

			// Update the document with a new text
			document.set(newDocumentContent);
			System.out.println("CAJsfPredictiveTagNameProposalsTest.60_JobUtils.waitForIdle()");
			JobUtils.waitForIdle();
			
			List<ICompletionProposal> res = CATestUtil.collectProposals(contentAssistant, viewer, offsetToTest);

			assertTrue("Content Assistant returned no proposals", (res != null && res.size() > 0));

			String replacementString = null;
			boolean bPropoosalToApplyFound = false;
			for (ICompletionProposal p : res) {
				if (!(p instanceof AutoContentAssistantProposal)) 
					continue;
				AutoContentAssistantProposal proposal = (AutoContentAssistantProposal)p;
				String proposalString = proposal.getReplacementString();

				if (proposalString != null && proposalString.startsWith(PROPOSAL_TO_APPLY)) {
					bPropoosalToApplyFound = true;
					replacementString = proposal.getReplacementString();
					proposal.apply(document);
					break;
				}
			}
			assertTrue("The proposal to apply not found.", bPropoosalToApplyFound && replacementString != null);
				
			System.out.println("CAJsfPredictiveTagNameProposalsTest.84_JobUtils.waitForIdle()");
			JobUtils.waitForIdle();

			String documentUpdatedContent = document.get();
			// Make the document text to compare
			String documentContentToCompare = newDocumentContent.substring(0, start) +
					replacementString + newDocumentContent.substring(start + INSERTION.length());

			assertTrue("The proposal replacement is failed.", documentContentToCompare.equals(documentUpdatedContent));
		} finally {
			closeEditor();
		}
	}


}
