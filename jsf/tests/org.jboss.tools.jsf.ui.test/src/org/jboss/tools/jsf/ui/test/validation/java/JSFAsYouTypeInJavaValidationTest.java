/*******************************************************************************
 * Copyright (c) 2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.ui.test.validation.java;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.jboss.tools.common.base.test.validation.java.BaseAsYouTypeInJavaValidationTest;
import org.jboss.tools.common.preferences.SeverityPreferences;
import org.jboss.tools.jst.web.kb.WebKbPlugin;
import org.jboss.tools.jst.web.kb.preferences.ELSeverityPreferences;
import org.jboss.tools.test.util.ProjectImportTestSetup;

/**
 * 
 * @author Victor V. Rubezhny
 *
 */
public class JSFAsYouTypeInJavaValidationTest extends BaseAsYouTypeInJavaValidationTest {
	private static final String PROJECT_NAME = "JSF2KickStartWithoutLibs";
	private static final String PAGE_NAME = "JavaSource/demo/User.java";

	private static final String [][] EL2VALIDATE = 
		{ 
			{"#{user.names}", "\"names\" cannot be resolved"}, 
			{"#{suser.name}", "\"suser\" cannot be resolved"},
			{"#{['}", "EL syntax error: Expecting expression."}
		};

	public void setUp() throws Exception {
		project = ProjectImportTestSetup.loadProject(PROJECT_NAME);
		project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
	}

	public void testAsYouTypeInJavaValidation() throws JavaModelException {
		assertNotNull("Test project '" + PROJECT_NAME + "' is not prepared", project);
		openEditor(PAGE_NAME);
		IPreferenceStore store = WebKbPlugin.getDefault().getPreferenceStore();
		String defaultValidateUnresolvedEL = SeverityPreferences.ENABLE;
		String defaultUnknownELVariableName = SeverityPreferences.IGNORE;
		try {
			defaultValidateUnresolvedEL = store.getString(ELSeverityPreferences.RE_VALIDATE_UNRESOLVED_EL);
			defaultUnknownELVariableName = store.getString(ELSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME);
			store.setValue(ELSeverityPreferences.RE_VALIDATE_UNRESOLVED_EL, SeverityPreferences.ENABLE);
			store.setValue(ELSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME, SeverityPreferences.ERROR);
			for (int i = 0; i < EL2VALIDATE.length; i++) {
				doAsYouTipeInJavaValidationTest(EL2VALIDATE[i][0], EL2VALIDATE[i][1]);
			}
		} finally {
			store.setValue(ELSeverityPreferences.RE_VALIDATE_UNRESOLVED_EL, defaultValidateUnresolvedEL);
			store.setValue(ELSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME, defaultUnknownELVariableName);
			closeEditor();
		}
	}
}
