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
package org.jboss.tools.struts.ui.preferences;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.jboss.tools.common.ui.preferences.SeverityPreferencePage;
import org.jboss.tools.common.ui.preferences.SeverityConfigurationBlock.SectionDescription;
import org.jboss.tools.struts.StrutsModelPlugin;
import org.jboss.tools.struts.validation.StrutsCoreValidator;

/**
 * @author Viacheslav Kabanovich
 */
public class StrutsConfigValidatorPreferencePage extends SeverityPreferencePage {

	public static final String PREF_ID = StrutsCoreValidator.PREFERENCE_PAGE_ID;
	public static final String PROP_ID = "org.jboss.tools.struts.ui.propertyPages.StrutsValidatorPreferencePage"; //$NON-NLS-1$

	public StrutsConfigValidatorPreferencePage() {
		setPreferenceStore(StrutsModelPlugin.getDefault().getPreferenceStore());
		setTitle(StrutsConfigPreferencesMessages.PREFERENCE_PAGE_STRUTS_CORE_VALIDATOR);
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
		fConfigurationBlock = new StrutsConfigConfigurationBlock(getNewStatusChangedListener(), getProject(), container);

		super.createControl(parent);
	}
	
	@Override
	protected SectionDescription[] getAllSections() {
		return StrutsConfigConfigurationBlock.ALL_SECTIONS;
	}
}