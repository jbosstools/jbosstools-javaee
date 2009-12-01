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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.common.ui.CommonUIPlugin;
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

		IFieldEditor cdiSupportCheckBox = IFieldEditorFactory.INSTANCE.createCheckboxEditor(
				CDIPreferencesMessages.CDI_SETTINGS_PREFERENCE_PAGE_CDI_SUPPORT, CDIPreferencesMessages.CDI_SETTINGS_PREFERENCE_PAGE_CDI_SUPPORT, isCDIEnabled(project));
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

		validate();

		return root;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	@Override
	protected void performDefaults() {
		getEditor(CDIPreferencesMessages.CDI_SETTINGS_PREFERENCE_PAGE_CDI_SUPPORT).setValue(isCDIEnabled(project));
		validate();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	@Override
	public boolean performOk() {
		if(isCDIEnabled()) {
			addCDISupport(project);
		} else {
			removeCDISupport(project);
		}
		return true;
	}

	private void addCDISupport(IProject project) {
		if(project==null) {
			return;
		}
		try {
			EclipseUtil.addNatureToProject(project,	CDICoreNature.NATURE_ID);
		} catch (CoreException e) {
			CommonUIPlugin.getDefault().logError(e);
		}
	}

	private void removeCDISupport(IProject project) {
		try {
			EclipseUtil.removeNatureFromProject(project, CDICoreNature.NATURE_ID);
		} catch (CoreException e) {
			CommonUIPlugin.getDefault().logError(e);
		}
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
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.common.ui.preferences.SettingsPage#validate()
	 */
	@Override
	protected void validate() {
	}
}