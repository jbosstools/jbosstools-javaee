package org.jboss.tools.jsf.project.facet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.common.project.facet.core.IDelegate;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.jsf.project.JSFNature;
import org.jboss.tools.jst.web.kb.IKbProject;

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
			EclipseResourceUtil.addNatureToProject(project, JSFNature.NATURE_ID);
			EclipseResourceUtil.addNatureToProject(project, IKbProject.NATURE_ID);
	}
	
}
