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
package org.jboss.tools.cdi.internal.core.validation;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.cdi.core.CDICoreBuilder;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.common.validation.IELValidationDelegate;
import org.jboss.tools.common.validation.IValidatingProjectTree;
import org.jboss.tools.jst.web.kb.internal.validation.KBValidator;
import org.jboss.tools.jst.web.kb.preferences.ELSeverityPreferences;

/**
 * @author Alexey Kazakov
 */
public class CDIELValidationDelegate implements IELValidationDelegate {
	public static final String ID = "org.jboss.tools.cdi.core.CDIELValidationDelegate";

	/* (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.validation.IELValidationDelegate#getValidatingProjects(org.eclipse.core.resources.IProject)
	 */
	public IValidatingProjectTree getValidatingProjects(IProject project) {
		return CDICoreValidator.getProjectTree(project);
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.validation.IELValidationDelegate#shouldValidate(org.eclipse.core.resources.IProject)
	 */
	public boolean shouldValidate(IProject project) {
		try {
			return project != null 
					&& project.isAccessible() 
					&& project.hasNature(CDICoreNature.NATURE_ID)
					&& validateBuilderOrder(project);
		} catch (CoreException e) {
			CDICorePlugin.getDefault().logError(e);
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
		return CDICoreBuilder.BUILDER_ID;
	}
}