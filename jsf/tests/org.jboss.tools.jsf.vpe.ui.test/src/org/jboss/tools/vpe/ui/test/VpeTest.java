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
package org.jboss.tools.vpe.ui.test;

import junit.framework.TestCase;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.common.model.util.ClassLoaderUtil;
import org.jboss.tools.jst.firstrun.JBossASAdapterInitializer;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.vpe.editor.VpeController;
import org.jboss.tools.vpe.editor.VpeEditorPart;
import org.jboss.tools.vpe.xulrunner.editor.XulRunnerEditor;
import org.mozilla.interfaces.nsIDOMDocument;

/**
 * @author Max Areshkau
 * 
 * Base Class for VPE tests
 */
public class VpeTest extends TestCase implements ILogListener {

	/**
	 * Editor in which we open visual page
	 */
	private final static String EDITOR_ID = "org.jboss.tools.jst.jsp.jspeditor.JSPTextEditor";

	/**
	 * Collects exceptions
	 */
	private Throwable exception;

	// check warning log
	private Boolean checkWarning;

	//FIX for JBIDE-1628
	static {
		ClassLoaderUtil.init();
		//wait for initialization
		TestUtil.delay(3000);
		} 
	/**
	 * 
	 * @param name
	 * @param importProjectName
	 */

	public VpeTest(String name) {
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

		Platform.addLogListener(this);
		String jbossPath = System.getProperty("jbosstools.test.jboss.home.4.2", "C:\\java\\jboss-4.2.2.GA");
//		JBossASAdapterInitializer.initJBossAS(jbossPath, new NullProgressMonitor());
		closeEditors();
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

		closeEditors();

		Platform.removeLogListener(this);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.ILogListener#logging(org.eclipse.core.runtime.IStatus,
	 *      java.lang.String)
	 */
	public void logging(IStatus status, String plugin) {
		switch (status.getSeverity()) {
		case IStatus.ERROR:
			setException(status.getException());
			break;
		case IStatus.WARNING:
			if (getCheckWarning())
				setException(status.getException());
			break;
		default:
			break;
		}

	}

	/**
	 * close all opened editors
	 */
	protected void closeEditors() {

		// wait
		TestUtil.waitForJobs();

		// close
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.closeAllEditors(false);

	}

	/**
	 * get xulrunner source page
	 * 
	 * @param part -
	 *            JSPMultiPageEditor
	 * @return nsIDOMDocument
	 */
	protected nsIDOMDocument getVpeVisualDocument(JSPMultiPageEditor part) {

		VpeEditorPart visualEditor = (VpeEditorPart) part.getVisualEditor();
		
		VpeController vpeController = visualEditor.getController();

		// get xulRunner editor
		XulRunnerEditor xulRunnerEditor = vpeController.getXulRunnerEditor();

		// get dom document
		nsIDOMDocument document = xulRunnerEditor.getDOMDocument();

		return document;
	}

	/**
	 * Perfoms test for some page
	 * 
	 * @param componentPage
	 * @throws PartInitException
	 * @throws Throwable
	 */
	protected void performTestForVpeComponent(IFile componentPage)
			throws PartInitException, Throwable {
		TestUtil.waitForJobs();

		setException(null);

		// IFile file = (IFile)
		// TestUtil.getComponentPath(componentPage,getImportProjectName());
		IEditorInput input = new FileEditorInput(componentPage);

		TestUtil.waitForJobs();

		IEditorPart editor = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().openEditor(input,
						EDITOR_ID, true);

		assertNotNull(editor);

		TestUtil.waitForJobs();
		TestUtil.delay(1000);

		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.closeAllEditors(true);

		if (getException() != null) {
			throw getException();
		}
	}

	/**
	 * Open JSPMultiPageEditor editor
	 * 
	 * @param input
	 * @return
	 * @throws PartInitException
	 */
	protected JSPMultiPageEditor openEditor(IEditorInput input)
			throws PartInitException {

		// get editor
		JSPMultiPageEditor part = (JSPMultiPageEditor) PlatformUI
				.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.openEditor(input, EDITOR_ID, true);

		assertNotNull(part);
		// wait for jobs
		//TestUtil.waitForJobs();
		// wait full initialization of vpe
		// commented by dgolovin to get rid of jvm error [libexpat.so.0+0xeff4]
		//TestUtil.delay(1000);

		return part;

	}

	/**
	 * @return the exception
	 */
	protected Throwable getException() {
		return exception;
	}

	/**
	 * @param exception
	 *            the exception to set
	 */
	protected void setException(Throwable exception) {
		this.exception = exception;
	}

	/**
	 * @return the checkWarning
	 */
	protected Boolean getCheckWarning() {
		return checkWarning;
	}

	/**
	 * @param checkWarning
	 *            the checkWarning to set
	 */
	protected void setCheckWarning(Boolean checkWarning) {
		this.checkWarning = checkWarning;
	}

}
