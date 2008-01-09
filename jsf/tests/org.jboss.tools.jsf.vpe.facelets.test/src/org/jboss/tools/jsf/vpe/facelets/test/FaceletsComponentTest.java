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
package org.jboss.tools.jsf.vpe.facelets.test;

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
 * Class for testing all Seam components
 * 
 * @author dsakovich@exadel.com
 * 
 */
public class FaceletsComponentTest extends VpeTest {

    // import project name
    private static final String IMPORT_PROJECT_NAME = "faceletsTest";

    public FaceletsComponentTest(String name) {
	super(name, IMPORT_PROJECT_NAME, FaceletsTestPlugin
		.getPluginResourcePath());
    }

    public void testDebug() throws Throwable {
	performTestForJsfComponent("components/debug.xhtml"); // $NON-NLS-1$
    }

    public void testDefine() throws Throwable {
	TestUtil.waitForJobs();
	// set exception
	setException(null);

	// get test page path
	IFile file = (IFile) TestUtil.getComponentPath(
		"components/define.xhtml", getImportProjectName());

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

	// get root node
	nsIDOMNode node = (nsIDOMNode) element
		.queryInterface(nsIDOMNode.NS_IDOMNODE_IID);

	List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();

	// find "span" elements
	TestUtil.findElementsByName(node, elements, HTML.TAG_SPAN);

	assertEquals(2, elements.size());

	nsIDOMElement elementSpan0 = (nsIDOMElement) elements.get(0)
		.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
	nsIDOMElement elementSpan1 = (nsIDOMElement) elements.get(1)
		.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);

	/*
	 * nsIDOMText text0 = (nsIDOMText) elementSpan0.getFirstChild();
	 * nsIDOMText text1 = (nsIDOMText) elementSpan1.getFirstChild();
	 * System.out.println(text0.getNodeValue());
	 * assertEquals(elementInput0.getAttribute("value"), "");
	 * assertNotNull(elementInput1.getAttribute("value"), "");
	 * assertNotNull(elementInput2.getAttribute("value"), "test");
	 */

	// TODO Dzmitry Sakovich Test not complete
	assertTrue("Defined content is not shown", false);
	if (getException() != null) {
	    throw getException();
	}
    }

    public void testInsert() throws Throwable {
	performTestForJsfComponent("components/insert.xhtml"); // $NON-NLS-1$
    }

}
