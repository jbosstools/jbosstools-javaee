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

import org.jboss.tools.vpe.base.test.ComponentContentTest;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Class for testing all richfaces components
 * 
 * @author sdzmitrovich
 * 
 */
public class RichFacesComponentContentTest extends ComponentContentTest {

	public RichFacesComponentContentTest() {
		setCheckWarning(false);
	}

	@Test
	public void testJBIDE3740() throws Throwable {
		performContentTest("components/panelMenu/JBIDE3740.xhtml"); //$NON-NLS-1$
	}

	@Test
	public void testJbide1605() throws Throwable {
		performContentTest("components/panelMenuGroup/jbide1605.xhtml"); //$NON-NLS-1$
	}

	@Test
	public void testJbide4309() throws Throwable {
		performContentTest("components/inplaceSelect/JBIDE4309.xhtml"); //$NON-NLS-1$
	}

	@Test
	public void testAjaxValidator() throws Throwable {
		performInvisibleTagTest("components/ajaxValidator.xhtml", "ajaxValidator");//$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	public void testGraphValidator() throws Throwable {
		performInvisibleWrapperTagTest("components/graphValidator.xhtml", "graphValidator");//$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	public void testBeanValidator() throws Throwable {
		performInvisibleTagTest("components/beanValidator.xhtml", "beanValidator");//$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	public void testCalendar() throws Throwable {
		performContentTest("components/calendar.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testComboBox() throws Throwable {
		performContentTest("components/comboBox.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testComponentControl() throws Throwable {
		performInvisibleTagTest("components/componentControl.xhtml", "componentControl");//$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	public void testContextMenu() throws Throwable {
		performInvisibleTagTest("components/contextMenu.xhtml", "contextMenu");//$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	public void testDataFilterSlider() throws Throwable {
		performContentTest("components/dataFilterSlider.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testDatascroller() throws Throwable {
		performContentTest("components/datascroller.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testDataScrollerRF4() throws Throwable {
		performContentTest("components/dataScroller-RF4.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testColumns() throws Throwable {
		performContentTest("components/columns.xhtml");//$NON-NLS-1$
	}

	@Test
	@Ignore
	public void _testColumnGroup() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	@Test
	public void testColumn() throws Throwable {
		performContentTest("components/column.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testColumnRF4() throws Throwable {
		performContentTest("components/column-RF4.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testDataGrid() throws Throwable {
		performContentTest("components/dataGrid.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testDataList() throws Throwable {
		performContentTest("components/dataList.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testDataOrderedList() throws Throwable {
		performContentTest("components/dataOrderedList.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testDataDefinitionList() throws Throwable {
		performContentTest("components/dataDefinitionList.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testDataTable() throws Throwable {
		performContentTest("components/dataTable.xhtml");//$NON-NLS-1$
	}

	@Test
	@Ignore
	public void _testSubTable() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	@Test
	public void testDndParam() throws Throwable {
		performInvisibleTagTest("components/dndParam.xhtml", "dndParam"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	public void testDragIndicator() throws Throwable {
		performInvisibleTagTest("components/dragIndicator.xhtml", "dragIndicator"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	public void testDragSupport() throws Throwable {
		performInvisibleTagTest("components/dragSupport.xhtml", "dragSupport"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	public void testDropSupport() throws Throwable {
		performInvisibleTagTest("components/dropSupport.xhtml", "dropSupport"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	public void testDragListener() throws Throwable {
		performInvisibleTagTest("components/dragListener.xhtml", "dragListener"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	public void testClientValidator() throws Throwable {
		performInvisibleTagTest("components/clientValidator.xhtml", "clientValidator"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	public void testDropListener() throws Throwable {
		performInvisibleTagTest("components/dropListener.xhtml", "dropListener"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	public void testDropDownMenu() throws Throwable {
		performContentTest("components/dropDownMenu.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testExtendedDataTable() throws Throwable {
		performContentTest("components/extendedDataTable.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testMenuGroup() throws Throwable {
		performContentTest("components/menuGroup.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testMenuItem() throws Throwable {
		performContentTest("components/menuItem.xhtml");//$NON-NLS-1$
	}
	
	@Test
	@Ignore
	public void _testMenuSeparator() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	@Test
	public void testEffect() throws Throwable {
		performInvisibleTagTest("components/effect.xhtml", "effect1"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	public void testFileUpload() throws Throwable {
		performContentTest("components/fileUpload.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testGmap() throws Throwable {
		performContentTest("components/gmap.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testVirtualEarth() throws Throwable {
		performContentTest("components/virtualEarth.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testHotKey() throws Throwable {
		performInvisibleTagTest("components/hotkey.xhtml", "hotkey"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	public void testInplaceInput() throws Throwable {
		performContentTest("components/inplaceInput.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testInplaceSelect() throws Throwable {
		performContentTest("components/inplaceSelect.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testInputNumberSlider() throws Throwable {
		performContentTest("components/inputNumberSlider.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testInputNumberSpinner() throws Throwable {
		performContentTest("components/inputNumberSpinner.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testInsert() throws Throwable {
		performContentTest("components/insert.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testJQuery() throws Throwable {
		performInvisibleTagTest("components/jQuery.xhtml", "jQuery"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	public void testList() throws Throwable {
		performContentTest("components/list.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testListShuttle() throws Throwable {
		performContentTest("components/listShuttle.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testMessage() throws Throwable {
		performContentTest("components/message.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testMessages() throws Throwable {
		performContentTest("components/messages.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testModalPanel() throws Throwable {
		performInvisibleTagTest("components/modalPanel.xhtml", "modalPanel"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	public void testOrderingList() throws Throwable {
		performContentTest("components/orderingList.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testPaint2D() throws Throwable {
		performContentTest("components/paint2D.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testPanel() throws Throwable {
		performContentTest("components/panel.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testPanelBar() throws Throwable {
		performContentTest("components/panelBar.xhtml");//$NON-NLS-1$
	}

	@Test
	@Ignore
	public void _testPanelBarItem() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	@Test
	public void testPanelMenu() throws Throwable {
		performContentTest("components/panelMenu.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testPanelMenuGroup() throws Throwable {
		performContentTest("components/panelMenuGroup.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testPanelMenuItem() throws Throwable {
		performContentTest("components/panelMenuItem.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testPickList() throws Throwable {
		performContentTest("components/pickList.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testProgressBar() throws Throwable {
		performContentTest("components/progressBar.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testEditor() throws Throwable {
		performContentTest("components/editor.xhtml"); //$NON-NLS-1$
	}

	@Test
	public void testScrollableDataTable() throws Throwable {
		performContentTest("components/scrollableDataTable.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testSeparator() throws Throwable {
		performContentTest("components/separator.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testSimpleTogglePanel() throws Throwable {
		performContentTest("components/simpleTogglePanel.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testSpacer() throws Throwable {
		performContentTest("components/spacer.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testSubTableToggleControl() throws Throwable {
		performContentTest("components/subTableToggleControl.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testSuggestionbox() throws Throwable {
		performInvisibleTagTest("components/suggestionbox.xhtml", "suggestionBox"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	public void testTabPanel() throws Throwable {
		performContentTest("components/tabPanel.xhtml");//$NON-NLS-1$
	}

	@Test
	@Ignore
	public void _testTab() throws Throwable {
		assertTrue("it is necessary to add a body of the test ", false);//$NON-NLS-1$
	}

	@Test
	public void testTogglePanel() throws Throwable {
		performContentTest("components/togglePanel.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testToggleControl() throws Throwable {
		performContentTest("components/toggleControl.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testToolBar() throws Throwable {
		performContentTest("components/toolBar.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testToolBarGroup() throws Throwable {
		performContentTest("components/toolBarGroup.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testToolTip() throws Throwable {
		performInvisibleTagTest("components/toolTip.xhtml", "toolTip"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	public void testTree() throws Throwable {
		performContentTest("components/tree.xhtml");//$NON-NLS-1$
	}
	@Test
	public void testTreeNode() throws Throwable {
		performContentTest("components/treeNode.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testRecursiveTreeNodesAdaptor() throws Throwable {
		performContentTest("components/recursiveTreeNodesAdaptor.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testTreeNodesAdaptor() throws Throwable {
		performContentTest("components/treeNodesAdaptor.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testDataTableSpacerDataScroller() throws Throwable {
		performContentTest("components/dataTable&Spacer&DataScroller.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testPageEmpty() throws Throwable {
		performContentTest("components/page/pageEmpty.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testPageAllFacets() throws Throwable {
		performContentTest("components/page/pageAllFacets.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testPageWithLayout() throws Throwable {
		performContentTest("components/page/pageWithLayout.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testPageWithNestedLayouts() throws Throwable {
		performContentTest("components/page/pageWithNestedLayouts.xhtml");//$NON-NLS-1$
	}
	
	@Test
	public void testPageWithWidths() throws Throwable {
		performContentTest("components/page/pageWithWidths.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testPageWithWidthsPixels() throws Throwable {
		performContentTest("components/page/pageWithWidthsPixels.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testPageWithWidthsEms() throws Throwable {
		performContentTest("components/page/pageWithWidthsEms.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testPageWithWidthsDifferent() throws Throwable {
		performContentTest("components/page/pageWithWidthsDifferent.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testColorPicker() throws Throwable {
		performContentTest("components/colorPicker.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testAutocomplete() throws Throwable {
		performContentTest("components/autocomplete.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testSelect() throws Throwable {
		performContentTest("components/select.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testCollapsiblePanel() throws Throwable {
		performContentTest("components/collapsiblePanel.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testToolBarSeparator() throws Throwable {
		performInvisibleTagTest("components/toolBarSeparator.xhtml", "toolBarSeparator"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	public void testChangeExpandListener() throws Throwable {
		performInvisibleTagTest("components/changeExpandListener.xhtml", "changeExpandListener"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	public void testPopupPanel() throws Throwable {
		performInvisibleTagTest("components/popupPanel.xhtml", "popupPanel"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	public void testAccordion() throws Throwable {
		performContentTest("components/accordion.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testTogglePanelItem() throws Throwable {
		performContentTest("components/togglePanelItem.xhtml");//$NON-NLS-1$
	}

	@Test
	public void testDragSource() throws Throwable {
		performInvisibleTagTest("components/dragSource.xhtml", "dragSource"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	public void testDropTarget() throws Throwable {
		performInvisibleTagTest("components/dropTarget.xhtml", "dropTarget"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	public void testHashParam() throws Throwable {
		performInvisibleTagTest("components/hashParam.xhtml", "hashParam"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	public void testTreeSelectionChangeListener() throws Throwable {
		performInvisibleTagTest("components/treeSelectionChangeListener.xhtml", "treeSelectionChangeListener"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	public void testTreeToggleListener() throws Throwable {
		performInvisibleTagTest("components/treeToggleListener.xhtml", "treeToggleListener"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	public void testValidator() throws Throwable {
		performInvisibleTagTest("components/validator.xhtml", "validator"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	public void testDataTableFacets() throws Throwable {
		performContentTest("JBIDE/5768/dataTable.xhtml"); //$NON-NLS-1$
	}

	@Override
	protected String getTestProjectName() {
		return RichFacesAllTests.IMPORT_PROJECT_NAME;
	}

}
