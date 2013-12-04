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

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.jboss.tools.common.base.test.contentassist.CATestUtil;
import org.jboss.tools.jst.web.ui.internal.editor.contentassist.AutoContentAssistantProposal;
import org.jboss.tools.jst.web.ui.base.test.ca.ContentAssistantTestCase;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ProjectImportTestSetup;

public class CASuggestsNotOnlyELProposalsJBIDE2437Test extends ContentAssistantTestCase {
	private static final String PROJECT_NAME = "JsfJbide1704Test";
	private static final String PAGE_NAME = "/WebContent/pages/greeting1.xhtml";
	
	public static Test suite() {
		return new TestSuite(CASuggestsNotOnlyELProposalsJBIDE2437Test.class);
	}

	public void setUp() throws Exception {
		project = ProjectImportTestSetup.loadProject(PROJECT_NAME);
	}

	public void testThereAreNotOnlyELProposalsJBIDE2437 () throws BadLocationException {
		openEditor(PAGE_NAME);
		JobUtils.waitForIdle();
		
		IRegion reg = new FindReplaceDocumentAdapter(document).find(0, "/templates/common.xhtml", true, true, false, false);
		
		List<ICompletionProposal> res = CATestUtil.collectProposals(contentAssistant, viewer, reg.getOffset());

        assertTrue("Content Assistant returned no proposals", (res != null && res.size() > 0)); //$NON-NLS-1$

		boolean bELProposalsFound = false;
		boolean bTemplatePathProposalsFound = false;
		for (int k = 0; k < res.size() && 
				(!bELProposalsFound || !bTemplatePathProposalsFound); k++) {
			if (res.get(k) instanceof AutoContentAssistantProposal) {
				AutoContentAssistantProposal proposal = (AutoContentAssistantProposal)res.get(k);
				
				// Test the display string for the proposals - it has to shown the thmplate path beginning or EL-expression beginning
				// because the CA is started the calculation from the very beginning of the attribute value.
				String dispString = proposal.getDisplayString();
				
				assertFalse("The CA proposal returned NULL display string.", (dispString == null));
				
				if (dispString.startsWith("#{") || dispString.startsWith("${")) {
					bELProposalsFound = true;
				} else if (dispString.indexOf("/") != -1) {
					bTemplatePathProposalsFound = true;
				}
			}
		}
			
		// There should be proposals for template paths 
		assertTrue("Content Assistant peturned no proposals for template paths.", bTemplatePathProposalsFound);
		// There should be proposals for EL-expresions
		
		// JBIDE-4341: JstJspAllTests/testJsfJspJbide1813Test failing with missing applicationScope in completions
		// Due to the current EL-prompting rules there is no EL-proposals allowed if there are no EL-starting char 
		// sequence in the text. So, The following code line is commented out. This is the subject to rollback in the future.
		//
//		assertTrue("Content Assistant peturned no proposals for EL.", bELProposalsFound);
		
		closeEditor();
	}

}
