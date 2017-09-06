/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.ui.preferences;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.CDIUtil;
import org.jboss.tools.cdi.ui.CDIUIPlugin;
import org.jboss.tools.common.ui.preferences.SettingsPage;
import org.jboss.tools.common.ui.widget.editor.IFieldEditor;
import org.jboss.tools.common.ui.widget.editor.IFieldEditorFactory;

/**
 * @author Alexey Kazakov
 */
public class CDISettingsPreferencePage extends SettingsPage {

	public static final String ID = "org.jboss.tools.cdi.ui.propertyPages.CDISettingsPreferencePage";

	private IProject project;
	private boolean cdiEnabled;
	private boolean initialState;
	private boolean generateBeansXml;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.PropertyPage#setElement(org.eclipse.core.runtime.IAdaptable)
	 */
	@Override
	public void setElement(IAdaptable element) {
		super.setElement(element);
		project = (IProject) getElement().getAdapter(IProject.class);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite root = new Composite(parent, SWT.NONE);

		GridData gd = new GridData();

		gd.horizontalSpan = 1;
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = false;

		GridLayout gridLayout = new GridLayout(1, false);
		root.setLayout(gridLayout);

		Composite generalGroup = new Composite(root, SWT.NONE);
		generalGroup.setLayoutData(gd);
		gridLayout = new GridLayout(4, false);

		generalGroup.setLayout(gridLayout);

		initialState = isCDIEnabled(project);
		IFieldEditor cdiSupportCheckBox = IFieldEditorFactory.INSTANCE.createCheckboxEditor(
				CDIPreferencesMessages.CDI_SETTINGS_PREFERENCE_PAGE_CDI_SUPPORT, CDIPreferencesMessages.CDI_SETTINGS_PREFERENCE_PAGE_CDI_SUPPORT, initialState);
		cdiSupportCheckBox.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				Object value = evt.getNewValue();
				if (value instanceof Boolean) {
					boolean v = ((Boolean) value).booleanValue();
					setEnabledCDISuport(v);
				}
			}
		});
		cdiEnabled = isCDIEnabled(project);
		registerEditor(cdiSupportCheckBox, generalGroup);

		generateBeansXml = false;

//		IFieldEditor generateBeansXmlCheckBox = IFieldEditorFactory.INSTANCE.createCheckboxEditor(
//				CDIUIMessages.CDI_GENERATE_BEANS_XML, CDIUIMessages.CDI_GENERATE_BEANS_XML, generateBeansXml);
//		generateBeansXmlCheckBox.addPropertyChangeListener(new PropertyChangeListener() {
//			public void propertyChange(PropertyChangeEvent evt) {
//				Object value = evt.getNewValue();
//				if (value instanceof Boolean) {
//					boolean v = ((Boolean) value).booleanValue();
//					generateBeansXml = v;
//				}
//			}
//		});

//		registerEditor(generateBeansXmlCheckBox, generalGroup);

		validate();
		checkGenerateBeansXml();

		return root;
	}

	private void checkGenerateBeansXml() {
//		getEditor(CDIUIMessages.CDI_GENERATE_BEANS_XML).setEnabled(!initialState && cdiEnabled);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	@Override
	protected void performDefaults() {
		getEditor(CDIPreferencesMessages.CDI_SETTINGS_PREFERENCE_PAGE_CDI_SUPPORT).setValue(isCDIEnabled(project));
//		getEditor(CDIUIMessages.CDI_GENERATE_BEANS_XML).setValue(true);
		validate();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	@Override
	public boolean performOk() {
		if(isCDIEnabled()!=initialState) {
			if(isCDIEnabled()) {
				addCDISupport(project);
			} else {
				removeCDISupport(project);
			}
		}
		return true;
	}

	private void addCDISupport(final IProject project) {
		if(project==null) {
			return;
		}
		CDIUtil.enableCDI(project, generateBeansXml, new NullProgressMonitor());
		Job buildJob = new Job("Build CDI project "+project.getName()) {
			
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					project.build(IncrementalProjectBuilder.CLEAN_BUILD, monitor);
				} catch (CoreException e) {
					CDIUIPlugin.getDefault().logError(e);
				}
				return Status.OK_STATUS;
			}
		};
		ISchedulingRule modifyRule = ResourcesPlugin.getWorkspace().getRuleFactory().buildRule();
		buildJob.setRule(modifyRule);
		buildJob.schedule();
	}

	private void removeCDISupport(IProject project) {
		CDIUtil.disableCDI(project);
	}

	private boolean isCDIEnabled(IProject project) {
		return CDICorePlugin.getCDI(project, false)!=null;
	}

	private boolean isCDIEnabled() {
		return cdiEnabled;
	}

	public void setEnabledCDISuport(boolean enabled) {
		cdiEnabled = enabled;
		editorRegistry.get(CDIPreferencesMessages.CDI_SETTINGS_PREFERENCE_PAGE_CDI_SUPPORT).setValue(enabled);
		checkGenerateBeansXml();
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.common.ui.preferences.SettingsPage#validate()
	 */
	@Override
	protected void validate() {
	}
}