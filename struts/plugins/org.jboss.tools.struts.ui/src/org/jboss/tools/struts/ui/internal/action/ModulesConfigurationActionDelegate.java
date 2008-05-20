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

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.model.ui.action.file.ModelResourceActionDelegate;
import org.jboss.tools.struts.StrutsProjectUtil;

public class ModulesConfigurationActionDelegate extends ModelResourceActionDelegate {
	
	protected String getActionPath() {
		return "SynchronizeModules";
	}

	protected boolean isSupportingImplementation(Class cls) {
		return (cls == IProject.class || cls == IJavaProject.class || cls == XModelObject.class);
	}
	
	protected boolean checkModelObject(Object object) {
		if(object instanceof XModelObject) {
			this.object = (XModelObject)object;
			if(EclipseResourceUtil.isProjectFragment(this.object.getModel())) {
				this.object = null;
			} else {
				this.object = this.object.getModel().getByPath("FileSystems");
			}
			return true;
		}
		return false;
	}
	
	protected boolean isRelevantProject(IProject project) {
		return StrutsProjectUtil.hasStrutsNature(project);
	}
	
}
