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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.test.validation.IValidatorSupport;
import org.jboss.tools.seam.core.test.validation.SeamProjectPropertyValidatorWrapper;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.seam.internal.core.validation.SeamValidationMessages;
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
			ejbProject.build(IncrementalProjectBuilder.FULL_BUILD, null);
			warProject.build(IncrementalProjectBuilder.FULL_BUILD, null);
			testProject.build(IncrementalProjectBuilder.FULL_BUILD, null);
			JobUtils.waitForIdle(2000);
		}
	}
	
	protected void assertMarkerIsCreated(IProject project, String template, Object[] parameters) throws CoreException, ValidationException {
		IValidatorSupport validator = new SeamProjectPropertyValidatorWrapper(project);
		validator.validate();
		assertTrue("Error marker not found", validator.isMessageCreated(template, parameters));
	}


	public void testProjectNameValidation() throws CoreException, ValidationException {
		assertMarkerIsCreated(ejbProject, SeamValidationMessages.INVALID_PARENT_PROJECT, new Object[]{"invalidParentProjectName","RefactoringTestProject-ejb"});
		//assertMarkerIsCreated(testProject, SeamValidationMessages.INVALID_TEST_PROJECT, new Object[]{"invalidTestProjectName", "RefactoringTestProject-test"});
		assertMarkerIsCreated(warProject, SeamValidationMessages.INVALID_EJB_PROJECT, new Object[]{"invalidEjbProjectName", "RefactoringTestProject-war"});
		assertMarkerIsCreated(warProject, SeamValidationMessages.INVALID_TEST_PROJECT, new Object[]{"invalidTestProjectName", "RefactoringTestProject-war"});
	}

	public void testFolderNameValidation() throws CoreException, ValidationException {
		assertMarkerIsCreated(warProject, SeamValidationMessages.INVALID_WEBFOLDER, new Object[]{"invalidWebFolderPath", "RefactoringTestProject-war"});
		assertMarkerIsCreated(warProject, SeamValidationMessages.INVALID_MODEL_SRC, new Object[]{"invalidModelSrcFolderPath", "RefactoringTestProject-war"});
		assertMarkerIsCreated(warProject, SeamValidationMessages.INVALID_ACTION_SRC, new Object[]{"invalidSessionBeanSrcFolderPath", "RefactoringTestProject-war"});
		assertMarkerIsCreated(warProject, SeamValidationMessages.INVALID_TEST_SRC, new Object[]{"invalidTestSrcFolderPath", "RefactoringTestProject-war"});
	}

	public void testPackageNameValidation() throws CoreException, ValidationException {
		assertMarkerIsCreated(warProject, SeamValidationMessages.INVALID_MODEL_PACKAGE_NAME, new Object[]{"invalid model package name", "RefactoringTestProject-war"});
		assertMarkerIsCreated(warProject, SeamValidationMessages.INVALID_ACTION_PACKAGE_NAME, new Object[]{"invalid session bean package name", "RefactoringTestProject-war"});
		assertMarkerIsCreated(warProject, SeamValidationMessages.INVALID_TEST_PACKAGE_NAME, new Object[]{"invalid test package name", "RefactoringTestProject-war"});
	}

	public void testRuntimeNameValidation() throws CoreException, ValidationException {
		assertMarkerIsCreated(warProject, SeamValidationMessages.INVALID_SEAM_RUNTIME, new Object[]{"invalidRuntimeName", "RefactoringTestProject-war"});
	}

}