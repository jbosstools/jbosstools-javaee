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
import org.jboss.tools.vpe.base.test.TestUtil;
import org.jboss.tools.vpe.base.test.VpeTest;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNamedNodeMap;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;

/**
 * 
 * @author sdzmitrovich
 * 
 * test for http://jira.jboss.com/jira/browse/JBIDE-1718
 * 
 * 
 */
public class JsfJbide1718Test extends VpeTest {

	private static final String DIR_TEST_PAGE_NAME = "JBIDE/1718/JBIDE-1718-dir.jsp"; //$NON-NLS-1$

	private static final String ESCAPE_TEST_PAGE_NAME = "JBIDE/1718/JBIDE-1718-escape.jsp"; //$NON-NLS-1$

	private static final String DISABLED_TEST_PAGE_NAME = "JBIDE/1718/JBIDE-1718-disabled.jsp"; //$NON-NLS-1$

	private static final String FORMAT_TEST_PAGE_NAME = "JBIDE/1718/JBIDE-1718-format.jsp"; //$NON-NLS-1$

	public JsfJbide1718Test(String name) {
		super(name);
	}

	/**
	 * 
	 * @throws Throwable
	 */
	//mareshkau, dir attributes was disabled due to JBIDE-3209
	public void _testDirAttribute() throws Throwable {

		// wait
		TestUtil.waitForJobs();
		// set exception
		setException(null);

		// get test page path
		IFile file = (IFile) TestUtil.getComponentPath(DIR_TEST_PAGE_NAME,
				JsfAllTests.IMPORT_PROJECT_NAME);

		IEditorInput input = new FileEditorInput(file);

		// open and get editor
		JSPMultiPageEditor part = openEditor(input);

		// get dom document
		nsIDOMDocument document = TestUtil.getVpeVisualDocument(part);
		assertNotNull(document);

		// get dom element
		nsIDOMElement element = document.getDocumentElement();
		assertNotNull(element);

		// get root node
		nsIDOMNode node = queryInterface(element, nsIDOMNode.class);

		// find "b" elements
		List<nsIDOMNode> bElements = new ArrayList<nsIDOMNode>();
		TestUtil.findElementsByName(node, bElements, HTML.TAG_B);

		// number of "b" elements must be 1
		assertEquals(1, bElements.size());
		nsIDOMNode b = bElements.get(0);

		List<nsIDOMNode> spanElements = new ArrayList<nsIDOMNode>();
		List<nsIDOMNode> labelElements = new ArrayList<nsIDOMNode>();
		List<nsIDOMNode> aElements = new ArrayList<nsIDOMNode>();

		// find "span" elements in "b" tag
		TestUtil.findElementsByName(b, spanElements, HTML.TAG_SPAN);
		// find "label" elements in "b"
		TestUtil.findElementsByName(b, labelElements, HTML.TAG_LABEL);
		// find "a" elements in "b"
		TestUtil.findElementsByName(b, aElements, HTML.TAG_A);

		// number of "span" elements must be 2
		assertEquals(2, spanElements.size());

		// number of "label" elements must be 1
		assertEquals(1, labelElements.size());

		// number of "a" elements must be 1
		assertEquals(1, aElements.size());

		// join all elements
		List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();

		elements.addAll(spanElements);
		elements.addAll(labelElements);
		elements.addAll(aElements);

		for (nsIDOMNode child : elements) {

			// get attributes
			nsIDOMNamedNodeMap attributes = child.getAttributes();

			// all elements must have attributes
			assertNotNull(attributes);

			// all elements must have "dir" attribute
			assertNotNull(attributes.getNamedItem(HTML.ATTR_DIR));

		}

		// check exception
		if (getException() != null) {
			throw getException();
		}

	}

	/**
	 * 
	 * @throws Throwable
	 */
	public void testEscapeAttribute() throws Throwable {

		// wait
		TestUtil.waitForJobs();
		// set exception
		setException(null);

		// get test page path
		IFile file = (IFile) TestUtil.getComponentPath(ESCAPE_TEST_PAGE_NAME,
				JsfAllTests.IMPORT_PROJECT_NAME);

		IEditorInput input = new FileEditorInput(file);

		// open and get editor
		JSPMultiPageEditor part = openEditor(input);

		// get dom document
		nsIDOMDocument document = TestUtil.getVpeVisualDocument(part);
		assertNotNull(document);

		// get dom element
		nsIDOMElement element = document.getDocumentElement();
		assertNotNull(element);

		// get root node
		nsIDOMNode node = queryInterface(element, nsIDOMNode.class);

		List<nsIDOMNode> spanElements = new ArrayList<nsIDOMNode>();

		// find "input" elements
		TestUtil.findElementsByName(node, spanElements, HTML.TAG_INPUT);

		// number of "input" elements must be 3
		assertEquals(3, spanElements.size());

		// check exception
		if (getException() != null) {
			throw getException();
		}

	}

	/**
	 * 
	 * @throws Throwable
	 */
	public void testDisabledAttribute() throws Throwable {

		// wait
		TestUtil.waitForJobs();
		// set exception
		setException(null);

		// get test page path
		IFile file = (IFile) TestUtil.getComponentPath(DISABLED_TEST_PAGE_NAME,
				JsfAllTests.IMPORT_PROJECT_NAME);

		IEditorInput input = new FileEditorInput(file);

		// open and get editor
		JSPMultiPageEditor part = openEditor(input);

		// get dom document
		nsIDOMDocument document = TestUtil.getVpeVisualDocument(part);
		assertNotNull(document);

		// get dom element
		nsIDOMElement element = document.getDocumentElement();
		assertNotNull(element);

		// get root node
		nsIDOMNode node = queryInterface(element, nsIDOMNode.class);

		List<nsIDOMNode> aElements = new ArrayList<nsIDOMNode>();

		// find "input" elements
		TestUtil.findElementsByName(node, aElements, HTML.TAG_A);

		// page has 2 <h:outputLink ...> tags but one from them have attribute
		// disabled="true", so number of "a" tags must be one
		assertEquals(1, aElements.size());

		// check exception
		if (getException() != null) {
			throw getException();
		}

	}

	/**
	 * 
	 * @throws Throwable
	 */
	public void testFormatMessageElements() throws Throwable {

		// wait
		TestUtil.waitForJobs();
		// set exception
		setException(null);

		// get test page path
		IFile file = (IFile) TestUtil.getComponentPath(FORMAT_TEST_PAGE_NAME,
				JsfAllTests.IMPORT_PROJECT_NAME);

		IEditorInput input = new FileEditorInput(file);

		// open and get editor
		JSPMultiPageEditor part = openEditor(input);

		// get dom document
		nsIDOMDocument document = TestUtil.getVpeVisualDocument(part);
		assertNotNull(document);

		// get dom element
		nsIDOMElement element = document.getDocumentElement();
		assertNotNull(element);

		// get root node
		nsIDOMNode node = queryInterface(element, nsIDOMNode.class);

		// find "b" elements
		List<nsIDOMNode> bElements = new ArrayList<nsIDOMNode>();
		TestUtil.findElementsByName(node, bElements, HTML.TAG_B);

		// number of "b" elements must be 1
		assertEquals(1, bElements.size());
		nsIDOMNode b = bElements.get(0);

		List<nsIDOMNode> spanElements = new ArrayList<nsIDOMNode>();

		// find "input" elements in b elements
		TestUtil.findElementsByName(b, spanElements, HTML.TAG_SPAN);

		// page has 2 <h:outputLink ...> tags but one from them have attribute
		// disabled="true", so number of "a" tags must be one
		assertEquals(1, spanElements.size());

		nsIDOMNode span = spanElements.get(0);

		nsIDOMNodeList children = span.getChildNodes();
		assertNotNull(children);

		boolean isFind = false;
		for (int i = 0; i < children.getLength(); i++) {

			nsIDOMNode text = children.item(i);

			if ("paramValue".equals(text.getNodeValue())) { //$NON-NLS-1$
				isFind = true;
				break;
			}

			assertEquals(true, isFind);

		}

		// check exception
		if (getException() != null) {
			throw getException();
		}

	}

}
