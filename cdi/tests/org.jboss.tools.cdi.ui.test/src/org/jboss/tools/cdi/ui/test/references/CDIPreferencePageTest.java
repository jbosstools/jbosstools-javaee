package org.jboss.tools.cdi.ui.test.references;

import junit.framework.TestCase;

import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.jboss.tools.cdi.ui.preferences.CDIPreferencePage;
import org.jboss.tools.cdi.ui.preferences.CDISettingsPreferencePage;
import org.jboss.tools.cdi.ui.preferences.CDIValidatorPreferencePage;
import org.jboss.tools.test.util.PreferencePageAbstractTest;


public class CDIPreferencePageTest extends PreferencePageAbstractTest{
	

	
	public void testCDIPreferencePageIsCreated() {
		PreferenceDialog prefDialog = createPreferenceDialog(CDIPreferencePage.ID);
		assertTrue("Selected page is not an instance of CDIPreferencePage", isPreferencePageIsCreated(CDISettingsPreferencePage.ID, CDIPreferencePage.class));
		
	}
	
	public void testCDIValidatorPreferencePageIsCreated() {
		PreferenceDialog prefDialog = createPreferenceDialog(CDIValidatorPreferencePage.PREF_ID);
		assertTrue("Selected page is not an instance of CDIPreferencePage", isPreferencePageIsCreated(CDIValidatorPreferencePage.PREF_ID, CDIValidatorPreferencePage.class));
	}
}
