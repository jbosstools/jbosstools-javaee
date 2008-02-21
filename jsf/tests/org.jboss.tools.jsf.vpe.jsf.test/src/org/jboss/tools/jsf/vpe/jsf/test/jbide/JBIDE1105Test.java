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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.progress.UIJob;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;

/**
 * @author mareshkau
 *
 */
public class JBIDE1105Test extends VpeTest {

	public static final String IMPORT_PROJECT_NAME = "jsfTest";
	
	private static final String TEST_PAGE_NAME="JBIDE/1105/employee.xhtml";
	
	public JBIDE1105Test(String name) {
		super(name);
	}
	
	public void testJBIDE1105() throws Throwable {
	// wait
	TestUtil.waitForJobs();
	setException(null);
	// get test page path
	IFile file = (IFile) TestUtil.getComponentPath(TEST_PAGE_NAME,
		IMPORT_PROJECT_NAME);

	assertNotNull("Could not open specified file " + TEST_PAGE_NAME, file);

	IEditorInput input = new FileEditorInput(file);

	assertNotNull("Editor input is null", input);

	TestUtil.waitForJobs();
	final JSPMultiPageEditor parts = openEditor(input);
	TestUtil.delay(1000L);
	assertNotNull(parts);

	Job job = new UIJob("Test JBIDE-1105") {
	    @Override
	    public IStatus runInUIThread(IProgressMonitor monitor) {
		StyledText styledText = parts.getSourceEditor().getTextViewer()
			.getTextWidget();
		String delimiter = styledText.getLineDelimiter();
		for (int i = 0; i < 200; i++) {
		    int offset = styledText.getOffsetAtLine(21);
		    styledText.setCaretOffset(offset - delimiter.length());
		    styledText.insert(delimiter);
		    TestUtil.delay(50L);
		}
		for (int i = 0; i < 200; i++) {
		    int offset = styledText.getOffsetAtLine(23);
		    styledText.setCaretOffset(offset - " Test ".length());
		    styledText.insert(" Test ");
		    TestUtil.delay(50L);
		}
		return Status.OK_STATUS;
	    }

	};
	job.setPriority(Job.SHORT);
	job.schedule(0L);
	TestUtil.delay(1000L);
	TestUtil.waitForJobs();

	PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
		.closeAllEditors(false);

	if (getException() != null) {
	    throw getException();
	}
	}

}
