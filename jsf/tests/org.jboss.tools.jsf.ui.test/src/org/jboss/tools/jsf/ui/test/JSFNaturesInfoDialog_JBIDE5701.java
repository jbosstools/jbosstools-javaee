/*******************************************************************************
 * Copyright (c) 2007-2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.jsf.ui.test;

/**
 * On fixing JBIDE-9427, this case suggests adding KB nature instead of JSF nature. 
 * 
 * @author yzhishko
 *
 */

public class JSFNaturesInfoDialog_JBIDE5701 extends NaturesInfoDialogTest {

	private static final String testProjectName = "naturesCheckTest"; //$NON-NLS-1$
	private static final String TEST_STRING = "The project \"" + testProjectName + "\" does not have JSF code completion and validation enabled completely."
	+ "\n\nPlease use \"Enable JSF Code Completion...\" fix button if you want these features working.";
	
	public JSFNaturesInfoDialog_JBIDE5701(String name) {
		super(name);
	}

	
	public void testJSFNaturesChecker() throws Throwable {
		
		ResultObject resultObject = startCheckerThread();
		
		openPage(getTestProjectName(), TEST_PAGE_NAME);
		
		if ("".equals(resultObject.getShellName()) && "".equals(resultObject.getTextLabel())) { //$NON-NLS-1$ //$NON-NLS-2$
			throw new Exception("Project natures checker dialog hasn't appeared :(("); //$NON-NLS-1$
		}
		
		assertEquals(TEST_SHELL_NAME, resultObject.getShellName());
		assertEquals(getDialogMessage(), resultObject.getTextLabel());
	
	}

	@Override
	protected String getDialogMessage() {
		return TEST_STRING;
	}

	@Override
	protected String getTestProjectName() {
		return testProjectName;
	}
	
}