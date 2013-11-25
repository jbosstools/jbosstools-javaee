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
import org.jboss.tools.jst.web.ui.internal.editor.contentassist.AutoELContentAssistantProposal;
import org.jboss.tools.jst.jsp.test.ca.ContentAssistantTestCase;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ProjectImportTestSetup;

/**
 * The JUnit test case for JBIDE-13995 issue
 * 
 * @author Victor V. Rubezhny
 *
 */
public class CAELBeanPropertyTest extends ContentAssistantTestCase {
	private static final String PROJECT_NAME = "JSF2KickStartWithoutLibs";
	private static final String PAGE_NAME = "WebContent/pages/inputname.xhtml";
	private static final String PREFIX = "#{user.na";
	private static final String PROPERTY_NAME = "name";
	private static final String PROPERTY_TYPE = "String";
	private static final String PROPERTY_BEAN = "User";
	
	public void setUp() throws Exception {
		project = ProjectImportTestSetup.loadProject(PROJECT_NAME);
	}
	
	public void testCAELBeanProperty () {
		assertNotNull("Test project \"" + PROJECT_NAME + "\" is not loaded", project);

		openEditor(PAGE_NAME);
		try {
			String documentContent = document.get();
			int start = (documentContent == null ? -1 : documentContent.indexOf(PREFIX));
			assertFalse("Required node '" + PREFIX + "' not found in document", (start == -1));
			int offsetToTest = start + PREFIX.length();
			
			JobUtils.waitForIdle();
			
			List<ICompletionProposal> res = CATestUtil.collectProposals(contentAssistant, viewer, offsetToTest);
	
			assertTrue("Content Assistant returned no proposals", (res != null && res.size() > 0));

			for (ICompletionProposal p : res) {
				if (!(p instanceof AutoELContentAssistantProposal))
					continue; // Our proposal should be of type AutoELContentAssistProposal 
				
				AutoELContentAssistantProposal proposal = (AutoELContentAssistantProposal)p;
				String displayString = proposal.getDisplayString();
				
				int columnIndex = displayString.indexOf(':');
				if (columnIndex == -1)
					continue; // Our proposal display string should be of form "<propertyName> : <propertyType> - <BeanType>" 

				int dashIndex = displayString.indexOf('-', columnIndex);
				if (dashIndex == -1)
					continue; // Our proposal display string should be of form "<propertyName> : <propertyType> - <BeanType>" 

				String propertyName = displayString.substring(0, columnIndex).trim();
				if (!PROPERTY_NAME.equals(propertyName))
					continue; // probably not our property.

				String propertyBean = displayString.substring(dashIndex + 1).trim();
				if (!PROPERTY_BEAN.equals(propertyBean))
					continue; // probably not our property.
				
				String propertyType = displayString.substring(columnIndex + 1, dashIndex).trim();
				assertEquals("Property type should be '" + PROPERTY_TYPE + "' but it isn't", PROPERTY_TYPE, propertyType);
				
				// No more proposals are to be tested
				return;
			}
			
			fail("No AutoELContentAssistProposal found for property '" + PROPERTY_NAME + "'");
		} finally {
			closeEditor();
		}
	}
}
