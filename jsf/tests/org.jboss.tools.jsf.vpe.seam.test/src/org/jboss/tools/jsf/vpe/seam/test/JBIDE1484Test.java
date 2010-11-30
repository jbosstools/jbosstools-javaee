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
package org.jboss.tools.jsf.vpe.seam.test;

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
 * @author Max Areshkau
 * 
 * junit for JBIDE-1484(http://jira.jboss.com/jira/browse/JBIDE-1484)
 */
public class JBIDE1484Test extends VpeTest {

	private static final String TEST_PAGE_NAME = "JBIDE/1484/JBIDE-1484.jsp";  //$NON-NLS-1$

	public JBIDE1484Test(String name) {

		super(name);
	}

	// test method for JBIDE 1484
	public void testJBIDE_1484() throws Throwable {
		// wait
		TestUtil.waitForJobs();
		// set exception
		setException(null);

		// get test page path
		IFile file = (IFile) TestUtil.getComponentPath(TEST_PAGE_NAME,
				SeamAllTests.IMPORT_PROJECT_NAME);

		assertNotNull("Could not open specified file. componentPage = " + TEST_PAGE_NAME//$NON-NLS-1$ 
				+ ";projectName = " + SeamAllTests.IMPORT_PROJECT_NAME, file);//$NON-NLS-1$

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

		// find "input" elements
		TestUtil.findElementsByName(node, elements, HTML.TAG_INPUT);

		assertEquals(3, elements.size());
		nsIDOMElement elementInput0 = queryInterface(elements.get(0), nsIDOMElement.class);
		nsIDOMElement elementInput1 = queryInterface(elements.get(1), nsIDOMElement.class);
		nsIDOMElement elementInput2 = queryInterface(elements.get(2), nsIDOMElement.class);

		assertEquals(" ", elementInput0.getAttribute("value"));  //$NON-NLS-1$//$NON-NLS-2$
		assertEquals(" ", elementInput1.getAttribute("value")); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("test", elementInput2.getAttribute("value")); //$NON-NLS-1$ //$NON-NLS-2$
		if (getException() != null) {
			throw getException();
		}
	}

}
