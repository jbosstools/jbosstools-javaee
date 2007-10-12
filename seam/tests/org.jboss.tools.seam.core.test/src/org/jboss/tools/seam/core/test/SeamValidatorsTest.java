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
package org.jboss.tools.seam.core.test;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.internal.decorators.DecoratorManager;
import org.eclipse.ui.progress.UIJob;
import org.jboss.tools.common.model.XJob;
import org.jboss.tools.common.test.util.TestProjectProvider;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.internal.core.SeamProject;
import org.jboss.tools.test.util.JUnitUtils;

public class SeamValidatorsTest extends TestCase {
	IProject project = null;
	
	boolean makeCopy = true;

	public SeamValidatorsTest() {}

	protected void setUp() throws Exception {
		TestProjectProvider providerEAR = new TestProjectProvider("org.jboss.tools.seam.core.test", null, "SeamWebWarTestProject", makeCopy);
		project = providerEAR.getProject();

		try {
			project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		} catch (Exception e) {
			JUnitUtils.fail("Error in refreshing",e);
		}

		refreshProject(project);
	}

	private ISeamProject getSeamProject(IProject project) {
		refreshProject(project);
		
		ISeamProject seamProject = null;
		try {
			seamProject = (ISeamProject)project.getNature(SeamProject.NATURE_ID);
		} catch (Exception e) {
			JUnitUtils.fail("Cannot get seam nature.",e);
		}
		assertNotNull("Seam project is null", seamProject);
		return seamProject;
	}
	
	/**
	 * This empty test is meaningful as it gives Eclipse opportunity 
	 * to pass for the first time setUp() and show the license dialog 
	 * that may cause InterruptedException for XJob.waitForJob()
	 */
	public void testCreatingProject() {
	}
	
	public void testComponentsValidator() {
		ISeamProject seamProject = getSeamProject(project);
		
		IFile bbcComponentFile = project.getFile("src/action/org/domain/SeamWebWarTestProject/session/BbcComponent.java");
		IFile statefulComponentFile = project.getFile("src/action/org/domain/SeamWebWarTestProject/session/StatefulComponent.java");
		IFile componentsFile = project.getFile("WebContent/WEB-INF/components.xml");
		
		int number = getMarkersNumber(bbcComponentFile);
		assertTrue("Problem marker was found in BbcComponent.java file", number == 0);

		number = getMarkersNumber(statefulComponentFile);
		assertTrue("Problem marker was found in StatefulComponent.java file", number == 0);

		number = getMarkersNumber(componentsFile);
		assertTrue("Problem marker was found in components.xml file", number == 0);

		// Duplicate component name
		System.out.println("Test - Duplicate component name");
		
		IFile bbcComponentFile2 = project.getFile("src/action/org/domain/SeamWebWarTestProject/session/BbcComponent.2");
		try{
			bbcComponentFile.setContents(bbcComponentFile2.getContents(), true, false, new NullProgressMonitor());
			bbcComponentFile.touch(new NullProgressMonitor());
		}catch(Exception ex){
			JUnitUtils.fail("Error in changing 'BbcComponent.java' content to " +
					"'BbcComponent.2'", ex);
		}
		
		refreshProject(project);
		
		String message = getMarkersMessage(bbcComponentFile);
		
		assertTrue("Problem marker 'Duplicate component name' not found","Duplicate component name: abcComponent".equals(message));
		
		// Stateful component does not contain @Remove method
		System.out.println("Test - Stateful component does not contain @Remove method");
		
		IFile statefulComponentFile2 = project.getFile("src/action/org/domain/SeamWebWarTestProject/session/StatefulComponent.2");
		try{
			statefulComponentFile.setContents(statefulComponentFile2.getContents(), true, false, new NullProgressMonitor());
			statefulComponentFile.touch(new NullProgressMonitor());
		}catch(Exception ex){
			JUnitUtils.fail("Error in changing 'StatefulComponent.java' content to " +
					"'StatefulComponent.2'", ex);
		}
		
		refreshProject(project);
		
		message = getMarkersMessage(statefulComponentFile);
		assertTrue("Problem marker 'Stateful component does not contain @Remove method' not found", "Stateful component \"statefulComponent\" must have a method marked @Remove".equals(message));
		
		// Stateful component does not contain @Destroy method
		System.out.println("Test - Stateful component does not contain @Destroy method");
		
		IFile statefulComponentFile3 = project.getFile("src/action/org/domain/SeamWebWarTestProject/session/StatefulComponent.3");
		try{
			statefulComponentFile.setContents(statefulComponentFile3.getContents(), true, false, new NullProgressMonitor());
			statefulComponentFile.touch(new NullProgressMonitor());
		}catch(Exception ex){
			JUnitUtils.fail("Error in changing 'StatefulComponent.java' content to " +
					"'StatefulComponent.3'", ex);
		}
		
		refreshProject(project);
		
		message = getMarkersMessage(statefulComponentFile);
		assertTrue("Problem marker 'Stateful component does not contain @Destroy method' not found", "Stateful component \"statefulComponent\" must have a method marked @Destroy".equals(message));
		
		// Stateful component has wrong scope
		System.out.println("Test - Stateful component has wrong scope");
		
		IFile statefulComponentFile4 = project.getFile("src/action/org/domain/SeamWebWarTestProject/session/StatefulComponent.4");
		try{
			statefulComponentFile.setContents(statefulComponentFile4.getContents(), true, false, new NullProgressMonitor());
			statefulComponentFile.touch(new NullProgressMonitor());
		}catch(Exception ex){
			JUnitUtils.fail("Error in changing 'StatefulComponent.java' content to " +
					"'StatefulComponent.4'", ex);
		}
		
		refreshProject(project);
		
		message = getMarkersMessage(statefulComponentFile);
		assertTrue("Problem marker 'Stateful component has wrong scope' not found", "Stateful component \"statefulComponent\" should not have org.jboss.seam.ScopeType.PAGE, nor org.jboss.seam.ScopeType.STATELESS".equals(message));
		
		// Component class name cannot be resolved to a type
		System.out.println("Test - Component class name cannot be resolved to a type");
		
		IFile componentsFile2 = project.getFile("WebContent/WEB-INF/components.2");
		
		try{
			componentsFile.setContents(componentsFile2.getContents(), true, false, new NullProgressMonitor());
			componentsFile.touch(new NullProgressMonitor());
		}catch(Exception ex){
			JUnitUtils.fail("Error in changing 'components.xml' content to " +
					"'components.2'", ex);
		}
		
		refreshProject(project);
		
		message = getMarkersMessage(componentsFile);
		assertTrue("Problem marker 'Component class name cannot be resolved to a type' not found", "\"org.domain.SeamWebWarTestProject.session.StateComponent\" cannot be resolved to a type".equals(message));

		// Component class does not contain setter for property
		System.out.println("Test - Component class does not contain setter for property");
		
		IFile componentsFile3 = project.getFile("WebContent/WEB-INF/components.3");
		
		try{
			componentsFile.setContents(componentsFile3.getContents(), true, false, new NullProgressMonitor());
			componentsFile.touch(new NullProgressMonitor());
		}catch(Exception ex){
			JUnitUtils.fail("Error in changing 'components.xml' content to " +
					"'components.3'", ex);
		}
		
		IFile statefulComponentFile5 = project.getFile("src/action/org/domain/SeamWebWarTestProject/session/StatefulComponent.5");

		try{
			statefulComponentFile.setContents(statefulComponentFile5.getContents(), true, false, new NullProgressMonitor());
			statefulComponentFile.touch(new NullProgressMonitor());
		}catch(Exception ex){
			JUnitUtils.fail("Error in changing 'StatefulComponent.java' content to " +
					"'StatefulComponent.5'", ex);
		}
		
		refreshProject(project);
		
		message = getMarkersMessage(componentsFile);
		assertTrue("Problem marker 'Component class does not contain setter for property' not found", "Class \"StatefulComponent\" of component \"statefulComponent\" does not contain setter for property \"abc\"".equals(message));
	}
	
	public void testEntitiesValidator() {
		
	}

	public void testComponentLifeCycleMethodsValidator() {
		ISeamProject seamProject = getSeamProject(project);
		
		IFile statefulComponentFile = project.getFile("src/action/org/domain/SeamWebWarTestProject/session/StatefulComponent.java");
		
		int number = getMarkersNumber(statefulComponentFile);
		assertTrue("Problem marker was found in StatefulComponent.java file", number == 0);

		// Duplicate @Destroy method
		System.out.println("Test - Duplicate @Destroy method");
		
		IFile statefulComponentFile6 = project.getFile("src/action/org/domain/SeamWebWarTestProject/session/StatefulComponent.6");
		try{
			statefulComponentFile.setContents(statefulComponentFile6.getContents(), true, false, new NullProgressMonitor());
			statefulComponentFile.touch(new NullProgressMonitor());
		}catch(Exception ex){
			JUnitUtils.fail("Error in changing 'StatefulComponent.java' content to " +
					"'StatefulComponent.6'", ex);
		}
		
		refreshProject(project);
		
		String message = getMarkersMessage(statefulComponentFile);
		assertTrue("Problem marker 'Duplicate @Destroy method' not found", "Stateful component \"statefulComponent\" must have a method marked @Remove".equals(message));

		// Duplicate @Create method
		// Duplicate @Unwrap method
		// Only component class can have @Destroy method
		// Only component class can have @Create method
		// Only component class can have @Unwrap method
		// Only component class can have @Observer method

	}
	
	public void testFactoriesValidator() {
		
	}
	
	public void testBijectionsValidator() {
		
	}

	public void testContextVariablesValidator() {
		
	}

	public void testExpressionLanguageValidator() {
		
	}
	
	private int getMarkersNumber(IFile file){
		try{
			IMarker[] markers = file.findMarkers(null, true, IResource.DEPTH_INFINITE);
			return markers.length;
			
		}catch(CoreException ex){
			JUnitUtils.fail("Error in getting problem markers", ex);
		}
		return -1;
	}

	private String getMarkersMessage(IFile file){
		String message="";
		try{
			IMarker[] markers = file.findMarkers(null, true, IResource.DEPTH_INFINITE);
			
			for(int i=0;i<markers.length;i++){
				System.out.println("Marker - "+markers[i].getAttribute(IMarker.MESSAGE, ""));
				message = markers[i].getAttribute(IMarker.MESSAGE, "");
			}
		}catch(CoreException ex){
			JUnitUtils.fail("Error in getting problem markers", ex);
		}
		return message;
	}
	
	private void refreshProject(IProject project){
		long timestamp = project.getModificationStamp();
		int count = 1;
		while(true){
			System.out.println("Refresh project "+count);
			try {
				project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
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
			if(count > 1) break;
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

}
