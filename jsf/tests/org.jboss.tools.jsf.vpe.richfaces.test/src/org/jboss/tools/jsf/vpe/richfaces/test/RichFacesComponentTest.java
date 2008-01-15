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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMText;

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
		"components/richFacesTest.xhtml", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
    }

    public void testCalendar() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/calendar.xhtml", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
    }

    public void testDataDefinitionList() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/dataDefinitionList.xhtml", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
    }

    public void testDataFilterSlider() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/dataFilterSlider.xhtml", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
    }

    public void testDataGrid() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/dataGrid.xhtml", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
    }

    public void testDataList() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/dataList.xhtml", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
    }

    public void testDataOrderedList() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/dataOrderedList.xhtml", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
    }

    public void testDataScroller() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/dataScroller.xhtml", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
    }

    public void testDataTable() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/dataTable.xhtml", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
    }

    public void testDragAndDrop() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/dragAndDrop.xhtml", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
    }

    public void testDropDawnMenu() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/dropDawnMenu.xhtml", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
    }

    public void testEffect() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/effect.xhtml", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
    }

    public void testGoogleMap() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/googleMap.xhtml", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
    }

    public void testInputNumberSlider() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/inputNumberSlider.xhtml", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
    }

    public void testInputNumberSpinner() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/inputNumberSpinner.xhtml", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
    }

    public void testInsert() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/insert.xhtml", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
    }

    public void testMessage() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/message.xhtml", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
    }

    public void testMessages() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/messages.xhtml", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
    }

    public void testModalPanel() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/modalPanel.xhtml", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
    }

    public void testOrderingList() throws PartInitException, Throwable {
    	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
    			"components/orderingList.xhtml", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
    }
    
    public void testPaint2D() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/paint2D.xhtml", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
    }

    public void testPanel() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/panel.xhtml", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
    }

    public void testPanelBar() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/panelBar.xhtml", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
    }

    public void testPanelMenu() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/panelMenu.xhtml", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
    }

    public void testScrollableDataTable() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/scrollableDataTable.xhtml", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
    }

    public void testSeparator() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/separator.xhtml", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
    }

    public void testSimpleTogglePanel() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/simpleTogglePanel.xhtml", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
    }

    public void testSpacer() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/spacer.xhtml", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
    }

    public void testSuggestionBox() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/suggestionbox.xhtml", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
    }

    public void testTabPanel() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/tabPanel.xhtml", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
    }

    public void testTogglePanel() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/togglePanel.xhtml", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
    }

    public void testToolBar() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/toolBar.xhtml", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
    }

    public void testTree() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/tree.xhtml", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
    }

    public void testVirtualEarth() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/virtualEarth.xhtml", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
    }

    public void testComponentControl() throws PartInitException, Throwable {
	performTestForVpeComponent((IFile) TestUtil.getComponentPath(
		"components/componentControl.xhtml", IMPORT_PROJECT_NAME)); // $NON-NLS-1$
    }

    public void testcontextMenu() throws Throwable {
	nsIDOMElement element = performTestForRichFacesComponent("components/contextMenu.xhtml");

	nsIDOMNode node = (nsIDOMNode) element
		.queryInterface(nsIDOMNode.NS_IDOMNODE_IID);

	List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();

	// find "span" elements
	TestUtil.findElementsByName(node, elements, HTML.TAG_SPAN);

	assertEquals("Component's content is not shown", 4, elements.size());

	nsIDOMElement elementSpan = (nsIDOMElement) elements.get(1)
		.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);

	nsIDOMText text = (nsIDOMText) elementSpan.getFirstChild()
		.queryInterface(nsIDOMText.NS_IDOMTEXT_IID);

	nsIDOMElement elementSpan1 = (nsIDOMElement) elements.get(3)
		.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);

	nsIDOMText text1 = (nsIDOMText) elementSpan1.getFirstChild()
		.queryInterface(nsIDOMText.NS_IDOMTEXT_IID);

	assertEquals("Component's content is not shown", text.getNodeValue(),
		"Zoom In");
	assertEquals("Component's content is not shown", text1.getNodeValue(),
		"Zoom Out");

    }

    /**
     * 
     * @param componentPage
     * @return
     * @throws Throwable
     */
    private nsIDOMElement performTestForRichFacesComponent(String componentPage)
	    throws Throwable {
	TestUtil.waitForJobs();
	// set exception
	setException(null);

	// get test page path
	IFile file = (IFile) TestUtil.getComponentPath(componentPage,
		IMPORT_PROJECT_NAME);

	assertNotNull("Could not open specified file " + file.getFullPath(),
		file);

	IEditorInput input = new FileEditorInput(file);

	assertNotNull("Editor input is null", input);
	// open and get editor
	JSPMultiPageEditor part = openEditor(input);

	// get dom document
	nsIDOMDocument document = getVpeVisualDocument(part);
	nsIDOMElement element = document.getDocumentElement();

	// check that element is not null
	assertNotNull(element);

	return element;
    }

}
