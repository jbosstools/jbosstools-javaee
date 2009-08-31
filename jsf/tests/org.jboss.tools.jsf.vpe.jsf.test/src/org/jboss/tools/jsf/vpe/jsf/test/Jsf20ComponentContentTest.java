/*******************************************************************************
  * Copyright (c) 2007-2008 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.jsf.vpe.jsf.test;

import org.jboss.tools.vpe.ui.test.ComponentContentTest;

/**
 * Performs tests for JavaServer Faces 2.0
 * 
 * @author yradtsevich
 *
 */
public class Jsf20ComponentContentTest extends ComponentContentTest {

	public Jsf20ComponentContentTest(String name) {
		super(name);
		setCheckWarning(false);
	}

	public void testBody1() throws Throwable {
		performContentTest("components/body1.xhtml"); //$NON-NLS-1$
	}
	
	public void testBody2() throws Throwable {
		performContentTest("components/body2.xhtml"); //$NON-NLS-1$
	}

	public void testHead1() throws Throwable {
		performContentTest("components/head1.xhtml"); //$NON-NLS-1$
	}
	
	public void testHead2() throws Throwable {
		performContentTest("components/head2.xhtml"); //$NON-NLS-1$
	}
	
	public void testOutputScript() throws Throwable {
		performInvisibleTagTest("components/outputScript.xhtml", "outputScript1"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public void testOutputStylesheet() throws Throwable {
		performInvisibleTagTest("components/outputStylesheet.xhtml", "outputStylesheetBlue"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	@Override
	protected String getTestProjectName() {
		return JsfAllTests.IMPORT_JSF_20_PROJECT_NAME;
	}
}
