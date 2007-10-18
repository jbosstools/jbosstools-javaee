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
package org.jboss.tools.vpe.test.richfaces;

import java.util.Collection;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

/**
 * Class for testing all RichFaces components
 * 
 * @author dsakovich@exadel.com
 * 
 */
public class RichFacesComponentTest extends TestCase implements ILogListener {

    private final static String EDITOR_ID = "org.jboss.tools.jst.jsp.jspeditor.JSPTextEditor";
    private final static String TEST_PROJECT_JAR_PATH = "/richFacesTest.jar"; 
    
    // check warning log
    private final static boolean checkWarning = false;
    private boolean failureLog;
    private Collection<IPath> components = null;

    public RichFacesComponentTest(String name) {
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
	if (ImportRichFacesComponents.importRichFacesPages(Activator
		.getPluginResourcePath()
		+ TEST_PROJECT_JAR_PATH)) {
	    components = ImportRichFacesComponents.getComponentsPaths();
	}
	failureLog = false;
	waitForJobs();
	Platform.addLogListener(this);
	waitForJobs();
	delay(5000);
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
	ImportRichFacesComponents.removeProject();
	waitForJobs();
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
	while (Platform.getJobManager().currentJob() != null)
	    delay(5000);
    }

    public void testRichFacesComponent() throws PartInitException {
	waitForJobs();
	
	for (IPath componentPath : components) {
	    IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(
		    componentPath);
	    IEditorInput input = new FileEditorInput(file);
	    PlatformUI.getWorkbench().getActiveWorkbenchWindow()
		    .getActivePage().openEditor(input, EDITOR_ID, true);

	    waitForJobs();
	    delay(3000);
	    PlatformUI.getWorkbench().getActiveWorkbenchWindow()
		    .getActivePage().closeAllEditors(true);
	}
	assertEquals(failureLog, false);
    }

    public void logging(IStatus status, String plugin) {
	switch (status.getSeverity()) {
	case IStatus.ERROR:
	    failureLog = true;
	    break;
	case IStatus.WARNING:
	    if (checkWarning)
		failureLog = true;
	    break;
	default:
	    break;
	}

    }

}
