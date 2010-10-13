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
package org.jboss.tools.jsf.model.handlers.bean;

import org.eclipse.core.resources.*;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.project.IModelNature;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.jsf.model.handlers.refactoring.JSFPagesRefactoringChange;
import org.jboss.tools.jsf.project.JSFNature;

public class JSFRenameFieldChange extends JSFPagesRefactoringChange {
	public JSFRenameFieldChange(IField field, String newName) {
		super(getModel(field), newName, JSFRenameFieldHelper.getReplacements(getModel(field), field, newName));
	}

	public JSFRenameFieldChange(XModelObject beanProperty, String newName) {
		super(beanProperty.getModel(), newName, JSFRenameFieldHelper.getReplacements(beanProperty, newName));
	}
	
	static XModel getModel(IMember field) {
		if(field == null || field.getJavaProject() == null) return null;
		IProject project = field.getJavaProject().getProject();
		if(!JSFNature.hasJSFNature(project)) return null;
		IModelNature n = EclipseResourceUtil.getModelNature(project);
		return (n != null) ? n.getModel() : null;
	}	

}
