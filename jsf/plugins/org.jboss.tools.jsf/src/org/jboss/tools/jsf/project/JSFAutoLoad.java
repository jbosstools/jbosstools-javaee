package org.jboss.tools.jsf.project;

import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelConstants;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.FileSystemsHelper;
import org.jboss.tools.common.model.filesystems.impl.FileSystemImpl;
import org.jboss.tools.common.model.project.IAutoLoad;
import org.jboss.tools.common.model.util.EclipseResourceUtil;

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
		
		XModelObject webroot = model.createModelObject("FileSystemFolder", null); //$NON-NLS-1$
		webroot.setAttributeValue("name", "WEB-ROOT"); //$NON-NLS-1$ //$NON-NLS-2$
		webroot.setAttributeValue("location", XModelConstants.WORKSPACE_REF + "/.."); //$NON-NLS-1$ //$NON-NLS-2$
		fs.addChild(webroot);
		
		XModelObject lib = model.createModelObject("FileSystemFolder", null); //$NON-NLS-1$
		lib.setAttributeValue("name", "lib"); //$NON-NLS-1$ //$NON-NLS-2$
		lib.setAttributeValue("location", XModelConstants.WORKSPACE_REF + "/lib"); //$NON-NLS-1$ //$NON-NLS-2$
		fs.addChild(lib);		
		
	}

}
