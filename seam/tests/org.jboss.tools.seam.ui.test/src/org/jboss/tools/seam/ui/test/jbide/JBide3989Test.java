/*************************************************************************************
 * Copyright (c) 2008-2009 JBoss by Red Hat and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     JBoss by Red Hat - Initial implementation.
 ************************************************************************************/
package org.jboss.tools.seam.ui.test.jbide;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.validation.internal.operations.OneValidatorOperation;
import org.eclipse.wst.validation.internal.operations.ValidatorManager;
import org.eclipse.wst.validation.internal.operations.ValidatorSubsetOperation;
import org.jboss.tools.common.test.util.TestProjectProvider;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.ui.marker.SeamRuntimeMarkerResolution;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ResourcesUtils;

/**
 * @author snjeza
 * 
 */
public class JBide3989Test extends TestCase {

	private IProject project;
	private static final String PROJECT_NAME = "TestSeamELContentAssist";
	private boolean makeCopy = true;

	public static Test suite() {
		return new TestSuite(JBide3989Test.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		TestProjectProvider provider = new TestProjectProvider(
				"org.jboss.tools.seam.ui.test", null, PROJECT_NAME, makeCopy);
		project = provider.getProject();
		project.refreshLocal(IResource.DEPTH_INFINITE, null);
		JobUtils.waitForIdle();
		ISeamProject seamProject = SeamCorePlugin.getSeamProject(project, true);
		seamProject.setRuntimeName("UNKNOWN");
		ValidatorManager.addProjectBuildValidationSupport(project);
		project.build(IncrementalProjectBuilder.CLEAN_BUILD,
				new NullProgressMonitor());
		// JBIDE-4832 - call SeamProjectPropertyValidator manually 
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			
			public void run(IProgressMonitor monitor) throws CoreException {
				String validatorId = "org.jboss.tools.seam.internal.core.validation.SeamProjectPropertyValidator";
				ValidatorSubsetOperation op = new OneValidatorOperation(project, validatorId, true, false);
				op.run(null);
			}
		};
		ResourcesPlugin.getWorkspace().run(runnable, null);
		JobUtils.waitForIdle();
	}

	@Override
	protected void tearDown() throws Exception {
		JobUtils.waitForIdle();
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		try {
			project.delete(true, true, null);
		} finally {
			ResourcesUtils.setBuildAutomatically(saveAutoBuild);
		}
	}

	public void testJBide3989() throws CoreException {
		IMarker[] markers = project.findMarkers(IMarker.PROBLEM, true,
				IResource.DEPTH_INFINITE);
		boolean found = false;
		for (int i = 0; i < markers.length; i++) {
			IMarker marker = markers[i];
			IMarkerResolution[] resolutions = IDE.getMarkerHelpRegistry()
					.getResolutions(marker);
			for (int j = 0; j < resolutions.length; j++) {
				IMarkerResolution resolution = resolutions[j];
				if (resolution instanceof SeamRuntimeMarkerResolution) {
					found = true;
					break;
				}
			}
			if (found) {
				break;
			}
		}
		assertTrue("The quickfix \"Set Seam Properties\" doesn't exist.", found);
	}

}
