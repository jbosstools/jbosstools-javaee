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

import org.jboss.tools.vpe.base.test.ComponentContentTest;

/**
 * Class for testing all jsf components
 * 
 * @author sdzmitrovich
 * 
 */
public class JsfComponentContentTest extends ComponentContentTest {

	public JsfComponentContentTest(String name) {
		super(name);
		setCheckWarning(false);
	}

	public void testCommandButton() throws Throwable {
		performContentTest("components/commandButton.jsp"); //$NON-NLS-1$
	}

	public void testCommandLink() throws Throwable {
		performContentTest("components/commandLink.jsp"); //$NON-NLS-1$
	}

	public void testDataTable() throws Throwable {
		performContentTest("components/dataTable.jsp"); //$NON-NLS-1$
	}

	public void testForm() throws Throwable {
		performContentTest("components/form.jsp"); //$NON-NLS-1$
	}

	public void testGraphicImage() throws Throwable {
		performContentTest("components/graphicImage.jsp"); //$NON-NLS-1$
	}

	public void testInputHidden() throws Throwable {
		performInvisibleTagTest(
				"components/inputHidden.jsp", "inputHidden"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testInputSecret() throws Throwable {
		performContentTest("components/inputSecret.jsp"); //$NON-NLS-1$
	}

	public void testInputText() throws Throwable {
		performContentTest("components/inputText.jsp"); //$NON-NLS-1$

	}

	public void testInputTextArea() throws Throwable {
		performContentTest("components/inputTextArea.jsp"); //$NON-NLS-1$
	}

	public void testMessage() throws Throwable {
		performContentTest("components/message.jsp"); //$NON-NLS-1$
	}

	public void testMessages() throws Throwable {
		performContentTest("components/messages.jsp"); //$NON-NLS-1$
	}

	public void testOutputFormat() throws Throwable {
		performContentTest("components/outputFormat.jsp"); //$NON-NLS-1$
	}

	public void testOutputLabel() throws Throwable {
		performContentTest("components/outputLabel.jsp"); //$NON-NLS-1$
	}

	public void testOutputLink() throws Throwable {
		performContentTest("components/outputLink.jsp"); //$NON-NLS-1$
	}

	public void testOutputText() throws Throwable {
		performContentTest("components/outputText.jsp"); //$NON-NLS-1$
	}

	public void testPanelGrid() throws Throwable {
		performContentTest("components/panelGrid.jsp"); //$NON-NLS-1$
	}

	public void testPanelGroup() throws Throwable {
		performContentTest("components/panelGroup.jsp"); //$NON-NLS-1$
	}

	public void testSelectBooleanCheckbox() throws Throwable {
		performContentTest("components/selectBooleanCheckbox.jsp"); //$NON-NLS-1$
	}

	public void testSelectManyCheckbox() throws Throwable {
		performContentTest("components/selectManyCheckbox.jsp"); //$NON-NLS-1$
	}

	public void testSelectManyListbox() throws Throwable {
		performContentTest("components/selectManyListbox.jsp"); //$NON-NLS-1$
	}

	public void testSelectManyMenu() throws Throwable {
		performContentTest("components/selectManyMenu.jsp"); //$NON-NLS-1$
	}

	public void testSelectOneListbox() throws Throwable {
		performContentTest("components/selectOneListbox.jsp"); //$NON-NLS-1$
	}

	public void testSelectOneMenu() throws Throwable {
		performContentTest("components/selectOneMenu.jsp"); //$NON-NLS-1$
	}

	public void testSelectOneRadio() throws Throwable {
		performContentTest("components/selectOneRadio.jsp"); //$NON-NLS-1$
	}

	public void testActionListener() throws Throwable {
		performInvisibleTagTest(
				"components/actionListener.jsp", "actionListener"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testAttribute() throws Throwable {
		performInvisibleTagTest("components/attribute.jsp", "attribute"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testConvertDateTime() throws Throwable {
		performInvisibleTagTest(
				"components/convertDateTime.jsp", "convertDateTime"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testConvertNumber() throws Throwable {
		performInvisibleTagTest("components/convertNumber.jsp", "convertNumber"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testConverter() throws Throwable {
		performInvisibleTagTest("components/converter.jsp", "converter"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testFacet() throws Throwable {
		performContentTest("components/facet.jsp"); //$NON-NLS-1$
	}

	public void testLoadBundle() throws Throwable {
		performInvisibleTagTest("components/loadBundle.jsp", "loadBundle"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testParam() throws Throwable {
		performInvisibleTagTest("components/param.jsp", "param"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testPhaseListener() throws Throwable {
		performInvisibleTagTest("components/phaseListener.jsp", "phaseListener"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testSelectItem() throws Throwable {
		performContentTest("components/selectItem.jsp"); //$NON-NLS-1$
	}

	public void testSelectItems() throws Throwable {
		performContentTest("components/selectItems.jsp"); //$NON-NLS-1$
	}

	public void testSetPropertyActionListener() throws Throwable {
		performInvisibleTagTest(
				"components/setPropertyActionListener.jsp", "setPropertyActionListener"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testSubview() throws Throwable {
		performContentTest("components/subview.jsp"); //$NON-NLS-1$

	}

	public void testValidateDoubleRange() throws Throwable {
		performInvisibleTagTest(
				"components/validateDoubleRange.jsp", "validateDoubleRange"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testValidateLength() throws Throwable {
		performInvisibleTagTest(
				"components/validateLength.jsp", "validateLength"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testValidateLongRange() throws Throwable {
		performInvisibleTagTest(
				"components/validateLongRange.jsp", "validateLongRange"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testValidator() throws Throwable {
		performInvisibleTagTest("components/validator.jsp", "validator"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testValueChangeListener() throws Throwable {
		performInvisibleTagTest(
				"components/valueChangeListener.jsp", "valueChangeListener"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testVerbatim() throws Throwable {
		performContentTest("components/verbatim.jsp"); //$NON-NLS-1$
	}

	public void testView() throws Throwable {
		performContentTest("components/view.jsp"); //$NON-NLS-1$
	}

	@Override
	protected String getTestProjectName() {
		return JsfAllTests.IMPORT_PROJECT_NAME;
	}

}
