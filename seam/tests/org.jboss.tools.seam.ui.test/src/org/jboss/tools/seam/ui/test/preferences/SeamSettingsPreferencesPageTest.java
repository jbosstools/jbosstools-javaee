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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.preference.PreferenceDialog;
import org.jboss.tools.common.ui.widget.editor.IFieldEditor;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.project.facet.SeamRuntime;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeManager;
import org.jboss.tools.seam.core.project.facet.SeamVersion;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.seam.ui.preferences.SeamPreferencesMessages;
import org.jboss.tools.seam.ui.preferences.SeamSettingsPreferencePage;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.WorkbenchUtils;
import org.osgi.framework.Bundle;

/**
 * @author Alexey Kazakov
 */
public class SeamSettingsPreferencesPageTest extends TestCase {

	IProject project = null;

	// TODO move Build related constants to separate class accessible for all tests
	static final String INIT_ERROR_MESSAGE = "System property ''{0}'' must be configured with -D to run these tests";
	static final String PROJECT_NAME = "TestSeamSettingsPreferencesPage";
	static final String MODEL_PACKAGE_NAME = ("org.domain." + PROJECT_NAME + ".entity").toLowerCase();
	static final String ACTION_PACKAGE_NAME = ("org.domain." + PROJECT_NAME + ".session").toLowerCase();
	static final String TEST_PACKAGE_NAME = ("org.domain." + PROJECT_NAME + ".test").toLowerCase();
	static final String RUNTIME_NAME = "Seam 1.2.0 Seam Settings Page Test";

	public static final String PROP_SEAM_1_2_HOME_PATH = "jbosstools.test.seam.1.2.1.eap.home";
	public static final String SEAM_1_2_HOME_PATH;
	
	static {
		SEAM_1_2_HOME_PATH = System.getProperty(PROP_SEAM_1_2_HOME_PATH);
		if(SEAM_1_2_HOME_PATH == null) {
			throw new IllegalArgumentException(MessageFormat.format(INIT_ERROR_MESSAGE, PROP_SEAM_1_2_HOME_PATH));
		}
	}
	
	public SeamSettingsPreferencesPageTest() {
		super("Seam Settings Preferences Page Tests");
	}

	protected void setUp() throws Exception {
		IResource project = ResourcesPlugin.getWorkspace().getRoot().findMember(PROJECT_NAME);
		assertNotNull(PROJECT_NAME + " project is not imported.", project);
		this.project = project.getProject();
		this.project.build(IncrementalProjectBuilder.FULL_BUILD, null);
		JobUtils.waitForIdle();
	}

	public void testSettingsPage() throws Exception {
		PreferenceDialog dialog = WorkbenchUtils.createPropertyDialog("org.jboss.tools.seam.ui.propertyPages.SeamSettingsPreferencePage", project);
		dialog.setBlockOnOpen(false);
		dialog.open();
		try {
			SeamSettingsPreferencePage page = (SeamSettingsPreferencePage)dialog.getSelectedPage();
			IFieldEditor seamSuport = page.getEditor(SeamPreferencesMessages.SEAM_SETTINGS_PREFERENCE_PAGE_SEAM_SUPPORT);
			seamSuport.setValue(Boolean.TRUE);

			Bundle seamCoreTest = Platform.getBundle("org.jboss.tools.seam.core.test");
			File folder = new File(SEAM_1_2_HOME_PATH);
			SeamRuntimeManager manager = SeamRuntimeManager.getInstance();
			assertNotNull("Cannot obtainSeamRuntimeManager instance", manager);
			manager.addRuntime(RUNTIME_NAME, folder.getAbsolutePath(), SeamVersion.SEAM_1_2, true);

			IFieldEditor seamRuntime = page.getEditor(ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME);
			SeamRuntime newRuntime = manager.findRuntimeByName(RUNTIME_NAME);
			assertNotNull("New runtime was not added to SeamRuntimeManager.", newRuntime);
			seamRuntime.setValue(newRuntime.getName());

			IFieldEditor viewFolder = page.getEditor(ISeamFacetDataModelProperties.WEB_CONTENTS_FOLDER);
			viewFolder.setValue("/" + PROJECT_NAME);

			IFieldEditor createTest = page.getEditor(ISeamFacetDataModelProperties.TEST_CREATING);
			createTest.setValue(Boolean.TRUE);

			assertTrue("Settings page is not valid: " + page.getErrorMessage(), page.okToLeave());
			page.performOk();
		} finally {
			dialog.close();
		}

		JobUtils.waitForIdle();

		ISeamProject seamProject = SeamCorePlugin.getSeamProject(project, false);
		assertNotNull("Can't load seam project. It seems seam nature was not added to rpoject by seam settings page.", seamProject);
		
		IEclipsePreferences pref = SeamCorePlugin.getSeamPreferences(project);
		assertEquals("Seam settings version 1.1 property is not set", pref.get(ISeamFacetDataModelProperties.SEAM_SETTINGS_VERSION, ""), ISeamFacetDataModelProperties.SEAM_SETTINGS_VERSION_1_1);
		assertEquals("Seam runtime property is not set", pref.get(ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME, ""), RUNTIME_NAME);
		assertEquals("Seam deployment type property is not set", pref.get(ISeamFacetDataModelProperties.JBOSS_AS_DEPLOY_AS, ""), ISeamFacetDataModelProperties.DEPLOY_AS_WAR);
		assertEquals("Model package name property is not set", MODEL_PACKAGE_NAME,pref.get(ISeamFacetDataModelProperties.ENTITY_BEAN_PACKAGE_NAME, ""));
		assertEquals("Model source folder property is not set", pref.get(ISeamFacetDataModelProperties.ENTITY_BEAN_SOURCE_FOLDER, ""), "/" + PROJECT_NAME + "/src");
		assertEquals("Action package name property is not set", ACTION_PACKAGE_NAME, pref.get(ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_NAME, ""));
		assertEquals("Action source folder property is not set", pref.get(ISeamFacetDataModelProperties.SESSION_BEAN_SOURCE_FOLDER, ""), "/" + PROJECT_NAME + "/src");
		assertEquals("Seam 'create tests' property is not set", pref.get(ISeamFacetDataModelProperties.TEST_CREATING, ""), "true");
		assertEquals("Test project property is not set", pref.get(ISeamFacetDataModelProperties.SEAM_TEST_PROJECT, ""), PROJECT_NAME);
		assertEquals("Test package name property is not set", TEST_PACKAGE_NAME, pref.get(ISeamFacetDataModelProperties.TEST_CASES_PACKAGE_NAME, ""));
		assertEquals("Test source folder property is not set", pref.get(ISeamFacetDataModelProperties.TEST_SOURCE_FOLDER, ""), "/" + PROJECT_NAME + "/src");
		assertEquals("View folder property is not set", pref.get(ISeamFacetDataModelProperties.WEB_CONTENTS_FOLDER, ""), "/" + PROJECT_NAME);
		
		assertTrue("Seam Nature was not enabled for project \"" + project.getName() + "\"",project.hasNature(ISeamProject.NATURE_ID));
	}
}