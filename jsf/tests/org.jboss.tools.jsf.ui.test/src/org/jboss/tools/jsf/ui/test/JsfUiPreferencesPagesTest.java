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
package org.jboss.tools.jsf.ui.test;

import org.jboss.tools.jsf.ui.preferences.JSFCapabilitiesPreferencesPage;
import org.jboss.tools.jsf.ui.preferences.JSFFlowTabbedPreferencesPage;
import org.jboss.tools.jsf.ui.preferences.JSFImplementationsPreferencesPage;
import org.jboss.tools.jsf.ui.preferences.JSFProjectPreferencesPage;
import org.jboss.tools.jsf.ui.preferences.JSFStudioPreferencesPage;
import org.jboss.tools.tests.PreferencePageTest;
import org.junit.Test;

/**
 * @author eskimo
 *
 */
public class JsfUiPreferencesPagesTest extends PreferencePageTest {

	public void _testJsfCapabilitiesPreferencesPage() {
		System.out.println("JsfUiPreferencesPagesTest._testJsfCapabilitiesPreferencesPage");
		doDefaultTest(JSFCapabilitiesPreferencesPage.ID, JSFCapabilitiesPreferencesPage.class);
	}
	
	@Test
	public void testJSFFlowTabbedPreferencesPage() {
		System.out.println("JsfUiPreferencesPagesTest.testJSFFlowTabbedPreferencesPage");
		doDefaultTest(JSFFlowTabbedPreferencesPage.ID,JSFFlowTabbedPreferencesPage.class);
	}
	
	public void _testJsfImplementationsPreferencesPage() {
		System.out.println("JsfUiPreferencesPagesTest._testJsfImplementationsPreferencesPage");
		doDefaultTest(JSFImplementationsPreferencesPage.ID,JSFImplementationsPreferencesPage.class);
	}
	
	@Test
	public void testJsfProjectPreferencesPage() {
		System.out.println("JsfUiPreferencesPagesTest.testJsfProjectPreferencesPage");
		doDefaultTest(JSFProjectPreferencesPage.ID,JSFProjectPreferencesPage.class);
	}
	
	@Test
	public void testJsfStudioPreferencesPage() {
		System.out.println("JsfUiPreferencesPagesTest.testJsfStudioPreferencesPage");
		doDefaultTest(JSFStudioPreferencesPage.ID,JSFStudioPreferencesPage.class);
	}
}
