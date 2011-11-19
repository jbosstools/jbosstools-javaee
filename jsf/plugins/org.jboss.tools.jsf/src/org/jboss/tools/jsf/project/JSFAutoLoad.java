package org.jboss.tools.jsf.project;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelConstants;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.XModelObjectConstants;
import org.jboss.tools.common.model.filesystems.FileSystemsHelper;
import org.jboss.tools.common.model.impl.XModelImpl;
import org.jboss.tools.common.model.project.IAutoLoad;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.model.util.XModelObjectUtil;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.jsf.model.pv.JSFProjectsTree;
import org.jboss.tools.jst.web.WebUtils;
import org.jboss.tools.jst.web.model.pv.WebProjectNode;

public class JSFAutoLoad implements IAutoLoad, XModelObjectConstants {
	static String WEB_INF = "WEB-INF"; //$NON-NLS-1$
	static String WEB_ROOT = "WEB-ROOT"; //$NON-NLS-1$

	public void load(XModel model) {
		XModelObject fs = FileSystemsHelper.getFileSystems(model);
		IProject project = EclipseResourceUtil.getProject(fs);
		if(project == null) return;
		
		fs.setAttributeValue("application name", project.getName()); //$NON-NLS-1$
		
		XModelObject s = createFileSystemFolder(model, project.getName(), project.getLocation().toString());
		fs.addChild(s);
		
		XModelObject webinf = createFileSystemFolder(model, WEB_INF, XModelConstants.WORKSPACE_REF);
		fs.addChild(webinf);

		String webInfLocation = XModelObjectUtil.expand(XModelConstants.WORKSPACE_REF, model, null);
		List<String> webRootLocations = getWebRootPaths(project, webInfLocation);

		int i = 0;
		for (String webRootLocation: webRootLocations) {
			String name = WEB_ROOT;
			if(i > 0) name += "-" + i;
			XModelObject webroot = createFileSystemFolder(model, name, webRootLocation);
			fs.addChild(webroot);
			i++;		
		}

		XModelObject lib = createFileSystemFolder(model, "lib", XModelConstants.WORKSPACE_REF + "/lib"); //$NON-NLS-1$ //$NON-NLS-2$
		fs.addChild(lib);		
		
	}
	XModelObject createFileSystemFolder(XModel model, String name, String location) {
		XModelObject f = model.createModelObject(ENT_FILE_SYSTEM_FOLDER, null);
		f.setAttributeValue(ATTR_NAME, name);
		f.setAttributeValue(ATTR_NAME_LOCATION, location);
		return f;
	}
	

	public void update(XModel model) {
		XModelObject fs = FileSystemsHelper.getFileSystems(model);
		IProject project = EclipseResourceUtil.getProject(fs);
		if(project == null) return;

		String webInfLocation = XModelObjectUtil.expand(XModelConstants.WORKSPACE_REF, model, null);
		List<String> webRootLocations = getWebRootPaths(project, webInfLocation);

		boolean modified = false;
		XModelObject webinf = fs.getChildByPath(WEB_INF);
		String rWebInfLocation = getWebInfPath(project, webInfLocation);
		if(rWebInfLocation != null) {
			if(webinf != null && !rWebInfLocation.equals(webinf.getAttributeValue(ATTR_NAME_LOCATION))) {
				webinf.removeFromParent();
				webinf = null;
				modified = true;
			}
			if(webinf == null) {
				webinf = createFileSystemFolder(model, WEB_INF, rWebInfLocation);
				fs.addChild(webinf);
				modified = true;
			}
		} else if(webinf != null) {
			webinf.removeFromParent();
			modified = true;
		}

		List<XModelObject> existingRoots = getExistingWebRoots(fs);
		boolean rootsChanged = rootsChanged(webRootLocations, existingRoots);
		if(rootsChanged) {
			modified = true;
			for (XModelObject c: existingRoots) {
				c.removeFromParent();
			}
			int i = 0;
			for (String webRootLocation: webRootLocations) {
				String name = WEB_ROOT;
				if(i > 0) name += "-" + i;
				XModelObject webroot = createFileSystemFolder(model, name, webRootLocation);
				fs.addChild(webroot);
				i++;		
			}
		}
		
		if(modified) {
			((XModelImpl)fs.getModel()).fireStructureChanged(fs);
			WebProjectNode n = JSFProjectsTree.getProjectsRoot(model);
			if(n != null) {
				n.invalidate();
			}
		}
	}

	boolean rootsChanged(List<String> webRootLocations, List<XModelObject> rs) {
		if(webRootLocations.size() != rs.size()) return true;
		for (int i = 0; i < webRootLocations.size(); i++) {
			String s = webRootLocations.get(i);
			XModelObject o = rs.get(i);
			if(!o.getAttributeValue(ATTR_NAME_LOCATION).equals(s)) {
				return true;
			}
		}
		return false;
	}

	List<XModelObject> getExistingWebRoots(XModelObject fs) {
		List<XModelObject> result = new ArrayList<XModelObject>();
		XModelObject[] cs = fs.getChildren(ENT_FILE_SYSTEM_FOLDER);
		for (XModelObject c: cs) {
			if(c.getAttributeValue(ATTR_NAME).startsWith(WEB_ROOT)) {
				result.add(c);
			}
		}
		return result;
	}

	static List<String> getWebRootPaths(IProject project, String webInfLocation) {
		List<String> result = new ArrayList<String>();
		IContainer[] cs = WebUtils.getWebRootFolders(project);
		for (IContainer c: cs) {
			IPath path = c.getLocation();
			if(path == null) continue;
			String webRootLocation = null;
			try {
				webRootLocation = path.toFile().getCanonicalPath().replace('\\', '/');
				String relative = FileUtil.getRelativePath(webInfLocation, webRootLocation);
				if(relative != null) {
					webRootLocation = XModelConstants.WORKSPACE_REF + relative;
				}
				result.add(webRootLocation);
			} catch (IOException e) {
				continue;
			}
		}
		return result;
	}

	static String getWebInfPath(IProject project, String webInfLocation) {
		IVirtualComponent component = ComponentCore.createComponent(project);	
		if(component != null && component.getRootFolder() != null) {
			IContainer[] cs = WebUtils.getWebRootFolders(project, true);
			for (IContainer c: cs) {
				if(c.exists()) {
					IFolder f = c.getFolder(new Path("/WEB-INF")); //$NON-NLS-1$
					if(f.exists()) {
						try {
							String location = f.getLocation().toFile().getCanonicalPath().replace('\\', '/');
							String relative = FileUtil.getRelativePath(webInfLocation, location);
							return (relative != null) ? XModelConstants.WORKSPACE_REF + relative : location;
						} catch (IOException e) {
							continue;
						}
					}
				}
			}
		}
		return null;
	}

}
