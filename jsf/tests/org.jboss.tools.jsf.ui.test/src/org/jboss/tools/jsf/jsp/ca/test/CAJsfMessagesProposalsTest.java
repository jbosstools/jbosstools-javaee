/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
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
 * The JUnit test cases for https://issues.jboss.org/browse/JBIDE-10021 issue 
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class CAJsfMessagesProposalsTest extends ContentAssistantTestCase {
	private static final String PROJECT_NAME = "testJSFProject";
	private static final String PAGE_NAME = "WebContent/pages/inputUserName.jsp";
	private static final String PROPOSAL_TO_APPLY_STRING = "abc";
	private static final String PREFIX_STRING = "#{Message.";
	private static final String TAG_STRING = "h:outputText";
	private static final String COMPARE_STRING = "#{Message.abc";
	
	public void setUp() throws Exception {
		project = ProjectImportTestSetup.loadProject(PROJECT_NAME);
		project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
	}

	public static Test suite() {
		return new TestSuite(CAJsfMessagesProposalsTest.class);
	}

	private void doMessagesInLocalizedBundles(String tagName, String prefix, String proposalToApply, String compareString) {
		String documentContent = document.get();
		int start = (documentContent == null ? -1 : documentContent.indexOf(tagName));
		assertFalse("Required node '" + tagName + "' not found in document", (start == -1));
		start = documentContent.indexOf(prefix, start);
		int offsetToTest = start + prefix.length();
		assertTrue("Cannot find the starting point in the test file  \"" + PAGE_NAME + "\"", (start != -1));
		
		JobUtils.waitForIdle();
		
		List<ICompletionProposal> res = CATestUtil.collectProposals(contentAssistant, viewer, offsetToTest);

		assertTrue("Content Assistant returned no proposals", (res != null && res.size() > 0));

		boolean bPropoFound = false;
		for (ICompletionProposal p : res) {
			if (!(p instanceof AutoContentAssistantProposal)) 
				continue;
			AutoContentAssistantProposal proposal = (AutoContentAssistantProposal)p;
			String proposalString = proposal.getDisplayString();

			if (proposalToApply.equals(proposalString)) {
				bPropoFound = true;
				break;
			}
		}
		assertTrue("The proposal to apply not found.", bPropoFound);

	}
	
	public void testMessagesInLocalizedBundles() {
		assertNotNull("Test project '" + PROJECT_NAME + "' is not prepared", project);
		
		openEditor(PAGE_NAME);
		try {
			doMessagesInLocalizedBundles(TAG_STRING, PREFIX_STRING, PROPOSAL_TO_APPLY_STRING, COMPARE_STRING);
		} finally {
			closeEditor();
		}
	}

}
