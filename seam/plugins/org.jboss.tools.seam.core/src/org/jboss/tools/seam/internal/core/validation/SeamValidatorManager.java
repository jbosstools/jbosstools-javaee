 /*******************************************************************************
  * Copyright (c) 2007 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.seam.internal.core.validation;

import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidatorJob;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.internal.core.SeamProject;

/**
 * This Manager invokes all dependent seam validators that should be invoked in one job.
 * We need this one because wst validation framework does not let us invoke
 * dependent validators in the same job.
 * @author Alexey Kazakov
 */
public class SeamValidatorManager implements IValidatorJob {

	public SeamValidatorManager() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.validation.internal.provisional.core.IValidatorJob#getSchedulingRule(org.eclipse.wst.validation.internal.provisional.core.IValidationContext)
	 */
	public ISchedulingRule getSchedulingRule(IValidationContext helper) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.validation.internal.provisional.core.IValidatorJob#validateInJob(org.eclipse.wst.validation.internal.provisional.core.IValidationContext, org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	public IStatus validateInJob(IValidationContext helper, IReporter reporter)	throws ValidationException {
		SeamValidationHelper coreHelper = (SeamValidationHelper)helper;
		ISeamProject project = coreHelper.getSeamProject();
		SeamValidationContext validationContext = ((SeamProject)project).getValidationContext();
		IStatus status = null;
		try {
			ISeamValidator coreValidator = new SeamCoreValidator(this, coreHelper, reporter, validationContext, project);
			ISeamValidator elValidator = new SeamELValidator(this, coreHelper, reporter, validationContext, project);			
			ISeamValidator[] validators = new ISeamValidator[]{coreValidator, elValidator};

			Set<IFile> changedFiles = coreHelper.getChangedFiles();
			if(changedFiles.size()>0) {
				status = validate(validators, changedFiles);
			} else {
				reporter.removeAllMessages(this);
				validationContext.clearAllResourceLinks();
				status = validateAll(validators);
			}
		} finally {
			validationContext.clearRegisteredFiles();
		}
		return status;
	}

	private IStatus validate(ISeamValidator[] validator, Set<IFile> changedFiles) throws ValidationException {
		for (int i = 0; i < validator.length; i++) {
			if(validator[i].isEnabled()) {
				validator[i].validate(changedFiles);
			}
		}
		return OK_STATUS;
	}

	private IStatus validateAll(ISeamValidator[] validator) throws ValidationException {
		for (int i = 0; i < validator.length; i++) {
			if(validator[i].isEnabled()) {
				validator[i].validateAll();
			}
		}
		return OK_STATUS;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.validation.internal.provisional.core.IValidator#cleanup(org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	public void cleanup(IReporter reporter) {
		reporter = null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.validation.internal.provisional.core.IValidator#validate(org.eclipse.wst.validation.internal.provisional.core.IValidationContext, org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	public void validate(IValidationContext helper, IReporter reporter)	throws ValidationException {
		validateInJob(helper, reporter);
	}
}