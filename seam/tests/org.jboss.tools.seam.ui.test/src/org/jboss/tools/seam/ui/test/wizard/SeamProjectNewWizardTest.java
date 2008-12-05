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
package org.jboss.tools.seam.ui.test.wizard;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.datatools.connectivity.ConnectionProfileException;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.operations.IProjectCreationPropertiesNew;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.web.ui.internal.wizards.NewProjectDataModelFacetWizard;
import org.jboss.tools.common.util.WorkbenchUtils;
import org.jboss.tools.jst.firstrun.JBossASAdapterInitializer;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeManager;
import org.jboss.tools.seam.core.project.facet.SeamVersion;
import org.jboss.tools.seam.ui.ISeamUiConstants;
import org.jboss.tools.test.util.JobUtils;
import org.osgi.framework.Bundle;

/**
 * @author eskimo, akazakov
 *
 */
public class SeamProjectNewWizardTest extends TestCase{

	
	/**
	 * 
	 */
	private static final String SEAM_2_0_0_RT_NAME = "Seam 2.0";
	/**
	 * 
	 */
	private static final String SEAM_1_2_1_RT_NAME = "Seam 1.2.1";
	public static final String JBOSS_AS_42_HOME 
		= System.getProperty("jbosstools.test.jboss.home.4.2", "C:\\java\\jboss-4.2.2.GA");
	NewProjectDataModelFacetWizard wizard;
	IWizardPage startSeamPrjWzPg;
	SeamRuntimeManager manager = SeamRuntimeManager.getInstance();
	
	
	public SeamProjectNewWizardTest() {
		super("New Seam Web Project tests");
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();

	}

	/**
	 * 
	 */
	public void testSeamProjectNewWizardInstanceIsCreated() {
		wizard = (NewProjectDataModelFacetWizard)WorkbenchUtils.findWizardByDefId(ISeamUiConstants.NEW_SEAM_PROJECT_WIZARD_ID);
		WizardDialog dialog = new WizardDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				wizard);
		dialog.create();
		dialog.setBlockOnOpen(false);
		dialog.open();
		JobUtils.delay(2000);
		boolean canFinish = wizard.canFinish();
		assertFalse("Finish button is enabled at first wizard page before all requerd fileds are valid.", canFinish);
		startSeamPrjWzPg = wizard.getStartingPage();
		wizard.getDataModel().setStringProperty("IProjectCreationPropertiesNew.PROJECT_NAME","testName");
		assertNotNull("Cannot create seam start wizard page", startSeamPrjWzPg);
		IWizardPage webModuleWizPg = wizard.getNextPage(startSeamPrjWzPg);
		assertNotNull("Cannot create dynamic web project wizard page",webModuleWizPg);
		IWizardPage jsfCapabilitiesWizPg = wizard.getNextPage(webModuleWizPg);
		assertNotNull("Cannot create JSF capabilities wizard page",jsfCapabilitiesWizPg);
		IWizardPage seamWizPg = wizard.getNextPage(jsfCapabilitiesWizPg);
		assertNotNull("Cannot create seam facet wizard page",seamWizPg);
		wizard.performCancel();
	}

	/**
	 * If all fields of all pages are valid then
	 * first page of New Seam Project Wizard must enable Finish button. 
	 * See http://jira.jboss.com/jira/browse/JBIDE-1111
	 */
	public void testJiraJbide1111() {
		wizard = (NewProjectDataModelFacetWizard)WorkbenchUtils.findWizardByDefId(ISeamUiConstants.NEW_SEAM_PROJECT_WIZARD_ID);
		WizardDialog dialog = new WizardDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				wizard);
		dialog.create();
		dialog.setBlockOnOpen(false);
		
		startSeamPrjWzPg = wizard.getStartingPage();
		wizard.getDataModel().setStringProperty("IProjectCreationPropertiesNew.PROJECT_NAME","testName");
		assertNotNull("Cannot create seam start wizard page", startSeamPrjWzPg);
		// Check Finish button
	

		// Create JBoss AS Runtime, Server, HSQL DB Driver
		try {
			IServerWorkingCopy server = JBossASAdapterInitializer.initJBossAS(JBOSS_AS_42_HOME, new NullProgressMonitor());
			System.out.println(server.getName());
			System.out.println(server.getRuntime().getName());
		} catch (CoreException e) {
			fail("Cannot create JBoss AS Runtime, Server or HSQL Driver for unexisted AS location to test New Seam Project Wizard. " + e.getMessage());
		} catch (ConnectionProfileException e) {
			fail("Cannot create HSQL Driver for nonexistent AS location to test New Seam Project Wizard. " + e.getMessage());
		}

		// Create Seam Runtime and set proper field
		Bundle seamTest = Platform.getBundle("org.jboss.tools.seam.ui.test");
		try {
			URL seamUrl = FileLocator.resolve(seamTest.getEntry("/seam"));
			File folder = new File(seamUrl.getPath());
			manager.addRuntime(SEAM_1_2_1_RT_NAME, folder.getAbsolutePath(), SeamVersion.SEAM_1_2, true);
			manager.addRuntime(SEAM_2_0_0_RT_NAME, folder.getAbsolutePath(), SeamVersion.SEAM_2_0, true);
		} catch (IOException e) {
			fail("Cannot create Seam Runtime to test New Seam Project Wizard. " + e.getMessage());
		}
		dialog.open();
		JobUtils.delay(2000);

		// Check Finish button
		boolean canFinish = wizard.canFinish();
		assertFalse("Finish button is enabled at first wizard page before user entered the project name.", canFinish);
		wizard.performCancel();
		
		// Set project name
		IDataModel model = wizard.getDataModel();
		model.setProperty(IFacetDataModelProperties.FACET_PROJECT_NAME, "testSeamProjectNewWizardAllowsToFinishAtFirstPageProjectName");

		dialog.open();
		JobUtils.delay(2000);
		
		// Check Finish button
		canFinish = wizard.canFinish();
		assertTrue("Finish button is disabled at first wizard page in spite of created JBoss AS Runtime, Server, DB Connection and Seam Runtime and valid project name.", canFinish);

		wizard.performCancel();
		
		manager.removeRuntime(manager.findRuntimeByName(SEAM_1_2_1_RT_NAME));
		manager.removeRuntime(manager.findRuntimeByName(SEAM_2_0_0_RT_NAME));		
		
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
}