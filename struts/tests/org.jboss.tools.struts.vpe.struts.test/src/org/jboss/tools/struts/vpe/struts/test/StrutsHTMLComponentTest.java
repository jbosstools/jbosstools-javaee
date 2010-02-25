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
 * Class for testing html struts components
 * 
 * @author dazarov
 * 
 */
public class StrutsHTMLComponentTest extends VpeTest {

	// import project name
	static final String IMPORT_PROJECT_NAME = "StrutsTest";

	public StrutsHTMLComponentTest(String name) {
		super(name);
	}

	/*
	 * Struts HTML test cases
	 */

	public void testBase() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/html/base.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testErrors() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/html/errors.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testFrame() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/html/frame.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testHtml() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/html/html.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testImage() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/html/image.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testImg() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/html/img.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testLink() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/html/link.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testRewrite() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/html/rewrite.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
	
	public void testMessages() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/html/messages.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testJavascript() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/html/javascript.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testOptionsCollection() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/html/optionsCollection.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testXHTML() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/html/xhtml.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
}
