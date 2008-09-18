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
		performContentTest("components/dataTable.jsp", "dataTable", "dataTable.xml");
	}

	public void testForm() throws Throwable {
		performContentTest("components/form.jsp", "form", "form.xml");
	}

	public void _testGraphicImage() throws Throwable {
		// -1$
	}

	public void _testInputHidden() throws Throwable {
	}

	public void _testInputSecret() throws Throwable {
	}

	public void testInputText() throws Throwable {

		performContentTest("components/inputText.jsp", "inputText", //$NON-NLS-1$ //$NON-NLS-2$
				"inputText.xml"); //$NON-NLS-1$

	}

	public void _testInputTextArea() throws Throwable {
	}

	public void _testMessage() throws Throwable {
	}

	public void _testMessages() throws Throwable {
	}

	public void _testOutputFormat() throws Throwable {
	}

	public void _testOutputLabel() throws Throwable {
	}

	public void _testOutputLink() throws Throwable {
	}

	public void _testOutputText() throws Throwable {
	}

	public void _testPanelGrid() throws Throwable {
	}

	public void _testPanelGroup() throws Throwable {
	}

	public void _testSelectBooleanCheckbox() throws Throwable {
	}

	public void _testSelectManyCheckbox() throws Throwable {
	}

	public void _testSelectManyListbox() throws Throwable {
	}

	public void _testSelectManyMenu() throws Throwable {
	}

	public void _testSelectOneListbox() throws Throwable {
	}

	public void _testSelectOneMenu() throws Throwable {
	}

	public void _testSelectOneRadio() throws Throwable {
	}

	/*
	 * JSF Core test cases
	 */

	public void _testActionListener() throws Throwable {
	}

	public void _testAttribute() throws Throwable {
	}

	public void _testConvertDateTime() throws Throwable {
	}

	public void _testConvertNumber() throws Throwable {
	}

	public void _testConverter() throws Throwable {
	}

	public void _testFacet() throws Throwable {
	}

	public void _testLoadBundle() throws Throwable {
	}

	public void _testParam() throws Throwable {
	}

	public void _testPhaseListener() throws Throwable {
	}

	public void _testSelectItem() throws Throwable {
	}

	public void _testSelectItems() throws Throwable {
	}

	public void _testSetPropertyActionListener() throws Throwable {
	}

	public void _testSubview() throws Throwable {
	}

	public void _testValidateDoubleRange() throws Throwable {
	}

	public void _testValidateLength() throws Throwable {
	}

	public void _testValidateLongRange() throws Throwable {
	}

	public void _testValidator() throws Throwable {
	}

	public void _testValueChangeListener() throws Throwable {
	}

	public void _testVerbatim() throws Throwable {
	}

	public void _testView() throws Throwable {
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
