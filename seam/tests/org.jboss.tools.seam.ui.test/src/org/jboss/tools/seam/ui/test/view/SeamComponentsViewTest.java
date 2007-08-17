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
package org.jboss.tools.seam.ui.test.view;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.navigator.CommonNavigator;
import org.jboss.tools.common.model.XJob;
import org.jboss.tools.common.test.util.TestProjectProvider;
import org.jboss.tools.jst.web.ui.RedHat4WebPerspectiveFactory;
import org.jboss.tools.seam.ui.ISeamUiConstants;
import org.jboss.tools.test.util.JUnitUtils;
import org.jboss.tools.test.util.WorkbenchUtils;

/**
 * 
 * @author eskimo
 *
 */
public class SeamComponentsViewTest extends TestCase {

	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		WorkbenchUtils.getWorkbench().showPerspective(
				RedHat4WebPerspectiveFactory.PERSPECTIVE_ID,
				WorkbenchUtils.getWorkbench().getActiveWorkbenchWindow());
		}

	/**
	 * 
	 */
	public void testSeamComponentsViewIsShowedOnPerspective() {
		IWorkbenchPage page  = WorkbenchUtils.getWorkbenchActivePage();
		IViewPart part = page.findView(ISeamUiConstants.SEAM_COMPONENTS_VIEW_ID);
		assertNotNull("Cannot show the Seam Components View", part);
	}
	
	public void testCreatedProjectIsShownOnTree() {
		TestProjectProvider provider=null;
		try {
			provider = new TestProjectProvider("org.jboss.tools.seam.core.test", null, "TestScanner", true);
		} catch (Exception e1) {
			JUnitUtils.fail("Cannot create Project Provider", e1);
		} 
		IProject project = provider.getProject();
		try {
			project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		} catch (Exception e) {
			JUnitUtils.fail("Cannot refresh created test Project", e);
		}
		try {
			XJob.waitForJob();
		} catch (InterruptedException e) {
			JUnitUtils.fail(e.getMessage(),e);
		}
		IStructuredContentProvider content 
		      = (IStructuredContentProvider)getSeamComponentsView().getCommonViewer().getContentProvider();
		assertTrue("Created Seam enabled project haven't been shown in tree",1==content.getElements(ResourcesPlugin.getWorkspace().getRoot()).length);		
		
	}
	
	public void testThatDeletedProjectIsDisappearedFromTree() {
		try {
			ResourcesPlugin.getWorkspace().getRoot().findMember("TestScanner").delete(true, new NullProgressMonitor());
		} catch (CoreException e) {
			JUnitUtils.fail(e.getMessage(),e);
		}	
		IStructuredContentProvider content 
	      = (IStructuredContentProvider)getSeamComponentsView().getCommonViewer().getContentProvider();
			assertTrue("Created Seam enabled project haven't been deleted from tree",0==content.getElements(ResourcesPlugin.getWorkspace().getRoot()).length);
	}
	
	private CommonNavigator getSeamComponentsView() {
		IWorkbenchPage page  = WorkbenchUtils.getWorkbenchActivePage();
		CommonNavigator part = (CommonNavigator)page.findView(ISeamUiConstants.SEAM_COMPONENTS_VIEW_ID);
		return part;
	}
}
