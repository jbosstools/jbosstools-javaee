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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.jboss.tools.common.base.test.contentassist.CATestUtil;
import org.jboss.tools.jst.jsp.contentassist.AutoContentAssistantProposal;
import org.jboss.tools.jst.jsp.test.ca.ContentAssistantTestCase;
import org.jboss.tools.jst.jsp.test.ca.JstJspJbide1641Test;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.TestProjectProvider;

/**
 * The JUnit test cases for https://issues.jboss.org/browse/JBIDE-9414 issue 
 * 
 * @author Victor V. Rubezhny
 *
 */
public class JsfJspLongResourceBundlePropertyNamesTest extends ContentAssistantTestCase {
	TestProjectProvider provider = null;
	boolean makeCopy = false;
	private static final String PROJECT_NAME = "CAForCompositeComponentTest";
	private static final String PAGE_NAME = "WebContent/pages/greetingLong.xhtml";
	private static final String PROPOSAL_TO_APPLY_STRING = "['org.jboss.long.named.Property']";
	private static final String PREFIX_STRING = "#{msg.";
	private static final String ATTR_PREFIX_STRING = "h:outputText";
	private static final String TEXT_PREFIX_STRING = "<ui:define name=\"body\"";
	private static final String COMPARE_STRING = "#{msg['org.jboss.long.named.Property']";
	
//	public JsfJspLongResourceBundlePropertyNamesTest() {
//		try {
//			provider = new TestProjectProvider("org.jboss.tools.jsf.ui.test", null, PROJECT_NAME, makeCopy);
//			project = provider.getProject();
//		} catch (CoreException e) {
//			e.printStackTrace();
//			project = null;
//		} 
//	}
	
	public void setUp() throws Exception {
		provider = new TestProjectProvider("org.jboss.tools.jsf.ui.test", null, PROJECT_NAME, makeCopy); 
		project = provider.getProject();
	}

	protected void tearDown() throws Exception {
		if(provider != null) {
			provider.dispose();
		}
	}

	public static Test suite() {
		return new TestSuite(JstJspJbide1641Test.class);
	}

//	@Override
//	protected void finalize() throws Throwable {
//		if(provider != null) {
//			provider.dispose();
//		}
//		super.finalize();
//	}

	private void doTestLongResourceBundlePropertyNames(String tagName, String prefix, String proposalToApply, String compareString) {
		// Find start of <ui:composition> tag
		String documentContent = document.get();
		int start = (documentContent == null ? -1 : documentContent.indexOf(tagName));
		assertFalse("Required node '" + tagName + "' not found in document", (start == -1));
		start = documentContent.indexOf(prefix, start);
		int offsetToTest = start + prefix.length();
		assertTrue("Cannot find the starting point in the test file  \"" + PAGE_NAME + "\"", (start != -1));
		
		String documentContentToCompare = documentContent.substring(0, start) +
			compareString + documentContent.substring(start + prefix.length());
		
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
	
	public void testJstJspLongResourceBundlePropertyNamesInTagAttributeValue() {
		assertNotNull("Test project '" + PROJECT_NAME + "' is not prepared", project);
		
		openEditor(PAGE_NAME);
		try {
			doTestLongResourceBundlePropertyNames(ATTR_PREFIX_STRING, PREFIX_STRING, PROPOSAL_TO_APPLY_STRING, COMPARE_STRING);
		} finally {
			closeEditor();
		}
	}

	public void testJstJspLongResourceBundlePropertyNamesInText() {
		assertNotNull("Test project '" + PROJECT_NAME + "' is not prepared", project);
		
		openEditor(PAGE_NAME);
		try {
			doTestLongResourceBundlePropertyNames(TEXT_PREFIX_STRING, PREFIX_STRING, PROPOSAL_TO_APPLY_STRING, COMPARE_STRING);
		} finally {
			closeEditor();
		}
	}

}
