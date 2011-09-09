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
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ProjectImportTestSetup;

/**
 * Test case testing http://jira.jboss.com/jira/browse/JBIDE-9633 issue.
 * 
 * @author Victor V. Rubezhny
 *
 */
public class CAJsfMessagesProposalsFilteringTest extends ContentAssistantTestCase {
	private static final String PROJECT_NAME = "JSF2KickStartWithoutLibs";
	private static final String PAGE_NAME = "WebContent/pages/inputname.xhtml";

	private static final String TAG_ATTRIBUTE_COMMON_PREFIX = "value=\"#{";
	private static final String TAG_ATTRIBUTE_USER_PREFIX = "value=\"#{us";
	private static final String TAG_ATTRIBUTE_MSGS_PREFIX = "label=\"${ms";

	private static final String[] PROPOSAL_TO_COMPARE_COMMON = {"user : User", "msgs"};
	private static final String[] PROPOSAL_TO_COMPARE_USER_STRING = {"user : User"};
	private static final String[] PROPOSAL_TO_COMPARE_MSGS_STRING = {"msgs"};
	
	public void setUp() throws Exception {
		project = ProjectImportTestSetup.loadProject(PROJECT_NAME);
		project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
	}

	public static Test suite() {
		return new TestSuite(CADefaultELStartingCharTest.class);
	}

	private void doTestCAJsfMessagesFilteringTest(String pageName, String attrPrefix, String[] correctProposals, String[] wrongProposals) {
		String documentContent = document.get();
		int start = (documentContent == null ? -1 : documentContent.indexOf(attrPrefix));
		assertFalse("Required node '" + attrPrefix + "' not found in document", (start == -1));
		int offsetToTest = start + attrPrefix.length();
		
		JobUtils.waitForIdle();
		
		List<ICompletionProposal> res = CATestUtil.collectProposals(contentAssistant, viewer, offsetToTest);

		assertTrue("Content Assistant returned no proposals", (res != null && res.size() > 0));

		for (String correct : correctProposals) {
			assertTrue("Proposal '" + correct +"' not found, but it should be amoung the proposals!", proposalExists(correct, res));
		}
		for (String wrong : wrongProposals) {
			assertFalse("Proposal '" + wrong +"' found, but it should not be amoung the proposals!", proposalExists(wrong, res));
		}
	}
	
	private boolean proposalExists(String proposal, List<ICompletionProposal> proposals) {
		for (ICompletionProposal p : proposals) {
			if (!(p instanceof AutoContentAssistantProposal)) 
				continue;
			AutoContentAssistantProposal existingProposal = (AutoContentAssistantProposal)p;
			String proposalString = existingProposal.getDisplayString();

			if (proposal.equals(proposalString)) {
				return true;
			}
		}
		return false;
	}
	
	public void testCAJsfMessagesFiltering() {
		assertNotNull("Test project '" + PROJECT_NAME + "' is not prepared", project);
		
		openEditor(PAGE_NAME);
		try {
			// Test common case were both, "user" and "msgs" proposals should exist
			doTestCAJsfMessagesFilteringTest(PAGE_NAME, TAG_ATTRIBUTE_COMMON_PREFIX, PROPOSAL_TO_COMPARE_COMMON, new String[0]);

			// Test user case were the only "user" proposal should exist
			doTestCAJsfMessagesFilteringTest(PAGE_NAME, TAG_ATTRIBUTE_USER_PREFIX, PROPOSAL_TO_COMPARE_USER_STRING, PROPOSAL_TO_COMPARE_MSGS_STRING);

			// Test user case were the only "msgs" proposal should exist
			doTestCAJsfMessagesFilteringTest(PAGE_NAME, TAG_ATTRIBUTE_MSGS_PREFIX, PROPOSAL_TO_COMPARE_MSGS_STRING, PROPOSAL_TO_COMPARE_USER_STRING);
		} finally {
			closeEditor();
		}
	}
}
