/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.facelets.test;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

/**
 * Class for testing all Seam components
 * 
 * @author dsakovich@exadel.com
 * 
 */
public class FaceletsComponentTest extends TestCase implements ILogListener {

    private final static String EDITOR_ID = "org.jboss.tools.jst.jsp.jspeditor.JSPTextEditor"; // $NON-NLS-1$
    private final static String TEST_PROJECT_JAR_PATH = "/faceletstest.jar"; // $NON-NLS-1$

    // check warning log
    private final static boolean checkWarning = false;
    private Throwable exception;

    public FaceletsComponentTest(String name) {
	super(name);
    }

    /**
     * Perform pre-test initialization.
     * 
     * @throws Exception
     * 
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
	super.setUp();

	// TODO: Use TestSetup to create and remove project once for all tests
	// not for every one
	if (ResourcesPlugin.getWorkspace().getRoot().findMember("faceletsTest") == null) {

	    ImportFaceletsComponents.importFaceletsPages(FaceletsTestPlugin
		    .getPluginResourcePath()
		    + TEST_PROJECT_JAR_PATH);

	    waitForJobs();
	    delay(5000);
	}
	Platform.addLogListener(this);
    }

    /**
     * Perform post-test cleanup.
     * 
     * @throws Exception
     * 
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
	super.tearDown();
	if (ResourcesPlugin.getWorkspace().getRoot().findMember("faceletsTest") != null) {
	    ImportFaceletsComponents.removeProject();
	}
	Platform.removeLogListener(this);
    }

    /**
     * Process UI input but do not return for the specified time interval.
     * 
     * @param waitTimeMillis
     *                the number of milliseconds
     */
    private void delay(long waitTimeMillis) {
	Display display = Display.getCurrent();
	if (display != null) {
	    long endTimeMillis = System.currentTimeMillis() + waitTimeMillis;
	    while (System.currentTimeMillis() < endTimeMillis) {
		if (!display.readAndDispatch())
		    display.sleep();
	    }
	    display.update();
	}
	// Otherwise, perform a simple sleep.
	else {
	    try {
		Thread.sleep(waitTimeMillis);
	    } catch (InterruptedException e) {
		// Ignored.
	    }
	}
    }

    /**
     * Wait until all background tasks are complete.
     */
    public void waitForJobs() {
	while (Job.getJobManager().currentJob() != null)
	    delay(5000);
    }

    public void testInsert() throws PartInitException, Throwable {
    	performTestForFaceletComponent("insert.xhtml"); // $NON-NLS-1$
    }

    public void testDefine() throws PartInitException, Throwable {
    	performTestForFaceletComponent("define.xhtml"); // $NON-NLS-1$
    	// TODO check that content from ui:defime element is shown
    	assertTrue("Defined content is not shown",false);
    }

    public void testComposite() throws PartInitException, Throwable {
    	performTestForFaceletComponent("composition.xhtml"); // $NON-NLS-1$
    	// TODO check that related and absolute path is processed
    	assertTrue("Template with absolute path is not included",false);
    	assertTrue("Template with related path is not included",false);    	
    }

    public void testComponent() throws PartInitException, Throwable {
    	performTestForFaceletComponent("component.xhtml"); // $NON-NLS-1$
    	// TODO check that content from ui:defime element is shown
    	//assertTrue("Component's content is not shown",false);
    }
    
    public void testRemove() throws PartInitException, Throwable {
    	performTestForFaceletComponent("remove.xhtml"); // $NON-NLS-1$
    	// TODO check that content in ui:remove isn't shown in VPE
    	assertTrue("Content inside ui:remove tag shouldn't be shown",false);
    }
    
    public void testDecorate() throws PartInitException, Throwable {
    	performTestForFaceletComponent("decorate.xhtml"); // $NON-NLS-1$
    	// TODO check that related and absolute path is processed
    	assertTrue("Template with absolute path is not included",false);
    	assertTrue("Template with related path is not included",false);    	
    }

    public void testRepeat() throws PartInitException, Throwable {
    	performTestForFaceletComponent("repeat.xhtml"); // $NON-NLS-1$
    	assertTrue("Component's content is not shown",false);    	
    }    
    
    public void testDebug() throws PartInitException, Throwable {
    	performTestForFaceletComponent("debug.xhtml"); // $NON-NLS-1$
    }
    
    public void testInclude() throws PartInitException, Throwable {
    	performTestForFaceletComponent("include.xhtml"); // $NON-NLS-1$
    	// TODO check that absolute and related path is processed
    	assertTrue("Template with absolute path is not included",false);
    	assertTrue("Template with related path is not included",false);    	
    }
    
    public void testFragment() throws PartInitException, Throwable {
    	performTestForFaceletComponent("fragment.xhtml"); // $NON-NLS-1$
    	// TODO check that fragment's content is showed
    	assertTrue("Fragment's content is not sown",false);
    }
    private void performTestForFaceletComponent(String componentPage)
	    throws PartInitException, Throwable {
	waitForJobs();

	exception = null;
	IPath componentPath = ImportFaceletsComponents
		.getComponentPath(componentPage);

	IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(
		componentPath);
	IEditorInput input = new FileEditorInput(file);

	PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
		.openEditor(input, EDITOR_ID, true);

	waitForJobs();
	delay(5000);
	PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
		.closeAllEditors(true);

	if (exception != null) {
	    throw exception;
	}
    }

    public void logging(IStatus status, String plugin) {
	switch (status.getSeverity()) {
	case IStatus.ERROR:
	    exception = status.getException();
	    break;
	case IStatus.WARNING:
	    if (checkWarning)
		exception = status.getException();
	    break;
	default:
	    break;
	}

    }

}
