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
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.refactoring.reorg.IConfirmQuery;
import org.eclipse.jdt.internal.corext.refactoring.reorg.ICreateTargetQueries;
import org.eclipse.jdt.internal.corext.refactoring.reorg.ICreateTargetQuery;
import org.eclipse.jdt.internal.corext.refactoring.reorg.IReorgQueries;
import org.eclipse.jdt.internal.corext.refactoring.reorg.JavaMoveProcessor;
import org.eclipse.jdt.internal.corext.refactoring.reorg.ReorgDestinationFactory;
import org.eclipse.jdt.internal.corext.refactoring.reorg.ReorgPolicyFactory;
import org.eclipse.jdt.internal.corext.refactoring.reorg.IReorgPolicy.IMovePolicy;
import org.eclipse.jdt.internal.corext.refactoring.tagging.ITextUpdating;
import org.eclipse.jdt.internal.ui.refactoring.RefactoringExecutionHelper;
import org.eclipse.jdt.internal.ui.refactoring.reorg.RenameSelectionState;
import org.eclipse.jdt.ui.refactoring.RefactoringSaveHelper;
import org.eclipse.jdt.ui.refactoring.RenameSupport;
import org.eclipse.ltk.core.refactoring.RefactoringCore;
import org.eclipse.ltk.core.refactoring.participants.MoveRefactoring;
import org.eclipse.ltk.core.refactoring.participants.RenameRefactoring;
import org.eclipse.ltk.internal.core.refactoring.resource.RenameResourceProcessor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.test.util.JUnitUtils;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ProjectImportTestSetup;
import org.jboss.tools.test.util.WorkbenchUtils;

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
	static String viewFolderParentName = "webroot";
	static String viewFolderName = "WebContent";
	static String viewFolderPath = "/" + warProjectName + "/" + viewFolderParentName + "/" + viewFolderName;
	static String actionPackageName = "ejbdemo";
	static String modelPackageName = "wardemo";
	static String testPackageName = "testdemo";
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
			warProject = ProjectImportTestSetup.loadProject(warProjectName);
		}
		if(ejbProject==null) {
			ejbProject = ProjectImportTestSetup.loadProject(ejbProjectName);;
		}
		if(testProject==null) {
			testProject = ProjectImportTestSetup.loadProject(testProjectName);
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

	private ISeamProject loadSeamProject(IProject project) throws CoreException {
		ISeamProject seamProject = SeamCorePlugin.getSeamProject(project, true);
		assertNotNull("Seam project for " + project.getName() + " is null", seamProject);
		JobUtils.waitForIdle();
		return seamProject;
	}

	public void testWarProjectRename() throws CoreException {
		warProjectName = "NewWarProjectName";
		updateFields();
		warProject = renameProject(warProject, warProjectName);
		seamWarProject = SeamCorePlugin.getSeamProject(warProject, true);
		assertCorrectProperties();
	}

	public void testEjbProjectRename() throws CoreException {
		ejbProjectName = "NewEjbProjectName";
		updateFields();
		ejbProject = renameProject(ejbProject, ejbProjectName);
		seamEjbProject = SeamCorePlugin.getSeamProject(ejbProject, true);
		assertCorrectProperties();
	}

	public void testTestProjectRename() throws CoreException {
		testProjectName = "NewTestProjectName";
		updateFields();
		testProject = renameProject(testProject, testProjectName);
		seamTestProject = SeamCorePlugin.getSeamProject(testProject, true);
		assertCorrectProperties();
	}

	public void testActionSourceFolderRename() throws CoreException {
		actionSourceFolderName = "newActionSrc";
		renameSourceFolder(actionSourceFolderPath, actionSourceFolderName);
		assertCorrectProperties();
	}

	public void testModelSourceFolderRename() throws CoreException {
		modelSourceFolderName = "newModelSrc";
		renameSourceFolder(modelSourceFolderPath, modelSourceFolderName);
		assertCorrectProperties();
	}

	public void testTestSourceFolderRename() throws CoreException {
		testSourceFolderName = "newTestSrc";
		renameSourceFolder(testSourceFolderPath, testSourceFolderName);
		assertCorrectProperties();
	}

	public void testViewFolderRename() throws CoreException {
		viewFolderName = "newViewFolder";
		renameFolder(viewFolderPath, viewFolderName);
		assertCorrectProperties();
	}

//	public void testActionPackageRename() throws CoreException {
//		System.out.println("SeamPropertyRefactoringTest testActionPackageRename");
//		String oldName = actionPackageName;
//		actionPackageName = "newejbdemo";
//		renamePackage(actionSourceFolderPath, oldName, actionPackageName);
//		assertCorrectProperties();
//	}

//	public void testModelPackageRename() throws CoreException {
//		System.out.println("SeamPropertyRefactoringTest testModelPackageRename");
//		String oldName = modelPackageName;
//		modelPackageName = "newwardemo";
//		renamePackage(modelSourceFolderPath, oldName, modelPackageName);
//		assertCorrectProperties();
//	}

//	public void testTestPackageRename() throws CoreException {
//		System.out.println("SeamPropertyRefactoringTest testTestPackageRename");
//		String oldName = testPackageName;
//		testPackageName = "newtestdemo";
//		renamePackage(testSourceFolderPath, oldName, testPackageName);
//		assertCorrectProperties();
//	}

	public void testViewFolderMove() throws CoreException {
		viewFolderParentName = "testwebroot";
		moveFolder(viewFolderPath, "/" + warProjectName + "/" + viewFolderParentName);
		assertCorrectProperties();
	}

	private void assertCorrectProperties() {
		updateFields();

		IEclipsePreferences pref = SeamCorePlugin.getSeamPreferences(warProject);

		String parentName = seamEjbProject.getParentProjectName();
		assertEquals("Parent seam project property for EJB project was not updated.", warProjectName, parentName);

		parentName = seamTestProject.getParentProjectName();
		assertEquals("Parent seam project property for Test project was not updated.", warProjectName, parentName);

		String ejbName = pref.get(ISeamFacetDataModelProperties.SEAM_EJB_PROJECT, "");
		assertEquals("EJB project name property was not updated.", ejbProjectName, ejbName);

		String testName = pref.get(ISeamFacetDataModelProperties.SEAM_TEST_PROJECT, "");
		assertEquals("Test project name property was not updated.", testProjectName, testName);

		String actionSources = pref.get(ISeamFacetDataModelProperties.SESSION_BEAN_SOURCE_FOLDER, "");
		assertEquals("Action source folder property was not updated.", actionSourceFolderPath, actionSources);

		String modelSources = pref.get(ISeamFacetDataModelProperties.ENTITY_BEAN_SOURCE_FOLDER, "");
		assertEquals("Model source folder property was not.", modelSourceFolderPath, modelSources);

		String testSources = pref.get(ISeamFacetDataModelProperties.TEST_SOURCE_FOLDER, "");
		assertEquals("Test source folder property was not updated.", testSourceFolderPath, testSources);

		String viewFolder = pref.get(ISeamFacetDataModelProperties.WEB_CONTENTS_FOLDER, "");
		assertEquals("View folder property was not updated.", viewFolderPath, viewFolder);

		String actionPackage = pref.get(ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_NAME, "");
		assertEquals("Action package name property was not updated.", actionPackageName, actionPackage);

		String modelPackage = pref.get(ISeamFacetDataModelProperties.ENTITY_BEAN_PACKAGE_NAME, "");
		assertEquals("Model package name property was not updated.", modelPackageName, modelPackage);

		String testPackage = pref.get(ISeamFacetDataModelProperties.TEST_CASES_PACKAGE_NAME, "");
		assertEquals("Test package name property was not updated.", testPackageName, testPackage);
	}

	private void updateFields() {
		actionSourceFolderPath = "/" + ejbProjectName + "/" + actionSourceFolderName;
		modelSourceFolderPath = "/" + warProjectName + "/" + modelSourceFolderName;
		testSourceFolderPath = "/" + testProjectName + "/" + testSourceFolderName;
		viewFolderPath = "/" + warProjectName + "/" + viewFolderParentName + "/" + viewFolderName;
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
		IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(folderPath);
		assertNotNull("Can't find folder: " + folderPath, resource);

		// init refactoring
		RenameResourceProcessor processor = new RenameResourceProcessor(resource);
		RenameRefactoring refactoring = new RenameRefactoring(processor);
		processor.setNewResourceName(newFolderName);
		processor.setUpdateReferences(true);
		ITextUpdating text = (ITextUpdating)refactoring.getAdapter(ITextUpdating.class);
		if(text != null) {
			text.setUpdateTextualMatches(true);
		}

		// perform
		Object[] elements = processor.getElements();
		RenameSelectionState state = elements.length==1?new RenameSelectionState(elements[0]):null;
		RefactoringExecutionHelper helper= new RefactoringExecutionHelper(refactoring,
				RefactoringCore.getConditionCheckingFailedSeverity(),
				RefactoringSaveHelper.SAVE_ALL,
				WorkbenchUtils.getActiveShell(),
				WorkbenchUtils.getWorkbench().getActiveWorkbenchWindow());
		try {
			helper.perform(true, true);
		} catch (InterruptedException e) {
			JUnitUtils.fail("Exception during perform folder renaming: " + folderPath, e);
		} catch (InvocationTargetException e) {
			JUnitUtils.fail("Exception during perform folder renaming: " + folderPath, e);
		}

		JobUtils.waitForIdle();

		IPath path = new Path(folderPath);
		String newFolderPath = path.removeLastSegments(1).append(newFolderName).toString();
		resource = ResourcesPlugin.getWorkspace().getRoot().findMember(newFolderPath);
		assertNotNull("Can't find folder: " + newFolderPath, resource);

		return (IFolder)resource;
	}

	private IPackageFragment renamePackage(String sourceFolderPath, String oldPackageName, String newPackageName) throws CoreException {
		IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(sourceFolderPath);
		assertNotNull("Can't find source folder: " + sourceFolderPath, resource);
		IProject project = resource.getProject();
		IJavaProject javaProject = EclipseResourceUtil.getJavaProject(project);
		assertNotNull("Can't find java project: " + project.getName(), javaProject);
		IPackageFragmentRoot root = null;
		try {
			IPackageFragmentRoot[] roots = javaProject.getPackageFragmentRoots();
			for (int i = 0; i < roots.length; i++) {
				if (roots[i].getKind() == IPackageFragmentRoot.K_SOURCE && roots[i].getResource().getFullPath().toString().equals(sourceFolderPath)) {
					root = roots[i];
					break;
				}
			}
		} catch (JavaModelException e) {
			JUnitUtils.fail("Can't find source folder: " + sourceFolderPath, e);
		}
		assertNotNull("Can't find source folder: " + sourceFolderPath, root);

		IPackageFragment oldPackage = findPackage(root, oldPackageName);
		assertNotNull("Can't find package \"" + oldPackageName + "\". So it's impossible to rename it.", oldPackage);

		IJavaElement[] packages = root.getChildren();
		performRename(RenameSupport.create(oldPackage, newPackageName, RenameSupport.UPDATE_REFERENCES));

		IPackageFragment newPackage = findPackage(root, newPackageName);
		assertNotNull("Can't find renamed package \"" + newPackageName + "\". It seems this package was not renamed.", newPackage);
		return null;
	}

	private IPackageFragment findPackage(IPackageFragmentRoot root, String packageName) {
		IJavaElement[] packages = null;
		try {
			packages = root.getChildren();
		} catch (JavaModelException e) {
			JUnitUtils.fail("Can't find package: " + packageName, e);
		}
		for (IJavaElement javaElement : packages) {
			if(javaElement instanceof IPackageFragment && javaElement.getElementName().equals(packageName)) {
				return (IPackageFragment)javaElement;
			}
		}
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
		JobUtils.waitForIdle();
	}

	private IFolder moveFolder(String folderPath, String destinationFolderPath) throws CoreException {
		IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(folderPath);
		assertNotNull("Can't find folder: " + folderPath, resource);
		IResource destination = ResourcesPlugin.getWorkspace().getRoot().findMember(destinationFolderPath);
		assertNotNull("Can't find destination folder: " + destinationFolderPath, destination);

		IMovePolicy policy = null;
		JavaMoveProcessor processor = null;
		try {
			policy = ReorgPolicyFactory.createMovePolicy(new IResource[]{resource}, new IJavaElement[0]);
			processor = new JavaMoveProcessor(policy);
			
			processor.setDestination(ReorgDestinationFactory.createDestination(destination));
		} catch (JavaModelException e) {
			JUnitUtils.fail("Exception during perform folder moving: " + folderPath, e);
		}
		MoveRefactoring refactoring = new MoveRefactoring(processor);
		processor.setCreateTargetQueries(new ICreateTargetQueries(){
			public ICreateTargetQuery createNewPackageQuery() {
				return null;
			}
		});
		processor.setReorgQueries(new IReorgQueries(){
			public IConfirmQuery createSkipQuery(String queryTitle, int queryID) {
				return null;
			}
			public IConfirmQuery createYesNoQuery(String queryTitle, boolean allowCancel, int queryID) {
				return null;
			}
			public IConfirmQuery createYesYesToAllNoNoToAllQuery(String queryTitle, boolean allowCancel, int queryID) {
				return null;
			}
		});

		// perform
		Object[] elements = processor.getElements();
		RenameSelectionState state = elements.length==1?new RenameSelectionState(elements[0]):null;
		RefactoringExecutionHelper helper= new RefactoringExecutionHelper(refactoring,
				RefactoringCore.getConditionCheckingFailedSeverity(),
				RefactoringSaveHelper.SAVE_ALL,
				WorkbenchUtils.getActiveShell(),
				WorkbenchUtils.getWorkbench().getActiveWorkbenchWindow());
		try {
			helper.perform(true, true);
		} catch (InterruptedException e) {
			JUnitUtils.fail("Exception during perform folder moving: " + folderPath, e);
		} catch (InvocationTargetException e) {
			JUnitUtils.fail("Exception during perform folder moving: " + folderPath, e);
		}

		JobUtils.waitForIdle();

		String newFolderPath = destination.getFullPath().append(resource.getName()).toString();
		resource = ResourcesPlugin.getWorkspace().getRoot().findMember(newFolderPath);
		assertNotNull("Can't find folder: " + newFolderPath, resource);

		return (IFolder)resource;
	}
}