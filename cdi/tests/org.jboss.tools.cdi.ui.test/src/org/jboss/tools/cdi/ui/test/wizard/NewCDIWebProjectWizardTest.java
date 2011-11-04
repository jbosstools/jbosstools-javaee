/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.ui.test.wizard;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jst.jsf.ui.internal.project.facet.JSFFacetInstallPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetDataModelProperties;
import org.eclipse.wst.web.ui.internal.wizards.NewProjectDataModelFacetWizard;
import org.jboss.tools.cdi.ui.wizard.CDIProjectWizard;
import org.jboss.tools.test.util.WorkbenchUtils;

/**
 * @author Alexey Kazakov
 */
public class NewCDIWebProjectWizardTest extends TestCase{

	NewProjectDataModelFacetWizard wizard;
	WizardDialog dialog;

	public NewCDIWebProjectWizardTest() {
		super("New CDI Web Project tests");
	}

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		wizard = (NewProjectDataModelFacetWizard)WorkbenchUtils.findWizardByDefId(CDIProjectWizard.ID);
		dialog = new WizardDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				wizard);
		dialog.create();
		dialog.setBlockOnOpen(false);
		dialog.open();
		cleanDefferedEvents();
	}

	private void cleanDefferedEvents() {
		while (Display.getCurrent().readAndDispatch());
	}

	public void testCDIProjectNewWizardInstanceIsCreated() {
		try {
			IWizardPage startCDIPrjWzPg = wizard.getStartingPage();

			wizard.getDataModel().setStringProperty("IProjectCreationPropertiesNew.PROJECT_NAME", "testName");
			assertNotNull("Cannot create start wizard page", startCDIPrjWzPg);

			IWizardPage javaModuleWizPg = wizard.getNextPage(startCDIPrjWzPg);
			assertNotNull("Cannot create java project wizard page", javaModuleWizPg);

			IWizardPage webModuleWizPg = wizard.getNextPage(javaModuleWizPg);
			assertNotNull("Cannot create dynamic web project wizard page", webModuleWizPg);

			IWizardPage cdiWizPg = wizard.getNextPage(webModuleWizPg);
			assertNotNull("Cannot create cdi facet wizard page", cdiWizPg);

			IWizardPage jsfCapabilitiesWizPg = wizard.getNextPage(cdiWizPg);
			assertNotNull("Cannot create JSF capabilities wizard page", jsfCapabilitiesWizPg);
		} finally {
			wizard.performCancel();
			dialog.close();
		}
	}

	public void testCDIProjectNewWizardFinisDisableByDefaul() {
		try {
			// Disable Library Configuration
			disableLibraryConfiguration();
			cleanDefferedEvents();

			boolean canFinish = wizard.canFinish();
			assertFalse("Finish button is enabled at first wizard page before all requerd fileds are valid.", canFinish);
		} finally {
			wizard.performCancel();
			dialog.close();
		}
	}

	/**
	 * If all fields of all pages are valid then
	 * first page of New CDI Project Wizard must enable Finish button. 
	 */
	public void testCDIProjectNewWizardFinisEnabled() {
		try {
			// Disable Library Configuration
			disableLibraryConfiguration();
			cleanDefferedEvents();

			// Set project name
			wizard.getDataModel().setProperty(IFacetDataModelProperties.FACET_PROJECT_NAME, "testSeamProject");
			cleanDefferedEvents();
			
			assertTrue("Finish button is disabled at first wizard page in spite of valid project name.", wizard.canFinish());
		} finally {
			wizard.performCancel();
			dialog.close();
		}
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
}