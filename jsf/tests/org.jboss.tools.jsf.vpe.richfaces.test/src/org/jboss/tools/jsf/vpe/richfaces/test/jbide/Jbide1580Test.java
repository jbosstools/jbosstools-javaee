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

import junit.framework.Assert;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.jsf.vpe.richfaces.test.RichFacesAllTests;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.editor.VpeController;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;

/**
 * 
 * 
 * @author sdzmitrovich
 * 
 */
public class Jbide1580Test extends VpeTest {

	public static final String FILE_NAME1 = "JBIDE/1580/JBIDE-1580-8button.xhtml";
	public static final String FILE_NAME2 = "JBIDE/1580/JBIDE-1580-4button.xhtml";
	private static final String TEST_ELEMENT_ID = "testElement";

	public Jbide1580Test(String name) {
		super(name);
	}

	public void testUsualState() throws Throwable {

		// wait
		TestUtil.waitForJobs();
		// set exception
		setException(null);

		// get test page path
		IFile file = (IFile) TestUtil.getComponentPath(FILE_NAME1,
				RichFacesAllTests.IMPORT_PROJECT_NAME);

		IEditorInput input = new FileEditorInput(file);

		// open and get editor
		JSPMultiPageEditor part = openEditor(input);

		// get dom document
		nsIDOMDocument document = TestUtil.getVpeVisualDocument(part);
		assertNotNull(document);

		VpeController controller = TestUtil.getVpeController(part);

		nsIDOMElement element = findElementById(controller, TEST_ELEMENT_ID);
		assertNotNull(element);

		// get root node
		nsIDOMNode node = (nsIDOMNode) element
				.queryInterface(nsIDOMNode.NS_IDOMNODE_IID);

		List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();

		// find "img" elements
		TestUtil.findElementsByName(node, elements, HTML.TAG_IMG);

		Assert.assertEquals(8, elements.size());

		// check exception
		if (getException() != null) {
			throw getException();
		}

	}

	public void testWithoutSomeButton() throws Throwable {

		// wait
		TestUtil.waitForJobs();
		// set exception
		setException(null);

		// get test page path
		IFile file = (IFile) TestUtil.getComponentPath(FILE_NAME2,
				RichFacesAllTests.IMPORT_PROJECT_NAME);

		IEditorInput input = new FileEditorInput(file);

		// open and get editor
		JSPMultiPageEditor part = openEditor(input);

		// get dom document
		nsIDOMDocument document = TestUtil.getVpeVisualDocument(part);
		assertNotNull(document);

		VpeController controller = TestUtil.getVpeController(part);

		nsIDOMElement element = findElementById(controller, TEST_ELEMENT_ID);
		assertNotNull(element);

		// get root node
		nsIDOMNode node = (nsIDOMNode) element
				.queryInterface(nsIDOMNode.NS_IDOMNODE_IID);

		List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();

		// find "img" elements
		TestUtil.findElementsByName(node, elements, HTML.TAG_IMG);

		Assert.assertEquals(4, elements.size());

		// check exception
		if (getException() != null) {
			throw getException();
		}

	}

}
