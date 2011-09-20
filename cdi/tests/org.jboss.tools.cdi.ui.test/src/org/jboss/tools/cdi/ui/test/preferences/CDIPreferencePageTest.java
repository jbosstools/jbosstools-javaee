/******************************************************************************* 
 * Copyright (c) 2009-2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.ui.test.preferences;

import org.jboss.tools.cdi.ui.preferences.CDIPreferencePage;
import org.jboss.tools.cdi.ui.preferences.CDISettingsPreferencePage;
import org.jboss.tools.cdi.ui.preferences.CDIValidatorPreferencePage;
import org.jboss.tools.test.util.PreferencePageAbstractTest;

public class CDIPreferencePageTest extends PreferencePageAbstractTest{

	public void testCDIPreferencePageIsCreated() {
		createPreferenceDialog(CDIPreferencePage.ID);
		assertTrue("Selected page is not an instance of CDIPreferencePage", createPreferencePage(CDISettingsPreferencePage.ID, CDIPreferencePage.class));
	}

	public void testCDIValidatorPreferencePageIsCreated() {
		assertTrue("Selected page is not an instance of CDIPreferencePage", createPreferencePage(CDIValidatorPreferencePage.PREF_ID, CDIValidatorPreferencePage.class));
	}
}