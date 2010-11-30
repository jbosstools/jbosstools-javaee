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
import org.jboss.tools.vpe.xulrunner.editor.XulRunnerEditor;
import org.mozilla.interfaces.nsIDOMNode;

/**
 * @author mareshkau
 *
 */
public class JBIDE3163Test extends VpeTest{

	public JBIDE3163Test(String name) {
		super(name);
	}

	public void testJBIDE3163() throws Exception {
		setException(null);
        IFile file = (IFile) TestUtil.getComponentPath("JBIDE/3163/jbide3163.html",  //$NON-NLS-1$
        		JsfAllTests.IMPORT_PROJECT_NAME);
		IEditorInput editorInput = new FileEditorInput(file);
		JSPMultiPageEditor part =  openEditor(editorInput);
		VpeController controller = TestUtil.getVpeController(part);
		XulRunnerEditor xulRunnerEditor = controller.getXulRunnerEditor();
		int position = TestUtil.getLinePositionOffcet(part.getSourceEditor().getTextViewer(), 1, 6);
		part.getSourceEditor().getTextViewer().setSelectedRange(position, 0);
		assertTrue("Show be selected Text Node",xulRunnerEditor.getLastSelectedNode().getNodeType()==nsIDOMNode.TEXT_NODE); //$NON-NLS-1$
		position =  TestUtil.getLinePositionOffcet(part.getSourceEditor().getTextViewer(), 1, 7);
		part.getSourceEditor().getTextViewer().setSelectedRange(position, 0);
		assertTrue("Selected should be Element Node",xulRunnerEditor.getLastSelectedNode().getNodeType()==nsIDOMNode.ELEMENT_NODE); //$NON-NLS-1$
		position = TestUtil.getLinePositionOffcet(part.getSourceEditor().getTextViewer(), 1, 15);
		part.getSourceEditor().getTextViewer().setSelectedRange(position, 0);
		assertTrue("Selected should be Text Node",xulRunnerEditor.getLastSelectedNode().getNodeType()==nsIDOMNode.TEXT_NODE); //$NON-NLS-1$
		position = TestUtil.getLinePositionOffcet(part.getSourceEditor().getTextViewer(), 1, 20);
		part.getSourceEditor().getTextViewer().setSelectedRange(position, 0);
		assertTrue("Selected should be Text Node",xulRunnerEditor.getLastSelectedNode().getNodeType()==nsIDOMNode.TEXT_NODE); //$NON-NLS-1$
		position = TestUtil.getLinePositionOffcet(part.getSourceEditor().getTextViewer(), 1, 25);
		part.getSourceEditor().getTextViewer().setSelectedRange(position, 0);
		assertTrue("Selected should be Text Node",xulRunnerEditor.getLastSelectedNode().getNodeType()==nsIDOMNode.TEXT_NODE); //$NON-NLS-1$
		position = TestUtil.getLinePositionOffcet(part.getSourceEditor().getTextViewer(), 1, 26);
		part.getSourceEditor().getTextViewer().setSelectedRange(position, 0);
		assertTrue("Selected should be Element Node",xulRunnerEditor.getLastSelectedNode().getNodeType()==nsIDOMNode.ELEMENT_NODE); //$NON-NLS-1$
		
	}
}
