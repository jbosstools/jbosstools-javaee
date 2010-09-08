/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.seam.ui.marker;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator2;
import org.jboss.tools.seam.internal.core.validation.SeamCoreValidator;
import org.jboss.tools.seam.ui.SeamGuiPlugin;
import org.jboss.tools.seam.ui.SeamUIMessages;

/**
 * @author Daniel Azarov
 */
public class SeamProblemMarkerResolutionGenerator  implements IMarkerResolutionGenerator2 {
	private static final String JAVA_EXTENSION = "java"; //$NON-NLS-1$

	public IMarkerResolution[] getResolutions(IMarker marker) {
		try{
			return findResolutions(marker);
		}catch(CoreException ex){
			SeamGuiPlugin.getPluginLog().logError(ex);
		}
		return new IMarkerResolution[]{};
	}
	
	private IMarkerResolution[] findResolutions(IMarker marker) throws CoreException{
		Integer attribute = ((Integer)marker.getAttribute(SeamCoreValidator.MESSAGE_ID_ATTRIBUTE_NAME));
		if(attribute == null)
			return new IMarkerResolution[]{};
		
		int messageId = attribute.intValue();
		
		IFile file = (IFile)marker.getResource();
		
		if(!JAVA_EXTENSION.equals(file.getFileExtension()))
			return new IMarkerResolution[]{}; 
		
		attribute =  ((Integer)marker.getAttribute(IMarker.CHAR_START));
		if(attribute == null)
			return new IMarkerResolution[]{};
		int start = attribute.intValue();
		
		attribute = ((Integer)marker.getAttribute(IMarker.CHAR_END));
		if(attribute == null)
			return new IMarkerResolution[]{};
		int end = attribute.intValue();
		
		if(messageId == SeamCoreValidator.NONUNIQUE_COMPONENT_NAME_MESSAGE_ID){
			return new IMarkerResolution[]{
					new RenameAnnotationMarkerResolution(SeamUIMessages.RENAME_NAME_ANNOTATION_MARKER_RESOLUTION_TITLE, "org.jboss.seam.annotations.Name", file, start, end),
					new DeleteAnnotationMarkerResolution(SeamUIMessages.DELETE_NAME_ANNOTATION_MARKER_RESOLUTION_TITLE, "org.jboss.seam.annotations.Name",file, start, end)
				};
		} else if(messageId == SeamCoreValidator.DUPLICATE_REMOVE_MESSAGE_ID)
			return new IMarkerResolution[]{new DeleteAnnotationMarkerResolution(SeamUIMessages.DELETE_REMOVE_ANNOTATION_MARKER_RESOLUTION_TITLE, "javax.ejb.Remove", file, start, end)};
		else if(messageId == SeamCoreValidator.DUPLICATE_DESTROY_MESSAGE_ID)
			return new IMarkerResolution[]{new DeleteAnnotationMarkerResolution(SeamUIMessages.DELETE_DESTROY_ANNOTATION_MARKER_RESOLUTION_TITLE, "org.jboss.seam.annotations.Destroy", file, start, end)};
		else if(messageId == SeamCoreValidator.DUPLICATE_CREATE_MESSAGE_ID)
			return new IMarkerResolution[]{new DeleteAnnotationMarkerResolution(SeamUIMessages.DELETE_CREATE_ANNOTATION_MARKER_RESOLUTION_TITLE, "org.jboss.seam.annotations.Create", file, start, end)};
		else if(messageId == SeamCoreValidator.DUPLICATE_UNWRAP_MESSAGE_ID)
			return new IMarkerResolution[]{new DeleteAnnotationMarkerResolution(SeamUIMessages.DELETE_UNWRAP_ANNOTATION_MARKER_RESOLUTION_TITLE, "org.jboss.seam.annotations.Unwrap", file, start, end)};
		else if(messageId == SeamCoreValidator.DESTROY_METHOD_BELONGS_TO_STATELESS_SESSION_BEAN_MESSAGE_ID)
			return new IMarkerResolution[]{new DeleteAnnotationMarkerResolution(SeamUIMessages.DELETE_DESTROY_ANNOTATION_MARKER_RESOLUTION_TITLE, "org.jboss.seam.annotations.Destroy", file, start, end)};
		else if(messageId == SeamCoreValidator.CREATE_DOESNT_BELONG_TO_COMPONENT_MESSAGE_ID)
			return new IMarkerResolution[]{
				new AddAnnotationMarkerResolution(SeamUIMessages.ADD_NAME_ANNOTATION_MARKER_RESOLUTION_TITLE, "org.jboss.seam.annotations.Name", file, start, end, true),
				new DeleteAnnotationMarkerResolution(SeamUIMessages.DELETE_CREATE_ANNOTATION_MARKER_RESOLUTION_TITLE, "org.jboss.seam.annotations.Create", file, start, end)
			};
		else if(messageId == SeamCoreValidator.UNWRAP_DOESNT_BELONG_TO_COMPONENT_MESSAGE_ID)
			return new IMarkerResolution[]{
				new AddAnnotationMarkerResolution(SeamUIMessages.ADD_NAME_ANNOTATION_MARKER_RESOLUTION_TITLE, "org.jboss.seam.annotations.Name", file, start, end, true),
				new DeleteAnnotationMarkerResolution(SeamUIMessages.DELETE_UNWRAP_ANNOTATION_MARKER_RESOLUTION_TITLE, "org.jboss.seam.annotations.Unwrap", file, start, end)
			};
		else if(messageId == SeamCoreValidator.OBSERVER_DOESNT_BELONG_TO_COMPONENT_MESSAGE_ID)
			return new IMarkerResolution[]{
				new AddAnnotationMarkerResolution(SeamUIMessages.ADD_NAME_ANNOTATION_MARKER_RESOLUTION_TITLE, "org.jboss.seam.annotations.Name", file, start, end, true),
				new DeleteAnnotationMarkerResolution(SeamUIMessages.DELETE_OBSERVER_ANNOTATION_MARKER_RESOLUTION_TITLE, "org.jboss.seam.annotations.Observer", file, start, end)
			};
		else if(messageId == SeamCoreValidator.STATEFUL_COMPONENT_DOES_NOT_CONTAIN_REMOVE_ID)
			return new IMarkerResolution[]{new AddAnnotatedMethodMarkerResolution(SeamUIMessages.ADD_ANNOTATED_REMOVE_METHOD_MARKER_RESOLUTION_TITLE, "javax.ejb.Remove", file, start, end)};
		else if(messageId == SeamCoreValidator.STATEFUL_COMPONENT_DOES_NOT_CONTAIN_DESTROY_ID)
			return new IMarkerResolution[]{new AddAnnotatedMethodMarkerResolution(SeamUIMessages.ADD_ANNOTATED_DESTROY_METHOD_MARKER_RESOLUTION_TITLE, "org.jboss.seam.annotations.Destroy", file, start, end)};

				
		return new IMarkerResolution[]{};
	}
	

	public boolean hasResolutions(IMarker marker) {
		try{
			if(findResolutions(marker).length != 0)
				return true;
		}catch(CoreException ex){
			SeamGuiPlugin.getPluginLog().logError(ex);
		}
		return false;
	}

}
