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

package org.jboss.tools.seam.ui.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.internal.ui.preferences.PropertyAndPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.jboss.tools.seam.core.SeamCorePlugin;

/**
 * @author Viacheslav Kabanovich
 */
public class SeamValidatorPreferencePage extends PropertyAndPreferencePage {
	public static final String PREF_ID = "org.jboss.tools.seam.ui.preferences.SeamValidatorPreferencePage"; //$NON-NLS-1$
	public static final String PROP_ID = "org.jboss.tools.seam.ui.propertyPages.SeamValidatorPreferencePage"; //$NON-NLS-1$

	private SeamValidatorConfigurationBlock fConfigurationBlock;

	public SeamValidatorPreferencePage() {
		setPreferenceStore(SeamCorePlugin.getDefault().getPreferenceStore());
		setTitle(SeamPreferencesMessages.SEAM_VALIDATOR_PREFERENCE_PAGE_SEAM_VALIDATOR);
	}

	@Override
	protected Control createPreferenceContent(Composite composite) {
		return fConfigurationBlock.createContents(composite);
	}

	@Override
	protected String getPreferencePageID() {
		return PREF_ID;
	}

	@Override
	protected String getPropertyPageID() {
		return PROP_ID;
	}

	@Override
	protected boolean hasProjectSpecificOptions(IProject project) {
		return fConfigurationBlock.hasProjectSpecificOptions(project);
	}

	@Override
	public void createControl(Composite parent) {
		IWorkbenchPreferenceContainer container = (IWorkbenchPreferenceContainer) getContainer();
		fConfigurationBlock = new SeamValidatorConfigurationBlock(getNewStatusChangedListener(), getProject(), container);

		super.createControl(parent);
		if (isProjectPreferencePage()) {
			// help goes here
		} else {
			// help goes here
		}
	}

	/*
	 * @see org.eclipse.jface.dialogs.DialogPage#dispose()
	 */
	@Override
	public void dispose() {
		if (fConfigurationBlock != null) {
			fConfigurationBlock.dispose();
		}
		super.dispose();
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.preferences.PropertyAndPreferencePage#enableProjectSpecificSettings(boolean)
	 */
	@Override
	protected void enableProjectSpecificSettings(boolean useProjectSpecificSettings) {
		super.enableProjectSpecificSettings(useProjectSpecificSettings);
		if (fConfigurationBlock != null) {
			fConfigurationBlock.useProjectSpecificSettings(useProjectSpecificSettings);
		}
	}

	/*
	 * @see org.eclipse.jface.preference.IPreferencePage#performDefaults()
	 */
	@Override
	protected void performDefaults() {
		super.performDefaults();
		if (fConfigurationBlock != null) {
			fConfigurationBlock.performDefaults();
		}
	}

	/*
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	@Override
	public boolean performOk() {
		if (fConfigurationBlock != null && !fConfigurationBlock.performOk()) {
			return false;
		}	
		return super.performOk();
	}

	/*
	 * @see org.eclipse.jface.preference.IPreferencePage#performApply()
	 */
	@Override
	public void performApply() {
		if (fConfigurationBlock != null) {
			fConfigurationBlock.performApply();
		}
	}
}