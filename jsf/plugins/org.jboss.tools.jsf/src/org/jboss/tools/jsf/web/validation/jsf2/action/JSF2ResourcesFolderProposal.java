/*******************************************************************************
 * Copyright (c) 2007-2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.jsf.web.validation.jsf2.action;

import java.text.MessageFormat;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IMarkerResolution;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.jsf2.util.JSF2ResourceUtil;
import org.jboss.tools.jsf.messages.JSFUIMessages;

/**
 * 
 * @author yzhishko
 * 
 */

public class JSF2ResourcesFolderProposal implements IMarkerResolution {
	private IResource resource;
	private String componentPath = null;
	private String URL=null;

	public JSF2ResourcesFolderProposal(IResource validateResource, String compPath, String URL) {
		this.resource = validateResource;
		this.componentPath = compPath;
		this.URL = URL;
	}

	@Override
	public String getLabel() {
		String folderName="";
		if(componentPath!=null){
			folderName=componentPath.replaceFirst(JSF2ResourceUtil.JSF2_URI_PREFIX, "").trim();
		}
		return MessageFormat.format(JSFUIMessages.Create_JSF_2_Resources_Folder,
				JSF2ResourceUtil.calculateProjectRelativeJSF2ResourceProposal(resource.getProject())+folderName,URL);
	}

	@Override
	public void run(IMarker marker) {
		try{
			JSF2ResourceUtil.createResourcesFolderByNameSpace(resource.getProject(), componentPath);
		}catch(CoreException ex){
			JSFModelPlugin.getPluginLog().logError(ex);
		}
	}

}
