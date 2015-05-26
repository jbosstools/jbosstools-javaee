/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.batch.ui.internal.preferences;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.jboss.tools.batch.internal.core.validation.BatchValidator;
import org.jboss.tools.batch.ui.BatchUIPlugin;
import org.jboss.tools.common.ui.preferences.SeverityPreferencePage;
import org.jboss.tools.common.ui.preferences.SeverityConfigurationBlock.SectionDescription;

/**
 * @author Viacheslav Kabanovich
 */
public class BatchValidationPreferencePage extends SeverityPreferencePage {

	public static final String PREF_ID = BatchValidator.PREFERENCE_PAGE_ID;
	public static final String PROP_ID = BatchValidator.PROPERTY_PAGE_ID;

	public BatchValidationPreferencePage() {
		setPreferenceStore(BatchUIPlugin.getDefault().getPreferenceStore());
		setTitle(BatchSeverityPreferencesMessages.BATCH_VALIDATION_PREFERENCE_PAGE_BATCH_VALIDATOR);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.preferences.PropertyAndPreferencePage#getPreferencePageID()
	 */
	@Override
	protected String getPreferencePageID() {
		return PREF_ID;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.preferences.PropertyAndPreferencePage#getPropertyPageID()
	 */
	@Override
	protected String getPropertyPageID() {
		return PROP_ID;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		IWorkbenchPreferenceContainer container = (IWorkbenchPreferenceContainer) getContainer();
		fConfigurationBlock = new BatchValidationConfigurationBlock(getNewStatusChangedListener(), getProject(), container);

		super.createControl(parent);
	}
	
	@Override
	protected SectionDescription[] getAllSections() {
		return BatchValidationConfigurationBlock.ALL_SECTIONS;
	}
}