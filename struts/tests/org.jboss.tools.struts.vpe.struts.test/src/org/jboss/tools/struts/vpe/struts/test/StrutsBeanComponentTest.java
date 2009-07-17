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
 * Class for testing bean struts components
 * 
 * @author dazarov
 * 
 */
public class StrutsBeanComponentTest extends VpeTest {

	// import project name
	static final String IMPORT_PROJECT_NAME = "StrutsTest";

	public StrutsBeanComponentTest(String name) {
		super(name);
	}

	/*
	 * Struts Bean test cases
	 */

	public void testCookie() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/bean/cookie.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testDefine() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/bean/define.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testHeader() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/bean/header.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testInclude() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/bean/include.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testMessage() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/bean/message.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testParameter() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/bean/parameter.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testResource() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/bean/resource.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testSize() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/bean/size.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testStruts() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/bean/struts.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testWrite() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/bean/write.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testPage() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/bean/page.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
}
