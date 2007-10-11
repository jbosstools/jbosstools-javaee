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
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.jboss.tools.common.model.XJob;
import org.jboss.tools.common.test.util.TestProjectProvider;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.internal.core.SeamProject;
import org.jboss.tools.test.util.JUnitUtils;

public class SeamValidatorsTest extends TestCase {
	IProject project = null;
	
	boolean makeCopy = false;

	public SeamValidatorsTest() {}

	protected void setUp() throws Exception {
		TestProjectProvider providerEAR = new TestProjectProvider("org.jboss.tools.seam.core.test", null, "SeamWebWarTestProject", makeCopy);
		project = providerEAR.getProject();

		try {
			project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		} catch (Exception e) {
			JUnitUtils.fail("Error in refreshing",e);
		}

		try {
			XJob.waitForJob();
		} catch (InterruptedException e) {
			JUnitUtils.fail("Interrupted",e);
		}
	}

	private ISeamProject getSeamProject(IProject project) {
		try {
			XJob.waitForJob();
		} catch (Exception e) {
			JUnitUtils.fail("Interrupted",e);
		}
		try {
			project.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
			try {
				XJob.waitForJob();
			} catch (InterruptedException e) {
				JUnitUtils.fail("Interrupted",e);
			}
		} catch (Exception e) {
			JUnitUtils.fail("Cannot build", e);
		}
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
		try {
			seamProject.getProject().build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
			try {
				XJob.waitForJob();
			} catch (InterruptedException e) {
				JUnitUtils.fail("Interrupted",e);
			}
		} catch (Exception e) {
			JUnitUtils.fail("Cannot build", e);
		}
		try{
			IMarker[] markers = bbcComponentFile.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
			for(int i=0;i<markers.length;i++){
				System.out.println("Marker - "+markers[i].getAttribute(IMarker.TEXT, ""));
			}
		}catch(CoreException ex){
			JUnitUtils.fail("Error in getting problem markers", ex);
		}
		
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
		try {
			seamProject.getProject().build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
			try {
				XJob.waitForJob();
			} catch (InterruptedException e) {
				JUnitUtils.fail("Interrupted",e);
			}
		} catch (Exception e) {
			JUnitUtils.fail("Cannot build", e);
		}
		try{
			IMarker[] markers = statefulComponentFile.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
			for(int i=0;i<markers.length;i++){
				System.out.println("Marker - "+markers[i].getAttribute(IMarker.TEXT, ""));
			}
		}catch(CoreException ex){
			JUnitUtils.fail("Error in getting problem markers", ex);
		}
		
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
		try {
			seamProject.getProject().build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
			try {
				XJob.waitForJob();
			} catch (InterruptedException e) {
				JUnitUtils.fail("Interrupted",e);
			}
		} catch (Exception e) {
			JUnitUtils.fail("Cannot build", e);
		}
		try{
			IMarker[] markers = statefulComponentFile.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
			for(int i=0;i<markers.length;i++){
				System.out.println("Marker - "+markers[i].getAttribute(IMarker.TEXT, ""));
			}
		}catch(CoreException ex){
			JUnitUtils.fail("Error in getting problem markers", ex);
		}
		
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
		try {
			seamProject.getProject().build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
			try {
				XJob.waitForJob();
			} catch (InterruptedException e) {
				JUnitUtils.fail("Interrupted",e);
			}
		} catch (Exception e) {
			JUnitUtils.fail("Cannot build", e);
		}
		try{
			IMarker[] markers = statefulComponentFile.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
			for(int i=0;i<markers.length;i++){
				System.out.println("Marker - "+markers[i].getAttribute(IMarker.TEXT, ""));
			}
		}catch(CoreException ex){
			JUnitUtils.fail("Error in getting problem markers", ex);
		}
		
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
		try {
			seamProject.getProject().build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
			try {
				XJob.waitForJob();
			} catch (InterruptedException e) {
				JUnitUtils.fail("Interrupted",e);
			}
		} catch (Exception e) {
			JUnitUtils.fail("Cannot build", e);
		}
		try{
			IMarker[] markers = componentsFile.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
			for(int i=0;i<markers.length;i++){
				System.out.println("Marker - "+markers[i].getAttribute(IMarker.TEXT, ""));
			}
		}catch(CoreException ex){
			JUnitUtils.fail("Error in getting problem markers", ex);
		}

		// Component class does not contain setter for property
		System.out.println("Test - Component class does not contain setter for property");
		
		IFile statefulComponentFile5 = project.getFile("src/action/org/domain/SeamWebWarTestProject/session/StatefulComponent.5");
		try{
			statefulComponentFile.setContents(statefulComponentFile5.getContents(), true, false, new NullProgressMonitor());
			statefulComponentFile.touch(new NullProgressMonitor());
		}catch(Exception ex){
			JUnitUtils.fail("Error in changing 'StatefulComponent.java' content to " +
					"'StatefulComponent.5'", ex);
		}
		try {
			seamProject.getProject().build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
			try {
				XJob.waitForJob();
			} catch (InterruptedException e) {
				JUnitUtils.fail("Interrupted",e);
			}
		} catch (Exception e) {
			JUnitUtils.fail("Cannot build", e);
		}
		try{
			IMarker[] markers = componentsFile.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
			for(int i=0;i<markers.length;i++){
				System.out.println("Marker - "+markers[i].getAttribute(IMarker.TEXT, ""));
			}
		}catch(CoreException ex){
			JUnitUtils.fail("Error in getting problem markers", ex);
		}

	}

}
