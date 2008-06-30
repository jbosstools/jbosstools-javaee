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

import java.util.Collection;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.editor.VpeController;
import org.jboss.tools.vpe.editor.mapping.VpeDomMapping;
import org.jboss.tools.vpe.editor.mapping.VpeNodeMapping;
import org.jboss.tools.vpe.editor.util.SelectionUtil;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;
import org.jboss.tools.vpe.xulrunner.editor.XulRunnerEditor;
import org.mozilla.interfaces.nsIDOMNode;
import org.w3c.dom.Node;

/**
 * 
 * @author sdzmitrovich
 * 
 *         http://jira.jboss.com/jira/browse/JBIDE-2362
 * 
 */
public class JsfJbide2362Test extends VpeTest {

	public static final String IMPORT_PROJECT_NAME = "jsfTest";

	private static final String SELECTION_PAGE_NAME = "JBIDE/2362/selection.jsp";
	private static final String EDITING_PAGE_NAME = "JBIDE/2362/editing.jsp";
	private static final String OUTPUT_TEXT = "outputText";
	private static final String INSERT_TEXT = "value=\"x\"";

	public JsfJbide2362Test(String name) {
		super(name);
	}

	/**
	 * It is simple selection test. We set cursor for each source node ( from
	 * VpeDomMapping). Then we compare visual nodes which was selected and which
	 * was associated with current source node.
	 * 
	 * @throws Throwable
	 */
	public void testSimpleSourceSelection() throws Throwable {

		// wait
		TestUtil.waitForJobs();
		// set exception
		setException(null);

		// get test page path
		IFile file = (IFile) TestUtil.getComponentPath(SELECTION_PAGE_NAME,
				IMPORT_PROJECT_NAME);

		IEditorInput input = new FileEditorInput(file);

		// open and get editor
		JSPMultiPageEditor part = openEditor(input);

		// get controller
		VpeController controller = getVpeController(part);
		assertNotNull(controller);

		// get dommapping
		VpeDomMapping domMapping = controller.getDomMapping();

		assertNotNull(domMapping);

		// get source map
		Map<Node, VpeNodeMapping> sourceMap = domMapping.getSourceMap();
		assertNotNull(sourceMap);

		// get collection of VpeNodeMapping
		Collection<VpeNodeMapping> mappings = sourceMap.values();
		assertNotNull(mappings);

		// get editor control
		StyledText styledText = part.getSourceEditor().getTextViewer()
				.getTextWidget();
		assertNotNull(styledText);

		// get xulrunner editor
		XulRunnerEditor xulRunnerEditor = controller.getXulRunnerEditor();
		assertNotNull(xulRunnerEditor);

		domMapping.printMapping();

		for (VpeNodeMapping nodeMapping : mappings) {

			/**
			 * exclude out DomDocument ( it is added to mapping specially ) and
			 * nodes without visual representation
			 */
			if (!(nodeMapping.getSourceNode() instanceof IDOMDocument)
					&& (nodeMapping.getVisualNode() != null)) {

				SelectionUtil.setSourceSelection(controller.getPageContext(),
						nodeMapping.getSourceNode(), 0, 0);

				// TestUtil.delay(50);

				assertNotNull(xulRunnerEditor.getLastSelectedNode());
				assertEquals(nodeMapping.getVisualNode(), xulRunnerEditor
						.getLastSelectedNode());
			}
		}

		// check exception
		if (getException() != null) {
			throw getException();
		}

	}

	// /**
	// * It is simple selection test.
	// *
	// * @throws Throwable
	// */
	// public void testSimpleVisualSelection() throws Throwable {
	//
	// // wait
	// TestUtil.waitForJobs();
	// // set exception
	// setException(null);
	//
	// // get test page path
	// IFile file = (IFile) TestUtil.getComponentPath(SELECTION_PAGE_NAME,
	// IMPORT_PROJECT_NAME);
	//
	// IEditorInput input = new FileEditorInput(file);
	//
	// // open and get editor
	// JSPMultiPageEditor part = openEditor(input);
	//
	// // get controller
	// VpeController controller = getVpeController(part);
	// assertNotNull(controller);
	//
	// // get dommapping
	// VpeDomMapping domMapping = controller.getDomMapping();
	//
	// assertNotNull(domMapping);
	//
	// // get source map
	// Map<nsIDOMNode, VpeNodeMapping> visualMap = domMapping.getVisualMap();
	// assertNotNull(visualMap);
	//
	// // get collection of VpeNodeMapping
	// Collection<VpeNodeMapping> mappings = visualMap.values();
	// assertNotNull(mappings);
	//
	// // get editor control
	// StyledText styledText = part.getSourceEditor().getTextViewer()
	// .getTextWidget();
	// assertNotNull(styledText);
	//
	// // get xulrunner editor
	// XulRunnerEditor xulRunnerEditor = controller.getXulRunnerEditor();
	// assertNotNull(xulRunnerEditor);
	//
	// VpeSelectionController selectionController = controller
	// .getVisualSelectionController();
	//
	// StructuredTextEditor sourceEditor = controller.getSourceEditor();
	// assertNotNull(sourceEditor);
	//
	// IStructuredModel model = NodesManagingUtil
	// .getStructuredModel(sourceEditor);
	// assertNotNull(model);
	//
	// for (VpeNodeMapping nodeMapping : mappings) {
	//
	// selectionController.getSelection(0).collapse(
	// nodeMapping.getVisualNode(), 0);
	//
	// TestUtil.delay(5000);
	//
	// Point range = SelectionUtil.getSourceSelection(sourceEditor);
	//
	// assertEquals(nodeMapping.getSourceNode(), SelectionUtil
	// .getSourceNodeByPosition(model, range.x));
	// }
	//
	// // check exception
	// if (getException() != null) {
	// throw getException();
	// }
	//
	// }

	/**
	 * It is simple selection test. We set cursor for each source node ( from
	 * VpeDomMapping). Then we compare visual nodes which was selected and which
	 * was associated with current source node.
	 * 
	 * @throws Throwable
	 */
	public void testEditingSourceSelection() throws Throwable {

		// wait
		TestUtil.waitForJobs();
		// set exception
		setException(null);

		// get test page path
		IFile file = (IFile) TestUtil.getComponentPath(EDITING_PAGE_NAME,
				IMPORT_PROJECT_NAME);

		IEditorInput input = new FileEditorInput(file);

		// open and get editor
		JSPMultiPageEditor part = openEditor(input);

		// get controller
		VpeController controller = getVpeController(part);
		assertNotNull(controller);

		// get dommapping
		VpeDomMapping domMapping = controller.getDomMapping();

		assertNotNull(domMapping);

		// get source map
		Map<Node, VpeNodeMapping> sourceMap = domMapping.getSourceMap();
		assertNotNull(sourceMap);

		// get collection of VpeNodeMapping
		Collection<VpeNodeMapping> mappings = sourceMap.values();
		assertNotNull(mappings);

		// get editor control
		StyledText styledText = part.getSourceEditor().getTextViewer()
				.getTextWidget();
		assertNotNull(styledText);

		// get xulrunner editor
		XulRunnerEditor xulRunnerEditor = controller.getXulRunnerEditor();
		assertNotNull(xulRunnerEditor);

		domMapping.printMapping();

		for (VpeNodeMapping nodeMapping : mappings) {

			/**
			 * exclude out DomDocument ( it is added to mapping specially ) and
			 * nodes without visual representation
			 */
			if (OUTPUT_TEXT.equals(nodeMapping.getSourceNode().getLocalName())) {

				IDOMElement element = (IDOMElement) nodeMapping.getSourceNode();

				SelectionUtil.setSourceSelection(controller.getPageContext(),
						element, 0, 0);

				nsIDOMNode firstSelectedNode = xulRunnerEditor
						.getLastSelectedNode();

				assertEquals(nodeMapping.getVisualNode(), firstSelectedNode);

				int endStart = element.getStartEndOffset() - 1;

				styledText.setCaretOffset(endStart);
				styledText.insert("  ");
				TestUtil.delay(50);
				for (int i = 0; i < INSERT_TEXT.length(); i++) {
					endStart++;
					styledText.setCaretOffset(endStart);
					styledText.insert(String.valueOf(INSERT_TEXT.charAt(i)));
					TestUtil.delay(50);
				}

				assertNotNull(xulRunnerEditor.getLastSelectedNode());
				assertEquals(firstSelectedNode, xulRunnerEditor
						.getLastSelectedNode());
			}
		}

		// check exception
		if (getException() != null) {
			throw getException();
		}

	}
}
