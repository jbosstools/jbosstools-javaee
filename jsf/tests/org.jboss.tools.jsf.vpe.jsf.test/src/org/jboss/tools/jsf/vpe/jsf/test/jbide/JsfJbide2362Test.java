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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.editor.VpeController;
import org.jboss.tools.vpe.editor.mapping.VpeDomMapping;
import org.jboss.tools.vpe.editor.mapping.VpeNodeMapping;
import org.jboss.tools.vpe.editor.util.SelectionUtil;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;
import org.jboss.tools.vpe.xulrunner.editor.XulRunnerEditor;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author sdzmitrovich
 * 
 *         http://jira.jboss.com/jira/browse/JBIDE-2362
 * 
 */
public class JsfJbide2362Test extends VpeTest {


	private static final String SELECTION_PAGE_NAME = "JBIDE/2362/selection.jsp"; //$NON-NLS-1$
	private static final String EDITING_PAGE_NAME = "JBIDE/2362/editing.jsp"; //$NON-NLS-1$

	private static final List<String> ELEMENTS;

	static {
		ELEMENTS = new ArrayList<String>();
		ELEMENTS.add("h:outputText"); //$NON-NLS-1$
		ELEMENTS.add("h:outputFormat"); //$NON-NLS-1$
		ELEMENTS.add("h:outputLabel"); //$NON-NLS-1$
		ELEMENTS.add("h:outputLink"); //$NON-NLS-1$
		ELEMENTS.add("h:inputText"); //$NON-NLS-1$
		ELEMENTS.add("h:inputTextarea"); //$NON-NLS-1$
		ELEMENTS.add("h:inputSecret"); //$NON-NLS-1$
	}

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
				JsfAllTests.IMPORT_PROJECT_NAME);

		IEditorInput input = new FileEditorInput(file);

		// open and get editor
		JSPMultiPageEditor part = openEditor(input);

		checkSourceSelection(part);

		// check exception
		if (getException() != null) {
			throw getException();
		}

	}

	/**
	 * This test checks selection after editing of source. If selection is not
	 * lost then test pass.
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
				JsfAllTests.IMPORT_PROJECT_NAME);

		IEditorInput input = new FileEditorInput(file);

		// open and get editor
		JSPMultiPageEditor part = openEditor(input);

		// get controller
		VpeController controller = TestUtil.getVpeController(part);
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

		IStructuredModel model = null;
		IDOMDocument document =null;
		try {
			model = StructuredModelManager.getModelManager()
			.getExistingModelForRead(controller.getSourceEditor().getTextViewer().getDocument());
			 document = ((IDOMModel)model).getDocument();

		for (String tag : ELEMENTS) {

			NodeList tagList = document.getElementsByTagName(tag);

			if (tagList.getLength() > 0) {

				Node node = tagList.item(0);

				SelectionUtil.setSourceSelection(controller.getPageContext(),
						node, 1, 0);

				assertEquals(domMapping.getNearNodeMapping(node)
						.getVisualNode(), xulRunnerEditor.getLastSelectedNode());

				String insertedString = null;
				int offset;
				if (node.getNodeType() == node.ELEMENT_NODE) {
					offset = ((IDOMElement) node).getStartEndOffset() - 1;
					insertedString = " value=\"x\" "; //$NON-NLS-1$
				} else {
					offset = ((IDOMNode) node).getStartOffset();
					insertedString = "someText"; //$NON-NLS-1$
				}

				for (int j = 0; j < insertedString.length(); j++) {
					styledText.setCaretOffset(offset + j);
					styledText.insert(String.valueOf(insertedString.charAt(j)));
					TestUtil.delay(50);
				}

				// wait
				TestUtil.delay(700);
				// wait
				assertNotNull(xulRunnerEditor.getLastSelectedNode());
			}
		}

		} finally {
			if(model!=null) {
				model.releaseFromRead();
			}
		}

		// check exception
		if (getException() != null) {
			throw getException();
		}

	}
}
