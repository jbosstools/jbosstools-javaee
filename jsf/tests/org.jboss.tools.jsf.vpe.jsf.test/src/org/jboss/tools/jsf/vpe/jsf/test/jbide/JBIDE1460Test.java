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
 * Test JBIDE-1460
 * 
 * @author dsakovich@exadel.com
 * 
 */
public class JBIDE1460Test extends VpeTest {

    private static final String TEST_PAGE_NAME = "JBIDE/1460/JBIDE-1460.xhtml"; //$NON-NLS-1$

    public JBIDE1460Test(String name) {
	super(name);
    }

    // test method for JBIDE 1460
    public void testJBIDE_1460() throws Throwable {
	// wait
	TestUtil.waitForJobs();
	// set exception
	setException(null);

	// get test page path
	IFile file = (IFile) TestUtil.getComponentPath(TEST_PAGE_NAME,
			JsfAllTests.IMPORT_PROJECT_NAME);

	assertNotNull("Could not open specified file. componentPage = " + TEST_PAGE_NAME//$NON-NLS-1$ 
			+ ";projectName = " + JsfAllTests.IMPORT_PROJECT_NAME, file);//$NON-NLS-1$

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
	nsIDOMNode node = (nsIDOMNode) element
		.queryInterface(nsIDOMNode.NS_IDOMNODE_IID);

	List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();

	// find "span" elements
	TestUtil.findElementsByName(node, elements, HTML.TAG_SPAN);

	assertEquals(3, elements.size());

	nsIDOMNode span = elements.get(0);

	nsIDOMNode text = span.getFirstChild();
	assertEquals(text.getNodeValue().trim(), "#{item.nodes}"); //$NON-NLS-1$

	if (getException() != null) {
	    throw getException();
	}
    }

}
