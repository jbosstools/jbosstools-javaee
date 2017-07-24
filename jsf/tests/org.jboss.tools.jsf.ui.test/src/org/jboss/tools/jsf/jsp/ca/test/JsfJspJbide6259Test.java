/*******************************************************************************
 * Copyright (c) 2011-2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.jsp.ca.test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.jboss.tools.common.base.test.contentassist.CATestUtil;
import org.jboss.tools.common.el.core.ELCorePlugin;
import org.jboss.tools.common.el.core.ca.preferences.ELContentAssistPreferences;
import org.jboss.tools.jst.web.ui.internal.editor.contentassist.AutoELContentAssistantProposal;
import org.jboss.tools.jst.jsp.test.ca.ContentAssistantTestCase;
import org.jboss.tools.test.util.ProjectImportTestSetup;

public class JsfJspJbide6259Test extends ContentAssistantTestCase {
	private static final String PROJECT_NAME = "JsfJbide1704Test";
	private static final String PAGE_NAME = "/WebContent/pages/greeting.jsp";
	
	public static Test suite() {
		return new TestSuite(JsfJspJbide1704Test.class);
	}

	public void setUp() throws Exception {
		System.out.println("JsfJspJbide6259Test.setUp");
		project = ProjectImportTestSetup.loadProject(PROJECT_NAME);
	}

	private static final String EXPRESSION_STRING = "#{person.";
	private static final String[] TEST_PROPOSAL_EMPTY_SET_EMPTY = new String[0];
	private static final String[] TEST_PROPOSAL_SET_GETTERS_AND_SETTERS_TRUE = {
		"name",
		"getName",
		"getName()"
	};
	private static final String[] TEST_PROPOSAL_SET_GETTERS_AND_SETTERS_FALSE = {
		"name"
	};
	private static final String[] TEST_PROPOSAL_SET_GETTERS_AND_SETTERS_FALSE_WRONG_CASES = {
		"getName",
		"getName()"
	};
	private static final String[] TEST_PROPOSAL_METHODS_WITH_PARENTHESES_ONLY_TRUE = {
		"getName()"
	};
	private static final String[] TEST_PROPOSAL_METHODS_WITH_PARENTHESES_ONLY_TRUE_WRONG_CASES = {
		"getName"
	};
	private static final String[] TEST_PROPOSAL_METHODS_WITH_PARENTHESES_ONLY_FALSE = {
		"getName",
		"getName()"
	};
	
	private void testProposals (List<ICompletionProposal> proposals, String[] testCases, String[] wrongCases) {
		// testCases is the array of proposals which are strict to be present
		// wrongCases is the array of proposals which are strict to be absent
		System.out.println("JsfJspJbide6259Test.testProposals");
		Set<String> existingCases = new HashSet<String>();
		for (ICompletionProposal p : proposals) {
			if (!(p instanceof AutoELContentAssistantProposal))
				continue;
			
			AutoELContentAssistantProposal proposal = (AutoELContentAssistantProposal)p;
			String replacement = proposal.getReplacementString();
			if (replacement == null)
				continue;
			
			if (replacement.indexOf('.') > -1) {
				replacement = replacement.substring(replacement.lastIndexOf('.') + 1);
			}
			
			for (int i = 0; i < testCases.length; i++) {
				if (replacement.equals(testCases[i])) {
					existingCases.add(testCases[i]); // add the proposal to existing set
					continue;
				}
			}

			for (int i = 0; i < wrongCases.length; i++) {
				assertFalse("The proposal \'" + replacement + "\' is not allowed to be shown!", replacement.equals(wrongCases[i]));
			}
		}
		assertTrue("Not all the required proposals are present!", (existingCases.size() == testCases.length));
	}

	private void setupELContentAssistPreferences(boolean methodsWithParenthesesOnly, boolean showGettersAndSetters) {
		ELCorePlugin.getDefault().getPreferenceStore().setValue(ELContentAssistPreferences.SHOW_METHODS_WITH_PARENTHESES_ONLY, methodsWithParenthesesOnly);
		ELCorePlugin.getDefault().getPreferenceStore().setValue(ELContentAssistPreferences.SHOW_GETTERS_AND_SETTERS, showGettersAndSetters);
	}
	
	public void testJspJbide6259 () {
		System.out.println("JsfJspJbide6259Test.testJspJbide6259");
		assertTrue("Test project \"" + PROJECT_NAME + "\" is not loaded", (project != null));

		openEditor(PAGE_NAME);
		
		try {
			
			final IRegion reg = new FindReplaceDocumentAdapter(document).find(0,
					EXPRESSION_STRING, true, true, false, false);
			String errorMessage = null;

			int offsetToTest = reg.getOffset() + EXPRESSION_STRING.length();
			
			// Test method presentations: with and without parentheses 
			
			// Set up EL Content Assist preferences to: 
			// methods are to be shown with and without parentheses
			// getters and setters are to be shown
			setupELContentAssistPreferences(false, true);
			
			List<ICompletionProposal> res = CATestUtil.collectProposals(contentAssistant, viewer, offsetToTest);
			
			assertTrue("Content Assist returned no proposals: ", (res != null && res.size() > 0));
			
			testProposals(res, TEST_PROPOSAL_METHODS_WITH_PARENTHESES_ONLY_FALSE, TEST_PROPOSAL_EMPTY_SET_EMPTY);

			// Test method presentations: with parentheses only 
			
			// Set up EL Content Assist preferences to: 
			// methods are to be shown with parentheses only
			// getters and setters are to be shown

			setupELContentAssistPreferences(true, true);
			CATestUtil.prepareCAInvokation(contentAssistant, viewer, 0); // drop the CA Window
			res = CATestUtil.collectProposals(contentAssistant, viewer, offsetToTest);
			
			assertTrue("Content Assist returned no proposals: ", (res != null && res.size() > 0));
			
			testProposals(res, TEST_PROPOSAL_METHODS_WITH_PARENTHESES_ONLY_TRUE, TEST_PROPOSAL_METHODS_WITH_PARENTHESES_ONLY_TRUE_WRONG_CASES);

			// Test property presentations: property name, getters and setters are to be shown 
			
			// Set up EL Content Assist preferences to: 
			// methods are to be shown with and without parentheses
			// getters and setters are to be shown

			setupELContentAssistPreferences(false, true);
			
			CATestUtil.prepareCAInvokation(contentAssistant, viewer, 0); // drop the CA Window
			res = CATestUtil.collectProposals(contentAssistant, viewer, offsetToTest);
			
			assertTrue("Content Assist returned no proposals: ", (res != null && res.size() > 0));
			
			testProposals(res, TEST_PROPOSAL_SET_GETTERS_AND_SETTERS_TRUE, TEST_PROPOSAL_EMPTY_SET_EMPTY);

			// Test property presentations: the only property name is to be shown 
			
			// Set up EL Content Assist preferences to: 
			// methods are to be shown with and without parentheses
			// getters and setters are to be shown

			setupELContentAssistPreferences(false, false);
			
			CATestUtil.prepareCAInvokation(contentAssistant, viewer, 0); // drop the CA Window
			res = CATestUtil.collectProposals(contentAssistant, viewer, offsetToTest);
			
			assertTrue("Content Assist returned no proposals: ", (res != null && res.size() > 0));
			
			testProposals(res, TEST_PROPOSAL_SET_GETTERS_AND_SETTERS_FALSE, TEST_PROPOSAL_SET_GETTERS_AND_SETTERS_FALSE_WRONG_CASES);
			
		} catch (BadLocationException e) {
			fail(e.getMessage());
		}
		
		closeEditor();
	}

}
