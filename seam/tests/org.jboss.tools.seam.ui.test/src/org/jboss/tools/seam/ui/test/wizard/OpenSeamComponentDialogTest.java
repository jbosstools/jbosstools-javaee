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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.ui.wizard.OpenSeamComponentDialog;
import org.jboss.tools.seam.ui.wizard.OpenSeamComponentDialog.SeamComponentWrapper;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ProjectImportTestSetup;
import org.jboss.tools.test.util.ResourcesUtils;

/**
 * @author Daniel Azarov
 * 
 */
public class OpenSeamComponentDialogTest extends TestCase{
	private IProject project;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
       
		project = (IProject)ResourcesPlugin.getWorkspace().getRoot().findMember("TestComponentView");
		if(project == null) {
			ProjectImportTestSetup setup = new ProjectImportTestSetup(
					this,
					"org.jboss.tools.seam.ui.test",
					"projects/TestComponentView",
					"TestComponentView");
			project = setup.importProject();
		}
		this.project = project.getProject();
		
		JobUtils.waitForIdle();
	}
	
	@Override
	protected void tearDown() throws Exception {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		try {
			JobUtils.waitForIdle();
			if(project != null){
				try {project.close(new NullProgressMonitor());} catch (Exception e) {e.printStackTrace(System.out);}
				project.delete(true, new NullProgressMonitor());
				project = null;
				JobUtils.waitForIdle();
			}
		} finally {
			ResourcesUtils.setBuildAutomatically(saveAutoBuild);
		}
		
	}
	
	public void testOpenSeamComponentDialogSearch() {
		find("mock", "mockSecureEntity", true);
		find("o", "org.jboss.seam.captcha.captcha", false);
		find("p", "org.jboss.seam.core.pageContext", false);
	}
	
	//JBIDE-1879
	public void testFindShortHand() {
		find("o*jbpm", "org.jboss.seam.core.jbpm", true);
		find("jbpm", "org.jboss.seam.core.jbpm", false);
	}
	
	private void find(String pattern, String componentName, boolean wait){
		OpenSeamComponentDialog dialog = new OpenSeamComponentDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		
		dialog.setBlockOnOpen(false);
		dialog.setInitialPattern(pattern);
		dialog.open();
		try {
			dialog.startSearch();
			if(wait){
					JobUtils.waitForIdle();
					JobUtils.delay(2000);
			}
			dialog.stopSearchAndShowResults();
			Object[] objects = dialog.getResult();
			
			assertNotNull("Search dialog returned null when searching for " + pattern, objects);
			
			assertTrue("Component "+componentName+" not found", objects.length != 0);
		
			ISeamComponent component = findComponent(objects, componentName);
		
			assertNotNull("Component "+componentName+" not found with " + pattern, component);
		} finally {
			dialog.okPressed();
			dialog.close();
		}
	}
	
	private ISeamComponent findComponent(Object[] objects, String componentName) {
		for (Object o: objects) {
			SeamComponentWrapper wrapper = (SeamComponentWrapper)o;
			assertNotNull(wrapper.getComponent());
			if(componentName.equals(wrapper.getComponentName())) {
				return wrapper.getComponent();
			}
		}
		return null;
	}
}
