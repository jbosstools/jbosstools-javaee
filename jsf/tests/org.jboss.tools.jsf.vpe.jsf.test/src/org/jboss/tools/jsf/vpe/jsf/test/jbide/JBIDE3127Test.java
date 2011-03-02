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
import org.jboss.tools.vpe.editor.VpeEditorPart;

/**
 * Test case for JBIDE-3127
 * 
 * @author mareshkau
 *
 */
public class JBIDE3127Test extends VpeTest{

	public JBIDE3127Test(String name) {
		super(name);
	}
	
	public void testJBIDE3127() throws Exception {
		setException(null);
        IFile file = (IFile) TestUtil.getComponentPath("JBIDE/3127/jbide3127.xhtml", //$NON-NLS-1$
        		JsfAllTests.IMPORT_PROJECT_NAME);
        IEditorInput input = new FileEditorInput(file);
        JSPMultiPageEditor editor = openEditor(input);
        VpeController vpeController = TestUtil.getVpeController(editor);
        int offcet  = TestUtil.getLinePositionOffcet(editor.getSourceEditor().getTextViewer(), 13, 78);
        editor.getSourceEditor().getTextViewer().setSelectedRange(offcet, 1);
        assertTrue("VE sould be visible", vpeController.isVisualEditorVisible()); //$NON-NLS-1$
        assertTrue("It's should be a div","DIV".equalsIgnoreCase(vpeController.getXulRunnerEditor().getSelectedElement().getNodeName()));  //$NON-NLS-1$//$NON-NLS-2$
        VpeEditorPart editorPart = ((VpeEditorPart)editor.getVisualEditor());
        editorPart.maximizeSource();
        assertFalse("Visual part shouldn't be visible",vpeController.isVisualEditorVisible()); //$NON-NLS-1$
        //change source code
        editor.getSourceEditor().getTextViewer().getTextWidget().replaceTextRange(offcet-20, "replaced text".length(), "replaced text"); //$NON-NLS-1$ //$NON-NLS-2$
        assertFalse("Synced should be false",vpeController.isSynced()); //$NON-NLS-1$
        editorPart.maximizeVisual();
        vpeController.visualRefresh();
        //wait while refresh jobs start
        TestUtil.delay(500);
        TestUtil.waitForJobs();
        if(getException()!=null) {
        	throw new Exception(getException());
        }
	}

}
