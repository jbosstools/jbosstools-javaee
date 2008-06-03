/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.struts.ui.internal.action;

import org.eclipse.core.resources.*;
import org.eclipse.jdt.core.IJavaProject;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.model.ui.action.file.ModelResourceActionDelegate;
///import org.jboss.tools.struts.StrutsProjectUtil;

public class VerifyActionDelegate extends ModelResourceActionDelegate {
	
	protected String getActionPath() {
		return "VerifyActions.StaticActions.VerifyAll";
	}

	protected boolean isSupportingImplementation(Class cls) {
		return (cls == IFile.class || cls == IFolder.class || cls == IProject.class || cls == IJavaProject.class);
	}
	
	protected boolean isRelevantProject(IProject project) {
		return EclipseResourceUtil.getModelNature(project) != null;
	}

}
