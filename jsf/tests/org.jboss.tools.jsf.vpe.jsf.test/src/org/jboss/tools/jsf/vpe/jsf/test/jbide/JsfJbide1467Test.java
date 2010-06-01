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

import static org.jboss.tools.vpe.xulrunner.util.XPCOM.queryInterface;

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
import org.mozilla.interfaces.nsIDOMNamedNodeMap;
import org.mozilla.interfaces.nsIDOMNode;

/**
 * Class for testing all jsf bugs
 * 
 * @author sdzmitrovich
 * 
 * test for http://jira.jboss.com/jira/browse/JBIDE-1467
 * 
 * the cause of bug : <input type=radio > tags ( which vpe template forms when
 * process "selectOneRadio" jsf tag ) didn't have equal "name" attribute
 * 
 * REQUIREMENT :test page must has only one <h:selectOneRadio > tag
 * 
 * test checks that all "radio" elements ( of xulRunner DOMDocument ) have equal
 * "name" attributes
 */
public class JsfJbide1467Test extends VpeTest {

	private static final String TEST_PAGE_NAME = "JBIDE/1467/JBIDE-1467.jsp";
	// type of input tag
	private static final String ATTR_TYPE_VALUE = "radio";

	// import project name

	public JsfJbide1467Test(String name) {
		super(name);
	}

	/*
	 * JBIDE's test cases
	 */

	public void testJbide() throws Throwable {

		// wait
		TestUtil.waitForJobs();
		// set exception
		setException(null);

		// get test page path
		IFile file = (IFile) TestUtil.getComponentPath(
				TEST_PAGE_NAME, JsfAllTests.IMPORT_PROJECT_NAME);

		assertNotNull("Could not open specified file. componentPage = " + TEST_PAGE_NAME
				+ ";projectName = " + JsfAllTests.IMPORT_PROJECT_NAME, file);//$NON-NLS-1$

		IEditorInput input = new FileEditorInput(file);

		assertNotNull("Editor input is null", input);
		// open and get editor
		JSPMultiPageEditor part = openEditor(input);

		// get dom document
		nsIDOMDocument document = TestUtil.getVpeVisualDocument(part);
		nsIDOMElement element = document.getDocumentElement();

		assertNotNull(element);

		// get root node
		nsIDOMNode node = queryInterface(element, nsIDOMNode.class);

		List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();

		// find "input" elements
		TestUtil.findElementsByName(node, elements, HTML.TAG_INPUT);

		// check that "radio" elements have equal names
		// (size of list of names == 1 )
		assertEquals(1, getRadioNames(elements).size());

		// check exception
		if (getException() != null) {
			throw getException();
		}

	}

	/**
	 * 
	 * @param elements
	 *            list of "input" elements
	 * @return list of names
	 */
	private List<String> getRadioNames(List<nsIDOMNode> elements) {

		// list of "name" of "radio" elements
		List<String> radioNames = new ArrayList<String>();

		for (nsIDOMNode element : elements) {
			// attributes
			nsIDOMNamedNodeMap attributes = element.getAttributes();

			if (attributes != null) {
				// get type of element
				nsIDOMNode type = attributes.getNamedItem(HTML.ATTR_TYPE);

				// if type is "radio"
				if ((type != null)
						&& (ATTR_TYPE_VALUE.equalsIgnoreCase(type
								.getNodeValue()))) {

					// get "name" attribute
					nsIDOMNode name = attributes.getNamedItem(HTML.ATTR_NAME);

					// check for null
					assertNotNull(name);

					// if name is unique
					if (!radioNames.contains(name.getNodeValue()))
						radioNames.add(name.getNodeValue());

				}
			}
		}

		return radioNames;

	}

}
