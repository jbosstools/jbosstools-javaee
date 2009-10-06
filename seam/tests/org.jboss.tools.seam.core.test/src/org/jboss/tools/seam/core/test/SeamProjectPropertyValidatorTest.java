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
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ProjectImportTestSetup;
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
//			pref.put(ISeamFacetDataModelProperties.SEAM_CONNECTION_PROFILE, "invalidConnectionName");
			pref.flush();

			JobUtils.waitForIdle();
			JobUtils.delay(2000);
		}
	}

	public void testProjectNameValidation() throws CoreException {
		assertMarkerIsCreated(ejbProject, null, ".*invalidParentProjectName.*");
		assertMarkerIsCreated(testProject, null, ".*invalidParentProjectName.*");
		assertMarkerIsCreated(warProject, null, ".*invalidEjbProjectName.*");
		assertMarkerIsCreated(warProject, null, ".*invalidTestProjectName.*");
	}

	public void testFolderNameValidation() throws CoreException {
		assertMarkerIsCreated(warProject, null, ".*invalidWebFolderPath.*");
		assertMarkerIsCreated(warProject, null, ".*invalidModelSrcFolderPath.*");
		assertMarkerIsCreated(warProject, null, ".*invalidSessionBeanSrcFolderPath.*");
		assertMarkerIsCreated(warProject, null, ".*invalidTestSrcFolderPath.*");
	}

	public void testPackageNameValidation() throws CoreException {
		assertMarkerIsCreated(warProject, null, ".*invalid model package name.*");
		assertMarkerIsCreated(warProject, null, ".*invalid session bean package name.*");
		assertMarkerIsCreated(warProject, null, ".*invalid test package name.*");
	}

	public void testRuntimeNameValidation() throws CoreException {
		assertMarkerIsCreated(warProject, null, ".*invalidRuntimeName.*");
	}

	/*
	public void testConnectionProfileNameValidation() throws CoreException {
		assertMarkerIsCreated(warProject, null, ".*invalidConnectionName.*");
	}
	*/

	public static void main(String[] args) {
		String errorMarker = "Model package name \"invalid model package name\" specified for Seam project \"RefactoringTestProject-war\" is not valid. Please correct this property in \"Seam settings\" page (Project->Properties->Seam Settings).";
//		String errorMarker = "Main Seam project \"invalidParentProjectName  ";
		System.out.println(errorMarker.matches(".*invalid model package name.*"));
	}
}