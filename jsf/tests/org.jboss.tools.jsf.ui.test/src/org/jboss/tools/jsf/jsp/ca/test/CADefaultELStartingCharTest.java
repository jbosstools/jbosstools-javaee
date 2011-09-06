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
 * The JUnit test cases for https://issues.jboss.org/browse/JBIDE-9634 issue 
 * 
 * @author Victor V. Rubezhny
 *
 */
public class CADefaultELStartingCharTest  extends ContentAssistantTestCase {
	private static final String PROJECT_NAME = "JsfJbide1704Test";
	private static final String JSP_PAGE_NAME = "WebContent/pages/greeting.jsp";
	private static final String XHTML_PAGE_NAME = "WebContent/pages/greeting.xhtml";

	private static final String TAG_ATTRIBUTE = "id=\"";
	private static final String TEXT_PREFIX_STRING = "pe";

	private static final String PROPOSAL_TO_APPLY_STRING = "person : Person";
	private static final String COMPARE_STRING = "#{person}";
	
	public void setUp() throws Exception {
		project = ProjectImportTestSetup.loadProject(PROJECT_NAME);
		project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
	}

	public static Test suite() {
		return new TestSuite(CADefaultELStartingCharTest.class);
	}

	private void doTestCADefaultELStartingCharTest(String pageName, String attr, String prefix, String proposalToApply, String compareString) {
		String documentContent = document.get();
		int start = (documentContent == null ? -1 : documentContent.indexOf(attr));
		assertFalse("Required node '" + attr + "' not found in document", (start == -1));
		int offsetToTest = start + attr.length() + prefix.length();
		
		String documentContentToCompare = documentContent.substring(0, start + attr.length()) +
			compareString + documentContent.substring(start + attr.length() + prefix.length());
		
		JobUtils.waitForIdle();
		
		List<ICompletionProposal> res = CATestUtil.collectProposals(contentAssistant, viewer, offsetToTest);

		assertTrue("Content Assistant returned no proposals", (res != null && res.size() > 0));

		boolean bPropoosalToApplyFound = false;
		for (ICompletionProposal p : res) {
			if (!(p instanceof AutoContentAssistantProposal)) 
				continue;
			AutoContentAssistantProposal proposal = (AutoContentAssistantProposal)p;
			String proposalString = proposal.getDisplayString();

			if (proposalToApply.equals(proposalString)) {
				bPropoosalToApplyFound = true;
				proposal.apply(document);
				break;
			}
		}
		assertTrue("The proposal to apply not found.", bPropoosalToApplyFound);

		try {
			JobUtils.waitForIdle();
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("Waiting for the jobs to complete has failed.", false);
		} 

		String documentUpdatedContent = document.get();
		assertTrue("The proposal replacement is failed.", documentContentToCompare.equals(documentUpdatedContent));
		
	}
	
	public void testCADefaultELStartingCharInJSP() {
		assertNotNull("Test project '" + PROJECT_NAME + "' is not prepared", project);
		
		openEditor(JSP_PAGE_NAME);
		try {
			doTestCADefaultELStartingCharTest(JSP_PAGE_NAME, TAG_ATTRIBUTE, TEXT_PREFIX_STRING, PROPOSAL_TO_APPLY_STRING, COMPARE_STRING);
		} finally {
			closeEditor();
		}
	}

	public void testCADefaultELStartingCharTestInXHTML() {
		assertNotNull("Test project '" + PROJECT_NAME + "' is not prepared", project);
		
		openEditor(XHTML_PAGE_NAME);
		try {
			doTestCADefaultELStartingCharTest(XHTML_PAGE_NAME, TAG_ATTRIBUTE, TEXT_PREFIX_STRING, PROPOSAL_TO_APPLY_STRING, COMPARE_STRING);
		} finally {
			closeEditor();
		}
	}

}
