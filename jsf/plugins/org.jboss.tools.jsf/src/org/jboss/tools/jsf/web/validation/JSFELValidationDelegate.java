/******************************************************************************* 
 * Copyright (c) 2010 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.jsf.web.validation;

import org.eclipse.core.resources.IProject;
import org.jboss.tools.jst.web.kb.internal.KbProject;
import org.jboss.tools.jst.web.kb.internal.validation.KBValidator;
import org.jboss.tools.jst.web.kb.validation.IELValidationDelegate;
import org.jboss.tools.jst.web.kb.validation.IValidatingProjectTree;

/**
 * @author Alexey Kazakov
 */
public class JSFELValidationDelegate implements IELValidationDelegate {
	public static final String ID = "org.jboss.tools.jsf.JSFELValidationDelegate";

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
		return project!=null && project.isAccessible() && KbProject.checkKBBuilderInstalled(project);
	}

	public String getID() {
		return ID;
	}
}