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
package org.jboss.tools.jsf.text.ext.hyperlink;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.project.IModelNature;
import org.jboss.tools.common.model.util.EclipseResourceUtil;

import org.jboss.tools.common.text.ext.hyperlink.LinkHyperlink;
import org.jboss.tools.jst.web.project.list.WebPromptingProvider;

/**
 * @author Jeremy
 */
public class JSFLinkHyperlink extends LinkHyperlink {
	
	protected String updateFilenameForModel(String filename, IProject project) {
		// Begin of Slava's magic
		WebPromptingProvider provider = WebPromptingProvider.getInstance();
		IModelNature n = EclipseResourceUtil.getModelNature(project);
		XModel xModel = n == null ? null : n.getModel();
		
		if (xModel != null) {
			List list = provider.getList(xModel, WebPromptingProvider.JSF_GET_PATH, filename, null);
			if (list != null && list.size() > 0) {
				for (Iterator i = list.iterator(); i.hasNext();) {
					Object o = i.next();
					if (o instanceof String) {
						return (String)o;
					}
				}
			}
		}
		return filename;
	}
	
	protected IFile getFileFromProject(String fileName) {
		IFile fileFromProject = null;
		IFile documentFile = getFile();
		if(documentFile == null) {
			return null;
		}
		IProject project = documentFile.getProject();
		/*
		 * Fixes https://jira.jboss.org/jira/browse/JBIDE-5577
		 * Get existed file from the project.
		 * There could be several files, the first one will be returned.
		 */
		WebPromptingProvider provider = WebPromptingProvider.getInstance();
		IModelNature n = EclipseResourceUtil.getModelNature(project);
		XModel xModel = n == null ? null : n.getModel();
		if (xModel != null) {
			List<Object> list = provider.getList(xModel, WebPromptingProvider.JSF_GET_PATH, fileName, null);
			if ((list != null) && (list.size() > 0)) {
				for (Object realFileName : list) {
					if (realFileName instanceof String) {
						fileFromProject = super.getFileFromProject((String)realFileName);
						if (fileFromProject != null) {
							break;
						}
					}	
				}
			}
		}
		
		return fileFromProject;
	}
	
}