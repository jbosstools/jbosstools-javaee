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
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.ui.util.ModelUtilities;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.vpe.editor.VpeController;
import org.jboss.tools.vpe.ui.test.VpeTest;

/**
 * @author yradtsevich
 *
 */
public abstract class ContextMenuTestAbstract extends VpeTest {
	protected VpeController vpeController;
	protected StructuredTextEditor sourceEditor;
	protected StructuredTextViewer textViewer;
	protected StyledText textWidget;
	protected IUndoManager undoManager;
	protected XModelObject insertionItem;
	
	public ContextMenuTestAbstract(String name) {
		super(name);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		vpeController = openInVpe(JsfAllTests.IMPORT_PROJECT_NAME,
				getTestPagePath());
		sourceEditor = vpeController.getSourceEditor();
		textViewer = sourceEditor.getTextViewer();
		textWidget = textViewer.getTextWidget();
		undoManager = textViewer.getUndoManager();
		insertionItem = ModelUtilities.getPreferenceModel()
				.getByPath(getInsertionItemPath());
	}
	
	protected abstract String getTestPagePath();
	protected abstract String  getInsertionItemPath();
}
