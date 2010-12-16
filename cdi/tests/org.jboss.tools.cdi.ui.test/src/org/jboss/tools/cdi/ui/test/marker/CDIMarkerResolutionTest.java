/*************************************************************************************
 * Copyright (c) 2008-2009 JBoss by Red Hat and others.
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
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.ide.IDE;
import org.jboss.tools.cdi.core.test.tck.validation.ValidationTest;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationErrorManager;
import org.jboss.tools.cdi.ui.marker.AddLocalBeanMarkerResolution;
import org.jboss.tools.cdi.ui.marker.MakeFieldStaticMarkerResolution;
import org.jboss.tools.cdi.ui.marker.MakeMethodBusinessMarkerResolution;
import org.jboss.tools.cdi.ui.marker.MakeMethodPublicMarkerResolution;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.test.util.JobUtils;

/**
 * @author Daniel Azarov
 * 
 */
public class CDIMarkerResolutionTest  extends ValidationTest {
	public static final String MARKER_TYPE = "org.jboss.tools.cdi.core.cdiproblem";
	
	private void checkResolution(IProject project, String[] fileNames, String markerType, String idName, int id, Class<? extends IMarkerResolution> resolutionClass) throws CoreException {
		checkResolution(project, fileNames, new String[]{}, markerType, idName, id, resolutionClass);
	}
	
	private void checkResolution(IProject project, String[] fileNames, String[] results, String markerType, String idName, int id, Class<? extends IMarkerResolution> resolutionClass) throws CoreException {
		IFile file = project.getFile(fileNames[0]);
		
		assertTrue("File - "+file.getFullPath()+" must be exist",file.exists());
		
		copyFiles(project, fileNames);
		
		try{
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
								
								resolution.run(marker);
								
								refresh(project);
								
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
			
			refresh(project);
		}
	}
	
	private void refresh(IProject project) throws CoreException{
		project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		JobUtils.waitForIdle();
		project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
		JobUtils.waitForIdle();
		
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
				MARKER_TYPE,
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
				MARKER_TYPE,
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
				MARKER_TYPE,
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
				MARKER_TYPE,
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
				MARKER_TYPE,
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
				MARKER_TYPE,
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
				MARKER_TYPE,
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
				MARKER_TYPE,
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
				MARKER_TYPE,
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
				MARKER_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.ILLEGAL_DISPOSER_IN_SESSION_BEAN_ID,
				MakeMethodPublicMarkerResolution.class);
	}

}
