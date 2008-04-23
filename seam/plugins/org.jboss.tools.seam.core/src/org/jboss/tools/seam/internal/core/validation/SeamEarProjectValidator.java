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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jst.j2ee.componentcore.J2EEModuleVirtualArchiveComponent;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.componentcore.resources.IVirtualReference;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidatorJob;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;

/**
 * This validator is workaround for bug of WTP 2.0.2
 * See http://jira.jboss.com/jira/browse/JBIDE-2117
 * @author Alexey Kazakov
 */
public class SeamEarProjectValidator implements IValidatorJob {
	private IValidationErrorManager errorManager;

	/* (non-Javadoc)
	 * @see org.eclipse.wst.validation.internal.provisional.core.IValidatorJob#getSchedulingRule(org.eclipse.wst.validation.internal.provisional.core.IValidationContext)
	 */
	public ISchedulingRule getSchedulingRule(IValidationContext helper) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.validation.internal.provisional.core.IValidatorJob#validateInJob(org.eclipse.wst.validation.internal.provisional.core.IValidationContext, org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	public IStatus validateInJob(IValidationContext helper, IReporter reporter)	throws ValidationException {
	
		SeamValidationHelper seamHelper = (SeamValidationHelper)helper;
		IProject project = seamHelper.getProject();
		if(!project.isAccessible()) {
			return OK_STATUS;
		}
		errorManager = new ValidationErrorManager(this, null, reporter, null, ISeamValidator.MARKED_SEAM_PROJECT_MESSAGE_GROUP);
		errorManager.removeAllMessagesFromResource(project);

		IVirtualComponent component = ComponentCore.createComponent(project);
		IVirtualReference[] rs = component.getReferences();
		for (int i = 0; i < rs.length; i++) {
			IVirtualComponent c = rs[i].getReferencedComponent();
			if(c == null) {
				continue;
			}
			IVirtualFolder folder = c.getRootFolder();
			if(folder==null) {
				continue;
			}
			ISeamProject seamProject = SeamCorePlugin.getSeamProject(folder.getProject(), false);
			if(seamProject!=null) {
				validateEar(project, rs);
				break;
			}
		}

		return OK_STATUS;
	}

	private void validateEar(IProject ear, IVirtualReference[] rs) {
		for (int i = 0; i < rs.length; i++) {
			IVirtualComponent c = rs[i].getReferencedComponent();
			if(c != null && c instanceof J2EEModuleVirtualArchiveComponent) {
				J2EEModuleVirtualArchiveComponent component = (J2EEModuleVirtualArchiveComponent)c;
				System.out.println(c.getName());
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.validation.internal.provisional.core.IValidator#cleanup(org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	public void cleanup(IReporter reporter) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.validation.internal.provisional.core.IValidator#validate(org.eclipse.wst.validation.internal.provisional.core.IValidationContext, org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	public void validate(IValidationContext helper, IReporter reporter) throws ValidationException {
		validateInJob(helper, reporter);
	}
}