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

package org.jboss.tools.jsf.ui.preferences;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.jboss.tools.common.model.ui.preferences.SeverityConfigurationBlock;
import org.jboss.tools.common.model.ui.preferences.SeverityPreferencePage;
import org.jboss.tools.jsf.JSFModelPlugin;

/**
 * @author Viacheslav Kabanovich
 */
public class JSFValidatorPreferencePage extends SeverityPreferencePage {
	public static final String PREF_ID = "org.jboss.tools.jsf.ui.preferences.JSFValidatorPreferencePage"; //$NON-NLS-1$
	public static final String PROP_ID = "org.jboss.tools.jsf.ui.propertyPages.JSFValidatorPreferencePage"; //$NON-NLS-1$

	private JSFValidatorConfigurationBlock fConfigurationBlock;

	public JSFValidatorPreferencePage() {
		setPreferenceStore(JSFModelPlugin.getDefault().getPreferenceStore());
		setTitle(JSFSeverityPreferencesMessages.JSF_VALIDATOR_PREFERENCE_PAGE_JSF_VALIDATOR);
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
	public void createControl(Composite parent) {
		IWorkbenchPreferenceContainer container = (IWorkbenchPreferenceContainer) getContainer();
		fConfigurationBlock = new JSFValidatorConfigurationBlock(getNewStatusChangedListener(), getProject(), container);

		super.createControl(parent);
	}

	@Override
	protected SeverityConfigurationBlock getConfigurationBlock() {
		return fConfigurationBlock;
	}
}