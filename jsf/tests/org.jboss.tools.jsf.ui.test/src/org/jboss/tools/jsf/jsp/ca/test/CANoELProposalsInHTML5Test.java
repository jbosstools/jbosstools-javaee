/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
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
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.jst.jsp.contentassist.AutoELContentAssistantProposal;
import org.jboss.tools.jst.jsp.test.ca.ContentAssistantTestCase;
import org.jboss.tools.test.util.ProjectImportTestSetup;

/**
 * 
 * Test for issue JBIDE-15331.
 * 
 * @author Victor Rubezhny
 *
 */
public class CANoELProposalsInHTML5Test extends ContentAssistantTestCase {
	private static final String PROJECT_NAME = "jsf2pr";
	private static final String PAGE_NAME = "WebContent/html5test.html";
	private static final String PREFIX = "</div>";

	private static final String PAGE_NAME_NON_HTML5 = "WebContent/greeting.xhtml";
	private static final String PREFIX_NON_HTML5 = "</ui:define>";

	public void setUp() throws Exception {
		project = ProjectImportTestSetup.loadProject(PROJECT_NAME);
	}
	
	public void testCANoELProposalsInHTML5() {
		assertNotNull("Test project '" + PROJECT_NAME + "' is not prepared", project);
		
		openEditor(PAGE_NAME);
		try {
			
			String documentContent = document.get();
			assertTrue("Test Document is not an HTML5 document", FileUtil.isDoctypeHTML(documentContent));
			
			int start = (documentContent == null ? -1 : documentContent.indexOf(PREFIX));
			assertFalse("Required prefix text '" + PREFIX + "' not found in document", (start == -1));
			int offsetToTest = start + PREFIX.length();

			
			List<ICompletionProposal> res = CATestUtil.collectProposals(contentAssistant, viewer, offsetToTest);

			assertTrue("Content Assistant returned no proposals", (res != null && res.size() > 0));

			// Proposals should exist but there should be no "#{}" (new empty EL) proposal
			for (ICompletionProposal p : res) {
				assertTrue("EL proposal returned inside HTML5 document: [" + p.getDisplayString() + "]", !(p instanceof AutoELContentAssistantProposal));
			}
		} finally {
			closeEditor();
		}
	}

	public void testCAELProposalsInNonHTML5() {
		assertNotNull("Test project '" + PROJECT_NAME + "' is not prepared", project);
		
		openEditor(PAGE_NAME_NON_HTML5);
		try {
			
			String documentContent = document.get();
			assertFalse("Test Document is an HTML5 document", FileUtil.isDoctypeHTML(documentContent));
			
			int start = (documentContent == null ? -1 : documentContent.indexOf(PREFIX_NON_HTML5));
			assertFalse("Required prefix text '" + PREFIX_NON_HTML5 + "' not found in document", (start == -1));
			int offsetToTest = start + PREFIX_NON_HTML5.length();

			
			List<ICompletionProposal> res = CATestUtil.collectProposals(contentAssistant, viewer, offsetToTest);

			assertTrue("Content Assistant returned no proposals", (res != null && res.size() > 0));

			// Proposals should exist and there should be "#{}" (new empty EL) proposal
			boolean atLeastOneELProposalFound = false;
			for (ICompletionProposal p : res) {
				if ((atLeastOneELProposalFound = (p instanceof AutoELContentAssistantProposal)))
						break;
			}
			assertTrue("EL proposals aren't returned inside non-HTML5 document", atLeastOneELProposalFound);

		} finally {
			closeEditor();
		}
	}
}
