/*************************************************************************************
 * Copyright (c) 2008-2011 Red Hat, Inc. and others.
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
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.validation.internal.operations.ValidatorManager;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.ui.marker.AddNewSeamRuntimeMarkerResolution;
import org.jboss.tools.seam.ui.marker.SeamRuntimeMarkerResolution;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ResourcesUtils;
import org.jboss.tools.test.util.TestProjectProvider;

/**
 * @author snjeza
 * 
 */
public class JBide3989Test extends TestCase {

	private IProject project;
	private static final String PROJECT_NAME = "SeamConfigValidatorsTest";
	private boolean makeCopy = true;

	public static Test suite() {
		return new TestSuite(JBide3989Test.class);
	}

	@Override
	protected void setUp() throws Exception {
		TestProjectProvider provider = new TestProjectProvider(
				"org.jboss.tools.seam.ui.test", null, PROJECT_NAME, makeCopy);
		project = provider.getProject();
//		ISeamProject seamProject = SeamCorePlugin.getSeamProject(project, true);
//		seamProject.setRuntimeName("UNKNOWN");
		ValidatorManager.addProjectBuildValidationSupport(project);
		// JBIDE-4832 - call SeamProjectPropertyValidator manually 
		project.build(IncrementalProjectBuilder.FULL_BUILD,
				new NullProgressMonitor());
		
		JobUtils.waitForIdle();
	}

	@Override
	protected void tearDown() {
		try {
			project.delete(false, true, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public void testJBide3989() throws CoreException {
		IMarker[] markers = project.findMarkers(IMarker.PROBLEM, true,
				IResource.DEPTH_ZERO);
		boolean found1 = false;
		boolean found2 = false;
		for (int i = 0; i < markers.length; i++) {
			IMarker marker = markers[i];
			IMarkerResolution[] resolutions = IDE.getMarkerHelpRegistry()
					.getResolutions(marker);
			for (int j = 0; j < resolutions.length; j++) {
				IMarkerResolution resolution = resolutions[j];
				if (resolution instanceof SeamRuntimeMarkerResolution) {
					found1 = true;
				} else if (resolution instanceof AddNewSeamRuntimeMarkerResolution) {
					found2 = true;
				}
			}
			if (found1 && found2) {
				break;
			}
		}
		assertTrue("The quickfix \"Set Seam Properties\" doesn't exist.", found1);
		assertTrue("The quickfix \"Add New Seam Runtime\" doesn't exist.", found2);
	}

}
