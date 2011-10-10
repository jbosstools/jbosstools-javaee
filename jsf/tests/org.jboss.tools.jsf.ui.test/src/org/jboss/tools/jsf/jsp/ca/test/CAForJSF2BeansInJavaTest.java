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
package org.jboss.tools.jsf.jsp.ca.test;

import org.jboss.tools.common.base.test.contentassist.JavaContentAssistantTestCase;
import org.jboss.tools.test.util.TestProjectProvider;

public class CAForJSF2BeansInJavaTest extends JavaContentAssistantTestCase{
	TestProjectProvider provider = null;
	boolean makeCopy = true;
	private static final String PROJECT_NAME = "JSF2Beans";
	private static final String PAGE_NAME = "/src/test/beans/Bean1.java";

	@Override
	public void setUp() throws Exception {
		provider = new TestProjectProvider("org.jboss.tools.jsf.test", null, PROJECT_NAME, makeCopy); 
		project = provider.getProject();
	}

	@Override
	protected void tearDown() throws Exception {
		if(provider != null) {
			provider.dispose();
		}
	}

	/**
	 * JBIDE-9362
	 */
	public void testCAForJSF2Beans(){
		String[] proposals = {
			"mybean1 : Bean1", "bean4 : Bean4", "bean5 : Bean5"
		};

		checkProposals(PAGE_NAME, "#{}", 2, proposals, false);
	}
}