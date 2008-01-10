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
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;

/**
 * @author Max Areshkau
 * 
 * junit for JBIDE-1484(http://jira.jboss.com/jira/browse/JBIDE-1484)
 */
public class JBIDE1484Test extends VpeTest {

	public static final String IMPORT_PROJECT_NAME = "jsfTest";
	
	private static final String TEST_PAGE_NAME = "JBIDE/1484/JBIDE-1484.jsp";
	
	public JBIDE1484Test(String name) {
		
		super(name);
	}
	
	//test method for JBIDE 1484
	public void testJBIDE_1484() throws Throwable{
		// wait
		TestUtil.waitForJobs();
		// set exception
		setException(null);

		// get test page path
		IFile file = (IFile) TestUtil.getComponentPath(
				TEST_PAGE_NAME, IMPORT_PROJECT_NAME);

		assertNotNull("Could not open specified file " + file.getFullPath(),
				file);

		IEditorInput input = new FileEditorInput(file);

		assertNotNull("Editor input is null", input);
		// open and get editor
		JSPMultiPageEditor part = openEditor(input);

		// get dom document
		nsIDOMDocument document = getVpeVisualDocument(part);
		nsIDOMElement element = document.getDocumentElement();
		
		//check that element is not null
		assertNotNull(element);

			// get root node
		nsIDOMNode node = (nsIDOMNode) element
					.queryInterface(nsIDOMNode.NS_IDOMNODE_IID);

		List<nsIDOMNode> elements = new ArrayList<nsIDOMNode>();
		
		// find "input" elements
		TestUtil.findElementsByName(node, elements, HTML.TAG_INPUT);
		
		assertEquals(3, elements.size());
		nsIDOMElement elementInput0 = (nsIDOMElement) elements.get(0).queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
		nsIDOMElement elementInput1 = (nsIDOMElement) elements.get(1).queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
		nsIDOMElement elementInput2 = (nsIDOMElement) elements.get(2).queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
		
		assertEquals(elementInput0.getAttribute("value"),"");
		assertEquals(elementInput1.getAttribute("value"),"");
		assertEquals(elementInput2.getAttribute("value"),"test");
		if(getException()!=null) {
			throw getException();
		}
	}

}
