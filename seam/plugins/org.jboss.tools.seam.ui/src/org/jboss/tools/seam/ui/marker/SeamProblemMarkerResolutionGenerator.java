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

import java.util.Collection;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator2;
import org.jboss.tools.common.text.ITextSourceReference;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamJavaComponentDeclaration;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.ISeamProperty;
import org.jboss.tools.seam.core.ISeamXmlComponentDeclaration;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.SeamCoreMessages;
import org.jboss.tools.seam.internal.core.SeamComponentDeclaration;
import org.jboss.tools.seam.internal.core.validation.SeamCoreValidator;
import org.jboss.tools.seam.ui.SeamGuiPlugin;

/**
 * @author Daniel Azarov
 */
public class SeamProblemMarkerResolutionGenerator implements
		IMarkerResolutionGenerator2 {
	private static final String JAVA_EXTENSION = "java"; //$NON-NLS-1$
	private static final String XML_EXTENSION = "xml"; //$NON-NLS-1$

	public IMarkerResolution[] getResolutions(IMarker marker) {
		try {
			return findResolutions(marker);
		} catch (CoreException ex) {
			SeamGuiPlugin.getPluginLog().logError(ex);
		}
		return new IMarkerResolution[] {};
	}
	
	/**
	 * return message id or -1 if impossible to find
	 * @param marker
	 * @return
	 */
	private int getMessageID(IMarker marker)throws CoreException{
		Integer attribute = ((Integer) marker.getAttribute(SeamCoreValidator.MESSAGE_ID_ATTRIBUTE_NAME));
		if (attribute != null)
			return attribute.intValue();

		return -1; 
	}

	private IMarkerResolution[] findResolutions(IMarker marker)
			throws CoreException {
		
		int messageId = getMessageID(marker);
		if (messageId == -1)
			return new IMarkerResolution[] {};

		IFile file = (IFile) marker.getResource();

		Integer attribute = ((Integer) marker.getAttribute(IMarker.CHAR_START));
		if (attribute == null)
			return new IMarkerResolution[] {};
		int start = attribute.intValue();

		attribute = ((Integer) marker.getAttribute(IMarker.CHAR_END));
		if (attribute == null)
			return new IMarkerResolution[] {};
		int end = attribute.intValue();

		if (JAVA_EXTENSION.equals(file.getFileExtension())) {
			if (messageId == SeamCoreValidator.NONUNIQUE_COMPONENT_NAME_MESSAGE_ID) {
				return new IMarkerResolution[] {
						new RenameAnnotationMarkerResolution(
								SeamCoreMessages.RENAME_NAME_ANNOTATION_MARKER_RESOLUTION_TITLE,
								"org.jboss.seam.annotations.Name", file, start,
								end),
						new DeleteAnnotationMarkerResolution(
								SeamCoreMessages.DELETE_NAME_ANNOTATION_MARKER_RESOLUTION_TITLE,
								"org.jboss.seam.annotations.Name", file, start,
								end) };
			} else if (messageId == SeamCoreValidator.DUPLICATE_REMOVE_MESSAGE_ID)
				return new IMarkerResolution[] { new DeleteAnnotationMarkerResolution(
						SeamCoreMessages.DELETE_REMOVE_ANNOTATION_MARKER_RESOLUTION_TITLE,
						"javax.ejb.Remove", file, start, end) };
			else if (messageId == SeamCoreValidator.DUPLICATE_DESTROY_MESSAGE_ID)
				return new IMarkerResolution[] { new DeleteAnnotationMarkerResolution(
						SeamCoreMessages.DELETE_DESTROY_ANNOTATION_MARKER_RESOLUTION_TITLE,
						"org.jboss.seam.annotations.Destroy", file, start, end) };
			else if (messageId == SeamCoreValidator.DUPLICATE_CREATE_MESSAGE_ID)
				return new IMarkerResolution[] { new DeleteAnnotationMarkerResolution(
						SeamCoreMessages.DELETE_CREATE_ANNOTATION_MARKER_RESOLUTION_TITLE,
						"org.jboss.seam.annotations.Create", file, start, end) };
			else if (messageId == SeamCoreValidator.DUPLICATE_UNWRAP_MESSAGE_ID)
				return new IMarkerResolution[] { new DeleteAnnotationMarkerResolution(
						SeamCoreMessages.DELETE_UNWRAP_ANNOTATION_MARKER_RESOLUTION_TITLE,
						"org.jboss.seam.annotations.Unwrap", file, start, end) };
			else if (messageId == SeamCoreValidator.DESTROY_METHOD_BELONGS_TO_STATELESS_SESSION_BEAN_MESSAGE_ID)
				return new IMarkerResolution[] { new DeleteAnnotationMarkerResolution(
						SeamCoreMessages.DELETE_DESTROY_ANNOTATION_MARKER_RESOLUTION_TITLE,
						"org.jboss.seam.annotations.Destroy", file, start, end) };
			else if (messageId == SeamCoreValidator.CREATE_DOESNT_BELONG_TO_COMPONENT_MESSAGE_ID)
				return new IMarkerResolution[] {
						new AddAnnotationMarkerResolution(
								SeamCoreMessages.ADD_NAME_ANNOTATION_MARKER_RESOLUTION_TITLE,
								"org.jboss.seam.annotations.Name", file, start,
								end, true),
						new DeleteAnnotationMarkerResolution(
								SeamCoreMessages.DELETE_CREATE_ANNOTATION_MARKER_RESOLUTION_TITLE,
								"org.jboss.seam.annotations.Create", file,
								start, end) };
			else if (messageId == SeamCoreValidator.UNWRAP_DOESNT_BELONG_TO_COMPONENT_MESSAGE_ID)
				return new IMarkerResolution[] {
						new AddAnnotationMarkerResolution(
								SeamCoreMessages.ADD_NAME_ANNOTATION_MARKER_RESOLUTION_TITLE,
								"org.jboss.seam.annotations.Name", file, start,
								end, true),
						new DeleteAnnotationMarkerResolution(
								SeamCoreMessages.DELETE_UNWRAP_ANNOTATION_MARKER_RESOLUTION_TITLE,
								"org.jboss.seam.annotations.Unwrap", file,
								start, end) };
			else if (messageId == SeamCoreValidator.OBSERVER_DOESNT_BELONG_TO_COMPONENT_MESSAGE_ID)
				return new IMarkerResolution[] {
						new AddAnnotationMarkerResolution(
								SeamCoreMessages.ADD_NAME_ANNOTATION_MARKER_RESOLUTION_TITLE,
								"org.jboss.seam.annotations.Name", file, start,
								end, true),
						new DeleteAnnotationMarkerResolution(
								SeamCoreMessages.DELETE_OBSERVER_ANNOTATION_MARKER_RESOLUTION_TITLE,
								"org.jboss.seam.annotations.Observer", file,
								start, end) };
			else if (messageId == SeamCoreValidator.STATEFUL_COMPONENT_DOES_NOT_CONTAIN_REMOVE_ID)
				return new IMarkerResolution[] { new AddAnnotatedMethodMarkerResolution(
						SeamCoreMessages.ADD_ANNOTATED_REMOVE_METHOD_MARKER_RESOLUTION_TITLE,
						"javax.ejb.Remove", file, start, end) };
			else if (messageId == SeamCoreValidator.STATEFUL_COMPONENT_DOES_NOT_CONTAIN_DESTROY_ID)
				return new IMarkerResolution[] { new AddAnnotatedMethodMarkerResolution(
						SeamCoreMessages.ADD_ANNOTATED_DESTROY_METHOD_MARKER_RESOLUTION_TITLE,
						"org.jboss.seam.annotations.Destroy", file, start, end) };
			else if (messageId == SeamCoreValidator.STATEFUL_COMPONENT_WRONG_SCOPE_ID)
				return new IMarkerResolution[] {
						new ChangeScopeMarkerResolution(
								SeamCoreMessages.CHANGE_SCOPETYPE_MARKER_RESOLUTION_TITLE,
								"ScopeType.APPLICATION", file, start, end),
						new ChangeScopeMarkerResolution(
								SeamCoreMessages.CHANGE_SCOPETYPE_MARKER_RESOLUTION_TITLE,
								"ScopeType.BUSINESS_PROCESS", file, start, end),
						new ChangeScopeMarkerResolution(
								SeamCoreMessages.CHANGE_SCOPETYPE_MARKER_RESOLUTION_TITLE,
								"ScopeType.CONVERSATION", file, start, end),
						new ChangeScopeMarkerResolution(
								SeamCoreMessages.CHANGE_SCOPETYPE_MARKER_RESOLUTION_TITLE,
								"ScopeType.EVENT", file, start, end),
						new ChangeScopeMarkerResolution(
								SeamCoreMessages.CHANGE_SCOPETYPE_MARKER_RESOLUTION_TITLE,
								"ScopeType.METHOD", file, start, end),
						new ChangeScopeMarkerResolution(
								SeamCoreMessages.CHANGE_SCOPETYPE_MARKER_RESOLUTION_TITLE,
								"ScopeType.SESSION", file, start, end),
						new ChangeScopeMarkerResolution(
								SeamCoreMessages.CHANGE_SCOPETYPE_MARKER_RESOLUTION_TITLE,
								"ScopeType.UNSPECIFIED", file, start, end) };
			else if (messageId == SeamCoreValidator.ENTITY_COMPONENT_WRONG_SCOPE_ID)
				return new IMarkerResolution[] {
						new ChangeScopeMarkerResolution(
								SeamCoreMessages.CHANGE_SCOPETYPE_MARKER_RESOLUTION_TITLE,
								"ScopeType.APPLICATION", file, start, end),
						new ChangeScopeMarkerResolution(
								SeamCoreMessages.CHANGE_SCOPETYPE_MARKER_RESOLUTION_TITLE,
								"ScopeType.BUSINESS_PROCESS", file, start, end),
						new ChangeScopeMarkerResolution(
								SeamCoreMessages.CHANGE_SCOPETYPE_MARKER_RESOLUTION_TITLE,
								"ScopeType.CONVERSATION", file, start, end),
						new ChangeScopeMarkerResolution(
								SeamCoreMessages.CHANGE_SCOPETYPE_MARKER_RESOLUTION_TITLE,
								"ScopeType.EVENT", file, start, end),
						new ChangeScopeMarkerResolution(
								SeamCoreMessages.CHANGE_SCOPETYPE_MARKER_RESOLUTION_TITLE,
								"ScopeType.METHOD", file, start, end),
						new ChangeScopeMarkerResolution(
								SeamCoreMessages.CHANGE_SCOPETYPE_MARKER_RESOLUTION_TITLE,
								"ScopeType.PAGE", file, start, end),
						new ChangeScopeMarkerResolution(
								SeamCoreMessages.CHANGE_SCOPETYPE_MARKER_RESOLUTION_TITLE,
								"ScopeType.SESSION", file, start, end),
						new ChangeScopeMarkerResolution(
								SeamCoreMessages.CHANGE_SCOPETYPE_MARKER_RESOLUTION_TITLE,
								"ScopeType.UNSPECIFIED", file, start, end) };
		}else if(XML_EXTENSION.equals(file.getFileExtension())){
			if (messageId == SeamCoreValidator.UNKNOWN_COMPONENT_PROPERTY_ID){
				ISeamProperty property = findSeamProperty(file, start, end);
				if(property != null){
					if(property.getParent() != null && property.getParent() instanceof SeamComponentDeclaration){
						SeamComponentDeclaration xmlDeclaration = (SeamComponentDeclaration)property.getParent();
						if(xmlDeclaration == null){
							return new IMarkerResolution[] {};
						}
						for(ISeamComponent component : xmlDeclaration.getComponents()){
							ISeamJavaComponentDeclaration javaDeclaration = component.getJavaDeclaration();
							if(javaDeclaration != null)
								return new IMarkerResolution[] { new AddSetterMarkerResolution(property, javaDeclaration) };
						}
					}
				}
			}
		}

		return new IMarkerResolution[] {};
	}
	
	private ISeamProperty findSeamProperty(IFile file, int start, int end){
		if(file == null)
			return null;
		IProject project = file.getProject();
		if(project == null)
			return null;
		
		ISeamProject seamProject = SeamCorePlugin.getSeamProject(project, true);
		if(seamProject == null)
			return null;
		
		Set<ISeamComponent> components = seamProject.getComponentsByPath(file.getFullPath());
		for(ISeamComponent component : components){
			Set<ISeamXmlComponentDeclaration> declarations = component.getXmlDeclarations();
			for(ISeamXmlComponentDeclaration declaration : declarations){
				Collection<ISeamProperty> properties = declaration.getProperties();
				for(ISeamProperty property : properties){
					ITextSourceReference location = property.getLocationFor(ISeamXmlComponentDeclaration.NAME);

					if(location.getStartPosition() <= start && (location.getStartPosition()+location.getLength()) >= end)
						return property;
				}
			}
		}
		
		return null;
	}

	public boolean hasResolutions(IMarker marker) {
		try {
			return getMessageID(marker) >= 0;
		} catch (CoreException ex) {
			SeamGuiPlugin.getPluginLog().logError(ex);
		}
		return false;
	}
	
}
