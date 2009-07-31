package org.jboss.tools.jsf.model.handlers;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.common.meta.action.SpecialWizard;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultRemoveHandler;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.model.JSFConstants;
import org.jboss.tools.jst.web.kb.IKbProject;
import org.jboss.tools.jst.web.model.helpers.WebAppHelper;

public class RemoveJSFNatureContribution implements SpecialWizard {
	XModel model = null;

	public void setObject(Object object) {
		if(object instanceof XModel) {
			model = (XModel)object;
		}
	}

	public int execute() {
		if(model == null) return 1;
		XModelObject webxml = WebAppHelper.getWebApp(model);
		XModelObject servlet = WebAppHelper.findServlet(webxml,
				JSFConstants.FACES_SERVLET_CLASS, "Faces Config"); //$NON-NLS-1$
		String servletName = servlet == null ? null : servlet.getAttributeValue("servlet-name"); //$NON-NLS-1$
		XModelObject mapping = WebAppHelper.findServletMapping(webxml, servletName);

		if(servlet != null) {
			DefaultRemoveHandler.removeFromParent(servlet);
		}
		if(mapping != null) {
			DefaultRemoveHandler.removeFromParent(mapping);
		}
		XModelObject folder = webxml.getChildByPath("Context Params"); //$NON-NLS-1$
		XModelObject[] params = folder.getChildren();
		for (int i = 0; i < params.length; i++) {
			String name = params[i].getAttributeValue("param-name"); //$NON-NLS-1$
			if(name != null && name.startsWith("javax.faces.")) { //$NON-NLS-1$
				DefaultRemoveHandler.removeFromParent(params[i]);
			}
		}

		IProject project = EclipseResourceUtil.getProject(model.getRoot());
		if(project != null) {
			try {
				EclipseResourceUtil.removeNatureFromProject(project, IKbProject.NATURE_ID);
			} catch (CoreException e) {
				JSFModelPlugin.getPluginLog().logError(e);
			}
		}
		
		return 0;
	}

}
