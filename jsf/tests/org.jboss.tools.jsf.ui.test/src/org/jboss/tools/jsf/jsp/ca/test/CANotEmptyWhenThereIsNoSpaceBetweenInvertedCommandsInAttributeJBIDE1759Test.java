/*******************************************************************************
 * Copyright (c) 2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.jsp.ca.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.jboss.tools.common.base.test.contentassist.CATestUtil;
import org.jboss.tools.jst.jsp.test.ca.ContentAssistantTestCase;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ProjectImportTestSetup;
/**
 * JBIDE-4341 JstJspAllTests/testJsfJspJbide1813Test failing with missing applicationScope in completions
 * 
 * The #{ characters are added to INSERTION_BEGIN_STRING, but it is a subject to rollback in the future.
 * The current EL-prompting rules don't allow to call prompting on EL when there is no EL-starting 
 * char sequence in the text. This doing this test to be completely useless. 
 *  
 * @author Jeremy
 *
 */
public class CANotEmptyWhenThereIsNoSpaceBetweenInvertedCommandsInAttributeJBIDE1759Test extends ContentAssistantTestCase {
	private static final String PROJECT_NAME = "JsfJbide1704Test";
	private static final String PAGE_NAME = "/WebContent/pages/greeting";
	private static final String[] PAGE_EXTS = {".jsp", ".xhtml"};
	private static final String INSERT_BEFORE_STRING = "<h:outputText";
	private static final String INSERTION_BEGIN_STRING = "<h:outputText value=\"#{";
	private static final String INSERTION_END_STRING = "\"  />";
	private static final String WHITESPACE_INSERTION_STRING = "";
	
	public static Test suite() {
		return new TestSuite(CANotEmptyWhenThereIsNoSpaceBetweenInvertedCommandsInAttributeJBIDE1759Test.class);
	}

	public void setUp() throws Exception {
		System.out.println("CANotEmptyWhenThereIsNoSpaceBetweenInvertedCommandsInAttributeJBIDE1759Test.setUp");
		project = ProjectImportTestSetup.loadProject(PROJECT_NAME);
	}

	public void testProposalListIsNotEmptyWhenThereIsNoSpaceBetweenInvertedCommandsInAttributeJBIDE1759() {
		System.out.println("CANotEmptyWhenThereIsNoSpaceBetweenInvertedCommandsInAttributeJBIDE1759Test.testProposalListIsNotEmptyWhenThereIsNoSpaceBetweenInvertedCommandsInAttributeJBIDE1759");
		try {
			JobUtils.waitForIdle();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		assertTrue("Test project \"" + PROJECT_NAME + "\" is not loaded", (project != null));

		for (int i = 0; i < PAGE_EXTS.length; i++) {
			testJstJspJbide1759(PAGE_NAME + PAGE_EXTS[i]);
		}
	}
	
	private void testJstJspJbide1759(String pageName) {
		System.out.println("CANotEmptyWhenThereIsNoSpaceBetweenInvertedCommandsInAttributeJBIDE1759Test.testJstJspJbide1759");
		openEditor(pageName);

		// Find start of <h:outputText> tag
		String documentContent = document.get();
		int start = (documentContent == null ? -1 : documentContent.indexOf(INSERT_BEFORE_STRING));

		assertTrue("Cannot find the starting point in the test file  \"" + pageName + "\"", (start != -1));
		
		// First of all perform the test on a region placed in one space behind empty-valued attribute - 
		// this is to return normal list of attribute names proposal list 
		
		String documentContentModified = documentContent.substring(0, start) +
			INSERTION_BEGIN_STRING + WHITESPACE_INSERTION_STRING + INSERTION_END_STRING + documentContent.substring(start);
		
		int offsetToTest = start + INSERTION_BEGIN_STRING.length();


		jspTextEditor.setText(documentContentModified);
		
//		ICompletionProposal[] result= null;
//		String errorMessage = null;

		List<ICompletionProposal> res = CATestUtil.collectProposals(contentAssistant, viewer, offsetToTest);

        assertTrue("Content Assistant returned no proposals", (res != null && res.size() > 0)); //$NON-NLS-1$

		List<String> customCompletionProposals = new ArrayList<String>();
		for (ICompletionProposal p : res) {
			// There should be at least one proposal of type CustomCompletionProposal in the result
			if (p instanceof CustomCompletionProposal) {
				customCompletionProposals.add(((CustomCompletionProposal)p).getReplacementString());
			}
		}
		assertFalse("Content Assistant returned no proposals of type CustomCompletionProposal.",customCompletionProposals.isEmpty());

		try {
			System.out.println("CANotEmptyWhenThereIsNoSpaceBetweenInvertedCommandsInAttributeJBIDE1759Test.105_waitForIdle");
			JobUtils.waitForIdle();
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue("Waiting for the jobs to complete has failed.", false);
		} 

		// Next perform the test on a region placed in one space behind an attribute those value is a container
		// (contains JSF expression) - this has to return the same normal list of attribute names proposal list as
		// we got at the first step (because the tag is the same, but only the attribute value is changed) 
		
		documentContentModified = documentContent.substring(0, start) +
				INSERTION_BEGIN_STRING + INSERTION_END_STRING + documentContent.substring(start);
	
		offsetToTest = start + INSERTION_BEGIN_STRING.length();
	
		jspTextEditor.setText(documentContentModified);
		
		res = CATestUtil.collectProposals(contentAssistant, viewer, offsetToTest);

        assertTrue("Content Assistant returned no proposals", (res != null && res.size() > 0)); //$NON-NLS-1$
		
		for (ICompletionProposal p : res) {
			// There should be the same proposals as in the saved result
			if (p instanceof CustomCompletionProposal) {
				assertTrue("Content Assistant returned additional proposal (proposal returned doesn't exist in the saved list).",
						customCompletionProposals.contains(((CustomCompletionProposal)p).getReplacementString()));
				customCompletionProposals.remove(((CustomCompletionProposal)p).getReplacementString());
			}
		}
		assertTrue("Content Assistant didn't return some of the required proposals.",customCompletionProposals.isEmpty());

		closeEditor();
	}
}
