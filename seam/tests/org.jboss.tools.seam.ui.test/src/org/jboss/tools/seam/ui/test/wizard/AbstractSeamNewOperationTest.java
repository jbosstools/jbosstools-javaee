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
import java.util.HashMap;
import java.util.Map;

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
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.project.facet.SeamRuntime;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeManager;
import org.jboss.tools.seam.core.project.facet.SeamVersion;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
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
	protected static final String SEAM_2_0_HOME = "jbosstools.test.seam.2.0.0.home";
	protected static final String SEAM_HOME_DEFAULT = "F:/jbdevstudio-ga/jboss-eap/seam";
	
	private static final String SEAM_ACTION_COMPONENT_NAME = "TestAction";
	private static final String SEAM_FORM_COMPONENT_NAME = "TestForm";
	private static final String SEAM_CONVERSATION_COMPONENT_NAME = "TestConversation";
	private static final String SEAM_ENTITY_COMPONENT_NAME = "TestEntity";
	
	private static final IUndoableOperation CREATE_SEAM_ACTION = new SeamActionCreateOperation();
	private static final IUndoableOperation CREATE_SEAM_FORM = new SeamFormCreateOperation();
	private static final IUndoableOperation CREATE_SEAM_CONVERSATION = new SeamConversationCreateOperation();
	private static final IUndoableOperation CREATE_SEAM_ENTITY = new SeamEntityCreateOperation();

	
	public AbstractSeamNewOperationTest() {
	}
	
	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
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

		IMarker[] markers = null;
		try {
			markers = resource.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
		} catch (CoreException e) {
			JUnitUtils.fail(e.getMessage(), e);
		}
		assertFalse("At least one problem markar exists on resource: " + path, (markers != null && markers.length > 0));
	}

	
	protected ISeamProject loadSeamProject(IProject project) throws CoreException {
		ISeamProject seamProject = SeamCorePlugin.getSeamProject(project, true);
		assertNotNull("Seam project for " + project.getName() + " is null", seamProject);
		EditorTestHelper.joinBackgroundActivities();
		return seamProject;
	}

	protected void setUpSeamProject(IProject project, String seamRTName) {
		File folder = getSeamHomeFolder(seamRTName);
		assertNotNull("An error occured while getting the SEAM HOME folder for: " + seamRTName, folder);
		
		SeamRuntimeManager.getInstance().addRuntime(seamRTName, folder.getAbsolutePath(), getSeamRTVersion(seamRTName), true);
		SeamRuntime sr = SeamRuntimeManager.getInstance().findRuntimeByName(seamRTName);
		assertNotNull("An error occured while getting the SEAM RUNTIME for: " + seamRTName, sr);

		IScopeContext projectScope = new ProjectScope(project);
		IEclipsePreferences prefs = projectScope.getNode(SeamCorePlugin.PLUGIN_ID);
		assertNotNull("An error occured while getting the preferences for project: " + project.getName(), prefs);

		prefs.put(ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME, 
				seamRTName);
		
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			JUnitUtils.fail(e.getMessage(), e);
		}
	}
	
	protected File getSeamHomeFolder(String seamRTName) {
		if (SEAM_1_2.equals(seamRTName)) {
			return new File(System.getProperty(SEAM_1_2_HOME, SEAM_HOME_DEFAULT));
		} else if (SEAM_2_0.equals(seamRTName)) {
			return new File(System.getProperty(SEAM_2_0_HOME, SEAM_HOME_DEFAULT));
		}
		return null;
	}

	protected SeamVersion getSeamRTVersion(String seamRTName) {
		if (SEAM_1_2.equals(seamRTName)) {
			return SeamVersion.SEAM_1_2;
		} else if (SEAM_2_0.equals(seamRTName)) {
			return SeamVersion.SEAM_2_0;
		}
		return null;
	}

	
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

		AdaptableRegistry registry = new AdaptableRegistry();
		registry.createData();
		registry.fillDataDefaults(SEAM_ENTITY_COMPONENT_NAME, getProject().getName());
		performOperation(CREATE_SEAM_ENTITY, registry);
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
