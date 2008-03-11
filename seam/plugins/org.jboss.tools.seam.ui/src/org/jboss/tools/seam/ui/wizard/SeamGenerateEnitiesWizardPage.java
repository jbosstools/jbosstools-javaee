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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IJavaProject;
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
import org.jboss.tools.seam.core.SeamProjectsSet;
import org.jboss.tools.seam.ui.SeamUIMessages;
import org.jboss.tools.seam.ui.internal.project.facet.IValidator;
import org.jboss.tools.seam.ui.internal.project.facet.ValidatorFactory;
import org.jboss.tools.seam.ui.widget.editor.IFieldEditor;
import org.jboss.tools.seam.ui.widget.field.RadioField;

/**
 * @author Alexey Kazakov
 */
public class SeamGenerateEnitiesWizardPage extends WizardPage implements PropertyChangeListener, IAdaptable {

	private IFieldEditor projectEditor;
	private IFieldEditor configEditor;
	private RadioField radios;

	public SeamGenerateEnitiesWizardPage() {
		super("seam.generate.entities.page", SeamUIMessages.GENERATE_SEAM_ENTITIES_WIZARD_TITLE, null); //$NON-NLS-1$
		setMessage(SeamUIMessages.GENERATE_SEAM_ENTITIES_WIZARD_PAGE_MESSAGE);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		setPageComplete(true);
		String projectName = SeamWizardUtils.getCurrentSelectedRootSeamProjectName();
		projectEditor = SeamWizardFactory.createSeamProjectSelectionFieldEditor(projectName);
		projectEditor.addPropertyChangeListener(this);
		if(projectName!=null && projectName.length()>0) {
			Map<String, String> errors = ValidatorFactory.SEAM_PROJECT_NAME_VALIDATOR.validate(projectEditor.getValue(), null);
			if(errors.size()>0) {
				setErrorMessage(errors.get(IValidator.DEFAULT_ERROR).toString());
				setPageComplete(false);
			} else {
				setMessage(null);
			}
		} else {
			setMessage(SeamUIMessages.GENERATE_SEAM_ENTITIES_WIZARD_PAGE_MESSAGE);
			setPageComplete(false);
		}

		Composite top = new GridLayoutComposite(parent);
		Composite projectComposite = new GridLayoutComposite(top, SWT.NONE, 3);

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
		if(IParameter.SEAM_PROJECT_NAME.equals(event.getPropertyName()) &&
				event.getNewValue()!=null &&
				!event.getNewValue().equals(event.getOldValue())) {
			String consoleConfigName = getConsoleConfigurationName(event.getNewValue().toString());
			if(consoleConfigName!=null) {
				configEditor.setValue(consoleConfigName);
			}
		}
		validate();
	}

	private void validate() {
		Map<String, String> errors = ValidatorFactory.SEAM_PROJECT_NAME_VALIDATOR.validate(projectEditor.getValue(), null);

		if(errors.size()>0) {
			setErrorMessage(errors.get(IValidator.DEFAULT_ERROR).toString());
			setPageComplete(false);
			return;
		}
		String config = (String)configEditor.getValue();
		if(config==null || config.length()==0) {
			setErrorMessage(SeamUIMessages.GENERATE_SEAM_ENTITIES_WIZARD_HIBERNATE_CONFIGURATION_ERROR);
			setPageComplete(false);
			return;
		}

		setErrorMessage(null);
		setMessage(null);
		setPageComplete(true);
	}

	public Object getAdapter(Class adapter) {
		if(adapter == Map.class) {
			Map<String,String> values = new HashMap<String, String>();
			values.put(projectEditor.getName(), projectEditor.getValueAsString());
			values.put(configEditor.getName(), configEditor.getValueAsString());
			String mode = radios.getValue().toString();
			values.put(HibernateLaunchConstants.ATTR_REVERSE_ENGINEER, ("reverse".equals(mode) ? "true" : "false")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			return values;
		}
		return null;
	}
}