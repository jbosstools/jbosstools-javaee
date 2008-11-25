/*******************************************************************************
 * Copyright (c) 2008 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.seam.ui.test.wizard;

import junit.framework.TestCase;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.web.ui.internal.wizards.NewProjectDataModelFacetWizard;
import org.jboss.tools.common.util.WorkbenchUtils;
import org.jboss.tools.seam.ui.ISeamUiConstants;
import org.jboss.tools.seam.ui.internal.project.facet.SeamInstallWizardPage;
/**
 * @author dazarov
 * JBIDE-3254 tests
 */
public class PackageNamesTest extends TestCase{
	NewProjectDataModelFacetWizard wizard;
	IWizardPage startSeamPrjWzPg;
	
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testProjectNameWithSpaces() {
		SeamInstallWizardPage seamWizPg = startCreateSeamWizard("Seam Test Project");

		String sessionBeanPkgName = seamWizPg.getSessionBeanPkgName();
		String entityBeanPkgName = seamWizPg.getEntityBeanPkgName();
		String testsPkgName = seamWizPg.getTestsPkgName();
		
		wizard.performCancel();
		assertEquals("org.domain.seamtestproject.session", sessionBeanPkgName);
		assertEquals("org.domain.seamtestproject.entity", entityBeanPkgName);
		assertEquals("org.domain.seamtestproject.test", testsPkgName);
	}
	
	public void testProjectNameWithUnderlines() {
		SeamInstallWizardPage seamWizPg = startCreateSeamWizard("Seam_Test_Project");

		String sessionBeanPkgName = seamWizPg.getSessionBeanPkgName();
		String entityBeanPkgName = seamWizPg.getEntityBeanPkgName();
		String testsPkgName = seamWizPg.getTestsPkgName();
		
		wizard.performCancel();
		assertEquals("org.domain.seamtestproject.session", sessionBeanPkgName);
		assertEquals("org.domain.seamtestproject.entity", entityBeanPkgName);
		assertEquals("org.domain.seamtestproject.test", testsPkgName);
	}
	
	public void testProjectNameWithMinuses() {
		SeamInstallWizardPage seamWizPg = startCreateSeamWizard("Seam-Test-Project");

		String sessionBeanPkgName = seamWizPg.getSessionBeanPkgName();
		String entityBeanPkgName = seamWizPg.getEntityBeanPkgName();
		String testsPkgName = seamWizPg.getTestsPkgName();
		
		wizard.performCancel();
		assertEquals("org.domain.seamtestproject.session", sessionBeanPkgName);
		assertEquals("org.domain.seamtestproject.entity", entityBeanPkgName);
		assertEquals("org.domain.seamtestproject.test", testsPkgName);
	}
	
	public void testProjectNameWithMultipleDots() {
		SeamInstallWizardPage seamWizPg = startCreateSeamWizard("Seam...Test....Project");

		String sessionBeanPkgName = seamWizPg.getSessionBeanPkgName();
		String entityBeanPkgName = seamWizPg.getEntityBeanPkgName();
		String testsPkgName = seamWizPg.getTestsPkgName();
		
		wizard.performCancel();
		assertEquals("org.domain.seam.test.project.session", sessionBeanPkgName);
		assertEquals("org.domain.seam.test.project.entity", entityBeanPkgName);
		assertEquals("org.domain.seam.test.project.test", testsPkgName);
	}
	
	private SeamInstallWizardPage startCreateSeamWizard(String projectName){
		wizard = (NewProjectDataModelFacetWizard)WorkbenchUtils.findWizardByDefId(ISeamUiConstants.NEW_SEAM_PROJECT_WIZARD_ID);
		WizardDialog dialog = new WizardDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				wizard);
		wizard.getDataModel().setStringProperty("IProjectCreationPropertiesNew.PROJECT_NAME", projectName);
		dialog.create();
		startSeamPrjWzPg = wizard.getStartingPage();
		assertNotNull("Cannot create seam start wizard page", startSeamPrjWzPg);
		
		IWizardPage webModuleWizPg = wizard.getNextPage(startSeamPrjWzPg);
		assertNotNull("Cannot create dynamic web project wizard page",webModuleWizPg);
		IWizardPage jsfCapabilitiesWizPg = wizard.getNextPage(webModuleWizPg);
		assertNotNull("Cannot create JSF capabilities wizard page",jsfCapabilitiesWizPg);
		SeamInstallWizardPage seamWizPg = (SeamInstallWizardPage)wizard.getNextPage(jsfCapabilitiesWizPg);
		assertNotNull("Cannot create seam facet wizard page",seamWizPg);
		
		return seamWizPg;
	}
}
