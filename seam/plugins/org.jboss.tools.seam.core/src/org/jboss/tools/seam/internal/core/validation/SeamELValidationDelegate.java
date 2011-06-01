/******************************************************************************* 
 * Copyright (c) 2010-2011 Red Hat, Inc. 
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
import org.jboss.tools.jst.web.kb.internal.validation.ELValidator;
import org.jboss.tools.jst.web.kb.internal.validation.ValidatorManager;
import org.jboss.tools.jst.web.kb.validation.IELValidationDelegate;
import org.jboss.tools.jst.web.kb.validation.IValidatingProjectTree;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCoreBuilder;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.SeamPreferences;

/**
 * @author Alexey Kazakov
 */
public class SeamELValidationDelegate implements IELValidationDelegate {

	/* (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.validation.IELValidationDelegate#getValidatingProjects(org.eclipse.core.resources.IProject)
	 */
	public IValidatingProjectTree getValidatingProjects(IProject project) {
		return SeamCoreValidator.getSeamValidatingProjects(project);
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.validation.IELValidationDelegate#shouldValidate(org.eclipse.core.resources.IProject)
	 */
	public boolean shouldValidate(IProject project) {
		try {
			return project != null 
					&& project.isAccessible() 
					&& project.hasNature(ISeamProject.NATURE_ID)
					&& validateBuilderOrder(project);
		} catch (CoreException e) {
			SeamCorePlugin.getDefault().logError(e);
		}
		return false;
	}

	private boolean validateBuilderOrder(IProject project) throws CoreException {
		return ValidatorManager.validateBuilderOrder(project, getBuilderId(), ELValidator.ID + "-Seam", SeamPreferences.getInstance());
	}

	public String getBuilderId() {
		return SeamCoreBuilder.BUILDER_ID;
	}

}