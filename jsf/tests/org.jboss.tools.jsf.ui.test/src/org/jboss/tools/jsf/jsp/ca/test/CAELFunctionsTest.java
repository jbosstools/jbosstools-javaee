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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.jst.web.ui.base.test.ca.ContentAssistantTestCase;
import org.jboss.tools.test.util.ProjectImportTestSetup;

/**	
 * Test for https://jira.jboss.org/jira/browse/JBIDE-8924
 * 
 * @author Victor V. Rubezhny
 *
 */
public class CAELFunctionsTest  extends ContentAssistantTestCase {
	private static final String PROJECT_NAME = "jsf2pr";
	private static final String PAGE_NAME = "/WebContent/greeting.xhtml";
	private static final String EL_TO_FIND = "#{rich:findComponent().";
	private static final String[] PROPOSALS = { // Compare first 5 proposals 
		"rich:findcomponent().attributes",
		"rich:findComponent().broadcast()",
		"rich:findComponent().childCount",
		"rich:findComponent().children",
		"rich:findComponent().clearInitialState()"
	};
	
	public void setUp() throws Exception {
		project = ProjectImportTestSetup.loadProject(PROJECT_NAME);
	}

	/**
	 * Test for https://jira.jboss.org/jira/browse/JBIDE-8924
	 */
	public void testELFunctions() {
		assertNotNull("Test project \"" + PROJECT_NAME + "\" is not loaded", project);
		try {
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			fail(e.getMessage());
			e.printStackTrace();
		}
		checkProposals(PAGE_NAME, EL_TO_FIND, EL_TO_FIND.length(), PROPOSALS, false);
	}
}
