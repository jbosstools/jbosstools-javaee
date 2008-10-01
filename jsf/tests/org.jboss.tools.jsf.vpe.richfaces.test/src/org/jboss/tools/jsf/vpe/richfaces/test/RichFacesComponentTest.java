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

    // import project name
    public static final String IMPORT_PROJECT_NAME = "richFacesTest";

    public RichFacesComponentTest(String name) {
	super(name);
	setCheckWarning(false);
    }

    public void testAllComponentsOnSinglePage() throws PartInitException,
	    Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/richFacesTest.xhtml", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testCalendar() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/calendar.xhtml", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testDataDefinitionList() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/dataDefinitionList.xhtml", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testDataFilterSlider() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/dataFilterSlider.xhtml", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testDataGrid() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/dataGrid.xhtml", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testDataList() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/dataList.xhtml", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testDataOrderedList() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/dataOrderedList.xhtml", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testDataScroller() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/dataScroller.xhtml", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testDataTable() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/dataTable.xhtml", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testDragAndDrop() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/dragAndDrop.xhtml", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testDropDawnMenu() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/dropDawnMenu.xhtml", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testEffect() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/effect.xhtml", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testGoogleMap() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/googleMap.xhtml", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testInputNumberSlider() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/inputNumberSlider.xhtml", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testInputNumberSpinner() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/inputNumberSpinner.xhtml", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testInsert() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/insert.xhtml", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testMessage() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/message.xhtml", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testMessages() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/messages.xhtml", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testModalPanel() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/modalPanel.xhtml", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testOrderingList() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/orderingList.xhtml", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testPaint2D() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/paint2D.xhtml", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testPanel() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/panel.xhtml", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testPanelBar() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/panelBar.xhtml", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testPanelMenu() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/panelMenu.xhtml", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testScrollableDataTable() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/scrollableDataTable.xhtml", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testSeparator() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/separator.xhtml", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testSimpleTogglePanel() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/simpleTogglePanel.xhtml", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testSpacer() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/spacer.xhtml", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testSuggestionBox() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/suggestionbox.xhtml", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testTabPanel() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/tabPanel.xhtml", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testTogglePanel() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/togglePanel.xhtml", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testToolBar() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/toolBar.xhtml", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testTree() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/tree.xhtml", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testVirtualEarth() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/virtualEarth.xhtml", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testComponentControl() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/componentControl.xhtml", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }

    public void testContextMenu() throws Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/contextMenu.xhtml", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
    }
    
    public void testListShuttle() throws Throwable {
		performTestForVpeComponent((IFile) TestUtil.getComponentPath(
				"components/listShuttle.xhtml", IMPORT_PROJECT_NAME)); //$NON-NLS-1$
	}
}
