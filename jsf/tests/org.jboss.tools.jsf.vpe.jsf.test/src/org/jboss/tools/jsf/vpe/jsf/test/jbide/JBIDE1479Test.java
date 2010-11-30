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
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.xml.core.internal.provisional.format.FormatProcessorXML;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.base.test.TestUtil;
import org.jboss.tools.vpe.base.test.VpeTest;

/**
 * @author Max Areshkau
 * 
 * test for JBIDE-1479
 *
 */
public class JBIDE1479Test extends VpeTest {

	public static final String TEST_PAGE_NAME = "JBIDE/1479/employee.xhtml"; //$NON-NLS-1$
	
	public JBIDE1479Test(String name) {
		super(name);
	}
	
	public void testJBIDE1479() throws Throwable {
		// wait
		setException(null);
		// get test page path
		final IFile file = (IFile) TestUtil.getComponentPath(
				TEST_PAGE_NAME, JsfAllTests.IMPORT_PROJECT_NAME);

		
		assertNotNull("Could not open specified file " + TEST_PAGE_NAME, //$NON-NLS-1$
				file);
		
		IEditorInput input = new FileEditorInput(file);
		
		assertNotNull("Editor input is null", input); //$NON-NLS-1$

		
		JSPMultiPageEditor  part = openEditor(input);
		TestUtil.waitForIdle(120*1000);
		assertNotNull(part);
		
		Job job = new WorkspaceJob("Test JBIDE-1479"){ //$NON-NLS-1$
			
            @Override
			public IStatus runInWorkspace(IProgressMonitor monitor) {
                try {
                    new FormatProcessorXML().formatFile(file);
                }catch (Throwable exception){
                /*
                 * Here we test JBIDE-1479, if eclipse crashed we won't get any
                 *  exception, so we just ignore it's.
                 */
                } 
                return Status.OK_STATUS;
            } 
		};
		job.setPriority(Job.SHORT);
		job.schedule(0L);
		job.join();
		TestUtil.waitForIdle(15*1000*60);
		TestUtil.delay(1000L);
		closeEditors();
		
/*
 * we ignore this code, because we are testint JBIDE-1479,
 * it's test fot crash of eclipse.And if we modifying content from non-ui thread, we almost
 * always will get SWTException 'access violation'.
 */
//		if(getException()!=null) {
//			throw getException();
//		}
	}
	
}
