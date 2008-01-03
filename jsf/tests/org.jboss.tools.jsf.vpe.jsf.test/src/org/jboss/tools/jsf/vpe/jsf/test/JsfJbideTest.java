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

	// $NON-NLS-1$

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

		TestJsfUtil.waitForJobs();
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.closeAllEditors(true);
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

		TestJsfUtil.waitForJobs();

		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.closeAllEditors(true);
	}

	/*
	 * JBIDE's test cases
	 */

	/**
	 * test for http://jira.jboss.com/jira/browse/JBIDE-1467
	 * 
	 * REQUIREMENT :test page must have only one <h:selectOneRadio > tag
	 * 
	 * test check that all "radio" elements ( of xulRunner DOMDocument ) have
	 * equal "name" attributes
	 */
	public void testJBIDE_1467() throws PartInitException, Throwable {
		// path to test page
		String path = "JBIDE/JBIDE-1467.jsp";
		// wait
		TestJsfUtil.waitForJobs();
		// set exception
		exception = null;

		// get test page path
		IFile file = (IFile) TestJsfUtil.getComponentPath(path);

		IEditorInput input = new FileEditorInput(file);

		// get editor
		JSPMultiPageEditor part = (JSPMultiPageEditor) PlatformUI
				.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.openEditor(input, EDITOR_ID, true);

		// wait for jobs
		TestJsfUtil.waitForJobs();
		// wait full initialization of vpe
		TestJsfUtil.delay(3000);

		VpeEditorPart visualEditor = (VpeEditorPart) part.getVisualEditor();
		VpeController vpeController = visualEditor.getController();

		// get xulRunner editor
		XulRunnerEditor xulRunnerEditor = vpeController.getXulRunnerEditor();

		// get dom document
		nsIDOMDocument document = xulRunnerEditor.getDOMDocument();
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
			assertEquals(getRadioNames(elements).size(), 1);

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
	void findElementsByName(nsIDOMNode node, List<nsIDOMNode> elements,
			String name) {

		// get children
		nsIDOMNodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			nsIDOMNode child = children.item(i);

			// if current child is required then add his to list
			if (name.equalsIgnoreCase((child.getNodeName()))) {

				elements.add(child);
				return;
			}
			// if current child is not required
			findElementsByName(child, elements, name);

		}

	}

	private List<String> getRadioNames(List<nsIDOMNode> elements) {

		// list of "name" of "radio" elements
		List<String> radioNames = new ArrayList<String>();

		for (nsIDOMNode element : elements) {
			// attributes
			nsIDOMNamedNodeMap attributes = element.getAttributes();

			if (null != attributes) {
				// get type of element
				nsIDOMNode type = attributes.getNamedItem(HTML.ATTR_TYPE);

				// if type is "radio"
				if ((null != type)
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
