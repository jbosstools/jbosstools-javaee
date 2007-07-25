package org.jboss.tools.jsf.project.facet;

import java.io.File;
import java.text.MessageFormat;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.jboss.tools.common.model.util.EclipseResourceUtil;

/**
 * 
 * @author eskimo
 *
 */
public class PostInstallJsfFacetDelegate implements IDelegate  {

	/* (non-Javadoc)
	 * @see org.eclipse.wst.common.project.facet.core.IDelegate#execute(org.eclipse.core.resources.IProject, org.eclipse.wst.common.project.facet.core.IProjectFacetVersion, java.lang.Object, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void execute(IProject project, IProjectFacetVersion fv,
			Object config, IProgressMonitor monitor) throws CoreException {
			writeXModel(project);
			project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
			
			EclipseResourceUtil.addNatureToProject(project, "org.jboss.tools.jsf.jsfnature");
	}
	
	private void writeXModel(IProject project) {
		String projectName = project.getName();
		String webContent = "WebContent";
		
		IVirtualComponent com = ComponentCore.createComponent(project);
		IVirtualFolder webRootFolder = com.getRootFolder().getFolder(new Path("/"));
		IContainer folder = webRootFolder.getUnderlyingFolder();
		
		webContent = folder.getLocation().lastSegment();
		
		if(webContent == null) {
			webContent = "WebContent";
		}
		String src = "src";
		
		String[] srcs = EclipseResourceUtil.getJavaProjectSrcLocations(project);
		if (srcs.length > 0) {
			src = srcs[0].replace('\\','/').substring(srcs[0].lastIndexOf('/') + 1);
		}
		File location = new File(project.getLocation().toFile(),".settings/org.jboss.tools.jst.web.xml");
		
		Object[] arguments = {
			projectName,
			webContent,
			src
		};
		String body = MessageFormat.format(XMODEL, arguments);
		
		org.jboss.tools.common.util.FileUtil.writeFile(location, body);
	}
	
	/**
	 * {0} - project name
	 * {1} - WebContent folder name
	 * {2} - src folder name
	 */
	private static String XMODEL = 
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
		"<FILESYSTEMS APPLICATION_NAME=\"{0}\" ENTITY=\"FileSystems\"" +
		" VERSION=\"2.0.0\" WORKSPACE_HOME=\"./{1}/WEB-INF\">" +
		"<FILESYSTEM ENTITY=\"FileSystemFolder\" LOCATION=\"%redhat.workspace%\" NAME=\"WEB-INF\"/>" +
		"<FILESYSTEM ENTITY=\"FileSystemFolder\" INFO=\"Content-Type=Web\"" +
		" LOCATION=\"%redhat.workspace%/..\" NAME=\"WEB-ROOT\"/>" +
		"<FILESYSTEM ENTITY=\"FileSystemFolder\"" +
		" LOCATION=\"%redhat.workspace%/../../{2}\" NAME=\"src\"/>" +
		"<FILESYSTEM ENTITY=\"FileSystemFolder\" LOCATION=\"%redhat.workspace%/lib\" NAME=\"lib\"/>" +
		"<FILESYSTEM ENTITY=\"FileSystemFolder\"" +
		" LOCATION=\"%redhat.workspace%/classes\" NAME=\"classes\"/>" +
		"<WEB ENTITY=\"JstWeb\" MODEL_PATH=\"/web.xml\" SERVLET_VERSION=\"2.4\">" + 
		"  <MODULE ENTITY=\"WebJSFModule\" MODEL_PATH=\"/faces-config.xml\"" +
		"   ROOT=\"WEB-ROOT\" SRC=\"src\" URI=\"/WEB-INF/faces-config.xml\"/>" +
		"</WEB>" +
		"</FILESYSTEMS>"
	;
}
