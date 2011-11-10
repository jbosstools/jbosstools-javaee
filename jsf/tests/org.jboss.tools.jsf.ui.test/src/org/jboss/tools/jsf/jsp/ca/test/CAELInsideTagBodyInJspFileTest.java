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
package org.jboss.tools.jsf.jsp.ca.test;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.jboss.tools.common.base.test.contentassist.CATestUtil;
import org.jboss.tools.jst.jsp.contentassist.AutoContentAssistantProposal;
import org.jboss.tools.jst.jsp.test.ca.ContentAssistantTestCase;
import org.jboss.tools.test.util.ProjectImportTestSetup;

/**
 * The JUnit test case for JBIDE-9270 issue
 * 
 * @author Victor V. Rubezhny
 *
 */
public class CAELInsideTagBodyInJspFileTest extends ContentAssistantTestCase {
	private static final String PROJECT_NAME = "Jbide3845Test";
	private static final String PAGE_NAME = "WebContent/pages/jsp_page.jsp";

	private static final String PREFIX = "<f:view>";
	private static final String INSERTION = "#{";

	private static final String[] PROPOSAL_TO_COMPARE_BEFORE_INSERTION = {"#{}"};
	private static final String[] PROPOSAL_TO_COMPARE_AFTER_INSERTION = {"user", "Message"};
	
	public void setUp() throws Exception {
		project = ProjectImportTestSetup.loadProject(PROJECT_NAME);
		project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
	}

	public static Test suite() {
		return new TestSuite(CAELInsideTagBodyInJspFileTest.class);
	}

	private void doCAELInsideTagBodyInJspFileTest(String pageName, String attrPrefix, String[] correctProposals) {
		String documentContent = document.get();
		int start = (documentContent == null ? -1 : documentContent.indexOf(attrPrefix));
		assertFalse("Required prefix text '" + attrPrefix + "' not found in document", (start == -1));
		int offsetToTest = start + attrPrefix.length();
		
		List<ICompletionProposal> res = CATestUtil.collectProposals(contentAssistant, viewer, offsetToTest);

		assertTrue("Content Assistant returned no proposals", (res != null && res.size() > 0));

		for (String correct : correctProposals) {
			assertTrue("Proposal '" + correct +"' not found, but it should be amoung the proposals!", proposalExists(correct, res));
		}
	}
	
	private boolean proposalExists(String proposal, List<ICompletionProposal> proposals) {
		for (ICompletionProposal p : proposals) {
			if (!(p instanceof AutoContentAssistantProposal)) 
				continue;
			AutoContentAssistantProposal existingProposal = (AutoContentAssistantProposal)p;
			String proposalString = existingProposal.getReplacementString();
			
			// We have to compare full proposalString if proposal is not a name (f.e. EL like "#{}"),
			// but we have to compare only name in case of proposal is name (f.e. EL like "user").
			if (proposal.indexOf('}') == -1 && proposalString.indexOf('}') != -1)
				proposalString = proposalString.substring(0, proposalString.indexOf('}'));
			
			if (proposal.equals(proposalString)) {
				return true;
			}
		}
		return false;
	}
	
	public void testCAELInsideTagBodyInJspFile() {
		assertNotNull("Test project '" + PROJECT_NAME + "' is not prepared", project);
		
		openEditor(PAGE_NAME);
		try {
			// Test EL CA for non-openned EL in tag body  
			doCAELInsideTagBodyInJspFileTest(PAGE_NAME, PREFIX, PROPOSAL_TO_COMPARE_BEFORE_INSERTION);
			
			String documentContent = document.get();
			int start = (documentContent == null ? -1 : documentContent.indexOf(PREFIX));
			assertFalse("Required prefix text '" + PREFIX + "' not found in document", (start == -1));

			String newDocumentContent = documentContent.substring(0, start + PREFIX.length()) + 
					INSERTION + documentContent.substring(start + PREFIX.length());
			document.set(newDocumentContent);
			
			// Test EL CA for openned EL in tag body  
			doCAELInsideTagBodyInJspFileTest(PAGE_NAME, PREFIX+INSERTION, PROPOSAL_TO_COMPARE_AFTER_INSERTION);
		} finally {
			closeEditor();
		}
	}
}
