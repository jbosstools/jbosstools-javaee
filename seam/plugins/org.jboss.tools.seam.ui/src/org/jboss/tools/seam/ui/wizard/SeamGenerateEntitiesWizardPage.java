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
package org.jboss.tools.seam.ui.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.utils.ProjectUtils;
import org.hibernate.eclipse.launch.HibernateLaunchConstants;
import org.jboss.tools.common.ui.CommonUIMessages;
import org.jboss.tools.common.ui.IValidator;
import org.jboss.tools.common.ui.widget.editor.IFieldEditor;
import org.jboss.tools.common.ui.widget.editor.INamedElement;
import org.jboss.tools.common.ui.widget.field.RadioField;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.SeamProjectsSet;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.seam.internal.core.validation.SeamProjectPropertyValidator;
import org.jboss.tools.seam.ui.SeamUIMessages;
import org.jboss.tools.seam.ui.internal.project.facet.SeamValidatorFactory;

/**
 * @author Alexey Kazakov
 */
public class SeamGenerateEntitiesWizardPage extends WizardPage implements PropertyChangeListener, IAdaptable {

	private IFieldEditor projectEditor;
	private IFieldEditor configEditor;
	private RadioField radios;
	IProject rootSeamProject;

	public SeamGenerateEntitiesWizardPage() {
		super("seam.generate.entities.page", SeamUIMessages.GENERATE_SEAM_ENTITIES_WIZARD_TITLE, null); //$NON-NLS-1$
		setMessage(SeamUIMessages.GENERATE_SEAM_ENTITIES_WIZARD_PAGE_MESSAGE);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		rootSeamProject = SeamWizardUtils.getCurrentSelectedRootSeamProject();
		String projectName = rootSeamProject==null?"":rootSeamProject.getName();
		projectEditor = SeamWizardFactory.createSeamProjectSelectionFieldEditor(projectName);
		projectEditor.addPropertyChangeListener(this);
		if(projectName!=null && projectName.length()>0) {
			Map<String, IStatus> errors = SeamValidatorFactory.SEAM_PROJECT_NAME_VALIDATOR.validate(projectEditor.getValue(), null);
			if(!errors.isEmpty()) {
				IStatus message = errors.get(IValidator.DEFAULT_ERROR);
				if(message.getSeverity()==IStatus.ERROR) {
					setErrorMessage(message.getMessage());
					setPageComplete(false);
				} else {
					setMessage(message.getMessage());
				}
			} else {
				setMessage(null);
			}
		} else {
			setMessage(SeamUIMessages.GENERATE_SEAM_ENTITIES_WIZARD_PAGE_MESSAGE);
			setPageComplete(false);
		}

		Composite top = new GridLayoutComposite(parent);
		Composite projectComposite = new GridLayoutComposite(top, SWT.NONE, 4);

		projectEditor.doFillIntoGrid(projectComposite);

		configEditor = SeamWizardFactory.createHibernateConsoleConfigurationSelectionFieldEditor(getConsoleConfigurationName(projectName));
		configEditor.addPropertyChangeListener(this);
		configEditor.doFillIntoGrid(projectComposite);
		configEditor.setEditable(false);
		configEditor.addPropertyChangeListener(this);

		String config = (String)configEditor.getValue();
		if(config==null || config.length()==0) {
			setMessage(SeamUIMessages.GENERATE_SEAM_ENTITIES_WIZARD_HIBERNATE_CONFIGURATION_MESSAGE);
			setPageComplete(false);
		}

		Composite groupComposite = new GridLayoutComposite(top);
		Group group = new Group(groupComposite, SWT.NONE);
		group.setText(SeamUIMessages.GENERATE_SEAM_ENTITIES_WIZARD_GROUP_LABEL);
		group.setLayout(new GridLayout(1, false));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Composite radioComposite = new GridLayoutComposite(group, SWT.NONE, 2);
		ArrayList<Object> values = new ArrayList<Object>();
		values.add("reverse"); //$NON-NLS-1$
		values.add("existing"); //$NON-NLS-1$
		ArrayList<String> labels = new ArrayList<String>();
		labels.add(SeamUIMessages.GENERATE_SEAM_ENTITIES_WIZARD_REVERSE_ENGINEER_LABEL);
		labels.add(SeamUIMessages.GENERATE_SEAM_ENTITIES_WIZARD_EXISTING_ENTITIES_LABEL);
		radios = new RadioField(radioComposite, labels, values, null, true);
		radios.addPropertyChangeListener(this);

		setControl(top);
		validate();
	}

	private static String getConsoleConfigurationName(String seamWebProjectName) {
		if(seamWebProjectName==null || seamWebProjectName.trim().length()==0) {
			return null;
		}
		String seamProjectName = seamWebProjectName;
		IProject webProject = ResourcesPlugin.getWorkspace().getRoot().getProject(seamWebProjectName);
		if(webProject==null) {
			return null;
		}
		SeamProjectsSet projectSet = SeamProjectsSet.create(webProject);
		if(!projectSet.isWarConfiguration()) {
			IProject ejbProject = projectSet.getEjbProject();
			if(ejbProject==null) {
				return null;
			}
			seamProjectName = ejbProject.getName();
		}
		ConsoleConfiguration[] configs = KnownConfigurations.getInstance().getConfigurations();
		for (int i = 0; i < configs.length; i++) {
			IJavaProject javaProject = ProjectUtils.findJavaProject(configs[i]);
			if(javaProject!=null && javaProject.getProject().getName().equals(seamProjectName)) {
				return configs[i].getName();
			}
		}
		return null;
	}

	public static class GridLayoutComposite extends Composite {

		public GridLayoutComposite(Composite parent, int style, int columnNumber) {
			super(parent, style);
			GridLayout gl = new GridLayout(columnNumber, false);
			setLayout(gl);
			setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		}

		public GridLayoutComposite(Composite parent) {
			this(parent, SWT.NONE, 1);
		}
	}

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		if(ISeamParameter.SEAM_PROJECT_NAME.equals(event.getPropertyName()) &&
				event.getNewValue()!=null &&
				!event.getNewValue().equals(event.getOldValue())) {
			String consoleConfigName = getConsoleConfigurationName(event.getNewValue().toString());
			if(consoleConfigName!=null) {
				configEditor.setValue(consoleConfigName);
			}
		}
		validate();
	}

	protected boolean isProjectSettingsOk() {
		if(rootSeamProject!=null) {
			if(!isValidRuntimeConfigured(rootSeamProject)) {
				return false;
			}
			IEclipsePreferences prefs = SeamCorePlugin.getSeamPreferences(rootSeamProject);
			return SeamProjectPropertyValidator.isFolderPathValid(prefs.get(ISeamFacetDataModelProperties.ENTITY_BEAN_SOURCE_FOLDER, ""), false) &&
				SeamProjectPropertyValidator.isFolderPathValid(prefs.get(ISeamFacetDataModelProperties.SESSION_BEAN_SOURCE_FOLDER, ""), false) &&
				SeamProjectPropertyValidator.isFolderPathValid(prefs.get(ISeamFacetDataModelProperties.WEB_CONTENTS_FOLDER, ""), false) &&
				(ISeamFacetDataModelProperties.DEPLOY_AS_WAR.equals(prefs.get(ISeamFacetDataModelProperties.JBOSS_AS_DEPLOY_AS, ISeamFacetDataModelProperties.DEPLOY_AS_WAR).trim()) || SeamProjectPropertyValidator.isProjectNameValid(prefs.get(ISeamFacetDataModelProperties.SEAM_EJB_PROJECT, ""), false));
		}
		return true;
	}

	/**
	 * @param project
	 */
	protected boolean isValidRuntimeConfigured(IProject project) {
		Map<String, IStatus> errors;
		String seamRt = SeamCorePlugin.getSeamPreferences(project).get(ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME,""); //$NON-NLS-1$
		errors = SeamValidatorFactory.SEAM_RUNTIME_VALIDATOR.validate(seamRt, null);
		if(!errors.isEmpty()) {
			setErrorMessage(errors.get(IValidator.DEFAULT_ERROR).getMessage());
			setPageComplete(false);
			return false;
		}
		return true;
	}

	private void validate() {
		String projectName = projectEditor.getValue().toString();
		Map<String, IStatus> errors = SeamValidatorFactory.SEAM_PROJECT_NAME_VALIDATOR.validate(projectName, null);

		if(!errors.isEmpty() || !isProjectSettingsOk()) {
			IStatus errorMessage = errors.get(IValidator.DEFAULT_ERROR);
			if(errorMessage==null) {
				setErrorMessage(SeamUIMessages.VALIDATOR_INVALID_SETTINGS);
				setPageComplete(false);
			} else {
				if(errorMessage.getSeverity()==IStatus.ERROR) {
					setErrorMessage(errorMessage.getMessage());
					setPageComplete(false);
				} else {
					setMessage(errorMessage.getMessage());
				}
			}
			return;
		}
		String config = (String)configEditor.getValue();
		if(config==null || config.length()==0) {
			setErrorMessage(SeamUIMessages.GENERATE_SEAM_ENTITIES_WIZARD_HIBERNATE_CONFIGURATION_ERROR);
			setPageComplete(false);
			return;
		}
		
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		SeamProjectsSet seamProjectsSet = SeamProjectsSet.create(project);
		IContainer viewsFolder = seamProjectsSet.getViewsFolder();
		if (viewsFolder != null){
			if (!viewsFolder.getFolder(new Path("layout")).exists()){//$NON-NLS-1$
				setErrorMessage(CommonUIMessages.bind(CommonUIMessages.VALIDATOR_FACTORY_FOLDER_DOES_NOT_EXIST, 
						viewsFolder.getName() + "/layout"));//$NON-NLS-1$
				setPageComplete(false);
				return;
			}
		}

		setErrorMessage(null);
		setMessage(null);
		setPageComplete(true);
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		if(adapter == Map.class) {
			Map<String, INamedElement> values = new HashMap<String, INamedElement>();
			values.put(projectEditor.getName(), new NamedElementImpl(projectEditor.getName(), projectEditor.getValueAsString()));
			values.put(configEditor.getName(), new NamedElementImpl(configEditor.getName(), configEditor.getValueAsString()));
			String mode = radios.getValue().toString();
			String value = "reverse".equals(mode) ? "true" : "false"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			values.put(HibernateLaunchConstants.ATTR_REVERSE_ENGINEER, new NamedElementImpl("reverse", value)); //$NON-NLS-1$
			IWizardPage page2 = getWizard().getPage(SeamGenerateEntitiesTablesWizardPage.pageName);
			if (page2 instanceof SeamGenerateEntitiesTablesWizardPage){
				SeamGenerateEntitiesTablesWizardPage page = (SeamGenerateEntitiesTablesWizardPage)page2;
				String filters = page.getFilters();
				if (filters.length() > 0) values.put(HibernateLaunchConstants.ATTR_REVENG_TABLES, new NamedElementImpl("filters", filters)); //$NON-NLS-1$
			}
			return values;
		}
		return null;
	}

	class NamedElementImpl implements INamedElement {

		private String name;
		private Object value;

		public NamedElementImpl(String name, Object value) {
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public Object getValue() {
			return value;
		}

		public String getValueAsString() {
			return value.toString();
		}

		public void setValue(Object newValue) {
			this.value = newValue;
		}

		public void setValueAsString(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return getValueAsString();
		}
	}

	@Override
	public boolean canFlipToNextPage() {		
		return "reverse".equals(radios.getValue()) && (getErrorMessage() == null);		//$NON-NLS-1$ 
	}

	public String getConsoleCongigurationName(){
		return configEditor.getValueAsString();
	}
}