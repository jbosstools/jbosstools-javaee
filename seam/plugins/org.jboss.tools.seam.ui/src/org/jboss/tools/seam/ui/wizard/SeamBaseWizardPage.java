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
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.ui.IValidator;
import org.jboss.tools.common.ui.widget.editor.CompositeEditor;
import org.jboss.tools.common.ui.widget.editor.IFieldEditor;
import org.jboss.tools.common.ui.widget.editor.LabelFieldEditor;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.SeamProjectsSet;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.seam.internal.core.validation.SeamProjectPropertyValidator;
import org.jboss.tools.seam.ui.SeamGuiPlugin;
import org.jboss.tools.seam.ui.SeamUIMessages;
import org.jboss.tools.seam.ui.internal.project.facet.SeamValidatorFactory;

/**
 * @author eskimo
 *
 */
public abstract class SeamBaseWizardPage extends WizardPage implements IAdaptable, PropertyChangeListener {

	protected final IStructuredSelection initialSelection;
	protected IProject rootSeamProject;

	/**
	 * 
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public SeamBaseWizardPage(String pageName, String title,
			ImageDescriptor titleImage, IStructuredSelection initialSelection) {
		super(pageName, title, titleImage);
		this.initialSelection = initialSelection;
		createEditors();
	}

	/**
	 * @param pageName
	 */
	protected SeamBaseWizardPage(String pageName, IStructuredSelection initSelection) {
		super(pageName);
		this.initialSelection = initSelection;
		createEditors();
	}

	protected void createEditors() {
		addEditors(SeamWizardFactory.createBaseFormFieldEditors(SeamWizardUtils.getRootSeamProjectName(initialSelection)));
		rootSeamProject = SeamWizardUtils.getRootSeamProject(initialSelection);
		String selectedProject = (rootSeamProject == null) ? "" : rootSeamProject.getName();
		String packageName = getDefaultPackageName(selectedProject);
		addEditor(SeamWizardFactory.createSeamJavaPackageSelectionFieldEditor(packageName));
		setSeamProjectNameData(selectedProject);
	}

	Map<String,IFieldEditor> editorRegistry = new HashMap<String,IFieldEditor>();

	List<IFieldEditor> editorOrder = new ArrayList<IFieldEditor>();

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		setControl(new GridLayoutComposite(parent));

		if (!"".equals(editorRegistry.get(ISeamParameter.SEAM_PROJECT_NAME).getValue())){ //$NON-NLS-1$
			Map<String, IStatus> errors = SeamValidatorFactory.SEAM_PROJECT_NAME_VALIDATOR.validate(
					getEditor(ISeamParameter.SEAM_PROJECT_NAME).getValue(), null);

			if(!errors.isEmpty()) {
				setErrorMessage(errors.get(IValidator.DEFAULT_ERROR).getMessage());
				getEditor(ISeamParameter.SEAM_BEAN_NAME).setEnabled(false);
			} else if(isWar()) {
				getEditor(ISeamParameter.SEAM_BEAN_NAME).setEnabled(false);	
				LabelFieldEditor label = (LabelFieldEditor)((CompositeEditor)getEditor(ISeamParameter.SEAM_LOCAL_INTERFACE_NAME)).getEditors().get(0);
				label.getLabelControl().setText(SeamUIMessages.SEAM_BASE_WIZARD_PAGE_POJO_CLASS_NAME);
			} else {
				getEditor(ISeamParameter.SEAM_BEAN_NAME).setEnabled(true);
			}
		} else {
			getEditor(ISeamParameter.SEAM_BEAN_NAME).setEnabled(false);
			if(getEditor(ISeamParameter.SEAM_PACKAGE_NAME)!=null) {
				getEditor(ISeamParameter.SEAM_PACKAGE_NAME).setEnabled(false);
			}
		}
		String selectdProject = getEditor(ISeamParameter.SEAM_PROJECT_NAME).getValueAsString();

		if(selectdProject!=null && !"".equals(selectdProject) && isValidProjectSelected()) {
			isValidRuntimeConfigured(getSelectedProject());
		}
		setPageComplete(false);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
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
	public void addEditor(IFieldEditor editor) {
		editorRegistry.put(editor.getName(), editor);
		editorOrder.add(editor);
		editor.addPropertyChangeListener(this);
	}

	/**
	 * 
	 * @param id
	 * @param editor
	 */
	public void addEditors(IFieldEditor[] editors) {
		for (IFieldEditor fieldEditor : editors) {
			addEditor(fieldEditor);
		}
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public IFieldEditor getEditor(String name) {
		return editorRegistry.get(name);
	}

	public void setDefaultValue(String name, Object value) {
		IFieldEditor editor = getEditor(name);
		editor.removePropertyChangeListener(this);
		editor.setValue(value);
		editor.addPropertyChangeListener(this);
	}

	/**
	 * @author eskimo
	 */
	public class GridLayoutComposite extends Composite {

		public GridLayoutComposite(Composite parent, int style) {
			super(parent, style);
			int columnNumber = 1;
			for (IFieldEditor fieldEditor : editorOrder) {
				if(fieldEditor.getNumberOfControls()>columnNumber)
					columnNumber=fieldEditor.getNumberOfControls();
			}
			GridLayout gl = new GridLayout(columnNumber,false);
			gl.verticalSpacing = 5;
			gl.marginTop = 3;
			gl.marginLeft = 3;
			gl.marginRight = 3;
			setLayout(gl);
			for (IFieldEditor fieldEditor2 : editorOrder) {
				fieldEditor2.doFillIntoGrid(this);
			}
		}

		public GridLayoutComposite(Composite parent) {
			this(parent, SWT.NONE);
		}
	}

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		if(ISeamParameter.SEAM_PROJECT_NAME.equals(event.getPropertyName())) {
			rootSeamProject = SeamWizardUtils.getRootSeamProject(getSelectedProject());
		}
		doFillDefaults(event);
		doValidate(event);
	}

	/**
	 * 
	 */
	protected void doValidate(PropertyChangeEvent event) {
		if(!isValidProjectSelected()) return;

		IProject project = getSelectedProject();
		boolean isWar = isWar();
		getEditor(ISeamParameter.SEAM_BEAN_NAME).setEnabled(!isWar);
		IFieldEditor packageEditor = getEditor(ISeamParameter.SEAM_PACKAGE_NAME);
		if(packageEditor!=null) {
			packageEditor.setEnabled(true);
		}

		if(!isValidRuntimeConfigured(project)) return;

		LabelFieldEditor label = (LabelFieldEditor)((CompositeEditor)getEditor(ISeamParameter.SEAM_LOCAL_INTERFACE_NAME)).getEditors().get(0);
		label.getLabelControl().setText(isWar?SeamUIMessages.SEAM_BASE_WIZARD_PAGE_POJO_CLASS_NAME: SeamUIMessages.SEAM_BASE_WIZARD_PAGE_LOCAL_CLASS_NAME);

		Map<String, IStatus> errors = SeamValidatorFactory.SEAM_COMPONENT_NAME_VALIDATOR.validate(
				editorRegistry.get(ISeamParameter.SEAM_COMPONENT_NAME).getValue(), null);

		if(!errors.isEmpty()) {
			setErrorMessage(NLS.bind(errors.get(IValidator.DEFAULT_ERROR).getMessage(),SeamUIMessages.SEAM_BASE_WIZARD_PAGE_SEAM_COMPONENTS));
			setPageComplete(false);
			return;
		}

		errors = SeamValidatorFactory.SEAM_COMPONENT_NAME_VALIDATOR.validate(
				editorRegistry.get(ISeamParameter.SEAM_LOCAL_INTERFACE_NAME).getValue(), null);

		if(!errors.isEmpty()) {
			setErrorMessage(NLS.bind(errors.get(IValidator.DEFAULT_ERROR).getMessage(),SeamUIMessages.SEAM_BASE_WIZARD_PAGE_LOCAL_INTERFACE));
			setPageComplete(false);
			return;
		}

		if(!isWar) {
			errors = SeamValidatorFactory.SEAM_COMPONENT_NAME_VALIDATOR.validate(
					editorRegistry.get(ISeamParameter.SEAM_BEAN_NAME).getValue(), null);

			if(!errors.isEmpty()) {
				setErrorMessage(NLS.bind(errors.get(IValidator.DEFAULT_ERROR).getMessage(),"Bean")); //$NON-NLS-1$
				setPageComplete(false);
				return;
			}
		}

		IFieldEditor editor = editorRegistry.get(ISeamParameter.SEAM_PACKAGE_NAME);
		if(editor!=null) {
			errors = SeamValidatorFactory.PACKAGE_NAME_VALIDATOR.validate(editor.getValue(), null);
			if(!errors.isEmpty()) {
				setErrorMessage(errors.get(IValidator.DEFAULT_ERROR).getMessage()); //$NON-NLS-1$
				setPageComplete(false);
				return;
			}
		}

		errors = SeamValidatorFactory.SEAM_METHOD_NAME_VALIDATOR.validate(
				editorRegistry.get(ISeamParameter.SEAM_METHOD_NAME).getValue(), new Object[]{"Method",project}); //$NON-NLS-1$

		if(!errors.isEmpty()) {
			setErrorMessage(errors.get(IValidator.DEFAULT_ERROR).getMessage());
			setPageComplete(false);
			return;
		}

		errors = SeamValidatorFactory.FILE_NAME_VALIDATOR.validate(
				editorRegistry.get(ISeamParameter.SEAM_PAGE_NAME).getValue(), new Object[]{"Page",project}); //$NON-NLS-1$

		if(!errors.isEmpty()) {
			setErrorMessage(errors.get(IValidator.DEFAULT_ERROR).getMessage());
			setPageComplete(false);
			return;
		}

		errors = SeamValidatorFactory.SEAM_JAVA_INTEFACE_NAME_CONVENTION_VALIDATOR.validate(
				editorRegistry.get(ISeamParameter.SEAM_LOCAL_INTERFACE_NAME).getValue(), new Object[]{SeamUIMessages.SEAM_BASE_WIZARD_PAGE_LOCAL_INTERFACE,project});

		if(!errors.isEmpty()) {
			setErrorMessage(null);
			setMessage(errors.get(IValidator.DEFAULT_ERROR).getMessage(),IMessageProvider.WARNING);
			setPageComplete(true);
			return;
		}
		
		IJavaProject javaProject = EclipseResourceUtil.getJavaProject(project);
		
		if(javaProject != null){
			try{
				IType component = javaProject.findType((String)editorRegistry.get(ISeamParameter.SEAM_PACKAGE_NAME).getValue()+"."+editorRegistry.get(ISeamParameter.SEAM_LOCAL_INTERFACE_NAME).getValue());
				if(component != null){
					setErrorMessage(null);
					setMessage(SeamUIMessages.POJO_CLASS_ALREADY_EXISTS, IMessageProvider.WARNING);
					setPageComplete(true);
					return;
				}
			}catch(JavaModelException ex){
				SeamGuiPlugin.getPluginLog().logError(ex);
			}
		}
		
		SeamProjectsSet seamPrjSet = new SeamProjectsSet(project);
		IPath webContent = seamPrjSet.getViewsFolder().getFullPath();
		
		IPath page = webContent.append(editorRegistry.get(ISeamParameter.SEAM_PAGE_NAME).getValue()+".xhtml");
		
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(page);
		if(file.exists()){
			setErrorMessage(null);
			setMessage(SeamUIMessages.PAGE_ALREADY_EXISTS, IMessageProvider.WARNING);
			setPageComplete(true);
			return;
		}
		 
		
		setErrorMessage(null);
		setMessage(getDefaultMessageText());
		setPageComplete(true);
	}

	protected boolean isProjectSettingsOk() {
		if(rootSeamProject!=null) {
			IEclipsePreferences prefs = SeamCorePlugin.getSeamPreferences(rootSeamProject);
			return SeamProjectPropertyValidator.isFolderPathValid(prefs.get(ISeamFacetDataModelProperties.SESSION_BEAN_SOURCE_FOLDER, ""), false) &&
				SeamProjectPropertyValidator.isFolderPathValid(prefs.get(ISeamFacetDataModelProperties.WEB_CONTENTS_FOLDER, ""), false) &&
				("false".equals(prefs.get(ISeamFacetDataModelProperties.TEST_CREATING, "false").trim()) || (SeamProjectPropertyValidator.isFolderPathValid(prefs.get(ISeamFacetDataModelProperties.TEST_SOURCE_FOLDER, ""), false) && SeamProjectPropertyValidator.isProjectNameValid(prefs.get(ISeamFacetDataModelProperties.SEAM_TEST_PROJECT, ""), false))) &&
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

	protected boolean isValidProjectSelected() {
		Map<String, IStatus> errors = SeamValidatorFactory.SEAM_PROJECT_NAME_VALIDATOR.validate(
				editorRegistry.get(ISeamParameter.SEAM_PROJECT_NAME).getValue(), null);

		if(!errors.isEmpty() || !isProjectSettingsOk()) {
			IStatus errorStatus = errors.get(IValidator.DEFAULT_ERROR);
			String errorMessage = SeamUIMessages.VALIDATOR_INVALID_SETTINGS;
			if(errorStatus!=null) {
				errorMessage = errorStatus.getMessage();
			}
			setErrorMessage(errorMessage);
			setPageComplete(false);
			IFieldEditor beanEditor = getEditor(ISeamParameter.SEAM_BEAN_NAME);
			if(beanEditor!=null) {
				beanEditor.setEnabled(false);
			}
			IFieldEditor packageEditor = getEditor(ISeamParameter.SEAM_PACKAGE_NAME);
			if(packageEditor!=null) {
				packageEditor.setEnabled(false);
			}
			return false;
		}
		return true;
	}

	/**
	 * 
	 */
	protected void doFillDefaults(PropertyChangeEvent event) {
		if(event.getPropertyName().equals(ISeamParameter.SEAM_COMPONENT_NAME) || event.getPropertyName().equals(ISeamParameter.SEAM_PROJECT_NAME)) {
			String value = getEditor(ISeamParameter.SEAM_COMPONENT_NAME).getValueAsString();
			if(value==null||"".equals(value)) { //$NON-NLS-1$
				setDefaultValue(ISeamParameter.SEAM_COMPONENT_NAME, ""); //$NON-NLS-1$
				setDefaultValue(ISeamParameter.SEAM_LOCAL_INTERFACE_NAME, ""); //$NON-NLS-1$
				setDefaultValue(ISeamParameter.SEAM_BEAN_NAME, ""); //$NON-NLS-1$
				setDefaultValue(ISeamParameter.SEAM_METHOD_NAME, ""); //$NON-NLS-1$
				setDefaultValue(ISeamParameter.SEAM_PAGE_NAME, ""); //$NON-NLS-1$
			} else {
				String valueU = value.substring(0,1).toUpperCase() + value.substring(1);
				setDefaultValue(ISeamParameter.SEAM_LOCAL_INTERFACE_NAME, valueU);
				setDefaultValue(ISeamParameter.SEAM_BEAN_NAME, valueU+"Bean"); //$NON-NLS-1$
				String valueL = value.substring(0,1).toLowerCase() + value.substring(1);
				setDefaultValue(ISeamParameter.SEAM_METHOD_NAME, valueL);
				setDefaultValue(ISeamParameter.SEAM_PAGE_NAME, valueL);
			}
		}
		if(event.getPropertyName().equals(ISeamParameter.SEAM_PROJECT_NAME)&& getEditor(ISeamParameter.SEAM_PACKAGE_NAME)!=null) {
			String selectedProject = event.getNewValue().toString();
			setSeamProjectNameData(selectedProject);
			setDefaultValue(ISeamParameter.SEAM_PACKAGE_NAME, getDefaultPackageName(selectedProject));
		}
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

	protected void setSeamProjectNameData(String projectName) {
		IFieldEditor editor = getEditor(ISeamParameter.SEAM_PACKAGE_NAME);
		if(editor!=null) {
			editor.setData(ISeamParameter.SEAM_PROJECT_NAME, projectName);
		}
	}

	/**
	 * @return
	 */
	public IProject getSelectedProject() {
		String projectName = editorRegistry.get(ISeamParameter.SEAM_PROJECT_NAME).getValueAsString();
		if(projectName!=null && projectName.trim().length()>0) {
			IResource project = ResourcesPlugin.getWorkspace().getRoot().findMember(projectName);
			if(project!=null && project instanceof IProject) {
				return (IProject)project;
			}
		}
		return null;
	}

	public boolean isWar() {
		if(getSelectedProject()==null ||
		SeamCorePlugin.getSeamPreferences(getSelectedProject().getProject())==null) return true;
		return "war".equals(SeamCorePlugin.getSeamPreferences(getSelectedProject().getProject()).get(ISeamFacetDataModelProperties.JBOSS_AS_DEPLOY_AS,"war")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public String getRootSeamProject() {
		return rootSeamProject!=null?rootSeamProject.getName():null;
	}

	public abstract String getDefaultMessageText();
}