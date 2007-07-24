package org.jboss.tools.seam.internal.core;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jst.ws.internal.common.J2EEUtils;
import org.eclipse.wst.common.componentcore.ComponentCore;
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
		XModelObject webinf = model.getByPath("FileSystems/WEB-INF");
		if(webinf != null) return model;
		
		IPath webInfPath = null;
		
		if(ComponentCore.createComponent(project)!=null) {
			webInfPath = J2EEUtils.getWebInfPath(project);
		}
		
		
		if(webInfPath == null) return model;
		
		IFolder webInfFolder = ResourcesPlugin.getWorkspace().getRoot().getFolder(webInfPath);
		
		model.getProperties().setProperty(XModelConstants.WORKSPACE, webInfFolder.getLocation().toString());
		
		XModelObject fs = model.getByPath("FileSystems");
		webinf = model.createModelObject("FileSystemFolder", null);
		webinf.setAttributeValue("name", "WEB-INF");
		webinf.setAttributeValue("location", "%redhat.workspace%");
		fs.addChild(webinf);
		
		XModelObject webroot = model.createModelObject("FileSystemFolder", null);
		webroot.setAttributeValue("name", "WEB-ROOT");
		webroot.setAttributeValue("location", "%redhat.workspace%/..");
		fs.addChild(webroot);
		
		XModelObject lib = model.createModelObject("FileSystemFolder", null);
		lib.setAttributeValue("name", "lib");
		lib.setAttributeValue("location", "%redhat.workspace%/lib");
		fs.addChild(lib);		
		
		return model;
	}

}
