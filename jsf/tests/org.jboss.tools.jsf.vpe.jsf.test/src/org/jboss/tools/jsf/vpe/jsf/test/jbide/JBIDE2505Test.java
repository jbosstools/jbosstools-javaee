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
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.base.test.TestUtil;
import org.jboss.tools.vpe.base.test.VpeTest;
import org.jboss.tools.vpe.editor.VpeController;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsISelectionController;

/**
 * @author mareshkau
 *
 */
public class JBIDE2505Test extends VpeTest {

	public JBIDE2505Test(String name) {
		super(name);
	}
	
	/**
	 * Tests inner nodes include URI
	 * 
	 * @throws Throwable
	 */
    //we process selections only with reasons, but when we select programticly,
    //we get exception with selection no reason
	public void _testCursorForJSPElements() throws Throwable {
		// wait
		TestUtil.waitForJobs();
		// set exception
		setException(null);

        //test for element node
		testCaretManupulationWithoutElSubstitution("JBIDE/2505/testJBIDE2505.jsp",11, 34); //$NON-NLS-1$
        //test for sourceNode
		testCaretManupulationWithoutElSubstitution("JBIDE/2505/testJBIDE2505.jsp",12, 58); //$NON-NLS-1$
        if(getException()!=null) {
        	throw getException();
        }
	}
	/**
	 * Tests inner nodes include URI
	 * 
	 * @throws Throwable
	 */
	public void testCursorXHTMLJSEL() throws Throwable {
		// wait
		TestUtil.waitForJobs();
		// set exception
		setException(null);

        //test for element node
		testCaretManupulationWithElSubstitution("JBIDE/2505/testJBIDE2505.xhtml",14, 10); //$NON-NLS-1$
        //test for sourceNode
		testCaretManupulationWithElSubstitution("JBIDE/2505/testJBIDE2505.xhtml",15, 27); //$NON-NLS-1$
        if(getException()!=null) {
        	throw getException();
        }
	}
	

	private void testCaretManupulationWithoutElSubstitution(String fileName, int sourceLine, int positioninLine) throws Throwable {
        // get test page path
        IFile file = (IFile) TestUtil.getComponentPath(fileName,
        		JsfAllTests.IMPORT_PROJECT_NAME);
		assertNotNull("Could not open specified file. componentPage = " + fileName//$NON-NLS-1$ 
				+ ";projectName = " + JsfAllTests.IMPORT_PROJECT_NAME, file);//$NON-NLS-1$

        IEditorInput input = new FileEditorInput(file);

        assertNotNull("Editor input is null", input); //$NON-NLS-1$

        // open and get editor
        JSPMultiPageEditor part = openEditor(input);
        
        int offset = TestUtil.getLinePositionOffcet(part.getSourceEditor().getTextViewer(),sourceLine, positioninLine);
		// get editor control
		StyledText styledText = part.getSourceEditor().getTextViewer()
				.getTextWidget();
		
        part.getSourceEditor().getTextViewer().getTextWidget().setCaretOffset(offset);

        
        
        VpeController vpeController = TestUtil.getVpeController(part);
        vpeController.sourceSelectionChanged();
        
        for (int i=0;i<10;i++) {
        	
	        nsIDOMNode domNode = vpeController.getXulRunnerEditor().getSelectedElement();
	        assertNotNull(domNode);
	        
	        //we process selections only with reasons, but when we select programticly,
	        //we get exception with selection no reason
	        vpeController.getVisualSelectionController().getSelection(nsISelectionController.SELECTION_NORMAL).collapse(domNode, i);
//	        vpeController.visualRefresh();
	        
	        assertEquals("Cursor position doesn't equals",offset+i,styledText.getCaretOffset()); //$NON-NLS-1$
        }
	}
	
	private void testCaretManupulationWithElSubstitution(String fileName, int sourceLine, int positioninLine) throws Throwable {
        // get test page path
        IFile file = (IFile) TestUtil.getComponentPath(fileName,
        		JsfAllTests.IMPORT_PROJECT_NAME);
		assertNotNull("Could not open specified file. componentPage = " + fileName//$NON-NLS-1$ 
				+ ";projectName = " + JsfAllTests.IMPORT_PROJECT_NAME, file);//$NON-NLS-1$

        IEditorInput input = new FileEditorInput(file);

        assertNotNull("Editor input is null", input); //$NON-NLS-1$

        // open and get editor
        JSPMultiPageEditor part = openEditor(input);
        
        int offset = TestUtil.getLinePositionOffcet(part.getSourceEditor().getTextViewer(),sourceLine, positioninLine);
		// get editor control
		
        part.getSourceEditor().getTextViewer().getTextWidget().setCaretOffset(offset);

        
        
        VpeController vpeController = TestUtil.getVpeController(part);
        vpeController.sourceSelectionChanged();
        
        for (int i=0;i<10;i++) {
        	
	        nsIDOMNode domNode = vpeController.getXulRunnerEditor().getSelectedElement();
	        assertNotNull(domNode);
	        vpeController.visualRefresh();
        }
	}
}
