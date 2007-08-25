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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.datatools.connectivity.ConnectionProfileException;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.datatools.connectivity.db.generic.IDBConnectionProfileConstants;
import org.eclipse.datatools.connectivity.db.generic.ui.NewConnectionProfileWizard;
import org.eclipse.datatools.connectivity.ui.dse.dialogs.ProfileSelectionComposite;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jst.j2ee.project.facet.IJ2EEModuleFacetInstallDataModelProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.internal.dialogs.PropertyDialog;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.DataModelEvent;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelListener;
import org.eclipse.wst.common.project.facet.ui.AbstractFacetWizardPage;
import org.eclipse.wst.common.project.facet.ui.IFacetWizardPage;
import org.eclipse.wst.web.ui.internal.wizards.NewProjectDataModelFacetWizard;
import org.hibernate.eclipse.console.utils.DriverClassHelpers;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.project.facet.SeamFacetPreference;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.seam.ui.widget.editor.ButtonFieldEditor;
import org.jboss.tools.seam.ui.widget.editor.CompositeEditor;
import org.jboss.tools.seam.ui.widget.editor.IFieldEditor;
import org.jboss.tools.seam.ui.widget.editor.IFieldEditorFactory;
import org.jboss.tools.seam.ui.widget.editor.ITaggedFieldEditor;
import org.jboss.tools.seam.ui.wizard.SeamFormWizard;

/**
 * @author eskimo
 * 
 */
public class SeamInstallWizardPage extends AbstractFacetWizardPage implements
		IFacetWizardPage, IDataModelListener {
	
	public static final String PAGE_DESCRIPTION = "Configure Seam Facet Settings";
	/**
	 *
	 */
	boolean seamHomeRequiresSave = SeamFacetPreference.getStringPreference(SeamFacetPreference.SEAM_HOME_FOLDER).equals("");
	/**
	 * 
	 */
	DriverClassHelpers HIBERNATE_HELPER = new DriverClassHelpers();

	/**
	 * 
	 */
	IDataModel model = null;

	/**
	 * 
	 */
	DataModelValidatorDelegate validatorDelegate;

	IFieldEditor jBossSeamHomeEditor = IFieldEditorFactory.INSTANCE
			.createBrowseFolderEditor(
					ISeamFacetDataModelProperties.JBOSS_SEAM_HOME,
					"JBoss Seam Home Folder:",
					SeamFacetPreference
							.getStringPreference(SeamFacetPreference.SEAM_HOME_FOLDER));
	IFieldEditor jBossAsDeployAsEditor = IFieldEditorFactory.INSTANCE
			.createComboEditor(
					ISeamFacetDataModelProperties.JBOSS_AS_DEPLOY_AS,
					"Deploy as:", Arrays.asList(new String[] { "war", "ear" }),
					"war", false);

	String lastCreatedCPName = "";

	// Database group
	IFieldEditor connProfileSelEditor = IFieldEditorFactory.INSTANCE
			.createComboWithTwoButtons(
					ISeamFacetDataModelProperties.SEAM_CONNECTION_PROFILE,
					"Connection profile:", getProfileNameList(), SeamCorePlugin.getDefault().getPluginPreferences().getString(
							SeamFacetPreference.SEAM_DEFAULT_CONNECTION_PROFILE), true,
					new EditConnectionProfileAction(),
					new NewConnectionProfileAction(),
					ValidatorFactory.NO_ERRORS_VALIDATOR);
	IFieldEditor jBossAsDbTypeEditor = IFieldEditorFactory.INSTANCE
			.createComboEditor(ISeamFacetDataModelProperties.DB_TYPE,
					"Database Type:", Arrays.asList(HIBERNATE_HELPER
							.getDialectNames()), "HSQL", true);
	IFieldEditor jBossHibernateDialectEditor = IFieldEditorFactory.INSTANCE
			.createUneditableTextEditor(
					ISeamFacetDataModelProperties.HIBERNATE_DIALECT,
					"Hibernate Dialect:", HIBERNATE_HELPER
							.getDialectClass("HSQL"));

	ITaggedFieldEditor jdbcDriverClassname = IFieldEditorFactory.INSTANCE
			.createComboEditor(
					ISeamFacetDataModelProperties.JDBC_DRIVER_CLASS_NAME,
					"JDBC Driver Class for Your Database:", Arrays
							.asList(HIBERNATE_HELPER
									.getDriverClasses(HIBERNATE_HELPER
											.getDialectClass("HSQL"))),
					HIBERNATE_HELPER.getDriverClasses(HIBERNATE_HELPER
							.getDialectClass("HSQL"))[0]);
	ITaggedFieldEditor jdbcUrlForDb = IFieldEditorFactory.INSTANCE
			.createComboEditor(ISeamFacetDataModelProperties.JDBC_URL_FOR_DB,
					"JDBC Url for Your Database:",
					Arrays.asList(HIBERNATE_HELPER
							.getConnectionURLS(HIBERNATE_HELPER
									.getDriverClasses(HIBERNATE_HELPER
											.getDialectClass("HSQL"))[0])),
					HIBERNATE_HELPER.getConnectionURLS(HIBERNATE_HELPER
							.getDriverClasses(HIBERNATE_HELPER
									.getDialectClass("HSQL"))[0])[0].replace(
							"<", "").replace(">", ""));
	IFieldEditor dbUserName = IFieldEditorFactory.INSTANCE.createTextEditor(
			ISeamFacetDataModelProperties.DB_USER_NAME, "Database User Name:",
			"username");
	IFieldEditor dbUserPassword = IFieldEditorFactory.INSTANCE
			.createTextEditor(ISeamFacetDataModelProperties.DB_USERP_PASSWORD,
					"User Password:", "password");
	IFieldEditor dbSchemaName = IFieldEditorFactory.INSTANCE.createTextEditor(
			ISeamFacetDataModelProperties.DB_SCHEMA_NAME,
			"Database Schema Name:", "");
	IFieldEditor dbCatalogName = IFieldEditorFactory.INSTANCE.createTextEditor(
			ISeamFacetDataModelProperties.DB_CATALOG_NAME,
			"Database Catalog Name:", "");
	IFieldEditor dbTablesExists = IFieldEditorFactory.INSTANCE
			.createCheckboxEditor(
					ISeamFacetDataModelProperties.DB_ALREADY_EXISTS,
					"DB Tables already exists in database:", false);
	IFieldEditor recreateTablesOnDeploy = IFieldEditorFactory.INSTANCE
			.createCheckboxEditor(
					ISeamFacetDataModelProperties.RECREATE_TABLES_AND_DATA_ON_DEPLOY,
					"Recreate database tables and data on deploy:", false);
	IFieldEditor pathToJdbcDriverJar = IFieldEditorFactory.INSTANCE
			.createBrowseFileEditor(
					ISeamFacetDataModelProperties.JDBC_DRIVER_JAR_PATH,
					"JDBC Driver jar:", "");

	// Code generation group
	IFieldEditor sessionBeanPkgNameditor = IFieldEditorFactory.INSTANCE
			.createTextEditor(
					ISeamFacetDataModelProperties.SESION_BEAN_PACKAGE_NAME,
					"Session Bean Package Name:", "");
	IFieldEditor entityBeanPkgNameditor = IFieldEditorFactory.INSTANCE
			.createTextEditor(
					ISeamFacetDataModelProperties.ENTITY_BEAN_PACKAGE_NAME,
					"Entity Bean Package Name:",
					"com.mydomain.projectname.entity");
	IFieldEditor testsPkgNameditor = IFieldEditorFactory.INSTANCE
			.createTextEditor(
					ISeamFacetDataModelProperties.TEST_CASES_PACKAGE_NAME,
					"Test Package Name:",
					"com.mydomain.projectname.test");

	/**
	 * 
	 */
	public SeamInstallWizardPage() {
		super("Seam Facet");
		setTitle("Seam Facet");
		setImageDescriptor(ImageDescriptor.createFromFile(SeamFormWizard.class,
				"SeamWebProjectWizBan.png"));
		setDescription(PAGE_DESCRIPTION);
	}

	/**
	 * 
	 */
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
		sync = new DataModelSynchronizer(model);
		model.addListener(this);
	}

	/**
	 * Finish has been pressed. 
	 */
	public void transferStateToConfig() {
		if( seamHomeRequiresSave) {
			SeamCorePlugin.getDefault().getPluginPreferences().setValue(
				SeamFacetPreference.SEAM_HOME_FOLDER, jBossSeamHomeEditor.getValueAsString());
		}
		if("".equals(SeamCorePlugin.getDefault().getPluginPreferences().getString((
				SeamFacetPreference.SEAM_DEFAULT_CONNECTION_PROFILE)))) {
			SeamCorePlugin.getDefault().getPluginPreferences().setValue(
				SeamFacetPreference.SEAM_DEFAULT_CONNECTION_PROFILE, connProfileSelEditor.getValueAsString());	
		}
	}
		

	/**
	 * Registers editor in data synchronizer and put SWT controls for it at
	 * wizard page.
	 * @param editor
	 * @param parent
	 * @param columns
	 */
	protected void registerEditor(IFieldEditor editor, Composite parent,
			int columns) {
		sync.register(editor);
		editor.doFillIntoGrid(parent);
	}

	/**
	 * Creates Seam Facet Wizard Page contents
	 */
	public void createControl(Composite parent) {
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
		generalGroup.setText("General");
		gridLayout = new GridLayout(3, false);

		generalGroup.setLayout(gridLayout);
		registerEditor(jBossSeamHomeEditor, generalGroup, 3);
		registerEditor(jBossAsDeployAsEditor, generalGroup, 3);

		gd = new GridData();
		gd.horizontalSpan = 1;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = false;

		Group databaseGroup = new Group(root, SWT.NONE);
		databaseGroup.setLayoutData(gd);
		databaseGroup.setText("Database");
		gridLayout = new GridLayout(4, false);
		databaseGroup.setLayout(gridLayout);
		registerEditor(jBossAsDbTypeEditor, databaseGroup, 4);
		registerEditor(jBossHibernateDialectEditor, databaseGroup, 4);
		registerEditor(connProfileSelEditor, databaseGroup, 4);
		registerEditor(dbSchemaName, databaseGroup, 4);
		registerEditor(dbCatalogName, databaseGroup, 4);
		registerEditor(dbTablesExists, databaseGroup, 4);
		registerEditor(recreateTablesOnDeploy, databaseGroup, 4);
		// registerEditor(pathToJdbcDriverJar,databaseGroup, 4);

		Group generationGroup = new Group(root, SWT.NONE);
		gd = new GridData();
		gd.horizontalSpan = 1;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = false;

		generationGroup.setLayoutData(gd);
		generationGroup.setText("Code Generation");
		gridLayout = new GridLayout(3, false);
		generationGroup.setLayout(gridLayout);
		registerEditor(sessionBeanPkgNameditor, generationGroup, 3);
		registerEditor(entityBeanPkgNameditor, generationGroup, 3);
		registerEditor(testsPkgNameditor, generationGroup, 3);

		setControl(root);
		NewProjectDataModelFacetWizard wizard = (NewProjectDataModelFacetWizard) getWizard();
		
		IDataModel model = wizard.getDataModel();

		if(validatorDelegate==null) {
			validatorDelegate = new DataModelValidatorDelegate(this.model, this);
			validatorDelegate.addValidatorForProperty(
					jBossSeamHomeEditor.getName(),
					ValidatorFactory.JBOSS_SEAM_HOME_FOLDER_VALIDATOR);
			validatorDelegate
					.addValidatorForProperty(connProfileSelEditor.getName(),
							ValidatorFactory.CONNECTION_PROFILE_IS_NOT_SELECTED);
			validatorDelegate
				.addValidatorForProperty(testsPkgNameditor.getName(),
						 new PackageNameValidator(
								 testsPkgNameditor.getName(), "tests"));
			validatorDelegate
				.addValidatorForProperty(entityBeanPkgNameditor.getName(),
						 new PackageNameValidator(
								 entityBeanPkgNameditor.getName(), "entity beans"));
			validatorDelegate
				.addValidatorForProperty(sessionBeanPkgNameditor.getName(),
						 new PackageNameValidator(
								 sessionBeanPkgNameditor.getName(), "session beans"));			
		} 
		
		jBossAsDbTypeEditor.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				jBossHibernateDialectEditor.setValue(HIBERNATE_HELPER.getDialectClass(evt.getNewValue().toString()));
			}
		});
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
		}
		if (event.getPropertyName().equals(
				ISeamFacetDataModelProperties.DB_TYPE)) {

		}
	}

	public static final String GENERIC_JDBC_PROVIDER_ID 
		= "org.eclipse.datatools.connectivity.db.generic.connectionProfile";
	
	/*
	 * 
	 */
	private List<String> getProfileNameList() {
		IConnectionProfile[] profiles = ProfileManager.getInstance()
				.getProfileByProviderID(GENERIC_JDBC_PROVIDER_ID);
		List<String> names = new ArrayList<String>();
		for (IConnectionProfile connectionProfile : profiles) {
			names.add(connectionProfile.getName());
		}
		return names;
	}

	/**
	 * 
	 */
	public class EditConnectionProfileAction extends
			ButtonFieldEditor.ButtonPressedAction {

		/**
		 * @param label
		 */
		public EditConnectionProfileAction() {
			super("Edit...");
		}

		/**
		 * 
		 */
		@Override
		public void run() {
			IConnectionProfile selectedProfile = ProfileManager.getInstance()
					.getProfileByName(getFieldEditor().getValue().toString());
			String oldName = getFieldEditor().getValue().toString();

			if (selectedProfile == null)
				return;
			PropertyDialog
					.createDialogOn(
							Display.getCurrent().getActiveShell(),
							"org.eclipse.datatools.connectivity.db.generic.profileProperties",
							selectedProfile).open();

			if (!oldName.equals(selectedProfile.getName())) {
				getFieldEditor().setValue(selectedProfile.getName());
				((ITaggedFieldEditor) ((CompositeEditor) connProfileSelEditor)
						.getEditors().get(1)).setTags(getProfileNameList()
						.toArray(new String[0]));
				oldName = selectedProfile.getName();
			}
		}
	};

	/**
	 * Handler for ButtonFieldEditor that shows Property Editor dialog for
	 * selected ConnectionProfile
	 * 
	 * @author eskimo
	 * 
	 */
	public class NewConnectionProfileAction extends
			ButtonFieldEditor.ButtonPressedAction {
		/**
		 * @param label
		 */
		public NewConnectionProfileAction() {
			super("New...");
		}

		@Override
		public void run() {
			NewConnectionProfileWizard wizard = new NewConnectionProfileWizard() {
				public boolean performFinish() {
					// create profile only
					try {
						ProfileManager.getInstance().createProfile(
								getProfileName() == null ? ""
										: getProfileName(),
								getProfileDescription() == null ? ""
										: getProfileDescription(),
								mProviderID,
								getProfileProperties(),
								mProfilePage.getRepository() == null ? ""
										: mProfilePage.getRepository()
												.getName(), false);
						lastCreatedCPName = getProfileName();
					} catch (ConnectionProfileException e) {
						SeamCorePlugin.getPluginLog().logError(e);
					}

					return true;
				}
			};
			wizard
					.initProviderID(IDBConnectionProfileConstants.CONNECTION_PROFILE_ID);
			WizardDialog wizardDialog = new WizardDialog(Display.getCurrent()
					.getActiveShell(), wizard);
			wizardDialog.open();

			if (wizardDialog.getReturnCode() != WizardDialog.CANCEL) {
				getFieldEditor().setValue(lastCreatedCPName);
				((ITaggedFieldEditor) ((CompositeEditor) connProfileSelEditor)
						.getEditors().get(1)).setTags(getProfileNameList()
						.toArray(new String[0]));
			}
		}
	}

	/**
	 * It is overridden to fill Code Generation group with the default package
	 * names
	 */
	@Override
	public void setVisible(boolean visible) {
		sessionBeanPkgNameditor
			.setValue("org.domain."
				+ model.getProperty(IFacetDataModelProperties.FACET_PROJECT_NAME)
				+ ".session");
		entityBeanPkgNameditor
			.setValue("org.domain."
				+ model.getProperty(IFacetDataModelProperties.FACET_PROJECT_NAME)
				+ ".entity");
		testsPkgNameditor
			.setValue("org.domain."
				+ model.getProperty(IFacetDataModelProperties.FACET_PROJECT_NAME)
				+ ".test");
		if(visible) {
			String message = validatorDelegate.getFirstValidationError();
			this.setPageComplete(message==null);
			this.setMessage(message);
		}
		super.setVisible(visible);
	};
	
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
		public Map<String, String> validate(Object value, Object context) {
			IStatus status = JavaConventions.validatePackageName(
				value.toString(),
				CompilerOptions.VERSION_1_5,
				CompilerOptions.VERSION_1_5);
			if(!status.isOK()) {
				return ValidatorFactory.createErrormessage(fieldName, "Package name for " + targetName  + " is not valid" );
			}
			return ValidatorFactory.NO_ERRORS;
		}
	}
}
