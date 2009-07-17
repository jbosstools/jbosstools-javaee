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
 * Class for testing logic struts components
 * 
 * @author dazarov
 * 
 */
public class StrutsLogicComponentTest extends VpeTest {

	// import project name
	static final String IMPORT_PROJECT_NAME = "StrutsTest";

	public StrutsLogicComponentTest(String name) {
		super(name);
	}

	/*
	 * Struts Logic test cases
	 */

	public void testEmpty() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/logic/empty.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testNotEmpty() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/logic/notEmpty.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testEqual() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/logic/equal.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testNotEqual() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/logic/notEqual.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testGreaterEqual() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/logic/greaterEqual.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testGreaterThan() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/logic/greaterThan.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testIterate() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/logic/iterate.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testLessEqual() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/logic/lessEqual.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testLessThan() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/logic/lessThan.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testMatch() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/logic/match.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testNotMatch() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/logic/notMatch.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testForward() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/logic/forward.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testMessagesNotPresent() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/logic/messagesNotPresent.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testMessagesPresent() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/logic/messagesPresent.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testNotPresent() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/logic/notPresent.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testPresent() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/logic/present.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testRedirect() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/logic/redirect.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
}
