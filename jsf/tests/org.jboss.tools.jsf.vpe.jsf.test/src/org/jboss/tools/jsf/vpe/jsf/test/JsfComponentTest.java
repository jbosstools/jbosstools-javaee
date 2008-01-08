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

import org.eclipse.ui.PartInitException;
import org.jboss.tools.vpe.ui.test.VpeTest;

/**
 * Class for testing all jsf components
 * 
 * @author sdzmitrovich
 * 
 */
public class JsfComponentTest extends VpeTest {

	//import project name	
	private static final String IMPORT_PROJECT_NAME = "jsfTest"; 
	
	public JsfComponentTest(String name) {
		super(name,IMPORT_PROJECT_NAME,JsfTestPlugin.getPluginResourcePath());
	}

	/*
	 * JSF HTML test cases
	 */

	public void testCommandButton() throws PartInitException, Throwable {
		performTestForJsfComponent("components/commandButton.jsp"); // $NON-NLS-1$
	}

	public void testCommandLink() throws PartInitException, Throwable {
		performTestForJsfComponent("components/commandLink.jsp"); // $NON-NLS-1$
	}

	public void testDataTable() throws PartInitException, Throwable {
		performTestForJsfComponent("components/dataTable.jsp"); // $NON-NLS-1$
	}

	public void testForm() throws PartInitException, Throwable {
		performTestForJsfComponent("components/form.jsp"); // $NON-NLS-1$
	}

	public void testGraphicImage() throws PartInitException, Throwable {
		performTestForJsfComponent("components/graphicImage.jsp"); // $NON-NLS-1$
	}

	public void testInputHidden() throws PartInitException, Throwable {
		performTestForJsfComponent("components/inputHidden.jsp"); // $NON-NLS-1$
	}

	public void testInputSecret() throws PartInitException, Throwable {
		performTestForJsfComponent("components/inputSecret.jsp"); // $NON-NLS-1$
	}

	public void testInputText() throws PartInitException, Throwable {
		performTestForJsfComponent("components/inputText.jsp"); // $NON-NLS-1$
	}

	public void testInputTextArea() throws PartInitException, Throwable {
		performTestForJsfComponent("components/inputTextArea.jsp"); // $NON-NLS-1$
	}

	public void testMessage() throws PartInitException, Throwable {
		performTestForJsfComponent("components/message.jsp"); // $NON-NLS-1$
	}

	public void testMessages() throws PartInitException, Throwable {
		performTestForJsfComponent("components/messages.jsp"); // $NON-NLS-1$
	}

	public void testOutputFormat() throws PartInitException, Throwable {
		performTestForJsfComponent("components/outputFormat.jsp"); // $NON-NLS-1$
	}

	public void testOutputLabel() throws PartInitException, Throwable {
		performTestForJsfComponent("components/outputLabel.jsp"); // $NON-NLS-1$
	}

	public void testOutputLink() throws PartInitException, Throwable {
		performTestForJsfComponent("components/outputLink.jsp"); // $NON-NLS-1$
	}

	public void testOutputText() throws PartInitException, Throwable {
		performTestForJsfComponent("components/outputText.jsp"); // $NON-NLS-1$
	}

	public void testPanelGrid() throws PartInitException, Throwable {
		performTestForJsfComponent("components/panelGrid.jsp"); // $NON-NLS-1$
	}

	public void testPanelGroup() throws PartInitException, Throwable {
		performTestForJsfComponent("components/panelGroup.jsp"); // $NON-NLS-1$
	}

	public void testSelectBooleanCheckbox() throws PartInitException, Throwable {
		performTestForJsfComponent("components/selectBooleanCheckbox.jsp"); // $NON-NLS-1$
	}

	public void testSelectManyCheckbox() throws PartInitException, Throwable {
		performTestForJsfComponent("components/selectManyCheckbox.jsp"); // $NON-NLS-1$
	}

	public void testSelectManyListbox() throws PartInitException, Throwable {
		performTestForJsfComponent("components/selectManyListbox.jsp"); // $NON-NLS-1$
	}

	public void testSelectManyMenu() throws PartInitException, Throwable {
		performTestForJsfComponent("components/selectManyMenu.jsp"); // $NON-NLS-1$
	}

	public void testSelectOneListbox() throws PartInitException, Throwable {
		performTestForJsfComponent("components/selectOneListbox.jsp"); // $NON-NLS-1$
	}

	public void testSelectOneMenu() throws PartInitException, Throwable {
		performTestForJsfComponent("components/selectOneMenu.jsp"); // $NON-NLS-1$
	}

	public void testSelectOneRadio() throws PartInitException, Throwable {
		performTestForJsfComponent("components/selectOneRadio.jsp"); // $NON-NLS-1$
	}

	/*
	 * JSF Core test cases
	 */

	public void testActionListener() throws PartInitException, Throwable {
		performTestForJsfComponent("components/actionListener.jsp"); // $NON-NLS-1$
	}

	public void testAttribute() throws PartInitException, Throwable {
		performTestForJsfComponent("components/attribute.jsp"); // $NON-NLS-1$
	}

	public void testConvertDateTime() throws PartInitException, Throwable {
		performTestForJsfComponent("components/convertDateTime.jsp"); // $NON-NLS-1$
	}

	public void testConvertNumber() throws PartInitException, Throwable {
		performTestForJsfComponent("components/convertNumber.jsp"); // $NON-NLS-1$
	}

	public void testConverter() throws PartInitException, Throwable {
		performTestForJsfComponent("components/converter.jsp"); // $NON-NLS-1$
	}

	public void testFacet() throws PartInitException, Throwable {
		performTestForJsfComponent("components/facet.jsp"); // $NON-NLS-1$
	}

	public void testLoadBundle() throws PartInitException, Throwable {
		performTestForJsfComponent("components/loadBundle.jsp"); // $NON-NLS-1$
	}

	public void testParam() throws PartInitException, Throwable {
		performTestForJsfComponent("components/param.jsp"); // $NON-NLS-1$
	}

	public void testPhaseListener() throws PartInitException, Throwable {
		performTestForJsfComponent("components/phaseListener.jsp"); // $NON-NLS-1$
	}

	public void testSelectItem() throws PartInitException, Throwable {
		performTestForJsfComponent("components/selectItem.jsp"); // $NON-NLS-1$
	}

	public void testSelectItems() throws PartInitException, Throwable {
		performTestForJsfComponent("components/selectItems.jsp"); // $NON-NLS-1$
	}

	public void testSetPropertyActionListener() throws PartInitException,
			Throwable {
		performTestForJsfComponent("components/setPropertyActionListener.jsp"); // $NON-NLS-1$
	}

	public void testSubview() throws PartInitException, Throwable {
		performTestForJsfComponent("components/subview.jsp"); // $NON-NLS-1$
	}

	public void testValidateDoubleRange() throws PartInitException, Throwable {
		performTestForJsfComponent("components/validateDoubleRange.jsp"); // $NON-NLS-1$
	}

	public void testValidateLength() throws PartInitException, Throwable {
		performTestForJsfComponent("components/validateLength.jsp"); // $NON-NLS-1$
	}

	public void testValidateLongRange() throws PartInitException, Throwable {
		performTestForJsfComponent("components/validateLongRange.jsp"); // $NON-NLS-1$
	}

	public void testValidator() throws PartInitException, Throwable {
		performTestForJsfComponent("components/validator.jsp"); // $NON-NLS-1$
	}

	public void testValueChangeListener() throws PartInitException, Throwable {
		performTestForJsfComponent("components/valueChangeListener.jsp"); // $NON-NLS-1$
	}

	public void testVerbatim() throws PartInitException, Throwable {
		performTestForJsfComponent("components/verbatim.jsp"); // $NON-NLS-1$
	}

	public void testView() throws PartInitException, Throwable {
		performTestForJsfComponent("components/view.jsp"); // $NON-NLS-1$
	}
}
