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
package org.jboss.tools.jsf.vpe.jsf.test.perfomance;

import java.util.Random;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
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
 * 
 * @author dsakovich@exadel.com
 * 
 * Perfomance tests for JBIDE-1619
 * 
 */
public class PerfomanceTest extends VpeTest {

    public static final String IMPORT_PROJECT_NAME = "perfomanceTest";

    private static final String TEST_PAGE_NAME = "employee.xhtml";
    private static final String TEST_PAGE1_NAME = "home.xhtml";
    private static final String TEST_PAGE2_NAME = "addEmployee.xhtml";
    private static final String TEST_PAGE3_NAME = "pdataEdit.xhtml";

    private static final String ANY_TAG = "<h:outputText value=\"Any Text\"/>";

    public PerfomanceTest(String name) {
	super(name);
	setCheckWarning(false);
    }

    /**
     * Test jbide-1105
     * 
     * @throws Throwable
     */
    public void testJBIDE1105() throws Throwable {
	// wait
	TestUtil.waitForJobs();
	setException(null);
	// get test page path
	IFile file = (IFile) TestUtil.getWebContentPath(TEST_PAGE_NAME,
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
		for (int i = 0; i < 20; i++) {
		    int offset = styledText.getOffsetAtLine(21);
		    styledText.setCaretOffset(offset - delimiter.length());
		    styledText.insert(delimiter);
		    TestUtil.delay(500L);
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

    /**
     * Testing work with big project
     * 
     * @throws Throwable
     */
    public void testWorkWithBigProject() throws Throwable {
	// wait
	TestUtil.waitForJobs();
	setException(null);
	// get test page path
	IFile file = (IFile) TestUtil.getWebContentPath(TEST_PAGE1_NAME,
		IMPORT_PROJECT_NAME);

	assertNotNull("Could not open specified file " + TEST_PAGE1_NAME, file);

	IEditorInput input = new FileEditorInput(file);

	assertNotNull("Editor input is null", input);

	JSPMultiPageEditor parts = openEditor(input);
	assertNotNull(parts);

	TestUtil.delay(5000L);
	TestUtil.waitForJobs();

	// //////////////////////

	file = (IFile) TestUtil.getWebContentPath(TEST_PAGE_NAME,
		IMPORT_PROJECT_NAME);

	assertNotNull("Could not open specified file " + TEST_PAGE_NAME, file);

	input = new FileEditorInput(file);

	assertNotNull("Editor input is null", input);

	parts = openEditor(input);
	assertNotNull(parts);
	TestUtil.delay(5000L);
	TestUtil.waitForJobs();
	// //////////////////////
	file = (IFile) TestUtil.getWebContentPath(TEST_PAGE2_NAME,
		IMPORT_PROJECT_NAME);

	assertNotNull("Could not open specified file " + TEST_PAGE2_NAME, file);

	input = new FileEditorInput(file);

	assertNotNull("Editor input is null", input);

	parts = openEditor(input);
	assertNotNull(parts);
	TestUtil.delay(5000L);
	TestUtil.waitForJobs();
	// //////////////////////
	file = (IFile) TestUtil.getWebContentPath(TEST_PAGE3_NAME,
		IMPORT_PROJECT_NAME);

	assertNotNull("Could not open specified file " + TEST_PAGE3_NAME, file);

	input = new FileEditorInput(file);

	assertNotNull("Editor input is null", input);

	parts = openEditor(input);
	assertNotNull(parts);
	TestUtil.delay(5000L);
	TestUtil.waitForJobs();

	// ///////////////////////
	PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
		.closeAllEditors(false);

	if (getException() != null) {
	    throw getException();
	}
    }

    /**
     * Testing work with big jsf page
     * 
     * @throws Throwable
     */
    public void testWorkWithBigFiles() throws Throwable {
	// wait
	TestUtil.waitForJobs();
	setException(null);
	// Test page
	pageTest(TEST_PAGE_NAME, "test - work VPE with large files");

	if (getException() != null) {
	    throw getException();
	}
    }

    /**
     * 
     * @param pageName
     * @param testName
     * @throws CoreException
     */
    private void pageTest(String pageName, String testName)
	    throws CoreException {

	IFile file = (IFile) TestUtil.getWebContentPath(pageName,
		IMPORT_PROJECT_NAME);

	assertNotNull("Could not open specified file " + TEST_PAGE_NAME, file);

	IEditorInput input = new FileEditorInput(file);

	assertNotNull("Editor input is null", input);

	TestUtil.waitForJobs();
	final JSPMultiPageEditor parts = openEditor(input);
	TestUtil.delay(2000L);
	assertNotNull(parts);

	Job job = new UIJob(testName) {
	    @Override
	    public IStatus runInUIThread(IProgressMonitor monitor) {
		StyledText styledText = parts.getSourceEditor().getTextViewer()
			.getTextWidget();
		String delimiter = styledText.getLineDelimiter();
		int offset = styledText.getOffsetAtLine(21);
		styledText.setCaretOffset(offset);
		// Add any tag in source
		for (int i = 0; i < ANY_TAG.length(); i++) {
		    styledText.insert(String.valueOf(ANY_TAG.charAt(i)));
		    offset++;
		    styledText.setCaretOffset(offset);
		    TestUtil.delay(200);
		}
		styledText.insert(delimiter);
		// Add any text(newline) in text
		Random random = new Random();
		for (int i = 0; i < 20; i++) {
		    int line = random.nextInt(styledText.getLineCount());
		    offset = styledText.getOffsetAtLine(line);
		    styledText.setCaretOffset(offset - delimiter.length());
		    styledText.insert(delimiter);
		    TestUtil.delay(500L);
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

    }

}
