/*******************************************************************************
 * Copyright (c) 2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.jsp.ca.test;

import org.jboss.tools.jst.web.ui.base.test.ca.ContentAssistantTestCase;
import org.jboss.tools.test.util.ProjectImportTestSetup;

public class CAInEventAttributesTest extends ContentAssistantTestCase{
	private static final String PROJECT_NAME = "testJSFProject"; //$NON-NLS-1$
	private static final String JSP_PAGE_NAME = "/WebContent/pages/inputUserName.jsp"; //$NON-NLS-1$
	private static final String XHTML_PAGE_NAME = "/WebContent/pages/inputname.xhtml"; //$NON-NLS-1$
	private static final String HTML_PAGE_NAME = "/WebContent/pages/inputname.html"; //$NON-NLS-1$
	
	public void setUp() throws Exception {
		project = ProjectImportTestSetup.loadProject(PROJECT_NAME);
	}

	public void testCAInOnclickInJSP(){
		String[] proposals = {
			"user", //$NON-NLS-1$
		};

		checkProposals(JSP_PAGE_NAME, "<p onclick=\"#{}\">", 14, proposals, false, false);
	}

	public void testCAInOnclickInXHTML(){
		String[] proposals = {
			"user", //$NON-NLS-1$
		};

		checkProposals(XHTML_PAGE_NAME, "<p onclick=\"#{}\">", 14, proposals, false, false);
	}

	public void testCAInOnclickInHTML(){
		String[] proposals = {
			"user", //$NON-NLS-1$
		};

		checkProposals(HTML_PAGE_NAME, "<p onclick=\"#{}\">", 14, proposals, false, false);
	}

}