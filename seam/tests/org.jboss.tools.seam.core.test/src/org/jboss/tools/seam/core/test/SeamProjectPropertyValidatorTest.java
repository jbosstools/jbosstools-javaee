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
package org.jboss.tools.seam.core.test;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.test.util.ProjectImportTestSetup;
import org.jboss.tools.test.util.xpl.EditorTestHelper;
import org.jboss.tools.tests.AbstractResourceMarkerTest;

/**
 * @author Alexey Kazakov
 */
public class SeamProjectPropertyValidatorTest extends AbstractResourceMarkerTest {

	IProject warProject;
	IProject ejbProject;
	IProject testProject;

	public SeamProjectPropertyValidatorTest() {
		super("Seam Project Property Validator Tests");
	}

	protected void setUp() throws Exception {
		if(warProject==null) {
			warProject = ProjectImportTestSetup.loadProject("RefactoringTestProject-war");
			ejbProject = ProjectImportTestSetup.loadProject("RefactoringTestProject-ejb");
			testProject = ProjectImportTestSetup.loadProject("RefactoringTestProject-test");

			// Change properties of the seam project
			IEclipsePreferences pref = SeamCorePlugin.getSeamPreferences(ejbProject);
			pref.put(ISeamFacetDataModelProperties.SEAM_PARENT_PROJECT, "invalidParentProjectName");
			pref.flush();
			pref = SeamCorePlugin.getSeamPreferences(testProject);
			pref.put(ISeamFacetDataModelProperties.SEAM_PARENT_PROJECT, "invalidParentProjectName");
			pref.flush();
			pref = SeamCorePlugin.getSeamPreferences(warProject);
			pref.put(ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME, "invalidRuntimeName");
			pref.put(ISeamFacetDataModelProperties.SEAM_EJB_PROJECT, "invalidEjbProjectName");
			pref.put(ISeamFacetDataModelProperties.WEB_CONTENTS_FOLDER, "invalidWebFolderPath");
			pref.put(ISeamFacetDataModelProperties.ENTITY_BEAN_SOURCE_FOLDER, "invalidModelSrcFolderPath");
			pref.put(ISeamFacetDataModelProperties.ENTITY_BEAN_PACKAGE_NAME, "invalid model package name");
			pref.put(ISeamFacetDataModelProperties.SESSION_BEAN_SOURCE_FOLDER, "invalidSessionBeanSrcFolderPath");
			pref.put(ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_NAME, "invalid session bean package name");
			pref.put(ISeamFacetDataModelProperties.SEAM_TEST_PROJECT, "invalidTestProjectName");
			pref.put(ISeamFacetDataModelProperties.TEST_SOURCE_FOLDER, "invalidTestSrcFolderPath");
			pref.put(ISeamFacetDataModelProperties.TEST_CASES_PACKAGE_NAME, "invalid test package name");
			pref.put(ISeamFacetDataModelProperties.SEAM_CONNECTION_PROFILE, "invalidConnectionName");
			pref.flush();

			// Force validation.
			warProject.getFolder("newFolder").create(true, true, null);
			ejbProject.getFolder("newFolder").create(true, true, null);
			testProject.getFolder("newFolder").create(true, true, null);

			EditorTestHelper.joinBackgroundActivities();
		}
	}

	public void testProjectNameValidation() throws CoreException {
		IEclipsePreferences pref = SeamCorePlugin.getSeamPreferences(ejbProject);
		System.out.println(pref.get(ISeamFacetDataModelProperties.SEAM_PARENT_PROJECT, "---"));
		assertMarkerIsCreated(ejbProject, null, "invalidParentProjectName", -1);
		assertMarkerIsCreated(testProject, null, "invalidParentProjectName", -1);
		assertMarkerIsCreated(warProject, null, "invalidEjbProjectName", -1);
		assertMarkerIsCreated(warProject, null, "invalidTestProjectName", -1);
	}
}