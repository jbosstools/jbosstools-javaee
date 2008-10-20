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
package org.jboss.tools.jsf.vpe.richfaces.test;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.PartInitException;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;

/**
 * Class for testing all RichFaces components
 * 
 * @author dsakovich@exadel.com
 * 
 */
public class RichFacesComponentTest extends VpeTest {

    public RichFacesComponentTest(String name) {
	super(name);
	setCheckWarning(false);
    }

    public void testAllComponentsOnSinglePage() throws PartInitException,
	    Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/richFacesTest.xhtml", RichFacesAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testCalendar() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/calendar.xhtml", RichFacesAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testDataDefinitionList() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/dataDefinitionList.xhtml", RichFacesAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testDataFilterSlider() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/dataFilterSlider.xhtml", RichFacesAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testDataGrid() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/dataGrid.xhtml", RichFacesAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testDataList() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/dataList.xhtml", RichFacesAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testDataOrderedList() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/dataOrderedList.xhtml", RichFacesAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testDataScroller() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/datascroller.xhtml", RichFacesAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testDataTable() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/dataTable.xhtml", RichFacesAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testDragAndDrop() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/dragAndDrop.xhtml", RichFacesAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testDropDownMenu() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/dropDownMenu.xhtml", RichFacesAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testEffect() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/effect.xhtml", RichFacesAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testGoogleMap() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/googleMap.xhtml", RichFacesAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testInputNumberSlider() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/inputNumberSlider.xhtml", RichFacesAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testInputNumberSpinner() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/inputNumberSpinner.xhtml", RichFacesAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testInsert() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/insert.xhtml", RichFacesAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testMessage() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/message.xhtml", RichFacesAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testMessages() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/messages.xhtml", RichFacesAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testModalPanel() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/modalPanel.xhtml", RichFacesAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testOrderingList() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/orderingList.xhtml", RichFacesAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testPaint2D() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/paint2D.xhtml", RichFacesAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testPanel() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/panel.xhtml", RichFacesAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testPanelBar() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/panelBar.xhtml", RichFacesAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testPanelMenu() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/panelMenu.xhtml", RichFacesAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testScrollableDataTable() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/scrollableDataTable.xhtml", RichFacesAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testSeparator() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/separator.xhtml", RichFacesAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testSimpleTogglePanel() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/simpleTogglePanel.xhtml", RichFacesAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testSpacer() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/spacer.xhtml", RichFacesAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testSuggestionBox() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/suggestionbox.xhtml", RichFacesAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testTabPanel() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/tabPanel.xhtml", RichFacesAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testTogglePanel() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/togglePanel.xhtml", RichFacesAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testToolBar() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/toolBar.xhtml", RichFacesAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testTree() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/tree.xhtml", RichFacesAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testVirtualEarth() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/virtualEarth.xhtml", RichFacesAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testComponentControl() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/componentControl.xhtml", RichFacesAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testContextMenu() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/contextMenu.xhtml", RichFacesAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }
    
    public void testListShuttle() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(
				"components/listShuttle.xhtml", RichFacesAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}

    public void testSubTable() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(
				"components/subTable.xhtml", RichFacesAllTests.IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
}
