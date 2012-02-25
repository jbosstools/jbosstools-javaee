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
package org.jboss.tools.seam.ui.test.marker;

import java.io.IOException;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.jboss.tools.common.base.test.MarkerResolutionTestUtil;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.SeamPreferences;
import org.jboss.tools.seam.internal.core.validation.SeamCoreValidator;
import org.jboss.tools.seam.ui.marker.AddAnnotatedMethodMarkerResolution;
import org.jboss.tools.seam.ui.marker.AddAnnotationMarkerResolution;
import org.jboss.tools.seam.ui.marker.ChangeScopeMarkerResolution;
import org.jboss.tools.seam.ui.marker.DeleteAnnotationMarkerResolution;
import org.jboss.tools.seam.ui.marker.RenameAnnotationMarkerResolution;
import org.jboss.tools.test.util.JobUtils;

/**
 * @author Daniel Azarov
 * 
 */
public class SeamMarkerResolutionTest extends TestCase {

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
		MarkerResolutionTestUtil.checkResolution(project, 
				new String[]{
					"src/action/org/domain/SeamWebWarTestProject/session/StatefulDuplicateRemoveComponent.java"
				},
				SeamCoreValidator.PROBLEM_TYPE,
				SeamCoreValidator.MESSAGE_ID_ATTRIBUTE_NAME,
				SeamCoreValidator.DUPLICATE_REMOVE_MESSAGE_ID,
				DeleteAnnotationMarkerResolution.class);
	}

	public void testDuplicateDestroyAnnotationResolution() throws CoreException {
		MarkerResolutionTestUtil.checkResolution(project, 
				new String[]{
					"src/action/org/domain/SeamWebWarTestProject/session/StatefulDuplicateDestroyComponent.java"
				},
				SeamCoreValidator.PROBLEM_TYPE,
				SeamCoreValidator.MESSAGE_ID_ATTRIBUTE_NAME,
				SeamCoreValidator.DUPLICATE_DESTROY_MESSAGE_ID,
				DeleteAnnotationMarkerResolution.class);
	}

//	public void testDuplicateDestroyAnnotationResolution2() throws CoreException {
//		MarkerResolutionTestUtil.checkResolution(project, 
//				new String[]{
//					"src/action/org/domain/SeamWebWarTestProject/session/StatelessClass.java"
//				},
//				SeamCoreValidator.PROBLEM_TYPE,
//				SeamCoreValidator.MESSAGE_ID_ATTRIBUTE_NAME,
//				SeamCoreValidator.DUPLICATE_DESTROY_MESSAGE_ID,
//				DeleteAnnotationMarkerResolution.class);
//	}
	
	public void testDuplicateCreateAnnotationResolution() throws CoreException {
		MarkerResolutionTestUtil.checkResolution(project, 
				new String[]{
					"src/action/org/domain/SeamWebWarTestProject/session/StatefulDuplicateCreateComponent.java"
				},
				SeamCoreValidator.PROBLEM_TYPE,
				SeamCoreValidator.MESSAGE_ID_ATTRIBUTE_NAME,
				SeamCoreValidator.DUPLICATE_CREATE_MESSAGE_ID,
				DeleteAnnotationMarkerResolution.class);
	}

	public void testDuplicateUnwrapAnnotationResolution() throws CoreException {
		MarkerResolutionTestUtil.checkResolution(project, 
				new String[]{
					"src/action/org/domain/SeamWebWarTestProject/session/StatefulDuplicateUnwrapComponent.java"
				},
				SeamCoreValidator.PROBLEM_TYPE,
				SeamCoreValidator.MESSAGE_ID_ATTRIBUTE_NAME,
				SeamCoreValidator.DUPLICATE_UNWRAP_MESSAGE_ID,
				DeleteAnnotationMarkerResolution.class);
	}

	public void testOnlyComponentClassCanHaveCreateMethodResolution() throws CoreException {
		MarkerResolutionTestUtil.checkResolution(project, 
				new String[]{
					"src/action/org/domain/SeamWebWarTestProject/session/NonComponentWithCreateMethod.java"
				},
				SeamCoreValidator.PROBLEM_TYPE,
				SeamCoreValidator.MESSAGE_ID_ATTRIBUTE_NAME,
				SeamCoreValidator.CREATE_DOESNT_BELONG_TO_COMPONENT_MESSAGE_ID,
				DeleteAnnotationMarkerResolution.class);
	}

	public void testOnlyComponentClassCanHaveCreateMethodResolution2() throws CoreException {
		MarkerResolutionTestUtil.checkResolution(project, 
				new String[]{
					"src/action/org/domain/SeamWebWarTestProject/session/NonComponentWithCreateMethod.java"
				},
				SeamCoreValidator.PROBLEM_TYPE,
				SeamCoreValidator.MESSAGE_ID_ATTRIBUTE_NAME,
				SeamCoreValidator.CREATE_DOESNT_BELONG_TO_COMPONENT_MESSAGE_ID,
				AddAnnotationMarkerResolution.class);
	}

	public void testOnlyComponentClassCanHaveUnwrapMethodResolution() throws CoreException {
		MarkerResolutionTestUtil.checkResolution(project, 
				new String[]{
					"src/action/org/domain/SeamWebWarTestProject/session/NonComponentWithUnwrapMethod.java"
				},
				SeamCoreValidator.PROBLEM_TYPE,
				SeamCoreValidator.MESSAGE_ID_ATTRIBUTE_NAME,
				SeamCoreValidator.UNWRAP_DOESNT_BELONG_TO_COMPONENT_MESSAGE_ID,
				DeleteAnnotationMarkerResolution.class);
	}

	public void testOnlyComponentClassCanHaveUnwrapMethodResolution2() throws CoreException {
		MarkerResolutionTestUtil.checkResolution(project, 
				new String[]{
					"src/action/org/domain/SeamWebWarTestProject/session/NonComponentWithUnwrapMethod.java"
				},
				SeamCoreValidator.PROBLEM_TYPE,
				SeamCoreValidator.MESSAGE_ID_ATTRIBUTE_NAME,
				SeamCoreValidator.UNWRAP_DOESNT_BELONG_TO_COMPONENT_MESSAGE_ID,
				AddAnnotationMarkerResolution.class);
	}

	public void testOnlyComponentClassCanHaveObserverMethodResolution() throws CoreException {
		MarkerResolutionTestUtil.checkResolution(project, 
				new String[]{
					"src/action/org/domain/SeamWebWarTestProject/session/NonComponentWithObserverMethod.java"
				},
				SeamCoreValidator.PROBLEM_TYPE,
				SeamCoreValidator.MESSAGE_ID_ATTRIBUTE_NAME,
				SeamCoreValidator.OBSERVER_DOESNT_BELONG_TO_COMPONENT_MESSAGE_ID,
				DeleteAnnotationMarkerResolution.class);
	}

	public void testOnlyComponentClassCanHaveObserverMethodResolution2() throws CoreException {
		MarkerResolutionTestUtil.checkResolution(project, 
				new String[]{
					"src/action/org/domain/SeamWebWarTestProject/session/NonComponentWithObserverMethod.java"
				},
				SeamCoreValidator.PROBLEM_TYPE,
				SeamCoreValidator.MESSAGE_ID_ATTRIBUTE_NAME,
				SeamCoreValidator.OBSERVER_DOESNT_BELONG_TO_COMPONENT_MESSAGE_ID,
				AddAnnotationMarkerResolution.class);
	}

	public void testDuplicateComponentNameResolution() throws CoreException {
		MarkerResolutionTestUtil.checkResolution(project, 
				new String[]{
					"src/action/org/domain/SeamWebWarTestProject/session/DuplicateComponent1.java"
				},
				SeamCoreValidator.PROBLEM_TYPE,
				SeamCoreValidator.MESSAGE_ID_ATTRIBUTE_NAME,
				SeamCoreValidator.NONUNIQUE_COMPONENT_NAME_MESSAGE_ID,
				DeleteAnnotationMarkerResolution.class);
	}

	public void testDuplicateComponentNameResolution2() throws CoreException {
		MarkerResolutionTestUtil.checkResolution(project, 
				new String[]{
					"src/action/org/domain/SeamWebWarTestProject/session/DuplicateComponent1.java"
				},
				SeamCoreValidator.PROBLEM_TYPE,
				SeamCoreValidator.MESSAGE_ID_ATTRIBUTE_NAME,
				SeamCoreValidator.NONUNIQUE_COMPONENT_NAME_MESSAGE_ID,
				RenameAnnotationMarkerResolution.class);
	}

	public void testAddRemoveMethodResolution() throws CoreException {
		MarkerResolutionTestUtil.checkResolution(project, 
				new String[]{
					"src/action/org/domain/SeamWebWarTestProject/session/StatefulComponentWithoutRemove.java"
				},
				SeamCoreValidator.PROBLEM_TYPE,
				SeamCoreValidator.MESSAGE_ID_ATTRIBUTE_NAME,
				SeamCoreValidator.STATEFUL_COMPONENT_DOES_NOT_CONTAIN_REMOVE_ID,
				AddAnnotatedMethodMarkerResolution.class);
	}

	public void testAddDestroyMethodResolution() throws CoreException {
		MarkerResolutionTestUtil.checkResolution(project, 
				new String[]{
					"src/action/org/domain/SeamWebWarTestProject/session/StatefulComponentWithoutDestroy.java"
				},
				SeamCoreValidator.PROBLEM_TYPE,
				SeamCoreValidator.MESSAGE_ID_ATTRIBUTE_NAME,
				SeamCoreValidator.STATEFUL_COMPONENT_DOES_NOT_CONTAIN_DESTROY_ID,
				AddAnnotatedMethodMarkerResolution.class);
	}

	public void testChangeScopeResolution() throws CoreException {
		MarkerResolutionTestUtil.checkResolution(project, 
				new String[]{
					"src/action/org/domain/SeamWebWarTestProject/session/StatefulComponentWithWrongScope.java"
				},
				SeamCoreValidator.PROBLEM_TYPE,
				SeamCoreValidator.MESSAGE_ID_ATTRIBUTE_NAME,
				SeamCoreValidator.STATEFUL_COMPONENT_WRONG_SCOPE_ID,
				ChangeScopeMarkerResolution.class);
	}

	public void testChangeScopeResolution2() throws CoreException {
		MarkerResolutionTestUtil.checkResolution(project, 
				new String[]{
					"src/action/org/domain/SeamWebWarTestProject/entity/EntityComponentWithWrongScope.java"
				},
				SeamCoreValidator.PROBLEM_TYPE,
				SeamCoreValidator.MESSAGE_ID_ATTRIBUTE_NAME,
				SeamCoreValidator.ENTITY_COMPONENT_WRONG_SCOPE_ID,
				ChangeScopeMarkerResolution.class);
	}

//	public void fixMeTestAddSetterForProperty() throws CoreException {
//		String TARGET_FILE_NAME = "WebContent/WEB-INF/components.xml";
//		copyContentsFile(TARGET_FILE_NAME, "WebContent/WEB-INF/components.3");
//		
//		project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, SeamCoreBuilder.BUILDER_ID, null, null);
//		
//		JobUtils.waitForIdle();
//		
//		IFile file = project.getFile(TARGET_FILE_NAME);
//		
//		assertTrue("File - "+TARGET_FILE_NAME+" must be exists",file.exists());
//		
//		IMarker[] markers = file.findMarkers(SeamCoreValidator.PROBLEM_TYPE, true,	IResource.DEPTH_INFINITE);
//		
//		assertTrue("Problem marker not found", markers.length > 0);
//		
//		boolean found = false;
//		for (int i = 0; i < markers.length; i++) {
//			IMarker marker = markers[i];
//			IMarkerResolution[] resolutions = IDE.getMarkerHelpRegistry()
//					.getResolutions(marker);
//			//checkForConfigureProblemSeverity(resolutions);
//			//checkForAddSuppressWarnings(file, marker, resolutions);
//			for (int j = 0; j < resolutions.length; j++) {
//				IMarkerResolution resolution = resolutions[j];
//				if (resolution instanceof AddSetterMarkerResolution) {
//					found = true;
//					break;
//				}
//			}
//			if (found) {
//				break;
//			}
//		}
//		assertTrue("The quickfix \"Add setter for 'abc' property in 'org.domain.SeamWebWarTestProject.session.StatefulComponentWithAbcField' class\" doesn't exist.", found);
//	}
	

}
