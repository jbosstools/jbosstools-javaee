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
import org.eclipse.swt.custom.StyledTextContent;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.progress.UIJob;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;

/**
 * @author Max Areshkau
 * 
 * test for JBIDE-1479
 *
 */
public class JBIDE1479Test extends VpeTest {

	public static final String IMPORT_PROJECT_NAME = "jsfTest";
	
	public static final String TEST_PAGE_NAME = "JBIDE/1479/employee.xhtml";
	
	public JBIDE1479Test(String name) {
		super(name);
	}
	
	public void testJBIDE1479() throws Throwable {
		// wait
		TestUtil.waitForJobs();
		setException(null);
		// get test page path
		final IFile file = (IFile) TestUtil.getComponentPath(
				TEST_PAGE_NAME, IMPORT_PROJECT_NAME);

		
		assertNotNull("Could not open specified file " + TEST_PAGE_NAME,
				file);
		
		IEditorInput input = new FileEditorInput(file);
		
		assertNotNull("Editor input is null", input);

		
		TestUtil.waitForJobs();
		JSPMultiPageEditor  part = openEditor(input);
		TestUtil.delay(20000L);
		assertNotNull(part);
		final StyledTextContent  content= part.getSourceEditor().getTextViewer().getTextWidget().getContent();
		 
		Job job = new UIJob("Test"){
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				for(int i=1000;i<content.getCharCount();i+=10) {
					if(content.getTextRange(i, 1).charAt(0) ==' ') {
						content.replaceTextRange(i, 2, content.getTextRange(i, 1)+" ");
					}
				}
				return Status.OK_STATUS;
			}

		};
		job.setPriority(Job.SHORT);
		job.schedule(0L);
		TestUtil.delay(15000L);
		TestUtil.waitForJobs();
		

		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
								.closeAllEditors(false);

		if(getException()!=null) {
			throw getException();
		}
	}
	
}
