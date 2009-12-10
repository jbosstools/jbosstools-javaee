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
import org.jboss.tools.jsf.ui.test.refactoring.ELReferencesRenameTest;
import org.jboss.tools.test.util.ProjectImportTestSetup;

import junit.framework.Test;
import junit.framework.TestSuite;

public class JsfUiAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("JSF UI tests");
		suite.addTestSuite(CssClassNewWizardTest.class);
		suite.addTestSuite(CssFileNewWizardTest.class);
		suite.addTestSuite(CSSStyleDialogTest.class);
		suite.addTestSuite(FacesConfigNewWizardTest.class);
		suite.addTestSuite(FasesConfigEditorTest.class);
		suite.addTestSuite(HtmlFileNewWizardTest.class);
		suite.addTestSuite(JsFileNewWizardTest.class);
		suite.addTestSuite(JspFileNewWizardTest.class);
		suite.addTestSuite(PropertiesNewWizardTest.class);
		suite.addTestSuite(XhtmlFileNewWizardTest.class);
		suite.addTestSuite(JsfUiPreferencesPagesTest.class);
		suite.addTest(new ProjectImportTestSetup(new TestSuite(ELReferencesRenameTest.class),
				"org.jboss.tools.jsf.ui.test",
				new String[]{"projects/testJSFProject",},
				new String[]{"testJSFProject"}));
		
		return new TestWizardsProject(suite);
	}
}