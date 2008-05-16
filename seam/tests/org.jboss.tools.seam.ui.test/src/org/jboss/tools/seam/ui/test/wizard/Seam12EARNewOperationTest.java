package org.jboss.tools.seam.ui.test.wizard;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.SeamProjectsSet;
import org.jboss.tools.seam.ui.wizard.IParameter;
import org.jboss.tools.test.util.ProjectImportTestSetup;

public class Seam12EARNewOperationTest extends AbstractSeamNewOperationTest {
	private static final String SEAM_EAR_PROJECTNAME = "seam_ear";
	private static final String SEAM_EAR_EJB_PROJECTNAME = "seam_ear-ejb";
	private static final String SEAM_EAR_TEST_PROJECTNAME = "seam_ear-test";
	private IProject earProject = null;
	private IProject earEjbProject = null;
	private IProject testProject = null;
	private ISeamProject seamEarProject = null;
	private ISeamProject seamEarEjbProject = null;
	private ISeamProject seamTestProject = null;

	
	public Seam12EARNewOperationTest() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		if(earProject==null) {
			earProject = ProjectImportTestSetup.loadProject(SEAM_EAR_PROJECTNAME);
		}
		if(earEjbProject==null) {
			earProject = ProjectImportTestSetup.loadProject(SEAM_EAR_EJB_PROJECTNAME);
		}
		if(testProject==null) {
			testProject = ProjectImportTestSetup.loadProject(SEAM_EAR_TEST_PROJECTNAME);
		}
		if(seamEarProject==null) {
			seamEarProject = loadSeamProject(earProject);
		}
		if(seamEarEjbProject==null) {
			seamEarEjbProject = loadSeamProject(earEjbProject);
		}
		if(seamTestProject==null) {
			seamTestProject = loadSeamProject(earProject);
		}
	}
	
	protected IProject getProject() {
		return earProject;
	}

	@Override
	void assertProjectsAreCreated() {
		assertTrue("Test project \"" + SEAM_EAR_PROJECTNAME + "\" is not loaded", (earProject != null));
		assertTrue("Test project \"" + SEAM_EAR_EJB_PROJECTNAME + "\" is not loaded", (earEjbProject != null));
		assertTrue("Test project \"" + SEAM_EAR_TEST_PROJECTNAME + "\" is not loaded", (testProject != null));
		assertTrue("Test Seam project \"" + SEAM_EAR_PROJECTNAME + "\" is not loaded", (seamEarProject != null));
		assertTrue("Test Seam project \"" + SEAM_EAR_EJB_PROJECTNAME + "\" is not loaded", (seamEarEjbProject != null));
		assertTrue("Test Seam project \"" + SEAM_EAR_TEST_PROJECTNAME + "\" is not loaded", (seamTestProject != null));
	}

	@Override
	void setUpSeamProjects() {
		setUpSeamProject(earProject, AbstractSeamNewOperationTest.SEAM_1_2);
	}

	@Override
	void assertNewActionFilesAreCreatedSuccessfully(AdaptableRegistry data) {
		IEclipsePreferences seamFacetPrefs = SeamCorePlugin.getSeamPreferences(earProject);
		SeamProjectsSet seamPrjSet = new SeamProjectsSet(earProject);

		String sessionBeanPackagePath = getPackagePath(getSessionBeanPackageName(seamFacetPrefs));
		String testCasesPackagePath = getPackagePath(getTestCasesPackageName(seamFacetPrefs));

		IContainer seamProjectSrcActionFolder = seamPrjSet.getActionFolder();
		IContainer testSourceFolder = seamPrjSet.getTestsFolder();
		IContainer seamProjectWebContentFolder = seamPrjSet.getViewsFolder();
		
		String seamPageName = data.getValue(IParameter.SEAM_PAGE_NAME);
		String seamLocalInterfaceName = data.getValue(IParameter.SEAM_LOCAL_INTERFACE_NAME);
		String seamBeanName = data.getValue(IParameter.SEAM_BEAN_NAME);

		IResource localInterfaceJava = seamProjectSrcActionFolder.findMember(
				sessionBeanPackagePath + "/" + seamLocalInterfaceName + ".java");
		assertResourceIsCreatedAndHasNoProblems(localInterfaceJava, 
				seamProjectSrcActionFolder.toString() + "/" +
				sessionBeanPackagePath + "/" + seamLocalInterfaceName + ".java");
		
		IResource beanJava = seamProjectSrcActionFolder.findMember(
				sessionBeanPackagePath + "/" + seamBeanName + ".java");
		assertResourceIsCreatedAndHasNoProblems(beanJava, 
				seamProjectSrcActionFolder.toString() + "/" +
				sessionBeanPackagePath + "/" + seamBeanName + ".java");

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
		"${" + IParameter.SEAM_PROJECT_SRC_ACTION + "}/
			${" + ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_PATH + "}/
			${" + IParameter.SEAM_BEAN_NAME +"}.java", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		"${" + IParameter.SEAM_PROJECT_SRC_ACTION + "}/
			${" + ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_PATH + "}/
			${" + IParameter.SEAM_LOCAL_INTERFACE_NAME +"}.java", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		"${" + IParameter.TEST_SOURCE_FOLDER + "}/
			${" + ISeamFacetDataModelProperties.TEST_CASES_PACKAGE_PATH + "}/
			${"+ IParameter.SEAM_LOCAL_INTERFACE_NAME +"}Test.java", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		"${" + IParameter.TEST_SOURCE_FOLDER + "}/
			${" + ISeamFacetDataModelProperties.TEST_CASES_PACKAGE_PATH + "}/
			${"+ IParameter.SEAM_LOCAL_INTERFACE_NAME +"}Test.xml", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		"${" + IParameter.SEAM_PROJECT_WEBCONTENT_PATH + "}/
			${" + IParameter.SEAM_PAGE_NAME +"}.xhtml",	 //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
*/
	}

	@Override
	void assertNewConversationFilesAreCreatedSuccessfully(AdaptableRegistry data) {
		IEclipsePreferences seamFacetPrefs = SeamCorePlugin.getSeamPreferences(earProject);
		SeamProjectsSet seamPrjSet = new SeamProjectsSet(earProject);

		String sessionBeanPackagePath = getPackagePath(getSessionBeanPackageName(seamFacetPrefs));

		IContainer seamProjectSrcActionFolder = seamPrjSet.getActionFolder();
		IContainer seamProjectWebContentFolder = seamPrjSet.getViewsFolder();
		
		String seamPageName = data.getValue(IParameter.SEAM_PAGE_NAME);
		String seamLocalInterfaceName = data.getValue(IParameter.SEAM_LOCAL_INTERFACE_NAME);
		String seamBeanName = data.getValue(IParameter.SEAM_BEAN_NAME);

		IResource beanJava = seamProjectSrcActionFolder.findMember(
				sessionBeanPackagePath + "/" + seamBeanName + ".java");
		assertResourceIsCreatedAndHasNoProblems(beanJava, 
				seamProjectSrcActionFolder.toString() + "/" +
				sessionBeanPackagePath + "/" + seamBeanName + ".java");
		
		IResource localInterfaceJava = seamProjectSrcActionFolder.findMember(
				sessionBeanPackagePath + "/" + seamLocalInterfaceName + ".java");
		assertResourceIsCreatedAndHasNoProblems(localInterfaceJava, 
				seamProjectSrcActionFolder.toString() + "/" +
				sessionBeanPackagePath + "/" + seamLocalInterfaceName + ".java");
		
		IResource seamPageNameXhtml = seamProjectWebContentFolder.findMember(
				seamPageName + ".xhtml");
		assertResourceIsCreatedAndHasNoProblems(seamPageNameXhtml, 
				seamProjectWebContentFolder.toString() + "/" +
				seamPageName + ".xhtml");
		
/*		
		"${" + IParameter.SEAM_PROJECT_SRC_ACTION + "}/
			${" + ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_PATH + "}/
			${" + IParameter.SEAM_BEAN_NAME +"}.java", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		"${" + IParameter.SEAM_PROJECT_SRC_ACTION + "}/
			${" + ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_PATH + "}/
			${" + IParameter.SEAM_LOCAL_INTERFACE_NAME +"}.java", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		"${" + IParameter.SEAM_PROJECT_WEBCONTENT_PATH + "}/
			${" + IParameter.SEAM_PAGE_NAME +"}.xhtml",	 //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
*/
	}

	@Override
	void assertNewEntityFilesAreCreatedSuccessfully(AdaptableRegistry data) {
		IEclipsePreferences seamFacetPrefs = SeamCorePlugin.getSeamPreferences(earProject);
		SeamProjectsSet seamPrjSet = new SeamProjectsSet(earProject);

		String sessionBeanPackagePath = getPackagePath(getSessionBeanPackageName(seamFacetPrefs));
		String entityBeanPackagePath = getPackagePath(getEntityBeanPackageName(seamFacetPrefs));

		IContainer seamProjectSrcActionFolder = seamPrjSet.getActionFolder();
		IContainer seamProjectSrcModelFolder = seamPrjSet.getModelFolder();
		IContainer seamProjectWebContentFolder = seamPrjSet.getViewsFolder();
		
		String seamPageName = data.getValue(IParameter.SEAM_PAGE_NAME);
		String seamMasterPageName = data.getValue(IParameter.SEAM_MASTER_PAGE_NAME);
		String seamEntityClassName = data.getValue(IParameter.SEAM_ENTITY_CLASS_NAME);
		
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
		
		IResource seamMasterPageNameXhtml = seamProjectWebContentFolder.findMember(
				seamMasterPageName + ".xhtml");
		assertResourceIsCreatedAndHasNoProblems(seamMasterPageNameXhtml, 
				seamProjectWebContentFolder.toString() + "/" +
				seamMasterPageName + ".xhtml");
		
		IResource seamPageNameXhtml = seamProjectWebContentFolder.findMember(
				seamPageName + ".xhtml");
		assertResourceIsCreatedAndHasNoProblems(seamPageNameXhtml, 
				seamProjectWebContentFolder.toString() + "/" +
				seamPageName + ".xhtml");
		
/*		
		"${" + IParameter.SEAM_PROJECT_WEBCONTENT_PATH + "}/
			${" + IParameter.SEAM_PAGE_NAME +"}.xhtml", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		"${" + IParameter.SEAM_PROJECT_WEBCONTENT_PATH + "}/
			${" + IParameter.SEAM_MASTER_PAGE_NAME +"}.xhtml",	 //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		"${" + IParameter.SEAM_PROJECT_SRC_MODEL + "}/
			${" + ISeamFacetDataModelProperties.ENTITY_BEAN_PACKAGE_PATH + "}/
			${" + IParameter.SEAM_ENTITY_CLASS_NAME +"}.java", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		"${" + IParameter.SEAM_PROJECT_SRC_ACTION + "}/
			${" + ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_PATH + "}/
			${" + IParameter.SEAM_ENTITY_CLASS_NAME +"}Home.java", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		"${" + IParameter.SEAM_PROJECT_SRC_ACTION + "}/
			${" + ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_PATH + "}/
			${" + IParameter.SEAM_ENTITY_CLASS_NAME +"}List.java", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
*/
	}

	@Override
	void assertNewFormFilesAreCreatedSuccessfully(AdaptableRegistry data) {
		assertNewActionFilesAreCreatedSuccessfully(data);
/*		
		"${" + IParameter.SEAM_PROJECT_SRC_ACTION + "}/
			${" + ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_PATH + "}/
			${" + IParameter.SEAM_BEAN_NAME +"}.java", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		"${" + IParameter.SEAM_PROJECT_SRC_ACTION + "}/
			${" + ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_PATH + "}/
			${" + IParameter.SEAM_LOCAL_INTERFACE_NAME +"}.java", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		"${" + IParameter.TEST_SOURCE_FOLDER + "}/
			${" + ISeamFacetDataModelProperties.TEST_CASES_PACKAGE_PATH + "}/
			${"+ IParameter.SEAM_LOCAL_INTERFACE_NAME +"}Test.java", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		"${" + IParameter.TEST_SOURCE_FOLDER + "}/
			${" + ISeamFacetDataModelProperties.TEST_CASES_PACKAGE_PATH + "}/
			${"+ IParameter.SEAM_LOCAL_INTERFACE_NAME +"}Test.xml", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		"${" + IParameter.SEAM_PROJECT_WEBCONTENT_PATH + "}/
			${" + IParameter.SEAM_PAGE_NAME +"}.xhtml",	//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
*/				
	}
}
