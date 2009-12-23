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
package org.jboss.tools.seam.ui.internal.project.facet;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jst.j2ee.project.facet.IJ2EEFacetConstants;
import org.eclipse.jst.j2ee.project.facet.IJ2EEModuleFacetInstallDataModelProperties;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.componentcore.datamodel.FacetInstallDataModelProvider;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetDataModelProperties;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.frameworks.datamodel.DataModelEvent;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelListener;
import org.eclipse.wst.common.frameworks.internal.operations.ProjectCreationDataModelProviderNew;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.IFacetedProject.Action;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;
import org.eclipse.wst.common.project.facet.ui.AbstractFacetWizardPage;
import org.eclipse.wst.common.project.facet.ui.IFacetWizardPage;
import org.eclipse.wst.common.project.facet.ui.ModifyFacetedProjectWizard;
import org.eclipse.wst.web.ui.internal.wizards.NewProjectDataModelFacetWizard;
import org.hibernate.eclipse.console.utils.DriverClassHelpers;
import org.jboss.tools.common.ui.IValidator;
import org.jboss.tools.common.ui.widget.editor.CompositeEditor;
import org.jboss.tools.common.ui.widget.editor.IFieldEditor;
import org.jboss.tools.common.ui.widget.editor.IFieldEditorFactory;
import org.jboss.tools.common.ui.widget.editor.ITaggedFieldEditor;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.SeamUtil;
import org.jboss.tools.seam.core.project.facet.SeamProjectPreferences;
import org.jboss.tools.seam.core.project.facet.SeamRuntime;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeManager;
import org.jboss.tools.seam.core.project.facet.SeamVersion;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.seam.internal.core.project.facet.SeamFacetInstallDataModelProvider;
import org.jboss.tools.seam.ui.SeamUIMessages;
import org.jboss.tools.seam.ui.wizard.SeamFormWizard;
import org.jboss.tools.seam.ui.wizard.SeamProjectWizard;
import org.jboss.tools.seam.ui.wizard.SeamWizardFactory;
import org.jboss.tools.seam.ui.wizard.SeamWizardUtils;

/**
 * @author eskimo
 * 
 */
@SuppressWarnings("restriction")
public class SeamInstallWizardPage extends AbstractFacetWizardPage implements
		IFacetWizardPage, IDataModelListener {

	public static final String PAGE_DESCRIPTION = SeamUIMessages.SEAM_INSTALL_WIZARD_PAGE_CONFIGURE_SEAM_FACET_SETTINGS;

	private static final DriverClassHelpers HIBERNATE_HELPER = new DriverClassHelpers();

	private IDataModel model = null;

	private DataModelValidatorDelegate validatorDelegate;

	private IFieldEditor jBossSeamHomeEditor;

	private IFieldEditor jBossAsDeployAsEditor = IFieldEditorFactory.INSTANCE
			.createRadioEditor(
					ISeamFacetDataModelProperties.JBOSS_AS_DEPLOY_AS,
					SeamUIMessages.SEAM_INSTALL_WIZARD_PAGE_DEPLOY_AS, Arrays
							.asList(new String[] {
									ISeamFacetDataModelProperties.DEPLOY_AS_WAR
											.toUpperCase(),
									ISeamFacetDataModelProperties.DEPLOY_AS_EAR
											.toUpperCase() }),
					Arrays.asList(new Object[] {
							ISeamFacetDataModelProperties.DEPLOY_AS_WAR,
							ISeamFacetDataModelProperties.DEPLOY_AS_EAR }),
					getDeployAsDefaultValue());

	// Database group
	private IFieldEditor connProfileSelEditor;

	//private IFieldEditor jBossHibernateDbTypeEditor ;

	private IFieldEditor dbSchemaName;

	private IFieldEditor dbCatalogName;

	private IFieldEditor dbTablesExists;

	private IFieldEditor recreateTablesOnDeploy;

	private IFieldEditor sessionBeanPkgNameditor = IFieldEditorFactory.INSTANCE
			.createTextEditor(
					ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_NAME,
					SeamUIMessages.SEAM_INSTALL_WIZARD_PAGE_SESSION_BEAN_PACKAGE_NAME,
					""); //$NON-NLS-1$

	private IFieldEditor entityBeanPkgNameditor = IFieldEditorFactory.INSTANCE
			.createTextEditor(
					ISeamFacetDataModelProperties.ENTITY_BEAN_PACKAGE_NAME,
					SeamUIMessages.SEAM_INSTALL_WIZARD_PAGE_ENTITY_BEAN_PACKAGE_NAME,
					"com.mydomain.projectname.entity"); //$NON-NLS-1$

	private IFieldEditor createTestProjectCheckboxeditor = IFieldEditorFactory.INSTANCE
	.createCheckboxEditor(
			ISeamFacetDataModelProperties.TEST_PROJECT_CREATING,
			SeamUIMessages.SEAM_INSTALL_WIZARD_PAGE_CREATE_TEST_PROJECT,
			true);

	private IFieldEditor testsPkgNameditor = IFieldEditorFactory.INSTANCE
			.createTextEditor(
					ISeamFacetDataModelProperties.TEST_CASES_PACKAGE_NAME,
					SeamUIMessages.SEAM_INSTALL_WIZARD_PAGE_TEST_PACKAGE_NAME,
					"com.mydomain.projectname.test"); //$NON-NLS-1$
	
	private Group databaseGroup;

	/**
	 * 
	 */
	public SeamInstallWizardPage() {
		super(SeamUIMessages.SEAM_INSTALL_WIZARD_PAGE_SEAM_FACET);
		setTitle(SeamUIMessages.SEAM_INSTALL_WIZARD_PAGE_SEAM_FACET);
		setImageDescriptor(ImageDescriptor.createFromFile(SeamFormWizard.class,
				"SeamWebProjectWizBan.png")); //$NON-NLS-1$
		setDescription(PAGE_DESCRIPTION);
	}

	/**
	 * @return
	 */
	private String getDefaultDbType() {
		return SeamProjectPreferences
				.getStringPreference(SeamProjectPreferences.HIBERNATE_DEFAULT_DB_TYPE);
	}

	/**
	 * @return
	 */
	private Object getDeployAsDefaultValue() {
		String result = SeamProjectPreferences
				.getStringPreference(SeamProjectPreferences.JBOSS_AS_DEFAULT_DEPLOY_AS);
		if (!isNewProjectWizard()) {
			ISelection sel = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getSelectionService()
					.getSelection();
			IProject project = SeamWizardUtils.getInitialProject(sel);
			if (project == null) {
				SeamCorePlugin
						.getPluginLog()
						.logError(
								"Can't get project name to initialize SeamInstallWizardPage for ModifyFacetedProjectWizard");
				return result;
			}
			IFacetedProject facetedProject;
			try {
				facetedProject = ProjectFacetsManager.create(project);
				if (facetedProject == null) {
					SeamCorePlugin
							.getPluginLog()
							.logError(
									"Can't get faceted project to initialize SeamInstallWizardPage for ModifyFacetedProjectWizard");
					return result;
				}
			} catch (CoreException e) {
				SeamCorePlugin.getPluginLog().logError(e);
				return result;
			}
			IProjectFacetVersion webVersion = facetedProject
					.getProjectFacetVersion(IJ2EEFacetConstants.DYNAMIC_WEB_FACET);
			if (webVersion != null) {
				return ISeamFacetDataModelProperties.DEPLOY_AS_WAR;
			} else {
				return ISeamFacetDataModelProperties.DEPLOY_AS_EAR;
			}
		}
		return result;
	}

	/**
	 * @return
	 */
	private Object getConnectionProfileDefaultValue() {
		String defaultDs = SeamProjectPreferences
				.getStringPreference(SeamProjectPreferences.SEAM_DEFAULT_CONNECTION_PROFILE);
		return getConnectionProfileNameList().contains(defaultDs) ? defaultDs
				: ""; //$NON-NLS-1$
	}

	private static List<String> getConnectionProfileNameList() {
		IConnectionProfile[] profiles = ProfileManager.getInstance()
				.getProfilesByCategory(
						"org.eclipse.datatools.connectivity.db.category"); //$NON-NLS-1$
		List<String> names = new ArrayList<String>();
		for (IConnectionProfile connectionProfile : profiles) {
			names.add(connectionProfile.getName());
		}
		return names;
	}

	private DataModelSynchronizer sync;

	/**
	 * 
	 */
	@Override
	public void setWizard(IWizard newWizard) {
		super.setWizard(newWizard);
	}

	/**
	 * 
	 */
	public void setConfig(Object config) {
		model = (IDataModel) config;
		model.setProperty(ISeamFacetDataModelProperties.HIBERNATE_DIALECT,
				HIBERNATE_HELPER.getDialectClass(getDefaultDbType()));
	}

	/**
	 * 
	 * @return
	 */
	public IDataModel getConfig() {
		return model;
	}

	/**
	 * Finish has been pressed.
	 */
	@SuppressWarnings("deprecation")
	public void transferStateToConfig() {
		String seamRuntimeName = jBossSeamHomeEditor.getValueAsString();
		SeamRuntime seamRuntime = SeamRuntimeManager.getInstance()
				.findRuntimeByName(seamRuntimeName);
		if (seamRuntime != null) {
			SeamRuntimeManager.getInstance().setDefaultRuntime(seamRuntime);
		}
		
		if (connProfileSelEditor != null) {
			SeamCorePlugin.getDefault().getPluginPreferences().setValue(
					SeamProjectPreferences.SEAM_DEFAULT_CONNECTION_PROFILE,
					connProfileSelEditor.getValueAsString());
		} else {
			SeamCorePlugin.getDefault().getPluginPreferences().setValue(
					SeamProjectPreferences.SEAM_DEFAULT_CONNECTION_PROFILE,
					getJpaConnectionProfile());
			model.setStringProperty(ISeamFacetDataModelProperties.SEAM_CONNECTION_PROFILE, getJpaConnectionProfile());
		}
		
		/*if (jBossHibernateDbTypeEditor != null) {
			SeamCorePlugin.getDefault().getPluginPreferences().setValue(
					SeamProjectPreferences.HIBERNATE_DEFAULT_DB_TYPE,
					this.jBossHibernateDbTypeEditor.getValueAsString());
		}*/		

		SeamCorePlugin.getDefault().getPluginPreferences().setValue(
				SeamProjectPreferences.JBOSS_AS_DEFAULT_DEPLOY_AS,
				this.jBossAsDeployAsEditor.getValueAsString());		
	}
	
	/*
	 * Don't want to add jpa as required plugin, so use String constant instead of JptCorePlugin.FACET_ID
	 */
	private IProjectFacetVersion getJpaFacetVersion(){
		IFacetedProjectWorkingCopy facetedProject = ( (ModifyFacetedProjectWizard) getWizard() ).getFacetedProjectWorkingCopy();
		return facetedProject.getProjectFacetVersion(ProjectFacetsManager.getProjectFacet("jpt.jpa"));	//$NON-NLS-1$	
	}
	
	/**
	 * Used to save connection profile from jpa facet.
	 * If not null then used as seam connection profile.
	 */
	private String jpaConnectioProfile;
	
	public void setJpaConnectionProfile(String jpaConnectioProfile){
		this.jpaConnectioProfile = jpaConnectioProfile;
	}
	
	private String getJpaConnectionProfile(){
		if (jpaConnectioProfile != null) return jpaConnectioProfile;
		IProjectFacetVersion jpaVersion = getJpaFacetVersion();	
		if (jpaVersion == null) throw new NullPointerException("Jpa facet version is null");
		try {
			Object config = context.getConfig(jpaVersion, Action.Type.INSTALL, context.getProjectName());
			if (config instanceof IDataModel) {
				/*
				 * Don't want to add jpa as required plugin, so use String constant instead of JpaFacetDataModelProperties.CONNECTION
				 */
				return ((IDataModel)config).getStringProperty("JpaFacetDataModelProperties.CONNECTION"); //$NON-NLS-1$
			}
		} catch (CoreException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		}
		return null;
	}


	/**
	 * Registers editor in data synchronizer and put SWT controls for it at
	 * wizard page.
	 * 
	 * @param editor
	 * @param parent
	 * @param columns
	 */
	protected void registerEditor(IFieldEditor editor, Composite parent,
			int columns) {
		sync.register(editor);
		editor.doFillIntoGrid(parent);
	}

	public List<String> getRuntimeNames(String version) {
		SeamRuntime[] rts = SeamRuntimeManager.getInstance().getRuntimes(
				SeamVersion.parseFromString(version));
		List<String> result = new ArrayList<String>();
		for (SeamRuntime seamRuntime : rts) {
			result.add(seamRuntime.getName());
		}
		return result;
	}

	/**
	 * Creates Seam Facet Wizard Page contents
	 */
	public void createControl(Composite parent) {
		sync = new DataModelSynchronizer(model);
		jBossSeamHomeEditor = SeamWizardFactory
				.createSeamRuntimeSelectionFieldEditor(new SeamVersion[0],
						SeamFacetInstallDataModelProvider
								.getSeamRuntimeDefaultValue(model),
						new NewSeamRuntimeAction());

		initializeDialogUnits(parent);

		Composite root = new Composite(parent, SWT.NONE);
		GridData gd = new GridData();

		gd.horizontalSpan = 1;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = false;

		GridLayout gridLayout = new GridLayout(1, false);
		root.setLayout(gridLayout);
		Group generalGroup = new Group(root, SWT.NONE);
		generalGroup.setLayoutData(gd);
		generalGroup.setText(SeamUIMessages.SEAM_INSTALL_WIZARD_PAGE_GENERAL);
		gridLayout = new GridLayout(3, false);

		generalGroup.setLayout(gridLayout);
		registerEditor(jBossSeamHomeEditor, generalGroup, 3);
		registerEditor(jBossAsDeployAsEditor, generalGroup, 3);

		gd = new GridData();
		gd.horizontalSpan = 1;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = false;

		databaseGroup = new Group(root, SWT.NONE);
		databaseGroup.setLayoutData(gd);
		databaseGroup.setText(SeamUIMessages.SEAM_INSTALL_WIZARD_PAGE_DATABASE);
		gridLayout = new GridLayout(4, false);
		databaseGroup.setLayout(gridLayout);

		createDatabaseGoupControl();

		Group generationGroup = new Group(root, SWT.NONE);
		gd = new GridData();
		gd.horizontalSpan = 1;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = false;

		generationGroup.setLayoutData(gd);
		generationGroup
				.setText(SeamUIMessages.SEAM_INSTALL_WIZARD_PAGE_CODE_GENERATION);
		gridLayout = new GridLayout(3, false);
		generationGroup.setLayout(gridLayout);
		registerEditor(sessionBeanPkgNameditor, generationGroup, 3);
		registerEditor(entityBeanPkgNameditor, generationGroup, 3);
		
		registerEditor(createTestProjectCheckboxeditor, generationGroup, 3);
		createTestProjectCheckboxeditor.addPropertyChangeListener(new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent arg0) {
				Boolean value = (Boolean)createTestProjectCheckboxeditor.getValue();
				testsPkgNameditor.setEnabled(value.booleanValue());
			}
		});
		
		createTestProjectCheckboxeditor.setEnabled(isNewProjectWizard());
		
		registerEditor(testsPkgNameditor, generationGroup, 3);

		setControl(root);

		if (validatorDelegate == null) {
			validatorDelegate = new DataModelValidatorDelegate(this.model, this);
			validatorDelegate.addValidatorForProperty(jBossSeamHomeEditor
					.getName(), SeamValidatorFactory.SEAM_RUNTIME_NAME_VALIDATOR);
			validatorDelegate.addValidatorForProperty(connProfileSelEditor
					.getName(), SeamValidatorFactory.CONNECTION_PROFILE_VALIDATOR);
			validatorDelegate.addValidatorForProperty(testsPkgNameditor
					.getName(), new PackageNameValidator(testsPkgNameditor
					.getName(), "tests")); //$NON-NLS-1$
			validatorDelegate.addValidatorForProperty(entityBeanPkgNameditor
					.getName(), new PackageNameValidator(entityBeanPkgNameditor
					.getName(), "entity beans")); //$NON-NLS-1$
			validatorDelegate.addValidatorForProperty(sessionBeanPkgNameditor
					.getName(), new PackageNameValidator(
					sessionBeanPkgNameditor.getName(), "session beans")); //$NON-NLS-1$
			if (isNewProjectWizard()) {
				validatorDelegate.addValidatorForProperty(
						IFacetDataModelProperties.FACET_PROJECT_NAME,
						new ProjectNamesDuplicationValidator(
								IFacetDataModelProperties.FACET_PROJECT_NAME));
			}
			validatorDelegate.addValidatorForProperty(
					ISeamFacetDataModelProperties.JBOSS_AS_DEPLOY_AS,
					getDeploymentTypeValidator(getWizard()));
		}

		

		Dialog.applyDialogFont(parent);
		initDefaultWizardProperties();
		Object parentDm = model
				.getProperty(FacetInstallDataModelProvider.MASTER_PROJECT_DM);
		if (parentDm != null) {
			((IDataModel) parentDm).addListener(this);
		}
	}

	public void createDatabaseGoupControl() {
		//disposeControls(jBossHibernateDbTypeEditor);
		disposeControls(connProfileSelEditor);
		disposeControls(dbSchemaName);
		disposeControls(dbCatalogName);
		disposeControls(dbTablesExists);
		disposeControls(recreateTablesOnDeploy);
		if (!needToShowConnectionProfile){
			//jBossHibernateDbTypeEditor = null;
			connProfileSelEditor = null;
			dbSchemaName = null;
			dbCatalogName = null;
		} else {
			/*jBossHibernateDbTypeEditor = IFieldEditorFactory.INSTANCE
			.createComboEditor(ISeamFacetDataModelProperties.DB_TYPE,
					SeamUIMessages.SEAM_INSTALL_WIZARD_PAGE_DATABASE_TYPE,
					Arrays.asList(HIBERNATE_HELPER.getDialectNames()),
					getDefaultDbType(), false);*/
			connProfileSelEditor = SeamWizardFactory
			.createConnectionProfileSelectionFieldEditor(
					getConnectionProfileDefaultValue(), new IValidator() {
						public Map<String, IStatus> validate(Object value,
								Object context) {
							SeamInstallWizardPage.this.validate();
							return SeamValidatorFactory.NO_ERRORS;
						}
					});
			dbSchemaName = IFieldEditorFactory.INSTANCE
				.createTextEditor(
						ISeamFacetDataModelProperties.DB_DEFAULT_SCHEMA_NAME,
						SeamUIMessages.SEAM_INSTALL_WIZARD_PAGE_DATABASE_SCHEMA_NAME,
						""); //$NON-NLS-1$
			dbCatalogName = IFieldEditorFactory.INSTANCE
				.createTextEditor(
						ISeamFacetDataModelProperties.DB_DEFAULT_CATALOG_NAME,
						SeamUIMessages.SEAM_INSTALL_WIZARD_PAGE_DATABASE_CATALOG_NAME,
						"");	//$NON-NLS-1$
			
			/*jBossHibernateDbTypeEditor
			.addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					SeamInstallWizardPage.this.model
							.setProperty(
									ISeamFacetDataModelProperties.HIBERNATE_DIALECT,
									HIBERNATE_HELPER.getDialectClass(evt
											.getNewValue().toString()));
				}
			});
			registerEditor(jBossHibernateDbTypeEditor, databaseGroup, 4);*/
			registerEditor(connProfileSelEditor, databaseGroup, 4);
			registerEditor(dbSchemaName, databaseGroup, 4);
			registerEditor(dbCatalogName, databaseGroup, 4);				
		}
		dbTablesExists = IFieldEditorFactory.INSTANCE
		.createCheckboxEditor(
				ISeamFacetDataModelProperties.DB_ALREADY_EXISTS,
				SeamUIMessages.SEAM_INSTALL_WIZARD_PAGE_DB_TABLES_ALREADY_EXISTS,
				false);
		recreateTablesOnDeploy = IFieldEditorFactory.INSTANCE
		.createCheckboxEditor(
				ISeamFacetDataModelProperties.RECREATE_TABLES_AND_DATA_ON_DEPLOY,
				SeamUIMessages.SEAM_INSTALL_WIZARD_PAGE_RECREATE_DATABASE_TABLES_AND_DATA_ON_DEPLOY,
				false);
		registerEditor(dbTablesExists, databaseGroup, 4);
		registerEditor(recreateTablesOnDeploy, databaseGroup, 4);
		databaseGroup.getParent().layout(true);
	}
	
	private void disposeControls(IFieldEditor editor){
		if (editor != null){
			Object[] controls = editor.getEditorControls();
			for (int i = 0; i < controls.length; i++) {
				Control control = (Control) controls[i];
				if (!control.isDisposed()) control.dispose();
			}
			editor.dispose();
		}
	}

	private boolean isNewSeamProjectWizard() {
		return getWizard() == null || getWizard() instanceof SeamProjectWizard;
	}

	private boolean isNewProjectWizard() {
		// ModifyFacetedProjectWizard or NewProjectDataModelFacetWizard
		return getWizard() == null
				|| getWizard() instanceof NewProjectDataModelFacetWizard;
	}

	/**
	 * 
	 */
	public void propertyChanged(DataModelEvent event) {
		if (event.getPropertyName().equals(
				IJ2EEModuleFacetInstallDataModelProperties.CONFIG_FOLDER)) {
			model.setStringProperty(
					ISeamFacetDataModelProperties.WEB_CONTENTS_FOLDER, event
							.getProperty().toString());
		} else if (event.getPropertyName().equals(
				IFacetDataModelProperties.FACET_PROJECT_NAME)) {
			String p = event.getProperty().toString();

			model.setStringProperty(
					ISeamFacetDataModelProperties.SEAM_PROJECT_NAME, p);
			model.setStringProperty(
					ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_NAME,
					getSessionPkgName(p));
			model.setStringProperty(
					ISeamFacetDataModelProperties.ENTITY_BEAN_PACKAGE_NAME,
					getEntityPkgName(p));
			model.setProperty(
					ISeamFacetDataModelProperties.TEST_PROJECT_CREATING,
					createTestProjectCheckboxeditor.getValue());
			model.setStringProperty(
					ISeamFacetDataModelProperties.TEST_CASES_PACKAGE_NAME,
					getTestPkgName(p));
		}
	}
	
	private boolean needToShowConnectionProfile = true;

	/**
	 * It is overridden to fill Code Generation group with the default package
	 * names
	 */
	@Override
	public void setVisible(boolean visible) {

		if (visible) {
			setCodeGenerationProperties();
			setDefaultSeamRuntime();
			boolean jpaFacetAdded = getJpaFacetVersion() != null;
			if (jpaFacetAdded == needToShowConnectionProfile){
				
				needToShowConnectionProfile = !jpaFacetAdded;
				createDatabaseGoupControl();
			}
			//update selected connection profile
			if (!needToShowConnectionProfile){
				model.setStringProperty(ISeamFacetDataModelProperties.SEAM_CONNECTION_PROFILE, getJpaConnectionProfile());
			}
			validate();
		}
		super.setVisible(visible);
	};

	private void initDefaultWizardProperties() {
		setCodeGenerationProperties();
		setDefaultSeamRuntime();
		validate();
	}

	/*
	 * Fills Code Generation group with the default package names.
	 */
	private void setCodeGenerationProperties() {
		String p = (String) model
				.getProperty(ISeamFacetDataModelProperties.SEAM_PROJECT_NAME);
		sessionBeanPkgNameditor.setValue(getSessionPkgName(p));
		entityBeanPkgNameditor.setValue(getEntityPkgName(p));
		testsPkgNameditor.setValue(getTestPkgName(p));
	}

	private String getSessionPkgName(String projectName) {
		return "org.domain." //$NON-NLS-1$
				+ SeamUtil.getSeamPackageName(projectName) + ".session"; //$NON-NLS-1$
	}

	private String getEntityPkgName(String projectName) {
		return "org.domain." //$NON-NLS-1$
				+ SeamUtil.getSeamPackageName(projectName) + ".entity"; //$NON-NLS-1$
	}

	private String getTestPkgName(String projectName) {
		return "org.domain." //$NON-NLS-1$
				+ SeamUtil.getSeamPackageName(projectName) + ".test"; //$NON-NLS-1$
	}

	/*
	 * Sets seam runtime field in default value.
	 */
	private void setDefaultSeamRuntime() {
		ITaggedFieldEditor runtimesField = (ITaggedFieldEditor) ((CompositeEditor) jBossSeamHomeEditor)
				.getEditors().get(1);
		Object oldValue = runtimesField.getValue();
		Object newValue = ""; //$NON-NLS-1$
		List<String> runtimes = getRuntimeNames(model.getProperty(
				IFacetDataModelProperties.FACET_VERSION_STR).toString());
		if (oldValue == null || !runtimes.contains(oldValue)) {
			Object defaultRnt = SeamFacetInstallDataModelProvider
					.getSeamRuntimeDefaultValue(model);
			if (defaultRnt != null && runtimes.contains(defaultRnt)) {
				newValue = defaultRnt;
			} else if (!runtimes.isEmpty()) {
				newValue = runtimes.get(0);
			}
		} else {
			newValue = oldValue;
		}
		runtimesField.setValue(newValue);
		runtimesField.setTags(runtimes.toArray(new String[0]));
	}

	private void validate() {
		final IStatus message = validatorDelegate.getFirstValidationError();
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				if (message == null) {
					setMessage(null);
					setErrorMessage(null);
					setPageComplete(true);
					return;
				}
				if (message.getSeverity() == IStatus.ERROR) {
					setErrorMessage(message.getMessage());
					setPageComplete(false);
					return;
				}
				setErrorMessage(null);
				setMessage(message.getMessage(), DialogPage.WARNING);
				setPageComplete(true);
				return;
			}
		});
	}

	public class PackageNameValidator implements IValidator {

		String fieldName;
		String targetName;

		/**
		 * @param fieldName
		 * @param targetName
		 */
		public PackageNameValidator(String fieldName, String targetName) {
			this.fieldName = fieldName;
			this.targetName = targetName;
		}

		/**
		 * @see IValidator#validate(Object, Object)
		 */
		public Map<String, IStatus> validate(Object value, Object context) {
			IStatus status = JavaConventions.validatePackageName(value.toString(),
					SeamValidatorFactory.DEFAULT_SOURCE_LEVEL,
					SeamValidatorFactory.DEFAULT_COMPLIANCE_LEVEL);
			
			if (((IStatus.ERROR | IStatus.WARNING) & status.getSeverity() ) != 0 ){
				return doPackStatus(status,
					fieldName,
					NLS.bind(SeamUIMessages.SEAM_INSTALL_WIZARD_PAGE_PACKAGE_NAME_NOT_VALID,
					targetName));
			}
			return SeamValidatorFactory.NO_ERRORS;
		}
		
	}

	class ProjectNamesDuplicationValidator implements IValidator {
		String propertyName;

		/**
		 */
		public ProjectNamesDuplicationValidator(String propertyName) {
			this.propertyName = propertyName;
		}

		/**
		 * @see IValidator#validate(Object, Object)
		 */
		public Map<String, IStatus> validate(Object value, Object context) {
			final String projectName = (String) value;
			final String deployAs = model
					.getStringProperty(ISeamFacetDataModelProperties.JBOSS_AS_DEPLOY_AS);

			final String testProjectName = projectName + "-test"; //$NON-NLS-1$
			IStatus status = ProjectCreationDataModelProviderNew
					.validateName(testProjectName);
			
			if (((IStatus.ERROR | IStatus.WARNING) & status.getSeverity() ) != 0 ){
				return doPackStatus(status,
						propertyName,
						NLS.bind(SeamUIMessages.VALIDATOR_FACTORY_TEST_PROJECT_ALREADY_EXISTS,
								testProjectName));
			}

			if (ISeamFacetDataModelProperties.DEPLOY_AS_EAR.equals(deployAs)) {
				final String earProjectName = projectName + "-ear"; //$NON-NLS-1$
				status = ProjectCreationDataModelProviderNew
						.validateName(earProjectName);
				
				if (((IStatus.ERROR | IStatus.WARNING) & status.getSeverity() ) != 0 ){
					return doPackStatus(status,
							propertyName,
							NLS.bind(SeamUIMessages.VALIDATOR_FACTORY_EAR_PROJECT_ALREADY_EXISTS,
									earProjectName));
				}

				final String ejbProjectName = projectName + "-ejb"; //$NON-NLS-1$
				status = ProjectCreationDataModelProviderNew
						.validateName(ejbProjectName);
				
				if (((IStatus.ERROR | IStatus.WARNING) & status.getSeverity() ) != 0 ){
					return doPackStatus(status,
							propertyName,
							NLS.bind(SeamUIMessages.VALIDATOR_FACTORY_EJB_PROJECT_ALREADY_EXISTS,
									ejbProjectName));
				}
			}
			return SeamValidatorFactory.NO_ERRORS;
		}
	}

	IValidator getDeploymentTypeValidator(IWizard wizard) {
		if (wizard instanceof NewProjectDataModelFacetWizard) {
			return new DeploymentTypeValidator(
					ISeamFacetDataModelProperties.JBOSS_AS_DEPLOY_AS,
					((NewProjectDataModelFacetWizard) wizard).getDataModel());
		}
		// return new DeploymentTypeValidator(ISeamFacetDataModelProperties.
		// JBOSS_AS_DEPLOY_AS, model);
		return new IValidator() {
			public Map<String, IStatus> validate(Object value, Object context) {
				return SeamValidatorFactory.NO_ERRORS;
			}
		};
	}

	static class DeploymentTypeValidator implements IValidator {

		String propertyName;

		IDataModel model;

		static final IProjectFacet EJB_FACET = ProjectFacetsManager
				.getProjectFacet(IModuleConstants.JST_EJB_MODULE);

		static final IProjectFacetVersion EJB_30 = EJB_FACET.getVersion("3.0"); //$NON-NLS-1$

		static final IProjectFacet EAR_FACET = ProjectFacetsManager
				.getProjectFacet(IModuleConstants.JST_EAR_MODULE);

		static final IProjectFacetVersion EAR_50 = EAR_FACET.getVersion("5.0"); //$NON-NLS-1$

		/**
		 */
		public DeploymentTypeValidator(String propertyName, IDataModel model) {
			this.propertyName = propertyName;
			this.model = model;
		}

		/**
		 * @see IValidator#validate(Object, Object)
		 */
		public Map<String, IStatus> validate(Object value, Object context) {
			final String deploymentType = value.toString();
			if (!ISeamFacetDataModelProperties.DEPLOY_AS_WAR
					.equals(deploymentType)) {
				Object runtimeName = model
						.getProperty(IFacetProjectCreationDataModelProperties.FACET_RUNTIME);
				if (runtimeName != null) {
					IRuntime rt = RuntimeManager.getRuntime(runtimeName
							.toString());
					if (!rt.supports(EJB_30) || !rt.supports(EAR_50)) {
						return SeamValidatorFactory.createErrormessage(
									propertyName,
									new Status(
											IStatus.ERROR,
											SeamCorePlugin.PLUGIN_ID,
											NLS.bind(
													SeamUIMessages.SEAM_INSTALL_WIZARD_PAGE_CANNOT_USE_SELECTED_DEPLOYMENT6,
													new String[] {
														deploymentType.toUpperCase(),
														runtimeName.toString() })));
					}
				}
			}
			return SeamValidatorFactory.NO_ERRORS;
		}
	}

	private class NewSeamRuntimeAction extends
			SeamWizardFactory.NewSeamRuntimeAction {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.jboss.tools.seam.ui.wizard.SeamWizardFactory.NewSeamRuntimeAction
		 * #getRuntimeSelectionEditor()
		 */
		@Override
		protected IFieldEditor getRuntimeSelectionEditor() {
			return jBossSeamHomeEditor;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.jboss.tools.seam.ui.wizard.SeamWizardFactory.NewSeamRuntimeAction
		 * #getSeamVersions()
		 */
		@Override
		protected SeamVersion[] getSeamVersions() {
			String seamVersion = model.getProperty(
					IFacetDataModelProperties.FACET_VERSION_STR).toString();
			return new SeamVersion[] { SeamVersion.parseFromString(seamVersion) };
		}
	}

	/**
	 * 
	 */
	public void finishPressed() {
		model.removeListener(validatorDelegate);
	};

	/*
	 * test method
	 */
	public String getSessionBeanPkgName() {
		return (String) sessionBeanPkgNameditor.getValue();
	}

	/*
	 * test method
	 */
	public String getEntityBeanPkgName() {
		return (String) entityBeanPkgNameditor.getValue();
	}

	/*
	 * test method
	 */
	public String getTestsPkgName() {
		return (String) testsPkgNameditor.getValue();
	}
	
	private Map<String, IStatus> doPackStatus(IStatus status, String propertyName, String message){
		return SeamValidatorFactory.createErrormessage(
					propertyName,
					new Status(status.getSeverity(), SeamCorePlugin.PLUGIN_ID, message));
	}
}