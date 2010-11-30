/*******************************************************************************
 * Copyright (c) 2007-2008 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.jsf.test.jbide;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.base.test.TestUtil;
import org.jboss.tools.vpe.base.test.VpeTest;
import org.jboss.tools.vpe.editor.VpeController;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;

/**
 * @author Sergey Dzmitrovich
 * 
 */
public class JBIDE1805Test extends VpeTest {

	private static final String TEST_PAGE = "JBIDE/1805/test.xhtml"; //$NON-NLS-1$
	private static final String ELEMENT_WRAPPER_ID = "invisibleElement"; //$NON-NLS-1$

	public JBIDE1805Test(String name) {
		super(name);
	}

	public void testJBIDE1805() throws Throwable {

		// get test page path
		setException(null);

		IFile file = (IFile) TestUtil.getComponentPath(TEST_PAGE,
				JsfAllTests.IMPORT_PROJECT_NAME);

		assertNotNull("Could not open specified file. componentPage = " + TEST_PAGE//$NON-NLS-1$ 
				+ ";projectName = " + JsfAllTests.IMPORT_PROJECT_NAME, file);//$NON-NLS-1$

		IEditorInput input = new FileEditorInput(file);

		assertNotNull("Editor input is null", input); //$NON-NLS-1$

		// open and get editor
		JSPMultiPageEditor part = openEditor(input);

		VpeController vpeController = TestUtil.getVpeController(part);
		assertNotNull(vpeController);

		// set show invisible tag's flag to false
		vpeController.getVisualBuilder().setShowInvisibleTags(false);
		// refresh vpe
		vpeController.visualRefresh();

		TestUtil.waitForIdle();
		
		nsIDOMDocument document = TestUtil.getVpeVisualDocument(part);
		assertNotNull(document);
		// get element
		nsIDOMElement element = document.getElementById(ELEMENT_WRAPPER_ID);
		assertNotNull(element);
		// element must have not children

		assertEquals(0, countElementsChildren(element.getChildNodes()));

		// set show invisible tag's flag to true
		vpeController.getVisualBuilder().setShowInvisibleTags(true);
		vpeController.visualRefresh();

		TestUtil.waitForIdle();
		document = TestUtil.getVpeVisualDocument(part);
		assertNotNull(document);
		// get element
		element = document.getElementById(ELEMENT_WRAPPER_ID);

		assertEquals(1, countElementsChildren(element.getChildNodes()));

		if (getException() != null) {

			throw getException();
		}
	}

	private int countElementsChildren(nsIDOMNodeList list) {

		int count = 0;
		for (int i = 0; i < list.getLength(); i++) {
			nsIDOMNode child = list.item(i);
			if (child.getNodeType() == nsIDOMNode.ELEMENT_NODE)
				count++;
		}

		return count;
	}

}
