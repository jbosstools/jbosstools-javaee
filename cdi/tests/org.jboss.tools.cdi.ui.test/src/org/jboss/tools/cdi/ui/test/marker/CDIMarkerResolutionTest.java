/*************************************************************************************
 * Copyright (c) 2008-2011 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     JBoss by Red Hat - Initial implementation.
 ************************************************************************************/
package org.jboss.tools.cdi.ui.test.marker;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.ide.IDE;
import org.jboss.tools.cdi.core.test.tck.validation.ValidationTest;
import org.jboss.tools.cdi.internal.core.validation.CDICoreValidator;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationErrorManager;
import org.jboss.tools.cdi.ui.marker.AddLocalBeanMarkerResolution;
import org.jboss.tools.cdi.ui.marker.AddRetentionAnnotationMarkerResolution;
import org.jboss.tools.cdi.ui.marker.AddSerializableInterfaceMarkerResolution;
import org.jboss.tools.cdi.ui.marker.AddTargetAnnotationMarkerResolution;
import org.jboss.tools.cdi.ui.marker.ChangeRetentionAnnotationMarkerResolution;
import org.jboss.tools.cdi.ui.marker.ChangeTargetAnnotationMarkerResolution;
import org.jboss.tools.cdi.ui.marker.DeleteAllDisposerDuplicantMarkerResolution;
import org.jboss.tools.cdi.ui.marker.DeleteAllInjectedConstructorsMarkerResolution;
import org.jboss.tools.cdi.ui.marker.MakeFieldStaticMarkerResolution;
import org.jboss.tools.cdi.ui.marker.MakeMethodBusinessMarkerResolution;
import org.jboss.tools.cdi.ui.marker.MakeMethodPublicMarkerResolution;
import org.jboss.tools.cdi.ui.marker.TestableResolutionWithRefactoringProcessor;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.jst.jsp.test.TestUtil;
import org.jboss.tools.jst.web.kb.internal.validation.ValidatorManager;

/**
 * @author Daniel Azarov
 * 
 */
public class CDIMarkerResolutionTest  extends ValidationTest {
	
	private void checkResolution(IProject project, String[] fileNames, String markerType, String idName, int id, Class<? extends IMarkerResolution> resolutionClass) throws CoreException {
		checkResolution(project, fileNames, new String[]{}, markerType, idName, id, resolutionClass);
	}
	
	private void checkResolution(IProject project, String[] fileNames, String[] results, String markerType, String idName, int id, Class<? extends IMarkerResolution> resolutionClass) throws CoreException {
		IFile file = project.getFile(fileNames[0]);

		assertTrue("File - "+file.getFullPath()+" must be exist",file.exists());

		ValidatorManager.setStatus("TESTING");
		copyFiles(project, fileNames);
		TestUtil.waitForValidation();

		try{
			file = project.getFile(fileNames[0]);
			IMarker[] markers = file.findMarkers(markerType, true,	IResource.DEPTH_INFINITE);

			for (int i = 0; i < markers.length; i++) {
				IMarker marker = markers[i];
				Integer attribute = ((Integer) marker
						.getAttribute(CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME));
				if (attribute != null){
					int messageId = attribute.intValue();
					if(messageId == id){
						IMarkerResolution[] resolutions = IDE.getMarkerHelpRegistry()
								.getResolutions(marker);
						for (int j = 0; j < resolutions.length; j++) {
							IMarkerResolution resolution = resolutions[j];
							if (resolution.getClass().equals(resolutionClass)) {

								ValidatorManager.setStatus("TESTING");

								if(resolution instanceof TestableResolutionWithRefactoringProcessor){
									RefactoringProcessor processor = ((TestableResolutionWithRefactoringProcessor)resolution).getRefactoringProcessor();
									
									RefactoringStatus status = processor.checkInitialConditions(new NullProgressMonitor());
									
//									RefactoringStatusEntry[] entries = status.getEntries();
//									for(RefactoringStatusEntry entry : entries){
//										System.out.println("Refactor status - "+entry.getMessage());
//									}

									assertNull("Rename processor returns fatal error", status.getEntryMatchingSeverity(RefactoringStatus.FATAL));

									status = processor.checkFinalConditions(new NullProgressMonitor(), null);

//									entries = status.getEntries();
//									for(RefactoringStatusEntry entry : entries){
//										System.out.println("Refactor status - "+entry.getMessage());
//									}

									assertNull("Rename processor returns fatal error", status.getEntryMatchingSeverity(RefactoringStatus.FATAL));

									CompositeChange rootChange = (CompositeChange)processor.createChange(new NullProgressMonitor());

									rootChange.perform(new NullProgressMonitor());
								} else {
									resolution.run(marker);
								}

								TestUtil.waitForValidation();

								file = project.getFile(fileNames[0]);
								IMarker[] newMarkers = file.findMarkers(markerType, true,	IResource.DEPTH_INFINITE);

								assertTrue("Marker resolution did not decrease number of problems. was: "+markers.length+" now: "+newMarkers.length, newMarkers.length < markers.length);

								checkResults(project, fileNames, results);

								return;
							}
						}
						fail("Marker resolution: "+resolutionClass+" not found");
					}
				}
			}
			fail("Problem marker with id: "+id+" not found");
		}finally{
			restoreFiles(project, fileNames);
			TestUtil.waitForValidation();
		}
	}

	private void copyFiles(IProject project, String[] fileNames) throws CoreException{
		for(String fileName : fileNames){
			IFile file = project.getFile(fileName);
			IFile copyFile = project.getFile(fileName+".copy");

			if(copyFile.exists())
				copyFile.delete(true, null);

			InputStream is = null;
			try{
				is = file.getContents();
				copyFile.create(is, true, null);
			} finally {
				if(is!=null) {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void restoreFiles(IProject project, String[] fileNames) throws CoreException {
		for(String fileName : fileNames){
			IFile file = project.getFile(fileName);
			IFile copyFile = project.getFile(fileName+".copy");
			InputStream is = null;
			try{
				is = copyFile.getContents();
				file.setContents(is, true, false, null);
			} finally {
				if(is!=null) {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			copyFile.delete(true, null);
		}
	}

	private void checkResults(IProject project, String[] fileNames, String[] results) throws CoreException{
		for(int i = 0; i < results.length; i++){
			IFile file = project.getFile(fileNames[i]);
			IFile resultFile = project.getFile(results[i]);

			String fileContent = FileUtil.readStream(file);
			String resultContent = FileUtil.readStream(resultFile);
			
			assertEquals("Wrong result of resolution", resultContent, fileContent);
		}
	}

	public void testMakeProducerFieldStaticResolution() throws CoreException {
		checkResolution(tckProject, 
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/NonStaticProducerOfSessionBeanBroken.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/NonStaticProducerOfSessionBeanBroken.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.ILLEGAL_PRODUCER_FIELD_IN_SESSION_BEAN_ID,
				MakeFieldStaticMarkerResolution.class);
	}

	public void testMakeProducerMethodBusinessResolution() throws CoreException {
		checkResolution(
				tckProject,
				new String[]{
						"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/FooProducer.java",
						"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/FooProducerLocal.java"
				},
				new String[]{
						"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/FooProducer1.qfxresult",
						"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/FooProducerLocal.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.ILLEGAL_PRODUCER_METHOD_IN_SESSION_BEAN_ID,
				MakeMethodBusinessMarkerResolution.class);
	}

	public void testAddLocalBeanResolution() throws CoreException {
		checkResolution(
				tckProject,
				new String[]{
						"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/FooProducer.java"
				},
				new String[]{
						"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/FooProducer2.qfxresult",
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.ILLEGAL_PRODUCER_METHOD_IN_SESSION_BEAN_ID,
				AddLocalBeanMarkerResolution.class);
	}

	public void testMakeProducerMethodPublicResolution() throws CoreException {
		checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/FooProducerNoInterface.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/FooProducerNoInterface.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.ILLEGAL_PRODUCER_METHOD_IN_SESSION_BEAN_ID,
				MakeMethodPublicMarkerResolution.class);
	}
	
	public void testMakeObserverParamMethodBusinessResolution() throws CoreException {
		checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TibetanTerrier_Broken.java",
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/Terrier.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TibetanTerrier_Broken1.qfxresult",
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/Terrier.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.ILLEGAL_OBSERVER_IN_SESSION_BEAN_ID,
				MakeMethodBusinessMarkerResolution.class);
	}

	public void testAddLocalBeanResolution2() throws CoreException {
		checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TibetanTerrier_Broken.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TibetanTerrier_Broken2.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.ILLEGAL_OBSERVER_IN_SESSION_BEAN_ID,
				AddLocalBeanMarkerResolution.class);
	}

	public void testMakeObserverParamMethodPublicResolution() throws CoreException {
		checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TibetanTerrier_BrokenNoInterface.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TibetanTerrier_BrokenNoInterface.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.ILLEGAL_OBSERVER_IN_SESSION_BEAN_ID,
				MakeMethodPublicMarkerResolution.class);
	}

	public void testMakeDisposerParamMethodBusinessResolution() throws CoreException {
		checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/NotBusinessMethod_Broken.java",
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/LocalInt.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/NotBusinessMethod_Broken1.qfxresult",
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/LocalInt.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.ILLEGAL_DISPOSER_IN_SESSION_BEAN_ID,
				MakeMethodBusinessMarkerResolution.class);
	}

	public void testAddLocalBeanResolution3() throws CoreException {
		checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/NotBusinessMethod_Broken.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/NotBusinessMethod_Broken2.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.ILLEGAL_DISPOSER_IN_SESSION_BEAN_ID,
				AddLocalBeanMarkerResolution.class);
	}

	public void testMakeDisposerParamMethodPublicResolution() throws CoreException {
		checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/NotBusinessMethod_BrokenNoInterface.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/NotBusinessMethod_BrokenNoInterface.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.ILLEGAL_DISPOSER_IN_SESSION_BEAN_ID,
				MakeMethodPublicMarkerResolution.class);
	}

	public void testDeleteAllDisposerDuplicantsResolution() throws CoreException {
		checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TimestampLogger_Broken.java"
				},
//				new String[]{
//					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TimestampLogger_Broken.qfxresult"
//				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.MULTIPLE_DISPOSERS_FOR_PRODUCER_ID,
				DeleteAllDisposerDuplicantMarkerResolution.class);
	}

	public void testDeleteAllInjectedConstructorsResolution() throws CoreException {
		checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/Goose_Broken.java"
				},
//				new String[]{
//					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/Goose_Broken.qfxresult"
//				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.MULTIPLE_INJECTION_CONSTRUCTORS_ID,
				DeleteAllInjectedConstructorsMarkerResolution.class);
	}
	
	public void testAddSerializableInterfaceResolution() throws CoreException{
		checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/Hamina_Broken.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/Hamina_Broken.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.NOT_PASSIVATION_CAPABLE_BEAN_ID,
				AddSerializableInterfaceMarkerResolution.class);
	}

	public void testAddSerializableInterfaceResolution2() throws CoreException{
		checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/SecondBean.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/SecondBean.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.NOT_PASSIVATION_CAPABLE_BEAN_ID,
				AddSerializableInterfaceMarkerResolution.class);
	}

	public void testAddRetentionToQualifierResolution() throws CoreException{
		checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestQualifier1.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestQualifier1.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.MISSING_RETENTION_ANNOTATION_IN_QUALIFIER_TYPE_ID,
				AddRetentionAnnotationMarkerResolution.class);
	}

	public void testChangeRetentionToQualifierResolution() throws CoreException{
		checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestQualifier2.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestQualifier2.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.MISSING_RETENTION_ANNOTATION_IN_QUALIFIER_TYPE_ID,
				ChangeRetentionAnnotationMarkerResolution.class);
	}
	
	public void testAddRetentionToScopeResolution() throws CoreException{
		checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestScope1.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestScope1.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.MISSING_RETENTION_ANNOTATION_IN_SCOPE_TYPE_ID,
				AddRetentionAnnotationMarkerResolution.class);
	}

	public void testChangeRetentionToScopeResolution() throws CoreException{
		checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestScope2.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestScope2.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.MISSING_RETENTION_ANNOTATION_IN_SCOPE_TYPE_ID,
				ChangeRetentionAnnotationMarkerResolution.class);
	}
	
	public void testAddRetentionToStereotypeResolution() throws CoreException{
		checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestStereotype1.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestStereotype1.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.MISSING_RETENTION_ANNOTATION_IN_STEREOTYPE_TYPE_ID,
				AddRetentionAnnotationMarkerResolution.class);
	}

	public void testChangeRetentionToStereotypeResolution() throws CoreException{
		checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestStereotype2.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestStereotype2.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.MISSING_RETENTION_ANNOTATION_IN_STEREOTYPE_TYPE_ID,
				ChangeRetentionAnnotationMarkerResolution.class);
	}
	
	public void testAddTargetToStereotypeResolution() throws CoreException{
		checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestStereotype3.java"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.MISSING_TARGET_ANNOTATION_IN_STEREOTYPE_TYPE_ID,
				AddTargetAnnotationMarkerResolution.class);
	}

	public void testTargetRetentionToStereotypeResolution() throws CoreException{
		checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestStereotype4.java"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.MISSING_TARGET_ANNOTATION_IN_STEREOTYPE_TYPE_ID,
				ChangeTargetAnnotationMarkerResolution.class);
	}
	
	public void testAddTargetToQualifierResolution() throws CoreException{
		checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestQualifier3.java"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.MISSING_TARGET_ANNOTATION_IN_QUALIFIER_TYPE_ID,
				AddTargetAnnotationMarkerResolution.class);
	}

	public void testChangeTargetToQualifierResolution() throws CoreException{
		checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestQualifier4.java"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.MISSING_TARGET_ANNOTATION_IN_QUALIFIER_TYPE_ID,
				ChangeTargetAnnotationMarkerResolution.class);
	}
	
}