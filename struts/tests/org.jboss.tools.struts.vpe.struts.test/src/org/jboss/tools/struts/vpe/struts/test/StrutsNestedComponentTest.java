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
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/nested/nest.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testRoot() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/nested/root.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testSelect() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/nested/select.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testText() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/nested/text.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testWriteNesting() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/nested/writeNesting.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testCheckbox() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/nested/checkbox.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testDefine() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/nested/define.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testEmpty() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/nested/empty.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testEqual() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/nested/equal.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testErrors() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/nested/errors.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testFile() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/nested/file.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testForm() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/nested/form.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testGraterEqual() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/nested/greaterEqual.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testGraterThan() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/nested/greaterThan.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testHidden() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/nested/hidden.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testImage() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/nested/image.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testImg() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/nested/img.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testIterate() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/nested/iterate.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testLessEqual() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/nested/lessEqual.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testLessThan() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/nested/lessThan.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testLink() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/nested/link.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testMatch() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/nested/match.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testMessage() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/nested/message.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testMessages() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/nested/messages.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
}
