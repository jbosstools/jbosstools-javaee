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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.datatools.connectivity.ConnectionProfileException;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jst.common.project.facet.core.libprov.LibraryInstallDelegate;
import org.eclipse.jst.jsf.ui.internal.project.facet.JSFFacetInstallPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetDataModelProperties;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.web.ui.internal.wizards.NewProjectDataModelFacetWizard;
import org.jboss.tools.jst.firstrun.JBossASAdapterInitializer;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeManager;
import org.jboss.tools.seam.core.project.facet.SeamVersion;
import org.jboss.tools.seam.ui.ISeamUiConstants;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.WorkbenchUtils;

/**
 * @author eskimo, akazakov
 *
 */
public class SeamProjectNewWizardTest extends TestCase{
	/**
	 * 
	 */
	private static final String SEAM_1_2_1_RT_NAME = "Seam 1.2.1";
	
	NewProjectDataModelFacetWizard wizard;
	WizardDialog dialog;
	public SeamProjectNewWizardTest() {
		super("New Seam Web Project tests");
	}
	
	public static TestSuite suite() {
		TestSuite suite = new TestSuite();
		suite.addTest(new WizardTestSetup(new TestSuite(SeamProjectNewWizardTest.class,"Seam Project New Wizard Tests")));
		return suite;
	}
	
	@Override
	protected void setUp() throws Exception {
		wizard = (NewProjectDataModelFacetWizard)WorkbenchUtils.findWizardByDefId(ISeamUiConstants.NEW_SEAM_PROJECT_WIZARD_ID);
		dialog = new WizardDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				wizard);
		dialog.create();
		dialog.setBlockOnOpen(false);
		dialog.open();
		JobUtils.delay(2000);
	}

	/**
	 * 
	 */
	public void testSeamProjectNewWizardInstanceIsCreated() {
		IWizardPage startSeamPrjWzPg = wizard.getStartingPage();
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
	
	public void testSeamProjectNewWizardFinisDisableByDefaul() {
		// Disable Library Configuration
		disableLibraryConfiguration();
		JobUtils.delay(1000);

		boolean canFinish = wizard.canFinish();
		assertFalse("Finish button is enabled at first wizard page before all requerd fileds are valid.", canFinish);
	}

	/**
	 * If all fields of all pages are valid then
	 * first page of New Seam Project Wizard must enable Finish button. 
	 * See http://jira.jboss.com/jira/browse/JBIDE-1111
	 */
	public void testJiraJbide1111() {
		// Disable Library Configuration
		disableLibraryConfiguration();
		JobUtils.delay(1000);
		
		// Set project name
		wizard.getDataModel().setProperty(IFacetDataModelProperties.FACET_PROJECT_NAME, "testSeamProject");
		JobUtils.delay(1000);
		
		assertTrue("Finish button is disabled at first wizard page in spite of created JBoss AS Runtime, Server, DB Connection and Seam Runtime and valid project name.", wizard.canFinish());
	}
	
	private void disableLibraryConfiguration(){
		for(IWizardPage page : wizard.getPages()){
			if(page instanceof JSFFacetInstallPage){
				JSFFacetInstallPage jsfPage = (JSFFacetInstallPage)page;
				Control control = page.getControl();
				if(control instanceof Composite){
					processComposite((Composite)control);
				}
			}
		}
	}
	
	private void processComposite(Composite parent){
		for(Control child : parent.getChildren()){
			if(child instanceof Combo){
				Combo combo = (Combo)child;
				
				int index = -1;
				for(int i=0; i < combo.getItemCount();i++){
					String item = combo.getItem(i);
					if("Disable Library Configuration".equals(item)){
						index = i;
						break;
					}
				}
				if(index >= 0){
					combo.select(index);
					try{
						Method method = Widget.class.getDeclaredMethod("sendEvent",new Class[]{int.class});
	                    if(method != null){
	                    	method.setAccessible(true);
	                    	method.invoke(combo, new Object[]{SWT.Selection});
	                    }
					}catch(NoSuchMethodException ex){
						ex.printStackTrace();
					}catch(InvocationTargetException ex){
						ex.printStackTrace();
					}catch(IllegalAccessException ex){
						ex.printStackTrace();
					}
				}
			}
			
			if(child instanceof Composite)
				processComposite((Composite)child);
		}
	}

	@Override
	protected void tearDown() throws Exception {
		wizard.performCancel();
		dialog.close();
	}
	
	public static final String INIT_ERROR_MESSAGE = "System property ''{0}'' must be configured with -D to run these tests";
	public static final String PROP_JBOSS_AS_4_2_HOME = "jbosstools.test.jboss.home.4.2";
	public static final String JBOSS_AS_42_HOME_PATH;
	public static final String PROP_SEAM_1_2_HOME_PATH = "jbosstools.test.seam.1.2.1.eap.home";
	public static final String SEAM_1_2_HOME_PATH;
	
	static {
		SEAM_1_2_HOME_PATH = System.getProperty(PROP_SEAM_1_2_HOME_PATH);
		if(SEAM_1_2_HOME_PATH == null) {
			throw new IllegalArgumentException(MessageFormat.format(INIT_ERROR_MESSAGE, PROP_SEAM_1_2_HOME_PATH));
		}
		JBOSS_AS_42_HOME_PATH = System.getProperty(PROP_JBOSS_AS_4_2_HOME);
		if(JBOSS_AS_42_HOME_PATH == null) {
			throw new IllegalArgumentException(MessageFormat.format(INIT_ERROR_MESSAGE,PROP_JBOSS_AS_4_2_HOME));
		}
	}
	
	public static class WizardTestSetup extends TestSetup {
			
		SeamRuntimeManager manager = SeamRuntimeManager.getInstance();
		
		public WizardTestSetup(Test test) {
			super(test);
		}

		@Override
		protected void setUp() throws Exception {
			File folder = new File(SEAM_1_2_HOME_PATH);
			manager.addRuntime(SEAM_1_2_1_RT_NAME, folder.getAbsolutePath(), SeamVersion.SEAM_1_2, true);
			
			// Create JBoss AS Runtime, Server, HSQL DB Driver
			try {
				IServerWorkingCopy server = JBossASAdapterInitializer.initJBossAS(JBOSS_AS_42_HOME_PATH, new NullProgressMonitor());
			} catch (CoreException e) {
				fail("Cannot create JBoss AS Runtime, Server or HSQL Driver for unexisted AS location to test New Seam Project Wizard. " + e.getMessage());
			} catch (ConnectionProfileException e) {
				fail("Cannot create HSQL Driver for nonexistent AS location to test New Seam Project Wizard. " + e.getMessage());
			}
		}

		@Override
		protected void tearDown() throws Exception {
			manager.removeRuntime(manager.findRuntimeByName(SEAM_1_2_1_RT_NAME));
		}
	}
}