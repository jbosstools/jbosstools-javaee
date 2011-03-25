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
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.base.test.TestUtil;
import org.jboss.tools.vpe.base.test.VpeTest;
import org.jboss.tools.vpe.editor.VpeController;

/**
 * Test case for issue <a href='https://jira.jboss.org/jira/browse/JBIDE-3617'>JBIDE-3617</a>:
 * Exception is appeared if .css file has " #url " style class
 * 
 * 
 * @author yradtsevich
 */
public class JBIDE3617Test extends VpeTest {

	private static final String TEST_FILE_NAME = "JBIDE/3617/JBIDE-3617.jsp"; //$NON-NLS-1$
	private static final String INSERTED_STRING = "<link rel=\"stylesheet\" type=\"text/css\" media=\"print\" href=\"JBIDE-3617.css\">"; //$NON-NLS-1$
	private static final Point INSERTION_POINT = new Point(5, 9); 
	public JBIDE3617Test(String name) {
		super(name);
	}
	
	/**
	 * Tests <a href='https://jira.jboss.org/jira/browse/JBIDE-3617'>JBIDE-3617</a>
	 * 
	 * @throws Throwable
	 */
	public void testJBIDE3617() throws Throwable {
		// wait
		TestUtil.waitForJobs();

		setException(null);

		VpeController vpeController = openInVpe(JsfAllTests.IMPORT_PROJECT_NAME, TEST_FILE_NAME); //$NON-NLS-1$

		StructuredTextViewer textViewer = vpeController.getSourceEditor().getTextViewer();
		
		int offset = TestUtil.getLinePositionOffcet(textViewer, INSERTION_POINT.x, INSERTION_POINT.y);
		// get editor control
		StyledText styledText = textViewer.getTextWidget();

		styledText.setCaretOffset(offset);
		styledText.insert(INSERTED_STRING);
		
		// refresh the view 
		vpeController.visualRefresh();
		TestUtil.waitForJobs();
		try {
			TestUtil.delay();
		} catch (Throwable e) {
			fail("Seems like JBIDE-3617 has occured.\n" + e);//$NON-NLS-1$
		}

        if(getException()!=null) {
        	throw getException();
        }
	}
}
