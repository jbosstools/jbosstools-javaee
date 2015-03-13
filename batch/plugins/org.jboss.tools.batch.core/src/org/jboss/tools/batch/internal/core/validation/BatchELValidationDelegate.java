/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.batch.internal.core.validation;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.batch.core.BatchCorePlugin;
import org.jboss.tools.common.validation.IELValidationDelegate;
import org.jboss.tools.common.validation.IValidatingProjectTree;
import org.jboss.tools.jst.web.kb.IKbProject;
import org.jboss.tools.jst.web.kb.internal.KbBuilder;
import org.jboss.tools.jst.web.kb.internal.validation.KBValidator;
import org.jboss.tools.jst.web.kb.preferences.ELSeverityPreferences;

/**
 * @author Viacheslav Kabanovich
 */
public class BatchELValidationDelegate implements IELValidationDelegate {
	public static final String ID = "org.jboss.tools.batch.core.BatchELValidationDelegate";

	/* (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.validation.IELValidationDelegate#getValidatingProjects(org.eclipse.core.resources.IProject)
	 */
	public IValidatingProjectTree getValidatingProjects(IProject project) {
		return KBValidator.createSimpleValidatingProjectTree(project);
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.validation.IELValidationDelegate#shouldValidate(org.eclipse.core.resources.IProject)
	 */
	public boolean shouldValidate(IProject project) {
		try {
			return project != null 
					&& project.isAccessible() 
					&& project.hasNature(IKbProject.NATURE_ID)
					&& validateBuilderOrder(project);
		} catch (CoreException e) {
			BatchCorePlugin.pluginLog().logError(e);
		}
		return false;
	}

	private boolean validateBuilderOrder(IProject project) throws CoreException {
		//It's EL, hence EL preferences, not CDI preferences.
		return KBValidator.validateBuilderOrder(project, getBuilderId(), getID(), ELSeverityPreferences.getInstance()); //$NON-NLS-1$
	}

	public String getID() {
		return ID;
	}

	public String getBuilderId() {
		return KbBuilder.BUILDER_ID;
	}
}