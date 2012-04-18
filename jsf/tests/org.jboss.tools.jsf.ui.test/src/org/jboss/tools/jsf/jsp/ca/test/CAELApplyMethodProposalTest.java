/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
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

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Point;
import org.jboss.tools.common.base.test.contentassist.CATestUtil;
import org.jboss.tools.jst.jsp.contentassist.AutoContentAssistantProposal;
import org.jboss.tools.jst.jsp.test.ca.ContentAssistantTestCase;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ProjectImportTestSetup;

/**
 * @author Victor V. Rubezhny
 */
public class CAELApplyMethodProposalTest extends ContentAssistantTestCase {
	private static final String PROJECT_NAME = "JSF2KickStartWithoutLibs";
	private static final String PAGE_NAME = "WebContent/pages/inputname.xhtml";
	private static final String EL_RBRACE = "}";

	private static final String[] EL_TO_FIND = { "#{user.name", "#{user.name" };
	private static final String[] EL_TO_REPLACE_WITH = {
			"#{user.name.substring(1).substr", "#{user.name.substring(1).by" };
	private static final String[] PROPOSALS_TO_APPLY = { "substring(", "bytes" };
	private static final String CURSOR_SIGNATURE = "<The cursor point>";

	public void setUp() throws Exception {
		project = ProjectImportTestSetup.loadProject(PROJECT_NAME);
		project.build(IncrementalProjectBuilder.FULL_BUILD,
				new NullProgressMonitor());
	}

	/*
	 * The test case for JBIDE-11492
	 */
	public void testELApplyMethodProposal() {

		openEditor(PAGE_NAME);
		try {

			assertNotNull("Text Viewer not found", getViewer());
			IDocument document = getViewer().getDocument();
			assertNotNull("Can't obtain a test Document.", document);

			for (int i = 0; i < EL_TO_FIND.length; i++) {
				String elToFind = EL_TO_FIND[i];
				String elToReplaceWith = EL_TO_REPLACE_WITH[i];
				String proposalToApply = PROPOSALS_TO_APPLY[i];

				String documentContent = document.get();
				int start = (documentContent == null ? -1 : documentContent
						.indexOf(elToFind));
				assertFalse("Required text '" + elToFind
						+ "' not found in document", (start == -1));

				int end = (documentContent == null ? -1 : documentContent
						.indexOf(EL_RBRACE, start));
				assertFalse("Required text '" + EL_RBRACE
						+ "' not found in document", (end == -1));

				String documentContentModified = documentContent.substring(0,
						start)
						+ elToReplaceWith
						+ documentContent.substring(end);

				int offsetToTest = start + elToReplaceWith.length();

				jspTextEditor.setText(documentContentModified);

				JobUtils.waitForIdle();

				List<ICompletionProposal> res = CATestUtil.collectProposals(
						getContentAssistant(), getViewer(), offsetToTest);

				assertTrue("Content Assistant returned no proposals",
						(res != null && res.size() > 0));

				boolean bPropoosalToApplyFound = false;
				for (ICompletionProposal p : res) {
					if (!(p instanceof AutoContentAssistantProposal))
						continue;
					AutoContentAssistantProposal proposal = (AutoContentAssistantProposal) p;
					String proposalString = proposal.getDisplayString();

					if (proposalString.startsWith(proposalToApply)) {
						bPropoosalToApplyFound = true;
						proposal.apply(document);

						// The following is copied from CompletionProposalPopup
						// class that actually applies the proposal.
						// Node that fContentAssistSubjectControlAdapter object
						// is replaced by the Viewer object (which is equivalent
						// in many cases).
						// So, after the proposal is applied a new selection is
						// set in the Editor:
						Point selection = p.getSelection(document);
						if (selection != null) {
							getViewer().setSelectedRange(selection.x,
									selection.y);
							getViewer().revealRange(selection.x, selection.y);
						}
						// End of code from CompletionProposalPopup

						break;
					}
				}
				assertTrue("The proposal to apply not found.",
						bPropoosalToApplyFound);

				try {
					JobUtils.waitForIdle();
				} catch (Exception e) {
					e.printStackTrace();
					assertTrue("Waiting for the jobs to complete has failed.",
							false);
				}

				Point s = getViewer().getSelectedRange();
				assertNotNull("Selection can't be obtained from the editor!", s);

				String documentUpdatedContent = document.get();
				String testUpdatedContent = documentUpdatedContent.substring(0,
						s.x)
						+ CURSOR_SIGNATURE
						+ documentUpdatedContent.substring(s.x);
				String testString = elToReplaceWith.substring(0,
						elToReplaceWith.lastIndexOf('.') + 1)
						+ proposalToApply
						+ CURSOR_SIGNATURE;
				assertTrue(
						"The proposal replacement is failed.",
						testUpdatedContent.substring(start,
								s.x + CURSOR_SIGNATURE.length()).equals(
								testString));
			}
		} finally {
			closeEditor();
		}
	}
}