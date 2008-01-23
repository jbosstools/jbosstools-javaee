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
package org.jboss.tools.seam.ui.test.preferences;

import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.jboss.tools.seam.core.project.facet.SeamRuntime;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeManager;
import org.jboss.tools.seam.ui.preferences.SeamPreferencePage;
import org.jboss.tools.seam.ui.preferences.SeamValidatorPreferencePage;
import org.jboss.tools.test.util.WorkbenchUtils;

import junit.framework.TestCase;

/**
 * @author eskimo
 *
 */

public class SeamPreferencesPageTest extends TestCase {


	/**
	 * Test that preference page is showed up without errors
	 */
	public void testShowSeamPreferencePage() {
		
		PreferenceDialog prefDialog = 
			WorkbenchUtils.createPreferenceDialog(
					SeamPreferencePage.SEAM_PREFERENCES_ID);

		try {
			prefDialog.setBlockOnOpen(false);
			prefDialog.open();
			
			Object selectedPage = prefDialog.getSelectedPage();
			assertTrue("Selected page is not an instance of SeamPreferencePage", selectedPage instanceof SeamPreferencePage);
		} finally {
			prefDialog.close();
		}
	}
	
	/**
	 * Test that preference page is showed up without errors
	 */
	public void testShowSeamValidationPreferencePage() {
		
		PreferenceDialog prefDialog = 
			WorkbenchUtils.createPreferenceDialog(
					SeamValidatorPreferencePage.PREF_ID);

		try {
			prefDialog.setBlockOnOpen(false);
			prefDialog.open();
			
			Object selectedPage = prefDialog.getSelectedPage();
			assertTrue("Selected page is not an instance of SeamValidatorPreferencePage", selectedPage instanceof SeamValidatorPreferencePage);
		} finally {
			prefDialog.close();
		}
	}
	
	public void testJiraJbide1490 () {
		SeamRuntime[] seamRts = SeamRuntimeManager.getInstance().getRuntimes();
		
		for (SeamRuntime seamRuntime : seamRts) {
			SeamRuntimeManager.getInstance().removeRuntime(seamRuntime);
		}

		PreferenceDialog prefDialog = 
			WorkbenchUtils.createPreferenceDialog(
					SeamPreferencePage.SEAM_PREFERENCES_ID);

		try {
			prefDialog.setBlockOnOpen(false);
			prefDialog.open();
			SeamPreferencePage selectedPage = (SeamPreferencePage)prefDialog.getSelectedPage();
			selectedPage.performOk();
		} finally {
			prefDialog.close();
		}
		
	}
	
}
