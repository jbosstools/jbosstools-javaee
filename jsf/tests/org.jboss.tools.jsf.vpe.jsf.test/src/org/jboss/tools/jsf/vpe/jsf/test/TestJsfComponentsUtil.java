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
package org.jboss.tools.jsf.vpe.jsf.test;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.wizards.datatransfer.FileSystemStructureProvider;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;

/**
 * Class for importing project from jar file
 * 
 * @author sdzmitrovich
 * 
 */
public class TestJsfComponentsUtil {
	private static final String PROJECT_NAME = "JsfTest"; // $NON-NLS-1$
	private static final String COMPONENTS_PATH = "WebContent/pages"; // $NON-NLS-1$

	@SuppressWarnings("restriction")
	static void importJsfPages(String path) {

		if (ResourcesPlugin.getWorkspace().getRoot().findMember(
				TestJsfComponentsUtil.PROJECT_NAME) != null) {
			waitForJobs();

			try {
				removeProject();
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}

		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(
				PROJECT_NAME);

		try {

			IOverwriteQuery overwrite = new IOverwriteQuery() {
				public String queryOverwrite(String pathString) {
					return ALL;
				}
			};

			// source file
			File source = new File(path);

			// create import operation
			ImportOperation importOp = new ImportOperation(project
					.getFullPath(), null, FileSystemStructureProvider.INSTANCE,
					overwrite, Arrays.asList(source.listFiles()));

			// import files just to project folder ( without old structure )
			importOp.setCreateContainerStructure(false);

			importOp.setContext(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell());

			// run import
			importOp.run(new NullProgressMonitor());

		} catch (InvocationTargetException ite) {
			JsfTestPlugin.getPluginLog().logError(ite.getCause());
		} catch (InterruptedException ie) {
			JsfTestPlugin.getPluginLog().logError(ie);
		}
	}

	/**
	 * 
	 * @return
	 * @throws CoreException
	 */
	static IPath getComponentPath(String componentPage) throws CoreException {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(
				PROJECT_NAME);
		if (project != null) {
			IResource resource = project.getFolder(COMPONENTS_PATH).findMember(
					componentPage);
			if (resource != null) {
				return resource.getFullPath();
			}

		}

		return null;
	}

	/**
	 * 
	 * @throws CoreException
	 */
	static void removeProject() throws CoreException {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(
				PROJECT_NAME);
		if (project != null) {
			project.delete(IResource.ALWAYS_DELETE_PROJECT_CONTENT,
					new NullProgressMonitor());
		}
	}

	/**
	 * Process UI input but do not return for the specified time interval.
	 * 
	 * @param waitTimeMillis
	 *            the number of milliseconds
	 */
	public static void delay(long waitTimeMillis) {
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
	public static void waitForJobs() {
		while (Job.getJobManager().currentJob() != null)
			delay(5000);
	}

}
