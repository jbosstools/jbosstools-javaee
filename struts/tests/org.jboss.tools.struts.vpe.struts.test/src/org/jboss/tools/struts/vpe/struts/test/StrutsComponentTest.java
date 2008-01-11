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

import org.eclipse.core.resources.IFile;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;

/**
 * Class for testing all struts components
 * 
 * @author sdzmitrovich
 * 
 */
public class StrutsComponentTest extends VpeTest {

	// import project name
	private static final String IMPORT_PROJECT_NAME = "StrutsTest";

	public StrutsComponentTest(String name) {
		super(name);
	}

	/*
	 * Struts HTML test cases
	 */

	public void testBase() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/base.jsp", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
	}

	public void testErrors() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/errors.jsp", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
	}

	public void testFrame() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/frame.jsp", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
	}
	
	public void testHtml() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/html.jsp", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
	}
	
	public void testImage() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/image.jsp", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
	}
	
	public void testImg() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/img.jsp", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
	}
	
	public void testLink() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/link.jsp", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
	}
	
	public void testRewrite() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/rewrite.jsp", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
	}
	
	public void testMessages() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/messages.jsp", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
	}






}
