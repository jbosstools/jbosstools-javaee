/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.jsf.ui.preferences;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.jboss.tools.common.ui.preferences.SeverityPreferencePage;
import org.jboss.tools.jsf.JSFModelPlugin;

/**
 * @author Alexey Kazakov
 */
public class JSFValidationPreferencePage extends SeverityPreferencePage {

	public static final String PREF_ID = "org.jboss.tools.jsf.ui.preferences.JSFValidationPreferencePage"; //$NON-NLS-1$
	public static final String PROP_ID = "org.jboss.tools.jsf.ui.propertyPages.JSFValidationPreferencePage"; //$NON-NLS-1$

	public JSFValidationPreferencePage() {
		setPreferenceStore(JSFModelPlugin.getDefault().getPreferenceStore());
		setTitle(JSFSeverityPreferencesMessages.JSF_VALIDATION_PREFERENCE_PAGE_JSF_VALIDATOR);
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
		fConfigurationBlock = new JSFValidationConfigurationBlock(getNewStatusChangedListener(), getProject(), container);

		super.createControl(parent);
	}
}