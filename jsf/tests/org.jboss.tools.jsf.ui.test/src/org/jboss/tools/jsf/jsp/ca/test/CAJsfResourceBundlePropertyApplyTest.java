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

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.jboss.tools.common.base.test.contentassist.CATestUtil;
import org.jboss.tools.jst.jsp.contentassist.AutoContentAssistantProposal;
import org.jboss.tools.jst.jsp.test.ca.ContentAssistantTestCase;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.TestProjectProvider;

/**
 * The JUnit test case for issue JBIDE-10320
 * 
 * @author Victor V. Rubezhny
 *
 */public class CAJsfResourceBundlePropertyApplyTest  extends ContentAssistantTestCase {
	private static final String PROJECT_NAME = "JSF2KickStartWithoutLibs";
	private static final String PAGE_NAME = "WebContent/pages/inputname.xhtml";
	
	private static final String INSERT_BEFORE[] = {"<ui:define", "<h:message"};
	private static final String INSERTION[] = {"<h:outputText value=\"#{msgs[]\"\r\n", "#{msgs[]\r\n"};
	private static final String TEMPLATE = "#{msgs[";
	private static final String PROPOSAL_TO_APPLY_STRING = "msgs['prompt']}";
	private static final String COMPARE_STRING[] = {"#{msgs['prompt']}\"", "#{msgs['prompt']}"};
	private static final String END_OF_SEQUENCE[] = {"\"", "]"};
	
	private TestProjectProvider provider = null;

	public void setUp() throws Exception {
       provider = new TestProjectProvider("org.jboss.tools.jsf.ui.test", null, PROJECT_NAME,false);  //$NON-NLS-1$
       project = provider.getProject();
	}

	public static Test suite() {
		return new TestSuite(CAJsfResourceBundlePropertyApplyTest.class);
	}

	@SuppressWarnings("restriction")
	private void doTestCAJsfResourceBundlePropertyApply(String insertBefore, String insertion, String endOfSequence, String template, String proposalToApply, String compareString) {
		// Find start of <ui:composition> tag
		String documentContent = document.get();

		int start = (documentContent == null ? -1 : documentContent.indexOf(insertBefore));
		assertFalse("Required text '" + insertBefore + "' not found in document", (start == -1));

		String newDocumentContent = documentContent.substring(0, start) + 
				insertion + documentContent.substring(start);

		start = (newDocumentContent == null ? -1 : newDocumentContent.indexOf(template, start));
		assertTrue("Cannot find the starting point in the test file  \"" + PAGE_NAME + "\"", (start != -1));
		int offsetToTest = start + template.length();

		// Make the document text to compare
		int endOfSequencePosition = newDocumentContent.indexOf(endOfSequence, start);
		assertTrue("Cannot find the ending point in the test file  \"" + PAGE_NAME + "\"", (endOfSequencePosition != -1));
		endOfSequencePosition += endOfSequence.length(); 
		
		String documentContentToCompare = newDocumentContent.substring(0, start) +
			compareString + newDocumentContent.substring(endOfSequencePosition);

		// Update the document with a new text
		document.set(newDocumentContent);
		JobUtils.waitForIdle();
		
		List<ICompletionProposal> res = CATestUtil.collectProposals(contentAssistant, viewer, offsetToTest);

		assertTrue("Content Assistant returned no proposals", (res != null && res.size() > 0));

		boolean bPropoosalToApplyFound = false;
		for (ICompletionProposal p : res) {
			if (!(p instanceof AutoContentAssistantProposal)) 
				continue;
			AutoContentAssistantProposal proposal = (AutoContentAssistantProposal)p;
			String proposalString = proposal.getReplacementString();

			if (proposalToApply.equals(proposalString)) {
				bPropoosalToApplyFound = true;
				proposal.apply(document);
				break;
			}
		}
		assertTrue("The proposal to apply not found.", bPropoosalToApplyFound);

		JobUtils.waitForIdle();

		String documentUpdatedContent = document.get();
		assertTrue("The proposal replacement is failed.", documentContentToCompare.equals(documentUpdatedContent));
	}
	
	public void testdoTestCAJsfResourceBundlePropertyApplyInAttributeValue() {
		assertNotNull("Test project '" + PROJECT_NAME + "' is not prepared", project);
		
		openEditor(PAGE_NAME);
		try {
			doTestCAJsfResourceBundlePropertyApply(INSERT_BEFORE[0], INSERTION[0], END_OF_SEQUENCE[0], TEMPLATE, PROPOSAL_TO_APPLY_STRING, COMPARE_STRING[0]);
		} finally {
			closeEditor();
		}
	}

	public void testdoTestCAJsfResourceBundlePropertyApplyInTextBody() {
		assertNotNull("Test project '" + PROJECT_NAME + "' is not prepared", project);
		
		openEditor(PAGE_NAME);
		try {
			doTestCAJsfResourceBundlePropertyApply(INSERT_BEFORE[1], INSERTION[1], END_OF_SEQUENCE[1], TEMPLATE, PROPOSAL_TO_APPLY_STRING, COMPARE_STRING[1]);
		} finally {
			closeEditor();
		}
	}
}
