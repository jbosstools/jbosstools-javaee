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
import org.eclipse.ui.internal.decorators.DecoratorManager;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.progress.UIJob;
import org.jboss.tools.common.model.XJob;
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
	IFile componentsFile;
	IFile classFile;
	
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
		componentsFile = project.getFile("WebContent/WEB-INF/components.xml");
		assertTrue("Cannot find components.xml in test project", componentsFile != null && componentsFile.exists());
	}
	
	public void testAddComponentInXmlFile(){
		SeamCorePlugin.getSeamProject(project, true);
		
		refreshProject(project);

		CommonNavigator navigator = getSeamComponentsView();

		navigator.getCommonViewer().expandAll();
		
		Tree tree = navigator.getCommonViewer().getTree();
		
		updateTree(tree);
		
		ISeamPackage seamPackage = findSeamPackage(tree, "myPackage");
		
		assertTrue("Error in initial loading components from components.xml. " +
				"Unexpected package 'myPackage' was found",seamPackage==null);
		
		IFile file1 = project.getFile("WebContent/WEB-INF/components.1");
		if(file1 == null || !file1.exists()) {
			fail("Cannot find test data file 'WebContent/WEB-INF/components.1'");
		}


		try {
			componentsFile.setContents(file1.getContents(), 
										true, false, new NullProgressMonitor());
			componentsFile.touch(new NullProgressMonitor());
		} catch (CoreException e) {
			JUnitUtils.fail("Error in changing 'components.xml' content to " +
					"'WebContent/WEB-INF/components.1'", e);
		}
		
		refreshProject(project);

		updateTree(tree);

		seamPackage = findSeamPackage(tree, "myPackage");
		assertTrue("Seam model is not updated, expected package 'myPackage'" +
				" is not found in tree",seamPackage!=null);

		ISeamComponent component = findSeamComponent(seamPackage, 
												"myPackage.myStringComponent");
		
		assertTrue("Expected component 'myPackage.myStringComponent' was not" +
													 " found",component!=null);
		
	}

	public void testRenameComponentInXmlFile(){
		
		CommonNavigator navigator = getSeamComponentsView();
		navigator.getCommonViewer().expandAll();
		
		Tree tree = navigator.getCommonViewer().getTree();
		updateTree(tree);

		ISeamPackage seamPackage = findSeamPackage(tree, "myPackage");
		
		assertTrue("Expected package 'myPackage' was not found it tree",
															seamPackage!=null);
		
		ISeamComponent component = findSeamComponent(seamPackage, 
												"myPackage.myStringComponent");
		
		assertTrue("Expected component 'myPackage.myStringComponent' was not" +
				 " found",component!=null);
				
		IFile file1 = project.getFile("WebContent/WEB-INF/components.2");
		if(file1 == null || !file1.exists()) {
			fail("Cannot find test data file 'WebContent/WEB-INF/components.2'");
		}		

		try{
			componentsFile.setContents(file1.getContents(), true, false, new NullProgressMonitor());
			componentsFile.touch(new NullProgressMonitor());
		}catch(Exception ex){
			JUnitUtils.fail("Error in changing 'components.xml' content to " +
					"'WebContent/WEB-INF/components.2'", ex);
		}
		
		refreshProject(project);
		
		updateTree(tree);
		
		seamPackage = findSeamPackage(tree, "myPackage");
		assertTrue("Expected package 'myPackage' was not found it tree",
				seamPackage!=null);
		
		component = findSeamComponent(seamPackage, "myPackage.myTextComponent");
		assertTrue("Expected component 'myPackage.myTextComponent' not found " +
				"after renaming",component!=null);
		
		file1 = project.getFile("WebContent/WEB-INF/components.3");
		if(file1 == null || !file1.exists()) {
			fail("Cannot find test data file 'WebContent/WEB-INF/components.3'");
		}		
		try{
			componentsFile.setContents(file1.getContents(), true, false, new NullProgressMonitor());
			componentsFile.touch(new NullProgressMonitor());
		}catch(Exception ex){
			JUnitUtils.fail("Error in changing 'components.xml' content to " +
					"'WebContent/WEB-INF/components.3'", ex);
		}
		
		refreshProject(project);
		
		updateTree(tree);
		
		seamPackage = findSeamPackage(tree, "myNewPackage");
		assertTrue("Expected package 'myNewPackage' was not found it tree after " +
				"renaming",
				seamPackage!=null);		
		
		component = findSeamComponent(seamPackage, "myNewPackage.myTextComponent");
		assertTrue("Expected component 'myNewPackage.myTextComponent' not found " +
				"after renaming",component!=null);
	}
	
	public void testDeleteComponentInXmlFile(){
		
		CommonNavigator navigator = getSeamComponentsView();
		navigator.getCommonViewer().expandAll();

		Tree tree = navigator.getCommonViewer().getTree();
		
		updateTree(tree);

		ISeamPackage seamPackage = findSeamPackage(tree, "myNewPackage");
		assertTrue("Package \"myNewPackage\" not found!",seamPackage!=null);
		
		ISeamComponent component = findSeamComponent(seamPackage, "myNewPackage.myTextComponent");
		assertTrue("Component \"myNewPackage.myTextComponent\" not found!",component!=null);
		
		IFile file1 = project.getFile("WebContent/WEB-INF/components.4");
		assertTrue("Cannot find components.2 in test project", file1 != null && file1.exists());
		
		try{
			componentsFile.setContents(file1.getContents(), true, false, new NullProgressMonitor());
			componentsFile.touch(new NullProgressMonitor());
		}catch(Exception ex){
			JUnitUtils.fail("Cannot read file WebContent/WEB-INF/components.4", ex);
		}
		
		refreshProject(project);
		
		updateTree(tree);
		
		seamPackage = findSeamPackage(tree, "myNewPackage");
		assertTrue("Package \"myNewPackage\" found!",seamPackage==null);

	}
	
	public void testAddComponentInClass(){
	
		classFile = project.getFile("JavaSource/demo/Person.java");
		assertTrue("Cannot find Person.java in test project", componentsFile != null);
		
		CommonNavigator navigator = getSeamComponentsView();
		navigator.getCommonViewer().expandAll();
		
		Tree tree = navigator.getCommonViewer().getTree();

		ISeamPackage seamPackage = findSeamPackage(tree, "demo");
		assertTrue("Package \"demo\" found!",seamPackage==null);
		
		IFile file1 = project.getFile("JavaSource/demo/Person.1");
		assertTrue("Cannot find Person.1 in test project", file1 != null && file1.exists());
		
		try{
			classFile.create(file1.getContents(), false, new NullProgressMonitor());
			classFile.touch(new NullProgressMonitor());
		}catch(Exception ex){
			JUnitUtils.fail("Cannot read file JavaSource/demo/Person.1", ex);
		}
		
		refreshProject(project);
		
		updateTree(tree);
		
		seamPackage = findSeamPackage(tree, "demo");
		assertTrue("Package \"demo\" not found!",seamPackage!=null);
		
		ISeamComponent component = findSeamComponent(seamPackage, "demo.John");
		assertTrue("Component \"demo.John\" not found!",component!=null);


	}
	
	public void testRenameComponentInClass(){
	
		classFile = project.getFile("JavaSource/demo/Person.java");
		
		CommonNavigator navigator = getSeamComponentsView();
		navigator.getCommonViewer().expandAll();
		
		Tree tree = navigator.getCommonViewer().getTree();
		
		ISeamPackage seamPackage = findSeamPackage(tree, "demo");
		assertTrue("Package \"demo\" not found!",seamPackage!=null);
		
		ISeamComponent component = findSeamComponent(seamPackage, "demo.John");
		assertTrue("Component \"demo.John\" not found!",component!=null);
		
		IFile file1 = project.getFile("JavaSource/demo/Person.2");
		assertTrue("Cannot find Person.2 in test project", file1 != null && file1.exists());
		
		try{
			classFile.setContents(file1.getContents(), true, false, new NullProgressMonitor());
			classFile.touch(new NullProgressMonitor());
		}catch(Exception ex){
			JUnitUtils.fail("Cannot read file JavaSource/demo/Person.2", ex);
		}
		
		refreshProject(project);
		
		updateTree(tree);
		
		seamPackage = findSeamPackage(tree, "demo");
		assertTrue("Package \"demo\" not found!",seamPackage!=null);

		component = findSeamComponent(seamPackage, "demo.John");
		assertTrue("Component \"demo.John\" found!",component==null);
			
		component = findSeamComponent(seamPackage, "demo.Pall");
		assertTrue("Component \"demo.Pall\" not found!",component!=null);
		
		IFile file2 = project.getFile("JavaSource/demo/Person.3");
		assertTrue("Cannot find Person.3 in test project", file2 != null && file2.exists());
		
		try{
			classFile.setContents(file2.getContents(), true, false, new NullProgressMonitor());
			classFile.touch(new NullProgressMonitor());
		}catch(Exception ex){
			JUnitUtils.fail("Cannot read file JavaSource/demo/Person.3", ex);
		}
		
		refreshProject(project);
		
		updateTree(tree);
		
		seamPackage = findSeamPackage(tree, "demo");
		assertTrue("Package \"demo\" found!",seamPackage==null);
		
		seamPackage = findSeamPackage(tree, "beatles");
		assertTrue("Package \"beatles\" not found!",seamPackage!=null);
		
		component = findSeamComponent(seamPackage, "beatles.Pall");
		assertTrue("Component \"beatles.Pall\" not found!",component!=null);
	}
	
	public void testDeleteComponentInClass(){
		CommonNavigator navigator = getSeamComponentsView();
		navigator.getCommonViewer().expandAll();
		
		Tree tree = navigator.getCommonViewer().getTree();
		
		ISeamPackage seamPackage = findSeamPackage(tree, "beatles");
		assertTrue("Package \"beatles\" not found!",seamPackage!=null);
		
		if(seamPackage != null){
			ISeamComponent component = findSeamComponent(seamPackage, "beatles.Pall");
			assertTrue("Component \"beatles.Pall\" not found!",component!=null);
		}
		
		try{
			classFile.delete(true, new NullProgressMonitor());
		}catch(Exception ex){
			JUnitUtils.fail("Cannot delete file JavaSource/demo/Person.java", ex);
		}
		
		refreshProject(project);
		
		updateTree(tree);
		
		seamPackage = findSeamPackage(tree, "beatles");
		assertTrue("Package \"beatles\" found!",seamPackage==null);
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
		
		for(int i=0;i<item.getItemCount();i++){
			TreeItem cur = item.getItem(i);
			if(cur.getData() instanceof ISeamPackage) {
				ISeamPackage pkg =(ISeamPackage)cur.getData();
				if(name.equals(pkg.getName())) {
					seamPackage = pkg;
					break;
				}
			}else {
				seamPackage = findSeamPackage(cur, name);
			}
		}
		
		return seamPackage;
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
	
	private static final int NUMBER_OF_REFRESHES = 1;
	
	private void refreshProject(IProject project){
		long timestamp = project.getModificationStamp();
		int count = 1;
		while(true){
			System.out.println("Refresh project "+count);
			try {
				project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
//				waitForJobs();
//				try {
//					waitForJob();
//				} catch (InterruptedException e) {
//					JUnitUtils.fail(e.getMessage(),e);
//				}
//				project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
				//waitForJobs();
				try {
					waitForJob();
				} catch (InterruptedException e) {
					JUnitUtils.fail(e.getMessage(),e);
				}
			} catch (Exception e) {
				JUnitUtils.fail("Cannot build test Project", e);
				break;
			}
			if(project.getModificationStamp() != timestamp) break;
			count++;
			if(count > NUMBER_OF_REFRESHES) break;
		}
	}
	
	public void waitForJobs() {
		while (Job.getJobManager().currentJob() != null)
			delay(5000);
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
	public static void waitForJob() throws InterruptedException {
		Object[] o = {
			XJob.FAMILY_XJOB, ResourcesPlugin.FAMILY_AUTO_REFRESH, ResourcesPlugin.FAMILY_AUTO_BUILD
		};
		while(true) {
			boolean stop = true;
			for (int i = 0; i < o.length; i++) {
				Job[] js = Job.getJobManager().find(o[i]);
				if(js != null && js.length > 0) {
					Job.getJobManager().join(o[i], new NullProgressMonitor());
					stop = false;
				}
			}
			if(stop) {
				Job running = getJobRunning(10);
				if(running != null) {
					running.join();
					stop = false;
				}
			}
			if(stop) break;
		}
	}
	
	public static Job getJobRunning(int iterationLimit) {
		Job[] js = Job.getJobManager().find(null);
		Job dm = null;
		if(js != null) for (int i = 0; i < js.length; i++) {
			if(js[i].getState() == Job.RUNNING && js[i].getThread() != Thread.currentThread()) {
				if(js[i] instanceof UIJob) continue;
				if(js[i].belongsTo(DecoratorManager.FAMILY_DECORATE) || js[i].getName().equals("Task List Saver")) {
					dm = js[i];
					continue;
				}
				//TODO keep watching 
				System.out.println(js[i].getName());
				return js[i];
			}
		}
		if(dm != null) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				//ignore
			}
			if(iterationLimit > 0)
				return getJobRunning(iterationLimit - 1);
		}
		return null;
		
	}

	public void updateTree(Tree tree) {
		for(int i=0;i<tree.getItemCount();i++){
			showTreeItem(tree.getItem(i),0);
		}

	}
}
