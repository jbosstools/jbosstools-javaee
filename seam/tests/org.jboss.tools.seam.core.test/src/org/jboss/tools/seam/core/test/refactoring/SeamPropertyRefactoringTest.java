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
import java.util.HashMap;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.refactoring.rename.JavaRenameProcessor;
import org.eclipse.jdt.internal.corext.refactoring.rename.RenameJavaProjectProcessor;
import org.eclipse.jdt.internal.corext.refactoring.rename.RenamePackageProcessor;
import org.eclipse.jdt.internal.corext.refactoring.rename.RenameSourceFolderProcessor;
import org.eclipse.jdt.internal.corext.refactoring.reorg.IConfirmQuery;
import org.eclipse.jdt.internal.corext.refactoring.reorg.ICreateTargetQueries;
import org.eclipse.jdt.internal.corext.refactoring.reorg.ICreateTargetQuery;
import org.eclipse.jdt.internal.corext.refactoring.reorg.IReorgQueries;
import org.eclipse.jdt.internal.corext.refactoring.reorg.JavaMoveProcessor;
import org.eclipse.jdt.internal.corext.refactoring.reorg.ReorgDestinationFactory;
import org.eclipse.jdt.internal.corext.refactoring.reorg.ReorgPolicyFactory;
import org.eclipse.jdt.internal.corext.refactoring.reorg.IReorgPolicy.IMovePolicy;
import org.eclipse.jdt.internal.corext.refactoring.tagging.ITextUpdating;
import org.eclipse.jdt.ui.refactoring.RenameSupport;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.participants.MoveArguments;
import org.eclipse.ltk.core.refactoring.participants.MoveParticipant;
import org.eclipse.ltk.core.refactoring.participants.MoveRefactoring;
import org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor;
import org.eclipse.ltk.core.refactoring.participants.RenameArguments;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.eclipse.ltk.core.refactoring.participants.RenameRefactoring;
import org.eclipse.ltk.internal.core.refactoring.resource.RenameResourceProcessor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.seam.internal.core.refactoring.SeamFolderMoveParticipant;
import org.jboss.tools.seam.internal.core.refactoring.SeamFolderRenameParticipant;
import org.jboss.tools.seam.internal.core.refactoring.SeamProjectChange;
import org.jboss.tools.seam.internal.core.refactoring.SeamProjectRenameParticipant;
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
		HashMap<String, String> preferences = new HashMap<String, String>();
		preferences.put(ISeamFacetDataModelProperties.SEAM_PARENT_PROJECT, warProjectName);
		preferences.put(ISeamFacetDataModelProperties.ENTITY_BEAN_SOURCE_FOLDER, "/"+warProjectName+"/src");
		preferences.put(ISeamFacetDataModelProperties.WEB_CONTENTS_FOLDER, "/"+warProjectName+"/webroot/WebContent");
		
		try{
			renameProject(warProject, warProjectName, preferences);
		}finally{
			warProjectName = "RefactoringTestProject-war";
			updateFields();
		}
	}

	public void testEjbProjectRename() throws CoreException {
		ejbProjectName = "NewEjbProjectName";
		HashMap<String, String> preferences = new HashMap<String, String>();
		preferences.put(ISeamFacetDataModelProperties.SEAM_EJB_PROJECT, ejbProjectName);
		preferences.put(ISeamFacetDataModelProperties.SESSION_BEAN_SOURCE_FOLDER, "/"+ejbProjectName+"/src");
		
		try{
			renameProject(ejbProject, ejbProjectName, preferences);
		}finally{
			ejbProjectName = "RefactoringTestProject-ejb";
			updateFields();
		}
	}

	public void testTestProjectRename() throws CoreException {
		testProjectName = "NewTestProjectName";
		HashMap<String, String> preferences = new HashMap<String, String>();
		preferences.put(ISeamFacetDataModelProperties.SEAM_TEST_PROJECT, testProjectName);
		preferences.put(ISeamFacetDataModelProperties.TEST_SOURCE_FOLDER, "/"+testProjectName+"/src");
		
		try{
			renameProject(testProject, testProjectName, preferences);
		}finally{
			testProjectName = "RefactoringTestProject-test";
			updateFields();
		}
	}

	public void testActionSourceFolderRename() throws CoreException {
		actionSourceFolderName = "newActionSrc";
		HashMap<String, String> preferences = new HashMap<String, String>();
		preferences.put(ISeamFacetDataModelProperties.SESSION_BEAN_SOURCE_FOLDER, "/RefactoringTestProject-ejb/RefactoringTestProject-ejb/"+actionSourceFolderName);
		
		try{
			renameSourceFolder(actionSourceFolderPath, actionSourceFolderName, preferences);
		}finally{
			actionSourceFolderName = "src";
			updateFields();
		}
	}

	public void testModelSourceFolderRename() throws CoreException {
		modelSourceFolderName = "newModelSrc";
		HashMap<String, String> preferences = new HashMap<String, String>();
		preferences.put(ISeamFacetDataModelProperties.SESSION_BEAN_SOURCE_FOLDER, "/RefactoringTestProject-ejb/RefactoringTestProject-war/"+modelSourceFolderName);
		
		try{
			renameSourceFolder(modelSourceFolderPath, modelSourceFolderName, preferences);
		}finally{
			modelSourceFolderName = "src";
			updateFields();
		}
	}

	public void testTestSourceFolderRename() throws CoreException {
		testSourceFolderName = "newTestSrc";
		HashMap<String, String> preferences = new HashMap<String, String>();
		preferences.put(ISeamFacetDataModelProperties.SESSION_BEAN_SOURCE_FOLDER, "/RefactoringTestProject-ejb/RefactoringTestProject-test/"+testSourceFolderName);
		
		try{
			renameSourceFolder(testSourceFolderPath, testSourceFolderName, preferences);
		}finally{
			testSourceFolderName = "src";
			updateFields();
		}
	}

	public void testViewFolderRename() throws CoreException {
		viewFolderName = "newViewFolder";
		HashMap<String, String> preferences = new HashMap<String, String>();
		preferences.put(ISeamFacetDataModelProperties.WEB_CONTENTS_FOLDER, "/RefactoringTestProject-war/webroot/"+viewFolderName);
		
		try{
			renameFolder(viewFolderPath, viewFolderName, preferences);
		}finally{
			viewFolderName = "WebContent";
			updateFields();
		}
	}

	public void testActionPackageRename() throws CoreException {
		String oldName = actionPackageName;
		actionPackageName = "newejbdemo";
		HashMap<String, String> preferences = new HashMap<String, String>();
		preferences.put(ISeamFacetDataModelProperties.SESSION_BEAN_SOURCE_FOLDER, "/RefactoringTestProject-ejb/"+actionPackageName);
		
		try{
			renamePackage(actionSourceFolderPath, oldName, actionPackageName, preferences);
		}finally{
			actionPackageName = "ejbdemo";
			updateFields();
		}
	}

	public void testModelPackageRename() throws CoreException {
		String oldName = modelPackageName;
		modelPackageName = "newwardemo";
		HashMap<String, String> preferences = new HashMap<String, String>();
		preferences.put(ISeamFacetDataModelProperties.ENTITY_BEAN_SOURCE_FOLDER, "/RefactoringTestProject-war/"+modelPackageName);
		
		try{
			renamePackage(modelSourceFolderPath, oldName, modelPackageName, preferences);
		}finally{
			modelPackageName = "wardemo";
			updateFields();
		}
	}

	public void testTestPackageRename() throws CoreException {
		String oldName = testPackageName;
		testPackageName = "newtestdemo";
		HashMap<String, String> preferences = new HashMap<String, String>();
		preferences.put(ISeamFacetDataModelProperties.TEST_SOURCE_FOLDER, "/RefactoringTestProject-test/"+testPackageName);
		
		try{
			renamePackage(testSourceFolderPath, oldName, testPackageName, preferences);
		}finally{
			testPackageName = "testdemo";
			updateFields();
		}
	}

	public void testViewFolderMove() throws CoreException {
		viewFolderParentName = "testwebroot";
		HashMap<String, String> preferences = new HashMap<String, String>();
		preferences.put(ISeamFacetDataModelProperties.WEB_CONTENTS_FOLDER, "/RefactoringTestProject-war/"+viewFolderParentName+"/WebContent");
		
		moveFolder(viewFolderPath, "/" + warProjectName + "/" + viewFolderParentName, preferences);
	}

	private void updateFields() {
		actionSourceFolderPath = "/" + ejbProjectName + "/" + actionSourceFolderName;
		modelSourceFolderPath = "/" + warProjectName + "/" + modelSourceFolderName;
		testSourceFolderPath = "/" + testProjectName + "/" + testSourceFolderName;
		viewFolderPath = "/" + warProjectName + "/" + viewFolderParentName + "/" + viewFolderName;
	}

	private void renameSourceFolder(String folderPath, String newFolderName, HashMap<String, String> preferences) throws CoreException {
		IPackageFragmentRoot packageFragmentRoot = getSourceFolder(folderPath);
		IProject project = packageFragmentRoot.getResource().getProject();
		String newPath = project.getFullPath().toString() + "/" + newFolderName;
		
		JavaRenameProcessor processor= new RenameSourceFolderProcessor(packageFragmentRoot);
		SeamFolderRenameParticipant participant = new SeamFolderRenameParticipant();
		IResource folder = ResourcesPlugin.getWorkspace().getRoot().findMember(actionSourceFolderPath);
		
		checkRename(processor, folder, newPath, participant, preferences);
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

	private void renameFolder(String folderPath, String newFolderName, HashMap<String, String> preferences) throws CoreException {
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

		SeamFolderRenameParticipant participant = new SeamFolderRenameParticipant();
		
		checkRename(processor, resource, newFolderName, participant, preferences);
	}

	private void renamePackage(String sourceFolderPath, String oldPackageName, String newPackageName, HashMap<String, String> preferences) throws CoreException {
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

		JavaRenameProcessor processor= new RenamePackageProcessor(oldPackage);
		
		SeamFolderRenameParticipant participant = new SeamFolderRenameParticipant();
		
		checkRename(processor, resource, newPackageName, participant, preferences);
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
	
	private void renameProject(IProject project, String newProjectName, HashMap<String, String> preferences) throws CoreException {
		JavaRenameProcessor processor= new RenameJavaProjectProcessor(JavaCore.create(project));
		
		SeamProjectRenameParticipant participant = new SeamProjectRenameParticipant();
		
		checkRename(processor, project, newProjectName, participant, preferences);
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

	private void moveFolder(String folderPath, String destinationFolderPath, HashMap<String, String> preferences) throws CoreException {
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
		
		SeamFolderMoveParticipant participant = new SeamFolderMoveParticipant();
		
		checkMove(processor, resource, destination, participant, preferences);
	}

	private void checkMove(RefactoringProcessor processor, Object oldObject, Object destinationObject, MoveParticipant participant, HashMap<String, String> preferences) throws CoreException {
		// Move
		MoveArguments arguments = new MoveArguments(destinationObject, true);
		participant.initialize(processor, oldObject, arguments);
		participant.checkConditions(new NullProgressMonitor(), null);
		
		CompositeChange rootChange = (CompositeChange)participant.createChange(new NullProgressMonitor());
		
		for(Change change : rootChange.getChildren()){
			if(change instanceof SeamProjectChange){
				SeamProjectChange seamChange = (SeamProjectChange)change;
				HashMap<String, String> preferencesToCheck = seamChange.getPreferencesForTest();
				
				checkChanges(preferencesToCheck, preferences);
			}
		}
	}

	private void checkRename(RefactoringProcessor processor, Object oldObject, String newName, RenameParticipant participant, HashMap<String, String> preferences) throws CoreException {
		// Rename
		RenameArguments arguments = new RenameArguments(newName, true);
		participant.initialize(processor, oldObject, arguments);
		participant.checkConditions(new NullProgressMonitor(), null);
		
		CompositeChange rootChange = (CompositeChange)participant.createChange(new NullProgressMonitor());
		
		for(Change change : rootChange.getChildren()){
			if(change instanceof SeamProjectChange){
				SeamProjectChange seamChange = (SeamProjectChange)change;
				HashMap<String, String> preferencesToCheck = seamChange.getPreferencesForTest();
				
				checkChanges(preferencesToCheck, preferences);
			}
		}
	}
	
	private void checkChanges(HashMap<String, String> preferencesToCheck, HashMap<String, String> preferences){
		for(String key : preferencesToCheck.keySet()){
			String value = preferences.get(key);
			assertNotNull("Unexpected preference "+key+" not found", value);
			
			String valueToCheck = preferencesToCheck.get(key);
			assertEquals("Wrong preference value", value, valueToCheck);
		}
	}

}