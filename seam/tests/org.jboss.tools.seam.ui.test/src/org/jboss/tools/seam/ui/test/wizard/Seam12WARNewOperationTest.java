/*******************************************************************************
 * Copyright (c) 2008 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 

package org.jboss.tools.seam.ui.test.wizard;

import java.io.File;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.SeamProjectsSet;
import org.jboss.tools.seam.core.project.facet.SeamRuntime;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeManager;
import org.jboss.tools.seam.ui.wizard.ISeamParameter;
import org.jboss.tools.test.util.JUnitUtils;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ProjectImportTestSetup;
import org.jboss.tools.test.util.WorkbenchUtils;

public class Seam12WARNewOperationTest extends AbstractSeamNewOperationTest {
	private static final String SEAM_WAR_PROJECTNAME = "seam_war";
	private static final String SEAM_WAR_TEST_PROJECTNAME = "seam_war-test";
	protected IProject warProject = null;
	private IProject testProject = null;
	private ISeamProject seamWarProject = null;
	private ISeamProject seamTestProject = null;

	
	public Seam12WARNewOperationTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();

		File folder = getSeamHomeFolder();
		assertNotNull("An error occured while getting the SEAM HOME folder for: " + getSeamRTName(), folder);
		
		SeamRuntimeManager.getInstance().addRuntime(getSeamRTName(), folder.getAbsolutePath(), getSeamRTVersion(getSeamRTName()), true);
		SeamRuntime sr = SeamRuntimeManager.getInstance().findRuntimeByName(getSeamRTName());
		assertNotNull("An error occured while getting the SEAM RUN-TIME for: " + getSeamRTName(), sr);
		
		IResource project = ResourcesPlugin.getWorkspace().getRoot().findMember(SEAM_WAR_PROJECTNAME);
		if(project==null) {
			createSeamWarProject(SEAM_WAR_PROJECTNAME);
		}
		try {
			JobUtils.waitForIdle();
		} catch (Exception e) {
			JUnitUtils.fail(e.getMessage(), e);
		}

		if(warProject==null) {
			warProject = ProjectImportTestSetup.loadProject(SEAM_WAR_PROJECTNAME);
		}
		if(testProject==null) {
			testProject = ProjectImportTestSetup.loadProject(SEAM_WAR_TEST_PROJECTNAME);
		}
		if(seamWarProject==null) {
			seamWarProject = loadSeamProject(warProject);
		}
		if(seamTestProject==null) {
			seamTestProject = loadSeamProject(testProject);
		}
	}
	
	
	protected void tearDown() throws Exception {
		WorkbenchUtils.closeAllEditors();
		
		super.tearDown();
		
		warProject = null;
		testProject = null;
		seamWarProject = null;
		seamTestProject = null;
	}

	protected IProject getProject() {
		return warProject;
	}

	@Override
	void assertProjectsAreCreated() {
		assertTrue("Test project \"" + SEAM_WAR_PROJECTNAME + "\" is not loaded", (warProject != null));
		assertTrue("Test project \"" + SEAM_WAR_TEST_PROJECTNAME + "\" is not loaded", (testProject != null));
		assertTrue("Test Seam project \"" + SEAM_WAR_PROJECTNAME + "\" is not loaded", (seamWarProject != null));
		assertTrue("Test Seam project \"" + SEAM_WAR_TEST_PROJECTNAME + "\" is not loaded", (seamTestProject != null));
	}

	@Override
	void setUpSeamProjects() {
		setUpSeamProject(warProject);
	}

	protected String getSeamRTName() {
		return AbstractSeamNewOperationTest.SEAM_1_2_0;
	}
	

	@Override
	void assertNewActionFilesAreCreatedSuccessfully(AdaptableRegistry data) {
		IEclipsePreferences seamFacetPrefs = SeamCorePlugin.getSeamPreferences(warProject);
		SeamProjectsSet seamPrjSet = new SeamProjectsSet(warProject);

		String sessionBeanPackagePath = getPackagePath(getSessionBeanPackageName(seamFacetPrefs));
		String testCasesPackagePath = getPackagePath(getTestCasesPackageName(seamFacetPrefs));

		IContainer seamProjectSrcActionFolder = seamPrjSet.getActionFolder();
		IContainer testSourceFolder = seamPrjSet.getTestsFolder();
		IContainer seamProjectWebContentFolder = seamPrjSet.getViewsFolder();
		
		String seamPageName = data.getValue(ISeamParameter.SEAM_PAGE_NAME);
		String seamLocalInterfaceName = data.getValue(ISeamParameter.SEAM_LOCAL_INTERFACE_NAME);

		IResource localInterfaceJava = seamProjectSrcActionFolder.findMember(
				sessionBeanPackagePath + "/" + seamLocalInterfaceName + ".java");
		assertResourceIsCreatedAndHasNoProblems(localInterfaceJava, 
				seamProjectSrcActionFolder.toString() + "/" +
				sessionBeanPackagePath + "/" + seamLocalInterfaceName + ".java");

		IResource localInterfaceTestJava = testSourceFolder.findMember(
				testCasesPackagePath + "/" + seamLocalInterfaceName + "Test.java");
		assertResourceIsCreatedAndHasNoProblems(localInterfaceTestJava, 
				testSourceFolder.toString() + "/" +
				sessionBeanPackagePath + "/" + seamLocalInterfaceName + ".java");

		IResource localInterfaceTestXml = testSourceFolder.findMember(
				testCasesPackagePath + "/" + seamLocalInterfaceName + "Test.xml");
		assertResourceIsCreatedAndHasNoProblems(localInterfaceTestXml, 
				testSourceFolder.toString() + "/" +
				sessionBeanPackagePath + "/" + seamLocalInterfaceName + ".java");

		IResource seamPageNameXhtml = seamProjectWebContentFolder.findMember(
				seamPageName + ".xhtml");
		assertResourceIsCreatedAndHasNoProblems(seamPageNameXhtml, 
				seamProjectWebContentFolder.toString() + "/" +
				seamPageName + ".xhtml");
		
/*		
		"${" + ISeamParameter.SEAM_PROJECT_SRC_ACTION + "}/
			${" + ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_PATH + "}/
			${" + ISeamParameter.SEAM_LOCAL_INTERFACE_NAME +"}.java", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		"${" + ISeamParameter.TEST_SOURCE_FOLDER + "}/
			${" + ISeamFacetDataModelProperties.TEST_CASES_PACKAGE_PATH + "}/
			${"+ ISeamParameter.SEAM_LOCAL_INTERFACE_NAME +"}Test.java", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		"${" + ISeamParameter.TEST_SOURCE_FOLDER + "}/
			${" + ISeamFacetDataModelProperties.TEST_CASES_PACKAGE_PATH + "}/
			${"+ ISeamParameter.SEAM_LOCAL_INTERFACE_NAME +"}Test.xml", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		"${" + ISeamParameter.SEAM_PROJECT_WEBCONTENT_PATH + "}/
			${" + ISeamParameter.SEAM_PAGE_NAME +"}.xhtml",	 //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
*/
	}

	@Override
	void assertNewConversationFilesAreCreatedSuccessfully(AdaptableRegistry data) {
		IEclipsePreferences seamFacetPrefs = SeamCorePlugin.getSeamPreferences(warProject);
		SeamProjectsSet seamPrjSet = new SeamProjectsSet(warProject);

		String sessionBeanPackagePath = getPackagePath(getSessionBeanPackageName(seamFacetPrefs));

		IContainer seamProjectSrcActionFolder = seamPrjSet.getActionFolder();
		IContainer seamProjectWebContentFolder = seamPrjSet.getViewsFolder();
		
		String seamPageName = data.getValue(ISeamParameter.SEAM_PAGE_NAME);
		String seamBeanName = data.getValue(ISeamParameter.SEAM_BEAN_NAME);
		
		IResource seamBeanJava = seamProjectSrcActionFolder.findMember(
				sessionBeanPackagePath + "/" + seamBeanName + ".java");
		assertResourceIsCreatedAndHasNoProblems(seamBeanJava, 
				seamProjectSrcActionFolder.toString() + "/" +
				sessionBeanPackagePath + "/" + seamBeanName + ".java");
		
		IResource seamPageNameXhtml = seamProjectWebContentFolder.findMember(
				seamPageName + ".xhtml");
		assertResourceIsCreatedAndHasNoProblems(seamPageNameXhtml, 
				seamProjectWebContentFolder.toString() + "/" +
				seamPageName + ".xhtml");
		
/*		
		"${" + ISeamParameter.SEAM_PROJECT_SRC_ACTION + "}/
			${" + ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_PATH + "}/
			${" + ISeamParameter.SEAM_BEAN_NAME +"}.java", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		"${" + ISeamParameter.SEAM_PROJECT_WEBCONTENT_PATH + "}/
			${" + ISeamParameter.SEAM_PAGE_NAME +"}.xhtml",	 //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
*/
	}

	@Override
	void assertNewEntityFilesAreCreatedSuccessfully(AdaptableRegistry data) {
		IEclipsePreferences seamFacetPrefs = SeamCorePlugin.getSeamPreferences(warProject);
		SeamProjectsSet seamPrjSet = new SeamProjectsSet(warProject);

		String sessionBeanPackagePath = getPackagePath(getSessionBeanPackageName(seamFacetPrefs));
		String entityBeanPackagePath = getPackagePath(getEntityBeanPackageName(seamFacetPrefs));

		IContainer seamProjectSrcActionFolder = seamPrjSet.getActionFolder();
		IContainer seamProjectSrcModelFolder = seamPrjSet.getModelFolder();
		IContainer seamProjectWebContentFolder = seamPrjSet.getViewsFolder();
		
		String seamPageName = data.getValue(ISeamParameter.SEAM_PAGE_NAME);
		String seamMasterPageName = data.getValue(ISeamParameter.SEAM_MASTER_PAGE_NAME);
		String seamEntityClassName = data.getValue(ISeamParameter.SEAM_ENTITY_CLASS_NAME);
		
		IResource seamPageNameXhtml = seamProjectWebContentFolder.findMember(
				seamPageName + ".xhtml");
		assertResourceIsCreatedAndHasNoProblems(seamPageNameXhtml, 
				seamProjectWebContentFolder.toString() + "/" +
				seamPageName + ".xhtml");
		
		IResource seamMasterPageNameXhtml = seamProjectWebContentFolder.findMember(
				seamMasterPageName + ".xhtml");
		assertResourceIsCreatedAndHasNoProblems(seamMasterPageNameXhtml, 
				seamProjectWebContentFolder.toString() + "/" +
				seamMasterPageName + ".xhtml");
		
		IResource entityClassJava = seamProjectSrcModelFolder.findMember(
				entityBeanPackagePath + "/" + seamEntityClassName + ".java");
		assertResourceIsCreatedAndHasNoProblems(entityClassJava, 
				seamProjectSrcModelFolder.toString() + "/" +
				entityBeanPackagePath + "/" + seamEntityClassName + ".java");
		
		IResource entityHomeJava = seamProjectSrcActionFolder.findMember(
				sessionBeanPackagePath + "/" + seamEntityClassName + "Home.java");
		assertResourceIsCreatedAndHasNoProblems(entityHomeJava, 
				seamProjectSrcActionFolder.toString() + "/" +
				sessionBeanPackagePath + "/" + seamEntityClassName + "Home.java");
		
		IResource entityListJava = seamProjectSrcActionFolder.findMember(
				sessionBeanPackagePath + "/" + seamEntityClassName + "List.java");
		assertResourceIsCreatedAndHasNoProblems(entityListJava, 
				seamProjectSrcActionFolder.toString() + "/" +
				sessionBeanPackagePath + "/" + seamEntityClassName + "List.java");
		
/*		
		"${" + ISeamParameter.SEAM_PROJECT_WEBCONTENT_PATH + "}/
			${" + ISeamParameter.SEAM_PAGE_NAME +"}.xhtml", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		"${" + ISeamParameter.SEAM_PROJECT_WEBCONTENT_PATH + "}/
			${" + ISeamParameter.SEAM_MASTER_PAGE_NAME +"}.xhtml",	 //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		"${" + ISeamParameter.SEAM_PROJECT_SRC_MODEL + "}/
			${" + ISeamFacetDataModelProperties.ENTITY_BEAN_PACKAGE_PATH + "}/
			${" + ISeamParameter.SEAM_ENTITY_CLASS_NAME +"}.java", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		"${" + ISeamParameter.SEAM_PROJECT_SRC_ACTION + "}/
			${" + ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_PATH + "}/
			${" + ISeamParameter.SEAM_ENTITY_CLASS_NAME +"}Home.java", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		"${" + ISeamParameter.SEAM_PROJECT_SRC_ACTION + "}/
			${" + ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_PATH + "}/
			${" + ISeamParameter.SEAM_ENTITY_CLASS_NAME +"}List.java", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
*/
	}

	@Override
	void assertNewFormFilesAreCreatedSuccessfully(AdaptableRegistry data) {
		assertNewActionFilesAreCreatedSuccessfully(data);
	}
}
