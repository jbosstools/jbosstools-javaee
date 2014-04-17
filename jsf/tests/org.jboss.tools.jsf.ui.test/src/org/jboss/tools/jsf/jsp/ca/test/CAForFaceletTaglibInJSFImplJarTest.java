/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.jsp.ca.test;

import org.jboss.tools.jst.jsp.test.ca.ContentAssistantTestCase;
import org.jboss.tools.test.util.TestProjectProvider;

public class CAForFaceletTaglibInJSFImplJarTest extends ContentAssistantTestCase{
	TestProjectProvider provider = null;
	private static final String PROJECT_NAME = "testJSF2Project";
	private static final String PAGE_NAME = "/WebContent/pages/inputname.xhtml";
	private static final String PAGE_NAME_22 = "/WebContent/pages/inputname22.xhtml";
	
	public void setUp() throws Exception {
		provider = new TestProjectProvider("org.jboss.tools.jsf.ui.test", null, PROJECT_NAME, true); 
		project = provider.getProject();
	}

	protected void tearDown() throws Exception {
		if(provider != null) {
			provider.dispose();
		}
	}
	
	public void testCAForJSFCore(){
		String[] proposals = {
			"<f:ajax"
		};

		checkProposals(PAGE_NAME, "<f:a />", 4, proposals, false);

	}

	/**
	 * In this case, taglib is taken not from jar, but from catalog.
	 */
	public void testCAForJSF22Core(){
		String[] proposals = {
			"<f:ajax"
		};

		checkProposals(PAGE_NAME, "<f:a />", 4, proposals, false);

	}

}
