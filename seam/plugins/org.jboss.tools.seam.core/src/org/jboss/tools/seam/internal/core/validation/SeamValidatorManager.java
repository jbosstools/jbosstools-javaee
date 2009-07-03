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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidatorJob;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.SeamProjectsSet;

/**
 * This Manager invokes all dependent seam validators that should be invoked in one job.
 * We need this one because wst validation framework does not let us invoke
 * dependent validators in the same job.
 * @author Alexey Kazakov
 */
public class SeamValidatorManager implements IValidatorJob {

	private static Set<ISeamProject> validatingProjects = new HashSet<ISeamProject>(); 

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
		SeamContextValidationHelper coreHelper = (SeamContextValidationHelper)helper;
		IProject project = coreHelper.getProject();
		if(project==null) {
			return OK_STATUS;
		}
		SeamProjectsSet set = new SeamProjectsSet(project);
		IProject warProject = set.getWarProject();
		ISeamProject seamWarProject = SeamCorePlugin.getSeamProject(warProject, false);
		IStatus status = OK_STATUS;
		synchronized (validatingProjects) {
			if(validatingProjects.contains(seamWarProject)) {
				return OK_STATUS;
			}
			validatingProjects.add(seamWarProject);
		}
		synchronized (validatingProjects) {
			ISeamValidationContext validationContext = null;
			try {
				coreHelper.setSeamProject(seamWarProject);
				validationContext = new SeamValidationContext(project);
				coreHelper.setValidationContext(validationContext);
				ISeamValidator coreValidator = new SeamCoreValidator(this, coreHelper, reporter, validationContext, seamWarProject);
				ISeamValidator elValidator = new SeamELValidator(this, coreHelper, reporter, validationContext, seamWarProject);
				ISeamValidator[] validators = new ISeamValidator[]{coreValidator, elValidator};

				Set<IFile> changedFiles = coreHelper.getChangedFiles();
				if(!changedFiles.isEmpty()) {
					status = validate(validators, changedFiles);
				} else if(!validationContext.getRegisteredFiles().isEmpty()) {
					validationContext.clearAllResourceLinks();
					status = validateAll(validators);
				}
			} finally {
				if(validationContext!=null) {
					validationContext.clearRegisteredFiles();
				}
				validatingProjects.remove(seamWarProject);
			}
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