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

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.base.test.TestUtil;
import org.jboss.tools.vpe.base.test.VpeTest;

/**
 * @author mareshkau
 * 
 *	Test case for JBIDE-2219
 */
public class JBIDE2219Test extends VpeTest {

	
	public JBIDE2219Test(String name) {
		super(name);
	}

	public void testJBIDE2219() throws Throwable {
		// wait
		TestUtil.waitForJobs();
		// set exception
		setException(null);
		// Tests CA
		// get test page path
		IFile file = (IFile) TestUtil.getComponentPath("JBIDE/2219/testJBIDE2219.xhtml", //$NON-NLS-1$
				JsfAllTests.IMPORT_PROJECT_NAME);
		assertNotNull("Could not open specified file " + "JBIDE/2219/testJBIDE2219.xhtml", file); //$NON-NLS-1$ //$NON-NLS-2$

		IEditorInput input = new FileEditorInput(file);

		assertNotNull("Editor input is null", input); //$NON-NLS-1$

		// open and get editor
		JSPMultiPageEditor part = openEditor(input);
		
		TestUtil.waitForJobs();
		
		StyledText styledText = part.getSourceEditor().getTextViewer()
				.getTextWidget();
		//sets caret in the begining of text
		styledText.setCaretOffset(0);
		assertTrue("Char count should be a 0", styledText.getCharCount()==0); //$NON-NLS-1$
		styledText.insert("Test "); //$NON-NLS-1$
		styledText.setSelection(0, 1);
		assertTrue("Char count shouldn't be a 0",styledText.getCharCount()>2); //$NON-NLS-1$
		TestUtil.delay(500);
		TestUtil.waitForJobs();
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeEditor(part, false);
        TestUtil.waitForJobs();
		//open again editor
		part = openEditor(input);
		TestUtil.waitForJobs();
		styledText = part.getSourceEditor().getTextViewer().getTextWidget();
		assertTrue("Number of chars should be a 0",styledText.getCharCount()==0); //$NON-NLS-1$
		part.close(false);
	}
}
