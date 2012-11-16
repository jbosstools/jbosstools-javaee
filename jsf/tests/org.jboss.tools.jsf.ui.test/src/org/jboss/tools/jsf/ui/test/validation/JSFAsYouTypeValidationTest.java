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
package org.jboss.tools.jsf.ui.test.validation;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.wst.validation.ValidationFramework;
import org.jboss.tools.common.preferences.SeverityPreferences;
import org.jboss.tools.jst.jsp.test.validation.BaseAsYouTypeValidationTest;
import org.jboss.tools.jst.web.kb.WebKbPlugin;
import org.jboss.tools.jst.web.kb.preferences.ELSeverityPreferences;
import org.jboss.tools.test.util.JobUtils;
import org.osgi.framework.Bundle;

/**
 * 
 * @author Victor V. Rubezhny
 *
 */
public class JSFAsYouTypeValidationTest extends BaseAsYouTypeValidationTest {
	private static final String PROJECT_NAME = "JSF2KickStartWithoutLibs";
	private static final String[][] PAGE_NAMES = {
		{"projects/AsYouTypeTestData/asYouType.xhtml", "WebContent/pages/asYouType.xhtml"},
		{"projects/AsYouTypeTestData/asYouType.jsp", "WebContent/pages/asYouType.jsp"}
	};

	private static final String [][] EL2VALIDATE = 
		{ 
			{"#{user.name}", "#{user.names}", "\"names\" cannot be resolved", "#{user.namess}", "\"namess\" cannot be resolved"}, 
			{"#{user.name}", "#{suser.name}", "\"suser\" cannot be resolved", "#{ssuser.name}", "\"ssuser\" cannot be resolved"},
			{"#{user.name}", "#{[}", "EL syntax error: Expecting expression.", "#{[[}", "EL syntax error: Expecting expression."}
		};

	@Override
	public void setUp() throws Exception {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME);
	}

	public void testAsYouTypeMarkerAnnotationsRemovalValidation() throws Exception {
		assertNotNull("Test project '" + PROJECT_NAME + "' is not prepared", project);

		for (int p = 0; p < PAGE_NAMES.length; p++) {
			String sourcePageName = PAGE_NAMES[p][0];
			String pageName = PAGE_NAMES[p][1];

			IFile file = project.getFile(pageName);
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
						prepareModifiedFile("org.jboss.tools.jsf.ui.test", sourcePageName, file, EL2VALIDATE[i][1]);
						waitForValidation(project);

						openEditor(pageName);
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
				try {
					file.delete(true, null);
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void testAsYouTypeValidation() throws Exception {
		assertNotNull("Test project '" + PROJECT_NAME + "' is not prepared", project);

		for (int p = 0; p < PAGE_NAMES.length; p++) {
			String sourcePageName = PAGE_NAMES[p][0];
			String pageName = PAGE_NAMES[p][1];

			IFile file = project.getFile(pageName);
			prepareFile("org.jboss.tools.jsf.ui.test", sourcePageName, file);
			IPreferenceStore store = WebKbPlugin.getDefault().getPreferenceStore();
			String defaultValidateUnresolvedEL = SeverityPreferences.ENABLE;
			String defaultUnknownELVariableName = SeverityPreferences.IGNORE;
			try {
				defaultValidateUnresolvedEL = store.getString(ELSeverityPreferences.RE_VALIDATE_UNRESOLVED_EL);
				defaultUnknownELVariableName = store.getString(ELSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME);
				store.setValue(ELSeverityPreferences.RE_VALIDATE_UNRESOLVED_EL, SeverityPreferences.ENABLE);
				store.setValue(ELSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME, SeverityPreferences.ERROR);

				openEditor(pageName);
				for (int i = 0; i < EL2VALIDATE.length; i++) {
					int count = 0;
					while(doAsYouTypeValidationTest(EL2VALIDATE[i][0], EL2VALIDATE[i][1], EL2VALIDATE[i][2], count++))
						;
					assertTrue("No test regions found!", count > 1);
				}
			} finally {
				closeEditor();
				store.setValue(ELSeverityPreferences.RE_VALIDATE_UNRESOLVED_EL, defaultValidateUnresolvedEL);
				store.setValue(ELSeverityPreferences.UNKNOWN_EL_VARIABLE_NAME, defaultUnknownELVariableName);
			}
		}
	}

	boolean suspended;

	@Override
	public void openEditor(String fileName) {
		suspended = ValidationFramework.getDefault().isSuspended();
		ValidationFramework.getDefault().suspendAllValidation(false);
		super.openEditor(fileName);
		JobUtils.delay(500);
	}

	@Override
	public void closeEditor() {
		super.closeEditor();
		ValidationFramework.getDefault().suspendAllValidation(suspended);
	}

	private void prepareFile(String bundleName, String file, IFile destination) throws Exception {
		Bundle bundle = Platform.getBundle(bundleName);

		String filePath = FileLocator.resolve(bundle.getEntry(file)).getFile();
		java.io.File data = new java.io.File(filePath);

		BufferedReader r = null;
		ByteArrayInputStream is = null;
		try {
			r = new BufferedReader(new FileReader(data));
			StringBuilder content = new StringBuilder();
			String line = null;
			while ((line = r.readLine()) != null) {
				content.append(line);
				content.append('\n');
			}
			is = new ByteArrayInputStream(content.toString().getBytes("UTF-8"));
			if (destination.exists()) {
				destination.setContents(is, true, false, null);
			} else {
				destination.create(is, true, null);
			}
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

	private void prepareModifiedFile(String bundleName, String file, IFile destination, String el) throws IOException, CoreException {
		Bundle bundle = Platform.getBundle(bundleName);
		String filePath = FileLocator.resolve(bundle.getEntry(file)).getFile();
		java.io.File data = new java.io.File(filePath);
		BufferedReader r = null;
		ByteArrayInputStream is = null;
		try {
			r = new BufferedReader(new FileReader(data));
			StringBuilder content = new StringBuilder();
			String line = null;
			while ((line = r.readLine()) != null) {
				content.append(line);
				content.append('\n');
			}

			String modifiedContent = modifyELInContent(content, el);
			if (modifiedContent == null)
				modifiedContent = "";

			is = new ByteArrayInputStream(modifiedContent.getBytes("UTF-8"));
			if (destination.exists()) {
				destination.setContents(is, true, false, null);
			} else {
				destination.create(is, true, null);
			}
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