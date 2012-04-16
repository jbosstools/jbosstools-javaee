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
package org.jboss.tools.cdi.ui.test;

import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Point;
import org.jboss.tools.cdi.core.test.tck.TCKTest;
import org.jboss.tools.common.base.test.contentassist.CATestUtil;
import org.jboss.tools.jst.jsp.contentassist.AutoContentAssistantProposal;
import org.jboss.tools.jst.jsp.test.ca.ContentAssistantTestCase;
import org.jboss.tools.test.util.JobUtils;

/**
 * @author Victor V. Rubezhny
 */
public class CAELApplyMethodProposalTest extends TestCase {

	private IProject project;
	private ContentAssistantTestCase caTest = new ContentAssistantTestCase();
	private static final String PAGE_NAME = "WebContent/ABCProducer.xhtml";
	private static final String[] EL_TO_FIND = { "#{abc.substring(1).substr",
			"#{abc.substring(1).by" };
	private static final String[] PROPOSALS_TO_APPLY = { "substring(", "bytes" };
	private static final String CURSOR_SIGNATURE = "<The cursor point>";

	public void setUp() {
		project = TCKTest.findTestProject();
		try {
			IProject tckProject = TCKTest
					.importPreparedProject("/ca_with_methods/");
		} catch (Exception e) {
			// Ignore all the exceptions in setUp()/tearDown() methods
		}
		caTest.setProject(project);
	}

	/*
	 * The test case for JBIDE-11492
	 */
	public void _testELApplyMethodProposal() {

		caTest.openEditor(PAGE_NAME);
		try {

			assertNotNull("Text Viewer not found", caTest.getViewer());
			IDocument document = caTest.getViewer().getDocument();
			assertNotNull("Can't obtain a test Document.", document);

			for (int i = 0; i < EL_TO_FIND.length; i++) {
				String elToFind = EL_TO_FIND[i];
				String proposalToApply = PROPOSALS_TO_APPLY[i];

				String documentContent = document.get();
				int start = (documentContent == null ? -1 : documentContent
						.indexOf(elToFind));
				assertFalse("Required text '" + elToFind
						+ "' not found in document", (start == -1));
				int offsetToTest = start + elToFind.length();

				JobUtils.waitForIdle();

				List<ICompletionProposal> res = CATestUtil.collectProposals(
						caTest.getContentAssistant(), caTest.getViewer(),
						offsetToTest);

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

				Point s = caTest.getViewer().getSelectedRange();
				assertNotNull("Selection can't be obtained from the editor!", s);

				String documentUpdatedContent = document.get();
				String testUpdatedContent = documentUpdatedContent.substring(0,
						s.x)
						+ CURSOR_SIGNATURE
						+ documentUpdatedContent.substring(s.x);
				String testString = elToFind.substring(0,
						elToFind.lastIndexOf('.') + 1)
						+ proposalToApply + CURSOR_SIGNATURE;
				assertTrue(
						"The proposal replacement is failed.",
						testUpdatedContent.substring(start,
								s.x + CURSOR_SIGNATURE.length()).equals(
								testString));
			}
		} finally {
			caTest.closeEditor();
		}
	}
}