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
package org.jboss.tools.jsf.vpe.richfaces.test.jbide;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;

/**
 * Test JBIDE-1713
 * 
 * @author Dzmitry Sakovich (dsakovich@exadel.com)
 * 
 */
public class JBIDE1713Test extends VpeTest {

	public static final String IMPORT_PROJECT_NAME = "richFacesTest";

	private static final String TEST_PAGE_NAME = "JBIDE/1713/JBIDE-1713.xhtml";

	public JBIDE1713Test(String name) {
		super(name);
	}

	// test method for JBIDE 1713 component
	public void testJBIDE_1713() throws Throwable {
		// wait
		TestUtil.waitForJobs();
		// set exception
		setException(null);

		// get test page path
		IFile file = (IFile) TestUtil.getComponentPath(TEST_PAGE_NAME,
				IMPORT_PROJECT_NAME);

		assertNotNull("Could not open specified file. componentPage = " + TEST_PAGE_NAME //$NON-NLS-1$
				+ ";projectName = " + IMPORT_PROJECT_NAME, file); //$NON-NLS-1$

		IEditorInput input = new FileEditorInput(file);

		assertNotNull("Editor input is null", input);
		// open and get editor
		JSPMultiPageEditor part = openEditor(input);
		// get dom document
		nsIDOMDocument document = TestUtil.getVpeVisualDocument(part);
		nsIDOMElement element = document.getDocumentElement();

		// check that element is not null
		assertNotNull(element);

		// get root node
		nsIDOMNode node = (nsIDOMNode) element
				.queryInterface(nsIDOMNode.NS_IDOMNODE_IID);

		List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();

		// find "table" elements
		TestUtil.findElementsByName(node, elements, HTML.TAG_TABLE);

		assertEquals(1, elements.size());

		nsIDOMElement table = (nsIDOMElement) elements.get(0).queryInterface(
				nsIDOMElement.NS_IDOMELEMENT_IID);

		assertNotNull(table);

		// Check applying styleClass
		String styleClass = table.getAttribute(HTML.ATTR_CLASS);
		assertNotNull("styleClass attribute not apply", styleClass);
		assertEquals("dr-pnlbar rich-panelbar dr-pnlbar-b myClass", styleClass);

		// Check applying style
		String stylePanel = table.getAttribute(HTML.ATTR_STYLE);
		assertNotNull("style attribute not apply", stylePanel);
		assertEquals(
				"padding: 0px; height: 207px; width: 453px; font-weight: bold;",
				stylePanel);

		elements.clear();

		TestUtil.findAllElementsByName(node, elements, HTML.TAG_DIV);

		assertEquals(10, elements.size());

		nsIDOMElement activeToggle = (nsIDOMElement) elements.get(5)
				.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);

		assertNotNull(activeToggle);
		String activeToggleClass = activeToggle.getAttribute(HTML.ATTR_CLASS);
		assertNotNull(activeToggleClass);
		assertEquals(
				"dr-pnlbar-h rich-panelbar-header myHeaderStyle1 myHeaderStyle myHeaderStyleActive1 myHeaderStyleActive",
				activeToggleClass);

		String activeToggleStyle = activeToggle.getAttribute(HTML.ATTR_STYLE);
		assertNotNull(activeToggleStyle);
		assertEquals(
				"background: red none repeat scroll 0% 0%; color: blue; -moz-background-clip: border; -moz-background-origin: padding; -moz-background-inline-policy: continuous;",
				activeToggleStyle);

		// check active content
		List<nsIDOMNode> contentElements = new ArrayList<nsIDOMNode>();
		TestUtil.findAllElementsByName(node, contentElements, HTML.TAG_TD);

		assertEquals(2, contentElements.size());

		nsIDOMElement contentElement = (nsIDOMElement) contentElements.get(1)
				.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);

		assertNotNull(contentElement);

		String activeContentStyle = contentElement
				.getAttribute(HTML.ATTR_STYLE);
		assertNotNull(activeContentStyle);
		assertEquals("color: green;", activeContentStyle);

		String activeContentClass = contentElement
				.getAttribute(HTML.ATTR_CLASS);
		assertNotNull(activeContentClass);
		assertEquals(
				"dr-pnlbar-c rich-panelbar-content myContentStyle1 myContentStyle",
				activeContentClass);

		// check facet
		nsIDOMElement disabledToggle = (nsIDOMElement) elements.get(7)
				.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);

		assertNotNull(contentElement);

		String disabledContentStyle = disabledToggle
				.getAttribute(HTML.ATTR_STYLE);
		assertNotNull(disabledContentStyle);
		assertEquals("color: green;", disabledContentStyle);

		String disabledContentClass = disabledToggle
				.getAttribute(HTML.ATTR_CLASS);
		assertNotNull(disabledContentClass);
		assertEquals("dr-pnlbar-h rich-panelbar-header myHeaderStyle1",
				disabledContentClass);

		contentElements.clear();
		TestUtil.findElementsByName((nsIDOMNode) disabledToggle,
				contentElements, HTML.TAG_IMG);
		assertEquals(1, contentElements.size());

		disabledToggle = (nsIDOMElement) elements.get(9).queryInterface(
				nsIDOMElement.NS_IDOMELEMENT_IID);

		assertNotNull(contentElement);

		disabledContentStyle = disabledToggle.getAttribute(HTML.ATTR_STYLE);
		assertNotNull(disabledContentStyle);
		assertEquals("color: green;", disabledContentStyle);

		disabledContentClass = disabledToggle.getAttribute(HTML.ATTR_CLASS);
		assertNotNull(disabledContentClass);
		assertEquals("dr-pnlbar-h rich-panelbar-header myHeaderStyle1",
				disabledContentClass);

		if (getException() != null) {
			throw getException();
		}
	}

}
