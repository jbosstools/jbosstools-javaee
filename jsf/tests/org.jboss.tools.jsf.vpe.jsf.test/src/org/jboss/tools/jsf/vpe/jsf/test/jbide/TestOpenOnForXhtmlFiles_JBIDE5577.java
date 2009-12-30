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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.editor.VpeController;
import org.jboss.tools.vpe.editor.util.SelectionUtil;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;
import org.mozilla.interfaces.nsIDOMNode;
import org.w3c.dom.Node;

/**
 * Checks that openOn will correctly resolve JSF page mapping. 
 * 
 * @author dmaliarevich
 */
public class TestOpenOnForXhtmlFiles_JBIDE5577 extends VpeTest {

	public TestOpenOnForXhtmlFiles_JBIDE5577(String name) {
		super(name);
	}

	public void testOpenOnForXhtmlFiles() throws CoreException {
		/*
		 * Open index.html
		 */
		IFile file = (IFile) TestUtil.getWebContentPath("index.html", JsfAllTests.IMPORT_JSF_20_PROJECT_NAME); //$NON-NLS-1$
		assertNotNull("Could not find the file for the editor", file);		//$NON-NLS-1$
		IEditorInput input = new FileEditorInput(file);
		assertNotNull("Editor input is null", input);		//$NON-NLS-1$
		JSPMultiPageEditor part = openEditor(input);
		VpeController vpeController = TestUtil.getVpeController(part);
		
		/*
		 * Make openOn action an <a> tag.
		 */
		int position = TestUtil.getLinePositionOffcet(vpeController.getSourceEditor().getTextViewer(), 7, 29);
		Node sourceNode = SelectionUtil.getNodeBySourcePosition(vpeController.getSourceEditor(), position);
		nsIDOMNode domNode = vpeController.getDomMapping().getNearVisualNode(sourceNode);
		vpeController.getSourceBuilder().openOn(domNode);
		IEditorPart  activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		assertEquals("head2.xhtml file should be opened","head2.xhtml", activeEditor.getEditorInput().getName()); //$NON-NLS-1$ //$NON-NLS-2$
	}
}
