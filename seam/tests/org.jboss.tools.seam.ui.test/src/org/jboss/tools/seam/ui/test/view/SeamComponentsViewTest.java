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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.navigator.CommonNavigator;
import org.jboss.tools.common.model.XJob;
import org.jboss.tools.common.test.util.TestProjectProvider;
import org.jboss.tools.jst.web.ui.RedHat4WebPerspectiveFactory;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamPackage;
import org.jboss.tools.seam.core.ISeamScope;
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
	
	public void testComponentViewTree(){
		System.out.println("testAComponentViewTree!");
		TestProjectProvider provider=null;
		try {
			provider = new TestProjectProvider("org.jboss.tools.seam.ui.test", null, "TestComponentView", true);
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
		System.out.println("Refresh is DONE!");
		
		IFile f = project.getFile("WebContent/WEB-INF/components.xml");
		assertTrue("Cannot find components.xml in test project", f != null && f.exists());
		
		CommonNavigator navigator = getSeamComponentsView();
		navigator.getCommonViewer().expandAll();
		Tree tree = navigator.getCommonViewer().getTree();
		
		System.out.println("tree.getItemCount() - "+tree.getItemCount());
		for(int i=0;i<tree.getItemCount();i++){
			showTreeItem(tree.getItem(i),0);
		}
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
			provider = new TestProjectProvider("org.jboss.tools.seam.ui.test", null, "TestComponentView", true);
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
			ResourcesPlugin.getWorkspace().getRoot().findMember("TestComponentView").delete(true, new NullProgressMonitor());
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
	
	
	
	private void showTreeItem(TreeItem item, int level){
		for(int i=0;i<level;i++)
			System.out.print("-");
		
		System.out.print(item.getText());
		System.out.println(" "+item.getData());
		if(item.getData() instanceof ISeamScope){
			ISeamScope scope = (ISeamScope)item.getData();
			Collection packages = scope.getPackages();
			System.out.println(" packages - "+packages.size());
			Iterator iter = packages.iterator();
			while(iter.hasNext())
				showSeamPackage((ISeamPackage)iter.next(), level++);
			
			
			List components = scope.getComponents();
			for(int i=0;i<components.size();i++)
				showSeamComponent((ISeamComponent)components.get(i), level++);
		}
		
		for(int i=0;i<item.getItemCount();i++){
			showTreeItem(item.getItem(i),level++);
		}
	}
	
	private void showSeamComponent(ISeamComponent component, int level){
		for(int i=0;i<level;i++)
			System.out.print("-");
		
		System.out.println("Component - "+component.getName());
		
	}

	private void showSeamPackage(ISeamPackage seamPackage, int level){
		for(int i=0;i<level;i++)
			System.out.print("-");
		
		System.out.println("Package - "+seamPackage.getName());
		
	}
}
