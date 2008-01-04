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

package org.jboss.tools.jsf.vpe.jsf.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.editor.VpeController;
import org.jboss.tools.vpe.editor.VpeEditorPart;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.xulrunner.editor.XulRunnerEditor;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNamedNodeMap;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;

/**
 * Class for testing all jsf bugs
 * 
 * @author sdzmitrovich
 * 
 */
public class JsfJbideTest extends TestCase implements ILogListener {

	private final static String EDITOR_ID = "org.jboss.tools.jst.jsp.jspeditor.JSPTextEditor"; // $NON-NLS-1$
	// type of input tag
	private static final String ATTR_TYPE_VALUE = "radio";

	// check warning log
	private final static boolean checkWarning = false;

	private Throwable exception;

	public JsfJbideTest(String name) {
		super(name);
	}

	/**
	 * Perform pre-test initialization.
	 * 
	 * @throws Exception
	 * 
	 * @see TestCase#setUp()
	 */

	protected void setUp() throws Exception {

		super.setUp();

		Platform.addLogListener(this);

		closeEditors();
	}

	/**
	 * Perform post-test cleanup.
	 * 
	 * @throws Exception
	 * 
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {

		super.tearDown();

		Platform.removeLogListener(this);

		closeEditors();
	}

	/*
	 * JBIDE's test cases
	 */

	/**
	 * test for http://jira.jboss.com/jira/browse/JBIDE-1467
	 * 
	 * the cause of bug : <input type=radio > tags ( which vpe template forms
	 * when process "selectOneRadio" jsf tag ) didn't have equal "name"
	 * attribute
	 * 
	 * REQUIREMENT :test page must has only one <h:selectOneRadio > tag
	 * 
	 * test checks that all "radio" elements ( of xulRunner DOMDocument ) have
	 * equal "name" attributes
	 */
	public void testJBIDE_1467() throws PartInitException, Throwable {

		// wait
		TestJsfUtil.waitForJobs();
		// set exception
		exception = null;

		// get test page path
		IFile file = (IFile) TestJsfUtil
				.getComponentPath("JBIDE/1467/JBIDE-1467.jsp");

		IEditorInput input = new FileEditorInput(file);

		// open and get editor
		JSPMultiPageEditor part = openEditor(input);

		// get dom document
		nsIDOMDocument document = getVpePageSource(part);
		nsIDOMElement element = document.getDocumentElement();

		if (element != null) {

			// get root node
			nsIDOMNode node = (nsIDOMNode) element
					.queryInterface(nsIDOMNode.NS_IDOMNODE_IID);

			List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();

			// find "input" elements
			findElementsByName(node, elements, HTML.TAG_INPUT);

			// check that "radio" elements have equal names
			// (size of list of names == 1 )
			assertEquals(1, getRadioNames(elements).size());

		}

		// check exception
		if (exception != null) {
			throw exception;
		}

	}

	/**
	 * test for http://jira.jboss.com/jira/browse/JBIDE-1467
	 * 
	 * the cause of bug :
	 * 
	 * 1. <select ... > tag ( which vpe template forms when process
	 * "selectManyMenu" and "selectManyListbox" jsf tags ) didn't have
	 * multiple="multiple" attribute
	 * 
	 * 2. <select ... > tag ( which vpe template forms when process
	 * "selectOneListbox" and "selectManyListbox" jsf tags ) sometimes had
	 * incorrect "size" attribute
	 * 
	 * DISCRIPTION: test consist of two part
	 * 
	 * 1. The first page (JBIDE-1501_multiple.jsp) has "selectManyMenu" and
	 * "selectManyListbox" jsf tags. And test checks that all "select" elements
	 * have "multiple" attribute
	 * 
	 * 2. The second page (JBIDE-1501_size.jsp) has "selectOneListbox" and
	 * "selectManyListbox" jsf tags and they was formed so that all "select"
	 * elements must have size="2" attribute . And test checks "size" attribute
	 */
	public void testJBIDE_1501() throws PartInitException, Throwable {

		// wait
		TestJsfUtil.waitForJobs();
		// set exception
		exception = null;

		// _____1st Part____//

		// get test page path
		IFile file = (IFile) TestJsfUtil
				.getComponentPath("JBIDE/1501/JBIDE-1501_multiple.jsp");

		IEditorInput input = new FileEditorInput(file);

		// open and get editor
		JSPMultiPageEditor part = openEditor(input);

		// get dom document
		nsIDOMDocument document = getVpePageSource(part);
		assertNotNull(document);

		// get dom element
		nsIDOMElement element = document.getDocumentElement();
		assertNotNull(element);

		// get root node
		nsIDOMNode node = (nsIDOMNode) element
				.queryInterface(nsIDOMNode.NS_IDOMNODE_IID);

		List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();

		// find "select" elements
		findElementsByName(node, elements, HTML.TAG_SELECT);

		for (nsIDOMNode select : elements) {

			// get attributes
			nsIDOMNamedNodeMap attributes = select.getAttributes();

			// "select" must have attributes
			assertNotNull(attributes);

			// select must have "multiple" attribute
			assertNotNull(attributes.getNamedItem(HTML.ATTR_MULTIPLE));

		}

		// _____2nd Part____//

		// get test page path
		file = (IFile) TestJsfUtil
				.getComponentPath("JBIDE/1501/JBIDE-1501_size.jsp");

		input = new FileEditorInput(file);

		// open and get editor
		part = openEditor(input);

		// get dom document
		document = getVpePageSource(part);
		assertNotNull(document);

		// get dom element
		element = document.getDocumentElement();
		assertNotNull(element);

		// get root node
		node = (nsIDOMNode) element.queryInterface(nsIDOMNode.NS_IDOMNODE_IID);

		elements = new ArrayList<nsIDOMNode>();

		// find "select" elements
		findElementsByName(node, elements, HTML.TAG_SELECT);

		for (nsIDOMNode select : elements) {

			// get attributes
			nsIDOMNamedNodeMap attributes = select.getAttributes();

			// "select" must have attributes
			assertNotNull(attributes);

			// select must have "size" attribute
			nsIDOMNode size = attributes.getNamedItem(HTML.ATTR_SIZE);
			assertNotNull(size);

			//
			assertEquals(2, Integer.parseInt(size.getNodeValue()));

		}

		// check exception
		if (exception != null) {
			throw exception;
		}

	}

	/**
	 * find elements by name
	 * 
	 * @param node -
	 *            current node
	 * @param elements -
	 *            list of found elements
	 * @param name -
	 *            name element
	 */
	private void findElementsByName(nsIDOMNode node, List<nsIDOMNode> elements,
			String name) {

		// get children
		nsIDOMNodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			nsIDOMNode child = children.item(i);

			// if current child is required then add his to list
			if (name.equalsIgnoreCase((child.getNodeName()))) {

				elements.add(child);

			} else {

				findElementsByName(child, elements, name);

			}
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

	/**
	 * close all opened editors
	 */
	private void closeEditors() {

		// wait
		TestJsfUtil.waitForJobs();

		// close
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.closeAllEditors(true);

	}

	/**
	 * Open JSPMultiPageEditor editor
	 * 
	 * @param input
	 * @return
	 * @throws PartInitException
	 */
	private JSPMultiPageEditor openEditor(IEditorInput input)
			throws PartInitException {

		// get editor
		JSPMultiPageEditor part = (JSPMultiPageEditor) PlatformUI
				.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.openEditor(input, EDITOR_ID, true);

		// wait for jobs
		TestJsfUtil.waitForJobs();
		// wait full initialization of vpe
		TestJsfUtil.delay(3000);

		return part;

	}

	/**
	 * get xulrunner source page
	 * 
	 * @param part -
	 *            JSPMultiPageEditor
	 * @return nsIDOMDocument
	 */
	private nsIDOMDocument getVpePageSource(JSPMultiPageEditor part) {

		VpeEditorPart visualEditor = (VpeEditorPart) part.getVisualEditor();
		VpeController vpeController = visualEditor.getController();

		// get xulRunner editor
		XulRunnerEditor xulRunnerEditor = vpeController.getXulRunnerEditor();

		// get dom document
		nsIDOMDocument document = xulRunnerEditor.getDOMDocument();

		return document;
	}

	public void logging(IStatus status, String plugin) {
		switch (status.getSeverity()) {
		case IStatus.ERROR:
			exception = status.getException();
			break;
		case IStatus.WARNING:
			if (checkWarning)
				exception = status.getException();
			break;
		default:
			break;
		}

	}

}
