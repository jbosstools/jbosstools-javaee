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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

/**
 * Class for testing all jsf components
 * 
 * @author sdzmitrovich
 * 
 */
public class JsfComponentTest extends TestCase implements ILogListener {

	private final static String EDITOR_ID = "org.jboss.tools.jst.jsp.jspeditor.JSPTextEditor"; // $NON-NLS-1$
	// private final static String TEST_PROJECT_JAR_PATH = "/jsfTest.jar"; //
	// $NON-NLS-1$

	// check warning log
	private final static boolean checkWarning = false;
	private Throwable exception;

	public JsfComponentTest(String name) {
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

		// TODO: Use TestSetup to create and remove project once for all tests
		// not for every one
		// if (ResourcesPlugin.getWorkspace().getRoot().findMember("JsfTest") ==
		// null) {
		//
		// ImportJsfComponents.importJsfPages(JsfTestPlugin
		// .getPluginResourcePath()
		// + TEST_PROJECT_JAR_PATH);
		//
		// waitForJobs();
		// waitForJobs();
		// delay(5000);
		// }
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
		// ImportJsfComponents.removeProject();
		// waitForJobs();
		Platform.removeLogListener(this);
	}

	/*
	 * JSF HTML test cases
	 */

	public void testCommandButton() throws PartInitException, Throwable {
		performTestForJsfComponent("commandButton.jsp"); // $NON-NLS-1$
	}

	public void testCommandLink() throws PartInitException, Throwable {
		performTestForJsfComponent("commandLink.jsp"); // $NON-NLS-1$
	}

	public void testDataTable() throws PartInitException, Throwable {
		performTestForJsfComponent("dataTable.jsp"); // $NON-NLS-1$
	}

	public void testForm() throws PartInitException, Throwable {
		performTestForJsfComponent("form.jsp"); // $NON-NLS-1$
	}

	public void testGraphicImage() throws PartInitException, Throwable {
		performTestForJsfComponent("graphicImage.jsp"); // $NON-NLS-1$
	}

	public void testInputHidden() throws PartInitException, Throwable {
		performTestForJsfComponent("inputHidden.jsp"); // $NON-NLS-1$
	}

	public void testInputSecret() throws PartInitException, Throwable {
		performTestForJsfComponent("inputSecret.jsp"); // $NON-NLS-1$
	}

	public void testInputText() throws PartInitException, Throwable {
		performTestForJsfComponent("inputText.jsp"); // $NON-NLS-1$
	}

	public void testInputTextArea() throws PartInitException, Throwable {
		performTestForJsfComponent("inputTextArea.jsp"); // $NON-NLS-1$
	}

	public void testMessage() throws PartInitException, Throwable {
		performTestForJsfComponent("message.jsp"); // $NON-NLS-1$
	}

	public void testMessages() throws PartInitException, Throwable {
		performTestForJsfComponent("messages.jsp"); // $NON-NLS-1$
	}

	public void testOutputFormat() throws PartInitException, Throwable {
		performTestForJsfComponent("outputFormat.jsp"); // $NON-NLS-1$
	}

	public void testOutputLabel() throws PartInitException, Throwable {
		performTestForJsfComponent("outputLabel.jsp"); // $NON-NLS-1$
	}

	public void testOutputLink() throws PartInitException, Throwable {
		performTestForJsfComponent("outputLink.jsp"); // $NON-NLS-1$
	}

	public void testOutputText() throws PartInitException, Throwable {
		performTestForJsfComponent("outputText.jsp"); // $NON-NLS-1$
	}

	public void testPanelGrid() throws PartInitException, Throwable {
		performTestForJsfComponent("panelGrid.jsp"); // $NON-NLS-1$
	}

	public void testPanelGroup() throws PartInitException, Throwable {
		performTestForJsfComponent("panelGroup.jsp"); // $NON-NLS-1$
	}

	public void testSelectBooleanCheckbox() throws PartInitException, Throwable {
		performTestForJsfComponent("selectBooleanCheckbox.jsp"); // $NON-NLS-1$
	}

	public void testSelectManyCheckbox() throws PartInitException, Throwable {
		performTestForJsfComponent("selectManyCheckbox.jsp"); // $NON-NLS-1$
	}

	public void testSelectManyListbox() throws PartInitException, Throwable {
		performTestForJsfComponent("selectManyListbox.jsp"); // $NON-NLS-1$
	}

	public void testSelectManyMenu() throws PartInitException, Throwable {
		performTestForJsfComponent("selectManyMenu.jsp"); // $NON-NLS-1$
	}

	public void testSelectOneListbox() throws PartInitException, Throwable {
		performTestForJsfComponent("selectOneListbox.jsp"); // $NON-NLS-1$
	}

	public void testSelectOneMenu() throws PartInitException, Throwable {
		performTestForJsfComponent("selectOneMenu.jsp"); // $NON-NLS-1$
	}

	public void testSelectOneRadio() throws PartInitException, Throwable {
		performTestForJsfComponent("selectOneRadio.jsp"); // $NON-NLS-1$
	}

	/*
	 * JSF Core test cases
	 */

	public void testActionListener() throws PartInitException, Throwable {
		performTestForJsfComponent("actionListener.jsp"); // $NON-NLS-1$
	}

	public void testAttribute() throws PartInitException, Throwable {
		performTestForJsfComponent("attribute.jsp"); // $NON-NLS-1$
	}

	public void testConvertDateTime() throws PartInitException, Throwable {
		performTestForJsfComponent("convertDateTime.jsp"); // $NON-NLS-1$
	}

	public void testConvertNumber() throws PartInitException, Throwable {
		performTestForJsfComponent("convertNumber.jsp"); // $NON-NLS-1$
	}

	public void testConverter() throws PartInitException, Throwable {
		performTestForJsfComponent("converter.jsp"); // $NON-NLS-1$
	}

	private void performTestForJsfComponent(String componentPage)
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
	}

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
