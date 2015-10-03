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
import org.jboss.tools.seam.core.project.facet.SeamRuntime;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeManager;
import org.jboss.tools.seam.ui.preferences.SeamPreferencePage;
import org.jboss.tools.seam.ui.preferences.SeamValidatorPreferencePage;
import org.jboss.tools.test.util.WorkbenchUtils;
import org.jboss.tools.tests.PreferencePageTest;
import org.junit.Test;

/**
 * @author eskimo
 *
 */

public class SeamPreferencesPageTest extends PreferencePageTest {


	/**
	 * Test that preference page is showed up without errors
	 */
	@Test
	public void testShowSeamPreferencePage() {
		doDefaultTest(SeamPreferencePage.SEAM_PREFERENCES_ID, SeamPreferencePage.class);
	}

	/**
	 * Test that preference page is showed up without errors
	 */
	@Test
	public void testShowSeamValidationPreferencePage() {
		doDefaultTest(SeamValidatorPreferencePage.PREF_ID, SeamValidatorPreferencePage.class);
	}

	@Test
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
