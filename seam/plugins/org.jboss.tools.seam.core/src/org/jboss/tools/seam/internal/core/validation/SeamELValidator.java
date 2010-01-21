/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.seam.internal.core.validation;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.jboss.tools.common.el.core.parser.ELParserUtil;
import org.jboss.tools.jsf.web.validation.ELValidator;
import org.jboss.tools.jst.web.kb.internal.validation.ContextValidationHelper;
import org.jboss.tools.jst.web.kb.validation.IValidatingProjectSet;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;

/**
 * @author Alexey Kazakov
 */
public class SeamELValidator extends ELValidator {

	public static final String ID = "org.jboss.tools.seam.core.ELValidator";

	/* (non-Javadoc)
	 * @see org.jboss.tools.jsf.web.validation.ELValidator#getId()
	 */
	@Override
	public String getId() {
		return ID;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.jsf.web.validation.ELValidator#getValidatingProjects(org.eclipse.core.resources.IProject)
	 */
	@Override
	public IValidatingProjectSet getValidatingProjects(IProject project) {
		try {
			if(!project.hasNature(ISeamProject.NATURE_ID)) {
				return super.getValidatingProjects(project);
			}
		} catch (CoreException e) {
			SeamCorePlugin.getDefault().logError(e);
		}

		return SeamCoreValidator.getSeamValidatingProjects(project);
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.jsf.web.validation.ELValidator#shouldValidate(org.eclipse.core.resources.IProject)
	 */
	@Override
	public boolean shouldValidate(IProject project) {
		try {
			return super.shouldValidate(project) || (project!=null && project.isAccessible() && project.hasNature(ISeamProject.NATURE_ID));
		} catch (CoreException e) {
			SeamCorePlugin.getDefault().logError(e);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.jsf.web.validation.ELValidator#init(org.eclipse.core.resources.IProject, org.jboss.tools.jst.web.kb.internal.validation.ContextValidationHelper, org.eclipse.wst.validation.internal.provisional.core.IValidator, org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	@Override
	public void init(IProject project,
			ContextValidationHelper validationHelper, IValidator manager,
			IReporter reporter) {
		super.init(project, validationHelper, manager, reporter);
		mainFactory = ELParserUtil.getJbossFactory();
	}
}