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
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetDataModelProperties;
import org.eclipse.wst.web.ui.internal.wizards.NewProjectDataModelFacetWizard;
import org.jboss.tools.seam.ui.ISeamUiConstants;
import org.jboss.tools.seam.ui.SeamUIMessages;
import org.jboss.tools.seam.ui.internal.project.facet.SeamInstallWizardPage;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.WorkbenchUtils;

/**
 * @author dazarov
 * JBIDE-3254 tests
 */
public class PackageNamesTest extends TestCase{
	NewProjectDataModelFacetWizard wizard;
	SeamInstallWizardPage seamWizPg;
	WizardDialog dialog;
	
	protected void setUp() throws Exception {
		wizard = (NewProjectDataModelFacetWizard)WorkbenchUtils.findWizardByDefId(ISeamUiConstants.NEW_SEAM_PROJECT_WIZARD_ID);

		dialog = new WizardDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				wizard);

	}
	
	@Override
	protected void tearDown() {
		wizard.getDataModel().setStringProperty(IFacetDataModelProperties.FACET_PROJECT_NAME, "");
		wizard.performCancel();
		dialog.close();
	}

	public void testProjectNameWithSpaces() {
		showWizard();
		
		wizard.getDataModel().setStringProperty(IFacetDataModelProperties.FACET_PROJECT_NAME, "Seam Test Project");

		String sessionBeanPkgName = seamWizPg.getSessionBeanPkgName();
		String entityBeanPkgName = seamWizPg.getEntityBeanPkgName();
		String testsPkgName = seamWizPg.getTestsPkgName();
		
		assertEquals("org.domain.seamtestproject.session", sessionBeanPkgName);
		assertEquals("org.domain.seamtestproject.entity", entityBeanPkgName);
		assertEquals("org.domain.seamtestproject.test", testsPkgName);
	}
	
	private void showWizard() {
		dialog.create();
		dialog.setBlockOnOpen(false);
		dialog.open();
		// delay is needed to wait for dialog initialization is finished
		// such as default runtime and configuration for seam wizard
		JobUtils.delay(2000);
		seamWizPg = (SeamInstallWizardPage)wizard.getPage(SeamUIMessages.SEAM_INSTALL_WIZARD_PAGE_SEAM_FACET);
	}

	public void testProjectNameWithUnderlines() {
		showWizard();
		
		wizard.getDataModel().setStringProperty(IFacetDataModelProperties.FACET_PROJECT_NAME, "Seam_Test_Project");
		
		String sessionBeanPkgName = seamWizPg.getSessionBeanPkgName();
		String entityBeanPkgName = seamWizPg.getEntityBeanPkgName();
		String testsPkgName = seamWizPg.getTestsPkgName();
		
		assertEquals("org.domain.seamtestproject.session", sessionBeanPkgName);
		assertEquals("org.domain.seamtestproject.entity", entityBeanPkgName);
		assertEquals("org.domain.seamtestproject.test", testsPkgName);
	}
	
	public void testProjectNameWithMinuses() {
		showWizard();
		
		wizard.getDataModel().setStringProperty(IFacetDataModelProperties.FACET_PROJECT_NAME, "Seam-Test-Project");
		
		String sessionBeanPkgName = seamWizPg.getSessionBeanPkgName();
		String entityBeanPkgName = seamWizPg.getEntityBeanPkgName();
		String testsPkgName = seamWizPg.getTestsPkgName();
		
		assertEquals("org.domain.seamtestproject.session", sessionBeanPkgName);
		assertEquals("org.domain.seamtestproject.entity", entityBeanPkgName);
		assertEquals("org.domain.seamtestproject.test", testsPkgName);
	}
	
	public void testProjectNameWithMultipleDots() {
		showWizard();
		
		wizard.getDataModel().setStringProperty(IFacetDataModelProperties.FACET_PROJECT_NAME, "Seam...Test....Project");
		
		String sessionBeanPkgName = seamWizPg.getSessionBeanPkgName();
		String entityBeanPkgName = seamWizPg.getEntityBeanPkgName();
		String testsPkgName = seamWizPg.getTestsPkgName();
		
		assertEquals("org.domain.seam.test.project.session", sessionBeanPkgName);
		assertEquals("org.domain.seam.test.project.entity", entityBeanPkgName);
		assertEquals("org.domain.seam.test.project.test", testsPkgName);
	}

}
