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
package org.jboss.tools.struts.model.handlers.refactoring;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.*;
import org.eclipse.ltk.core.refactoring.participants.*;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.impl.FileAnyImpl;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.jst.web.project.WebModuleConstants;
import org.jboss.tools.jst.web.project.WebProject;
import org.jboss.tools.struts.webprj.model.helpers.WebModulesHelper;

public class StrutsRenameStrutsConfigParticipant extends RenameParticipant {
	public static final String PARTICIPANT_NAME="jsf-RenameStrutsConfigParticipant";
	XModelObject object;
	WebModulesHelper wh;
	String module;
	XModelObject m;
	String path;
	XModelObject cg;
	String oldURI;
	

	protected boolean initialize(Object element) {
		if(!(element instanceof IFile)) return false;
		IFile f = (IFile)element;
		object = EclipseResourceUtil.getObjectByResource(f);
		if(object == null) return false;
		String entity = object.getModelEntity().getName();
		if(!entity.startsWith("StrutsConfig")) return false;
		
		wh = WebModulesHelper.getInstance(object.getModel());
		module = "" + wh.getModuleForConfig(object);
		m = object.getModel().getByPath("Web/" + module.replace('/', '#'));
		path = XModelObjectLoaderUtil.getResourcePath(object);
		cg = null;
		XModelObject[] cgs = m.getChildren();
		for (int i = 0; i < cgs.length; i++) {
			if(path.equals(cgs[i].getAttributeValue(WebModuleConstants.ATTR_MODEL_PATH))) cg = cgs[i]; 
		}
		if(m != null && !path.equals(m.getAttributeValue(WebModuleConstants.ATTR_MODEL_PATH))) m = null;
		oldURI = WebProject.getInstance(object.getModel()).getPathInWebRoot(object);
		if(oldURI == null) oldURI = "/WEB-INF" + XModelObjectLoaderUtil.getResourcePath(object);

		return true;
	}

	public String getName() {
		return PARTICIPANT_NAME;
	}

	public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context) throws OperationCanceledException {
		return null;
	}

	public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		if (!pm.isCanceled()) {
			String newName = getArguments().getNewName();
			if(newName == null || newName.trim().length() == 0) return null;
			String oldName = FileAnyImpl.toFileName(object);
			StrutsRenameStrutsConfigWebAppChange change = new StrutsRenameStrutsConfigWebAppChange(this, object, oldName, newName);
			return change;
		}
		return null;
	}

}
