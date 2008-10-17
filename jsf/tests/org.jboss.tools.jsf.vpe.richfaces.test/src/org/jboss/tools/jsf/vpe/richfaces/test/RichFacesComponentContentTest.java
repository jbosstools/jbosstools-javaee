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
package org.jboss.tools.jsf.vpe.richfaces.test;

import org.jboss.tools.vpe.ui.test.ComponentContentTest;

/**
 * Class for testing all richfaces components
 * 
 * @author sdzmitrovich
 * 
 */
public class RichFacesComponentContentTest extends ComponentContentTest {

	public RichFacesComponentContentTest(String name) {
		super(name);
		setCheckWarning(false);
	}

	/**
	 * 
	 * @throws Throwable
	 */
	public void testAjaxValidator() throws Throwable {
		performInvisibleTagTest(
				"components/ajaxValidator.xhtml", "ajaxValidator");//$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testGraphValidator() throws Throwable {
		performInvisibleWrapperTagTest(
				"components/graphValidator.xhtml", "graphValidator");//$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testBeanValidator() throws Throwable {
		performInvisibleTagTest(
				"components/beanValidator.xhtml", "beanValidator");//$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testCalendar() throws Throwable {
		performContentTest("components/calendar.xhtml");//$NON-NLS-1$
	}

	public void testComboBox() throws Throwable {
		performContentTest("components/comboBox.xhtml");//$NON-NLS-1$
	}

	public void testComponentControl() throws Throwable {
		performInvisibleTagTest(
				"components/componentControl.xhtml", "componentControl");//$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testContextMenu() throws Throwable {
		performInvisibleTagTest("components/contextMenu.xhtml", "contextMenu");//$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testDataFilterSlider() throws Throwable {
		performContentTest("components/dataFilterSlider.xhtml");//$NON-NLS-1$
	}

	public void testDatascroller() throws Throwable {

		performContentTest("components/datascroller.xhtml");//$NON-NLS-1$
	}

	public void testColumns() throws Throwable {
		performContentTest("components/columns.xhtml");//$NON-NLS-1$
	}

	public void testColumnGroup() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testColumn() throws Throwable {
		performContentTest("components/column.xhtml");//$NON-NLS-1$
	}

	public void testDataGrid() throws Throwable {
		performContentTest("components/dataGrid.xhtml");//$NON-NLS-1$
	}

	public void testDataList() throws Throwable {
		performContentTest("components/dataList.xhtml");//$NON-NLS-1$
	}

	public void testDataOrderedList() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testDataDefinitionList() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testDataTable() throws Throwable {
		performContentTest("components/dataTable.xhtml");//$NON-NLS-1$

	}

	public void testSubTable() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testDndParam() throws Throwable {
		performInvisibleTagTest("components/dndParam.xhtml", "dndParam"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testDragIndicator() throws Throwable {
		performInvisibleTagTest("components/dragIndicator.xhtml", "dragIndicator"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testDragSupport() throws Throwable {
		performInvisibleTagTest("components/dragSupport.xhtml", "dragSupport"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testDropSupport() throws Throwable {
		performInvisibleTagTest("components/dropSupport.xhtml", "dropSupport"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void testDragListener() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testDropListener() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testDropDownMenu() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testExtendedDataTable() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testMenuGroup() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testMenuItem() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testMenuSeparator() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testEffect() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testFileUpload() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testGmap() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testVirtualEarth() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testHotKey() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testInplaceInput() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testInplaceSelect() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testInputNumberSlider() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testInputNumberSpinner() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testInsert() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testJQuery() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testListShuttle() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testMessage() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testMessages() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testModalPanel() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testOrderingList() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testPaint2D() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testPanel() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testPanelBar() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testPanelBarItem() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testPanelMenu() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testPanelMenuGroup() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testPanelMenuItem() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testPickList() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testProgressBar() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testScrollableDataTable() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testSeparator() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testSimpleTogglePanel() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testSpacer() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testSuggestionbox() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testTabPanel() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testTab() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testTogglePanel() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testToggleControl() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testToolBar() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testToolBarGroup() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testToolTip() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testTree() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testTreeNode() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testChangeExpandListener() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testNodeSelectListener() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testRecursiveTreeNodesAdaptor() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	public void testTreeNodesAdaptor() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	protected String getTestProjectName() {
		return RichFacesAllTests.IMPORT_PROJECT_NAME;
	}

}
