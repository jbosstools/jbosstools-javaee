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

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.base.test.TestUtil;
import org.jboss.tools.vpe.base.test.VpeTest;
import org.jboss.tools.vpe.editor.VpeController;
import org.jboss.tools.vpe.editor.util.SelectionUtil;
import org.jboss.tools.vpe.editor.util.TextUtil;
import org.mozilla.interfaces.nsISelection;
import org.mozilla.interfaces.nsISelectionController;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author sdzmitrovich
 * 
 *         http://jira.jboss.com/jira/browse/JBIDE-2569
 * 
 */
public class JBIDE2526Test extends VpeTest {

	private static final String SELECTION_PAGE_NAME = "JBIDE/2569/selection.jsp"; //$NON-NLS-1$

	public JBIDE2526Test(String name) {
		super(name);
	}

	/**
	 * 
	 * @throws Throwable
	 */
	public void testSourceSelection() throws Throwable {

		// wait
		TestUtil.waitForJobs();
		// set exception
		setException(null);

		// get test page path
		IFile file = (IFile) TestUtil.getComponentPath(SELECTION_PAGE_NAME,
				JsfAllTests.IMPORT_PROJECT_NAME);

		IEditorInput input = new FileEditorInput(file);

		// open and get editor
		JSPMultiPageEditor part = openEditor(input);

		// get controller
		VpeController controller = TestUtil.getVpeController(part);
		assertNotNull(controller);

		Document document = getSourceDocument(controller);

		Node body = document.getDocumentElement().getElementsByTagName("body")
				.item(0);

		NodeList children = body.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {

			Node child = children.item(i);

			if (child.getNodeType() == Node.TEXT_NODE
					&& child.getNodeValue() != null)

				SelectionUtil.setSourceSelection(controller.getPageContext(),
						child, 0, child.getNodeValue().length() - 1);

			nsISelection selection = controller.getVisualSelectionController()
					.getSelection(nsISelectionController.SELECTION_NORMAL);

			System.out.println("delay");
			TestUtil.delay(20000);
			
			assertEquals(3, selection.getAnchorOffset());
			assertEquals(12, selection.getFocusOffset());
			assertEquals(14, TextUtil.visualPosition(child.getNodeValue(), child
					.getNodeValue().length() - 1));

		}

		// check exception
		if (getException() != null) {
			throw getException();
		}

	}

}
