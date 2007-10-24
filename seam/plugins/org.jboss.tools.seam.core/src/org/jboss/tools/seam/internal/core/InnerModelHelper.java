/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 

package org.jboss.tools.seam.internal.core;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelConstants;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.project.IModelNature;
import org.jboss.tools.common.model.util.EclipseResourceUtil;

public class InnerModelHelper {
	
	public static XModel createXModel(IProject project) {
		IModelNature n = EclipseResourceUtil.getModelNature(project.getProject());
		if(n != null) return n.getModel();
		
		XModel model = EclipseResourceUtil.createObjectForResource(project.getProject()).getModel();
		XModelObject webinf = model.getByPath("FileSystems/WEB-INF"); //$NON-NLS-1$
		if(webinf != null) return model;
		
		IPath webInfPath = null;
		
		if(ComponentCore.createComponent(project)!=null) {
			webInfPath = getWebInfPath(project);
		}		
		
		if(webInfPath == null) return model;
		
		IFolder webInfFolder = ResourcesPlugin.getWorkspace().getRoot().getFolder(webInfPath);
		
		model.getProperties().setProperty(XModelConstants.WORKSPACE, webInfFolder.getLocation().toString());
		model.getProperties().setProperty(XModelConstants.WORKSPACE_OLD, webInfFolder.getLocation().toString());
		
		XModelObject fs = model.getByPath("FileSystems"); //$NON-NLS-1$
		webinf = model.createModelObject("FileSystemFolder", null); //$NON-NLS-1$
		webinf.setAttributeValue("name", "WEB-INF"); //$NON-NLS-1$ //$NON-NLS-2$
		webinf.setAttributeValue("location", XModelConstants.WORKSPACE_REF); //$NON-NLS-1$
		fs.addChild(webinf);
		
		XModelObject webroot = model.createModelObject("FileSystemFolder", null); //$NON-NLS-1$
		webroot.setAttributeValue("name", "WEB-ROOT"); //$NON-NLS-1$ //$NON-NLS-2$
		webroot.setAttributeValue("location", XModelConstants.WORKSPACE_REF + "/.."); //$NON-NLS-1$ //$NON-NLS-2$
		fs.addChild(webroot);
		
		XModelObject lib = model.createModelObject("FileSystemFolder", null); //$NON-NLS-1$
		lib.setAttributeValue("name", "lib"); //$NON-NLS-1$ //$NON-NLS-2$
		lib.setAttributeValue("location", XModelConstants.WORKSPACE_REF + "/lib"); //$NON-NLS-1$ //$NON-NLS-2$
		fs.addChild(lib);		
		
		return model;
	}

	//Taken from J2EEUtils and modified
	public static IPath getWebInfPath(IProject project) {		
		IVirtualComponent component = ComponentCore.createComponent(project);		
		IVirtualFolder webInfDir = component.getRootFolder().getFolder(new Path("/WEB-INF"));
		IPath modulePath = webInfDir.getWorkspaceRelativePath();
		return (!webInfDir.exists()) ? null : modulePath;
	}

}
