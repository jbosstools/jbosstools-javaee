/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.struts.vpe.struts.test;

import org.jboss.tools.vpe.ui.test.VpeTest;

/**
 * Class for testing all struts components
 * 
 * @author dazarov
 * 
 */
public class StrutsComponentTest extends VpeTest {

	// import project name
	private static final String IMPORT_PROJECT_NAME = "StrutsTest";

	public StrutsComponentTest(String name) {
		super(name, IMPORT_PROJECT_NAME, StrutsTestPlugin.getPluginResourcePath());
	}

	/*
	 * Struts HTML test cases
	 */

	public void testBase() throws Throwable {
		performTestForJsfComponent("components/base.jsp"); // $NON-NLS-1$
	}

	public void testErrors() throws Throwable {
		performTestForJsfComponent("components/errors.jsp"); // $NON-NLS-1$
	}

	public void testFrame() throws Throwable {
		performTestForJsfComponent("components/frame.jsp"); // $NON-NLS-1$
	}
	
	public void testHtml() throws Throwable {
		performTestForJsfComponent("components/html.jsp"); // $NON-NLS-1$
	}
	
	public void testImage() throws Throwable {
		performTestForJsfComponent("components/image.jsp"); // $NON-NLS-1$
	}
	
	public void testImg() throws Throwable {
		performTestForJsfComponent("components/img.jsp"); // $NON-NLS-1$
	}
	
	public void testLink() throws Throwable {
		performTestForJsfComponent("components/link.jsp"); // $NON-NLS-1$
	}
	
	public void testRewrite() throws Throwable {
		performTestForJsfComponent("components/rewrite.jsp"); // $NON-NLS-1$
	}
	
	public void testMessages() throws Throwable {
		performTestForJsfComponent("components/messages.jsp"); // $NON-NLS-1$
	}






}
