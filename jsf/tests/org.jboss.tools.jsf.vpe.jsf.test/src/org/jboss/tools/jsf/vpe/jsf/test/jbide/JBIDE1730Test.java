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
package org.jboss.tools.jsf.vpe.jsf.test.jbide;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;

/**
 * Test JBIDE-1730
 * 
 * @author Dzmitry Sakovich (dsakovich@exadel.com)
 * 
 */
public class JBIDE1730Test extends VpeTest {

	private static final String TEST_PAGE_NAME1 = "JBIDE/1730/JBIDE-1730.jsp"; //$NON-NLS-1$

	public JBIDE1730Test(String name) {
		super(name);
	}

	// test method for JBIDE 1730 selectOneRadio component
	public void testJBIDE_1730() throws Throwable {
		// wait
		TestUtil.waitForJobs();
		// set exception
		setException(null);

		// get test page path
		IFile file = (IFile) TestUtil.getComponentPath(TEST_PAGE_NAME1,
				JsfAllTests.IMPORT_PROJECT_NAME);

		assertNotNull("Could not open specified file " + file.getFullPath(), //$NON-NLS-1$
				file);

		IEditorInput input = new FileEditorInput(file);

		assertNotNull("Editor input is null", input); //$NON-NLS-1$
		// open and get editor
		JSPMultiPageEditor part = openEditor(input);

		// get dom document
		nsIDOMDocument document = getVpeVisualDocument(part);
		nsIDOMElement element = document.getDocumentElement();

		// check that element is not null
		assertNotNull(element);

		// get root node
		nsIDOMNode node = (nsIDOMNode) element
				.queryInterface(nsIDOMNode.NS_IDOMNODE_IID);

		List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();

		// find "table" elements
		TestUtil.findAllElementsByName(node, elements, HTML.TAG_TABLE);

		assertEquals(4, elements.size());

		nsIDOMElement table = (nsIDOMElement) elements.get(3).queryInterface(
				nsIDOMElement.NS_IDOMELEMENT_IID);

		// test border attribute
		String border = table.getAttribute(HTML.ATTR_BORDER);

		assertNotNull(border);
		assertEquals("5", border.trim()); //$NON-NLS-1$

		// test dir attribute
		String dir = table.getAttribute(HTML.ATTR_DIR);

		assertNotNull(dir);
		assertEquals("rtl", dir.trim().toLowerCase()); //$NON-NLS-1$

		// test cellspacing attribute
		String cellspacing = table.getAttribute(HTML.ATTR_CELLSPACING);

		assertNotNull(cellspacing);
		assertEquals("6", cellspacing.trim()); //$NON-NLS-1$

		// test frame attribute
		String frame = table.getAttribute(HTML.ATTR_FRAME);

		assertNotNull(frame);
		assertEquals("above", frame.trim().toLowerCase()); //$NON-NLS-1$

		elements.clear();

		// find "caption" elements
		TestUtil.findAllElementsByName(node, elements, HTML.TAG_CAPTION);

		assertEquals(1, elements.size());

		// Test captionClass
		nsIDOMElement caption = (nsIDOMElement) elements.get(0).queryInterface(
				nsIDOMElement.NS_IDOMELEMENT_IID);

		String captionClass = caption.getAttribute(HTML.ATTR_CLASS);

		assertNotNull(captionClass);
		assertEquals("myStyle0", captionClass.trim()); //$NON-NLS-1$

		// Test captionStyle

		String captionStyle = caption.getAttribute(HTML.ATTR_STYLE);

		assertNotNull(captionStyle);
		assertEquals("color: red;", captionStyle.trim()); //$NON-NLS-1$

		if (getException() != null) {
			throw getException();
		}
	}

}
