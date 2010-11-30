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
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.base.test.TestUtil;
import org.jboss.tools.vpe.base.test.VpeTest;
import org.jboss.tools.vpe.editor.VpeController;

/**
 * Test class for JBIDE-2774
 * @author mareshkau
 *
 */
public class JBIDE2774Test extends VpeTest {

	/**
	 * 
	 * @param name
	 */
	public JBIDE2774Test(String name) {
		super(name);
	}
	
	/**
	 * Test case for jbide-2774
	 * 
	 * @throws Throwable
	 */
	public void testJBIDE2774() throws Throwable {
		setException(null);
		
		// get test page path
		IFile file = (IFile) TestUtil.getComponentPath("JBIDE/2774/jbide2774test.xhtml", //$NON-NLS-1$
				JsfAllTests.IMPORT_PROJECT_NAME);

		IEditorInput input = new FileEditorInput(file);

		// open and get editor
		JSPMultiPageEditor part = openEditor(input);

		
		int offcet = TestUtil.getLinePositionOffcet(part.getSourceEditor().getTextViewer(), 21, 25);
		//sets selection
		part.getSourceEditor().getTextViewer().setSelectedRange(offcet, 2);
		
		Point selectionRangeBeforeRefresh = part.getSourceEditor().getTextViewer().getSelectedRange();
		// get controller
		VpeController controller = TestUtil.getVpeController(part);
		assertNotNull(controller);
		
		controller.visualRefresh();
		TestUtil.delay(500);
		Point selectionRangeAfterRefresh =  part.getSourceEditor().getTextViewer().getSelectedRange();
		
		assertEquals("Selection should be before and after refresh equals", selectionRangeBeforeRefresh,selectionRangeAfterRefresh); //$NON-NLS-1$
		
		if(getException()!=null) {
			throw getException();
		}
	}

}
