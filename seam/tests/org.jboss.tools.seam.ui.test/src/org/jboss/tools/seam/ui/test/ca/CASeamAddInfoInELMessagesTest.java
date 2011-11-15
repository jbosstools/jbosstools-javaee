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
package org.jboss.tools.seam.ui.test.ca;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.jboss.tools.common.base.test.contentassist.CATestUtil;
import org.jboss.tools.jst.jsp.contentassist.AutoELContentAssistantProposal;
import org.jboss.tools.jst.jsp.test.ca.ContentAssistantTestCase;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ProjectImportTestSetup;

/**
 * The JUnit test case for JBIDE-9910 issue
 * 
 * @author Victor V. Rubezhny
 *
 */
public class CASeamAddInfoInELMessagesTest extends ContentAssistantTestCase {
	private static final String PROJECT_NAME = "TestSeamELContentAssist";
	private static final String PAGE_NAME = "/WebContent/messages.xhtml";

	
	
	private static final String INSERT_AFTER[] = new String[] {"#{messages."};
	private static final String INSERTIONS[] = new String[] {"Text1"};
	private static final String PREFIXES[] = new String[] {"#{messages.Text1"};
	private static final String ADD_INFOS[] = new String[] {
			"<html><body text=\"#000000\" bgcolor=\"#ffffe1\"><b>Property:</b> Text1<br><br><b>Resource Bundle:</b> /TestSeamELContentAssist/src/action/messages.properties<br><b>Value:</b> sss</body></html>"
		};

	public void setUp() throws Exception {
		project = ProjectImportTestSetup.loadProject(PROJECT_NAME);
		project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
	}

	public static Test suite() {
		return new TestSuite(CASeamAddInfoInELMessagesTest.class);
	}

	public void testCASeamAddInfoInELMessages () {
		for (int i = 0; i < PREFIXES.length; i++) {
			AutoELContentAssistantProposal jstProposals[] = getJSTProposals(INSERT_AFTER[i], INSERTIONS[i], PREFIXES[i]);
			assertFalse ("No EL Proposals found in Web page: " + PAGE_NAME, (jstProposals == null || jstProposals.length == 0));
			assertEquals ("Content Assist in returned more than 1 proposal for Web page: " + PAGE_NAME + 
					". Test project and/or data should be verfied/updated.", 1, jstProposals.length);

			for (AutoELContentAssistantProposal proposal : jstProposals) {
				String addInfo = proposal.getAdditionalProposalInfo();

				String addInfoValue = html2Text(addInfo);
				String compareValue = html2Text(ADD_INFOS[i]);
				assertTrue("Additional Info exists but its value is not expected:\nAdd. Info: [" + addInfoValue + "]\nExpected Value: [" + compareValue + "]", compareValue.equalsIgnoreCase(addInfoValue));
			}
		}
	}
	
	String html2Text(String html) {
		StringBuilder sb = new StringBuilder();
		int state = 0;
		for (char ch : html.toCharArray()) {
			switch (state) {
			case (int)'<':
				// Read to null until '>'-char is read
				if (ch != '>')
					continue;
				state = 0;
				break;
			default:
				if (ch == '<') {
					state = '<';
					continue;
				}
				sb.append(ch);
				break;
			}
		}
		return sb.toString();
	}

	AutoELContentAssistantProposal[] getJSTProposals(String insertAfter, String insertion, String prefix) {
		openEditor(PAGE_NAME);
		try {
			String documentContent = document.get();

			int insertionStart = (documentContent == null ? -1 : documentContent.indexOf(insertAfter));
			assertFalse("Required insertion node '" + prefix + "' not found in document", (insertionStart == -1));
			int offsetToInsert = insertionStart + insertAfter.length();

			documentContent = documentContent.substring(0, offsetToInsert) +
					insertion + documentContent.substring(offsetToInsert);
			
			document.set(documentContent);
			JobUtils.waitForIdle();
			
			documentContent = document.get();
			int start = (documentContent == null ? -1 : documentContent.indexOf(prefix));
			assertFalse("Required node '" + prefix + "' not found in document", (start == -1));
			int offsetToTest = start + prefix.length();
			
			
			List<ICompletionProposal> res = CATestUtil.collectProposals(contentAssistant, viewer, offsetToTest);
	
			assertTrue("Content Assistant returned no proposals", (res != null && res.size() > 0));

			Set<AutoELContentAssistantProposal> jstProposals = new HashSet<AutoELContentAssistantProposal>();
			for (ICompletionProposal p : res) {
				if (p instanceof AutoELContentAssistantProposal) {
					jstProposals.add((AutoELContentAssistantProposal)p);
				}
			}
			
			return jstProposals.toArray(new AutoELContentAssistantProposal[0]);
		} finally {
			closeEditor();
		}
	}
}
