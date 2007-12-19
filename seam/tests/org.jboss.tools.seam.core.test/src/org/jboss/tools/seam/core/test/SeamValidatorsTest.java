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
		
		String[] messages = getMarkersMessage(bbcComponentFile);
		
		assertTrue("Problem marker 'Duplicate component name' not found","Duplicate component name: abcComponent".equals(messages[0]));
		
		int[] lineNumbers = getMarkersNumbersOfLine(bbcComponentFile);
		
		assertTrue("Problem marker has wrong line number", lineNumbers[0] == 7);
		
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
		
		messages = getMarkersMessage(statefulComponentFile);
		assertTrue("Problem marker 'Stateful component does not contain @Remove method' not found", "Stateful component \"statefulComponent\" must have a method marked @Remove".equals(messages[0]));
		
		lineNumbers = getMarkersNumbersOfLine(statefulComponentFile);
		
		assertTrue("Problem marker has wrong line number", lineNumbers[0] == 16);
		
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
		
		messages = getMarkersMessage(statefulComponentFile);
		assertTrue("Problem marker 'Stateful component does not contain @Destroy method' not found", "Stateful component \"statefulComponent\" must have a method marked @Destroy".equals(messages[0]));
		
		lineNumbers = getMarkersNumbersOfLine(statefulComponentFile);
		
		assertTrue("Problem marker has wrong line number", lineNumbers[0] == 16);
		
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
		
		messages = getMarkersMessage(statefulComponentFile);
		assertTrue("Problem marker 'Stateful component has wrong scope' not found", "Stateful component \"statefulComponent\" should not have org.jboss.seam.ScopeType.PAGE, nor org.jboss.seam.ScopeType.STATELESS".equals(messages[0]));
		
		lineNumbers = getMarkersNumbersOfLine(statefulComponentFile);
		
		assertTrue("Problem marker has wrong line number", lineNumbers[0] == 16);
		
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
		
		messages = getMarkersMessage(componentsFile);
		assertTrue("Problem marker 'Component class name cannot be resolved to a type' not found", "\"org.domain.SeamWebTestProject.session.StateComponent\" cannot be resolved to a type".equals(messages[0]));
		
		lineNumbers = getMarkersNumbersOfLine(componentsFile);
		
		assertTrue("Problem marker has wrong line number", lineNumbers[0] == 15);

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
		
		messages = getMarkersMessage(componentsFile);
		assertTrue("Problem marker 'Component class does not contain setter for property' not found", "Class \"StatefulComponent\" of component \"statefulComponent\" does not contain setter for property \"abc\"".equals(messages[0]));
		
		lineNumbers = getMarkersNumbersOfLine(componentsFile);
		
		assertTrue("Problem marker has wrong line number", lineNumbers[0] == 16);
	}
	
	public void testEntitiesValidator() {
		ISeamProject seamProject = getSeamProject(project);
		
		IFile abcEntityFile = project.getFile("src/action/org/domain/SeamWebWarTestProject/entity/abcEntity.java");
		
		int number = getMarkersNumber(abcEntityFile);
		assertTrue("Problem marker was found in abcEntity.java", number == 0);
		
		// Entity component has wrong scope
		System.out.println("Test - Entity component has wrong scope");
		
		IFile abcEntityFile2 = project.getFile("src/action/org/domain/SeamWebWarTestProject/entity/abcEntity.2");
		try{
			abcEntityFile.setContents(abcEntityFile2.getContents(), true, false, new NullProgressMonitor());
			abcEntityFile.touch(new NullProgressMonitor());
		}catch(Exception ex){
			JUnitUtils.fail("Error in changing 'abcEntity.java' content to " +
					"'abcEntity.2'", ex);
		}
		
		refreshProject(project);
		
		String[] messages = getMarkersMessage(abcEntityFile);
		assertTrue("Problem marker 'Entity component has wrong scope' not found", "Entity component \"abcEntity\" should not have org.jboss.seam.ScopeType.STATELESS".equals(messages[0]));

		int[] lineNumbers = getMarkersNumbersOfLine(abcEntityFile);
		
		assertTrue("Problem marker has wrong line number", lineNumbers[0] == 15);
		
		// Duplicate @Remove method
		System.out.println("Test - Duplicate @Remove method");
		
		IFile abcEntityFile3 = project.getFile("src/action/org/domain/SeamWebWarTestProject/entity/abcEntity.3");
		try{
			abcEntityFile.setContents(abcEntityFile3.getContents(), true, false, new NullProgressMonitor());
			abcEntityFile.touch(new NullProgressMonitor());
		}catch(Exception ex){
			JUnitUtils.fail("Error in changing 'abcEntity.java' content to " +
					"'abcEntity.3'", ex);
		}
		
		refreshProject(project);
		
		messages = getMarkersMessage(abcEntityFile);
		assertTrue("Problem marker 'Duplicate @Remove method' not found", messages[0].startsWith("Duplicate @Remove method \"removeMethod"));

		lineNumbers = getMarkersNumbersOfLine(abcEntityFile);
		
		assertTrue("Wrong number of problem markers", lineNumbers.length == messages.length && messages.length == 2);
		
		if(messages[1].indexOf("removeMethod2") >= 0){
			assertTrue("Problem marker has wrong line number", lineNumbers[0] == 42);
			assertTrue("Problem marker has wrong line number", lineNumbers[1] == 47);
		}else{
			assertTrue("Problem marker has wrong line number", lineNumbers[0] == 47);
			assertTrue("Problem marker has wrong line number", lineNumbers[1] == 42);
			
		}
	}

	public void testComponentLifeCycleMethodsValidator() {
		ISeamProject seamProject = getSeamProject(project);
		IFile componentsFile = project.getFile("WebContent/WEB-INF/components.xml");
		
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
		
		String[] messages = getMarkersMessage(statefulComponentFile);
		assertTrue("Problem marker 'Duplicate @Destroy method' not found", messages[0].startsWith("Duplicate @Destroy method \"destroyMethod"));

		int[] lineNumbers = getMarkersNumbersOfLine(statefulComponentFile);
		
		assertTrue("Wrong number of problem markers", lineNumbers.length == messages.length && messages.length == 2);
		
		if(messages[1].indexOf("destroyMethod2") >= 0){
			assertTrue("Problem marker has wrong line number", lineNumbers[0] == 32);
			assertTrue("Problem marker has wrong line number", lineNumbers[1] == 38);
		}else{
			assertTrue("Problem marker has wrong line number", lineNumbers[0] == 38);
			assertTrue("Problem marker has wrong line number", lineNumbers[1] == 32);
			
		}
		
		// Duplicate @Create method
		System.out.println("Test - Duplicate @Create method");
		
		IFile statefulComponentFile7 = project.getFile("src/action/org/domain/SeamWebWarTestProject/session/StatefulComponent.7");
		try{
			statefulComponentFile.setContents(statefulComponentFile7.getContents(), true, false, new NullProgressMonitor());
			statefulComponentFile.touch(new NullProgressMonitor());
		}catch(Exception ex){
			JUnitUtils.fail("Error in changing 'StatefulComponent.java' content to " +
					"'StatefulComponent.7'", ex);
		}
		
		refreshProject(project);
		
		messages = getMarkersMessage(statefulComponentFile);
		assertTrue("Problem marker 'Duplicate @Create method' not found", messages[0].startsWith("Duplicate @Create method \"createMethod"));
		
		lineNumbers = getMarkersNumbersOfLine(statefulComponentFile);
		
		assertTrue("Wrong number of problem markers", lineNumbers.length == messages.length && messages.length == 2);
		
		if(messages[1].indexOf("createMethod2") >= 0){
			assertTrue("Problem marker has wrong line number", lineNumbers[0] == 33);
			assertTrue("Problem marker has wrong line number", lineNumbers[1] == 40);
		}else{
			assertTrue("Problem marker has wrong line number", lineNumbers[0] == 40);
			assertTrue("Problem marker has wrong line number", lineNumbers[1] == 33);
			
		}
		
		// Duplicate @Unwrap method
		System.out.println("Test - Duplicate @Unwrap method");
		
		IFile statefulComponentFile8 = project.getFile("src/action/org/domain/SeamWebWarTestProject/session/StatefulComponent.8");
		try{
			statefulComponentFile.setContents(statefulComponentFile8.getContents(), true, false, new NullProgressMonitor());
			statefulComponentFile.touch(new NullProgressMonitor());
		}catch(Exception ex){
			JUnitUtils.fail("Error in changing 'StatefulComponent.java' content to " +
					"'StatefulComponent.8'", ex);
		}
		
		refreshProject(project);
		
		messages = getMarkersMessage(statefulComponentFile);
		assertTrue("Problem marker 'Duplicate @Unwrap method' not found", messages[0].startsWith("Duplicate @Unwrap method \"unwrapMethod"));

		lineNumbers = getMarkersNumbersOfLine(statefulComponentFile);
		
		assertTrue("Wrong number of problem markers", lineNumbers.length == messages.length && messages.length == 2);
		
		if(messages[1].indexOf("unwrapMethod2") >= 0){
			assertTrue("Problem marker has wrong line number", lineNumbers[0] == 39);
			assertTrue("Problem marker has wrong line number", lineNumbers[1] == 44);
		}else{
			assertTrue("Problem marker has wrong line number", lineNumbers[0] == 44);
			assertTrue("Problem marker has wrong line number", lineNumbers[1] == 39);
			
		}
		
		// Only component class can have @Destroy method
		System.out.println("Test - Only component class can have @Destroy method");
		
		IFile componentsFile4 = project.getFile("WebContent/WEB-INF/components.4");
		
		try{
			componentsFile.setContents(componentsFile4.getContents(), true, false, new NullProgressMonitor());
			componentsFile.touch(new NullProgressMonitor());
		}catch(Exception ex){
			JUnitUtils.fail("Error in changing 'components.xml' content to " +
					"'components.4'", ex);
		}
		IFile statefulComponentFile9 = project.getFile("src/action/org/domain/SeamWebWarTestProject/session/StatefulComponent.9");
		try{
			statefulComponentFile.setContents(statefulComponentFile9.getContents(), true, false, new NullProgressMonitor());
			statefulComponentFile.touch(new NullProgressMonitor());
		}catch(Exception ex){
			JUnitUtils.fail("Error in changing 'StatefulComponent.java' content to " +
					"'StatefulComponent.9'", ex);
		}
		
		refreshProject(project);
		
		messages = getMarkersMessage(statefulComponentFile);
		assertTrue("Problem marker 'Only component class can have @Destroy method' not found", "Only component class can have @Destroy method \"destroyMethod\"".equals(messages[0]));
		
		lineNumbers = getMarkersNumbersOfLine(statefulComponentFile);
		
		assertTrue("Problem marker has wrong line number", lineNumbers[0] == 23);
		
		// Only component class can have @Create method
		System.out.println("Test - Only component class can have @Create method");
		
		IFile statefulComponentFile10 = project.getFile("src/action/org/domain/SeamWebWarTestProject/session/StatefulComponent.10");
		try{
			statefulComponentFile.setContents(statefulComponentFile10.getContents(), true, false, new NullProgressMonitor());
			statefulComponentFile.touch(new NullProgressMonitor());
		}catch(Exception ex){
			JUnitUtils.fail("Error in changing 'StatefulComponent.java' content to " +
					"'StatefulComponent.10'", ex);
		}
		
		refreshProject(project);
		
		messages = getMarkersMessage(statefulComponentFile);
		assertTrue("Problem marker 'Only component class can have @Create method' not found", "Only component class can have @Create method \"createMethod\"".equals(messages[0]));
		
		lineNumbers = getMarkersNumbersOfLine(statefulComponentFile);
		
		assertTrue("Problem marker has wrong line number", lineNumbers[0] == 23);
		
		// Only component class can have @Unwrap method
		System.out.println("Test - Only component class can have @Unwrap method");
		
		IFile statefulComponentFile11 = project.getFile("src/action/org/domain/SeamWebWarTestProject/session/StatefulComponent.11");
		try{
			statefulComponentFile.setContents(statefulComponentFile11.getContents(), true, false, new NullProgressMonitor());
			statefulComponentFile.touch(new NullProgressMonitor());
		}catch(Exception ex){
			JUnitUtils.fail("Error in changing 'StatefulComponent.java' content to " +
					"'StatefulComponent.11'", ex);
		}
		
		refreshProject(project);
		
		messages = getMarkersMessage(statefulComponentFile);
		assertTrue("Problem marker 'Only component class can have @Unwrap method' not found", "Only component class can have @Unwrap method \"unwrapMethod\"".equals(messages[0]));

		lineNumbers = getMarkersNumbersOfLine(statefulComponentFile);
		
		assertTrue("Problem marker has wrong line number", lineNumbers[0] == 23);
		
		// Only component class can have @Observer method
		System.out.println("Test - Only component class can have @Observer method");
		
		IFile statefulComponentFile12 = project.getFile("src/action/org/domain/SeamWebWarTestProject/session/StatefulComponent.12");
		try{
			statefulComponentFile.setContents(statefulComponentFile12.getContents(), true, false, new NullProgressMonitor());
			statefulComponentFile.touch(new NullProgressMonitor());
		}catch(Exception ex){
			JUnitUtils.fail("Error in changing 'StatefulComponent.java' content to " +
					"'StatefulComponent.12'", ex);
		}
		
		refreshProject(project);
		
		messages = getMarkersMessage(statefulComponentFile);
		assertTrue("Problem marker 'Only component class can have @Observer method' not found", "Only component class can have @Observer method \"observerMethod\"".equals(messages[0]));
		
		lineNumbers = getMarkersNumbersOfLine(statefulComponentFile);
		
		assertTrue("Problem marker has wrong line number", lineNumbers[0] == 23);
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
			for(int i=0;i<markers.length;i++){
				System.out.println("Marker - "+markers[i].getAttribute(IMarker.MESSAGE, ""));
			}
			return markers.length;
			
		}catch(CoreException ex){
			JUnitUtils.fail("Error in getting problem markers", ex);
		}
		return -1;
	}

	private String[] getMarkersMessage(IFile file){
		String[] messages = new String[1];
		messages[0]="";
		try{
			IMarker[] markers = file.findMarkers(null, true, IResource.DEPTH_INFINITE);
			messages = new String[markers.length];
			
			for(int i=0;i<markers.length;i++){
				System.out.println("Marker - "+markers[i].getAttribute(IMarker.MESSAGE, ""));
				messages[i] = markers[i].getAttribute(IMarker.MESSAGE, "");
			}
		}catch(CoreException ex){
			JUnitUtils.fail("Error in getting problem markers", ex);
		}
		return messages;
	}

	private int[] getMarkersNumbersOfLine(IFile file){
		int[] numbers = new int[1];
		numbers[0]=0;
		try{
			IMarker[] markers = file.findMarkers(null, true, IResource.DEPTH_INFINITE);
			numbers = new int[markers.length];
			
			for(int i=0;i<markers.length;i++){
				System.out.println("Marker line number - "+markers[i].getAttribute(IMarker.LINE_NUMBER, 0));
				numbers[i] = markers[i].getAttribute(IMarker.LINE_NUMBER, 0);
			}
		}catch(CoreException ex){
			JUnitUtils.fail("Error in getting problem markers", ex);
		}
		return numbers;
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
