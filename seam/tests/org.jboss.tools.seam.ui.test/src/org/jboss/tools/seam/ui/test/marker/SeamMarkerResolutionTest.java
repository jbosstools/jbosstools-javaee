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

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.ide.IDE;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.SeamPreferences;
import org.jboss.tools.seam.ui.marker.AddAnnotatedMethodMarkerResolution;
import org.jboss.tools.seam.ui.marker.AddAnnotationMarkerResolution;
import org.jboss.tools.seam.ui.marker.DeleteAnnotationMarkerResolution;
import org.jboss.tools.seam.ui.marker.RenameAnnotationMarkerResolution;
import org.jboss.tools.test.util.JobUtils;

/**
 * @author Daniel Azarov
 * 
 */
public class SeamMarkerResolutionTest extends TestCase {
	public static final String MARKER_TYPE = "org.eclipse.wst.validation.problemmarker";
	//public static final String TEXT_MARKER_TYPE = "org.eclipse.wst.validation.textmarker";

	private IProject project;

	@Override
	protected void setUp() throws Exception {
		JobUtils.waitForIdle();
		IResource project = ResourcesPlugin.getWorkspace().getRoot().findMember("SeamWebWarTestProject");
		this.project = project.getProject();
		
		IPreferenceStore store = SeamCorePlugin.getDefault().getPreferenceStore();
		store.putValue(SeamPreferences.STATEFUL_COMPONENT_DOES_NOT_CONTENT_REMOVE, SeamPreferences.ERROR);
		store.putValue(SeamPreferences.STATEFUL_COMPONENT_DOES_NOT_CONTENT_DESTROY, SeamPreferences.ERROR);

		if(store instanceof IPersistentPreferenceStore) {
			try {
				((IPersistentPreferenceStore)store).save();
			} catch (IOException e) {
				SeamCorePlugin.getPluginLog().logError(e);
			}
		}
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
				if (resolution instanceof DeleteAnnotationMarkerResolution) {
					assertEquals("javax.ejb.Remove", ((DeleteAnnotationMarkerResolution)resolution).getQualifiedName());
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
				if (resolution instanceof DeleteAnnotationMarkerResolution) {
					assertEquals("org.jboss.seam.annotations.Destroy", ((DeleteAnnotationMarkerResolution)resolution).getQualifiedName());
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
				if (resolution instanceof DeleteAnnotationMarkerResolution) {
					assertEquals("org.jboss.seam.annotations.Destroy", ((DeleteAnnotationMarkerResolution)resolution).getQualifiedName());
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
				if (resolution instanceof DeleteAnnotationMarkerResolution) {
					assertEquals("org.jboss.seam.annotations.Create", ((DeleteAnnotationMarkerResolution)resolution).getQualifiedName());
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
				if (resolution instanceof DeleteAnnotationMarkerResolution) {
					assertEquals("org.jboss.seam.annotations.Unwrap", ((DeleteAnnotationMarkerResolution)resolution).getQualifiedName());
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
				if (resolution instanceof DeleteAnnotationMarkerResolution) {
					assertEquals("org.jboss.seam.annotations.Create", ((DeleteAnnotationMarkerResolution)resolution).getQualifiedName());
					dFound = true;
				}
				if (resolution instanceof AddAnnotationMarkerResolution) {
					assertEquals("org.jboss.seam.annotations.Name", ((AddAnnotationMarkerResolution)resolution).getQualifiedName());
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
				if (resolution instanceof DeleteAnnotationMarkerResolution) {
					assertEquals("org.jboss.seam.annotations.Unwrap", ((DeleteAnnotationMarkerResolution)resolution).getQualifiedName());
					dFound = true;
				}
				if (resolution instanceof AddAnnotationMarkerResolution) {
					assertEquals("org.jboss.seam.annotations.Name", ((AddAnnotationMarkerResolution)resolution).getQualifiedName());
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
				if (resolution instanceof DeleteAnnotationMarkerResolution) {
					assertEquals("org.jboss.seam.annotations.Observer", ((DeleteAnnotationMarkerResolution)resolution).getQualifiedName());
					dFound = true;
				}
				if (resolution instanceof AddAnnotationMarkerResolution) {
					assertEquals("org.jboss.seam.annotations.Name", ((AddAnnotationMarkerResolution)resolution).getQualifiedName());
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
	
	public void testDuplicateComponentNameResolution() throws CoreException {
		String TARGET_FILE_NAME = "src/action/org/domain/SeamWebWarTestProject/session/DuplicateComponent1.java";
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
				if (resolution instanceof DeleteAnnotationMarkerResolution) {
					assertEquals("org.jboss.seam.annotations.Name", ((DeleteAnnotationMarkerResolution)resolution).getQualifiedName());
					dFound = true;
				}
				if (resolution instanceof RenameAnnotationMarkerResolution) {
					assertEquals("org.jboss.seam.annotations.Name", ((RenameAnnotationMarkerResolution)resolution).getQualifiedName());
					cFound = true;
				}
			}
			if (dFound && cFound) {
				break;
			}
		}
		assertTrue("The quickfix \"Delete @Name annotation\" doesn't exist.", dFound);
		assertTrue("The quickfix \"Rename @Name annotation\" doesn't exist.", cFound);
	}

	public void testAddRemoveMethodResolution() throws CoreException {
		String TARGET_FILE_NAME = "src/action/org/domain/SeamWebWarTestProject/session/StatefulComponentWithoutRemove.java";
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
				if (resolution instanceof AddAnnotatedMethodMarkerResolution) {
					assertEquals("javax.ejb.Remove", ((AddAnnotatedMethodMarkerResolution)resolution).getQualifiedName());
					found = true;
					break;
				}
			}
			if (found) {
				break;
			}
		}
		assertTrue("The quickfix \"Add @Remove annotated method\" doesn't exist.", found);
	}

	public void testAddDestroyMethodResolution() throws CoreException {
		String TARGET_FILE_NAME = "src/action/org/domain/SeamWebWarTestProject/session/StatefulComponentWithoutDestroy.java";
		IFile file = project.getFile(TARGET_FILE_NAME);
		
		assertTrue("File - "+TARGET_FILE_NAME+" must be exists",file.exists());
		
		IMarker[] markers = file.findMarkers(MARKER_TYPE, true, IResource.DEPTH_INFINITE);
		
		boolean found = false;
		for (int i = 0; i < markers.length; i++) {
			IMarker marker = markers[i];
			IMarkerResolution[] resolutions = IDE.getMarkerHelpRegistry()
					.getResolutions(marker);
			for (int j = 0; j < resolutions.length; j++) {
				IMarkerResolution resolution = resolutions[j];
				if (resolution instanceof AddAnnotatedMethodMarkerResolution) {
					assertEquals("org.jboss.seam.annotations.Destroy", ((AddAnnotatedMethodMarkerResolution)resolution).getQualifiedName());
					found = true;
					break;
				}
			}
			if (found) {
				break;
			}
		}
		assertTrue("The quickfix \"Add @Destroy annotated method\" doesn't exist.", found);
	}

}
