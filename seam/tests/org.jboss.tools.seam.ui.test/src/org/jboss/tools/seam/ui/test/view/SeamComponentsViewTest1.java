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

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.internal.decorators.DecoratorManager;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.IExtensionStateModel;
import org.eclipse.ui.progress.UIJob;
import org.jboss.tools.common.model.XJob;
import org.jboss.tools.common.util.ResourcesUtils;
import org.jboss.tools.common.util.WorkbenchUtils;
import org.jboss.tools.jst.web.ui.WebDevelopmentPerspectiveFactory;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamPackage;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.ui.ISeamUiConstants;
import org.jboss.tools.seam.ui.views.ViewConstants;
import org.jboss.tools.test.util.JUnitUtils;

/**
 * 
 * @author eskimo
 *
 */
public class SeamComponentsViewTest1 extends TestCase {
	IProject project;
	IFile componentsFile;
	IFile classFile;
	Tree tree;
	CommonNavigator navigator;
	CommonViewer viewer;
	IExtensionStateModel m2;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Job.getJobManager().addJobChangeListener(new IJobChangeListener() {

			public void aboutToRun(IJobChangeEvent event) {
				System.out.println(event.getJob().getName());
				
			}

			public void awake(IJobChangeEvent event) {
				// TODO Auto-generated method stub
				
			}

			public void done(IJobChangeEvent event) {

				
			}

			public void running(IJobChangeEvent event) {
				// TODO Auto-generated method stub
				
			}

			public void scheduled(IJobChangeEvent event) {
				// TODO Auto-generated method stub
				
			}

			public void sleeping(IJobChangeEvent event) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		ResourcesPlugin.getWorkspace().addResourceChangeListener(new IResourceChangeListener() {
			/* (non-Javadoc)
			 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
			 */
			public void resourceChanged(IResourceChangeEvent event) {
				System.out.println(event.getResource().getLocation().toString());
			}
		});
		WorkbenchUtils.getWorkbench().showPerspective(
				WebDevelopmentPerspectiveFactory.PERSPECTIVE_ID,
				WorkbenchUtils.getWorkbench().getActiveWorkbenchWindow());
		
		project = (IProject)ResourcesPlugin.getWorkspace().getRoot().findMember("TestComponentView");
		
		if(project==null) {	
			project = ResourcesUtils.importProject(Platform.getBundle("org.jboss.tools.seam.ui.test"), "/projects/TestComponentView", new NullProgressMonitor());
		}
		navigator = getSeamComponentsView();
		viewer = navigator.getCommonViewer();
		tree = viewer.getTree();

		m2 = navigator.getNavigatorContentService().findStateModel("org.jboss.tools.seam.ui.views.rootContent");
		
		componentsFile = project.getFile("WebContent/WEB-INF/components.xml");
		assertTrue("Cannot find components.xml in test project", componentsFile != null && componentsFile.exists());
	
	}
	
	public void testFlatSeamPackages() throws InterruptedException{
		m2.setBooleanProperty(ViewConstants.PACKAGE_STRUCTURE, true);
		updateView();
		JobUtils.waitUsersJobsAreFinished();
		viewer.expandAll();
		for (Object viewObject : viewer.getExpandedElements()) {
			if(viewObject instanceof ISeamPackage) {
				System.out.println(((ISeamPackage)viewObject).getQualifiedName());
			}
		}
		ISeamPackage seamPackage = findSeamPackage(tree, "package1");
		assertTrue("Expected package 'package1' was not" +
				 " found",seamPackage!=null);
		
		seamPackage = findSeamPackage(tree, "package1.package2");	
		assertTrue("Expected package 'package1.package2' was not" +
				 " found",seamPackage!=null);
		
		seamPackage = findSeamPackage(tree, "package1.package2.package3");	
		assertTrue("Expected package 'package1.package2.package3' was not" +
				 " found",seamPackage!=null);
		
		seamPackage = findSeamPackage(tree, "package1.package2.package3.package4");
		assertTrue("Expected package 'package1.package2.package3.package4' was not" +
				 " found",seamPackage!=null);
	}

//	public void testHierarchicalSeamPackages() throws InterruptedException{
//		m2.setBooleanProperty(ViewConstants.PACKAGE_STRUCTURE, false);
//		m2.setBooleanProperty(ViewConstants.SCOPE_PRESENTATION, false);
//		updateView();
//		JobUtils.waitUsersJobsAreFinished();
//		ISeamPackage seamPackage = findSeamPackage(tree, "package4");
//		assertTrue("Expected package 'package4' was not" +
//				 " found",seamPackage!=null);
//		
//		seamPackage = (ISeamPackage)seamPackage.getParent();
//		assertTrue("For 'package5' expected parent is 'package3' not '" + 
//				seamPackage.getName() +"'", "package3".equals(seamPackage.getName()));
//	
//		seamPackage = findSeamPackage(tree, "package3");
//		assertTrue("Expected package 'package3' was not" +
//				 " found",seamPackage!=null);
//		
//		seamPackage = (ISeamPackage)seamPackage.getParent();
//		assertTrue("For 'package3' expected parent is 'package2' not '" + 
//				seamPackage.getName() +"'", "package2".equals(seamPackage.getName()));
//		
//		seamPackage = findSeamPackage(tree, "package2");
//		assertTrue("Expected package 'package2' was not" +
//				 " found",seamPackage!=null);
//		
//		seamPackage = (ISeamPackage)seamPackage.getParent();
//		assertTrue("For 'package2' expected parent is 'package1' not '" + 
//				seamPackage.getName() +"'", "package1".equals(seamPackage.getName()));
//	}
//	
//	public void testAddComponentInXmlFile() throws InterruptedException{
//		
//		IFile file1 = project.getFile("WebContent/WEB-INF/components.1");
//		if(file1 == null || !file1.exists()) {
//			fail("Cannot find test data file 'WebContent/WEB-INF/components.1'");
//		}
//
//		try {
//			System.out.println("--------------------Before set content");
//			componentsFile.setContents(file1.getContents(), 
//										true, false, new NullProgressMonitor());
//			System.out.println("--------------------After set content");
//			componentsFile.touch(new NullProgressMonitor());
//			System.out.println("--------------------After touch");
//		} catch (CoreException e) {
//			JUnitUtils.fail("Error in changing 'components.xml' content to " +
//					"'WebContent/WEB-INF/components.1'", e);
//		}
//		JobUtils.waitUsersJobsAreFinished();
//		updateView();
//
//		ISeamPackage seamPackage = findSeamPackage(tree, "myPackage");
//		assertTrue("Seam model is not updated, expected package 'myPackage'" +
//				" is not found in tree",seamPackage!=null);
//
//		ISeamComponent component = findSeamComponent(seamPackage, 
//												"myPackage.myComponent");
//		
//		assertTrue("Expected component 'myPackage.myStringComponent' was not" +
//													 " found",component!=null);
//	}
//
//	public void testRenameComponentInXmlFile(){
//		m2.setBooleanProperty(ViewConstants.PACKAGE_STRUCTURE, true);
//		
//		IFile file1 = project.getFile("WebContent/WEB-INF/components.2");
//		if(file1 == null || !file1.exists()) {
//			fail("Cannot find test data file 'WebContent/WEB-INF/components.2'");
//		}		
//
//		try{
//			System.out.println("--------------------Before set content");
//			componentsFile.setContents(file1.getContents(), true, false, new NullProgressMonitor());
//			System.out.println("--------------------After set content");
//			componentsFile.touch(new NullProgressMonitor());
//			System.out.println("--------------------After set content");
//		}catch(Exception ex){
//			JUnitUtils.fail("Error in changing 'components.xml' content to " +
//					"'WebContent/WEB-INF/components.2'", ex);
//		}
//		
//		ISeamPackage seamPackage = findSeamPackage(tree, "myRenamedPackage");
//		assertTrue("Expected package 'myRenamedPackage' was not found it tree",
//				seamPackage!=null);
//		
//		ISeamComponent component = findSeamComponent(seamPackage, "myRenamedPackage.myRenamedComponent");
//		assertTrue("Expected component 'myRenamedPackage.myRenamedComponent' not found " +
//				"after renaming",component!=null);
//		
//		seamPackage = findSeamPackage(tree, "package4");
//		assertTrue("Expected package 'myPackage4' was not found it tree",
//				seamPackage!=null);
//		
//		component = findSeamComponent(seamPackage, "package4.component4");
//		assertTrue("Expected component 'package4.component4' not found " +
//				"after renaming",component!=null);
//		
//		seamPackage = findSeamPackage(tree, "package4.package3");
//		assertTrue("Expected package 'package4.package3' was not found it tree",
//				seamPackage!=null);
//		
//		component = findSeamComponent(seamPackage, "package4.package3.component3");
//		assertTrue("Expected component 'package4.package3.component3' not found " +
//				"after renaming",component!=null);
//		
//		seamPackage = findSeamPackage(tree, "package4.package3.package2");
//		assertTrue("Expected package 'package4.package3.package2' was not found it tree",
//				seamPackage!=null);
//		
//		component = findSeamComponent(seamPackage, "package4.package3.package2.component2");
//		assertTrue("Expected component 'package4.package3.package2.component2' not found " +
//				"after renaming",component!=null);
//		
//		seamPackage = findSeamPackage(tree, "package4.package3.package2.package1");
//		assertTrue("Expected package 'package4.package3.package2.package1' was not found it tree",
//				seamPackage!=null);
//		
//		component = findSeamComponent(seamPackage, "package4.package3.package2.package1.component1");
//		assertTrue("Expected component 'package4.package3.package2.package1.component1' not found " +
//				"after renaming",component!=null);
//	
//	}
	
//	public void testDeleteComponentInXmlFile(){
//		
//		IFile file1 = project.getFile("WebContent/WEB-INF/components.3");
//		assertTrue("Cannot find components.2 in test project", file1 != null && file1.exists());
//		
//		try{
//			System.out.println("--------------------Before set content");
//			componentsFile.setContents(file1.getContents(), true, false, new NullProgressMonitor());
//			System.out.println("--------------------After set content");
//			componentsFile.touch(new NullProgressMonitor());
//			System.out.println("--------------------After touch");
//		}catch(Exception ex){
//			JUnitUtils.fail("Cannot read file WebContent/WEB-INF/components.3", ex);
//		}
//		
//		refreshProject(project);
//		
//		ISeamPackage seamPackage = findSeamPackage(tree, "package4");
//		assertTrue("Expected package 'myPackage4' is expected to be deleted",
//				seamPackage==null);
//		
//		seamPackage = findSeamPackage(tree, "package4.package3");
//		assertTrue("Expected package 'package4.package3' is expected to be deleted",
//				seamPackage==null);
//		
//		seamPackage = findSeamPackage(tree, "package4.package3.package2");
//		assertTrue("Expected package 'package4.package3.package2' is expected to be deleted",
//				seamPackage==null);
//		
//		seamPackage = findSeamPackage(tree, "package4.package3.package2.package1");
//		assertTrue("Expected package 'package4.package3.package2.package1' is expected to be deleted",
//				seamPackage==null);
//	
//	}

//	public void testAddComponentInClass(){
//	
//		classFile = project.getFile("JavaSource/demo/Person.java");
//		assertTrue("Cannot find Person.java in test project", componentsFile != null);
//		
//		CommonNavigator navigator = getSeamComponentsView();
//		navigator.getCommonViewer().expandAll();
//		
//		Tree tree = navigator.getCommonViewer().getTree();
//
//		ISeamPackage seamPackage = findSeamPackage(tree, "demo");
//		assertTrue("Package \"demo\" found!",seamPackage==null);
//		
//		IFile file1 = project.getFile("JavaSource/demo/Person.1");
//		assertTrue("Cannot find Person.1 in test project", file1 != null && file1.exists());
//		
//		try{
//			classFile.create(file1.getContents(), false, new NullProgressMonitor());
//			classFile.touch(new NullProgressMonitor());
//		}catch(Exception ex){
//			JUnitUtils.fail("Cannot read file JavaSource/demo/Person.1", ex);
//		}
//		
//		refreshProject(project);
//		
//		seamPackage = findSeamPackage(tree, "demo");
//		assertTrue("Package \"demo\" not found!",seamPackage!=null);
//		
//		ISeamComponent component = findSeamComponent(seamPackage, "demo.John");
//		assertTrue("Component \"demo.John\" not found!",component!=null);
//	}

//	public void testRenameComponentInClass(){
//	
//		classFile = project.getFile("JavaSource/demo/Person.java");
//		
//		CommonNavigator navigator = getSeamComponentsView();
//		navigator.getCommonViewer().expandAll();
//		
//		Tree tree = navigator.getCommonViewer().getTree();
//		
//		ISeamPackage seamPackage = findSeamPackage(tree, "demo");
//		assertTrue("Package \"demo\" not found!",seamPackage!=null);
//		
//		ISeamComponent component = findSeamComponent(seamPackage, "demo.John");
//		assertTrue("Component \"demo.John\" not found!",component!=null);
//		
//		IFile file1 = project.getFile("JavaSource/demo/Person.2");
//		assertTrue("Cannot find Person.2 in test project", file1 != null && file1.exists());
//		
//		try{
//			classFile.setContents(file1.getContents(), true, false, new NullProgressMonitor());
//			classFile.touch(new NullProgressMonitor());
//		}catch(Exception ex){
//			JUnitUtils.fail("Cannot read file JavaSource/demo/Person.2", ex);
//		}
//		
//		refreshProject(project);
//		
//		seamPackage = findSeamPackage(tree, "demo");
//		assertTrue("Package \"demo\" not found!",seamPackage!=null);
//
//		component = findSeamComponent(seamPackage, "demo.John");
//		assertTrue("Component \"demo.John\" found!",component==null);
//			
//		component = findSeamComponent(seamPackage, "demo.Pall");
//		assertTrue("Component \"demo.Pall\" not found!",component!=null);
//		
//		IFile file2 = project.getFile("JavaSource/demo/Person.3");
//		assertTrue("Cannot find Person.3 in test project", file2 != null && file2.exists());
//		
//		try{
//			classFile.setContents(file2.getContents(), true, false, new NullProgressMonitor());
//			
//			classFile.touch(new NullProgressMonitor());
//		}catch(Exception ex){
//			JUnitUtils.fail("Cannot read file JavaSource/demo/Person.3", ex);
//		}
//		
//		refreshProject(project);
//		
//		seamPackage = findSeamPackage(tree, "demo");
//		assertTrue("Package \"demo\" found!",seamPackage==null);
//		
//		seamPackage = findSeamPackage(tree, "beatles");
//		assertTrue("Package \"beatles\" not found!",seamPackage!=null);
//		
//		component = findSeamComponent(seamPackage, "beatles.Pall");
//		assertTrue("Component \"beatles.Pall\" not found!",component!=null);
//	}

//	public void testDeleteComponentInClass(){
//		classFile = project.getFile("JavaSource/demo/Person.java");
//		
//		CommonNavigator navigator = getSeamComponentsView();
//		navigator.getCommonViewer().expandAll();
//		
//		Tree tree = navigator.getCommonViewer().getTree();
//		
//		ISeamPackage seamPackage = findSeamPackage(tree, "beatles");
//		assertTrue("Package \"beatles\" not found!",seamPackage!=null);
//		
//		if(seamPackage != null){
//			ISeamComponent component = findSeamComponent(seamPackage, "beatles.Pall");
//			assertTrue("Component \"beatles.Pall\" not found!",component!=null);
//		}
//		
//		try{
//			classFile.delete(true, new NullProgressMonitor());
//		}catch(Exception ex){
//			JUnitUtils.fail("Cannot delete file JavaSource/demo/Person.java", ex);
//		}
//		
//		refreshProject(project);
//		
//		seamPackage = findSeamPackage(tree, "beatles");
//		assertTrue("Package \"beatles\" found!",seamPackage==null);
//	}

//	/**
//	 * 
//	 */
	public void testSeamComponentsViewIsShowedOnPerspective() {
		IWorkbenchPage page  = WorkbenchUtils.getWorkbenchActivePage();
		IViewPart part = page.findView(ISeamUiConstants.SEAM_COMPONENTS_VIEW_ID);
		assertNotNull("Cannot show the Seam Components View", part);
	}

//	public void testCreatedProjectIsShownOnTree() {
//		TestProjectProvider provider=null;
//		try {
//			provider = new TestProjectProvider("org.jboss.tools.seam.ui.test", null, "TestComponentView", true);
//		} catch (Exception e1) {
//			JUnitUtils.fail("Cannot create Project Provider", e1);
//		} 
//		IProject project = provider.getProject();
//		try {
//			project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
//		} catch (Exception e) {
//			JUnitUtils.fail("Cannot refresh created test Project", e);
//		}
//		IStructuredContentProvider content 
//		      = (IStructuredContentProvider)getSeamComponentsView().getCommonViewer().getContentProvider();
//		assertTrue("Created Seam enabled project haven't been shown in tree",1==content.getElements(ResourcesPlugin.getWorkspace().getRoot()).length);		
//		
//	}
//	
//	public void testThatDeletedProjectIsDisappearedFromTree() {
//		try {
//			ResourcesPlugin.getWorkspace().getRoot().findMember("TestComponentView").delete(true, new NullProgressMonitor());
//		} catch (CoreException e) {
//			JUnitUtils.fail(e.getMessage(),e);
//		}	
//		IStructuredContentProvider content 
//	      = (IStructuredContentProvider)getSeamComponentsView().getCommonViewer().getContentProvider();
//			assertTrue("Created Seam enabled project haven't been deleted from tree",0==content.getElements(ResourcesPlugin.getWorkspace().getRoot()).length);
//	}
	
	private CommonNavigator getSeamComponentsView() {
		IWorkbenchPage page  = WorkbenchUtils.getWorkbenchActivePage();
		CommonNavigator part = (CommonNavigator)page.findView(ISeamUiConstants.SEAM_COMPONENTS_VIEW_ID);
		return part;
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

	private ISeamPackage findSeamPackage(TreeItem item, String name){
		ISeamPackage seamPackage=null;
		
		for(int i=0;i<item.getItemCount();i++){
			TreeItem cur = item.getItem(i);
			if(cur.getData() instanceof ISeamPackage) {
				ISeamPackage pkg =(ISeamPackage)cur.getData();
				if(m2.getBooleanProperty(ViewConstants.PACKAGE_STRUCTURE) 
						&& name.equals(pkg.getQualifiedName()) || name.equals(pkg.getName())) {
					seamPackage = pkg;
					break;
				}
			}
			seamPackage = findSeamPackage(cur, name);
			if(seamPackage != null) break;
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
	
	public void waitForJobs() {
		while (!Job.getJobManager().isIdle())
			delay(5000);
		System.out.println("---------------------------------continue----------------------------");
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
	
	private void refreshProject(IProject project){
		try {
			System.out.println("------------------------------brfore refresh");
			project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
			System.out.println("------------------------------abter refresh");
		} catch (CoreException e) {
			JUnitUtils.fail(e.getMessage(),e);
		}
		System.out.println("------------------------------before waitForJobs");
		waitForJobs();
		System.out.println("------------------------------abter waitForJobs");
	}
	
	public static void waitForJob() {
		Object[] o = {
			XJob.FAMILY_XJOB, ResourcesPlugin.FAMILY_AUTO_REFRESH, ResourcesPlugin.FAMILY_AUTO_BUILD
		};
		while(true) {
			boolean stop = true;
			for (int i = 0; i < o.length; i++) {
				Job[] js = Job.getJobManager().find(o[i]);
				if(js != null && js.length > 0) {
					System.out.print(js[0].getName());
					try {
						Job.getJobManager().join(o[i], new NullProgressMonitor());
					} catch (OperationCanceledException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					stop = false;
				}
			}
			if(stop) {
				Job running = getJobRunning(10);
				if(running != null) {
					try {
						running.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
	
	/**
	 * 
	 */
	private void updateView() {
			viewer.refresh();
			viewer.expandAll();
			Object[] array = viewer.getExpandedElements();
			for (Object object : array) {
				System.out.println(object.toString());
			}
	}
}
