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

import org.eclipse.jface.text.IUndoManager;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.ui.util.ModelUtilities;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.vpe.editor.VpeController;
import org.jboss.tools.vpe.editor.menu.InsertType;
import org.jboss.tools.vpe.editor.menu.action.InsertAction2;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;

/**
 * Test for JIRA issue JBIDE-3519: Ctrl+Z (Undo) doesn't work
 * properly for "Replace With" operation from context menu.
 * (https://jira.jboss.org/jira/browse/JBIDE-3519 )
 * 
 * @author yradtsevich
 */
public class JBIDE3519Test extends VpeTest {
	private static final Point SELECTION_START = new Point(5, 9);
	private static final Point SELECTION_END = new Point(7, 23);
	private static final String TEST_PAGE_NAME
			= "JBIDE/3519/JBIDE-3519.xhtml";	//$NON-NLS-1$
	private static final String INSERTION_ITEM_PATH
			= "%Palette%/JSF/HTML/column";		//$NON-NLS-1$
	
	private VpeController vpeController;
	private StructuredTextEditor sourceEditor;
	private StructuredTextViewer textViewer;
	private StyledText textWidget;
	private String originalText;	
	private IUndoManager undoManager;
	private int selectionStartOffset;
	private int selectionEndOffset;
	private XModelObject insertionItem;

	public JBIDE3519Test(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		vpeController = openInVpe(JsfAllTests.IMPORT_PROJECT_NAME,
				TEST_PAGE_NAME);
		sourceEditor = vpeController.getSourceEditor();
		textViewer = sourceEditor.getTextViewer();
		textWidget = textViewer.getTextWidget();
		originalText = textWidget.getText();
		
		undoManager = textViewer.getUndoManager();
		
		selectionStartOffset = TestUtil.getLinePositionOffcet(
				textViewer, SELECTION_START.x, SELECTION_START.y);
		selectionEndOffset = TestUtil.getLinePositionOffcet(
				textViewer, SELECTION_END.x, SELECTION_END.y);
		
		insertionItem = ModelUtilities.getPreferenceModel()
				.getByPath(INSERTION_ITEM_PATH);
	}

// known issue, will fail
//	public void testInsertAround() {
//		insertAndUndo(InsertType.INSERT_AROUND);
//	}

	public void testInsertAfter() {
		insertAndUndo(InsertType.INSERT_AFTER);
	}

	public void testInsertBefore() {
		insertAndUndo(InsertType.INSERT_BEFORE);
	}

	public void testInsertInto() {
		insertAndUndo(InsertType.INSERT_INTO);
	}

	public void testReplaceWith() throws Throwable {
		insertAndUndo(InsertType.REPLACE_WITH);
	}

	private void insertAndUndo(final InsertType insertType) {
		textWidget.setSelection(selectionStartOffset, selectionEndOffset);
		final InsertAction2 insertAction = new InsertAction2(
				"Insert Action", insertionItem,				//$NON-NLS-1$
				sourceEditor, insertType);
		insertAction.run();
		undoManager.undo();
		assertEquals(insertType.getMessage() 
					+ " action has been performed. The content is "//$NON-NLS-1$
					+ " not reverted after UNDO operation.",	   //$NON-NLS-1$
				originalText, textWidget.getText());
	}
}
