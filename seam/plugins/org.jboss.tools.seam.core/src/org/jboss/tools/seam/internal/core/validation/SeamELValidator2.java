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
import org.jboss.tools.jsf.web.validation.ELValidator;
import org.jboss.tools.jst.web.kb.validation.IValidatingProjectSet;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;

/**
 * @author Alexey Kazakov
 */
public class SeamELValidator2 extends ELValidator {

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
			if(project.hasNature(ISeamProject.NATURE_ID)) {
				return super.getValidatingProjects(project);
			}
		} catch (CoreException e) {
			SeamCorePlugin.getDefault().logError(e);
		}

		return SeamCoreValidator2.getSeamValidatingProjects(project);
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.jsf.web.validation.ELValidator#shouldValidate(org.eclipse.core.resources.IProject)
	 */
	@Override
	public boolean shouldValidate(IProject project) {
		try {
			return super.shouldValidate(project) || project.hasNature(ISeamProject.NATURE_ID);
		} catch (CoreException e) {
			SeamCorePlugin.getDefault().logError(e);
		}
		return false;
	}
}