/******************************************************************************* 
 * Copyright (c) 2010 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/

package org.jboss.tools.jsf.ui.test.validation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.preferences.JSFSeverityPreferences;
import org.jboss.tools.jst.web.kb.validation.IValidator;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.tests.AbstractResourceMarkerTest;

/**
 * @author Alexey Kazakov
 */
public class ELValidationTest extends AbstractResourceMarkerTest {

	private IProject project;

	@Override
	protected void setUp() throws Exception {
		JobUtils.waitForIdle();
		project = ResourcesPlugin.getWorkspace().getRoot().getProject("testJSFProject");
		assertTrue("Project testJSFProject is not accessible.", project.isAccessible());
	}

	/**
	 * See https://jira.jboss.org/browse/JBIDE-7067 
	 * @throws CoreException
	 */
	public void testELValidationEnablement() throws CoreException {
		JSFModelPlugin.getDefault().getPreferenceStore().setValue(JSFSeverityPreferences.ENABLE_BLOCK_PREFERENCE_NAME, false);
		project.build(IncrementalProjectBuilder.CLEAN_BUILD, new NullProgressMonitor());
		JobUtils.waitForIdle();
//		project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
//		JobUtils.waitForIdle();

		IFile file = project.getFile("WebContent/pages/el.jsp");

		int number = getMarkersNumberByGroupName(file, IValidator.MARKED_RESOURCE_MESSAGE_GROUP);
		JSFModelPlugin.getDefault().getPreferenceStore().setValue(JSFSeverityPreferences.ENABLE_BLOCK_PREFERENCE_NAME, true);
		assertEquals("Problem marker was found.", 0, number);

		project.build(IncrementalProjectBuilder.CLEAN_BUILD, new NullProgressMonitor());
		JobUtils.waitForIdle();
//		project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
//		JobUtils.waitForIdle();

		number = getMarkersNumberByGroupName(file, IValidator.MARKED_RESOURCE_MESSAGE_GROUP);
		assertEquals("Problem marker was not found.", 1, number);
	}
}