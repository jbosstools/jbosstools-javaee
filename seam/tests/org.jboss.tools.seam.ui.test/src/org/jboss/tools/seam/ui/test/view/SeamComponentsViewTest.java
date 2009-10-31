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
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.navigator.CommonNavigator;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamPackage;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.ui.ISeamUiConstants;
import org.jboss.tools.seam.ui.SeamPerspectiveFactory;
import org.jboss.tools.seam.ui.views.actions.SeamViewLayoutActionGroup.SeamContributionItem;
import org.jboss.tools.test.util.JUnitUtils;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ResourcesUtils;
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
		IWorkbench workbench = PlatformUI.getWorkbench();
		try {
			workbench.showPerspective(SeamPerspectiveFactory.PERSPECTIVE_ID,workbench.getActiveWorkbenchWindow());
		} catch (WorkbenchException e) {
			fail("Cannot load perspective '" + SeamPerspectiveFactory.PERSPECTIVE_ID + "'");
		}
		project = (IProject)ResourcesPlugin.getWorkspace().getRoot().findMember("TestComponentView");
		if(project==null) {
			project = ResourcesUtils.createEclipseProject(Platform.getBundle("org.jboss.tools.seam.ui.test"), "/projects/TestComponentView");
		}
		assertNotNull("",project);
		this.project.refreshLocal(IResource.DEPTH_INFINITE, null);
		componentsFile = project.getFile(new Path("WebContent/WEB-INF/components.xml"));
		assertTrue("Cannot find components.xml in test project", componentsFile != null && componentsFile.exists());

		this.project.build(IncrementalProjectBuilder.FULL_BUILD, null);
		JobUtils.waitForIdle();
	}
	
	public void testAddComponentInXmlFile() throws CoreException{
		CommonNavigator navigator = getSeamComponentsView();

		navigator.getCommonViewer().expandAll();
		
		Tree tree = navigator.getCommonViewer().getTree();
		
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
		} catch (CoreException e) {
			JUnitUtils.fail("Error in changing 'components.xml' content to " +
					"'WebContent/WEB-INF/components.1'", e);
		}
		
		refreshProject(project);

		navigator.getCommonViewer().refresh(true);
		navigator.getCommonViewer().expandAll();
		seamPackage = findSeamPackage(tree, "myPackage");
		assertTrue("Seam model is not updated, expected package 'myPackage'" +
				" is not found in tree",seamPackage!=null);

		ISeamComponent component = findSeamComponent(seamPackage, 
												"myPackage.myStringComponent");
		
		assertTrue("Expected component 'myPackage.myStringComponent' was not" +
													 " found",component!=null);
		
	}

	public void testRenameComponentInXmlFile() throws CoreException{
		
		CommonNavigator navigator = getSeamComponentsView();
		navigator.getCommonViewer().expandAll();
		
		Tree tree = navigator.getCommonViewer().getTree();

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
		}catch(Exception ex){
			JUnitUtils.fail("Error in changing 'components.xml' content to " +
					"'WebContent/WEB-INF/components.2'", ex);
		}
		
		refreshProject(project);
		navigator.getCommonViewer().refresh(true);
		navigator.getCommonViewer().expandAll();
		
		seamPackage = findSeamPackage(tree, "myPackage");
		assertTrue("Expected package 'myPackage' was not found it tree",
				seamPackage!=null);
		JobUtils.delay(1000);
		component = findSeamComponent(seamPackage, "myPackage.myTextComponent");
		assertTrue("Expected component 'myPackage.myTextComponent' not found " +
				"after renaming",component!=null);
		
		file1 = project.getFile("WebContent/WEB-INF/components.3");
		if(file1 == null || !file1.exists()) {
			fail("Cannot find test data file 'WebContent/WEB-INF/components.3'");
		}		
		try{
			componentsFile.setContents(file1.getContents(), true, false, new NullProgressMonitor());
		}catch(Exception ex){
			JUnitUtils.fail("Error in changing 'components.xml' content to " +
					"'WebContent/WEB-INF/components.3'", ex);
		}
		
		refreshProject(project);
		navigator.getCommonViewer().refresh(true);
		navigator.getCommonViewer().expandAll();
		
		JobUtils.waitForIdle();
		
		seamPackage = findSeamPackage(tree, "myNewPackage");
		assertTrue("Expected package 'myNewPackage' was not found it tree after " +
				"renaming",
				seamPackage!=null);		
		
		component = findSeamComponent(seamPackage, "myNewPackage.myTextComponent");
		assertTrue("Expected component 'myNewPackage.myTextComponent' not found " +
				"after renaming",component!=null);
	}
	
	public void testDeleteComponentInXmlFile() throws CoreException{
		
		CommonNavigator navigator = getSeamComponentsView();
		navigator.getCommonViewer().expandAll();
		JobUtils.waitForIdle();
		Tree tree = navigator.getCommonViewer().getTree();
		
		ISeamPackage seamPackage = findSeamPackage(tree, "myNewPackage");
		assertTrue("Package \"myNewPackage\" not found!",seamPackage!=null);
		
		ISeamComponent component = findSeamComponent(seamPackage, "myNewPackage.myTextComponent");
		assertTrue("Component \"myNewPackage.myTextComponent\" not found!",component!=null);
		
		IFile file1 = project.getFile("WebContent/WEB-INF/components.4");
		assertTrue("Cannot find components.2 in test project", file1 != null && file1.exists());
		
		try{
			componentsFile.setContents(file1.getContents(), true, false, new NullProgressMonitor());
		}catch(Exception ex){
			JUnitUtils.fail("Cannot read file WebContent/WEB-INF/components.4", ex);
		}
		
		refreshProject(project);
		navigator.getCommonViewer().refresh(true);
		navigator.getCommonViewer().expandAll();
		
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
		}catch(Exception ex){
			JUnitUtils.fail("Cannot read file JavaSource/demo/Person.1", ex);
		}
		
		refreshProject(project);
		navigator.getCommonViewer().refresh(true);
		navigator.getCommonViewer().expandAll();
		
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
		}catch(Exception ex){
			JUnitUtils.fail("Cannot read file JavaSource/demo/Person.2", ex);
		}
		
		refreshProject(project);
		navigator.getCommonViewer().refresh(true);
		navigator.getCommonViewer().expandAll();
		
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
		}catch(Exception ex){
			JUnitUtils.fail("Cannot read file JavaSource/demo/Person.3", ex);
		}
		
		refreshProject(project);
		navigator.getCommonViewer().refresh(true);
		navigator.getCommonViewer().expandAll();
		
		seamPackage = findSeamPackage(tree, "demo");
		assertTrue("Package \"demo\" found!",seamPackage==null);
		
		seamPackage = findSeamPackage(tree, "beatles");
		assertTrue("Package \"beatles\" not found!",seamPackage!=null);
		
		component = findSeamComponent(seamPackage, "beatles.Pall");
		assertTrue("Component \"beatles.Pall\" not found!",component!=null);
	}
	
	public void testDeleteComponentInClass(){
		classFile = project.getFile("JavaSource/demo/Person.java");
		
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
		navigator.getCommonViewer().refresh(true);
		navigator.getCommonViewer().expandAll();
		
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
	
	public void testFlatSeamPackages(){
		SeamCorePlugin.getSeamProject(project, true);
		
		refreshProject(project);

		CommonNavigator navigator = getSeamComponentsView();
		
		IActionBars bars = ((IViewSite)navigator.getSite()).getActionBars();
		IMenuManager mm = bars.getMenuManager();
		IContributionItem item = ((MenuManager)mm).find("Seam Packages");
		SeamContributionItem item2 = (SeamContributionItem)((MenuManager)item).find("package.flat");
		item2.getAction().run();
		
		refreshProject(project);
		navigator.getCommonViewer().refresh();
		navigator.getCommonViewer().expandAll();
		Object[] expanded = navigator.getCommonViewer().getExpandedElements();
		for (Object object : expanded) {
			System.out.println(object.getClass().getName());
			System.out.println(object);
		}
		Tree tree = navigator.getCommonViewer().getTree();
		navigator.getCommonViewer().refresh(true);
		navigator.getCommonViewer().expandAll();
		
		ISeamPackage seamPackage = findSeamPackage(tree, "package1");
		
		assertTrue("Unexpected package 'package1' was" +
				 " found",seamPackage==null);
		
		seamPackage = findSeamPackage(tree, "package1.package2.package3.package4");
		
		assertTrue("Expected package 'package1.package2.package3.package4' was not" +
				 " found",seamPackage!=null);
	}

	public void testHierarchicalSeamPackages(){
		SeamCorePlugin.getSeamProject(project, true);
		
		refreshProject(project);

		CommonNavigator navigator = getSeamComponentsView();
		
		IActionBars bars = ((IViewSite)navigator.getSite()).getActionBars();
		IMenuManager mm = bars.getMenuManager();
		IContributionItem item = ((MenuManager)mm).find("Seam Packages");
		SeamContributionItem item2 = (SeamContributionItem)((MenuManager)item).find("package.hierarchical");
		item2.getAction().run();
		
		refreshProject(project);
		navigator.getCommonViewer().refresh();
		navigator.getCommonViewer().expandAll();
		Object[] expanded = navigator.getCommonViewer().getExpandedElements();
	
		Tree tree = navigator.getCommonViewer().getTree();
		navigator.getCommonViewer().refresh(true);
		navigator.getCommonViewer().expandAll();
		
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

	private CommonNavigator getSeamComponentsView() {
		IWorkbenchPage page  = WorkbenchUtils.getWorkbenchActivePage();
		CommonNavigator part = (CommonNavigator)page.findView(ISeamUiConstants.SEAM_COMPONENTS_VIEW_ID);
		return part;
	}
	
	private void showTreeItem(TreeItem item, int level){
		for(int i=0;i<level;i++)
			System.out.print("-");
		
		System.out.print("Item "+item.getText());
		System.out.println(" Data "+item.getData());
		if(item.getData() instanceof ISeamPackage)
			showSeamPackage((ISeamPackage)item.getData(),1);
		else if(item.getData() instanceof ISeamComponent)
			showSeamComponent((ISeamComponent)item.getData(),1);
		
		for(int i=0;i<item.getItemCount();i++){
			showTreeItem(item.getItem(i),level+1);
		}
	}

	private void showSeamPackage(ISeamPackage seamPackage, int level){
//		for(int i=0;i<level;i++)
//			System.out.print("-");
		
		System.out.println("Package - "+seamPackage.getName()+" "+seamPackage.getQualifiedName());
		
		Iterator<ISeamComponent> iter = seamPackage.getComponents().iterator();
		while(iter.hasNext())
			showSeamComponent(iter.next(), level+1);
	}

	private void showSeamComponent(ISeamComponent component, int level){
//		for(int i=0;i<level;i++)
//			System.out.print("-");
		
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

//	private ISeamPackage findSeamPackage(ISeamScope seamScope, String name){
//		ISeamPackage seamPackage=null;
//		
//		Iterator<ISeamPackage> iter = seamScope.getAllPackages().iterator();
//		while(iter.hasNext()){
//			seamPackage = iter.next();
//			if(seamPackage.getName().equals(name)) return seamPackage;
//		}
//		
//		return null;
//	}

	private ISeamPackage findSeamPackage(TreeItem item, String name){
		ISeamPackage seamPackage=null;
		
		for(int i=0;i<item.getItemCount();i++){
			TreeItem cur = item.getItem(i);
			if(cur.getData() instanceof ISeamPackage) {
				ISeamPackage pkg =(ISeamPackage)cur.getData();
				//System.out.println("Searching: "+name+" found: "+pkg.getQualifiedName());
				if(name.equals(pkg.getQualifiedName())) {
					//System.out.println("Found!");
					return pkg;
				}
			}
			seamPackage = findSeamPackage(cur, name);
			if(seamPackage != null) return seamPackage;
			
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
				JobUtils.waitForIdle();
			} catch (CoreException e) {
				JUnitUtils.fail("Cannot build test Project", e);
				break;
			}
			if(project.getModificationStamp() != timestamp) break;
			count++;
			if(count > NUMBER_OF_REFRESHES) break;
		}
		JobUtils.delay(1000);
	}
	
}
