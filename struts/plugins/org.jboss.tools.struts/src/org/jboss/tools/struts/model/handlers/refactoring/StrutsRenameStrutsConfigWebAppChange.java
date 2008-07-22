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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.impl.FolderImpl;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.jst.web.project.WebModuleConstants;
import org.jboss.tools.jst.web.project.WebProject;
import org.jboss.tools.struts.model.handlers.RenameStrutsConfigHandler;

public class StrutsRenameStrutsConfigWebAppChange extends Change {
	XModelObject folder;
	XModelObject object;
	String oldName;
	String newName;
	StrutsRenameStrutsConfigParticipant participant;
	
	public StrutsRenameStrutsConfigWebAppChange(StrutsRenameStrutsConfigParticipant participant, XModelObject object, String oldName, String newName) {
		this.participant = participant;
		this.object = object;
		folder = object.getParent();
		this.newName = newName;
		this.oldName = oldName;
	}

	public String getName() {
		return "web.xml";
	}

	public void initializeValidationData(IProgressMonitor pm) {}

	public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		return null;
	}

	public Change perform(IProgressMonitor pm) throws CoreException {
//		String path = WebProject.getInstance(folder.getModel()).getPathInWebRoot(folder);
//		if(path == null) path = "" + XModelObjectLoaderUtil.getResourcePath(folder);
//		if(!path.startsWith("/")) path += "/";
//		path += newName;
		if(folder instanceof FolderImpl) ((FolderImpl)folder).update();
		folder.getModel().update();

		String resourcePath = XModelObjectLoaderUtil.getResourcePath(object);
		String newURI = WebProject.getInstance(object.getModel()).getPathInWebRoot(object);
		if(newURI == null) newURI = "/WEB-INF" + resourcePath;
		boolean meq = (participant.m != null && participant.m.getAttributeValue("URI").equals(participant.oldURI));
		boolean ceq = (participant.cg != null && participant.cg.getAttributeValue("URI").equals(participant.oldURI));
		boolean replaceInWebXML = meq || ceq;
		if(participant.m != null) participant.m.getModel().changeObjectAttribute(participant.m, WebModuleConstants.ATTR_MODEL_PATH, resourcePath);
		if(participant.cg != null) participant.cg.getModel().changeObjectAttribute(participant.cg, WebModuleConstants.ATTR_MODEL_PATH, resourcePath);
		XActionInvoker.invoke("SaveActions.Save", object, null); //prop?
		object.getModel().update();
		if(replaceInWebXML) {
			if(meq) participant.m.getModel().changeObjectAttribute(participant.m, WebModuleConstants.ATTR_URI, newURI);
			if(ceq) participant.cg.getModel().changeObjectAttribute(participant.cg, WebModuleConstants.ATTR_URI, newURI);
			RenameStrutsConfigHandler.renameConfigInWebXML(object.getModel(), participant.module, participant.oldURI, newURI);
		}

		return null;
	}

	public Object getModifiedElement() {
		return null;
	}

}
