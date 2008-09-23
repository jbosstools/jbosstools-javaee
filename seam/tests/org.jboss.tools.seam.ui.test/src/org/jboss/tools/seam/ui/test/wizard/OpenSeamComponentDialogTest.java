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
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.seam.ui.wizard.OpenSeamComponentDialog;
import org.jboss.tools.seam.ui.wizard.OpenSeamComponentDialog.SeamComponentWrapper;
import org.jboss.tools.test.util.ProjectImportTestSetup;
import org.jboss.tools.test.util.xpl.EditorTestHelper;

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
			this.project.refreshLocal(IResource.DEPTH_INFINITE, null);
		}
		this.project = project.getProject();
		this.project.build(IncrementalProjectBuilder.FULL_BUILD, null);
		
		EditorTestHelper.joinBackgroundActivities();
	}
	
	@Override
	protected void tearDown() throws Exception {
		EditorTestHelper.joinBackgroundActivities();
		if(project != null){
			project.close(new NullProgressMonitor());
			project.delete(true, new NullProgressMonitor());
			project = null;
			EditorTestHelper.joinBackgroundActivities();
		}
		
	}
	
	public void testOpenSeamComponentDialogSearch() {
		
		find("m", "mockSecureEntity", true);
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
		
		dialog.setInitialPattern(pattern);
		dialog.beginTest();
		if(wait){
			try{
				EditorTestHelper.joinBackgroundActivities();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		dialog.endTest();
		Object[] objects = dialog.getResult();
		
		assertNotNull("Search dialog returned null when searching for " + pattern, objects);
		
		assertTrue("Component "+componentName+" not found", objects.length != 0);
		
		SeamComponentWrapper wrapper = (SeamComponentWrapper)objects[0];
		
		assertEquals("Component "+componentName+" not found with " + pattern, wrapper.getComponentName(), componentName);
	}
	
}
