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
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.SeamProjectsSet;
import org.jboss.tools.seam.core.project.facet.SeamRuntime;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeManager;
import org.jboss.tools.seam.core.project.facet.SeamVersion;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.seam.internal.core.project.facet.SeamFacetInstallDataModelProvider;
import org.jboss.tools.seam.ui.widget.editor.IFieldEditor;
import org.jboss.tools.seam.ui.wizard.IParameter;
import org.jboss.tools.seam.ui.wizard.SeamWizardFactory;
import org.jboss.tools.seam.ui.wizard.SeamWizardUtils;
import org.jboss.tools.seam.ui.wizard.SeamActionWizard.SeamActionCreateOperation;
import org.jboss.tools.seam.ui.wizard.SeamConversationWizard.SeamConversationCreateOperation;
import org.jboss.tools.seam.ui.wizard.SeamEntityWizard.SeamEntityCreateOperation;
import org.jboss.tools.seam.ui.wizard.SeamFormWizard.SeamFormCreateOperation;
import org.jboss.tools.test.util.JUnitUtils;
import org.jboss.tools.test.util.xpl.EditorTestHelper;
import org.osgi.service.prefs.BackingStoreException;

abstract public class AbstractSeamNewOperationTest extends TestCase {
	protected static final IWorkspace ws = ResourcesPlugin.getWorkspace();
	protected static final IWorkbench wb = PlatformUI.getWorkbench();
	
	protected static final String SEAM_1_2 = "Seam 1.2.0";
	protected static final String SEAM_2_0 = "Seam 2.0.0";

	protected static final String SEAM_1_2_HOME = "jbosstools.test.seam.1.2.1.eap.home";
	protected static final String SEAM_2_0_HOME = "jbosstools.test.seam.2.0.1.GA.home";
	
	private static final String SEAM_ACTION_COMPONENT_NAME = "TestAction";
	private static final String SEAM_FORM_COMPONENT_NAME = "TestForm";
	private static final String SEAM_CONVERSATION_COMPONENT_NAME = "TestConversation";
	private static final String SEAM_ENTITY_COMPONENT_NAME = "TestEntity";
	
	private static final IUndoableOperation CREATE_SEAM_ACTION = new SeamActionCreateOperation();
	private static final IUndoableOperation CREATE_SEAM_FORM = new SeamFormCreateOperation();
	private static final IUndoableOperation CREATE_SEAM_CONVERSATION = new SeamConversationCreateOperation();
	private static final IUndoableOperation CREATE_SEAM_ENTITY = new SeamEntityCreateOperation();

	protected final Set<IResource> resourcesToCleanup = new HashSet<IResource>();

	protected static final IProjectFacetVersion dynamicWebVersion;
	protected static final IProjectFacetVersion javaVersion;
	protected static final IProjectFacetVersion javaFacesVersion;
	private static final IProjectFacet seamFacet;

	static {
		javaVersion = ProjectFacetsManager.getProjectFacet("jst.java").getVersion("5.0");
		dynamicWebVersion = ProjectFacetsManager.getProjectFacet("jst.web").getVersion("2.5");
		javaFacesVersion = ProjectFacetsManager.getProjectFacet("jst.jsf").getVersion("1.2");
		seamFacet = ProjectFacetsManager.getProjectFacet("jst.seam");
	}

	public AbstractSeamNewOperationTest() {
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		try { EditorTestHelper.joinBackgroundActivities(); } 
		catch (Exception e) { JUnitUtils.fail(e.getMessage(), e); }
		EditorTestHelper.runEventQueue(3000);
	}

	protected void tearDown() throws Exception {
		// Wait until all jobs is finished to avoid delete project problems
		EditorTestHelper.joinBackgroundActivities();
		EditorTestHelper.runEventQueue(3000);
		Exception last = null;
		for (IResource r : this.resourcesToCleanup) {
			try {
				System.out.println("Deleting " + r);
				r.delete(true, null);
			} catch(Exception e) {
				System.out.println("Error deleting " + r);
				e.printStackTrace();
				last = e;
			}
		}

		if(last!=null) throw last;

		resourcesToCleanup.clear();
		
		super.tearDown();
	}

	abstract protected IProject getProject();

	abstract void setUpSeamProjects();

	abstract void assertProjectsAreCreated();

	abstract void assertNewActionFilesAreCreatedSuccessfully(AdaptableRegistry data);
	abstract void assertNewFormFilesAreCreatedSuccessfully(AdaptableRegistry data);
	abstract void assertNewConversationFilesAreCreatedSuccessfully(AdaptableRegistry data);
	abstract void assertNewEntityFilesAreCreatedSuccessfully(AdaptableRegistry data);

	protected void assertResourceIsCreatedAndHasNoProblems(IResource resource, String path) {
		assertNotNull("Resource isn't created: " + path, resource);
		assertTrue("Resource isn't created: " + path, resource.exists());

		int maxSevarityMarkersCount = -1;
		try {
			maxSevarityMarkersCount = resource.findMaxProblemSeverity(IMarker.PROBLEM, false, IResource.DEPTH_INFINITE);
		} catch (CoreException e) {
			JUnitUtils.fail(e.getMessage(), e);
		}
		assertFalse("At least one problem marker exists on resource: " + path, (maxSevarityMarkersCount >= 0));
	}

	
	protected ISeamProject loadSeamProject(IProject project) throws CoreException {
		ISeamProject seamProject = SeamCorePlugin.getSeamProject(project, true);
		assertNotNull("Seam project for " + project.getName() + " is null", seamProject);
		EditorTestHelper.joinBackgroundActivities();
		return seamProject;
	}

	protected void setUpSeamProject(IProject project) {
		IScopeContext projectScope = new ProjectScope(project);
		IEclipsePreferences prefs = projectScope.getNode(SeamCorePlugin.PLUGIN_ID);
		assertNotNull("An error occured while getting the preferences for project: " + project.getName(), prefs);

		prefs.put(ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME, 
				getSeamRTName());
		
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			JUnitUtils.fail(e.getMessage(), e);
		}
	}
	
	protected IFacetedProject createSeamWarProject(String name) throws CoreException {
		final IFacetedProject fproj = createSeamProject(name, createSeamDataModel("war"));
		
		final IProject proj = fproj.getProject();

		assertNotNull(proj);
		assertTrue(proj.exists());

		assertTrue(proj.getWorkspace().getRoot().getProject(proj.getName() + "-test").exists());
		IProject testProject = proj.getWorkspace().getRoot().getProject(proj.getName() + "-test");
		this.addResourceToCleanup(testProject);
		this.addResourceToCleanup(proj);		

		return fproj;
	}
	
	protected IDataModel createSeamDataModel(String deployType) {
		IDataModel config = (IDataModel) new SeamFacetInstallDataModelProvider().create();
		config.setStringProperty(ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME, getSeamRTName());
		config.setBooleanProperty(ISeamFacetDataModelProperties.DB_ALREADY_EXISTS, true);
		config.setBooleanProperty(ISeamFacetDataModelProperties.RECREATE_TABLES_AND_DATA_ON_DEPLOY, false);
		config.setStringProperty(ISeamFacetDataModelProperties.JBOSS_AS_DEPLOY_AS, deployType);
		config.setStringProperty(ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_NAME, "org.session.beans");
		config.setStringProperty(ISeamFacetDataModelProperties.ENTITY_BEAN_PACKAGE_NAME, "org.entity.beans");
		config.setStringProperty(ISeamFacetDataModelProperties.TEST_CASES_PACKAGE_NAME, "org.test.beans");
		config.setStringProperty(ISeamFacetDataModelProperties.SEAM_CONNECTION_PROFILE, "noop-connection");
		config.setProperty(ISeamFacetDataModelProperties.JDBC_DRIVER_JAR_PATH, new String[] { "noop-driver.jar" });
		return config;
	}

	protected IFacetedProject createSeamProject(String baseProjectName, IDataModel config) throws CoreException {
		final IFacetedProject fproj = ProjectFacetsManager.create(baseProjectName, null,
				null);
	
		installDependentFacets(fproj);
//		new SeamFacetPreInstallDelegate().execute(fproj.getProject(), getSeamFacetVersion(), config, null);
		fproj.installProjectFacet(getSeamFacetVersion(getSeamRTName()), config, null);
		
		SeamProjectsSet seamProjectsSet = new SeamProjectsSet(fproj.getProject());
		assertTrue(seamProjectsSet.getActionFolder().exists());
		assertTrue(seamProjectsSet.getModelFolder().exists());
		
		return fproj;
	}
	
	protected void installDependentFacets(final IFacetedProject fproj) throws CoreException {
		fproj.installProjectFacet(javaVersion, null, null);
		fproj.installProjectFacet(dynamicWebVersion, null, null);
		fproj.installProjectFacet(javaFacesVersion, null, null);
	}

	protected IProjectFacetVersion getSeamFacetVersion(String seamRTName) {
		assertTrue("Wrong SEAM run-time name is specified: " + seamRTName, 
				(SEAM_1_2.equals(seamRTName) || SEAM_2_0.equals(seamRTName)));
		if (SEAM_1_2.equals(seamRTName)) {
			return seamFacet.getVersion("1.2");
		} else if (SEAM_2_0.equals(seamRTName)) {
			return seamFacet.getVersion("2.0");
		}
		return null;
	}

	protected final void addResourceToCleanup(final IResource resource) {
		this.resourcesToCleanup.add(resource);
	}

	protected IFacetedProject createSeamEarProject(String name) throws CoreException {
		final IFacetedProject fproj = createSeamProject(name, createSeamDataModel("ear"));
		
		final IProject proj = fproj.getProject();
		assertNotNull(proj);
		
		IProject testProject = proj.getWorkspace().getRoot().getProject(proj.getName() + "-test");
		IProject ejbProject = proj.getWorkspace().getRoot().getProject(proj.getName() + "-ejb");
		IProject earProject = proj.getWorkspace().getRoot().getProject(proj.getName() + "-ear");
		
		this.resourcesToCleanup.add(proj);
		this.resourcesToCleanup.add(testProject);
		this.resourcesToCleanup.add(ejbProject);
		this.resourcesToCleanup.add(earProject);

		assertTrue(proj.exists());
		assertTrue(testProject.exists());
		assertTrue(ejbProject.exists());
		assertTrue(earProject.exists());
		
		return fproj;
	}

	
	protected File getSeamHomeFolder(String seamRTName) {
		File seamHome = null;
		if (SEAM_1_2.equals(seamRTName)) {
			seamHome =  new File(System.getProperty(SEAM_1_2_HOME));
			
		} else if (SEAM_2_0.equals(seamRTName)) {
			seamHome = new File(System.getProperty(SEAM_2_0_HOME));
		}
		
		return seamHome;
	}

	protected SeamVersion getSeamRTVersion(String seamRTName) {
		if (SEAM_1_2.equals(seamRTName)) {
			return SeamVersion.SEAM_1_2;
		} else if (SEAM_2_0.equals(seamRTName)) {
			return SeamVersion.SEAM_2_0;
		}
		return null;
	}


	abstract protected String getSeamRTName() ;

	protected String getPackagePath(String packageName) {
		return (packageName == null ? "" : packageName.replace('.', '/'));
	}
	
	protected String getSessionBeanPackageName(IEclipsePreferences seamFacetPrefs) {
		return seamFacetPrefs.get(IParameter.SESSION_BEAN_PACKAGE_NAME, "");
	}

	protected String getEntityBeanPackageName(IEclipsePreferences seamFacetPrefs) {
		return seamFacetPrefs.get(IParameter.ENTITY_BEAN_PACKAGE_NAME, "");
	}

	protected String getTestCasesPackageName(IEclipsePreferences seamFacetPrefs) {
		return seamFacetPrefs.get(IParameter.TEST_CASES_PACKAGE_NAME, "");
	}

	/**
	 * Test Seam Action for http://jira.jboss.com/jira/browse/JBIDE-2004
	 */
	public void testNewSeamActionOperation() {
		try { EditorTestHelper.joinBackgroundActivities(); } 
		catch (Exception e) { JUnitUtils.fail(e.getMessage(), e); }

		assertProjectsAreCreated();
	
		setUpSeamProjects();

		AdaptableRegistry registry = new AdaptableRegistry();
		
		registry.createData();
		registry.fillDataDefaults(SEAM_ACTION_COMPONENT_NAME, getProject().getName());
		performOperation(CREATE_SEAM_ACTION, registry);
		
		try {
			EditorTestHelper.joinBackgroundActivities();
		} catch (Exception e) {
			JUnitUtils.fail(e.getMessage(), e);
		}

		assertNewActionFilesAreCreatedSuccessfully(registry);
	}
	
	/**
	 * Test Seam Form for http://jira.jboss.com/jira/browse/JBIDE-2004
	 */
	public void testNewSeamFormOperation() {
		try {
			EditorTestHelper.joinBackgroundActivities();
		} catch (Exception e) {
			JUnitUtils.fail(e.getMessage(), e);
		}
		assertProjectsAreCreated();
		
		setUpSeamProjects();

		AdaptableRegistry registry = new AdaptableRegistry();
		registry.createData();
		registry.fillDataDefaults(SEAM_FORM_COMPONENT_NAME, getProject().getName());
		performOperation(CREATE_SEAM_FORM, registry);
		try {
			EditorTestHelper.joinBackgroundActivities();
		} catch (Exception e) {
			JUnitUtils.fail(e.getMessage(), e);
		}

		assertNewFormFilesAreCreatedSuccessfully(registry);
	}
	/**
	 * Test Seam Action for http://jira.jboss.com/jira/browse/JBIDE-2004
	 */
	public void testNewSeamConversationOperation() {
		try {
			EditorTestHelper.joinBackgroundActivities();
		} catch (Exception e) {
			JUnitUtils.fail(e.getMessage(), e);
		}
		assertProjectsAreCreated();
		
		setUpSeamProjects();

		AdaptableRegistry registry = new AdaptableRegistry();
		registry.createData();
		registry.fillDataDefaults(SEAM_CONVERSATION_COMPONENT_NAME, getProject().getName());
		performOperation(CREATE_SEAM_CONVERSATION, registry);
		try {
			EditorTestHelper.joinBackgroundActivities();
		} catch (Exception e) {
			JUnitUtils.fail(e.getMessage(), e);
		}

		assertNewConversationFilesAreCreatedSuccessfully(registry);
	}
	
	/**
	 * Test Seam Action for http://jira.jboss.com/jira/browse/JBIDE-2004
	 */
	public void testNewSeamEntityOperation() {
		try {
			EditorTestHelper.joinBackgroundActivities();
		} catch (Exception e) {
			JUnitUtils.fail(e.getMessage(), e);
		}
		
		assertProjectsAreCreated();
		
		setUpSeamProjects();

		AdaptableRegistry registry = new AdaptableRegistry() {
			protected void fillDataDefaults(String componentName, String projectName) {
				super.fillDataDefaults(componentName, projectName);
				setDefaultValue(IParameter.SEAM_PACKAGE_NAME, getEntityBeanPackageName(getSeamFacetPreferences(projectName)));
			}

		};
		registry.createData();
		registry.fillDataDefaults(SEAM_ENTITY_COMPONENT_NAME, getProject().getName());
		performOperation(CREATE_SEAM_ENTITY, registry);
		try {
			EditorTestHelper.joinBackgroundActivities();
		} catch (Exception e) {
			JUnitUtils.fail(e.getMessage(), e);
		}

		assertNewEntityFilesAreCreatedSuccessfully(registry);
	}

	protected IEclipsePreferences getSeamFacetPreferences(String selectedProject) {
		if(selectedProject!=null && selectedProject.length()>0) {
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(selectedProject);
			if(project!=null) {
				return SeamCorePlugin.getSeamPreferences(project);
			}
		}

		return null;
	}

	protected String getDefaultPackageName(String selectedProject) {
		String packageName = "";
		if(selectedProject!=null && selectedProject.length()>0) {
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(selectedProject);
			if(project!=null) {
				IEclipsePreferences seamFacetPrefs = SeamCorePlugin.getSeamPreferences(project);
				packageName = getDefaultPackageName(seamFacetPrefs);
			}
		}

		return packageName;
	}
	
	protected String getDefaultPackageName(IEclipsePreferences seamFacetPrefs) {
		return seamFacetPrefs.get(ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_NAME, "");
	}

	private void performOperation(final IUndoableOperation operation, final AdaptableRegistry data) {
		
		// TODO lock only current project, not entire workspace
		try {
			wb.getActiveWorkbenchWindow().run(false, false, new WorkspaceModifyOperation(){
				@Override
				protected void execute(IProgressMonitor monitor)
						throws CoreException, InvocationTargetException,
						InterruptedException {
					IStatus result;
					IOperationHistory operationHistory = wb.getOperationSupport().getOperationHistory();
					IUndoContext undoContext = wb.getOperationSupport().getUndoContext();
					operation.addContext(undoContext);
					try {
						result = operationHistory.execute(operation, monitor, data);
					} catch (ExecutionException e) {
						JUnitUtils.fail(e.getMessage(), e);
					}
				}
			});
		} catch (InvocationTargetException e) {
			JUnitUtils.fail(e.getMessage(), e);
		} catch (InterruptedException e) {
			JUnitUtils.fail(e.getMessage(), e);
		}
		return;
	}

	class AdaptableRegistry implements IAdaptable {
		Map<String,IFieldEditor> editorRegistry;

		AdaptableRegistry () {
			editorRegistry = new HashMap<String,IFieldEditor>();
		}
		
		public Object getAdapter(Class adapter) {
			if(adapter == Map.class)
				return editorRegistry;
			return null;
		}

		/**
		 * 
		 * @param id
		 * @param editor
		 */
		protected void add(IFieldEditor editor) {
			editorRegistry.put(editor.getName(), editor);
		}
		
		protected void createData() {
			add(SeamWizardFactory.createSeamProjectSelectionFieldEditor(getProject().getName()));
			add(SeamWizardFactory.createSeamComponentNameFieldEditor());
			add(SeamWizardFactory.createSeamLocalInterfaceNameFieldEditor());
			add(SeamWizardFactory.createSeamBeanNameFieldEditor());
			add(SeamWizardFactory.createSeamMethodNameFieldEditor());
			add(SeamWizardFactory.createSeamMasterPageNameFieldEditor());	
			add(SeamWizardFactory.createSeamPageNameFieldEditor());	
			add(SeamWizardFactory.createSeamEntityClasNameFieldEditor());	
			IProject rootSeamProject = SeamWizardUtils.getRootSeamProject(getProject());
			String selectedProject = (rootSeamProject == null) ? "" : rootSeamProject.getName();
			String packageName = getDefaultPackageName(selectedProject);
			add(SeamWizardFactory.createSeamJavaPackageSelectionFieldEditor(packageName));
		}
		
		protected void fillDataDefaults(String componentName, String projectName) {
			String valueU = componentName.substring(0,1).toUpperCase() + componentName.substring(1);
			String valueL = componentName.substring(0,1).toLowerCase() + componentName.substring(1);
			setDefaultValue(IParameter.SEAM_COMPONENT_NAME, valueU); //$NON-NLS-1$
			setDefaultValue(IParameter.SEAM_LOCAL_INTERFACE_NAME, valueU); //$NON-NLS-1$
			setDefaultValue(IParameter.SEAM_BEAN_NAME, valueU+"Bean"); //$NON-NLS-1$
			setDefaultValue(IParameter.SEAM_ENTITY_CLASS_NAME, valueU); //$NON-NLS-1$
			setDefaultValue(IParameter.SEAM_METHOD_NAME, valueL); //$NON-NLS-1$
			setDefaultValue(IParameter.SEAM_MASTER_PAGE_NAME, valueL+"List");
			setDefaultValue(IParameter.SEAM_PAGE_NAME, valueL); //$NON-NLS-1$
			setSeamProjectNameData(projectName);
			setDefaultValue(IParameter.SEAM_PACKAGE_NAME, getDefaultPackageName(projectName));
		}
		
		protected void setDefaultValue(String name, Object value) {
			IFieldEditor editor = editorRegistry.get(name);
			if (editor != null)
				editor.setValue(value);
		}

		protected void setSeamProjectNameData(String projectName) {
			IFieldEditor editor = editorRegistry.get(IParameter.SEAM_PACKAGE_NAME);
			if(editor!=null) {
				editor.setData(IParameter.SEAM_PROJECT_NAME, projectName);
			}
		}
		
		public String getValue(String key) {
			IFieldEditor editor = editorRegistry.get(key);
			return (editor == null ? null : editor.getValueAsString());
		}
	}
}
