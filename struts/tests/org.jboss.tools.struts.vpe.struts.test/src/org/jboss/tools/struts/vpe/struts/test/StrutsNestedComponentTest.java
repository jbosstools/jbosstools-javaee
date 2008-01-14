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
 * Class for testing nested struts components
 * 
 * @author dazarov
 * 
 */
public class StrutsNestedComponentTest extends VpeTest {

	// import project name
	static final String IMPORT_PROJECT_NAME = "StrutsTest";

	public StrutsNestedComponentTest(String name) {
		super(name);
	}

	/*
	 * Struts Nested test cases
	 */

	public void testNest() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/nested/nest.jsp", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
	}

	public void testRoot() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/nested/root.jsp", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
	}

	public void testSelect() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/nested/select.jsp", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
	}

	public void testText() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/nested/text.jsp", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
	}

	public void testWriteNesting() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/nested/writeNesting.jsp", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
	}
}
