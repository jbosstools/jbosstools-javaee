/*******************************************************************************
  * Copyright (c) 2010 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.jsf.verification.test;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.jboss.tools.common.model.markers.ResourceMarkers;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ResourcesUtils;
import org.jboss.tools.test.util.TestProjectProvider;
import org.jboss.tools.tests.AbstractResourceMarkerTest;

import junit.framework.TestCase;

public class WebVerificationTest extends TestCase {
	public static final String TEST_PROJECT_NAME = "Test";
	static String MARKER_TYPE = ResourceMarkers.JST_WEB_PROBLEM;

	public static final String TEST_PROJECT_PATH = "/projects/" + TEST_PROJECT_NAME;

	TestProjectProvider prjProvider = null;
	IProject project = null;

	protected void setUp() throws Exception {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		project = (IProject)ResourcesPlugin.getWorkspace().getRoot().findMember(TEST_PROJECT_NAME);
		if(project==null) {
			prjProvider = new TestProjectProvider("org.jboss.tools.jsf.verification.test", TEST_PROJECT_PATH, TEST_PROJECT_NAME, true);
			project = prjProvider.getProject();
		}
		
		project.build(IncrementalProjectBuilder.FULL_BUILD, null);
		JobUtils.waitForIdle();
		ResourcesUtils.setBuildAutomatically(saveAutoBuild);
	}

	public void testWebVerification() throws CoreException {
		IFile web = (IFile)project.getFile(new Path("WebContent/WEB-INF/web.xml"));
		
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(web, MARKER_TYPE, "error: Attribute location references to /error that does not exist in web content", 40);
		AbstractResourceMarkerTest.assertMarkerIsCreated(web, MARKER_TYPE, "error: Attribute location references to /error2 that does not exist in web content", 44);

		AbstractResourceMarkerTest.assertMarkerIsNotCreated(web, MARKER_TYPE, "error: Attribute location references to .* that does not exist in web content", 48);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(web, MARKER_TYPE, "error: Attribute location references to .* that does not exist in web content", 52);
		AbstractResourceMarkerTest.assertMarkerIsCreated(web, MARKER_TYPE, "error: Attribute location references to pages/400.* that does not exist in web content", 56);

		IMarker[] markers = web.findMarkers(null, true, IResource.DEPTH_INFINITE);
		for (IMarker marker: markers) {
			int line = marker.getAttribute(IMarker.LINE_NUMBER, -1);
			int start = marker.getAttribute(IMarker.CHAR_START, -1);
			int end = marker.getAttribute(IMarker.CHAR_END, -1);

			System.out.println(marker.getType() + " " + line + " " + start + " " + end);
			System.out.println(marker.getAttribute(IMarker.MESSAGE));
		}
		System.out.println(markers.length);
	}

	@Override
	protected void tearDown() throws Exception {
		if(prjProvider!=null) {
			prjProvider.dispose();
		}
	}

}
