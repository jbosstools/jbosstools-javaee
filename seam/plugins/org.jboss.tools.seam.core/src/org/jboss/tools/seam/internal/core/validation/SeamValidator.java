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

import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.jboss.tools.seam.core.ISeamProject;

/**
 * Abstract implementation of ISeamValidator
 * 
 * @author Alexey Kazakov
 */
public abstract class SeamValidator extends ValidationErrorManager implements ISeamValidator {

	protected ISeamValidationContext validationContext;
	protected String projectName;

	/**
	 * Constructor
	 * @param validatorManager
	 * @param coreHelper
	 * @param reporter
	 * @param validationContext
	 * @param project must not be null
	 */
	public SeamValidator(IValidator validatorManager,
			SeamContextValidationHelper coreHelper, IReporter reporter,
			ISeamValidationContext validationContext, ISeamProject project) {
		super(validatorManager, coreHelper, reporter, project, coreHelper.getProject(), ISeamValidator.MARKED_SEAM_RESOURCE_MESSAGE_GROUP);
		this.validationContext = validationContext;
		this.projectName = project.getProject().getName();
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.seam.internal.core.validation.ValidationErrorManager#setProject(org.jboss.tools.seam.core.ISeamProject)
	 */
	@Override
	public void setProject(ISeamProject project) {
		super.setProject(project);
		this.projectName = project.getProject().getName();
	}
}