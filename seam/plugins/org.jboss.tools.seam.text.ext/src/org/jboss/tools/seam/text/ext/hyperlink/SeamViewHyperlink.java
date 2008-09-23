/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.seam.text.ext.hyperlink;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.project.IModelNature;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.text.ext.hyperlink.LinkHyperlink;
import org.jboss.tools.jst.web.project.list.WebPromptingProvider;

public class SeamViewHyperlink extends LinkHyperlink {
	
	protected String updateFilenameForModel(String filename, IProject project) {
		if (filename == null ||
				!filename.trim().startsWith("/"))
			return null;
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
		IFile documentFile = getFile();
		IProject project = documentFile.getProject();
		String updatedFileName = updateFilenameForModel(fileName, project);
		if(updatedFileName!=null) {
			return super.getFileFromProject(updatedFileName);
		} else {
			return null;
		}
	}
	
}