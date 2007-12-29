/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.jsf.test;

import junit.framework.TestCase;

import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.PartInitException;

/**
 * Class for testing all jsf components
 * 
 * @author sdzmitrovich
 * 
 */
public class JsfJbideTest extends TestCase implements ILogListener {

	private final static String EDITOR_ID = "org.jboss.tools.jst.jsp.jspeditor.JSPTextEditor"; // $NON-NLS-1$
	// $NON-NLS-1$

	// check warning log
	private final static boolean checkWarning = false;
	private Throwable exception;

	public JsfJbideTest(String name) {
		super(name);
	}

	/**
	 * Perform pre-test initialization.
	 * 
	 * @throws Exception
	 * 
	 * @see TestCase#setUp()
	 */

	protected void setUp() throws Exception {
		super.setUp();

		Platform.addLogListener(this);
	}

	/**
	 * Perform post-test cleanup.
	 * 
	 * @throws Exception
	 * 
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		Platform.removeLogListener(this);
	}

	/*
	 * JBIDE's test cases
	 */

	public void testJBIDE_1467() throws PartInitException, Throwable {
		String path = "JBIDE/JBIDE-1467.jsp";
			
	}

	/*private void performTestForJsfComponent(String componentPage)
			throws PartInitException, Throwable {
		TestJsfComponentsUtil.waitForJobs();
		
		exception = null;
		IPath componentPath = TestJsfComponentsUtil
				.getComponentPath(componentPage);

		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(
				componentPath);
		IEditorInput input = new FileEditorInput(file);

		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.openEditor(input, EDITOR_ID, true);

		TestJsfComponentsUtil.waitForJobs();
		TestJsfComponentsUtil.delay(3000);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.closeAllEditors(true);

		if (exception != null) {
			throw exception;
		}
	}*/

	public void logging(IStatus status, String plugin) {
		switch (status.getSeverity()) {
		case IStatus.ERROR:
			exception = status.getException();
			break;
		case IStatus.WARNING:
			if (checkWarning)
				exception = status.getException();
			break;
		default:
			break;
		}

	}

}
