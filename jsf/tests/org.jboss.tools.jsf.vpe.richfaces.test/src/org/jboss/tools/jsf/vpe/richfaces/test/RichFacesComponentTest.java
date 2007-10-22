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
package org.jboss.tools.jsf.vpe.richfaces.test;

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
 * Class for testing all RichFaces components
 * 
 * @author dsakovich@exadel.com
 * 
 */
public class RichFacesComponentTest extends TestCase implements ILogListener {

	private final static String EDITOR_ID = "org.jboss.tools.jst.jsp.jspeditor.JSPTextEditor"; // $NON-NLS-1$
	private final static String TEST_PROJECT_JAR_PATH = "/richFacesTest.jar"; // $NON-NLS-1$

	// check warning log
	private final static boolean checkWarning = false;
	private Throwable exception;

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
		
		ImportRichFacesComponents.importRichFacesPages(RichFacesTestPlugin
				.getPluginResourcePath() + TEST_PROJECT_JAR_PATH);
		
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
		while (Job.getJobManager().currentJob() != null)
			delay(5000);
	}

	public void testAllComponentsOnSinglePage() throws PartInitException, Throwable {
		performTestForRichFacesComponent("richFacesTest.xhtml"); // $NON-NLS-1$
	}
	
	public void testCalendar() throws PartInitException, Throwable {
		performTestForRichFacesComponent("calendar.xhtml"); // $NON-NLS-1$
	}
	
	public void testDataDefinitionList() throws PartInitException, Throwable {
		performTestForRichFacesComponent("dataDefinitionList.xhtml"); // $NON-NLS-1$
	}
	
	public void testDataFilterSlider() throws PartInitException, Throwable {
		performTestForRichFacesComponent("dataFilterSlider.xhtml"); // $NON-NLS-1$
	}
	
	public void testDataGrid() throws PartInitException, Throwable {
		performTestForRichFacesComponent("dataGrid.xhtml"); // $NON-NLS-1$
	}
	
	public void testDataList() throws PartInitException, Throwable {
		performTestForRichFacesComponent("dataList.xhtml"); // $NON-NLS-1$
	}
	
	public void testDataOrderedList() throws PartInitException, Throwable {
		performTestForRichFacesComponent("dataOrderedList.xhtml"); // $NON-NLS-1$
	}
	
	public void testDataScroller() throws PartInitException, Throwable {
		performTestForRichFacesComponent("dataScroller.xhtml"); // $NON-NLS-1$
	}
	
	public void testDataTable() throws PartInitException, Throwable {
		performTestForRichFacesComponent("dataTable.xhtml"); // $NON-NLS-1$
	}
	
	public void testDragAndDrop() throws PartInitException, Throwable {
		performTestForRichFacesComponent("dragAndDrop.xhtml"); // $NON-NLS-1$
	}
	
	public void testDropDawnMenu() throws PartInitException, Throwable {
		performTestForRichFacesComponent("dropDawnMenu.xhtml"); // $NON-NLS-1$
	}
	
	public void testEffect() throws PartInitException, Throwable {
		performTestForRichFacesComponent("effect.xhtml"); // $NON-NLS-1$
	}
	
	public void testGoogleMap() throws PartInitException, Throwable {
		performTestForRichFacesComponent("googleMap.xhtml"); // $NON-NLS-1$
	}
	
	public void testInputNumberSlider() throws PartInitException, Throwable {
		performTestForRichFacesComponent("inputNumberSlider.xhtml"); // $NON-NLS-1$
	}
	
	public void testInputNumberSpinner() throws PartInitException, Throwable {
		performTestForRichFacesComponent("inputNumberSpinner.xhtml"); // $NON-NLS-1$
	}
	
	public void testInsert() throws PartInitException, Throwable {
		performTestForRichFacesComponent("insert.xhtml"); // $NON-NLS-1$
	}
	
	public void testMessage() throws PartInitException, Throwable {
		performTestForRichFacesComponent("message.xhtml"); // $NON-NLS-1$
	}
	
	public void testMessages() throws PartInitException, Throwable {
		performTestForRichFacesComponent("messages.xhtml"); // $NON-NLS-1$
	}
	
	public void testModalPanel() throws PartInitException, Throwable {
		performTestForRichFacesComponent("modalPanel.xhtml"); // $NON-NLS-1$
	}
	
	public void testPaint2D() throws PartInitException, Throwable {
		performTestForRichFacesComponent("paint2D.xhtml"); // $NON-NLS-1$
	}
	
	public void testPanel() throws PartInitException, Throwable {
		performTestForRichFacesComponent("panel.xhtml"); // $NON-NLS-1$
	}
	
	public void testPanelBar() throws PartInitException, Throwable {
		performTestForRichFacesComponent("panelBar.xhtml"); // $NON-NLS-1$
	}
	
	public void testPanelMenu() throws PartInitException, Throwable {
		performTestForRichFacesComponent("panelMenu.xhtml"); // $NON-NLS-1$
	}
	
	public void testScrollableDataTable() throws PartInitException, Throwable {
		performTestForRichFacesComponent("scrollableDataTable.xhtml"); // $NON-NLS-1$
	}
	
	public void testSeparator() throws PartInitException, Throwable {
		performTestForRichFacesComponent("separator.xhtml"); // $NON-NLS-1$
	}
	
	public void testSimpleTogglePanel() throws PartInitException, Throwable {
		performTestForRichFacesComponent("simpleTogglePanel.xhtml"); // $NON-NLS-1$
	}
	
	public void testSpacer() throws PartInitException, Throwable {
		performTestForRichFacesComponent("spacer.xhtml"); // $NON-NLS-1$
	}
	
	public void testSuggestionBox() throws PartInitException, Throwable {
		performTestForRichFacesComponent("suggestionbox.xhtml"); // $NON-NLS-1$
	}
	
	public void testTabPanel() throws PartInitException, Throwable {
		performTestForRichFacesComponent("tabPanel.xhtml"); // $NON-NLS-1$
	}
	
	public void testTogglePanel() throws PartInitException, Throwable {
		performTestForRichFacesComponent("togglePanel.xhtml"); // $NON-NLS-1$
	}
	
	public void testToolBar() throws PartInitException, Throwable {
		performTestForRichFacesComponent("toolBar.xhtml"); // $NON-NLS-1$
	}
	
	public void testTree() throws PartInitException, Throwable {
		performTestForRichFacesComponent("tree.xhtml"); // $NON-NLS-1$
	}

	public void testVirtualEarth() throws PartInitException, Throwable {
		performTestForRichFacesComponent("virtualEarth.xhtml"); // $NON-NLS-1$
	}
	
	
	private void performTestForRichFacesComponent(String componentPage) throws PartInitException, Throwable {
		waitForJobs();

		exception = null;
		IPath componentPath = ImportRichFacesComponents.getComponentPath(componentPage);
		
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(componentPath);
		IEditorInput input = new FileEditorInput(file);
		
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, EDITOR_ID, true);

		waitForJobs();
		delay(3000);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllEditors(true);

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
