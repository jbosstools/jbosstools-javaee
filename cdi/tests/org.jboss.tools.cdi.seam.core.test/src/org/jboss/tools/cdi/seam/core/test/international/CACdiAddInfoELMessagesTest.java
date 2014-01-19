/*******************************************************************************
 * Copyright (c) 2011-2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.seam.core.test.international;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.jboss.tools.cdi.seam.core.test.SeamCoreTest;
import org.jboss.tools.common.base.test.contentassist.CATestUtil;
import org.jboss.tools.jst.web.ui.internal.editor.contentassist.AutoELContentAssistantProposal;
import org.jboss.tools.jst.jsp.test.ca.ContentAssistantTestCase;

/**
 * The JUnit test case for JBIDE-9910 issue
 * 
 * @author Victor V. Rubezhny
 *
 */
public class CACdiAddInfoELMessagesTest extends SeamCoreTest {

	private ContentAssistantTestCase caTest = new ContentAssistantTestCase();
	private static final String PAGE_NAME = "WebContent/seam-international.xhtml";
	
	private static final String PREFIXES[] = new String[] {"#{bundles.messa", "#{bundles.messages.home_hea"};
	private static final String NAMES[] = new String[] {"bundles.messages", "home_header"};
	private static final String ADD_INFOS[] = new String[] {
			"/SeamCoreTest/src/messages.properties",
			"home_header"
		};

	/**
	 * The method tests CA on CDI Seam International Module Resource Bundles
	 * @throws Exception 
	 */
	public void testCACdiAddInfoELMessagesTest() throws Exception {
		// Perform CA test
		caTest.setProject(getTestProject());
		
		for (int i = 0; i < PREFIXES.length; i++) {
			AutoELContentAssistantProposal jstProposals[] = getJSTProposals(PREFIXES[i]);
			assertFalse ("No EL Proposals found in Web page: " + PAGE_NAME, (jstProposals == null || jstProposals.length == 0));

			boolean proposalFound = false;
			for (AutoELContentAssistantProposal proposal : jstProposals) {
				if (NAMES[i].equals(proposal.getDisplayString())) { 
					String addInfo = proposal.getAdditionalProposalInfo();
	
					String addInfoValue = HTML2TextUtil.html2Text(addInfo);
					String compareValue = HTML2TextUtil.html2Text(ADD_INFOS[i]);
					assertTrue("Additional Info exists but its value is not expected:\nAdd. Info: [" + addInfoValue + "]\nExpected Value: [" + compareValue + "]", addInfoValue.indexOf(compareValue)>-1);
					proposalFound = true;
					break;
				}
			}
			assertTrue("No '" + NAMES[i] + "' EL Proposals found in Web page: " + PAGE_NAME, proposalFound);
		}
	}

	AutoELContentAssistantProposal[] getJSTProposals(String prefix) {
		caTest.openEditor(PAGE_NAME);
		try {
			String documentContent = caTest.getDocument().get();

			int start = (documentContent == null ? -1 : documentContent.indexOf(prefix));
			assertFalse("Required node '" + prefix + "' not found in document", (start == -1));
			int offsetToTest = start + prefix.length();
			
			
			List<ICompletionProposal> res = CATestUtil.collectProposals(caTest.getContentAssistant(), caTest.getViewer(), offsetToTest);
	
			assertTrue("Content Assistant returned no proposals", (res != null && res.size() > 0));

			Set<AutoELContentAssistantProposal> jstProposals = new HashSet<AutoELContentAssistantProposal>();
			for (ICompletionProposal p : res) {
				if (p instanceof AutoELContentAssistantProposal) {
					jstProposals.add((AutoELContentAssistantProposal)p);
				}
			}
			
			return jstProposals.toArray(new AutoELContentAssistantProposal[0]);
		} finally {
			caTest.closeEditor();
		}
	}
}