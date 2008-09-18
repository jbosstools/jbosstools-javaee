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
package org.jboss.tools.jsf.vpe.jsf.test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.jboss.tools.vpe.ui.test.ComponentContentTest;

/**
 * Class for testing all jsf components
 * 
 * @author sdzmitrovich
 * 
 */
public class JsfComponentContentTest extends ComponentContentTest {

	// import project name
	public static final String IMPORT_PROJECT_NAME = "jsfTest"; //$NON-NLS-1$

	public static final String TESTS_ROOT_NAME = "tests"; //$NON-NLS-1$

	public JsfComponentContentTest(String name) {
		super(name);
		setCheckWarning(false);
	}

	public void testCommandButton() throws Throwable {

		Map<String, String> mapIds = new HashMap<String, String>();
		mapIds.put("commandButton1", "commandButton1");
		mapIds.put("commandButton2", "commandButton2");
		mapIds.put("commandButton3", "commandButton3");
		mapIds.put("commandButton4", "commandButton4");
		mapIds.put("commandButton5", "commandButton5");
		performContentTest("components/commandButton.jsp", //$NON-NLS-1$ 
				"commandButton.xml", mapIds); //$NON-NLS-1$
	}

	public void testCommandLink() throws Throwable {
		Map<String, String> mapIds = new HashMap<String, String>();
		mapIds.put("commandLink1", "commandLink1");
		mapIds.put("commandLink2", "commandLink2");
		performContentTest("components/commandLink.jsp", //$NON-NLS-1$ 
				"commandLink.xml", mapIds); //$NON-NLS-1$
	}

	public void testDataTable() throws Throwable {
		performContentTest("components/dataTable.jsp", "dataTable",
				"dataTable.xml");
	}

	public void testForm() throws Throwable {
		performContentTest("components/form.jsp", "form", "form.xml");
	}

	public void testGraphicImage() throws Throwable {
		Map<String, String> mapIds = new HashMap<String, String>();
		mapIds.put("graphicImage1", "graphicImage1");
		mapIds.put("graphicImage2", "graphicImage2");
		performContentTest("components/graphicImage.jsp", //$NON-NLS-1$ 
				"graphicImage.xml", mapIds); //$NON-NLS-1$
	}

	public void testInputHidden() throws Throwable {
		assertTrue("it is necessery to add body of the test", false);
	}

	public void testInputSecret() throws Throwable {
		performContentTest("components/inputSecret.jsp", "inputSecret",
				"inputSecret.xml");
	}

	public void testInputText() throws Throwable {

		performContentTest("components/inputText.jsp", "inputText", //$NON-NLS-1$ //$NON-NLS-2$
				"inputText.xml"); //$NON-NLS-1$

	}

	public void testInputTextArea() throws Throwable {
		performContentTest("components/inputTextArea.jsp", "inputTextArea", //$NON-NLS-1$ //$NON-NLS-2$
				"inputTextArea.xml"); //$NON-NLS-1$
	}

	public void testMessage() throws Throwable {
		performContentTest("components/message.jsp", "message", //$NON-NLS-1$ //$NON-NLS-2$
				"message.xml"); //$NON-NLS-1$
	}

	public void testMessages() throws Throwable {
		performContentTest("components/messages.jsp", "messages", //$NON-NLS-1$ //$NON-NLS-2$
		"messages.xml"); //$NON-NLS-1$
	}

	public void testOutputFormat() throws Throwable {
		assertTrue("it is necessery to add body of the test", false);
	}

	public void testOutputLabel() throws Throwable {
		assertTrue("it is necessery to add body of the test", false);
	}

	public void testOutputLink() throws Throwable {
		assertTrue("it is necessery to add body of the test", false);
	}

	public void testOutputText() throws Throwable {
		assertTrue("it is necessery to add body of the test", false);
	}

	public void testPanelGrid() throws Throwable {
		assertTrue("it is necessery to add body of the test", false);
	}

	public void testPanelGroup() throws Throwable {
		assertTrue("it is necessery to add body of the test", false);
	}

	public void testSelectBooleanCheckbox() throws Throwable {
		assertTrue("it is necessery to add body of the test", false);
	}

	public void testSelectManyCheckbox() throws Throwable {
		assertTrue("it is necessery to add body of the test", false);
	}

	public void testSelectManyListbox() throws Throwable {
		assertTrue("it is necessery to add body of the test", false);
	}

	public void testSelectManyMenu() throws Throwable {
		assertTrue("it is necessery to add body of the test", false);
	}

	public void testSelectOneListbox() throws Throwable {
		assertTrue("it is necessery to add body of the test", false);
	}

	public void testSelectOneMenu() throws Throwable {
		assertTrue("it is necessery to add body of the test", false);
	}

	public void _testSelectOneRadio() throws Throwable {
		assertTrue("it is necessery to add body of the test", false);
	}

	/*
	 * JSF Core test cases
	 */

	public void testActionListener() throws Throwable {
		assertTrue("it is necessery to add body of the test", false);
	}

	public void testAttribute() throws Throwable {
		assertTrue("it is necessery to add body of the test", false);
	}

	public void testConvertDateTime() throws Throwable {
		assertTrue("it is necessery to add body of the test", false);
	}

	public void testConvertNumber() throws Throwable {
		assertTrue("it is necessery to add body of the test", false);
	}

	public void testConverter() throws Throwable {
		assertTrue("it is necessery to add body of the test", false);
	}

	public void testFacet() throws Throwable {
		assertTrue("it is necessery to add body of the test", false);
	}

	public void testLoadBundle() throws Throwable {
		assertTrue("it is necessery to add body of the test", false);
	}

	public void testParam() throws Throwable {
		assertTrue("it is necessery to add body of the test", false);
	}

	public void testPhaseListener() throws Throwable {
		assertTrue("it is necessery to add body of the test", false);
	}

	public void testSelectItem() throws Throwable {
		assertTrue("it is necessery to add body of the test", false);
	}

	public void testSelectItems() throws Throwable {
		assertTrue("it is necessery to add body of the test", false);
	}

	public void testSetPropertyActionListener() throws Throwable {
		assertTrue("it is necessery to add body of the test", false);
	}

	public void testSubview() throws Throwable {
		assertTrue("it is necessery to add body of the test", false);
	}

	public void testValidateDoubleRange() throws Throwable {
		assertTrue("it is necessery to add body of the test", false);
	}

	public void testValidateLength() throws Throwable {
		assertTrue("it is necessery to add body of the test", false);
	}

	public void testValidateLongRange() throws Throwable {
		assertTrue("it is necessery to add body of the test", false);
	}

	public void testValidator() throws Throwable {
		assertTrue("it is necessery to add body of the test", false);
	}

	public void testValueChangeListener() throws Throwable {
		assertTrue("it is necessery to add body of the test", false);
	}

	public void testVerbatim() throws Throwable {
		assertTrue("it is necessery to add body of the test", false);
	}

	public void testView() throws Throwable {
		assertTrue("it is necessery to add body of the test", false);
	}

	protected String getTestProjectName() {
		return IMPORT_PROJECT_NAME;
	}

	@Override
	protected String getTestsRoot() {
		return JsfTestPlugin.getPluginResourcePath() + File.separator
				+ TESTS_ROOT_NAME;
	}
}
