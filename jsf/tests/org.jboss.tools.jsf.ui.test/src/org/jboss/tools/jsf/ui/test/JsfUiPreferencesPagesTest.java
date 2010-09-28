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

/**
 * @author eskimo
 *
 */
public class JsfUiPreferencesPagesTest extends PreferencePageTest {

	public void testJsfCapabilitiesPreferencesPage() {
		doDefaultTest(JSFCapabilitiesPreferencesPage.ID, JSFCapabilitiesPreferencesPage.class);
	}
	
	public void testJSFFlowTabbedPreferencesPage() {
		doDefaultTest(JSFFlowTabbedPreferencesPage.ID,JSFFlowTabbedPreferencesPage.class);
	}
	
	public void testJsfImplementationsPreferencesPage() {
		doDefaultTest(JSFImplementationsPreferencesPage.ID,JSFImplementationsPreferencesPage.class);
	}
	
	public void testJsfProjectPreferencesPage() {
		doDefaultTest(JSFProjectPreferencesPage.ID,JSFProjectPreferencesPage.class);
	}
	
	public void testJsfStudioPreferencesPage() {
		doDefaultTest(JSFStudioPreferencesPage.ID,JSFStudioPreferencesPage.class);
	}
}
