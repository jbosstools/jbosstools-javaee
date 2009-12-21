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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.common.ui.preferences.SeverityPreferencePage;

/**
 * @author Alexey Kazakov
 */
public class CDIValidatorPreferencePage extends SeverityPreferencePage {

	public static final String PREF_ID = "org.jboss.tools.cdi.ui.preferences.CDIValidatorPreferencePage"; //$NON-NLS-1$
	public static final String PROP_ID = "org.jboss.tools.cdi.ui.propertyPages.CDIValidatorPreferencePage"; //$NON-NLS-1$

	public CDIValidatorPreferencePage() {
		setPreferenceStore(CDICorePlugin.getDefault().getPreferenceStore());
		setTitle(CDIPreferencesMessages.CDI_VALIDATOR_PREFERENCE_PAGE_CDI_VALIDATOR);
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

	@Override
	public void createControl(Composite parent) {
		IWorkbenchPreferenceContainer container = (IWorkbenchPreferenceContainer) getContainer();
		fConfigurationBlock = new CDIConfigurationBlock(getNewStatusChangedListener(), getProject(), container);

		super.createControl(parent);
	}
}