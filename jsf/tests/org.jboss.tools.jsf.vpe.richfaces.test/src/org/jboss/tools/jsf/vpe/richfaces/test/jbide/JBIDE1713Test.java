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

import static org.jboss.tools.vpe.xulrunner.util.XPCOM.queryInterface;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.base.test.TestUtil;
import org.jboss.tools.vpe.base.test.VpeTest;
import org.jboss.tools.vpe.editor.util.HTML;
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

	public static final String IMPORT_PROJECT_NAME = "richFacesTest"; //$NON-NLS-1$

	private static final String TEST_PAGE_NAME = "JBIDE/1713/JBIDE-1713.xhtml"; //$NON-NLS-1$

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

		assertNotNull("Editor input is null", input); //$NON-NLS-1$
		// open and get editor
		JSPMultiPageEditor part = openEditor(input);
		// get dom document
		nsIDOMDocument document = TestUtil.getVpeVisualDocument(part);
		nsIDOMElement element = document.getDocumentElement();

		// check that element is not null
		assertNotNull(element);

		// get root node
		nsIDOMNode node = queryInterface(element, nsIDOMNode.class);

		List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();

		// find "table" elements
		TestUtil.findElementsByName(node, elements, HTML.TAG_TABLE);

		assertEquals(1, elements.size());

		nsIDOMElement table = queryInterface(elements.get(0), nsIDOMElement.class);

		assertNotNull(table);

		// Check applying styleClass
		String styleClass = table.getAttribute(HTML.ATTR_CLASS);
		assertNotNull("styleClass attribute not apply", styleClass); //$NON-NLS-1$
		assertEquals("dr-pnlbar rich-panelbar dr-pnlbar-b myClass", styleClass); //$NON-NLS-1$

		// Check applying style
		String stylePanel = table.getAttribute(HTML.ATTR_STYLE);
		assertNotNull("style attribute not apply", stylePanel); //$NON-NLS-1$
		assertEquals(
				"padding: 0px; height: 207px; width: 453px; font-weight: bold;", //$NON-NLS-1$
				stylePanel);

		elements.clear();

		TestUtil.findAllElementsByName(node, elements, HTML.TAG_DIV);

		assertEquals(10, elements.size());

		/*
		 * Get the fifth DIV with text "nice"
		 */
		nsIDOMElement activeToggle = queryInterface(elements.get(5), nsIDOMElement.class);

		assertNotNull(activeToggle);
		String activeToggleClass = activeToggle.getAttribute(HTML.ATTR_CLASS);
		assertNotNull(activeToggleClass);
		assertEquals(
				"dr-pnlbar-h rich-panelbar-header myHeaderStyle1 myHeaderStyle myHeaderStyleActive1 myHeaderStyleActive", //$NON-NLS-1$
				activeToggleClass);

		String activeToggleStyle = activeToggle.getAttribute(HTML.ATTR_STYLE);
		assertNotNull(activeToggleStyle);

		/*
		 * Fix for -- https://jira.jboss.org/browse/JBIDE-6539
		 * Due to differences in XulRunner 1.9.2 and 1.9.1
		 * style attribute is compared by its splitted values.
		 * After migration to XR 1.9.2 is finished style value should be equals
		 * "background: red none repeat scroll 0% 0%; color: blue;"
		 */
//		assertEquals(
//		"background: red none repeat scroll 0% 0%; color: blue; -moz-background-clip: border; -moz-background-origin: padding; -moz-background-inline-policy: continuous;",
//		activeToggleStyle);
		assertTrue("Style is incorrect: [color: blue] should present.", //$NON-NLS-1$
				activeToggleStyle.contains("color: blue")); //$NON-NLS-1$
		assertTrue("Style is incorrect: [background:] should present.", //$NON-NLS-1$
				activeToggleStyle.contains("background:")); //$NON-NLS-1$
		assertTrue("Style is incorrect: [red] should present.", //$NON-NLS-1$
				activeToggleStyle.contains("red")); //$NON-NLS-1$
		assertTrue(
				"Style is incorrect: [none repeat scroll 0% 0%] should present.", //$NON-NLS-1$
				activeToggleStyle.contains("none repeat scroll 0% 0%")); //$NON-NLS-1$
		
		// check active content
		List<nsIDOMNode> contentElements = new ArrayList<nsIDOMNode>();
		TestUtil.findAllElementsByName(node, contentElements, HTML.TAG_TD);

		assertEquals(2, contentElements.size());

		nsIDOMElement contentElement = queryInterface(contentElements.get(1), nsIDOMElement.class);

		assertNotNull(contentElement);

		String activeContentStyle = contentElement
				.getAttribute(HTML.ATTR_STYLE);
		assertNotNull(activeContentStyle);
		assertEquals("color: green;", activeContentStyle); //$NON-NLS-1$

		String activeContentClass = contentElement
				.getAttribute(HTML.ATTR_CLASS);
		assertNotNull(activeContentClass);
		assertEquals(
				"dr-pnlbar-c rich-panelbar-content myContentStyle1 myContentStyle", //$NON-NLS-1$
				activeContentClass);

		// check facet
		nsIDOMElement disabledToggle = queryInterface(elements.get(7), nsIDOMElement.class);

		assertNotNull(contentElement);

		String disabledContentStyle = disabledToggle
				.getAttribute(HTML.ATTR_STYLE);
		assertNotNull(disabledContentStyle);
		assertEquals("color: green;", disabledContentStyle); //$NON-NLS-1$

		String disabledContentClass = disabledToggle
				.getAttribute(HTML.ATTR_CLASS);
		assertNotNull(disabledContentClass);
		assertEquals("dr-pnlbar-h rich-panelbar-header myHeaderStyle1", //$NON-NLS-1$
				disabledContentClass);

		contentElements.clear();
		TestUtil.findElementsByName((nsIDOMNode) disabledToggle,
				contentElements, HTML.TAG_IMG);
		assertEquals(1, contentElements.size());

		disabledToggle = queryInterface(elements.get(9), nsIDOMElement.class);

		assertNotNull(contentElement);

		disabledContentStyle = disabledToggle.getAttribute(HTML.ATTR_STYLE);
		assertNotNull(disabledContentStyle);
		assertEquals("color: green;", disabledContentStyle); //$NON-NLS-1$

		disabledContentClass = disabledToggle.getAttribute(HTML.ATTR_CLASS);
		assertNotNull(disabledContentClass);
		assertEquals("dr-pnlbar-h rich-panelbar-header myHeaderStyle1", //$NON-NLS-1$
				disabledContentClass);

		if (getException() != null) {
			throw getException();
		}
	}

}
