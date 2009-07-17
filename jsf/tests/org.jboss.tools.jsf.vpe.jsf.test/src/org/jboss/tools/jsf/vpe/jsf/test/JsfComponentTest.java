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

import org.eclipse.core.resources.IFile;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;

/**
 * Class for testing all jsf components
 * 
 * @author sdzmitrovich
 * 
 */
public class JsfComponentTest extends VpeTest {

	// import project name
	public static final String IMPORT_PROJECT_NAME = "jsfTest";

	public JsfComponentTest(String name) {
		super(name);
		setCheckWarning(false);
	}

	/*
	 * JSF HTML test cases
	 */

	public void testCommandButton() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("components/commandButton.jsp",IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testCommandLink() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("components/commandLink.jsp",IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testDataTable() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("components/dataTable.jsp",IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testForm() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("components/form.jsp",IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testGraphicImage() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("components/graphicImage.jsp",IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testInputHidden() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("components/inputHidden.jsp",IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testInputSecret() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("components/inputSecret.jsp",IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testInputText() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("components/inputText.jsp",IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testInputTextArea() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("components/inputTextArea.jsp",IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testMessage() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("components/message.jsp",IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testMessages() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("components/messages.jsp",IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testOutputFormat() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("components/outputFormat.jsp",IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testOutputLabel() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("components/outputLabel.jsp",IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testOutputLink() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("components/outputLink.jsp",IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testOutputText() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("components/outputText.jsp",IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testPanelGrid() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("components/panelGrid.jsp",IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testPanelGroup() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("components/panelGroup.jsp",IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testSelectBooleanCheckbox() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("components/selectBooleanCheckbox.jsp",IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testSelectManyCheckbox() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("components/selectManyCheckbox.jsp",IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testSelectManyListbox() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("components/selectManyListbox.jsp",IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testSelectManyMenu() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("components/selectManyMenu.jsp",IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testSelectOneListbox() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("components/selectOneListbox.jsp",IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testSelectOneMenu() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("components/selectOneMenu.jsp",IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testSelectOneRadio() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("components/selectOneRadio.jsp",IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	/*
	 * JSF Core test cases
	 */

	public void testActionListener() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("components/actionListener.jsp",IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testAttribute() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("components/attribute.jsp",IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testConvertDateTime() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("components/convertDateTime.jsp",IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testConvertNumber() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("components/convertNumber.jsp",IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testConverter() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("components/converter.jsp",IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testFacet() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("components/facet.jsp",IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testLoadBundle() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("components/loadBundle.jsp",IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testParam() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("components/param.jsp",IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testPhaseListener() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("components/phaseListener.jsp",IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testSelectItem() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("components/selectItem.jsp",IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testSelectItems() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("components/selectItems.jsp",IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testSetPropertyActionListener() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("components/setPropertyActionListener.jsp",IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testSubview() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("components/subview.jsp",IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testValidateDoubleRange() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("components/validateDoubleRange.jsp",IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testValidateLength() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("components/validateLength.jsp",IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testValidateLongRange() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("components/validateLongRange.jsp",IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testValidator() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("components/validator.jsp",IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testValueChangeListener() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("components/valueChangeListener.jsp",IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testVerbatim() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("components/verbatim.jsp",IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

	public void testView() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath("components/view.jsp",IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
}
