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
package org.jboss.tools.seam.ui.test.marker;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.ide.IDE;
import org.jboss.tools.seam.ui.marker.AddAnnotaionMarkerResolution;
import org.jboss.tools.seam.ui.marker.DeleteAnnotaionMarkerResolution;
import org.jboss.tools.test.util.JobUtils;

/**
 * @author Daniel Azarov
 * 
 */
public class SeamMarkerResolutionTest extends TestCase {
	public static final String MARKER_TYPE = "org.eclipse.wst.validation.problemmarker";

	private IProject project;

	@Override
	protected void setUp() throws Exception {
		JobUtils.waitForIdle();
		IResource project = ResourcesPlugin.getWorkspace().getRoot().findMember("SeamWebWarTestProject");
		this.project = project.getProject();
	}

	@Override
	protected void tearDown() throws Exception {
	}

	public void testDuplicateRemoveAnnotationResolution() throws CoreException {
		String TARGET_FILE_NAME = "src/action/org/domain/SeamWebWarTestProject/session/StatefulDuplicateRemoveComponent.java";
		IFile file = project.getFile(TARGET_FILE_NAME);
		
		assertTrue("File - "+TARGET_FILE_NAME+" must be exists",file.exists());
		
		IMarker[] markers = file.findMarkers(MARKER_TYPE, true,	IResource.DEPTH_INFINITE);
		
		boolean found = false;
		for (int i = 0; i < markers.length; i++) {
			IMarker marker = markers[i];
			IMarkerResolution[] resolutions = IDE.getMarkerHelpRegistry()
					.getResolutions(marker);
			for (int j = 0; j < resolutions.length; j++) {
				IMarkerResolution resolution = resolutions[j];
				if (resolution instanceof DeleteAnnotaionMarkerResolution) {
					assertEquals("javax.ejb.Remove", ((DeleteAnnotaionMarkerResolution)resolution).getQualifiedName());
					found = true;
					break;
				}
			}
			if (found) {
				break;
			}
		}
		assertTrue("The quickfix \"Delete @Remove annotation\" doesn't exist.", found);
	}
	
	public void testDuplicateDestroyAnnotationResolution() throws CoreException {
		String TARGET_FILE_NAME = "src/action/org/domain/SeamWebWarTestProject/session/StatefulDuplicateDestroyComponent.java";
		IFile file = project.getFile(TARGET_FILE_NAME);
		
		assertTrue("File - "+TARGET_FILE_NAME+" must be exists",file.exists());
		
		IMarker[] markers = file.findMarkers(MARKER_TYPE, true,	IResource.DEPTH_INFINITE);
		
		boolean found = false;
		for (int i = 0; i < markers.length; i++) {
			IMarker marker = markers[i];
			IMarkerResolution[] resolutions = IDE.getMarkerHelpRegistry()
					.getResolutions(marker);
			for (int j = 0; j < resolutions.length; j++) {
				IMarkerResolution resolution = resolutions[j];
				if (resolution instanceof DeleteAnnotaionMarkerResolution) {
					assertEquals("org.jboss.seam.annotations.Destroy", ((DeleteAnnotaionMarkerResolution)resolution).getQualifiedName());
					found = true;
					break;
				}
			}
			if (found) {
				break;
			}
		}
		assertTrue("The quickfix \"Delete @Destroy annotation\" doesn't exist.", found);
	}

	public void testDuplicateDestroyAnnotationResolution2() throws CoreException {
		String TARGET_FILE_NAME = "src/action/org/domain/SeamWebWarTestProject/session/StatelessClass.java";
		IFile file = project.getFile(TARGET_FILE_NAME);
		
		assertTrue("File - "+TARGET_FILE_NAME+" must be exists",file.exists());
		
		IMarker[] markers = file.findMarkers(MARKER_TYPE, true,	IResource.DEPTH_INFINITE);
		
		boolean found = false;
		for (int i = 0; i < markers.length; i++) {
			IMarker marker = markers[i];
			IMarkerResolution[] resolutions = IDE.getMarkerHelpRegistry()
					.getResolutions(marker);
			for (int j = 0; j < resolutions.length; j++) {
				IMarkerResolution resolution = resolutions[j];
				if (resolution instanceof DeleteAnnotaionMarkerResolution) {
					assertEquals("org.jboss.seam.annotations.Destroy", ((DeleteAnnotaionMarkerResolution)resolution).getQualifiedName());
					found = true;
					break;
				}
			}
			if (found) {
				break;
			}
		}
		assertTrue("The quickfix \"Delete @Destroy annotation\" doesn't exist.", found);
	}

	public void testDuplicateCreateAnnotationResolution() throws CoreException {
		String TARGET_FILE_NAME = "src/action/org/domain/SeamWebWarTestProject/session/StatefulDuplicateCreateComponent.java";
		IFile file = project.getFile(TARGET_FILE_NAME);
		
		assertTrue("File - "+TARGET_FILE_NAME+" must be exists",file.exists());
		
		IMarker[] markers = file.findMarkers(MARKER_TYPE, true,	IResource.DEPTH_INFINITE);
		
		boolean found = false;
		for (int i = 0; i < markers.length; i++) {
			IMarker marker = markers[i];
			IMarkerResolution[] resolutions = IDE.getMarkerHelpRegistry()
					.getResolutions(marker);
			for (int j = 0; j < resolutions.length; j++) {
				IMarkerResolution resolution = resolutions[j];
				if (resolution instanceof DeleteAnnotaionMarkerResolution) {
					assertEquals("org.jboss.seam.annotations.Create", ((DeleteAnnotaionMarkerResolution)resolution).getQualifiedName());
					found = true;
					break;
				}
			}
			if (found) {
				break;
			}
		}
		assertTrue("The quickfix \"Delete @Create annotation\" doesn't exist.", found);
	}

	public void testDuplicateUnwrapAnnotationResolution() throws CoreException {
		String TARGET_FILE_NAME = "src/action/org/domain/SeamWebWarTestProject/session/StatefulDuplicateUnwrapComponent.java";
		IFile file = project.getFile(TARGET_FILE_NAME);
		
		assertTrue("File - "+TARGET_FILE_NAME+" must be exists",file.exists());
		
		IMarker[] markers = file.findMarkers(MARKER_TYPE, true,	IResource.DEPTH_INFINITE);
		
		boolean found = false;
		for (int i = 0; i < markers.length; i++) {
			IMarker marker = markers[i];
			IMarkerResolution[] resolutions = IDE.getMarkerHelpRegistry()
					.getResolutions(marker);
			for (int j = 0; j < resolutions.length; j++) {
				IMarkerResolution resolution = resolutions[j];
				if (resolution instanceof DeleteAnnotaionMarkerResolution) {
					assertEquals("org.jboss.seam.annotations.Unwrap", ((DeleteAnnotaionMarkerResolution)resolution).getQualifiedName());
					found = true;
					break;
				}
			}
			if (found) {
				break;
			}
		}
		assertTrue("The quickfix \"Delete @Unwrap annotation\" doesn't exist.", found);
	}
	
	public void testOnlyComponentClassCanHaveCreateMethodResolution() throws CoreException {
		String TARGET_FILE_NAME = "src/action/org/domain/SeamWebWarTestProject/session/NonComponentWithCreateMethod.java";
		IFile file = project.getFile(TARGET_FILE_NAME);
		
		assertTrue("File - "+TARGET_FILE_NAME+" must be exists",file.exists());
		
		IMarker[] markers = file.findMarkers(MARKER_TYPE, true,	IResource.DEPTH_INFINITE);
		
		boolean dFound = false;
		boolean cFound = false;
		for (int i = 0; i < markers.length; i++) {
			IMarker marker = markers[i];
			IMarkerResolution[] resolutions = IDE.getMarkerHelpRegistry()
					.getResolutions(marker);
			for (int j = 0; j < resolutions.length; j++) {
				IMarkerResolution resolution = resolutions[j];
				if (resolution instanceof DeleteAnnotaionMarkerResolution) {
					assertEquals("org.jboss.seam.annotations.Create", ((DeleteAnnotaionMarkerResolution)resolution).getQualifiedName());
					dFound = true;
				}
				if (resolution instanceof AddAnnotaionMarkerResolution) {
					assertEquals("org.jboss.seam.annotations.Name", ((AddAnnotaionMarkerResolution)resolution).getQualifiedName());
					cFound = true;
				}
			}
			if (dFound && cFound) {
				break;
			}
		}
		assertTrue("The quickfix \"Delete @Create annotation\" doesn't exist.", dFound);
		assertTrue("The quickfix \"Add @Name annotation\" doesn't exist.", cFound);
	}
	
	public void testOnlyComponentClassCanHaveUnwrapMethodResolution() throws CoreException {
		String TARGET_FILE_NAME = "src/action/org/domain/SeamWebWarTestProject/session/NonComponentWithUnwrapMethod.java";
		IFile file = project.getFile(TARGET_FILE_NAME);
		
		assertTrue("File - "+TARGET_FILE_NAME+" must be exists",file.exists());
		
		IMarker[] markers = file.findMarkers(MARKER_TYPE, true,	IResource.DEPTH_INFINITE);
		
		boolean dFound = false;
		boolean cFound = false;
		for (int i = 0; i < markers.length; i++) {
			IMarker marker = markers[i];
			IMarkerResolution[] resolutions = IDE.getMarkerHelpRegistry()
					.getResolutions(marker);
			for (int j = 0; j < resolutions.length; j++) {
				IMarkerResolution resolution = resolutions[j];
				if (resolution instanceof DeleteAnnotaionMarkerResolution) {
					assertEquals("org.jboss.seam.annotations.Unwrap", ((DeleteAnnotaionMarkerResolution)resolution).getQualifiedName());
					dFound = true;
				}
				if (resolution instanceof AddAnnotaionMarkerResolution) {
					assertEquals("org.jboss.seam.annotations.Name", ((AddAnnotaionMarkerResolution)resolution).getQualifiedName());
					cFound = true;
				}
			}
			if (dFound && cFound) {
				break;
			}
		}
		assertTrue("The quickfix \"Delete @Unwrap annotation\" doesn't exist.", dFound);
		assertTrue("The quickfix \"Add @Name annotation\" doesn't exist.", cFound);
	}
	
	public void testOnlyComponentClassCanHaveObserverMethodResolution() throws CoreException {
		String TARGET_FILE_NAME = "src/action/org/domain/SeamWebWarTestProject/session/NonComponentWithObserverMethod.java";
		IFile file = project.getFile(TARGET_FILE_NAME);
		
		assertTrue("File - "+TARGET_FILE_NAME+" must be exists",file.exists());
		
		IMarker[] markers = file.findMarkers(MARKER_TYPE, true,	IResource.DEPTH_INFINITE);
		
		boolean dFound = false;
		boolean cFound = false;
		for (int i = 0; i < markers.length; i++) {
			IMarker marker = markers[i];
			IMarkerResolution[] resolutions = IDE.getMarkerHelpRegistry()
					.getResolutions(marker);
			for (int j = 0; j < resolutions.length; j++) {
				IMarkerResolution resolution = resolutions[j];
				if (resolution instanceof DeleteAnnotaionMarkerResolution) {
					assertEquals("org.jboss.seam.annotations.Observer", ((DeleteAnnotaionMarkerResolution)resolution).getQualifiedName());
					dFound = true;
				}
				if (resolution instanceof AddAnnotaionMarkerResolution) {
					assertEquals("org.jboss.seam.annotations.Name", ((AddAnnotaionMarkerResolution)resolution).getQualifiedName());
					cFound = true;
				}
			}
			if (dFound && cFound) {
				break;
			}
		}
		assertTrue("The quickfix \"Delete @Observer annotation\" doesn't exist.", dFound);
		assertTrue("The quickfix \"Add @Name annotation\" doesn't exist.", cFound);
	}

}
