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
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.project.IModelNature;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.jsf.vpe.jsf.test.JsfAllTests;
import org.jboss.tools.jsf.vpe.jsf.test.JsfTestPlugin;
import org.jboss.tools.jst.jsp.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.jst.web.project.WebProject;
import org.jboss.tools.jst.web.tld.TaglibMapping;
import org.jboss.tools.vpe.editor.VpeController;
import org.jboss.tools.vpe.ui.test.ProjectsLoader;
import org.jboss.tools.vpe.ui.test.TestUtil;
import org.jboss.tools.vpe.ui.test.VpeTest;

/**
 * @author mareshkau
 *
 */
public class JBIDE4534Test extends VpeTest{

	private VpeController vpeController;
	private Job nonUIJob;
	private IProject project;
	/* (non-Javadoc)
	 * @see org.jboss.tools.vpe.ui.test.VpeTest#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setVpeController(null);
		this.project = ProjectsLoader.getInstance()
				.getProject(JsfAllTests.IMPORT_JBIDE3247_PROJECT_NAME);
		this.nonUIJob = new Job("Revalidate Taglibs Test Job name"){ //$NON-NLS-1$
			@SuppressWarnings("synthetic-access")
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				XModel xm = null;
				IModelNature mn = EclipseResourceUtil.getModelNature(JBIDE4534Test.this.project);
				if (mn != null) {
					xm = mn.getModel();
				}
				TaglibMapping taglibMapping = WebProject.getInstance(xm).getTaglibMapping();
				final long timeOfEndExecution = System.currentTimeMillis()+TestUtil.MAX_IDLE;
					synchronized (taglibMapping) {
						while(getVpeController()==null) {
								if(timeOfEndExecution<System.currentTimeMillis()) {
									return new Status(IStatus.ERROR, JsfTestPlugin.PLUGIN_ID, "Visual page editor hasn't been initialized in time, possibly it's sleeped");//$NON-NLS-1$
								}
								if(monitor.isCanceled()) {
									return new Status(IStatus.CANCEL, JsfTestPlugin.PLUGIN_ID, "Job Execution has been canceled");//$NON-NLS-1$
								}
								try {
									Thread.sleep(5);
								} catch (InterruptedException e) {
									fail(e.getStackTrace()+""); //$NON-NLS-1$
								}
						}
					}
				return Status.OK_STATUS;
			}};
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.vpe.ui.test.VpeTest#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		this.nonUIJob.cancel();
		setVpeController(null);
		super.tearDown();
	}

	public JBIDE4534Test(String name) {
		super(name);
	}

	public void testJBIDE4534Test() throws Throwable {
		
			this.nonUIJob.schedule();
			while(this.nonUIJob.getState()!=Job.RUNNING) {
				TestUtil.delay(4);
			}
			IFile file = (IFile) TestUtil.getComponentPath("index.xhtml", //$NON-NLS-1$
					JsfAllTests.IMPORT_JBIDE3247_PROJECT_NAME);

			IEditorInput input = new FileEditorInput(file);
			// open and get editor
			final JSPMultiPageEditor part  = openEditor(input);
			setVpeController(TestUtil.getVpeController(part));
			TestUtil.waitForIdle();
			IStatus result = this.nonUIJob.getResult();
			if(result==null 
					||result.matches(IStatus.ERROR)
					||result.matches(IStatus.CANCEL)){
				fail("Test failed becouse "+result); //$NON-NLS-1$
			}
	}

	/**
	 * @return the vpeController
	 */
	private VpeController getVpeController() {
		return this.vpeController;
	}

	/**
	 * @param vpeControllerParam the vpeController to set
	 */
	private void setVpeController(VpeController vpeControllerParam) {
		this.vpeController = vpeControllerParam;
	}
}
