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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.wst.validation.ValidationFramework;
import org.jboss.tools.common.base.test.validation.java.BaseAsYouTypeInJavaValidationTest;
import org.jboss.tools.common.preferences.SeverityPreferences;
import org.jboss.tools.jst.web.kb.WebKbPlugin;
import org.jboss.tools.jst.web.kb.preferences.ELSeverityPreferences;
import org.jboss.tools.test.util.JobUtils;
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
			{"#{user.name}", "#{user.names}", "\"names\" cannot be resolved", "#{user.namess}", "\"namess\" cannot be resolved"}, 
			{"#{user.name}", "#{suser.name}", "\"suser\" cannot be resolved", "#{ssuser.name}", "\"ssuser\" cannot be resolved"},
			{"#{user.name}", "#{[}", "EL syntax error: Expecting expression.", "#{[[}", "EL syntax error: Expecting expression."}
		};
	private boolean isSuspendedValidationDefaultValue;

	public void setUp() throws Exception {
		project = ProjectImportTestSetup.loadProject(PROJECT_NAME);
		isSuspendedValidationDefaultValue = ValidationFramework.getDefault().isSuspended();
		ValidationFramework.getDefault().suspendAllValidation(false);
		project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
		JobUtils.waitForIdle();
	}

	public void tearDown() throws Exception {
		ValidationFramework.getDefault().suspendAllValidation(isSuspendedValidationDefaultValue);
	}

	public void testAsYouTypeMarkerAnnotationsRemovalInJavaValidation() throws BadLocationException, CoreException {
		assertNotNull("Test project '" + PROJECT_NAME + "' is not prepared", project);
			
		IFile file = project.getFile(PAGE_NAME);
		IPreferenceStore store = WebKbPlugin.getDefault().getPreferenceStore();
		String defaultValidateUnresolvedEL = SeverityPreferences.ENABLE;
		String defaultUnknownELVariableName = SeverityPreferences.IGNORE;
		try {
			defaultValidateUnresolvedEL = store.getString(ELSeverityPreferences.RE_VALIDATE_UNRESOLVED_EL);
			defaultUnknownELVariableName = store.getString(ELSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME);
			store.setValue(ELSeverityPreferences.RE_VALIDATE_UNRESOLVED_EL, SeverityPreferences.ENABLE);
			store.setValue(ELSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME, SeverityPreferences.ERROR);
			for (int i = 0; i < EL2VALIDATE.length; i++) {
				boolean doContinue = true;
				int count = 0;
				while (doContinue) {
					prepareModifiedFile(file, EL2VALIDATE[i][1]);
					waitForValidation(project);

					openEditor(PAGE_NAME);
					try {
						doContinue = doAsYouTypeValidationMarkerAnnotationsRemovalTest(EL2VALIDATE[i][0], EL2VALIDATE[i][1], EL2VALIDATE[i][2], EL2VALIDATE[i][3], EL2VALIDATE[i][4], count++);
					} finally {
						closeEditor();
					}
				}
				assertTrue("No test regions found!", count > 1);
			}
		} finally {
			store.setValue(ELSeverityPreferences.RE_VALIDATE_UNRESOLVED_EL, defaultValidateUnresolvedEL);
			store.setValue(ELSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME, defaultUnknownELVariableName);
		}
	}

	public void testAsYouTypeInJavaValidation() throws JavaModelException, BadLocationException {
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
				int count = 0;
				while(doAsYouTypeValidationTest(EL2VALIDATE[i][0], EL2VALIDATE[i][1], EL2VALIDATE[i][2], count++))
					;
				assertTrue("No test regions found!", count > 1);
			}
		} finally {
			store.setValue(ELSeverityPreferences.RE_VALIDATE_UNRESOLVED_EL, defaultValidateUnresolvedEL);
			store.setValue(ELSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME, defaultUnknownELVariableName);
			closeEditor();
		}
	}
	
	private void prepareModifiedFile(IFile destination, String el) {
		BufferedReader r = null;
		InputStream is = null;
		try {
			is = destination.getContents();
			r = new BufferedReader(new InputStreamReader(is));
			StringBuilder content = new StringBuilder();
			String line = null;
			while ((line = r.readLine()) != null) {
				content.append(line);
				content.append('\n');
			}
			is.close();
			
			String modifiedContent = modifyModifyELInContent(content, el);
			if (modifiedContent == null)
				modifiedContent = "";
			
			is = new ByteArrayInputStream(modifiedContent.getBytes("UTF-8"));
			if (destination.exists()) {
				destination.setContents(is, true, false, null);
			} else {
				destination.create(is, true, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (r != null) {
				try {
					r.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
