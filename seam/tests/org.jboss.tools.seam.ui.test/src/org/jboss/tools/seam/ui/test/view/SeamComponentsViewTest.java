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

import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.navigator.CommonNavigator;
import org.jboss.tools.common.test.util.TestProjectProvider;
import org.jboss.tools.jst.web.ui.RedHat4WebPerspectiveFactory;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamPackage;
import org.jboss.tools.seam.core.ISeamScope;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.ui.ISeamUiConstants;
import org.jboss.tools.test.util.JUnitUtils;
import org.jboss.tools.test.util.WorkbenchUtils;

/**
 * 
 * @author eskimo
 *
 */
public class SeamComponentsViewTest extends TestCase {
	IProject project;
	IFile file;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		WorkbenchUtils.getWorkbench().showPerspective(
				RedHat4WebPerspectiveFactory.PERSPECTIVE_ID,
				WorkbenchUtils.getWorkbench().getActiveWorkbenchWindow());
		TestProjectProvider provider=null;
		try {
			provider = new TestProjectProvider("org.jboss.tools.seam.ui.test", null, "TestComponentView", true);
		} catch (Exception e1) {
			JUnitUtils.fail("Cannot create Project Provider", e1);
		} 
		project = provider.getProject();
		file = project.getFile("WebContent/WEB-INF/components.xml");
		assertTrue("Cannot find components.xml in test project", file != null && file.exists());
	}
	
	public void testComponentView(){
		addComponent();
		renameComponent();
		deleteComponent();
	}
	
	public void addComponent(){
		SeamCorePlugin.getSeamProject(project, true);
		
		try {
			project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
		} catch (Exception e) {
			JUnitUtils.fail("Cannot build test Project", e);
		}

		CommonNavigator navigator = getSeamComponentsView();
		navigator.getCommonViewer().expandAll();
		
		Tree tree = navigator.getCommonViewer().getTree();

		ISeamPackage seamPackage = findSeamPackage(tree, "myPackage");
		assertTrue("Package \"myPackage\" found!",seamPackage==null);
		
		IFile file1 = project.getFile("WebContent/WEB-INF/components.1");
		assertTrue("Cannot find components.1 in test project", file1 != null && file1.exists());
		
		try{
			file.setContents(file1.getContents(), false, false, new NullProgressMonitor());
		}catch(Exception ex){
			JUnitUtils.fail("Cannot read file WebContent/WEB-INF/components.1", ex);
		}
		
		try {
			project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
		} catch (Exception e) {
			JUnitUtils.fail("Cannot build test Project", e);
		}
		
		seamPackage = findSeamPackage(tree, "myPackage");
		assertTrue("Package \"myPackage\" not found!",seamPackage!=null);
		
		if(seamPackage != null){
			ISeamComponent component = findSeamComponent(seamPackage, "myPackage.myStringComponent");
			assertTrue("Component \"myPackage.myStringComponent\" not found!",component!=null);
		}

	}

	public void renameComponent(){
		CommonNavigator navigator = getSeamComponentsView();
		navigator.getCommonViewer().expandAll();
		
		Tree tree = navigator.getCommonViewer().getTree();

		ISeamPackage seamPackage = findSeamPackage(tree, "myPackage");
		assertTrue("Package \"myPackage\" not found!",seamPackage!=null);
		
		if(seamPackage != null){
			ISeamComponent component = findSeamComponent(seamPackage, "myPackage.myStringComponent");
			assertTrue("Component \"myPackage.myStringComponent\" not found!",component!=null);
		}
		
		IFile file1 = project.getFile("WebContent/WEB-INF/components.2");
		assertTrue("Cannot find components.2 in test project", file1 != null && file1.exists());
		
		try{
			file.setContents(file1.getContents(), false, false, new NullProgressMonitor());
		}catch(Exception ex){
			JUnitUtils.fail("Cannot read file WebContent/WEB-INF/components.2", ex);
		}
		
		try {
			project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
		} catch (Exception e) {
			JUnitUtils.fail("Cannot build test Project", e);
		}
		
		seamPackage = findSeamPackage(tree, "myPackage");
		assertTrue("Package \"myPackage\" not found!",seamPackage!=null);
		
		if(seamPackage != null){
			ISeamComponent component = findSeamComponent(seamPackage, "myPackage.myTextComponent");
			assertTrue("Component \"myPackage.myTextComponent\" not found!",component!=null);
		}
		
		IFile file2 = project.getFile("WebContent/WEB-INF/components.3");
		assertTrue("Cannot find components.3 in test project", file2 != null && file2.exists());
		
		try{
			file.setContents(file2.getContents(), false, false, new NullProgressMonitor());
		}catch(Exception ex){
			JUnitUtils.fail("Cannot read file WebContent/WEB-INF/components.3", ex);
		}
		
		try {
			project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
		} catch (Exception e) {
			JUnitUtils.fail("Cannot build test Project", e);
		}
		
		seamPackage = findSeamPackage(tree, "myNewPackage");
		assertTrue("Package \"myNewPackage\" not found!",seamPackage!=null);
		
		if(seamPackage != null){
			ISeamComponent component = findSeamComponent(seamPackage, "myNewPackage.myTextComponent");
			assertTrue("Component \"myNewPackage.myTextComponent\" not found!",component!=null);
		}

	}
	
	public void deleteComponent(){
		CommonNavigator navigator = getSeamComponentsView();
		navigator.getCommonViewer().expandAll();
		
		Tree tree = navigator.getCommonViewer().getTree();
		
//		System.out.println("tree.getItemCount() - "+tree.getItemCount());
//		for(int i=0;i<tree.getItemCount();i++){
//			showTreeItem(tree.getItem(i),0);
//		}

		ISeamPackage seamPackage = findSeamPackage(tree, "myNewPackage");
		assertTrue("Package \"myNewPackage\" not found!",seamPackage!=null);
		
		if(seamPackage != null){
			ISeamComponent component = findSeamComponent(seamPackage, "myNewPackage.myTextComponent");
			assertTrue("Component \"myNewPackage.myTextComponent\" not found!",component!=null);
		}
		
		IFile file1 = project.getFile("WebContent/WEB-INF/components.4");
		assertTrue("Cannot find components.2 in test project", file1 != null && file1.exists());
		
		try{
			file.setContents(file1.getContents(), false, false, new NullProgressMonitor());
		}catch(Exception ex){
			JUnitUtils.fail("Cannot read file WebContent/WEB-INF/components.4", ex);
		}
		
		try {
			project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
		} catch (Exception e) {
			JUnitUtils.fail("Cannot build test Project", e);
		}
		
//		System.out.println("Before!");
//		
//		waitForJobs();
//		
//		System.out.println("After!");
		
//		System.out.println("tree.getItemCount() - "+tree.getItemCount());
//		for(int i=0;i<tree.getItemCount();i++){
//			showTreeItem(tree.getItem(i),0);
//		}

		seamPackage = findSeamPackage(tree, "myNewPackage");
		assertTrue("Package \"myNewPackage\" found!",seamPackage==null);

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
		/*try {
			XJob.waitForJob();
		} catch (InterruptedException e) {
			JUnitUtils.fail(e.getMessage(),e);
		}*/
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
		System.out.println("Item "+item.getData());
		if(item.getData() instanceof ISeamScope){
			ISeamScope scope = (ISeamScope)item.getData();
			Iterator<ISeamPackage> iter = scope.getAllPackages().iterator();
			while(iter.hasNext())
				showSeamPackage(iter.next(), level+1);
			
			
			List<ISeamComponent> components = scope.getComponents();
			for(int i=0;i<components.size();i++)
				showSeamComponent(components.get(i), level+1);
		}
		
		for(int i=0;i<item.getItemCount();i++){
			showTreeItem(item.getItem(i),level+1);
		}
	}

	private void showSeamPackage(ISeamPackage seamPackage, int level){
		for(int i=0;i<level;i++)
			System.out.print("-");
		
		System.out.println("Package - "+seamPackage.getName()+" "+seamPackage.getQualifiedName());
		
		Iterator<ISeamComponent> iter = seamPackage.getComponents().iterator();
		while(iter.hasNext())
			showSeamComponent(iter.next(), level+1);
	}

	private void showSeamComponent(ISeamComponent component, int level){
		for(int i=0;i<level;i++)
			System.out.print("-");
		
		System.out.println("Component - "+component.getName()+" "+component.getClassName());
	}
	
	private ISeamComponent findSeamComponent(ISeamPackage seamPackage, String name){
		ISeamComponent component=null;
		
		Iterator<ISeamComponent> iter = seamPackage.getComponents().iterator();
		while(iter.hasNext()){
			component = iter.next();
			if(component.getName().equals(name)) return component;
		}
		
		return null;
	}

	private ISeamPackage findSeamPackage(ISeamScope seamScope, String name){
		ISeamPackage seamPackage=null;
		
		Iterator<ISeamPackage> iter = seamScope.getAllPackages().iterator();
		while(iter.hasNext()){
			seamPackage = iter.next();
			if(seamPackage.getName().equals(name)) return seamPackage;
		}
		
		return null;
	}

	private ISeamPackage findSeamPackage(TreeItem item, String name){
		ISeamPackage seamPackage=null;
		
		if(item.getData() instanceof ISeamScope){
			seamPackage = findSeamPackage((ISeamScope)item.getData(), name);
			if(seamPackage != null) return seamPackage;
		}
		for(int i=0;i<item.getItemCount();i++){
			seamPackage = findSeamPackage(item.getItem(i), name);
			if(seamPackage != null) return seamPackage;
		}
		
		return null;
	}
	
	private ISeamPackage findSeamPackage(Tree tree, String name){
		ISeamPackage seamPackage=null;
		TreeItem item;
		
		for(int i=0;i<tree.getItemCount();i++){
			item = tree.getItem(i);
			seamPackage = findSeamPackage(item, name);
			if(seamPackage != null) return seamPackage;
		}
		
		return null;
	}
	
	public void waitForJobs() {
		while (Job.getJobManager().currentJob() != null)
			delay(10000);
	}
	
	/** * Process UI input but do not return for the 
	 * specified time interval. *
	 * @param waitTimeMillis the number of milliseconds */ 
	protected void delay(long waitTimeMillis) {
		Display display = Display.getCurrent();
		// If this is the UI thread,
		// then process input.
		if (display != null) {
			long endTimeMillis = System.currentTimeMillis() + waitTimeMillis;
			while (System.currentTimeMillis() < endTimeMillis){
				if (!display.readAndDispatch()) display.sleep();
			} display.update();
		}
		// Otherwise, perform a simple sleep.
		else {
			try {
				Thread.sleep(waitTimeMillis);
			} catch (InterruptedException e) {
					// Ignored.
			}
		}
	}
}
