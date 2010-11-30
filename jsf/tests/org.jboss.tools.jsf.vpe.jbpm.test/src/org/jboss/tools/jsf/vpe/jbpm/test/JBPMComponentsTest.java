/******************************************************************************* 
 * Copyright (c) 2007-2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.jbpm.test;

import org.jboss.tools.vpe.base.test.ComponentContentTest;

/**
 * 
 * @author yzhishko
 *
 */

public class JBPMComponentsTest extends ComponentContentTest {

	public JBPMComponentsTest(String name) {
		super(name);
		setCheckWarning(false);
	}

	public void testCancelButton() throws Throwable{
		performContentTest("components/cancelButton.xhtml"); //$NON-NLS-1$
	}

	public void testSaveButton() throws Throwable{
		performContentTest("components/saveButton.xhtml"); //$NON-NLS-1$
	}
	
	public void testTransitionButton() throws Throwable{
		performContentTest("components/transitionButton.xhtml"); //$NON-NLS-1$
	}
	
	public void testTaskForm() throws Throwable{
		performContentTest("components/taskForm.xhtml"); //$NON-NLS-1$
	}

	@Override
	protected String getTestProjectName() {
		return JbpmVisualAllTests.JBPM_TEST_PROJECT;
	}

}
