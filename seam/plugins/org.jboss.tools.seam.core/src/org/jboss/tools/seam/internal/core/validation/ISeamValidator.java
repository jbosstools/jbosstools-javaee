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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.validation.internal.core.ValidationException;

/**
 * Seam valodator that is managed by SeamValidatorManager.
 * @author Alexey Kazakov
 */
public interface ISeamValidator {

	public static final String MARKED_SEAM_RESOURCE_MESSAGE_GROUP = "markedSeamResource"; //$NON-NLS-1$
	public static final String MARKED_SEAM_PROJECT_MESSAGE_GROUP = "markedSeamProject"; //$NON-NLS-1$
	public static final String SEAM_RESOURCE_MESSAGE_ID = "org.jboss.tools.seam.core.seamProblem"; //$NON-NLS-1$
	public static final String SEAM_PROJECT_MESSAGE_ID = "org.jboss.tools.seam.core.seamProjectProblem"; //$NON-NLS-1$

	/**
	 * @return true if validator is enabled.
	 */
	public boolean isEnabled();

	/**
	 * Incremental Validation
	 * @return
	 * @throws ValidationException
	 */
	public IStatus validate(Set<IFile> changedFiles) throws ValidationException;

	/**
	 * Full Validation
	 * @return
	 * @throws ValidationException
	 */
	public IStatus validateAll() throws ValidationException;
}