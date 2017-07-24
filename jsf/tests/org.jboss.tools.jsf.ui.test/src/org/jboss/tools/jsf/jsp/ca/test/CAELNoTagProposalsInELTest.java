/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.jsf.jsp.ca.test;

import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.jboss.tools.common.base.test.contentassist.CATestUtil;
import org.jboss.tools.jst.web.ui.internal.editor.contentassist.AutoELContentAssistantProposal;
import org.jboss.tools.jst.jsp.test.ca.ContentAssistantTestCase;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ProjectImportTestSetup;

/**
 * JUnit Test for the following issue(s):
 * - JBIDE-8428
 * 
 * @author Victor V. Rubezhny
 */
public class CAELNoTagProposalsInELTest  extends ContentAssistantTestCase {
	private static final String PROJECT_NAME = "JSF2KickStartWithoutLibs";
	private static final String PAGE_NAME = "WebContent/pages/inputname.xhtml";
	
	private static final String INSERT_BEFORE = "</ui:define>";
	private static final String EL_LBRACE = "#{";
	private static final String EL_RBRACE = "}";

	public void setUp() throws Exception {
		System.out.println("CAELNoTagProposalsInELTest.setUp");
		project = ProjectImportTestSetup.loadProject(PROJECT_NAME);
	}

	/*
	 * The test case for JBIDE-8428
	 */
	public void testELApplyMethodProposal() {
		System.out.println("CAELNoTagProposalsInELTest.testELApplyMethodProposal");
		openEditor(PAGE_NAME);
		try {

			assertNotNull("Text Viewer not found", getViewer());
			IDocument document = getViewer().getDocument();
			assertNotNull("Can't obtain a test Document.", document);

			String documentContent = document.get();
			int start = (documentContent == null ? -1 : documentContent
					.indexOf(INSERT_BEFORE));
			assertFalse("Required text '" + INSERT_BEFORE
					+ "' not found in document", (start == -1));

			String documentContentModified = documentContent.substring(0,start)
						+ EL_LBRACE + EL_RBRACE 
						+ documentContent.substring(start);

			int offsetToTest = start + EL_LBRACE.length();

			jspTextEditor.setText(documentContentModified);

			System.out.println("CAELNoTagProposalsInELTest.68_JobUtils.waitForIdle");
			JobUtils.waitForIdle();

			List<ICompletionProposal> res = CATestUtil.collectProposals(
					getContentAssistant(), getViewer(), offsetToTest);

			assertTrue("Content Assistant returned no proposals",
					(res != null && res.size() > 0));

			for (ICompletionProposal p : res) {
				assertTrue("Non-EL proposal returned inside #{}", (p instanceof AutoELContentAssistantProposal));
			}
		} finally {
			closeEditor();
		}
	}
}