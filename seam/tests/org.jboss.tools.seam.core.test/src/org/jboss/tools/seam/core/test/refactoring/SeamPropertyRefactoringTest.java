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

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.refactoring.RenameSupport;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
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
	static String actionSourceFolderName = "src";
	static String modelSourceFolderName = "src";
	static String testSourceFolderName = "src";
	static String actionSourceFolderPath = "/" + ejbProjectName + "/" + actionSourceFolderName;
	static String modelSourceFolderPath = "/" + warProjectName + "/" + modelSourceFolderName;
	static String testSourceFolderPath = "/" + testProjectName + "/" + testSourceFolderName;
	static String viewFolderName = "WebContent";
	static String viewFolderPath = "/" + warProjectName + "/" + viewFolderName;
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
		updateFields();
		warProject = renameProject(warProject, warProjectName);
		seamWarProject = SeamCorePlugin.getSeamProject(warProject, true);

		String newParentName = seamEjbProject.getParentProjectName();
		assertEquals("WAR project was renamed but parent seam project property for EJB project was not.", warProjectName, newParentName);
		newParentName = seamTestProject.getParentProjectName();
		assertEquals("WAR project was renamed but parent seam project property for test project was not.", warProjectName, newParentName);

		IEclipsePreferences pref = SeamCorePlugin.getSeamPreferences(warProject);
		String modelSources = pref.get(ISeamFacetDataModelProperties.ENTITY_BEAN_SOURCE_FOLDER, "");
		assertEquals("WAR project was renamed but model source folder property was not.", modelSourceFolderPath, modelSources);

		String viewFolder = pref.get(ISeamFacetDataModelProperties.WEB_CONTENTS_FOLDER, "");
		assertEquals("WAR project was renamed but view folder property was not.", viewFolderPath, viewFolder);
	}

	public void testEjbProjectRename() throws CoreException {
		ejbProjectName = "NewEjbProjectName";
		updateFields();
		ejbProject = renameProject(ejbProject, ejbProjectName);
		seamEjbProject = SeamCorePlugin.getSeamProject(ejbProject, true);

		IEclipsePreferences pref = SeamCorePlugin.getSeamPreferences(warProject);
		String newEjbName = pref.get(ISeamFacetDataModelProperties.SEAM_EJB_PROJECT, "");
		assertEquals("EJB project was renamed but property was not.", ejbProjectName, newEjbName);

		String actionSources = pref.get(ISeamFacetDataModelProperties.SESSION_BEAN_SOURCE_FOLDER, "");
		assertEquals("EJB project was renamed but action source folder property was not.", actionSourceFolderPath, actionSources);
	}

	public void testTestProjectRename() throws CoreException {
		testProjectName = "NewTestProjectName";
		updateFields();
		testProject = renameProject(testProject, testProjectName);
		seamTestProject = SeamCorePlugin.getSeamProject(testProject, true);

		String newTestName = SeamCorePlugin.getSeamPreferences(warProject).get(ISeamFacetDataModelProperties.SEAM_TEST_PROJECT, "");
		assertEquals("Test project was renamed but property was not.", testProjectName, newTestName);

		IEclipsePreferences pref = SeamCorePlugin.getSeamPreferences(warProject);
		String testSources = pref.get(ISeamFacetDataModelProperties.TEST_SOURCE_FOLDER, "");
		assertEquals("Test project was renamed but test source folder property was not.", testSourceFolderPath, testSources);
	}

	public void testActionSourceFolderRename() throws CoreException {
		actionSourceFolderName = "newActionSrc";
		renameSourceFolder(actionSourceFolderPath, actionSourceFolderName);
		updateFields();

		IEclipsePreferences pref = SeamCorePlugin.getSeamPreferences(warProject);
		String actionSources = pref.get(ISeamFacetDataModelProperties.SESSION_BEAN_SOURCE_FOLDER, "");
		assertEquals("Action source folder was renamed but property was not.", actionSourceFolderPath, actionSources);
	}

	public void testModelSourceFolderRename() throws CoreException {
		modelSourceFolderName = "newModelSrc";
		renameSourceFolder(modelSourceFolderPath, modelSourceFolderName);
		updateFields();

		IEclipsePreferences pref = SeamCorePlugin.getSeamPreferences(warProject);
		String modelSources = pref.get(ISeamFacetDataModelProperties.ENTITY_BEAN_SOURCE_FOLDER, "");
		assertEquals("Model source folder was renamed but property was not.", modelSourceFolderPath, modelSources);
	}

	public void testTestSourceFolderRename() throws CoreException {
		testSourceFolderName = "newTestSrc";
		renameSourceFolder(testSourceFolderPath, testSourceFolderName);
		updateFields();

		IEclipsePreferences pref = SeamCorePlugin.getSeamPreferences(warProject);
		String testSources = pref.get(ISeamFacetDataModelProperties.TEST_SOURCE_FOLDER, "");
		assertEquals("Test source folder was renamed but property was not.", testSourceFolderPath, testSources);
	}

	public void testViewFolderRename() throws CoreException {
		viewFolderName = "newViewFolder";
		renameFolder(viewFolderPath, viewFolderName);
		updateFields();

		IEclipsePreferences pref = SeamCorePlugin.getSeamPreferences(warProject);
		String viewFolder = pref.get(ISeamFacetDataModelProperties.WEB_CONTENTS_FOLDER, "");
		assertEquals("View folder was renamed but property was not.", viewFolderPath, viewFolder);
	}

	private void updateFields() {
		actionSourceFolderPath = "/" + ejbProjectName + "/" + actionSourceFolderName;
		modelSourceFolderPath = "/" + warProjectName + "/" + modelSourceFolderName;
		testSourceFolderPath = "/" + testProjectName + "/" + testSourceFolderName;
		viewFolderPath = "/" + warProjectName + "/" + viewFolderName;
	}

	private IPackageFragmentRoot renameSourceFolder(String folderPath, String newFolderName) throws CoreException {
		IPackageFragmentRoot packageFragmentRoot = getSourceFolder(folderPath);
		IProject project = packageFragmentRoot.getResource().getProject();
		performRename(RenameSupport.create(packageFragmentRoot, newFolderName));
		String newPath = project.getFullPath().toString() + "/" + newFolderName;
		IPackageFragmentRoot newPackageFragmentRoot = getSourceFolder(newPath);
		assertNotNull("Cannot find renamed source folder: " + newPath, newPackageFragmentRoot);
		return newPackageFragmentRoot;
	}

	private IPackageFragmentRoot getSourceFolder(String folderPath) {
		IResource initSourceFolder = ResourcesPlugin.getWorkspace().getRoot().findMember(folderPath);
		assertNotNull("Can't find source folder: " + folderPath, initSourceFolder);
		IProject project = initSourceFolder.getProject();
		IJavaProject javaProject = EclipseResourceUtil.getJavaProject(project);
		IPackageFragmentRoot packageFragmentRoot = null;
		try {
			IPackageFragmentRoot[] roots = javaProject.getPackageFragmentRoots();
			for (int i = 0; i < roots.length; i++) {
				if (roots[i].getKind() == IPackageFragmentRoot.K_SOURCE && roots[i].getResource().equals(initSourceFolder)) {
					packageFragmentRoot = roots[i];
					break;
				}
			}
		} catch (JavaModelException e) {
			JUnitUtils.fail("Exception during searching source folder: " + folderPath, e);
		}
		assertNotNull("Can't find source folder: " + folderPath, packageFragmentRoot);
		return packageFragmentRoot;
	}

	private IFolder renameFolder(String folderPath, String newFolderName) throws CoreException {
		return null;
	}

	private IProject renameProject(IProject project, String newProjectName) throws CoreException {
		performRename(RenameSupport.create(JavaCore.create(project), newProjectName, RenameSupport.UPDATE_REFERENCES));

		IProject renamedProject = (IProject)ResourcesPlugin.getWorkspace().getRoot().findMember(newProjectName);
		assertNotNull("Can't load renamed project " + newProjectName, renamedProject);
		return renamedProject;
	}

	private void performRename(RenameSupport support) throws CoreException {
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
	}
}