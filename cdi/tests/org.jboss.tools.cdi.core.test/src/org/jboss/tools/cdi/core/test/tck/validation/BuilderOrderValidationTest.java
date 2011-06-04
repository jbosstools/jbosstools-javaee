/*************************************************************************************
 * Copyright (c) 2011 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     JBoss by Red Hat - Initial implementation.
 ************************************************************************************/
package org.jboss.tools.cdi.core.test.tck.validation;

import junit.framework.TestCase;

import org.eclipse.core.internal.preferences.EclipsePreferences;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.ide.IDE;
import org.jboss.tools.cdi.core.preferences.CDIPreferences;
import org.jboss.tools.common.preferences.SeverityPreferences;
import org.jboss.tools.jst.jsp.test.TestUtil;
import org.jboss.tools.jst.web.kb.internal.validation.ValidatorManager;
import org.jboss.tools.jst.web.kb.preferences.ELSeverityPreferences;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ResourcesUtils;

public class BuilderOrderValidationTest extends TestCase {

	IProject project = null;
	boolean saveAutoBuild;
	
	public void setUp() throws Exception {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject("CDITestBrokenBuilderOrder");
		assertNotNull("Can't load CDITestBrokenBuilderOrder", project); //$NON-NLS-1$
		saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		rebuild();
	}

	public void tearDown() throws CoreException {
		ResourcesUtils.setBuildAutomatically(saveAutoBuild);
	}

	private void checkResolution(IProject project, String markerType, String resolutionClassName) throws CoreException {
		try{
			IMarker[] markers = getBuilderOrderMarkers();
			assertTrue(markers.length > 0);
			for (int i = 0; i < markers.length; i++) {
				IMarker marker = markers[i];
				IMarkerResolution[] resolutions = IDE.getMarkerHelpRegistry().getResolutions(marker);
				for (int j = 0; j < resolutions.length; j++) {
					IMarkerResolution resolution = resolutions[j];
					if (resolution.getClass().getName().equals(resolutionClassName)) {
						ValidatorManager.setStatus(ValidatorManager.RUNNING);
						resolution.run(marker);
						JobUtils.waitForIdle();
						TestUtil.waitForValidation(project);
						IMarker[] newMarkers = project.findMarkers(markerType, true, IResource.DEPTH_INFINITE);
						assertTrue("Marker resolution did not decrease number of problems. was: "+markers.length+" now: "+newMarkers.length, newMarkers.length < markers.length);
						return;
					}
					fail("Marker resolution: "+resolutionClassName+" not found");
				}
			}
		} finally {
			JobUtils.waitForIdle();
//			TestUtil.waitForValidation(project);
		}
	}

	private IMarker[] getBuilderOrderMarkers() throws CoreException {
		return project.findMarkers(ValidatorManager.ORDER_PROBLEM_MARKER_TYPE, true, IResource.DEPTH_ZERO);
	}

	public void testWrongBuildOrderPreference() throws CoreException {
		IMarker[] markers = getBuilderOrderMarkers();
		assertEquals(2, markers.length);
		assertEquals(IMarker.SEVERITY_ERROR, markers[0].getAttribute(IMarker.SEVERITY, -1));

		modifyPreference(SeverityPreferences.IGNORE);
		markers = getBuilderOrderMarkers();
		assertEquals(0, markers.length);

		modifyPreference(SeverityPreferences.WARNING);
		markers = getBuilderOrderMarkers();
		assertEquals(2, markers.length);
		assertEquals(IMarker.SEVERITY_WARNING, markers[0].getAttribute(IMarker.SEVERITY, -1));

		modifyPreference(SeverityPreferences.ERROR);
		markers = getBuilderOrderMarkers();
		assertEquals(2, markers.length);
		assertEquals(IMarker.SEVERITY_ERROR, markers[0].getAttribute(IMarker.SEVERITY, -1));
	}

	void modifyPreference(String value) throws CoreException {
		EclipsePreferences ps = (EclipsePreferences)CDIPreferences.getInstance().getProjectPreferences(project);
		ps.put(ELSeverityPreferences.WRONG_BUILDER_ORDER_PREFERENCE_NAME, value);
		ps = (EclipsePreferences)ELSeverityPreferences.getInstance().getProjectPreferences(project);
		ps.put(ELSeverityPreferences.WRONG_BUILDER_ORDER_PREFERENCE_NAME, value);
		rebuild();
	}

	void rebuild() throws CoreException {
		ValidatorManager.setStatus(ValidatorManager.RUNNING);
		JobUtils.waitForIdle();
		project.build(IncrementalProjectBuilder.CLEAN_BUILD, new NullProgressMonitor());
		JobUtils.waitForIdle();
		project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
		JobUtils.waitForIdle();
		TestUtil.waitForValidation(project);
	}

	public void testBuilderOrderResolution() throws CoreException {
		checkResolution(project,
				ValidatorManager.ORDER_PROBLEM_MARKER_TYPE,
				"org.jboss.tools.jst.web.kb.internal.validation.BuilderOrderResolution");
	}

}
