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
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.sse.core.internal.provisional.IndexedRegion;
import org.eclipse.wst.sse.ui.internal.contentassist.ContentAssistUtils;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.editor.VpeController;
import org.jboss.tools.vpe.editor.mapping.VpeDomMapping;
import org.jboss.tools.vpe.editor.mapping.VpeNodeMapping;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;
import org.mozilla.interfaces.nsIDOMNode;
import org.w3c.dom.Node;

/**
 * @author mareshkau
 * 
 */
public class JBIDE675Test extends VpeTest {

	private static final String IMPORT_PROJECT_NAME = "jsfTest";

	private static final String TEST_PAGE_NAME = "JBIDE/675/testChangeOnUserInputTextNode.xhtml";

	public JBIDE675Test(String name) {
		super(name);
	}

	/**
	 * Tests Base Input on Source Page
	 * 
	 * @throws Throwable
	 */
	public void testBaseTextInputOnPage() throws Throwable {
		// wait
		TestUtil.waitForJobs();
		// set exception
		setException(null);
		// Tests CA
		// get test page path
		IFile file = (IFile) TestUtil.getComponentPath(TEST_PAGE_NAME,
				IMPORT_PROJECT_NAME);
		assertNotNull("Could not open specified file " + TEST_PAGE_NAME, file);

		IEditorInput input = new FileEditorInput(file);

		assertNotNull("Editor input is null", input);

		// open and get editor
		JSPMultiPageEditor part = openEditor(input);

		StyledText styledText = part.getSourceEditor().getTextViewer()
				.getTextWidget();

		for (int i = 0; i < 20; i++) {

			styledText.setCaretOffset(339);
			IndexedRegion treeNode = ContentAssistUtils.getNodeAt(part
					.getSourceEditor().getTextViewer(), 339);
			Node node = (Node) treeNode;
			assertNotNull(node);

			VpeController vpeController = getVpeController(part);

			VpeDomMapping domMapping = vpeController.getDomMapping();

			VpeNodeMapping nodeMapping = domMapping.getNodeMapping(node);

			assertNotNull(nodeMapping);

			nsIDOMNode span = nodeMapping.getVisualNode();

			nsIDOMNode textNode = span.getFirstChild();

			assertEquals(textNode.getNodeType(), nsIDOMNode.TEXT_NODE);

			assertNotNull(textNode.getNodeValue());

			assertEquals(textNode.getNodeValue().trim(), node.getNodeValue()
					.trim());

			styledText.insert("t");
		}
	}
	/**
	 * Tests tag Input on Source Page
	 * 
	 * @throws Throwable
	 */
	public void testBaseTagInputOnPage() throws Throwable {
		// wait
		TestUtil.waitForJobs();
		// set exception
		setException(null);
		// Tests CA
		// get test page path
		IFile file = (IFile) TestUtil.getComponentPath("JBIDE/675/testUserInputOnTag.xhtml",
				IMPORT_PROJECT_NAME);
		assertNotNull("Could not open specified file " + "JBIDE/675/testUserInputOnTag.xhtml", file);

		IEditorInput input = new FileEditorInput(file);

		assertNotNull("Editor input is null", input);

		// open and get editor
		JSPMultiPageEditor part = openEditor(input);

		StyledText styledText = part.getSourceEditor().getTextViewer()
				.getTextWidget();

		for (int i = 0; i < 20; i++) {

			styledText.setCaretOffset(311);
			IndexedRegion treeNode = ContentAssistUtils.getNodeAt(part
					.getSourceEditor().getTextViewer(), 311);
			Node node = (Node) treeNode;
			assertNotNull(node);

			VpeController vpeController = getVpeController(part);

			VpeDomMapping domMapping = vpeController.getDomMapping();

			VpeNodeMapping nodeMapping = domMapping.getNodeMapping(node);

			assertNotNull(nodeMapping);

			nsIDOMNode div = nodeMapping.getVisualNode();

			nsIDOMNode span = div.getFirstChild();
			
			nsIDOMNode textNode = span.getFirstChild();

			assertEquals(textNode.getNodeType(), nsIDOMNode.TEXT_NODE);

			assertNotNull(textNode.getNodeValue());
			assertNotNull(node.getNodeName());
			assertEquals(textNode.getNodeValue().trim(), node.getNodeName()
					.trim());

			styledText.insert("t");
		}
	}

}
