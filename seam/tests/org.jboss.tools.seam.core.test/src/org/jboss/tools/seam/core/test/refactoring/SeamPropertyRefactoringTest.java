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
package org.jboss.tools.seam.core.test.refactoring;

import java.lang.reflect.InvocationTargetException;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.refactoring.RenameSupport;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.test.util.JUnitUtils;
import org.jboss.tools.test.util.WorkbenchUtils;
import org.jboss.tools.test.util.xpl.EditorTestHelper;

/**
 * @author Alexey Kazakov
 */
public class SeamPropertyRefactoringTest extends TestCase {
	static String warProjectName = "RefactoringTestProject-war";
	static String ejbProjectName = "RefactoringTestProject-ejb";
	static String testProjectName = "RefactoringTestProject-test";
	static String actionSourceFolderName = "/" + ejbProjectName + "/src";
	static String modelSourceFolderName = "/" + warProjectName + "/src";
	static String testSourceFolderName = "/" + testProjectName + "/src";
	static String viewFolderName = "/" + warProjectName + "/WebContent";
	static IProject warProject;
	static IProject ejbProject;
	static IProject testProject;
	static ISeamProject seamWarProject;
	static ISeamProject seamEjbProject;
	static ISeamProject seamTestProject;

	public SeamPropertyRefactoringTest() {
		super("Seam Property Refactoring Tests");
	}

	protected void setUp() throws Exception {
		if(warProject==null) {
			warProject = loadProject(warProjectName);
		}
		if(ejbProject==null) {
			ejbProject = loadProject(ejbProjectName);;
		}
		if(testProject==null) {
			testProject = loadProject(testProjectName);
		}
		if(seamWarProject==null) {
			seamWarProject = loadSeamProject(warProject);
		}
		if(seamEjbProject==null) {
			seamEjbProject = loadSeamProject(ejbProject);
		}
		if(seamTestProject==null) {
			seamTestProject = loadSeamProject(testProject);
		}
	}

	private IProject loadProject(String projectName) throws CoreException {
		IResource project = ResourcesPlugin.getWorkspace().getRoot().findMember(projectName);
		assertNotNull("Can't load " + projectName, project);
		IProject result = project.getProject();
		result.build(IncrementalProjectBuilder.FULL_BUILD, null);
		EditorTestHelper.joinBackgroundActivities();
		return result;
	}

	private ISeamProject loadSeamProject(IProject project) throws CoreException {
		ISeamProject seamProject = SeamCorePlugin.getSeamProject(project, true);
		assertNotNull("Seam project for " + project.getName() + " is null", seamProject);
		EditorTestHelper.joinBackgroundActivities();
		return seamProject;
	}

	public void testWarProjectRename() throws CoreException {
		warProjectName = "NewWarProjectName";
		modelSourceFolderName = "/" + warProjectName + "/src";
		viewFolderName = "/" + warProjectName + "/WebContent"; 
		warProject = renameProject(warProject, warProjectName);
		seamWarProject = SeamCorePlugin.getSeamProject(warProject, true);

		String newParentName = seamEjbProject.getParentProjectName();
		assertEquals(warProjectName, newParentName);
		newParentName = seamTestProject.getParentProjectName();
		assertEquals(warProjectName, newParentName);

		IEclipsePreferences pref = SeamCorePlugin.getSeamPreferences(warProject);
		String modelSources = pref.get(ISeamFacetDataModelProperties.ENTITY_BEAN_SOURCE_FOLDER, "");
		assertEquals(modelSourceFolderName, modelSources);

		String viewFolder = pref.get(ISeamFacetDataModelProperties.WEB_CONTENTS_FOLDER, "");
		assertEquals(viewFolderName, viewFolder);
	}

	public void testEjbProjectRename() throws CoreException {
		ejbProjectName = "NewEjbProjectName";
		actionSourceFolderName = "/" + ejbProjectName + "/src";
		ejbProject = renameProject(ejbProject, ejbProjectName);
		seamEjbProject = SeamCorePlugin.getSeamProject(ejbProject, true);

		IEclipsePreferences pref = SeamCorePlugin.getSeamPreferences(warProject);
		String newEjbName = pref.get(ISeamFacetDataModelProperties.SEAM_EJB_PROJECT, "");
		assertEquals(ejbProjectName, newEjbName);

		String actionSources = pref.get(ISeamFacetDataModelProperties.SESSION_BEAN_SOURCE_FOLDER, "");
		assertEquals(actionSourceFolderName, actionSources);
	}

	public void testTestProjectRename() throws CoreException {
		testProjectName = "NewTestProjectName";
		testSourceFolderName = "/" + testProjectName + "/src";
		testProject = renameProject(testProject, testProjectName);
		seamTestProject = SeamCorePlugin.getSeamProject(testProject, true);

		String newTestName = SeamCorePlugin.getSeamPreferences(warProject).get(ISeamFacetDataModelProperties.SEAM_TEST_PROJECT, "");
		assertEquals(testProjectName, newTestName);

		IEclipsePreferences pref = SeamCorePlugin.getSeamPreferences(warProject);
		String testSources = pref.get(ISeamFacetDataModelProperties.TEST_SOURCE_FOLDER, "");
		assertEquals(testSourceFolderName, testSources);
	}

	private IProject renameProject(IProject project, String newProjectName) throws CoreException {
		RenameSupport support = RenameSupport.create(JavaCore.create(project), newProjectName, RenameSupport.UPDATE_REFERENCES);

		Shell parent = WorkbenchUtils.getActiveShell();
		IWorkbenchWindow context = WorkbenchUtils.getWorkbench().getActiveWorkbenchWindow();
		try {
			support.perform(parent, context);
		} catch (InterruptedException e) {
			JUnitUtils.fail("Rename failed", e);
		} catch (InvocationTargetException e) {
			JUnitUtils.fail("Rename failed", e);
		}

		EditorTestHelper.joinBackgroundActivities();

		IProject renamedProject = (IProject)ResourcesPlugin.getWorkspace().getRoot().findMember(newProjectName);
		assertNotNull("Can't load renamed project " + newProjectName, renamedProject);
		return renamedProject;
	}
}