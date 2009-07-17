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
 * Class for testing form struts components
 * 
 * @author dazarov
 * 
 */
public class StrutsFormComponentTest extends VpeTest {

	// import project name
	static final String IMPORT_PROJECT_NAME = "StrutsTest";

	public StrutsFormComponentTest(String name) {
		super(name);
	}

	/*
	 * Struts Form test cases
	 */

	public void testCancel() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/form/cancel.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testCheckbox() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/form/checkbox.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testFile() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/form/file.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testForm() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/form/form.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testHidden() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/form/hidden.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testMultibox() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/form/multibox.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testOption() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/form/option.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testOptions() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/form/options.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testRadio() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/form/radio.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testReset() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/form/reset.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testSelect() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/form/select.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testSubmit() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/form/submit.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testText() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/form/text.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testButton() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/form/button.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testPassword() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/form/password.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testTextarea() throws Throwable {
		performTestForVpeComponent((IFile)TestUtil.getComponentPath("components/form/textarea.jsp", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
}
