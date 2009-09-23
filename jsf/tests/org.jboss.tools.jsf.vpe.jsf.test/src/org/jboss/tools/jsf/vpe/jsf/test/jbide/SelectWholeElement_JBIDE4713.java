/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.jsf.test.jbide;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.vpe.editor.VpeController;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.SelectionUtil;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;

/**
 * Tests for JIRA issue JBIDE-4713:
 * Visual selection isn't correct from selection bar.
 * (https://jira.jboss.org/jira/browse/JBIDE-4713 )
 * 
 * @author yradtsevich
 */
public class SelectWholeElement_JBIDE4713 extends VpeTest {
	private static final String TEST_PAGE_NAME
			= "JBIDE/4713/SelectWholeElement_JBIDE4713.html"; //$NON-NLS-1$
	private static final Point SELECTION_START = new Point(5, 13);
	private static final Point SELECTION_END = new Point(5, 46);
	private static final String SELECTED_ELEMENT_ID
			= "selected-element"; //$NON-NLS-1$

	public SelectWholeElement_JBIDE4713(String name) {
		super(name);
	}

	public void testSelectWholeElement() throws Throwable {
		VpeController vpeController = openInVpe(JsfAllTests.IMPORT_PROJECT_NAME,
				TEST_PAGE_NAME);

		StructuredTextViewer textViewer = vpeController.getSourceEditor()
				.getTextViewer();
		StyledText textWidget = textViewer.getTextWidget();

		int selectionStartOffset = TestUtil.getLinePositionOffcet(
				textViewer, SELECTION_START.x, SELECTION_START.y);
		int selectionEndOffset = TestUtil.getLinePositionOffcet(
				textViewer, SELECTION_END.x, SELECTION_END.y);

		textViewer.setSelectedRange(selectionStartOffset,
				selectionEndOffset - selectionStartOffset);
		TestUtil.waitForIdle();

		nsIDOMNode selectedNode = SelectionUtil.getLastSelectedNode(
				vpeController.getPageContext()); 

		assertTrue(selectedNode.getNodeType() == nsIDOMNode.ELEMENT_NODE);
		nsIDOMElement element = (nsIDOMElement)
				selectedNode.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);

		assertEquals(SELECTED_ELEMENT_ID, element.getAttribute(HTML.ATTR_ID));
	}
}
