package org.jboss.tools.jsf.project;

import java.io.IOException;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelConstants;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.FileSystemsHelper;
import org.jboss.tools.common.model.filesystems.impl.FileSystemImpl;
import org.jboss.tools.common.model.project.IAutoLoad;
import org.jboss.tools.common.model.project.ProjectHome;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.model.util.XModelObjectUtil;

public class JSFAutoLoad implements IAutoLoad {

	public void load(XModel model) {
		Properties properties = null;
		XModelObject fs = FileSystemsHelper.getFileSystems(model);
		IProject project = EclipseResourceUtil.getProject(fs);
		if(project == null) return;
		
		fs.setAttributeValue("application name", project.getName());
		
		String fsLoc = null;
		FileSystemImpl s = null;
		properties = new Properties();

		fsLoc = project.getLocation().toString();
		properties.setProperty("location", fsLoc);
		properties.setProperty("name", project.getName());
		s = (FileSystemImpl)model.createModelObject("FileSystemFolder", properties);
		fs.addChild(s);
		
		XModelObject webinf = model.createModelObject("FileSystemFolder", null); //$NON-NLS-1$
		webinf.setAttributeValue("name", "WEB-INF"); //$NON-NLS-1$ //$NON-NLS-2$
		webinf.setAttributeValue("location", XModelConstants.WORKSPACE_REF); //$NON-NLS-1$
		fs.addChild(webinf);

		String webInfLocation = XModelObjectUtil.expand(XModelConstants.WORKSPACE_REF, model, null);
		String webRootLocation = getWebRootPath(project, webInfLocation);
		
		XModelObject webroot = model.createModelObject("FileSystemFolder", null); //$NON-NLS-1$
		webroot.setAttributeValue("name", "WEB-ROOT"); //$NON-NLS-1$ //$NON-NLS-2$
		webroot.setAttributeValue("location", webRootLocation); //$NON-NLS-1$ //$NON-NLS-2$
		fs.addChild(webroot);
		
		XModelObject lib = model.createModelObject("FileSystemFolder", null); //$NON-NLS-1$
		lib.setAttributeValue("name", "lib"); //$NON-NLS-1$ //$NON-NLS-2$
		lib.setAttributeValue("location", XModelConstants.WORKSPACE_REF + "/lib"); //$NON-NLS-1$ //$NON-NLS-2$
		fs.addChild(lib);		
		
	}

	static String getWebRootPath(IProject project, String webInfLocation) {
		String webRootLocation = XModelConstants.WORKSPACE_REF + "/..";
		
		IPath wrp = ProjectHome.getFirstWebContentPath(project);
		IPath wip = ProjectHome.getWebInfPath(project);

		if(wrp == null || wip == null) {
			return webRootLocation;
		}
		
		IResource wrpc = ResourcesPlugin.getWorkspace().getRoot().findMember(wrp);
		IResource wipc = ResourcesPlugin.getWorkspace().getRoot().findMember(wip);
		if(wrpc != null && wipc != null && wipc.isLinked()) {
			IPath p = wrpc.getLocation();
			if(p != null) {
				try {
					webRootLocation = p.toFile().getCanonicalPath().replace('\\', '/');
				} catch (IOException e) {
				}
				String relative = org.jboss.tools.common.util.FileUtil.getRelativePath(webInfLocation, webRootLocation);
				if(relative != null) {
					webRootLocation = XModelConstants.WORKSPACE_REF + relative;
				}
			}
		}
		return webRootLocation;
	}

}
