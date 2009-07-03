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
package org.jboss.tools.seam.internal.core.validation;

import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.jboss.tools.common.model.project.ext.ITextSourceReference;
import org.jboss.tools.seam.core.ISeamProject;

/**
 * @author Alexey Kazakov
 */
public interface IValidationErrorManager {

	/**
	 * Adds a marker to the resource
	 * @param messageId
	 * @param preferenceKey
	 * @param messageArguments
	 * @param location
	 * @param target
	 */
	IMarker addError(String messageId, String preferenceKey,
			String[] messageArguments, ITextSourceReference location,
			IResource target);

	/**
	 * Adds a marker to the resource
	 * @param messageId
	 * @param preferenceKey
	 * @param messageArguments
	 * @param target
	 */
	IMarker addError(String messageId, String preferenceKey,
			String[] messageArguments,
			IResource target);

	/**
	 * Adds a marker to the resource
	 * @param messageId
	 * @param preferenceKey
	 * @param location
	 * @param target
	 */
	IMarker addError(String messageId, String preferenceKey,
			ITextSourceReference location, IResource target);

	/**
	 * Adds a marker to the resource
	 * @param messageId
	 * @param preferenceKey
	 * @param messageArguments
	 * @param length
	 * @param offset
	 * @param target
	 */
	IMarker addError(String messageId, String preferenceKey,
			String[] messageArguments, int length, int offset, IResource target);

	/**
	 * Adds a marker to the resource
	 * @param messageId
	 * @param severity
	 * @param messageArguments
	 * @param length
	 * @param offset
	 * @param target
	 */
	IMarker addError(String messageId, int severity, String[] messageArguments, int length, int offset, IResource target);

	/**
	 * Displays a subtask in the progress view. 
	 * @param messageId
	 */
	void displaySubtask(String messageId);

	/**
	 * Displays a subtask in the progress view.
	 * @param messageId
	 * @param messageArguments
	 */
	void displaySubtask(String messageId, String[] messageArguments);

	/**
	 * Removes all markers for the resources
	 * @param resources
	 */
	void removeMessagesFromResources(Set<IResource> resources);

	/**
	 * Sets seam project
	 * @param project
	 */
	void setProject(ISeamProject project);

	/**
	 * Remove all validation messages for the resource.
	 * @param resource
	 */
	void removeAllMessagesFromResource(IResource resource);
}